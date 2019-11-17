package com.bevelio.arcade.games.runner;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.BlockData;
import com.bevelio.arcade.types.PlayState;

public class Runner extends SoloGame
{
	private HashMap<Block, Long> blocksToFall = new HashMap<>();
	private long timeForBlockToFall = 100; //2 seconds
	private boolean spawnFallingEntity = true;
	private HashMap<BlockData, BlockData> blocksToBlocks = new HashMap<>();
	// - STONE:0 -> COBBLE:0
	// blocksToBlocks.put(stone, cobble)
	
	public Runner() 
	{
		super("Runner", new String[] 
						{
							"Blocks disappear when you run on them",
							"Keep moving and don't fall",
							"Last man standing wins"
						},
			new ItemStackBuilder(Material.TNT));
		this.deathOut = true;
		this.quitOut = true;
		
		this.hungerSet = 20;
		
		this.pregameFreeze = false;
	}
	
	public void addBlock(Block block)
	{
		if(block == null) return;
		if(this.getWorld() != block.getWorld()) return;
		if(block.getType() == Material.AIR) return;
		
		blocksToFall.put(block, System.currentTimeMillis() + timeForBlockToFall);
	}
	
	public void updateBlock(Block block)
	{
		Block finalBlock = block;
		Material turnToType = Material.AIR;
		byte turnToData = 0;
		if(this.spawnFallingEntity)
		{
			Material type = finalBlock.getType();
			byte data = finalBlock.getData();
			Location loc = finalBlock.getLocation();
			loc.add(0.5, 0.5, 0.5);
		
			loc.getWorld().spawnFallingBlock(loc, type, data);
		}
		
		BlockData blockData = new BlockData();
		blockData.parseBlockData(finalBlock);
		if(this.blocksToBlocks.containsKey(blockData))
		{
			turnToType = this.blocksToBlocks.get(blockData).getMaterial();
			turnToData = this.blocksToBlocks.get(blockData).getData();	
		}
			
		
		block.setType(turnToType);
		block.setData(turnToData);
		
		if(turnToType == Material.AIR)
			Bukkit.getScheduler().scheduleSyncDelayedTask(ArcadePlugin.getInstance(), ()-> this.blocksToFall.remove(finalBlock));
	}
	
	@EventHandler
	public void onDamageCancel(CustomDamageEvent e)
	{
		if(e.getPlayer() == null)
			e.setCancelled("Not Player");
		if(e.getProjectile() == null)
			e.setCancelled("No Projectile");
		if(e.getCause() == DamageCause.FALL)
			e.setCancelled("No fall enabled");
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
		this.checkEnd();
	}
	
	@SuppressWarnings("deprecation")
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
		    
		    for(int z = zMin; z <= zMax; z++)
		    	for(int x = xMin; x <= xMax; x++)
		    		 addBlock(player.getLocation().add(x, -0.5D, z).getBlock());
		}
		
		for(Entry<Block, Long> blocksToFallSet : this.blocksToFall.entrySet())
		{
			if(blocksToFallSet.getValue() < System.currentTimeMillis())
			{
				
			}
		}
	}
}
