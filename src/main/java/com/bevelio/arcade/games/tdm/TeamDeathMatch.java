package com.bevelio.arcade.games.tdm;

import org.bukkit.Material;

import com.bevelio.arcade.games.TeamGame;
import com.bevelio.arcade.misc.ItemStackBuilder;

public class TeamDeathMatch extends TeamGame
{
	public TeamDeathMatch() 
	{
		super("TDM", new String[] {"Kill the other team", "Last team standing wins"}, new ItemStackBuilder(Material.IRON_SWORD));
		
		this.deathOut = true;
		
		this.dropItems = true;
	    this.pickUpItems = true;

	    this.breakBlocks = true;
	    this.placeBlocks = true;
	}
}
