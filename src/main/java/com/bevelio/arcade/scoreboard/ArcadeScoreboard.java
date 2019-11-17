package com.bevelio.arcade.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.bevelio.arcade.utils.MathUtils;

import lombok.Getter;

public class ArcadeScoreboard 
{
	private final static String hiddenChars = "1234567890abcdefghijklmnopqrstuvwxyz";
	private final static int maxTeamSize = 16;
	private static String[] teamNames = new String[maxTeamSize];
	
	static
	{
		for (int i = 0; i < maxTeamSize; i++)
		{
		      String teamName = ChatColor.COLOR_CHAR + "" + hiddenChars.toCharArray()[i] + ChatColor.RESET;
		      teamNames[i] = teamName;
		}
	}
	
	private @Getter String title;
	private @Getter Scoreboard scoreboard;
	private @Getter Objective objective;
	
	public ArcadeScoreboard(String title)
	{
		this.title = title;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = this.scoreboard.registerNewObjective(title + MathUtils.random(9999), "dummy");
		this.objective.setDisplayName(title);
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void setTitle(String title)
	{
		this.title = title;
		this.objective.setDisplayName(title);
	}
	
	public void setLine(String name, int slot)
	{
		String teamName = teamNames[slot];
		Team team = this.scoreboard.getTeam(teamName);
		if(team == null) return;
		
		if(name.contains("%Blank%"))
			name = " ";
		
		this.objective.getScore(team.getName()).setScore(slot);
		team.setPrefix(name.substring(0, Math.min(name.length(), 16)));
        team.setSuffix(ChatColor.getLastColors(name) + name.substring(team.getPrefix().length(), Math.min(name.length(), 32)));
	}
	
	public void clear()
	{
		for(String entry : this.scoreboard.getEntries())
		{
			String name = entry + "";
			this.scoreboard.resetScores(name);
			Team team = this.scoreboard.getTeam(name);
			if(team != null)
			{
				team.setPrefix("");
				team.setSuffix("");
			}
		}
	}
	
	public void removeLine(int slot)
	{
		String teamName = hiddenChars.toCharArray()[slot] + "";
		this.scoreboard.resetScores(teamName);
	}
	
	public void setUp()
	{
		for (int i = 0; i < 16; i++)
	    {
			String teamName = teamNames[i];
			Team team = this.scoreboard.registerNewTeam(teamName);
			team.addEntry(teamName);
//			System.out.println("new team created for scoreboard " + teamName);
	    }
	}
	
	public void send(Player player)
	{
		player.setScoreboard(getScoreboard());
	}
}
