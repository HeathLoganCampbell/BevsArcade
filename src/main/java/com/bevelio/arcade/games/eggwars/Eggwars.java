package com.bevelio.arcade.games.eggwars;

import org.bukkit.Material;

import com.bevelio.arcade.games.TeamGame;
import com.bevelio.arcade.misc.ItemStackBuilder;

public class Eggwars extends TeamGame
{

	public Eggwars()
	{
		super("Eggwars", new String[] {"If your egg is destroyed", "You can no longer respawn"}, new ItemStackBuilder(Material.DRAGON_EGG));
		
		
	}
	
	
}
