package com.bevelio.arcade.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class PlayerUtils 
{
	public static boolean contains(Player player, String itemNameContains, Material itemType, byte data, int required)
	{
		if(required <= 0) return true;
		
		for(ItemStack item : getItems(player))
		{
			if((item == null || item.getType() == Material.AIR)) continue;
			if(item.getType() != itemType) continue;
			if(data > 0)
				if(item.getData().getData() != data) continue;
			if(itemNameContains != null)
				if(item.hasItemMeta()
					&& item.getItemMeta().hasDisplayName()
					&& item.getItemMeta().getDisplayName().contains(itemNameContains))
						required -= item.getAmount();
		}
		
		if(required <= 0) return true;
		return false;
	}
	
	public static void clear(Player player)
	{
		PlayerInventory inv = player.getInventory();

		inv.clear();
		inv.setArmorContents(new ItemStack[4]);
		player.setItemOnCursor(new ItemStack(Material.AIR));

		player.saveData();
	}
	
	public static void reset(Player player)
	{
		player.resetMaxHealth();
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.setExp(0);
		player.setLevel(0);
		
		player.setFoodLevel(20);
		player.setSaturation(1f);
		
		player.setFlying(false);
		player.setAllowFlight(false);
		
		for(PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
	}
	
	public static List<ItemStack> getItems(Player player)
	{
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	    PlayerInventory inv = player.getInventory();

	    items.add(player.getItemOnCursor());
	    
	    for (ItemStack item : inv.getContents())
	      if ((item != null) && (item.getType() != Material.AIR))
	        items.add(item.clone());
	    return items;
	}
	
	public static void drop(Player player, boolean clear)
	{
		for (ItemStack cur : getItems(player))
			player.getWorld().dropItemNaturally(player.getLocation(), cur);

		if (clear)
			clear(player);
	}
	
	public static boolean isSpectating(Entity player)
	{
		if(player instanceof Player)
		{
			Player realPlayer = (Player) player;
			return realPlayer.getGameMode() == GameMode.CREATIVE || realPlayer.getGameMode() == GameMode.SPECTATOR;
		}
		return false;
	}
}
