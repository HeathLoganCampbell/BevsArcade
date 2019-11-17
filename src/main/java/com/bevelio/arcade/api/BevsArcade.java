package com.bevelio.arcade.api;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;

public class BevsArcade
{
	public static GameManager getGameManager()
	{
		return ArcadePlugin.getInstance().getGameManager();
	}
	
	public static void registerGame(Class<? extends Game> gameClazz)
	{
		BevsArcade.getGameManager().registerGame(gameClazz);
	}
}
