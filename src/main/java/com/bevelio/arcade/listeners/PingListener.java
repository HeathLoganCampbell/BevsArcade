package com.bevelio.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.MainConfig;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.types.GameState;

public class PingListener implements Listener
{
	@EventHandler
	public void onPing(ServerListPingEvent e)
	{
		MainConfig config = ArcadePlugin.getInstance().getConfigManager().getMainConfig();
		if(!config.isEnableMOTD()) return;
		GameManager gameManager = ArcadePlugin.getInstance().getGameManager();
		String message = config.getMotdMessageUnknown();
		if(gameManager.getGameState() == GameState.STARTING)
			message = config.getMotdMessageStarting();
		else if(gameManager.getGameState() == GameState.LIVE || gameManager.getGameState() == GameState.PREGAME)
			message = config.getMotdMessageLive();
		
		if(gameManager.getGame() != null)
		{
			message = message.replaceAll("%Game_Map%", gameManager.getGame().getWorldData().name)
							 .replaceAll("%GameType%", gameManager.getGame().getDisplayName());
		}
		
		e.setMotd(message);
	}
	
//	@EventHandler
//	public void onInteraction(PlayerInteractEvent e)
//	{
//		Player player = e.getPlayer();
//		if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
//		if(e.getItem() == null) return;
//		if(!e.getItem().getType().name().contains("SWORD")) return;
//		Location loc = player.getLocation().clone();
//		loc.subtract(0.2, 0, 0.2);
//		loc.setYaw(loc.getYaw() - 5);
//		Vector dir = loc.getDirection();
//		loc.add(0, 0.5, 0);
//		Vector baseVector = dir.normalize();
//		Vector vector = baseVector.clone();
//		vector.multiply(5.2);
//		
//		
//		ArmorStand armorstand = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().subtract(0, 20, 0), EntityType.ARMOR_STAND);
////		armorstand.setGravity(false);
//		armorstand.setVisible(false);
//		armorstand.setItemInHand(new ItemStack(Material.IRON_SWORD));
//		armorstand.setBasePlate(false);
//		armorstand.setArms(true);
//		armorstand.setRightArmPose(new EulerAngle(Math.toRadians(-10), Math.toRadians(0), Math.toRadians(90)));
//		armorstand.teleport(loc);
//		
//		ItemStack sword = e.getItem();
//		player.setItemInHand(new ItemStack(Material.AIR));
//		
//		Bukkit.getScheduler().scheduleSyncDelayedTask(ArcadePlugin.getInstance(), () -> 
//		{
//			armorstand.setVelocity(vector);
//		}, 8);
//		
//		Bukkit.getScheduler().scheduleSyncDelayedTask(ArcadePlugin.getInstance(), () -> 
//		{
//			armorstand.remove();
//			player.setItemInHand(sword);
//		}, 20 * 2);
//	}
}
