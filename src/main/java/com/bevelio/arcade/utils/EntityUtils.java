package com.bevelio.arcade.utils;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class EntityUtils
{
	public static HashMap<LivingEntity, Double> getInRadius(Location loc, double dR)
	{
		HashMap<LivingEntity, Double> ents = new HashMap<LivingEntity, Double>();

	    for (org.bukkit.entity.Entity cur : loc.getWorld().getEntities())
	    {
	    	if (((cur instanceof LivingEntity)) && (!PlayerUtils.isSpectating(cur)))
	    	{
	    		LivingEntity ent = (LivingEntity)cur;

	    		double offset = MathUtils.offset(loc, ent.getLocation());

	    		if (offset < dR)
	    			ents.put(ent, Double.valueOf(1.0D - offset / dR));
	    	}
	    }
	    return ents;
	}
}
