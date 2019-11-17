package com.bevelio.arcade.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.PlayState;

import lombok.Getter;
import lombok.Setter;

public class PlayerPlayStateEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private @Getter @Setter Player player;
	private @Getter @Setter PlayState to;
	private @Getter @Setter PlayState from;
	private boolean isCancelled;
	
	public PlayerPlayStateEvent(Player player, PlayState to, PlayState from)
	{
		this.player = player;
		this.to = to;
		this.from = from;
		
		this.isCancelled = false;
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
