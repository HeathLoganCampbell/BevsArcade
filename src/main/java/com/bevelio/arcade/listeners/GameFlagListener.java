package com.bevelio.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;

public class GameFlagListener implements Listener
{
	private GameManager gm = ArcadePlugin.getInstance().getGameManager();
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e)
	{
		Game game = gm.getGame();
		if(game == null) return;
		Player player = (Player) e.getEntity();
		if(!game.isAlive(player)) return;
		
		if(game.hungerSet != -1)
		{
			e.setFoodLevel((int) gm.getGame().hungerSet);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e)
	{
		Game game = gm.getGame();
		if(game == null) return;
		Player player = e.getPlayer();
		if(!game.isAlive(player)) return;
		
		ItemStack item = e.getItemDrop().getItemStack();
		if(game.dropItems)
		{
			if(game.dropItemsDeny.contains(item.getType()))
				e.setCancelled(true);
		} 
		else
		{
			if(!game.dropItemsAllow.contains(item.getType()))
				e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent e)
	{
		Game game = gm.getGame();
		if(game == null) return;
		Player player = e.getPlayer();
		if(!game.isAlive(player)) return;
		
		ItemStack item = e.getItem().getItemStack();
		if(game.pickUpItems)
		{
			DebugCommands.message(player, "Can pick up!!");
			if(game.pickUpItemsDeny.contains(item.getType()))
			{
				DebugCommands.message(player, "LOL NOPE!!!");
				e.setCancelled(true);
			}
		} 
		else
		{
			if(!game.pickUpItemsAllow.contains(item.getType()))
				e.setCancelled(true);
		}
		
	}

	@EventHandler(priority=EventPriority.LOW)
	public void onCreature(EntitySpawnEvent e)
	{
		Game game = gm.getGame();
		
		if(!(e.getEntity() instanceof LivingEntity))
			return;
		
		if(gm.getNextGame() != null)
			if(gm.getNextGame().getWorld().getName().equalsIgnoreCase(e.getLocation().getWorld().getName()))
				if(!(game.LivingEntitiesAllowed && game.LivingEntitiesAllowedOverride))
					e.setCancelled(true);
		
		if(game == null) return;
		if(!game.getWorld().getName().equalsIgnoreCase(e.getLocation().getWorld().getName())) return;
		if(game.LivingEntitiesAllowed || game.LivingEntitiesAllowedOverride) return;
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onArmorClick(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		Game game = gm.getGame();
		if(game == null) return;
		if(!game.isAlive(player)) return;
		if(game.armorCanBeTakenOff) return;
		if(e.getSlotType() == InventoryType.SlotType.ARMOR)
            e.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onArrowStuck(ProjectileHitEvent e)
	{
		Game game = gm.getGame();
		if(game == null) return;
		if(!game.removeArrows) return;
		if(!(e.getEntity() instanceof Arrow)) return;
		e.getEntity().remove();
	}
}
