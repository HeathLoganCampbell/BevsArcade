package com.bevelio.arcade.games.goldrush;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;

import com.bevelio.arcade.games.TeamGame;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.types.Team;

public class GoldRush extends TeamGame
{
	private int goldToWin = 20;
	private HashMap<String, Integer> teamsGold = new HashMap<>();
	
	public GoldRush()
	{
		super("GoldRush", new String[] {"Bring 20 gold back to your base to win"}, new ItemStackBuilder(Material.GOLD_NUGGET));
	}
	
	public int getGold(Team team)
	{
		if(this.teamsGold.containsKey(team.getName()))
			return this.teamsGold.get(team.getName());
		return 0;
	}
	
	public void increaseGold(Team team)
	{
		int gold = 0;
		if(this.teamsGold.containsKey(team.getName()))
			gold = this.teamsGold.get(team.getName());
		gold++;
		this.teamsGold.put(team.getName(), gold);
	}
	
	@Override
	public void checkEnd() 
	{
		for(Entry<String, Integer> entryTeamGold : teamsGold.entrySet())
		{
			if(entryTeamGold.getValue() >= this.goldToWin)
			{
				Team team = this.getTeam(entryTeamGold.getKey());
				if(team == null) continue;
				this.onFinish(team);
			}
		}
	}
}
