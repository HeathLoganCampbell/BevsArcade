package com.bevelio.arcade.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bevelio.arcade.types.GameState;

import lombok.Getter;
import lombok.Setter;

public class GameStateChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private @Getter @Setter GameState to;
	private @Getter @Setter GameState from;
	private @Getter @Setter int seconds;
	private boolean isCancelled;
	
	public GameStateChangeEvent(GameState to, GameState from)
	{
		this.to = to;
		this.from = from;
		
		this.seconds = this.to.getSeconds();
		
		this.isCancelled = false;
	}
	
	public void setToAndSeconds(GameState gameState, int seconds)
	{
		this.setTo(gameState);
		this.setSeconds(seconds);
	}
	
	public void setTo(GameState gameState, boolean updateSeconds)
	{
		this.setTo(gameState);
		if(updateSeconds)
			this.setSeconds(gameState.getSeconds());
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
