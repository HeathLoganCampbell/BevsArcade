package com.bevelio.arcade.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

public class PlayerEnterLobbyEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	private @Getter @Setter Player player;
	
	public PlayerEnterLobbyEvent(Player player)
	{
		this.player = player;
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
