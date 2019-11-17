package com.bevelio.arcade.managers;

import java.util.HashMap;

import com.bevelio.arcade.abilities.*;
import com.bevelio.arcade.types.Ability;

public class AbilityManager 
{
	private HashMap<String, Class<?>> abilityClazz = new HashMap<>();
	
	public AbilityManager()
	{
		this.registerAbilityByClass(SpleefArcherAbility.class);
		this.registerAbilityByClass(SpleefBrawlerAbility.class);
		this.registerAbilityByClass(LeapAbility.class);
		this.registerAbilityByClass(ExtractAbility.class);
		this.registerAbilityByClass(FletcherAbility.class);
		this.registerAbilityByClass(KnockbackAbility.class);
		this.registerAbilityByClass(SlamBamAbility.class);
		this.registerAbilityByClass(HardSkinAbility.class);
	}
	
	public Ability getAbility(String name)
	{
		Class<?> abClazz = this.abilityClazz.get(name);
		if(abClazz == null) return null;
		try {
			return (Ability) abClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void registerAbilityByClass(Class<? extends Ability> abilityClazz)
	{
		Ability abs = null;
		try {
			abs = (Ability) abilityClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		this.registerAbility(abs);
	}

	public void registerAbility(Ability ability)
	{
		this.abilityClazz.put(ability.getName(), ability.getClass());
	}
}
