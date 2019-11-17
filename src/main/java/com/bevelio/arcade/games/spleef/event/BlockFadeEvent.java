package com.bevelio.arcade.games.spleef.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BlockFadeEvent extends PlayerEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Block block;
	
	public BlockFadeEvent(Player who, Block block)
	{
		super(who);
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
