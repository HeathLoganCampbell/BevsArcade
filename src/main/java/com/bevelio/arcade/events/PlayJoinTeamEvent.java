package com.bevelio.arcade.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.types.Team;

import lombok.Getter;
import lombok.Setter;

public class PlayJoinTeamEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private @Getter @Setter Player player;
	private @Getter @Setter Team team;
	private boolean isCancelled;
	
	public PlayJoinTeamEvent(Player player, Team team)
	{
		this.player = player;
		this.team = team;
		
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
