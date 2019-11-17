package com.bevelio.arcade.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.configs.files.LobbyConfig;
import com.bevelio.arcade.events.PreApplyKitEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.events.GameStateChangeEvent;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.Kit;
import com.bevelio.arcade.types.Team;
import com.bevelio.arcade.utils.PlayerUtils;
import com.bevelio.arcade.utils.ShapeUtils;

public class GameStateListener implements Listener
{
	private LobbyConfig lc;
	private GameManager gm;
	
	public GameStateListener()
	{
		this.lc = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig();
		this.gm = ArcadePlugin.getInstance().getGameManager();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onGameState(GameStateChangeEvent e)
	{
		if(this.gm.getGame() != null)
		{
			int minPlayers = ArcadePlugin.getInstance().getConfigManager().getMainConfig().getMinNumberOfPlayersToStart();
			if(gm.getInteractivePlayers().size() < minPlayers && (e.getTo() == GameState.STARTING ))
			{
				e.setCancelled(true);
				return;
			}
			
			
			Game game = this.gm.getGame();
			if(e.getTo() == game.getRegisterAtState())
				Bukkit.getPluginManager().registerEvents(game, ArcadePlugin.getInstance());
			
			if(e.getTo() == GameState.PREGAME)
			{
				game.onPreStart();
				game.onStartAnnouncement();
			} 
			else if(e.getTo() == GameState.LIVE)
			{
				game.onStart();
				e.setSeconds(game.getWorldData().maxSeconds);
			}
			else if(e.getTo() == GameState.FINISHING)
			{
				this.gm.getGame().onEndAnnouncement();
			}
		}
		
		if(e.getTo() == GameState.WAITING)
		{
			if(e.getFrom() != GameState.STARTING)
			{
				if(lc.isPlateformGenerate())
				{
					lc.getPlateformFloorBlocks();
					lc.getPlateformWallsBlocks();
					int radius = lc.getPlateformRadius();
					
						ShapeUtils.getCircle(lc.getSpawnLocation().clone().add(0, -2, 0), false, radius).forEach(loc -> 
						{
							Block block = loc.getBlock();
							
							block.setType(lc.getPlateformFloorBlocks().getType());
							block.setData(lc.getPlateformFloorBlocks().getData().getData());
						});
					
				}
				
				HandlerList.unregisterAll(gm.getGame());
				gm.nextGame(); //Create new game.
				
			
				Bukkit.getOnlinePlayers().forEach(player -> 
				{
					if(!gm.isInteractivePlayer(player)) return;
					player.setFlying(false);
					player.setAllowFlight(false);
					DebugCommands.message(player, "You have been respawned.");
					
					player.setMaxHealth(20);
					player.setHealth(20);
					player.setFoodLevel(20);
					player.setSaturation(1f);
					
					Bukkit.getOnlinePlayers().forEach(viewer ->  
					{
						if(viewer != player)
							if(!viewer.canSee(player))
								viewer.showPlayer(player);
					});
					
					if(gm.isInteractivePlayer(player))
					{
						gm.toLobby(player);
					}
				});
			}  
		}
		
		if(e.getTo() == GameState.STARTING)
			gm.getGame().hanndleTeamPreferences();
		
		if(e.getTo() == GameState.PREGAME)
		{
			gm.getGame().hanndleTeamPreferences();
			Bukkit.getOnlinePlayers().forEach(player -> 
			{
				if(gm.isInteractivePlayer(player))
				{
					String teamName = null;
					for(Entry<String, ArrayList<UUID>> prefTeamSet : gm.getGame().getPlayersPrefTeams().entrySet())
						if(prefTeamSet.getValue().contains(player.getUniqueId()))
							teamName = prefTeamSet.getKey();
					if(teamName == null)
						gm.getGame().hanndleTeamPreferences();
					
					Team team = gm.getGame().getTeam(teamName);
					if(team == null) return;
					
					gm.getGame().addMember(team, player);
					DebugCommands.message(player, "You are in team " + team.getDisplayName() + CC.gray + "!");
				}
			});
		}
		
		if(e.getTo() == GameState.PREGAME)
		{
			Bukkit.getOnlinePlayers().forEach(player -> 
			{
				if(gm.isInteractivePlayer(player))
					gm.getGame().respawnPlayer(player);
			});
			//this.teleportAllToNewGame();
		}
	}
	
	public void teleportAllToNewGame()
	{
		World world = null;
		System.out.println("World: " + ArcadePlugin.getInstance().getWorldManager().getWorld());
		System.out.println("Next World: " + ArcadePlugin.getInstance().getWorldManager().getNextWorld());
		world = ArcadePlugin.getInstance().getWorldManager().getWorld();
		Location location = new Location(world , 530, 0, 811);
		Bukkit.getOnlinePlayers().forEach(player -> 
		{
			if(gm.isInteractivePlayer(player))
				player.teleport(location);
		});
	}
}
