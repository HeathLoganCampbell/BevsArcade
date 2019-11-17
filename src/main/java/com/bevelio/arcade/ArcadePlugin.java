package com.bevelio.arcade;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.arcade.commands.BasicCommands;
import com.bevelio.arcade.commands.BuildCommands;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.listeners.ColorTeamArmorListener;
import com.bevelio.arcade.listeners.CompassListener;
import com.bevelio.arcade.listeners.GameFlagListener;
import com.bevelio.arcade.listeners.GameStateListener;
import com.bevelio.arcade.listeners.JoinSignListener;
import com.bevelio.arcade.listeners.LobbyListener;
import com.bevelio.arcade.listeners.PingListener;
import com.bevelio.arcade.listeners.PreGameListener;
import com.bevelio.arcade.listeners.RejoinListener;
import com.bevelio.arcade.listeners.SpectatorListener;
import com.bevelio.arcade.managers.AbilityManager;
import com.bevelio.arcade.managers.BuildManager;
import com.bevelio.arcade.managers.ConfigManager;
import com.bevelio.arcade.managers.DamageManager;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.managers.RewardManager;
import com.bevelio.arcade.managers.WorldCreatorManager;
import com.bevelio.arcade.managers.WorldManager;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.module.commandframework.CommandFramework;
import com.bevelio.arcade.module.component.ComponentManager;
import com.bevelio.arcade.module.display.DisplayCore;
import com.bevelio.arcade.module.lag.LagMeter;
import com.bevelio.arcade.module.updater.Updater;
import com.bevelio.arcade.utils.MathUtils;
import com.bevelio.arcade.utils.ServerUtils;
import com.bevelio.hooks.HookManager;

import lombok.Getter;


public class ArcadePlugin extends JavaPlugin
{
	private @Getter GameManager gameManager;
	private @Getter WorldCreatorManager worldCreatorManager;
	private @Getter WorldManager worldManager;
	private @Getter ConfigManager configManager;
	private @Getter CommandFramework commandFramework;
	private @Getter DamageManager damagerManager;
	private @Getter DisplayCore displayCore;
	private @Getter BuildManager builderManager;
	private @Getter AbilityManager abilityManager;
	private @Getter HookManager hookManager;
	private @Getter RewardManager rewardManager;
	private @Getter ComponentManager componentManager;
	private @Getter static final boolean DEBUG_MODE = true;
	private boolean shareware = false;
	private final String user = "%%__USER__%%";
	
	@Override
	public void onEnable()
	{
		
		new Updater(this);
		
		downloadDefaults();
		
		this.configManager = new ConfigManager();
		this.commandFramework = new CommandFramework(this);
		this.displayCore = new DisplayCore(this);
		this.componentManager = new ComponentManager(this);
		this.gameManager = new GameManager();
		this.abilityManager = new AbilityManager();
		this.worldCreatorManager = new WorldCreatorManager();
		this.worldManager = new WorldManager();
		this.damagerManager = new DamageManager();
		this.builderManager = new BuildManager();
		this.hookManager = new HookManager();
		this.rewardManager = new RewardManager();
		new LagMeter(this);
		
		registerListeners();
		registerCommands();
		
		boolean approved = true;
		
		if(!this.getDescription().getAuthors().contains("Bevelio"))
			approved = false;
		if(!this.getDescription().getName().equalsIgnoreCase("BevsArcade"))
			approved = false;
		
		if(DEBUG_MODE)
		{
			ServerUtils.log(CC.red + "-------------------------------------");
			ServerUtils.log(CC.red + "              BevsArcade");
			ServerUtils.log(CC.red + "  You are currently running a debug");
			ServerUtils.log(CC.red + "  Join the discord @ https://discord.gg/6U8DVwH");
			ServerUtils.log(CC.red + "  Checkout the Plugin @ https://www.spigotmc.org/resources/bevsarcade.36196/");
			if(this.shareware)
				if(1491309504078l < System.currentTimeMillis())
				{
					ServerUtils.log(CC.red + "-------------------------------------");
					ServerUtils.log(CC.red + "              BevsArcade");
					ServerUtils.log(CC.red + "  You can no longer use this plugin since");
					ServerUtils.log(CC.red + "  your trial is up! ");
					ServerUtils.log(CC.red + "  Join the discord @ https://discord.gg/6U8DVwH");
					ServerUtils.log(CC.red + "  Checkout the Plugin @ https://www.spigotmc.org/resources/bevsarcade.36196/");
					Bukkit.getPluginManager().disablePlugin(this);
				}
			return;
		} else if(!MathUtils.isNumeric(user) && !DEBUG_MODE)
			approved = false;
		
		
		if(!approved)
		{
			this.configManager = null;
			this.displayCore = null;
			ServerUtils.log(CC.red + "-------------------------------------");
			ServerUtils.log(CC.red + "              BevsArcade");
			ServerUtils.log(CC.red + "  Error! Sorry about this, the code");
			ServerUtils.log(CC.red + "  is still a bit bug, and you found");
			ServerUtils.log(CC.red + "  one of those bugs, to fix this");
			ServerUtils.log(CC.red + "  hit me up on discord @ https://discord.gg/6U8DVwH");
		}
	}
	
