package com.bevelio.arcade.managers;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.MainConfig;
import com.bevelio.arcade.events.GameStateChangeEvent;
import com.bevelio.arcade.managers.GameManager.GameSummary;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.WorldData;

public class WorldManager implements Listener
{
	private WorldCreatorManager wcm =  ArcadePlugin.getInstance().getWorldCreatorManager();
	private MainConfig mc =  ArcadePlugin.getInstance().getConfigManager().getMainConfig();
	private int currentCount = 0;
	
	public WorldManager()
	{
		
	}
	
	public World getWorld()
	{
		return this.getWorld(0);
	}
	
	public World getNextWorld() 
	{
		return this.getWorld(1);
	}
	
	private World getWorld(int idOffSet)
	{
		int gameId = ArcadePlugin.getInstance().getGameManager().getCurrentGameId();
		String worldName = "Game_" + (gameId + idOffSet);
		String worldFileName = ArcadePlugin.getInstance().getWorldCreatorManager().getFileWorld(worldName);
		return Bukkit.getWorld(worldFileName);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onStateChange(GameStateChangeEvent e)
	{
		int gameID = ArcadePlugin.getInstance().getGameManager().getCurrentGameId();
		if(e.getTo() == GameState.FINISHING || e.getFrom() == GameState.LOADING)
		{
			int nextGameID = gameID + 1;
			World world = null;
			
			
			if(mc.isRandomWorldSelection())
				world = wcm.createNewWorld(null, "Game_" + nextGameID);
			else
			{
				List<File> worlds = wcm.fetchWorlds();
				world = wcm.createNewWorld(worlds.get(this.currentCount % worlds.size()).getName(), "Game_" + nextGameID);
				this.currentCount++;
			}
			
			if(world == null)
			{
				e.setCancelled(true);
				return;
			}
				
			WorldData worldData = new WorldData(world.getName());
			
			worldData.world = world;
			try {
				worldData.load();
				worldData.loadConfig();
				GameSummary gameSummary = ArcadePlugin.getInstance().getGameManager().getGameSummary(worldData.gameType);
				ArcadePlugin.getInstance().getGameManager().setNextGame(gameSummary);
				ArcadePlugin.getInstance().getGameManager().getNextGame().setWorldData(worldData);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//TODO: Create next game
		}
		
		//TODO: This cause the world bug
		if(e.getTo() == GameState.PREGAME)
		{
			String worldName = ArcadePlugin.getInstance().getWorldCreatorManager().getFileWorld("Game_" + (gameID));
			ArcadePlugin.getInstance().getWorldCreatorManager().deleteWorld(worldName);
			ArcadePlugin.getInstance().getGameManager().nextGameId();
		}
	}
}
