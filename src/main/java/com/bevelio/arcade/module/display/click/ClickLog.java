package com.bevelio.arcade.module.display.click;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.module.display.Display;

public class ClickLog 
{
	private final Player 	player;
	private final boolean 	leftClick
						  , rightClick
				          , shiftClick;
	private 	  boolean 	cancelled;
	private final Display 	display;
	
	private final ItemStack item;
	private final int		slot;
	
	public ClickLog(Player player, boolean leftClick, boolean rightClick, boolean shiftClick, ItemStack item, int slot, Display display) 
	{
		this.player = player;
		this.leftClick = leftClick;
		this.rightClick = rightClick;
		this.shiftClick = shiftClick;
		this.item = item;
		this.slot = slot;
		this.display = display;
		this.cancelled = true;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the leftClick
	 */
	public boolean isLeftClick() {
		return leftClick;
	}

	/**
	 * @return the rightClick
	 */
	public boolean isRightClick() {
		return rightClick;
	}

	/**
	 * @return the shiftClick
	 */
	public boolean isShiftClick() {
		return shiftClick;
	}

	/**
	 * @return the item
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}

	public Display getDisplay() {
		return display;
	}
}
