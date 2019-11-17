package com.bevelio.arcade.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.bevelio.arcade.events.PostApplyKitEvent;
import com.bevelio.arcade.events.PreApplyKitEvent;
import com.bevelio.arcade.misc.ItemStackBuilder;

import lombok.Getter;
import lombok.Setter;

public class Kit 
{
	public static final Kit NULL_KIT = new Kit("none", "None");
	private @Getter @Setter String name;
	private @Getter @Setter String displayName;
	private @Getter @Setter String permission;
	private @Getter @Setter List<String> description;
	private @Getter @Setter List<String> abilities;
	private @Getter @Setter boolean isFree;
	private @Getter @Setter double price;
	
	
	private @Getter HashMap<Integer, ItemStack> items;
	private @Getter @Setter ItemStack helmet;
	private @Getter @Setter ItemStack chestplate;
	private @Getter @Setter ItemStack leggings;
	private @Getter @Setter ItemStack boots;
	private @Getter @Setter ItemStackBuilder icon;
	private @Getter @Setter EntityType entityType;
	
	private @Getter @Setter List<PotionEffect> effects;
	
	public Kit(String name)
	{
		this(name, name);
	}
	
	public Kit(String name, String displayName)
	{
		this.name = name;
		this.displayName = displayName;
		this.permission = "bevsarcade.kit." + name.toLowerCase();
		this.isFree = false;
		this.price = 400;
		
		this.icon = new ItemStackBuilder(Material.DIRT);
		this.entityType = EntityType.ZOMBIE;
		
		this.items = new HashMap<>();
		this.effects = new ArrayList<>();
	}
	
	public void apply(Player player)
	{
		PreApplyKitEvent eventPre = new PreApplyKitEvent(player, this, false);
		Bukkit.getPluginManager().callEvent(eventPre);
		if(eventPre.isCancelled()) return;
		
		PlayerInventory inv = player.getInventory();
		this.getItems().forEach((slot, item) -> 
		{
			inv.setItem(slot, item);
		});
		if(this.getHelmet() != null)
			inv.setHelmet(this.getHelmet());
		if(this.getChestplate() != null)
			inv.setChestplate(this.getChestplate());
		if(this.getLeggings() != null)
			inv.setLeggings(this.getLeggings());
		if(this.getBoots() != null)
			inv.setBoots(this.getBoots());
		
		this.getEffects().forEach(potionEffect -> player.addPotionEffect(potionEffect));
		
		PostApplyKitEvent eventPost = new PostApplyKitEvent(player, this, false);
		Bukkit.getPluginManager().callEvent(eventPost);
	}
}
