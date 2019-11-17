package com.bevelio.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.types.PlayState;

public class SpectatorListener implements Listener
{
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		
		if(!ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player)) return;
		
		DebugCommands.message(player, "You have died.");
		DebugCommands.message(player, "Are you a Interactive player? " + (ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player) ? "Yes" : "No"));
		if(!ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player)) return;
		Game game = ArcadePlugin.getInstance().getGameManager().getGame();
		DebugCommands.message(player, "Checking if game is null.");
		if(game == null) return;
		DebugCommands.message(player, "Game is not null.");
		game.setPlayState(player, PlayState.OUT);
		if(game.deathOut)
		{
			DebugCommands.message(player, "Death out is Enabled.");
			//TODO
			return;
		}
		
		DebugCommands.message(player, "Specatator death time is " + DebugCommands.spec(game.deathSpecatatorSeconds + "") +".");
		if(game.deathSpecatatorSeconds <= 0.0)
			game.respawnPlayer(player);
		else
		{
			game.getRespawnTimestamp().put(player.getUniqueId(), (long) (System.currentTimeMillis() + (game.deathSpecatatorSeconds * 1000)));
			Bukkit.getScheduler().scheduleSyncDelayedTask(ArcadePlugin.getInstance(), () -> game.respawnPlayer(player), (long) (game.deathSpecatatorSeconds * 20));
		}
	}
	
	@EventHandler
	public void onDamage(CustomDamageEvent e)
	{
		Player attacker = e.getDamagerPlayer();
		if(attacker == null) return;
		Game game = ArcadePlugin.getInstance().getGameManager().getGame();
		if(game.isAlive(attacker)) return;
		e.setCancelled("Attacker is dead.");
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e)
	{
		Player attacker = e.getPlayer();
		if(attacker == null) return;
		Game game = ArcadePlugin.getInstance().getGameManager().getGame();
		if(game == null) return;
		if(game.isAlive(attacker)) return;
		if(attacker.getWorld() != game.getWorld()) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e)
	{
		Player attacker = e.getPlayer();
		if(attacker == null) return;
		Game game = ArcadePlugin.getInstance().getGameManager().getGame();
		if(game == null) return;
		if(game.isAlive(attacker)) return;
		if(attacker.getWorld() != game.getWorld()) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBreakPainting(HangingBreakByEntityEvent e)
	{
		Entity attacker = e.getRemover();
		Player player = null;
		if(attacker instanceof Projectile)
			if( ((Projectile) attacker).getShooter() instanceof Player)
				player = (Player) ((Projectile) attacker).getShooter();
		if(attacker instanceof Player)
			player = (Player) attacker;
		if(attacker == null) return;
		Game game = ArcadePlugin.getInstance().getGameManager().getGame();
		if(game == null) return;
		if(game.isAlive(player)) return;
		if(attacker.getWorld() != game.getWorld()) return;
		e.setCancelled(true);
	}
}
