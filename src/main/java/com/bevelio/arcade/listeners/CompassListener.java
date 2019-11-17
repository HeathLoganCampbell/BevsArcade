package com.bevelio.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.events.PlayerTrackEvent;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;

public class CompassListener implements Listener
{
	private GameManager gm = ArcadePlugin.getInstance().getGameManager();
	private TranslationConfig tc = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCompass(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Player target = null;
		double bestDis = 0.0;
		
		if(!e.getAction().name().contains("RIGHT_CLICK")) return;
		if(e.getItem() == null) return;
		if(e.getItem().getType() != Material.COMPASS) return;
		if(gm.getGame() == null) return;
		if(!gm.isRunning()) return;
		for(Player posTarget : Bukkit.getOnlinePlayers())
		{
			if(!gm.isInteractivePlayer(posTarget)) continue;
			if(!gm.getGame().isAlive(posTarget)) continue;
			if(posTarget == player) continue;
			if(target == null || player.getLocation().distance(posTarget.getLocation()) < bestDis)
			{
				target = posTarget;
				bestDis = player.getLocation().distance(posTarget.getLocation());
			}
		}
		
		PlayerTrackEvent event = new PlayerTrackEvent(player, target);
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled()) 
		{
			player.sendMessage(tc.getCompassTrackingNoOneFound());
			return;
		}
		
		player.setCompassTarget(event.getTargetLocation());
		player.sendMessage(tc.getCompassTrackingTargetFound().replaceAll("%Target%", event.getTargetName()));
	}
}
