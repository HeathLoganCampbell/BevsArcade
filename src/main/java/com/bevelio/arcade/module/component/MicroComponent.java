package com.bevelio.arcade.module.component;

import org.bukkit.event.Listener;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;

import lombok.Getter;

public class MicroComponent implements Listener
{
	private @Getter String name;
	private @Getter String description;
	private @Getter String[] instructions;
	private @Getter Game game;
	
	public MicroComponent(String name, String description, String[] instructions, Game game) 
	{
		this.name = name;
		this.description = description;
		this.instructions = instructions;
		this.game = game;
	}
	
	public GameManager getGameManager()
	{
		return ArcadePlugin.getInstance().getGameManager();
	}
}
