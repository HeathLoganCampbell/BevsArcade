package com.bevelio.arcade.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import com.bevelio.arcade.configs.MiniGamesConfig;
import com.bevelio.arcade.configs.SoloMiniGamesConfig;
import com.bevelio.arcade.configs.TeamMiniGamesConfig;
import com.bevelio.arcade.games.microwalls.configs.MicroWallsConfig;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.scoreboard.ArcadeScoreboard;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.types.Team;

public class TeamGame extends Game
{
	protected List<Team> places = new ArrayList<Team>();
	protected long rejoinTime = 120000;// 2 Minutes
	private int teamsAliveLast = 100;

	public TeamGame(String gameTypeName, String[] description, ItemStackBuilder icon)
	{
		super(gameTypeName, description, icon);
		this.setConfigs(new TeamMiniGamesConfig(this));
		
		this.teamArmor = true;
		this.damageSelf = false;
		this.damageOwnTeam = false;
		this.damageOtherTeam = true;
	}
	
	public int getAlivePlayersInTeam(String teamName)
	{
		return (int) this.getTeam(teamName).getMembers().stream()
												  .map(Bukkit::getPlayer)
												  .filter(player -> player != null)
												  .filter(player -> this.getPlayState(player) == PlayState.IN)
												  .count();
	}
	
	protected void setLine(String line, int slot)
	{
		line = line.replaceAll("%Alive_Players%", this.getAlivePlayers().size() + "")
				   .replaceAll("%Dead_Players%", (this.getPlayers().size() - this.getAlivePlayers().size()) + "");
		for(Entry<String, Team> teamSet : this.getTeams().entrySet())
		{
			teamSet.getValue().getScoreboard().setLine(line, slot);
		}
	}
	
	//Team name
	// 8 Alive
	
	@Override
	public void updateScoreboard() 
	{
		TeamMiniGamesConfig config = (TeamMiniGamesConfig) this.configs;
		
		String[] scoreboardLines = config.getSimpleScoreboard();
		
		int slot = 0;
		for(int i = scoreboardLines.length - 1; i >= 0; i--)
		{
			
			String line = scoreboardLines[i];
			if(line.contains("%Scoreboard_Team_Section%"))
			{
				String[] teamLines = config.getScoreboardTeamSection();
				
				for(Entry<String, Team> teamSet : this.getTeams().entrySet())
				{
					Team team = teamSet.getValue();
					int alivePlayers = this.getAlivePlayers(team).size();
					
					team.getScoreboard().setTitle(config.getSimpleScoreboardTitle());
					int slotsForTeam = teamLines.length;
					for(int it = 0; it < teamLines.length; it++)
					{
						line = teamLines[it];
						line = line.replaceAll("%Team_Color%", team.getPrefix() +"")
								   .replaceAll("%Team_Name%", team.getDisplayName())
								   .replaceAll("%Alive_Players%", alivePlayers + "")
								   .replaceAll("%Blank%", "");
						
						if(alivePlayers != 0)
						{
							this.setLine(line, slot + slotsForTeam);
							slotsForTeam-= 2;
							slot++;
						}
					}
				}
				
				continue;
			}
			
			this.setLine(line, slot);
			slot++;
		}
//		
////		for(int i = 0; i < config.getSimpleScoreboard().length; i++)
////		this.setLine(config.getSimpleScoreboard()[i], config.getSimpleScoreboard().length - i);
		
//		for(Entry<String, Team> teamSet : this.getTeams().entrySet())
//		{
//			
//		}
		
//		ArcadeScoreboard scoreboard = teamSet.getValue().getScoreboard();
//		scoreboard.setTitle(config.getSimpleScoreboardTitle());
//		for(int i = 0; i < config.getSimpleScoreboard().length; i++)
//			this.setLine(config.getSimpleScoreboard()[i], config.getSimpleScoreboard().length - i);
//		this.setLine(config.getSimpleScoreboard()[i], config.getSimpleScoreboard().length - i);
	}
	
	@Override
	public void checkEnd()
	{
		ArrayList<Team> teamsAlive = new ArrayList<Team>();
		
		for (Entry<String, Team> teamSet : this.getTeams().entrySet()) 
		{
			Team team = teamSet.getValue();
			if (this.getAlivePlayersInTeam(team.getName()) > 0)
				teamsAlive.add(team);
		}
		
		if(this.teamsAliveLast != teamsAlive.size())
			for(Entry<String, Team> teamSet : this.getTeams().entrySet())
				teamSet.getValue().getScoreboard().clear();
		
		this.teamsAliveLast = teamsAlive.size();
		
		if(teamsAlive.size() <= 1)
		{
			if (teamsAlive.size() > 0)
				this.onFinish(teamsAlive.get(0));
			else
				this.getGameManager().setGameState(GameState.FINISHING);
		}
	}

	
}
