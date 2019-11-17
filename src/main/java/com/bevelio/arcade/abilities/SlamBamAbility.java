package com.bevelio.arcade.abilities;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bevelio.arcade.types.Ability;
import com.bevelio.arcade.utils.EntityUtils;
import com.bevelio.arcade.utils.InputUtils;
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
public class SlamBamAbility extends Ability
{
	private String slambamItem = "IRON_SPADE";
	private double affectRadius = 8;
	private double launchPower = 2;
	private double cooldownSeconds = 6;
	private String slambamMessage = null;
	private String slambamMessageToVictim = null;
	private String clickType = "RIGHT_CLICK";
	private Sound slamSound = null;//Sound.ZOMBIE_WOOD
	
	public SlamBamAbility()
	{
		super("SlamBamAbility", "Do damage to all those around you");
		for(Sound sound : Sound.values())
			if(sound.name().contains("ZOMBIE") && sound.name().contains("WOOD"))
				this.slamSound = sound;
	}
	
	@EventHandler
	public void leap(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		if(!e.getAction().name().contains(clickType)) return;
		if(player.getItemInHand() == null) return;
		if(!player.getItemInHand().getType().name().contains(slambamItem)) return;
		if(!this.hasAbility(player)) return;
		if(!this.isActive(player.getUniqueId())) return;
		
		Entity ent = player;
		if ((player.getVehicle() != null) && 
				((player.getVehicle() instanceof Horse))) 
		{
			ent = player.getVehicle();
		}
		
		HashMap<LivingEntity, Double> targets = EntityUtils.getInRadius(ent.getLocation(), this.affectRadius);
		for(LivingEntity entity : targets.keySet() )
		{
			if(entity == player)
				continue;
			
			double distance = targets.get(entity);
			double launchY = this.launchPower - distance;
			Vector vel = entity.getVelocity();
			vel.setY(launchY);
			entity.setVelocity(vel);
			
			
			if ((entity instanceof Player))
				entity.sendMessage(this.slambamMessageToVictim);
		}
		
		player.getWorld().playSound(player.getLocation(), slamSound, 2.0F, 0.2F);
		this.setCooldown(player.getUniqueId(), cooldownSeconds);
		if(slambamMessage != null)
			player.sendMessage(slambamMessage);
	}
	

	@Override
	public void setOptions(HashMap<String, Object> options)
	{
		if(options.containsKey("LaunchPower"))
			this.launchPower =  InputUtils.getDouble(options.get("LaunchPower"));
		
		if(options.containsKey("AffectRadius"))
			this.affectRadius =  InputUtils.getDouble(options.get("AffectRadius"));
		
		if(options.containsKey("CooldownSeconds"))
			this.cooldownSeconds = InputUtils.getDouble(options.get("CooldownSeconds"));
		
		if(options.containsKey("SlambamMessage"))
			this.slambamMessage = (String) options.get("SlambamMessage");
		
		if(options.containsKey("slambamMessageToVictim"))
			this.slambamMessageToVictim = (String) options.get("slambamMessageToVictim");
		
		if(options.containsKey("ItemName"))
			this.slambamItem = (String) options.get("ItemName");
		
		if(options.containsKey("ClickType"))
			this.clickType = (String) options.get("ClickType");
		
		if(options.containsKey("SoundEffect"))
		{
			String effectStr = (String) options.get("SoundEffect");
			for(Sound effect : Sound.values())
				if(effect.name().equalsIgnoreCase(effectStr))
				{
					this.slamSound = effect;
					break;
				}
		}
	}
}
