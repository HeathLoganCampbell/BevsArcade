package com.bevelio.arcade.listeners;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.events.GameStateChangeEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.utils.PlayerUtils;

public class RejoinListener implements Listener
{
	private GameManager gm = ArcadePlugin.getInstance().getGameManager();
	private HashMap<UUID, Long> playerRejoinTime = new HashMap<UUID, Long>();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		Game game = gm.getGame();
		if(game == null) return;
		if(!game.isAlive(player)) return;
		if(!game.quitOut) 
		{
			gm.broadcast(CC.gray + "Play has quit and has 2 minutes to reconnect.");//TODO
			playerRejoinTime.put(player.getUniqueId(), (long) (System.currentTimeMillis() + (1000 * game.maxRejoinSeconds)));
		} else {
			PlayerUtils.clear(player);
			PlayerUtils.reset(player);
			game.setPlayState(player, PlayState.OUT);
			game.removeMemeber(player.getUniqueId());
			gm.broadcast(CC.gray + "Player has quit mid game.");//TODO
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		Game game = gm.getGame();
		if(game == null) return;
		if(game.quitOut) return;
		if(!playerRejoinTime.containsKey(player.getUniqueId())) return;
		if(playerRejoinTime.get(player.getUniqueId()) > System.currentTimeMillis())
		{
			playerRejoinTime.remove(player.getUniqueId());
			gm.broadcast(CC.gray + "Player has reconnected");//TODO
			return;
		} 
	}
	
	@EventHandler
	public void onJoin(PlayerSpawnLocationEvent e)
	{
		Player player = e.getPlayer();
		Game game = gm.getGame();
		if(game == null) return;
		if(!game.quitOut) return;
		if(ArcadePlugin.getInstance().getConfigManager().getMainConfig().isForceJoinGameOnJoin()) return;
		if(e.getSpawnLocation().getWorld().getName().contains(ArcadePlugin.getInstance().getWorldCreatorManager().getFileWorld(null)))
		{
			Location spawn = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation();
			e.setSpawnLocation(spawn);
//			ArcadePlugin.getInstance().getGameManager().toLobby(player);
		}
	}
	
	@EventHandler
	public void onUpdateState(GameStateChangeEvent e)
	{
		if(e.getTo() == GameState.FINISHING)
			this.playerRejoinTime.clear();
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UpdateType.SECOND) return;
		if(gm.getGame() == null) return;
		if(gm.getGameState() != GameState.LIVE) return;
		
		for(Entry<UUID, Long> rejoinSet : playerRejoinTime.entrySet())
		{
			if(rejoinSet.getValue() < System.currentTimeMillis())
			{
				UUID uuid = rejoinSet.getKey();
				gm.getGame().getPlayers().put(uuid, PlayState.OUT);
				gm.getGame().removeMemeber(uuid);
				playerRejoinTime.remove(uuid);
				gm.broadcast(CC.gray + "Player has not reconnected in 2 minutes. So they are out.");//TODO
			}
		}
	}
}
