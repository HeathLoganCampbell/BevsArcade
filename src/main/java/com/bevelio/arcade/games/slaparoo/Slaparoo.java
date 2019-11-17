package com.bevelio.arcade.games.slaparoo;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.types.PlayState;

public class Slaparoo extends SoloGame
{
	public Slaparoo() 
	{
		super("Slaparoo", new String[] {"You have increased knockback", "Knock others off", "Last man standing wins"}, new ItemStackBuilder(Material.COOKIE));
		this.deathOut = true;
		this.quitOut = true;
		
		this.breakBlocks = false;
		this.placeBlocks = false;
		this.dropItems = false;
	}
	
	@EventHandler
	public void onDeath(PlayerPlayStateEvent e)
	{
		Player player = e.getPlayer();
		if(e.getFrom() != PlayState.IN) return;
		if(e.getTo() != PlayState.OUT) return;
		if(!this.isInQueue(player)) return;
		if(!this.isLive()) return;
		if(player.getLocation().getY() < 2)
			player.teleport(this.getWorldData().spectatorSpawn.toLocation(player.getWorld()));
		this.getWinners().add(0, player);
		this.checkEnd();
	}
}
