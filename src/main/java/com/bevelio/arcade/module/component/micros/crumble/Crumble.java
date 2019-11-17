package com.bevelio.arcade.module.component.micros.crumble;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.module.component.MicroComponent;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.utils.MathUtils;

public class Crumble extends MicroComponent
{
	public Crumble(Game game) 
	{
		super("Crumble", "The map will fall apart as the game goes on", new String[] { "When making the map, add customs to block called 'Crumble'" }, game);
	}

	private Location center;
	private List<Block> crumbleBlocks = new ArrayList<>();
	private long startCrumbleTimestamp = 12000l;
	private long IncreaseSpeedOfEveryCrumbleTimestamp = 4000l;
	private int speedCapCrumbleTimestamp = 3;
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e)
	{
		Block block = e.getBlock();
		if(block.getWorld() != this.getGame().getWorld()) return;
		this.crumbleBlocks.add(block);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		if(block.getWorld() != this.getGame().getWorld()) return;
		this.crumbleBlocks.add(block);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UpdateType.TICK) return;
		if(!this.getGameManager().isRunning()) return;
		if(this.crumbleBlocks.isEmpty()) return;
		long milliseconds = (System.currentTimeMillis() - this.getGame().getStartTimeStamp());
		if(milliseconds < this.startCrumbleTimestamp) return;
		int blockPerSec = (int) ((milliseconds - this.startCrumbleTimestamp) / this.IncreaseSpeedOfEveryCrumbleTimestamp);
		if(blockPerSec >= this.speedCapCrumbleTimestamp)
			blockPerSec = this.speedCapCrumbleTimestamp;
		for(int i = 0; i < blockPerSec; i++)
		{
			Block bestBlock = null;
			double bestDistance = 0.0d;
			
			for(Block block : this.crumbleBlocks)
			{
				double distance = MathUtils.offset2D(this.center, block.getLocation().add(0.5, 0.5, 0.5));
				if(bestBlock == null || distance > bestDistance)
				{
					bestBlock = block;
					bestDistance = distance;
				}
			}
			
			while(bestBlock.getRelative(BlockFace.DOWN).getType() != Material.AIR)
				bestBlock = bestBlock.getRelative(BlockFace.DOWN);
				
			this.crumbleBlocks.remove(bestBlock);
			
			if(bestBlock.getType() != Material.AIR)
			{
				if(MathUtils.random() > 0.75d)
					bestBlock.getWorld().spawnFallingBlock(bestBlock.getLocation().add(0.5D, 0.5D, 0.5D), bestBlock.getType(), bestBlock.getData());
				bestBlock.setType(Material.AIR);
			}
		}
	}
}
