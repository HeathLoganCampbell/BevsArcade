package com.bevelio.build.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.Compress;
import com.bevelio.arcade.misc.XYZ;
import com.bevelio.arcade.module.commandframework.Command;
import com.bevelio.arcade.module.commandframework.CommandArgs;
import com.bevelio.arcade.types.Team;
import com.bevelio.arcade.types.WorldData;
import com.bevelio.arcade.utils.MathUtils;
import com.bevelio.arcade.utils.Pair;
import com.bevelio.arcade.utils.ZipUtils;
import com.bevelio.build.listener.CustomSelectorListener;

public class BuildCommands 
{
	private TranslationConfig tc = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
	private HashMap<String, WorldData> worldDatas = new HashMap<>();
	private final static String[] BLACKLIST_FILES = {"data", "playerdata", "session.lock", "uid.dat"};
	
	
	public boolean isValidplayer(Player player)
	{
		if(ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player))
		{
			player.sendMessage(CC.gray + "You are currently in a game!");
			return false;
		}
		
		return true;
	}
	
	public String getFolder(String worldName)
	{
		return ArcadePlugin.getInstance().getDataFolder().getPath() + File.separator + "builds" + File.separator + worldName;
	}
	
	@Command(name="build")
    public void onBuild(CommandArgs args) 
	{
		args.getSender().sendMessage(tc.getCommandBuildBase());
	}
	
	@Command(name="build.help")
    public void onHelp(CommandArgs args) 
	{
		CommandSender sender = args.getSender();
		int page = 0;
		if(args.length() == 1)
		{
			String input = args.getArgs(0);
			if(!MathUtils.isNumeric(input))
			{
				sender.sendMessage(tc.getCommandOnlyNumber());
				return;
			}
			page = Integer.getInteger(input);
			page = Math.abs(page);
		}
		String[] helps = tc.getCommandBuildHelp();
		int maxItems = 5;
		int offset = page * maxItems;
		int hardMaxItems = offset + maxItems;
		if(helps.length < hardMaxItems)
			hardMaxItems = helps.length - 1;
		sender.sendMessage(tc.getCommandBuildHelpHeading());
		for(int i = offset; i < hardMaxItems; i++)
			sender.sendMessage(helps[i]);
		sender.sendMessage(tc.getCommandBuildHelpFooting());
	}
	
	
	@Command(name="build.createGame", inGameOnly=true)
    public void onCreate(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(!this.isValidplayer(player)) return;
		if(args.length() < 2)
		{
			player.sendMessage(CC.gray + "Usage: /Build CreateGame <WorldName> <GameType>");
			return;
		}
		
		String worldName = args.getArgs(0);
		String gameType = args.getArgs(1);
		String author = player.getName();
		boolean force = false;
		if(args.length() == 3)
			if(args.getArgs(2).equalsIgnoreCase("true"))
				force = true;
		
		String worldPath = this.getFolder(worldName);
		File worldFold = new File(worldPath);
		if(worldFold.exists() && !force)
		{
			player.sendMessage(CC.gray + "This world already exists");
			return;
		}
		player.sendMessage("Creating world...");
		
		WorldCreator worldCreator = new WorldCreator(worldPath);
		worldCreator.generator(new ChunkGenerator() 
									{
										@Override
										public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
										{
											return new byte[world.getMaxHeight() >> 4][];
										}
									});
		
		World world = worldCreator.createWorld();
		world.setDifficulty(Difficulty.PEACEFUL);
		
		world.setSpawnLocation(0, 35, 0);
		Location loc = world.getSpawnLocation();
		
		WorldData worldData = new WorldData(worldPath);
		worldData.name = worldName;
		worldData.gameType = gameType;
		worldData.authors = Arrays.asList(author);
		worldData.maxPlayers = 10;
		worldData.maxSeconds = 120;
		worldData.spectatorSpawn = new XYZ(loc.getX(), loc.getY(), loc.getZ());
		worldData.defaultKit = "none";
		worldData.kits = Arrays.asList("none");
		worldData.saveSettings();
		worldData.save();
		
		worldDatas.put(worldPath, worldData);
		
		world.getBlockAt(0, 32, 0).setType(Material.GLASS);
		player.teleport(world.getSpawnLocation());
		player.sendMessage("You are now in the build world.");
	}
	
	@Command(name="build.load", inGameOnly=true)
    public void onLoad(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(!this.isValidplayer(player)) return;
		if(args.length() != 1)
		{
			player.sendMessage(CC.gray + "Usage: /Build Load <WorldName>");
			return;
		}
		
		String worldName = args.getArgs(0);
		
		String worldPath = this.getFolder(worldName);
		File worldFold = new File(worldPath);
		if(!worldFold.exists())
		{
			player.sendMessage(CC.gray + "This world doesn't exists");
			return;
		}
		player.sendMessage("Loading world...");
		
		WorldCreator worldCreator = new WorldCreator(worldPath);
		worldCreator.generator(new ChunkGenerator() 
									{
										@Override
										public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
										{
											return new byte[world.getMaxHeight() >> 4][];
										}
									});
		
		World world = worldCreator.createWorld();
		world.setDifficulty(Difficulty.PEACEFUL);
		
		WorldData worldData = new WorldData(worldPath);
		
		try {
			worldData.load();
			worldData.loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("First: "+ worldName);
		worldDatas.put(worldPath, worldData);
		
		player.teleport(world.getSpawnLocation());
		player.sendMessage("You are now in the build world.");
		player.setGameMode(GameMode.CREATIVE);
	}
	
	@Command(name="build.createTeam", inGameOnly=true)
    public void onCreateTeam(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(!this.isValidplayer(player)) return;
		if(args.length() != 1)
		{
			player.sendMessage(CC.gray + "Usage: /Build createTeam <TeamName>");
			return;
		}
		
		String teamName = args.getArgs(0).toLowerCase();
		String worldName = player.getWorld().getName();
		
		System.out.println(worldName);
		this.worldDatas.forEach((k,v) -> System.out.println(k + " <"));
		
		if(!worldDatas.containsKey(worldName))
		{
			player.sendMessage("World not in build mode");
			return;
		}
		
		WorldData worldData = worldDatas.get(worldName);
		ChatColor color = Team.getChatColor(worldData.teams.size());
		Team team = new Team(teamName, color);
		worldData.teams.put(team.getName(), team);
		worldData.saveTeam(team);
		player.sendMessage("Team created");
		worldData.save();
	}
	
	@Command(name="build.publish", inGameOnly=true)
    public void onUpload(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(!this.isValidplayer(player)) return;
		World world = player.getWorld();
		String worldName = world.getName();
		if(!worldDatas.containsKey(worldName))
		{
			player.sendMessage("World not in build mode");
			return;
		}
		File worldFile = new File(worldName);
		String cleanWorldName = worldFile.getName();
		player.sendMessage("Publishing " + cleanWorldName);
		
		String zipString = ArcadePlugin.getInstance().getDataFolder() + File.separator + "maps" + File.separator + cleanWorldName + ".zip";
		
		
		Location kickToLoc = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation();
		world.getPlayers().forEach(viewer -> 
		{
			viewer.sendMessage("World being published");
			viewer.teleport(kickToLoc);
			viewer.setGameMode(GameMode.SURVIVAL);
		});
		
		Bukkit.unloadWorld(world, true);
		
		
		for(File file : worldFile.listFiles())
			for(String blackListFile : BLACKLIST_FILES)
			{
				if(file.getName().equalsIgnoreCase(blackListFile))
				{
					try 
					{
						if(file.isFile())
							file.delete();
						else
							FileUtils.deleteDirectory(file);
					} 
						catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		
		try {
			ZipUtils.pack(new File(worldName).toPath(), new File(zipString).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		player.sendMessage("Published");
	}
	
	@Command(name="build.addSpawn", inGameOnly=true)
    public void onSpawnTeam(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(!this.isValidplayer(player)) return;
		if(args.length() != 1)
		{
			player.sendMessage(CC.gray + "Usage: /Build addSpawn <TeamName>");
			return;
		}
		
		String teamName = args.getArgs(0);
		String worldName = player.getWorld().getName();
		
		if(!worldDatas.containsKey(worldName))
		{
			player.sendMessage("World not in build mode");
			return;
		}
		
		WorldData worldData = worldDatas.get(worldName);
		Team team = worldData.teams.get(teamName);
		if(team == null)
		{
			player.sendMessage("Team not found");
			return;
		}
		XYZ xyz = new XYZ(player.getLocation());
		team.addSpawn(xyz);
		worldData.saveTeam(team);
		player.sendMessage(team.getColor() + "Team Location Saved");
		worldData.save();
	}
	
	@Command(name="build.addCustom", inGameOnly=true)
    public void onCustom(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(!this.isValidplayer(player)) return;
		if(args.length() != 1)
		{
			player.sendMessage(CC.gray + "Usage: /Build addCustom <CustomName>");
			return;
		}
		
		String customName = args.getArgs(0);
		String worldName = player.getWorld().getName();
		
		if(!worldDatas.containsKey(worldName))
		{
			player.sendMessage("World not in build mode");
			return;
		}
		
		WorldData worldData = worldDatas.get(worldName);
		List<XYZ> customLocs = new ArrayList<>();
		if(worldData.customs.containsKey(customName))
			customLocs = worldData.customs.get(customLocs);
		
		
		XYZ xyz = new XYZ(player.getLocation());
		customLocs.add(xyz);
		worldData.saveCustom(customName, customLocs);;
		player.sendMessage("Custom Location Saved");
		worldData.save();
	}
	
	@Command(name="build.wand", inGameOnly=true)
    public void onWand(CommandArgs args) 
	{
		args.getPlayer().getInventory().addItem(CustomSelectorListener.getWand());
	}
	
	@Command(name="build.setCustom", aliases={"build.setCustoms"}, inGameOnly=true)
    public void onSetCustom(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(!this.isValidplayer(player)) return;
		if(args.length() != 1)
		{
			player.sendMessage(CC.gray + "Usage: /Build setCustom <CustomName>");
			return;
		}
		
		String customName = args.getArgs(0);
		String worldName = player.getWorld().getName();
		
		if(!worldDatas.containsKey(worldName))
		{
			player.sendMessage("World not in build mode");
			return;
		}
		
		WorldData worldData = worldDatas.get(worldName);
		List<XYZ> customLocs = new ArrayList<>();
		if(worldData.customs.containsKey(customName))
			customLocs = worldData.customs.get(customName);
		
		List<Block> blocks = CustomSelectorListener.getBlocks(player);
		if(blocks == null) 
		{
			player.sendMessage("Locations not set");
			return;
		}
		for(Block block : blocks)
		{
			XYZ xyz = new XYZ(block.getLocation());
			customLocs.add(xyz);
		}
		worldData.saveCustom(customName, customLocs);;
		player.sendMessage("Custom Location Saved");
		worldData.save();
	}
}
