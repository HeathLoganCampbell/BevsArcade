package com.bevelio.arcade.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.events.PlayJoinTeamEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.misc.XYZ;
import com.bevelio.arcade.scoreboard.ArcadeScoreboard;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

public class Team
{
	private String name
				 , defaultKit;
	private @Getter @Setter String displayName;
	private ChatColor prefix;
	private String[] blockedKits;
	private ItemStackBuilder icon;
	private List<XYZ> spawns;
	private List<UUID> members;
	private @Getter @Setter ArcadeScoreboard scoreboard;
	
	private org.bukkit.scoreboard.Team bukkitTeam;
	private static Map<ChatColor, DyeColor> dyeChatMap;
	static
	{
		dyeChatMap = Maps.newHashMap();
		dyeChatMap.put(ChatColor.DARK_GRAY, DyeColor.BLACK);
		dyeChatMap.put(ChatColor.DARK_BLUE, DyeColor.BLUE);
		dyeChatMap.put(ChatColor.GOLD, DyeColor.BROWN);
		dyeChatMap.put(ChatColor.AQUA, DyeColor.CYAN);
		dyeChatMap.put(ChatColor.GRAY, DyeColor.GRAY);
		dyeChatMap.put(ChatColor.DARK_GREEN, DyeColor.GREEN);
		dyeChatMap.put(ChatColor.BLUE, DyeColor.LIGHT_BLUE);
		dyeChatMap.put(ChatColor.GREEN, DyeColor.LIME);
		dyeChatMap.put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA);
		dyeChatMap.put(ChatColor.GOLD, DyeColor.ORANGE);
		dyeChatMap.put(ChatColor.LIGHT_PURPLE, DyeColor.PINK);
		dyeChatMap.put(ChatColor.DARK_PURPLE, DyeColor.PURPLE);
		dyeChatMap.put(ChatColor.DARK_RED, DyeColor.RED);
		dyeChatMap.put(ChatColor.GRAY, DyeColor.SILVER);
		dyeChatMap.put(ChatColor.WHITE, DyeColor.WHITE);
		dyeChatMap.put(ChatColor.YELLOW, DyeColor.YELLOW);
		dyeChatMap.put(ChatColor.RED, DyeColor.RED);
	}
	
	public Team(String name, ChatColor prefix)
	{
		this.name = name;
		this.displayName = name;
		this.prefix = prefix;
		this.spawns = new ArrayList<XYZ>();
		this.members = new ArrayList<UUID>();
		this.defaultKit = null;
		this.scoreboard = new ArcadeScoreboard(name);
		this.scoreboard.setUp();
		this.bukkitTeam = this.scoreboard.getScoreboard().registerNewTeam(name);
		this.bukkitTeam.setPrefix(getPrefix() + "");
		this.setIcon(new ItemStackBuilder(Material.WOOL).leatherColour(this.getColor()));
	}
	
	public Team(String name, ChatColor prefix, boolean isBuildMode)
	{
		this.name = name;
		this.displayName = name;
		this.prefix = prefix;
		this.spawns = new ArrayList<XYZ>();
		this.members = new ArrayList<UUID>();
		this.defaultKit = null;
		if(!isBuildMode)
		{
			this.scoreboard = new ArcadeScoreboard(name);
			this.scoreboard.setUp();
			this.bukkitTeam = this.scoreboard.getScoreboard().registerNewTeam(name);
			this.bukkitTeam.setPrefix(getPrefix() + "");
		}
		this.setIcon(new ItemStackBuilder(Material.WOOL).leatherColour(this.getColor()));
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ChatColor getPrefix()
	{
		return prefix;
	}

	public void setPrefix(ChatColor prefix)
	{
		this.prefix = prefix;
	}
	
	public void addMember(UUID uuid)
	{
		this.members.add(uuid);
		Player player = Bukkit.getPlayer(uuid);
		
		PlayJoinTeamEvent event = new PlayJoinTeamEvent(player, this);
		Bukkit.getPluginManager().callEvent(event);
		
		if(this.getBukkitTeam() != null && player != null)
		{
			this.getBukkitTeam().addEntry(player.getName());
			Game game = ArcadePlugin.getInstance().getGameManager().getGame();
			if(game != null)
				for(Entry<String, Team> teamSet : game.getTeams().entrySet())
				{
					ArcadeScoreboard otherTeamSb = teamSet.getValue().getScoreboard();
					org.bukkit.scoreboard.Team otherTeam = otherTeamSb.getScoreboard().getTeam(this.getName());
					if(otherTeam != null)
						otherTeam.addEntry(player.getName());
				}
		}
		
	}
	
	public boolean isMember(UUID uuid)
	{
		return this.members.contains(uuid);
	}
	
	public void removeMember(UUID uuid)
	{
		this.members.remove(uuid);
		Player player = Bukkit.getPlayer(uuid);
		if(this.getBukkitTeam() != null && player != null)
		{
			this.getBukkitTeam().removeEntry(player.getName());
			ArcadePlugin.getInstance().getGameManager().getDefaultScoreboard().send(player);
			
			Game game = ArcadePlugin.getInstance().getGameManager().getGame();
			if(game != null)
				for(Entry<String, Team> teamSet : game.getTeams().entrySet())
				{
					ArcadeScoreboard otherTeamSb = teamSet.getValue().getScoreboard();
					org.bukkit.scoreboard.Team otherTeam = otherTeamSb.getScoreboard().getTeam(this.getName());
					if(otherTeam != null)
					{
						otherTeam.removeEntry(player.getName());
					}
				}
		}
	}
	
	public int size()
	{
		return this.members.size();
	}

	public List<XYZ> getSpawnNames()
	{
		return spawns;
	}

	public void addSpawn(XYZ spawnName)
	{
		this.spawns.add(spawnName);
	}
	
	public void addAllSpawns(List<XYZ> spawns)
	{
		this.spawns.addAll(spawns);
	}

	public String getDisplayName()
	{
		return getDisplayName(true);
	}
	
	public String getDisplayName(boolean withColor)
	{
		return (withColor ? this.getPrefix() + "" : "") + this.displayName;
	}

	public String getDefaultKit()
	{
		return defaultKit;
	}

	public void setDefaultKit(String defaultKit)
	{
		this.defaultKit = defaultKit;
	}

	public org.bukkit.scoreboard.Team getBukkitTeam() {
		return bukkitTeam;
	}

	public void setBukkitTeam(org.bukkit.scoreboard.Team bukkitTeam) {
		this.bukkitTeam = bukkitTeam;
	}

	public String[] getBlockedKits() {
		return blockedKits;
	}

	public void setBlockedKits(String[] blockedKits) {
		this.blockedKits = blockedKits;
	}

	public ItemStackBuilder getIcon() {
		return icon;
	}

	public void setIcon(ItemStackBuilder icon) {
		this.icon = icon;
	}

	public List<XYZ> getSpawns() {
		return spawns;
	}

	public List<UUID> getMembers() {
		return members;
	}

	private int i = 0;
	public Location getNewSpawnLocation(World world)
	{
//		int index = MathUtils.random(this.getSpawns().size() - 1);
		System.out.println(this.getSpawns().get(i % this.getSpawns().size()).toLocation(world).toString());
		return this.getSpawns().get(++i % this.getSpawns().size()).toLocation(world);
	}
	
	public Color getColor()
	{
		switch(this.getPrefix())
		{
		case AQUA:
			return Color.AQUA;
		case BLACK:
			return Color.BLACK;
		case BLUE:
			return Color.BLUE;
		case DARK_AQUA:
			return Color.BLUE;
		case DARK_BLUE:
			return Color.PURPLE;
		case DARK_GRAY:
			return Color.GRAY;
		case DARK_GREEN:
			return Color.GREEN;
		case DARK_PURPLE:
			return Color.PURPLE;
		case DARK_RED:
			return Color.RED;
		case GOLD:
			return Color.YELLOW;
		case GRAY:
			return Color.GRAY;
		case GREEN:
			return Color.GREEN;
		case LIGHT_PURPLE:
			return Color.PURPLE;
		case RED:
			return Color.RED;
		case WHITE:
			return Color.WHITE;
		case YELLOW:
			return Color.YELLOW;
		default:
			break;
		}
		return Color.RED;
	}
	
	public DyeColor getDyeColor()
	{
		if(dyeChatMap.containsKey(this.getPrefix()))
			return dyeChatMap.get(this.getPrefix());
		return DyeColor.WHITE;
	}
	
	public static ChatColor getChatColor(int id)
	{
		switch(id)
		{
			case 0:
				return ChatColor.RED;
			case 1:
				return ChatColor.AQUA;
			case 2:
				return ChatColor.GREEN;
			case 3:
				return ChatColor.GOLD;
		}
		return ChatColor.WHITE;
	}
}
