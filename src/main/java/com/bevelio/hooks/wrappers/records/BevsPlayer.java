package com.bevelio.hooks.wrappers.records;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class BevsPlayer 
{
	private @Getter String name;
	private @Getter UUID uuid;
	
	private @Getter @Setter int kills = 0;
	private @Getter @Setter int deaths = 0;
	private @Getter @Setter int coins = 0;
	private @Getter HashMap<String, Integer> wins = new HashMap<>();//GameType, kill count
	private @Getter List<String> kits = new ArrayList<>();

	public BevsPlayer(String name, UUID uuid) 
	{
		this.name = name;
		this.uuid = uuid;
	}
	
	public BevsPlayer(Player player)
	{
		this(player.getName(), player.getUniqueId());
	}
	
	public boolean hasKit(String name)
	{
		return this.kits.contains(name);
	}
}
//Save every 5 minutes

//Players - table (Done)
//	PLAYER_ID, Name, UUID, Kills, Wins, Coins
//Wins - table (Done)
//	WIN_ID, PLAYER_ID, place, game_id
//Kills - table (Done)
//	Kill_id, player_id, killer_id, weapon, game_id
//Games - table (Done)
//	Game_id, game_name
//Kits - table 
//	kit_id, player_id, KitName