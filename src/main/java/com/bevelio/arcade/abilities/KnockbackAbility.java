package com.bevelio.arcade.abilities;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.types.Ability;
import com.bevelio.arcade.utils.InputUtils;
import com.bevelio.arcade.utils.ItemUtils;

/**
 * KnockbackAbility
 * You deal more knockback when you damage a player
 * 		 
 * 
 * Options
 *  Type 		Field			Default
 *  Double		KnockbackPower	0.3
 *-----------------------------------------
 */
public class KnockbackAbility extends Ability
{
	private double power = 0.3;

	public KnockbackAbility() 
	{
		super("KnockbackAbility", "Deal extra knockback when damaging others");
	}
	
	@EventHandler
	public void onDamage(CustomDamageEvent e)
	{
		if(e.getDamagerPlayer() == null)
			return;
		Player player = e.getDamagerPlayer();
		if(!this.hasAbility(player)) return;
		if(!this.isActive(player.getUniqueId())) return;
		
		e.addKnockback(this.getName(), this.power);
	}
	

	@Override
	public void setOptions(HashMap<String, Object> options)
	{
		if(options.containsKey("KnockbackPower"))
			this.power =  InputUtils.getDouble(options.get("LeapPower"));
	}
}
