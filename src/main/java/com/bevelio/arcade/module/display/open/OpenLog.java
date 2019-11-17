package com.bevelio.arcade.module.display.open;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bevelio.arcade.module.display.Display;

public class OpenLog 
{
	private final Display 	display;
	private final Player 	player;
	private final Inventory inventory;
	private 	  boolean 	cancelled;
	
	
	public OpenLog(Display display, Player player, Inventory inventory) 
	{
		this.display = display;
		this.player = player;
		this.inventory = inventory;
		this.cancelled = false;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public Display getDisplay() {
		return display;
	}
	public Player getPlayer() {
		return player;
	}
	public Inventory getInventory() {
		return inventory;
	}
	
	
}
