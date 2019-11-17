package com.bevelio.arcade.configs;

import com.bevelio.arcade.games.TeamGame;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TeamMiniGamesConfig extends MiniGamesConfig
{
	
	private String[] scoreboardTeamSection =
		{
			"%Team_Color%%Team_Name% Team",
			"%Alive_Players% %Team_Color%Alive",
			"%Blank%",
		};
	
	public TeamMiniGamesConfig(TeamGame teamGame)
	{
		super(teamGame);
		this.simpleScoreboard =  new String[] 
								{
										"%Scoreboard_Team_Section%",
										"------------------"
								};
	}
	
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
	}
}