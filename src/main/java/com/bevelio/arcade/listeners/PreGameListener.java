package com.bevelio.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.LobbyConfig;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.utils.ActionBarUtils;
import com.bevelio.arcade.utils.MathUtils;

public class PreGameListener implements Listener
{
	private GameManager gameManager = ArcadePlugin.getInstance().getGameManager();
	private TranslationConfig tc = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UpdateType.TICK) return;
		if(gameManager.getGameState() != GameState.PREGAME) return;
		Game game = gameManager.getGame();
		if(game == null) return;
		if(game.getPreGameSeconds() <= 0) return;
		long timestamp = game.getPreGameEndTimeStamp();
		long timeStampDiff = timestamp - System.currentTimeMillis();
		double secondsRemaining = timeStampDiff / 1000.0;
		secondsRemaining += 0.2;//margin for error;
		double value = game.getPreGameSeconds() - secondsRemaining;
		double percentage = value / game.getPreGameSeconds();
		if(percentage < 0 || value < 0.000 || secondsRemaining < 0.00)
			return;
		
		String textSeconds = String.format("%.1f", secondsRemaining);
		
		
		String percentBar = tc.getPregameActionBarPrefix() + MathUtils.getPercentageBar(tc.getPregameActionBarPercentageBarFilled(), tc.getPregameActionBarPercentageBarUnfilled(), percentage, 12) + tc.getPregameActionBarSuffix();
		percentBar = percentBar.replace("%Seconds%", textSeconds  + "")
							   .replaceAll("%SOrNot%", secondsRemaining <= 1 ? " " : "s");
		
		for(Player player : game.getWorld().getPlayers())
			ActionBarUtils.sendActionBar(player, percentBar);
	}
}
