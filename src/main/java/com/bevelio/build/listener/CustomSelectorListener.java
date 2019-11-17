package com.bevelio.build.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.utils.Pair;

public class CustomSelectorListener implements Listener
{
	public static HashMap<UUID, Pair<Location, Location>> selectedBlocks = new HashMap<>();
	private static Material wandMaterial = Material.BLAZE_ROD;
	private static String wandName = "Wand";
	
	@EventHandler
	public void onLeft(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getBlock();
		UUID uuid = player.getUniqueId();
		if(block == null) return;
		if(block.getType() == Material.AIR) return;
		if(player.getItemInHand() == null) return;
		if(player.getItemInHand().getType() != wandMaterial) return;
		if(!player.getItemInHand().hasItemMeta()) return;
		if(!player.getItemInHand().getItemMeta().hasDisplayName()) return;
		if(!player.getItemInHand().getItemMeta().getDisplayName().contains(this.wandName)) return;
		Pair<Location, Location> pair = new Pair<Location, Location>(block.getLocation(), null);
		if(selectedBlocks.containsKey(uuid))
			pair = selectedBlocks.get(uuid);
		pair.setLeft(block.getLocation());
		selectedBlocks.put(uuid, pair);
		player.sendMessage(CC.gray + "Location left selected");
		updateRegion(player);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onRight(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();
		UUID uuid = player.getUniqueId();
		if(block == null) return;
		if(block.getType() == Material.AIR) return;
		if(player.getItemInHand() == null) return;
		if(player.getItemInHand().getType() != wandMaterial) return;
		if(!player.getItemInHand().hasItemMeta()) return;
		if(!player.getItemInHand().getItemMeta().hasDisplayName()) return;
		if(!player.getItemInHand().getItemMeta().getDisplayName().contains(wandName)) return;
		if(!e.getAction().name().contains("RIGHT")) return;
		Pair<Location, Location> pair = new Pair<Location, Location>(null, block.getLocation());
		if(selectedBlocks.containsKey(uuid))
			pair = selectedBlocks.get(uuid);
		pair.setRight(block.getLocation());
		selectedBlocks.put(uuid, pair);
		player.sendMessage(CC.gray + "Location right selected");
		updateRegion(player);
		e.setCancelled(true);
	}
	
	public static void updateRegion(Player player)
	{
		player.sendMessage(getBlocks(player).size() + " Blocks");
		
	}
	
	public static List<Block> getBlocks(Player player)
	{
		Pair<Location, Location> pair = CustomSelectorListener.selectedBlocks.get(player.getUniqueId());
		Location pos1 = pair.getLeft();
		Location pos2 = pair.getRight();
		if(pos1 == null || pos2 == null) return null;
		
		int dx =  pos2.getBlockX() - pos1.getBlockX();
		int dy =  pos2.getBlockY() - pos1.getBlockY();
		int dz =  pos2.getBlockZ() - pos1.getBlockZ();
		
		int biasX = dx < 0 ? -1 : 1;
		int biasY = dy < 0 ? -1 : 1;
		int biasZ = dz < 0 ? -1 : 1;
		
		player.sendMessage("X: " + dx +", Y: " + dy + ", Z: " + dz );
		
		List<Block> blocks = new ArrayList<>();
		for(int x = 0; x <= Math.abs(dx); x++)
			for(int y = 0; y <= Math.abs(dy); y++)
				for(int z = 0; z <= Math.abs(dz); z++)
					blocks.add(pos1.clone().add((x * biasX), (y * biasY), (z * biasZ)).getBlock());
		return blocks;
		
	}
	
	public static ItemStack getWand()
	{
		return new ItemStackBuilder(wandMaterial).displayName(wandName).build();
	}
}