	public void downloadDefaults()
	{
		File maps = new File(this.getDataFolder(), "maps");
		if(!maps.exists())
		{
			if(!this.getDataFolder().exists())
				this.getDataFolder().mkdir();
			if(!new File(this.getDataFolder(), "activeGames").exists())
				new File(this.getDataFolder(), "activeGames").mkdir();
			maps.mkdir();
			try
			{
				for(String fileLoc : new String[]{"https://dl.dropboxusercontent.com/s/vlhjrg8g7h727i2/CandyLand.zip?dl=0",
												  "https://dl.dropboxusercontent.com/s/9himwxh0sqv2u9o/Dupeplex.zip?dl=0",
												  "https://dl.dropboxusercontent.com/s/g7e28hx7uqupmi0/MicroSpleef.zip?dl=0",
												  "https://dl.dropboxusercontent.com/s/n2gzdhezd8tmew4/MicroWalls.zip?dl=0",
												  "https://dl.dropboxusercontent.com/s/5dn3ushi3w4a32q/RunBoiRun.zip?dl=0",
												  "https://dl.dropboxusercontent.com/s/4owsfzukugavurz/Tetris.zip?dl=0"})
				{
					URI uri = new URI(fileLoc);
					String path = uri.getPath();
					String worldNameZip = path.substring(path.lastIndexOf('/') + 1);
					worldNameZip = worldNameZip.replace("?dl=0", "");
					ServerUtils.log("Downloading " + worldNameZip + " for BevsArcade's because we saw you had no maps");
					
				    URL download = uri.toURL();
				    File filename = new File(maps, worldNameZip);
				    ReadableByteChannel rbc=Channels.newChannel(download.openStream());
				    FileOutputStream fileOut = new FileOutputStream(filename);
				    fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
				    fileOut.flush();
				    fileOut.close();
				    rbc.close();
				}
			}
			catch(Exception e)
			{ 
//				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		for(World world : Bukkit.getWorlds())
		{
			String worldName = world.getName();
			if(worldName.contains((ArcadePlugin.getInstance().getDataFolder().getPath() + File.separator + "activeGames" + File.separator)))
				this.worldCreatorManager.deleteWorld(worldName);
		}
	}
	
	public void registerListeners()
	{
		Bukkit.getPluginManager().registerEvents(this.getGameManager(), this);
		Bukkit.getPluginManager().registerEvents(this.getWorldManager(), this);
		Bukkit.getPluginManager().registerEvents(this.getDamagerManager(), this);
		Bukkit.getPluginManager().registerEvents(new GameStateListener(), this);
		Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
		Bukkit.getPluginManager().registerEvents(new GameFlagListener(), this);
		Bukkit.getPluginManager().registerEvents(new RejoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new CompassListener(), this);
		Bukkit.getPluginManager().registerEvents(new LobbyListener(), this);
		Bukkit.getPluginManager().registerEvents(new ColorTeamArmorListener(), this);
		Bukkit.getPluginManager().registerEvents(new PreGameListener(), this);
		Bukkit.getPluginManager().registerEvents(new JoinSignListener(), this);
		Bukkit.getPluginManager().registerEvents(new PingListener(), this);
	}
	
	public void registerCommands()
	{
		this.getCommandFramework().registerCommands(new DebugCommands());
		this.getCommandFramework().registerCommands(new BuildCommands());
		this.getCommandFramework().registerCommands(new BasicCommands());
	}
	
	public static ArcadePlugin getInstance()
	{
		return JavaPlugin.getPlugin(ArcadePlugin.class);
	}
}
