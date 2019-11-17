package com.bevelio.arcade.abilities;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.bevelio.arcade.types.Ability;
import com.bevelio.arcade.utils.InputUtils;
import com.bevelio.arcade.utils.ItemUtils;

/**
 * LeapAbility
 * Get a jump boost on right click of an axe
 * 		 
 * 
 * Options
 *  Type 		Field			Default
 *-----------------------------------------
 * 	Double	 	LeapPower		1.2
 * 	Double	 	CooldownSeconds	6
 * 	String	 	LeapMessage		null
 * 	String	 	ItemName		_AXE
 * 	String	 	SoundEffect		BLAZE_SHOOT
 * 	String	 	ClickType		RIGHT_CLICK
 */
public class LeapAbility extends Ability
{
	private double power = 1.2;
	private double cooldownSeconds = 6;
	private String leapMessage = null;
	private String itemName = "_AXE";
	private Effect soundEffect = Effect.BLAZE_SHOOT;
	private String clickType = "RIGHT_CLICK";

	public LeapAbility() 
	{
		super("LeapAbility", "Get launched in a direction");
		for(Effect sound : Effect.values())
			if(sound.name().contains("BLAZE") && sound.name().contains("SHOOT"))
				this.soundEffect = sound;
	}
	
	@EventHandler
	public void leap(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		if(!e.getAction().name().contains(clickType)) return;
		if(player.getItemInHand() == null) return;
		if(!player.getItemInHand().getType().name().contains(itemName)) return;
		if(!this.hasAbility(player)) return;
		if(!this.isActive(player.getUniqueId())) return;
		
		Entity ent = player;
		if ((player.getVehicle() != null) && 
				((player.getVehicle() instanceof Horse))) 
		{
			ent = player.getVehicle();
		}
		
		Vector vec = ent.getLocation().getDirection();
		vec.normalize();
		vec.multiply(this.power);
		
		ent.setVelocity(vec);
		
		player.setFallDistance(0.0F);
		player.getWorld().playEffect(player.getLocation(), soundEffect, 0);
		this.setCooldown(player.getUniqueId(), cooldownSeconds);
		if(leapMessage != null)
			player.sendMessage(leapMessage);
	}
	

	@Override
	public void setOptions(HashMap<String, Object> options)
	{
		if(options.containsKey("LeapPower"))
			this.power =  InputUtils.getDouble(options.get("LeapPower"));
		
		if(options.containsKey("CooldownSeconds"))
			this.cooldownSeconds = InputUtils.getDouble(options.get("CooldownSeconds"));
		
		if(options.containsKey("LeapMessage"))
			this.leapMessage = (String) options.get("LeapMessage");
		
		if(options.containsKey("ItemName"))
			this.itemName = (String) options.get("ItemName");
		
		if(options.containsKey("ClickType"))
			this.clickType = (String) options.get("ClickType");
		
		if(options.containsKey("SoundEffect"))
		{
			String effectStr = (String) options.get("SoundEffect");
			for(Effect effect : Effect.values())
				if(effect.name().equalsIgnoreCase(effectStr))
				{
					this.soundEffect = effect;
					break;
				}
		}
	}
}
