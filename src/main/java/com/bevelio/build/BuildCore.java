package com.bevelio.build;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.build.commands.BuildCommands;
import com.bevelio.build.listener.CustomSelectorListener;

public class BuildCore 
{
	private HashSet<UUID> buildMode = new HashSet<>();
	
	public BuildCore()
	{
		ArcadePlugin.getInstance().getCommandFramework().registerCommands(new BuildCommands());
		Bukkit.getPluginManager().registerEvents(new CustomSelectorListener(), ArcadePlugin.getInstance());
	}
	
	public void setBuildMode(Player player, boolean buildMode)
	{
		UUID uuid = player.getUniqueId();
		if(buildMode)
		{
			this.buildMode.add(uuid);
		} else {
			this.buildMode.remove(uuid);
		}
	}
}

// Team
//		Team One
//			Name
//			Prefix
//			Spawns
//		Team Two
//		Team Three
//		Team Four
// Customs
//		Custom One
//			Location One
//			Location Two
//			Location Three
// Data
// World Options
//		Name
//		Authors
//		GameType
//		MaxPlayers
//		MaxSeconds
//		SpectatorSpawn
//		DefaultKit
//		Kits
