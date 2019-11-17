package com.bevelio.arcade.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.LobbyConfig;
import com.bevelio.arcade.configs.files.MainConfig;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.pages.KitSelector;
import com.bevelio.arcade.pages.TeamSelector;
import com.bevelio.arcade.types.GameState;

public class LobbyListener implements Listener
{
	private LobbyConfig lc = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig();
	private MainConfig mc = ArcadePlugin.getInstance().getConfigManager().getMainConfig();
	private TranslationConfig tc = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
	
	@EventHandler
	public void onJoin(PlayerSpawnLocationEvent e)
	{
		if(mc.isForceJoinGameOnJoin())
			e.setSpawnLocation(lc.getSpawnLocation());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		if(mc.isForceJoinGameOnJoin())
		{
			String targetsMsg = tc.getCommandJoinMessage();
			
			ArcadePlugin.getInstance().getGameManager().playerJoin(player);
			
			targetsMsg = targetsMsg.replaceAll("%Player%", player.getName());
			player.sendMessage(targetsMsg);
		}
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e)
	{
		if(e.getBlock().getLocation().getWorld() != lc.getSpawnLocation().getWorld()) return;
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if(!lc.isSimpleLobbyProtection()) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e)
	{
		if(e.getBlock().getWorld() != lc.getSpawnLocation().getWorld()) return;
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if(!lc.isSimpleLobbyProtection()) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onWeather(WeatherChangeEvent e)
	{
		if(!lc.isDisableRain()) return;
		if(lc.getSpawnLocation() == null) return;
		if(lc.getSpawnLocation().getWorld() == null) return;
		if(e.getWorld() != lc.getSpawnLocation().getWorld()) return;
		if(e.toWeatherState())
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e)
	{
		Player player = e.getPlayer();
		if(!lc.isDisableItemDropping()) return;
		if(player.getGameMode() == GameMode.CREATIVE) return;
		if(player.getWorld() != lc.getSpawnLocation().getWorld()) return;
		if(!ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player)) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPaintingPop(PlayerInteractEvent e)
	{
		if(e.getPlayer().getLocation().getWorld() != lc.getSpawnLocation().getWorld()) return;
		if(!ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(e.getPlayer())) return;
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if(!lc.isSimpleLobbyProtection()) return;
		if(e.getClickedBlock() == null) return;
		if(!(e.getClickedBlock().getType().name().contains("DOOR") 
			|| e.getClickedBlock().getType().name().contains("BUTTON")
			|| e.getClickedBlock().getType().name().contains("LEAVER")))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onCustomDamage(CustomDamageEvent e)
	{
		if(e.getPlayer() == null) return;
		if(e.getPlayer().getWorld() != lc.getSpawnLocation().getWorld()) return;
		e.setCancelled("In Lobby");
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e)
	{
		 Player player = (Player) e.getEntity();
		 if(player.getWorld() != lc.getSpawnLocation().getWorld()) return;
		 if(!ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player)) return;
		 e.setFoodLevel(20);
	}
	
	@EventHandler
	public void onKitMenu(PlayerInteractEvent e)
	{
		 Player player = e.getPlayer();
		 if(player.getLocation().getWorld() != lc.getSpawnLocation().getWorld()) return;
		 if(player.getGameMode() == GameMode.CREATIVE) return;
		 if(!e.getAction().name().contains("CLICK")) return;
		 if(player.getItemInHand() == null) return;
		 if(player.getItemInHand().getType().name().equalsIgnoreCase(ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getLobbyOpenKitMenu())) 
		 {
			 Display selectorDisplay = ArcadePlugin.getInstance().getDisplayCore().getDisplay(player.getUniqueId(), "kit_selector_" + player.getName());
			 if(selectorDisplay == null)
			 {
				 selectorDisplay = new Display("kit_selector_" + player.getName(), player);
				 ArcadePlugin.getInstance().getDisplayCore().registerDisplay(player.getUniqueId(), selectorDisplay);
			 }
			 selectorDisplay.setPage(new KitSelector(tc.kitMenuSelectorTitle, 0, selectorDisplay));
			 selectorDisplay.open();
			 e.setCancelled(true);
		 } else if(player.getItemInHand().getType().name().equalsIgnoreCase(ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getLobbyOpenTeamMenu())) 
		 {
			 Display selectorDisplay = ArcadePlugin.getInstance().getDisplayCore().getDisplay(player.getUniqueId(), "team_selector_" + player.getName());
			 if(selectorDisplay == null)
			 {
				 selectorDisplay = new Display("team_selector_" + player.getName(), player);
				 ArcadePlugin.getInstance().getDisplayCore().registerDisplay(player.getUniqueId(), selectorDisplay);
			 }
			 selectorDisplay.setPage(new TeamSelector(tc.teamMenuSelectorTitle, 0, selectorDisplay));
			 selectorDisplay.open();
			 e.setCancelled(true);
		 }
	}
}
