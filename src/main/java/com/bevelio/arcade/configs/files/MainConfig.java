package com.bevelio.arcade.configs.files;

import com.bevelio.arcade.configs.BaseConfig;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MainConfig extends BaseConfig
{
	private int minNumberOfPlayersToStart = 2;
	private int maxNumberOfPlayers = 100;
	private int GameCountTillRestart = 30;//30 games untill the server restarts
	private boolean ScoreboardEnabled = true;
	private boolean forceJoinGameOnJoin = true;
	private boolean randomWorldSelection = false;
	private boolean fireworksOnWin = true;
	private double hitDelaySeconds = 0.4;
	
	private boolean enableMOTD = false;
	private String motdMessageStarting = CC.bWhite + "%Seconds% till %Game_Map% (%GameType%) ";
	private String motdMessageLive = CC.bRed + "Currently playing %Game_Map% (%GameType%)";
	private String motdMessageUnknown = CC.bdAqua + "Waiting for more players!";
	
	@Override
	public void loadConfig()
	{
		super.loadConfig();
	}
	
	public MainConfig()
	{
		super("config");
	}
}
