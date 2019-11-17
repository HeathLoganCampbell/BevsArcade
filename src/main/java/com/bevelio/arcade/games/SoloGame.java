package com.bevelio.arcade.games;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bevelio.arcade.configs.MiniGamesConfig;
import com.bevelio.arcade.configs.SoloMiniGamesConfig;
import com.bevelio.arcade.games.oitc.config.OITCConfig;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.scoreboard.ArcadeScoreboard;
import com.bevelio.arcade.types.Team;

import lombok.Getter;

public class SoloGame extends Game
{
	protected ArcadeScoreboard scoreboard;
	protected Team team;
	
	public SoloGame(String gameTypeName, String[] description, ItemStackBuilder icon) 
	{
		super(gameTypeName, description, icon);
//		this.setConfigs(new SoloMiniGamesConfig(this));
		this.soloTeamMode = true;
		this.damageOwnTeam = true;
		this.damageOtherTeam = false;
		
	}

	protected void setLine(String line, int slot)
	{
		line = line.replaceAll("%Alive_Players%", this.getAlivePlayers().size() + "")
				   .replaceAll("%Dead_Players%", (this.getPlayers().size() - this.getAlivePlayers().size()) + "");
		this.scoreboard.setLine(line, slot);
	}
	
	public Team getTeam()
	{
		return this.team;
	}
	
	@Override
	public void updateScoreboard() 
	{
//		if(!(this.configs instanceof SoloMiniGamesConfig)) return;
		MiniGamesConfig config = (MiniGamesConfig) this.configs;
		
		for(Entry<String, Team> teamSet : this.getTeams().entrySet())
		{
			ArcadeScoreboard scoreboard = teamSet.getValue().getScoreboard();
			scoreboard.setTitle(config.getSimpleScoreboardTitle());
			for(int i = 0; i < config.getSimpleScoreboard().length; i++)
				this.setLine(config.getSimpleScoreboard()[i], config.getSimpleScoreboard().length - i);
		}
	}
	
	@Override
	public void onPreStart() 
	{
		super.onPreStart();
		
		this.team = this.getTeams().entrySet().stream().map(set -> set.getValue()).findFirst().get();
		this.scoreboard = this.team.getScoreboard();
	}
	
	@Override
	public void onStart() 
	{
		super.onStart();
	}
	
	@Override
	public void checkEnd() 
	{
		if(this.getAlivePlayers().size() == 1)
			if(!this.getWinners().contains(this.getAlivePlayers().get(0)))
				this.getWinners().add(0, this.getAlivePlayers().get(0));
			
		if(this.getAlivePlayers().size() <= 1)
			this.onFinish(this.getWinners());
	}

}
