package com.bevelio.arcade.abilities;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.types.Ability;
import com.bevelio.arcade.utils.ItemUtils;

/**
 * Get item when block is broken
 * Uses: Spleef#Snowballer
 * 
 * Options
 *  Type 		Field			Default
 *-----------------------------------------
 *  Boolean		Whitelist		False
 *  Boolean		PlaySoundEffect	False
 * 	ItemStack 	ExtractionBlock	SNOW_BLOCK (Ignored because whitelist off)
 *  ItemStack 	ExtractedItem	SNOW_BALL 0 1 
 *  String		SoundEffect		LAVA_POP
 *  
 */
public class ExtractAbility extends Ability
{
	private boolean   whitelistEnabled = false;
	private ItemStack extractionBlock  = new ItemStack(Material.SNOW_BLOCK)
				    , extractedItem    = new ItemStack(Material.SNOW_BALL);
	private boolean playSoundEffect = false;
	private Sound soundEffect = null;
	
	public ExtractAbility()
	{
		super("ExtractAbility", "Get item from block when broken");
		for(Sound sound : Sound.values())
			if(sound.name().contains("LAVA") && sound.name().contains("POP"))
				this.soundEffect = sound;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(whitelistEnabled)
			if(block.getType() != extractionBlock.getType() && block.getData() != extractionBlock.getData().getData())
				return;
		if(!this.hasAbility(player)) return;
		player.getInventory().addItem(this.extractedItem);
		if(this.playSoundEffect)
			player.getWorld().playSound(player.getLocation(), soundEffect, 1, 1);
	}
	@Override
	public void setOptions(HashMap<String, Object> options)
	{
		if(options.containsKey("Whitelist"))
			if(options.get("Whitelist") instanceof Boolean)
			{
				this.whitelistEnabled = (Boolean) options.get("Whitelist");
			} else System.out.println(this.getName() + " the field " + "Whitelist" + " does't accept anything other than a boolean. You gave it" + options.get("Whitelist"));
	
		if(options.containsKey("PlaySoundEffect"))
			if(options.get("PlaySoundEffect") instanceof Boolean)
			{
				this.playSoundEffect = (Boolean) options.get("PlaySoundEffect");
			} else System.out.println(this.getName() + " the field " + "PlaySoundEffect" + " does't accept anything other than a boolean. You gave it" + options.get("Whitelist"));
	
		
		if(options.containsKey("ExtractedItem"))
			this.extractedItem = ItemUtils.parseItem((String) options.get("ExtractedItem"))[0];
		
		if(options.containsKey("ExtractionBlock"))
			this.extractionBlock = ItemUtils.parseItem((String) options.get("ExtractionBlock"))[0];
		
		if(options.containsKey("SoundEffect"))
		{
			String effectStr = (String) options.get("SoundEffect");
			for(Sound effect : Sound.values())
				if(effect.name().equalsIgnoreCase(effectStr))
				{
					this.soundEffect = effect;
					break;
				}
		}
	}
}
