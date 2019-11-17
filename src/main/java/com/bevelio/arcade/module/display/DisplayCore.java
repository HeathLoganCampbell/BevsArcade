package com.bevelio.arcade.module.display;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.module.Module;

public class DisplayCore extends Module
{
	private HashMap<UUID, HashMap<String, Display>> displayMap;
	
	public DisplayCore(JavaPlugin plugin)
	{
		super("DisplayCore", plugin);
		this.displayMap = new HashMap<>();
		this.register(this);
	}
	
	public void registerDisplay(UUID uuid, Display display)
	{
		HashMap<String, Display> displays = getDisplays(uuid);
		displays.put(display.getIdName(), display);
		Bukkit.getPluginManager().registerEvents(display, ArcadePlugin.getInstance());
		this.displayMap.put(uuid, displays);
	}
	
	public HashMap<String, Display> getDisplays(UUID uuid)
	{
		if(this.displayMap.containsKey(uuid))
			return this.displayMap.get(uuid);
		return new HashMap<>();
	}
	
	public Display getDisplay(UUID uuid, String displayId)
	{
		return this.getDisplays(uuid).get(displayId);
	}
	
	public void leave(Player player)
	{
		for(Entry<String, Display> entry : this.getDisplays(player.getUniqueId()).entrySet())
			entry.getValue().destroy();
		this.getDisplays(player.getUniqueId()).clear();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		this.leave(player);
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e)
	{
		Player player = e.getPlayer();
		this.leave(player);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e)
	{
		List<HumanEntity> players = e.getViewers();
		Bukkit.getScheduler().scheduleSyncDelayedTask( ArcadePlugin.getInstance(), () -> players.forEach(player -> ((Player) player).updateInventory()), 1l);
	}
}
