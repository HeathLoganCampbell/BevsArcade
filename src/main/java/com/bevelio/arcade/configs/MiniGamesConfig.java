package com.bevelio.arcade.configs;

import java.io.File;

import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MiniGamesConfig extends BaseConfig
{
	private Game game;
	protected String displayName = "";
	protected String[] description = {};
	protected String simpleScoreboardTitle = "";
	protected String[] simpleScoreboard =
		{
			"%Blank%", 
			CC.aqua + "Alive",
			"%Alive_Players%",
			"%Blank%", 
			CC.red + "Dead",
			"%Dead_Players%",
			"%Blank%", 
			CC.gray + "------------------"
		  };
	
	
	public MiniGamesConfig(Game game) 
	{
		super(File.separator + "games" + File.separator + game.getGameTypeName());
		this.game = game;
		this.simpleScoreboardTitle = CC.bold + game.getGameTypeName();
		this.dontSave("game");
	}
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
        this.game.setDisplayName(displayName);
        this.game.setDescription(description);
	}
}
