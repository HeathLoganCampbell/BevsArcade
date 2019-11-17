package com.bevelio.arcade.abilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;

import com.bevelio.arcade.games.spleef.Spleef;
import com.bevelio.arcade.types.Ability;
import com.bevelio.arcade.types.Kit;
import com.bevelio.arcade.utils.InputUtils;

public class SpleefBrawlerAbility extends Ability
{
	private static final BlockFace[] touchingBlocks = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}; 
	private double cooldownSeconds = 0.05;
	private String itemName = "_AXE";
	
	public SpleefBrawlerAbility() 
	{
		super("SpleefBrawlerAbility", "Break blocks with axe.");
	}
	
	@EventHandler
	public void onBlockHit(BlockDamageEvent e)
	{
		Player player = e.getPlayer();
		Block baseBlock = e.getBlock();
		if(player == null) return;
		if(!this.hasAbility(player)) return;
		if(!(this.isActive(player.getUniqueId()))) return;
		if(!player.getItemInHand().getType().name().contains(itemName)) return;
		
		for(BlockFace face :touchingBlocks)
		{
			Block newBlock = baseBlock.getRelative(face);
			Spleef.damageBlock(player, newBlock);
		}
		this.setCooldown(player.getUniqueId(), cooldownSeconds);
	}
	
	@Override
	public void setOptions(HashMap<String, Object> options)
	{
		if(options.containsKey("ItemName"))
			this.itemName = (String) options.get("ItemName");
	}
}
