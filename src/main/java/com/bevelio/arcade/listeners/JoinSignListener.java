package com.bevelio.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.SpecialLocationsConfig;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.events.PlayerTrackEvent;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;

public class JoinSignListener implements Listener
{
	private GameManager gm = ArcadePlugin.getInstance().getGameManager();
	private TranslationConfig tc = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
	private SpecialLocationsConfig sl = ArcadePlugin.getInstance().getConfigManager().getSpecialLocationsConfig();
	
	@EventHandler
	public void onSign(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();
		if(block == null)
			return;
		if(!block.getType().name().contains("SIGN"))
			return;
		Sign sign = (Sign) block.getState();
		
		if(this.holdingItem(e.getItem()))
			return;
		
		if(sl.getSigns().contains(sign))
		{
			String targetsMsg = tc.getCommandJoinMessage();
			
			ArcadePlugin.getInstance().getGameManager().playerJoin(player);
			
			targetsMsg = targetsMsg.replaceAll("%Player%", player.getName());
			player.sendMessage(targetsMsg);
			
			player.teleport(ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation());
		}
	}
	
	public boolean holdingItem(ItemStack item)
	{
		if(item == null)
			return false;
		if(!item.hasItemMeta())
			return false;
		if(!item.getItemMeta().hasDisplayName())
			return false;
		if(!item.getItemMeta().getDisplayName().contains(tc.getSignToolName()))
			return false;
		return true;
	}
	
	@EventHandler
    public void deleteSign(BlockBreakEvent e)
	{
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if(e.isCancelled())
        	return;
        if(!block.getType().name().contains("SIGN"))
			return;
		Sign sign = (Sign) block.getState();
		ItemStack item = player.getItemInHand();
		if(!this.holdingItem(item))
			return;
		if(!player.hasPermission("bevsarcade.sign.break"))
			return;
		sl.deleteSign(sign.getLocation());
		sl.loadSign();
		player.sendMessage(tc.getSignPlaceBroken());
	}
	
	@EventHandler
    public void createSign(BlockPlaceEvent e)
	{
        Player player = e.getPlayer();
        Block block = e.getBlockPlaced();
        if(e.isCancelled())
        	return;
        if(!block.getType().name().contains("SIGN"))
			return;
		Sign sign = (Sign) block.getState();
		ItemStack item = e.getItemInHand();
		if(!this.holdingItem(item))
			return;
		if(!player.hasPermission("bevsarcade.sign.place"))
			return;
		sl.saveSign(sign.getLocation());
		sl.loadSign();
		player.sendMessage(tc.getSignPlaceSuccess());
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UpdateType.SECOND) return;
		
		String currentGame = (gm.getGame() != null ? gm.getGame().getGameTypeName() : tc.getJoinSignUnknownCurrentMap());
		for(Sign sign : this.sl.getSigns())
		{
			int i = 0;
			for(String line : new String[] { tc.getJoinSignsLineOne(), tc.getJoinSignsLineTwo(), tc.getJoinSignsLineThree(), tc.getJoinSignsLineFour() })
			{
				
				line = line.replaceAll("%Players%", gm.getInteractivePlayers().size() + "")
						   .replaceAll("%Current_Map%", currentGame);
				line = ChatColor.translateAlternateColorCodes('&', line);
				sign.setLine(i, line);
				i++;
			}
			sign.update();
		}
	}
}
