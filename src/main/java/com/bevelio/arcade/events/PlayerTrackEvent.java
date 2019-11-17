package com.bevelio.arcade.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

public class PlayerTrackEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled;
	
	private @Getter @Setter Player player;
	private @Getter @Setter Location targetLocation;
	private @Getter @Setter String targetName;
	private @Getter @Setter Player targetPlayer;
	
	public PlayerTrackEvent(Player player, Location targetLocation, String targetName, Player targetPlayer) {
		this.isCancelled = false;
		this.player = player;
		this.targetLocation = targetLocation;
		this.targetName = targetName;
		this.targetPlayer = targetPlayer;
	}
	
	public PlayerTrackEvent(Player player, Player targetPlayer) {
		this.isCancelled = false;
		this.player = player;
		this.targetLocation = targetPlayer.getLocation();
		this.targetName = targetPlayer.getName();
		this.targetPlayer = targetPlayer;
	}

	@Override
	public boolean isCancelled() 
	{
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) 
	{
		this.isCancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() 
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
        return handlers;
    }
}
