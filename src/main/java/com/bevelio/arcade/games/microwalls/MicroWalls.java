package com.bevelio.arcade.games.microwalls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.bevelio.arcade.configs.TeamMiniGamesConfig;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.PostApplyKitEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.games.TeamGame;
import com.bevelio.arcade.games.microwalls.configs.MicroWallsConfig;
import com.bevelio.arcade.games.oitc.config.OITCConfig;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.misc.XYZ;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.Team;

public class MicroWalls extends TeamGame
{
	private List<Block> wallBlocks = new ArrayList<>();
	private long wallDropsSec = 10000;
	
	public MicroWalls()
	{
		super("MicroWalls", new String[]{"Small game, big strategy!"}, new ItemStackBuilder(Material.LAVA_BUCKET));
		this.setConfigs(new MicroWallsConfig(this));
		
		this.teamArmor = true;
		this.armorCanBeTakenOff = false;
		this.deathOut = true;
		
		this.dropItems = true;
	    this.pickUpItems = true;

	    this.breakBlocks = true;
	    this.placeBlocks = true;
	    
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		findWallBlocks();
		if(!(this.configs instanceof MicroWallsConfig)) return;
		MicroWallsConfig config = (MicroWallsConfig) this.getConfigs();
		wallDropsSec = (long) (1000 * config.getWallsDropInSeconds());
	}
	
	public void findWallBlocks()
	{
		List<XYZ> wallBlocks = this.getWorldData().customs.get("WallBlocks");
		if(wallBlocks == null) return;
		for(XYZ xyzLoc : wallBlocks)
		{
			Location location = xyzLoc.toLocation(this.getWorld());
			Block block = location.getBlock();
			if(block.getType() == Material.AIR) continue;
			this.wallBlocks.add(block);
		}
	}
	
	public void removeWallBlocks()
	{
		if(!this.wallBlocks.isEmpty())
		{
			for(Block block : this.wallBlocks)
				block.setType(Material.AIR);
			wallBlocks.clear();
		}
	}
	
	@EventHandler
	public void onClickCancel(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		if(!(this.configs instanceof MicroWallsConfig)) return;
		MicroWallsConfig config = (MicroWallsConfig) this.getConfigs();
		int slot = e.getSlot();
		
		if(config.isTeamHotbarSymbolEnabled())
			if(slot == config.getTeamHotbarSymbolSlot())
				e.setCancelled(true);
	}
	
	@EventHandler
	public void onPostKit(PostApplyKitEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		Team team = this.getPlayersTeam(uuid);
		if(team == null) return;
		ChatColor color = team.getPrefix();
		
		if(!(this.configs instanceof MicroWallsConfig)) return;
		MicroWallsConfig config = (MicroWallsConfig) this.getConfigs();
		if(!config.isTeamHotbarSymbolEnabled()) return;
		
		ItemStackBuilder itemBuilder = config.getTeamHotbarSymbols().get(color);
		
		if(itemBuilder == null) return;
		itemBuilder.displayName(team.getDisplayName(true));
		
		player.getInventory().setItem(config.getTeamHotbarSymbolSlot(), itemBuilder.build());
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		if(this.wallBlocks.contains(block))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void ArrowDecrease(CustomDamageEvent e)
	{
		if (e.getProjectile() == null) return;
	    e.addMod(this.getDisplayName(), "Projectile Reduce", -2.0D, false);
	    e.addKnockback("Increase", 1.6D);
	  }

	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() == UpdateType.TICK && this.isLive())
		{
			this.getWorld().setTime(this.getWorld().getTime() + 1L);
			for(Player player : this.getAlivePlayers())
				if (player.getFoodLevel() < 2)
					player.setFoodLevel(2);
		}
		
		if(e.getType() != UpdateType.SECOND) return;
		if(System.currentTimeMillis() - this.getStartTimeStamp() > wallDropsSec) //Remove walls after 10 seconds
			this.removeWallBlocks();
	}
}
