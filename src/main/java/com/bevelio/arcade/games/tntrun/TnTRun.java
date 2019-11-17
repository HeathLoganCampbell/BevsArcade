package com.bevelio.arcade.games.tntrun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.oitc.config.OITCConfig;
import com.bevelio.arcade.games.tntrun.config.TnTRunConfig;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.PlayState;

public class TnTRun extends SoloGame
{
	private HashMap<Block, Long> blocksToFall = new HashMap<>();
	private long timeForBlockToFall = 100; //2 seconds
	
	//BlockDropDelay
	//Block
	
	public TnTRun() 
	{
		super("TnTRun", new String[] 
						{
							"Blocks disappear when you run on them",
							"Keep moving and don't fall",
							"Last man standing wins"
						},
			new ItemStackBuilder(Material.TNT));
		this.setConfigs(new TnTRunConfig(this));
		this.deathOut = true;
		this.quitOut = true;
		
		this.hungerSet = 20;
		
		this.pregameFreeze = false;
		
		if(this.getConfigs() instanceof TnTRunConfig)
		{
			TnTRunConfig config = (TnTRunConfig) this.getConfigs();
			if(config != null) 
			{
				this.timeForBlockToFall = (long) config.getDestroyBlockAfterWalking() * 1000;
			}
		}
	}
	
	public void addBlock(Block block)
	{
		if(block == null) return;
		if(this.getWorld() != block.getWorld()) return;
		if(block.getType() == Material.AIR) return;
		if(blocksToFall.containsKey(block)) return;
		
		blocksToFall.put(block, System.currentTimeMillis() + timeForBlockToFall);
	}
	
	@EventHandler
	public void onDamageCancel(CustomDamageEvent e)
	{
		if(e.getCause() == DamageCause.VOID)
			return;
		e.setCancelled("Random damage");
	}
	
	@EventHandler
	public void onDeath(PlayerPlayStateEvent e)
	{
		Player player = e.getPlayer();
		if(e.getFrom() != PlayState.IN) return;
		if(e.getTo() != PlayState.OUT) return;
		if(!this.isInQueue(player)) return;
		if(!this.isLive()) return;
		if(player.getLocation().getY() < 2)
			player.teleport(this.getWorldData().spectatorSpawn.toLocation(player.getWorld()));
		this.getWinners().add(0, player);
		
		if(this.getConfigs() instanceof TnTRunConfig)
		{
			TnTRunConfig config = (TnTRunConfig) this.getConfigs();
			String msg = config.getDeathMessage();
			if(msg != null)
			{
				msg = msg.replace("%Player%", player.getName())
						 .replace("%Alive_Players%", (this.getAlivePlayers().size() - 1) + "");
				this.getGameManager().broadcast(msg);
			}
		}
		
		this.checkEnd();
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UpdateType.TICK) return;
		if(!this.isLive()) return;
		for(Player player : this.getAlivePlayers())
		{
			Location location = player.getLocation();
			double xMod = location.getX() % 1;
			if (player.getLocation().getX() < 0.0D) 
				xMod += 1;
			double zMod = location.getZ() % 1;
			if (player.getLocation().getZ() < 0.0D) 
				zMod += 1;
			
			
			int xMin = 0;
		    int xMax = 0;
		    int zMin = 0;
		    int zMax = 0;
		    
		    if (xMod < 0.3D) xMin = -1;
		    if (xMod > 0.7D) xMax = 1;

		    if (zMod < 0.3D) zMin = -1;
		    if (zMod > 0.7D) zMax = 1;
		    
		    addBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN));
		    
		    for(int z = zMin; z <= zMax; z++)
		    	for(int x = xMin; x <= xMax; x++)
		    		 addBlock(player.getLocation().add(x, -0.5D, z).getBlock());
		}
		
		for(Entry<Block, Long> blocksToFallSet : this.blocksToFall.entrySet())
		{
			if(blocksToFallSet.getValue() < System.currentTimeMillis())
			{
				Block finalBlock = blocksToFallSet.getKey();
				blocksToFallSet.getKey().setType(Material.AIR);
				blocksToFallSet.getKey().getRelative(BlockFace.DOWN).setType(Material.AIR);
				Bukkit.getScheduler().scheduleSyncDelayedTask(ArcadePlugin.getInstance(), ()-> this.blocksToFall.remove(finalBlock));
			}
		}
	}
}
