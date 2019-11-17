package com.bevelio.arcade.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerEvent;

import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.types.DamageLog;

import lombok.Getter;
import lombok.Setter;

public class CustomDamageEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private @Getter double baseDamage;
	private @Getter DamageCause cause;
	
	private @Getter ArrayList<DamageLog> damagesMod = new ArrayList<>();
	private @Getter ArrayList<DamageLog> damagesMulti = new ArrayList<>();
	private @Getter ArrayList<String> cancelledReasons = new ArrayList<>(); 
	private @Getter HashMap<String, Double> knockbackMod = new HashMap<>();
	private @Getter Location knockbackBaseLoc = null;
	
	private @Getter boolean ignoreArmor = false;
	private @Getter boolean knockback = true;
	private @Getter @Setter double damageReductionArmor;
	
	private @Getter LivingEntity entity;
	private @Getter Player player;
	private @Getter LivingEntity damagerEntity;
	private @Getter Player damagerPlayer;
	private @Getter Projectile projectile;


	public CustomDamageEvent(LivingEntity entity, LivingEntity damager, Projectile projectile, double baseDamage, double damageReduceArmor, DamageCause cause,Location knockbackBaseLoc, String baseReason, String baseSource, boolean preCancelled) {
		
		this.entity = entity;
		if(this.entity != null && this.entity instanceof Player) 
			this.player = (Player) this.entity;
		
		this.damagerEntity = damager;
		if(this.damagerEntity != null && this.damagerEntity instanceof Player) 
			this.damagerPlayer = (Player) this.damagerEntity;
		
		this.projectile = projectile;
		
		this.baseDamage = baseDamage;
		this.cause = cause;
		this.knockbackBaseLoc = knockbackBaseLoc;
		this.damageReductionArmor = damageReduceArmor;
		
		if ((baseSource != null) && (baseReason != null))
			addMod(baseSource, baseReason, 0.0D, true);
		
		if(cause == DamageCause.FALL)
			this.ignoreArmor = true;
		
		if(preCancelled)
			this.setCancelled("Pre-Cancelled");
	}
	
	public void addMod(String source, String reason, double mod, boolean useAttackName)
	{
		this.damagesMod.add(new DamageLog(mod, reason, source, useAttackName));
	}
	
	public void addMulti(String source, String reason, double mod, boolean useAttackName)
	{
		this.damagesMulti.add(new DamageLog(mod, reason, source, useAttackName));
	}
	
	public void addKnockback(String reason, double knockback)
	{
		this.knockbackMod.put(reason, knockback);
	}

	public double getDamage()
	{
		double damage = this.getBaseDamage();

		for (DamageLog mult : this.damagesMod)
			damage += mult.getDamage();
		
	    for (DamageLog mult : this.damagesMulti)
	    	damage *= mult.getDamage();
	    return damage;
	}
	
	public String getReason()
	{
		String reason = "";

	    for (DamageLog change : this.damagesMod)
	    	if (change.isUseReason())
	    		reason = reason + CC.aqua + change.getReason() + CC.gray + ", ";
	    
	    if (reason.length() > 0)
	    {
	    	reason = reason.substring(0, reason.length() - 2);
	    	return reason;
	    }

	    return null;
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
	
	public void setCancelled(String reason)
	{
		this.cancelledReasons.add(reason);
	}
	
	@Deprecated
	public void setCancelled(boolean isCancelled)
	{
		setCancelled("No reason given because SOMEONE IS AN IDIOT");
	}

	@Override
	public boolean isCancelled() 
	{
		return !this.cancelledReasons.isEmpty();
	}
}
