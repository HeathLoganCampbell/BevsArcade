package com.bevelio.arcade.games.oitc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.bevelio.arcade.configs.SoloMiniGamesConfig;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.oitc.config.OITCConfig;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.utils.MathUtils;

public class OITC extends SoloGame
{
	private ArrayList<OITCScore> scores = new ArrayList<>();
	private Objective underHeadOnjective;
	private int scoreToWin = 20;
	
	public OITC() 
	{
		super("OITC", new String[] 
						{
							"Arrows can insta kill",
							"Every kill you get an arrow",
							"Get 20 kills"
						},
			new ItemStackBuilder(Material.BOW));
		this.setConfigs(new OITCConfig(this));
		this.deathOut = false;
		this.quitOut = true;
		this.automaticRespawn = true;
		this.deathSpecatatorSeconds = 1.2;
		this.hungerSet = 20;
		this.breakBlocks = false;
		this.placeBlocks = false;
	}
	
	@Override
	public void checkEnd()
	{
		boolean finished = false;
		this.SortScores();
		
		if(this.scores.size() != 0)
			if(this.scores.get(0).score >= 20)
				finished = true;
//			for(OITCScore score : scores)
//			{
//				if(score.score >= this.scoreToWin)
//				{
//					finished = true;
//					
//				}
//			}
		
		if(finished)
		{
			ArrayList<Player> winners = new ArrayList<>();
			for(int i = 0; i < this.scores.size(); i++)
			{
				OITCScore score = this.scores.get(i);
				Player player = Bukkit.getPlayer(score.uuid);
				if(player != null)
					winners.add(player);
			}
			this.onFinish(this.getWinners());
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		HashMap<UUID, PlayState> players = this.getPlayers();
		if(!players.isEmpty())
			players.forEach((uuid, playState) -> {
				if(Bukkit.getPlayer(uuid) != null)
					this.underHeadOnjective.getScore(Bukkit.getPlayer(uuid).getName()).setScore(0);
			});
	}
	
	@Override
	public void onPreStart() 
	{
		super.onPreStart();
		
		
		this.underHeadOnjective = this.scoreboard.getScoreboard().registerNewObjective("underhead-" + MathUtils.random(99999), "dummy");
		this.underHeadOnjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		this.underHeadOnjective.setDisplayName(" Kills");
	}
	
	public int getKills(UUID uuid)
	{
		for(OITCScore score : this.scores)
		{
			if(score.uuid == uuid)
			{
				return score.score;
			}
		}
		return 0;
	}
	
	public void addKill(UUID uuid)
	{
		boolean found = false;
		for(OITCScore score : this.scores)
		{
			if(score.uuid == uuid)
			{
				score.score++;
				this.underHeadOnjective.getScore(Bukkit.getPlayer(uuid).getName()).setScore(score.score);
				found = true;
			}
		}
		
		if(!found)
		{
			OITCScore oitcScore = new OITCScore(Bukkit.getPlayer(uuid));
			oitcScore.score++;
			this.scores.add(oitcScore);
			this.underHeadOnjective.getScore(Bukkit.getPlayer(uuid).getName()).setScore(1);
		}
	}
	
	public void onKill(Player killer)
	{
		killer.getInventory().addItem(new ItemStackBuilder(Material.ARROW).build());
//		killer.playSound(killer.getLocation(), Sound.PISTON_EXTEND, 3.0F, 2.0F);
		addKill(killer.getUniqueId());
		SortScores();
	}
	
	private void SortScores()
	{
		for (int i = 0; i < this.scores.size(); i++)
	    {
	      for (int j = this.scores.size() - 1; j > 0; j--)
	      {
	        if (((OITCScore)this.scores.get(j)).score > ((OITCScore)this.scores.get(j - 1)).score)
	        {
	        	OITCScore temp = (OITCScore)this.scores.get(j);
	        	this.scores.set(j, (OITCScore)this.scores.get(j - 1));
	        	this.scores.set(j - 1, temp);
	        }
	      }
	    }
	  }
	
	@Override
	protected void setLine(String line, int slot)
	{
		for(int i = 0; i < 17; i++)
		{
			if(this.scores.size() <= i) 
				continue;
			OITCScore score = this.scores.get(i);
			line = line.replaceAll("%Score_" + (i + 1) + "%", score.score + "")
					   .replaceAll("%Place_" + (i + 1) + "%", score.name + "");
		}
		
		if(line.contains("Score_") || line.contains("%Place_"))
			return;
		super.setLine(line, slot);
	}
	
//	@Override
//	public void updateScoreboard() 
//	{
//		if(!(this.configs instanceof OITCConfig)) return;
//		OITCConfig config = (OITCConfig) this.configs;
//		
//		this.scoreboard.setTitle(config.getSimpleScoreboardTitle());
//		for(int i = 0; i < config.getSimpleScoreboard().length; i++)
//			this.setLine(config.getSimpleScoreboard()[i], config.getSimpleScoreboard().length - i);
//	}
	
	@EventHandler
	public void onArrowHit(CustomDamageEvent e)
	{
		if(e.getProjectile() == null) return;
		if(!(e.getProjectile() instanceof Arrow)) return;
		if(e.getDamagerPlayer() == null) return;
		
		e.addMod("Arrow Damage", "Insta Kill", 999, false);
		e.getProjectile().remove();
	}
	
	@EventHandler
	public void onDeath(PlayerPlayStateEvent e)
	{
		Player player = e.getPlayer();
		if(e.getFrom() != PlayState.IN) return;
		if(e.getTo() != PlayState.OUT) return;
		if(!this.isInQueue(player)) return;
		if(!this.isLive()) return;
		
		Player killer = player.getKiller();
		if(killer != null)
			this.onKill(killer);
		
		this.checkEnd();
	}
	
	public class OITCScore implements Comparable<OITCScore>
	{
		public UUID uuid;
		public String name;
		public int score;
		
		public OITCScore(Player player)
		{
			this.uuid = player.getUniqueId();
			this.name = player.getName();
			this.score = 0;
		}

		@Override
		public int compareTo(OITCScore o) 
		{
			return Integer.compare(o.score, this.score);
		}
	}
}
