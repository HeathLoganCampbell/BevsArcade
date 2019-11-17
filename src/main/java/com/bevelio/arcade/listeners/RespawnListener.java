package com.bevelio.arcade.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.utils.ActionBarUtils;
import com.bevelio.arcade.utils.MathUtils;

public class RespawnListener implements Listener
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
		
		for(Player player : game.getWorld().getPlayers())
		{
			if(!gameManager.isInteractivePlayer(player)) continue;
			if(!game.getRespawnTimestamp().containsKey(player.getUniqueId())) continue;
			
			long timeStampDiff = game.getRespawnTimestamp().get(player) - System.currentTimeMillis();
			double secondsRemaining = timeStampDiff / 1000.0;
			double value = game.getPreGameSeconds() - secondsRemaining;
			double percentage = value / game.getPreGameSeconds();
			if(percentage < 0)
				return;
			
			String percentBar = tc.getPregameActionBarPrefix() + MathUtils.getPercentageBar(tc.getPregameActionBarPercentageBarFilled(), tc.getPregameActionBarPercentageBarUnfilled(), percentage, 12) + tc.getPregameActionBarSuffix();
			percentBar = percentBar.replaceAll("%Seconds%", secondsRemaining  + "")
								   .replaceAll("%SOrNot%", secondsRemaining <= 1 ? "" : "s");
			ActionBarUtils.sendActionBar(player, percentBar);
		}
	}
}
