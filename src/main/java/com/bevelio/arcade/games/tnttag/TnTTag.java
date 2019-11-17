package com.bevelio.arcade.games.tnttag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;

public class TnTTag extends SoloGame
{
	private List<UUID> itPlayers = new ArrayList<>();
	private int secondsTillBoom = 20;
	private int secondsTillNextRound = 10;
	
	private int currentSecsTillBoom = 0;
	
	public TnTTag() 
	{
		super("TntTag", new String[] 
						{
							"Tnt explose when the timer hits zero", 
							"Hit a person to put the tnt on their head", 
							"Last man standing wins"
						},
			new ItemStackBuilder(Material.EGG));
		this.deathOut = true;
		this.quitOut = true;
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
			this.getWinners().add(0, this.getAlivePlayers().get(0));

		if(this.getAlivePlayers().size() <= 1)
			this.onFinish(this.getWinners());
	}
	
	public void tag(Player itPlayer, Player victim)
	{
		if(!this.itPlayers.contains(itPlayer.getUniqueId()))
			return;
		if(this.itPlayers.contains(victim.getUniqueId()))
			return;
		
		itPlayers.remove(itPlayer.getUniqueId());
		this.clearTntHead(itPlayer);
		
		this.makeIt(victim);
	}
	
	public void makeIt(Player player)
	{
		itPlayers.add(player.getUniqueId());
		this.setTntHead(player);
		player.sendMessage("You are now it");
	}
	
	public void setTntHead(Player player)
	{
		player.getInventory().setHelmet(new ItemStack(Material.TNT));
	}
	
	
	public void clearTntHead(Player player)
	{
		player.getInventory().setHelmet(null);
	}
	
	public int getNumberOfItPlayers(int alivePlayers)
	{
		return alivePlayers > 5 ? (alivePlayers / 4) : (alivePlayers / 2);
	}
	
	public void newRound()
	{
		List<Player> alivePlayers = this.getAlivePlayers();
		int playerSize = alivePlayers.size();
		int itPlayerCount = this.getNumberOfItPlayers(playerSize);
		for(Player player : this.getAlivePlayers())
		{
			if(itPlayerCount > 0)
			{
				this.makeIt(player);
				itPlayerCount--;
			}
		}
	}
	
	public void killAllItPlayers()
	{
		for(UUID uuid : this.itPlayers)
		{
			Player player = Bukkit.getPlayer(uuid);
			player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 0);
			player.damage(999);
		}
	}
	
	
	@EventHandler
	public void onHitByIt(PlayerInteractAtEntityEvent e)
	{
		Player player = e.getPlayer();
		Entity clickedEntity = e.getRightClicked();
		if(!(clickedEntity instanceof Player)) return;
		Player victum = (Player) clickedEntity;
		
		this.tag(player, victum);
	}
	
	@EventHandler
	public void onDamageCancel(CustomDamageEvent e)
	{
		if(e.getPlayer() == null)
			e.setCancelled("Not Player");
		if(e.getProjectile() == null)
			e.setCancelled("No Projectile");
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		this.getWinners().add(0, this.getAlivePlayers().get(0));
		this.checkEnd();
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UpdateType.SECOND) return;
		if(this.currentSecsTillBoom == 0)
		{
			this.killAllItPlayers();
		}
		
		if(-currentSecsTillBoom == secondsTillNextRound)
		{
			this.currentSecsTillBoom = this.secondsTillBoom;
		}
		this.currentSecsTillBoom--;
	}
}
