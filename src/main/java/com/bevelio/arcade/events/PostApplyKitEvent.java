package com.bevelio.arcade.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bevelio.arcade.types.Kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class PostApplyKitEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private @Getter @Setter Player player;
	private @Getter @Setter Kit kit;
	private boolean isCancelled;

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
