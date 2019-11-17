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
 * HardSkinAbility
 * Take less damage
 * 		 
 * 
 * Options
 *  Type 		Field			Default
 *  Double		damageReduction	0.5 (Half a heart)
 *-----------------------------------------
 */
public class HardSkinAbility extends Ability
{
	private double damageReduction = 0.5;

	public HardSkinAbility() 
	{
		super("HardSkinAbility", "Take less damage");
	}
	
	@EventHandler
	public void onDamage(CustomDamageEvent e)
	{
		if(e.getDamagerPlayer() == null)
			return;
		Player player = e.getDamagerPlayer();
		if(!this.hasAbility(player)) return;
		if(!this.isActive(player.getUniqueId())) return;
		
		e.addMod(player.getName(), "Reduce Damage", -this.damageReduction, false);
	}
	

	@Override
	public void setOptions(HashMap<String, Object> options)
	{
		if(options.containsKey("DamageReduction"))
			this.damageReduction =  InputUtils.getDouble(options.get("DamageReduction"));
	}
}
