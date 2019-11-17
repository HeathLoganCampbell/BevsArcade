package com.bevelio.arcade.games.murdermystery;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.NameTagVisibility;

import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.murdermystery.config.MurderMysteryConfig;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.types.PlayState;

//Murder kills
//Players hide

public class MurderMystery extends SoloGame
{
	private HashMap<UUID, MysteryType> playersMysteryType = new HashMap<>();
	
	public MurderMystery() 
	{
		super("MurderMystery", new String[] {"One player is a murder", "Kill them before they kill you", "10 gold gives you a bow and arrow"}, new ItemStackBuilder(Material.IRON_SWORD));
		this.setConfigs(new MurderMysteryConfig(this));
		
		this.deathOut = true;
		this.quitOut = true;
		this.hungerSet = 20;
		this.pregameFreeze = false;
		this.preGameSeconds = 10;
		this.pregameActionbarCountDown = false;
		
		this.dropItems = false;
		this.breakBlocks = false;
		this.placeBlocks = false;
	}
	
	public void selectRandomMysteryType(List<Player> list, MysteryType mysteryType)
	{
		Player player = null;
		for(int i = 0; i < 5; i++)
		{
			if(list.size() <= i)
				break;
			if(list.get(i).isOnline())
				if(this.playersMysteryType.get(list.get(i).getUniqueId()) == null)
					player = list.get(i);
		}
		
		if(player != null)
			playersMysteryType.put(player.getUniqueId(), mysteryType);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPreStart()
	{
		super.onPreStart();
		
		List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> this.getGameManager().isInteractivePlayer(player)).collect(Collectors.toList());
		Collections.shuffle(players);
		
		this.selectRandomMysteryType(players, MysteryType.MURDER);
		this.selectRandomMysteryType(players, MysteryType.DETECTIVE);
		for(Player player : players)
		{
			if(this.playersMysteryType.get(player.getUniqueId()) == null)
			{
				playersMysteryType.put(player.getUniqueId(), MysteryType.INNOCENT);
			}
			player.sendMessage("You are a " + playersMysteryType.get(player.getUniqueId()).name().toLowerCase());
		}
		
		this.getTeam().getBukkitTeam().setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld() == this.getWorld()).filter(player -> this.getGameManager().isInteractivePlayer(player)).collect(Collectors.toList());
		for(Player player : players)
		{
//			if(this.playersMysteryType.get(player.getUniqueId()))
//			{
//				playersMysteryType.put(player.getUniqueId(), MysteryType.INNOCENT);
//			}
			player.sendMessage("You are " + playersMysteryType.get(player.getUniqueId()));
			MysteryType type = this.playersMysteryType.get(player.getUniqueId());
			switch(type)
			{
			case DETECTIVE:
				player.getInventory().addItem(new ItemStackBuilder(Material.IRON_SWORD).displayName(CC.green + "Bow").build());
				player.getInventory().setItem(9, new ItemStackBuilder(Material.ARROW).build());
				player.getInventory().setHeldItemSlot(1);
				break;
			case MURDER:
				player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
				player.getInventory().setHeldItemSlot(1);
				break;
			case INNOCENT:
			default:
				break;
			}
		}
	}

	@EventHandler
	public void onArrowHit(CustomDamageEvent e)
	{
		if(e.getPlayer() == null) return;
		if(e.getDamagerPlayer() == null) return;
		
		if(e.getDamagerPlayer().getItemInHand() == null)
		{
			e.setCancelled("Only weapons can do damage.");
		}
		
		if(e.getProjectile() != null) 
		{
			if(e.getProjectile() instanceof Arrow)
			{
				if(this.playersMysteryType.get(e.getPlayer().getUniqueId()) != null)
					if(this.playersMysteryType.get(e.getPlayer().getUniqueId()) == MysteryType.INNOCENT)
					{
						this.setPlayState(e.getDamagerPlayer(), PlayState.OUT);
					}
				e.addMod("Arrow Damage", "Insta Kill", 999, false);
				e.getProjectile().remove();
			}
		}
		
		if(e.getDamagerPlayer().getItemInHand() != null)
		{
			if(e.getDamagerPlayer().getItemInHand().getType().name().contains("SWORD"))
			{
				e.addMod("Sword Damage", "Insta Kill", 999, false);
			}
		}
		
	}

	@EventHandler
	public void onDeath(PlayerPlayStateEvent e)
	{
		Player player = e.getPlayer();
		if(e.getFrom() != PlayState.IN) return;
		if(e.getTo() != PlayState.OUT) return;
		if(!this.isInQueue(player)) return;
		if(!this.isLive()) return;
		
//		Player killer = player.getKiller();
//		if(killer != null)
//			this.onKill(killer);
		
		this.checkEnd();
	}
	
	@Override
	public void checkEnd() 
	{
		int murders = 0;
		int innocents = 0;
		for(Entry<UUID, MysteryType> mysteryEntry : this.playersMysteryType.entrySet())
		{
			Player player = Bukkit.getPlayer(mysteryEntry.getKey());
			if(player == null) continue;
			if(!player.isOnline()) continue;
			MysteryType mysteryType = mysteryEntry.getValue();
			if(mysteryType == MysteryType.MURDER)
				murders++;
			else
				innocents++;
		}
		
		if(murders == 0)
		{
			//INOCENTS win
		}
		
		if(innocents == 0)
		{
			//Murders win
		}
	}
	
	@Override
	public void onEndAnnouncement()
	{
		String[] lines = null;
//		if(this.soloTeamMode == true)
//			lines = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getEndSoloAnnouncementMessages();
//		else 
//			lines = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getEndTeamAnnouncementMessages();
//		
//		for(String line : lines)
//		{
//			line = line.replaceAll("%WorldName%", this.getWorldData().name)
//					   .replaceAll("%GameType%", this.getWorldData().gameType)
//					   .replaceAll("%Authors%", this.getWorldData().getAuthors())
//					   ;
//			
//			if(this.soloTeamMode)
//			{
//				int announceNumOfwinners = lines.length;
//				if(this.winners.size() > announceNumOfwinners) announceNumOfwinners = this.winners.size();
//				if(5 > announceNumOfwinners) announceNumOfwinners = 5;
//				
//				for(int i = 0; i < announceNumOfwinners; i++)
//				{
//					line = line.replaceAll("%Winner_Player_Place_" + (i + 1) + "%",  this.winners.size() > i ? this.winners.get(i).getName() : "No one");
//				}
//			}
//			
//			if(this.winnerTeams != null)
//			{
//				line = line.replaceAll("%Winner_Team%", this.winnerTeams.getDisplayName(false));
//			} else {
//				line = line.replaceAll("%Winner_Team%", "No one");
//			}
//			
//			if(line.contains("%Description%"))
//			{
//				for(String desc : this.getDescription())
//				{
//					line = line.replaceAll("%Description%", desc);
//					this.broadcast(line);
//				}
//				continue;
//			}
//			
//			this.broadcast(line);
//		}
	}
}
