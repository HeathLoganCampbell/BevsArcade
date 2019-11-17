package com.bevelio.arcade.games.rainingblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;

import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.rainingblocks.configs.RainingBlocksConfig;
import com.bevelio.arcade.games.tntrun.config.TnTRunConfig;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.misc.XYZ;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.utils.MathUtils;
import com.bevelio.arcade.utils.ServerUtils;


// 14 * 14
// 196 Possible location

public class RainingBlocks extends SoloGame 
{
	private double probabilityOfBlock = 0.01;
	private double probabilityIncreaser = 0.005;
	private List<Block> cloudBlocks = new ArrayList<>();
	private int secTick = 0;

	public RainingBlocks()
	{
		super("RainingBlocks", new String[] {"WATCHOUT FOR BLOCKS FROM ABOVE", "LAST MAN STANDING WINS!"}, new ItemStackBuilder(Material.STAINED_CLAY, 1,(short) 1));
		this.setConfigs(new RainingBlocksConfig(this));
		
		this.deathOut = true;
		this.quitOut = true;
		this.damageOwnTeam = false;
		this.pregameFreeze = false;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		findCloudBlocks();
//		if(!(this.configs instanceof MicroWallsConfig)) return;
//		MicroWallsConfig config = (MicroWallsConfig) this.getConfigs();
//		wallDropsSec = (long) (1000 * config.getWallsDropInSeconds());
	}
	
	public void findCloudBlocks()
	{
//		Bukkit.broadcastMessage("Finding rain clouds");
		List<XYZ> wallBlocks = this.getWorldData().customs.get("Clouds");
//		Bukkit.broadcastMessage(wallBlocks + "");
		if(wallBlocks == null) return;
//		Bukkit.broadcastMessage(Arrays.toString(wallBlocks.toArray()));
		for(XYZ xyzLoc : wallBlocks)
		{
			Location location = xyzLoc.toLocation(this.getWorld());
			Block block = location.getBlock();
			this.cloudBlocks.add(block);
		}
		
		if(this.getConfigs() instanceof RainingBlocksConfig)
		{
			RainingBlocksConfig config = (RainingBlocksConfig) this.getConfigs();
			this.probabilityOfBlock = (config.getBlockFallingSpeedBase() * this.cloudBlocks.size()) /  this.cloudBlocks.size();
			this.probabilityIncreaser = (config.getBlockFallingSpeedIncreaser() * this.cloudBlocks.size()) /  this.cloudBlocks.size();
		}
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(!this.isLive()) return;
		if(e.getType() == UpdateType.SECOND)
		{
			secTick++;
			if(this.getConfigs() instanceof RainingBlocksConfig)
			{
				RainingBlocksConfig config = (RainingBlocksConfig) this.getConfigs();
				String message = config.getDifficultMessages().get(secTick);
				if(message != null)
					this.broadcast(message);
			}
		}
		
		if(e.getType() == UpdateType.SEC_10)
			probabilityOfBlock += probabilityIncreaser;
		
		if(e.getType() != UpdateType.SEC_ODD)
			return;
		
		for(Block block : this.cloudBlocks)
		{
			if(MathUtils.random() > this.probabilityOfBlock)
				continue;
			Location location = block.getLocation();
			location.add(0.5, 0.5, 0.5);
			location.getWorld().spawnFallingBlock(location, 159, (byte) MathUtils.random(1, 14));
		}
		
	}
	
	@EventHandler
	public void onFallingSandHitsGround(EntityChangeBlockEvent e)
	{
		Block block = e.getBlock();
		if(!this.isLive()) return;
		if(block.getWorld() != this.getWorld()) return;
		Location blockLoc = block.getLocation();
		blockLoc.add(0.5, 0.5, 0.5);
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(!this.isInQueue(player)) continue;
			double dist = MathUtils.offset2D(blockLoc, player.getLocation());
			if(dist < 0.49999)
			{
				ServerUtils.callEvent(new CustomDamageEvent(player, null, null, 999, 0, null, blockLoc, "Falling Block", "Falling Block", false));
			}
		}
	}
	
	@EventHandler
	public void onBlockHit(CustomDamageEvent e)
	{
		if(e.getCause() != DamageCause.FALLING_BLOCK) return;
		
		e.addMod("Falling Block", "Insta Kill", 999, false);
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
		
		if(this.getConfigs() instanceof RainingBlocksConfig)
		{
			RainingBlocksConfig config = (RainingBlocksConfig) this.getConfigs();
			String deathMsg = config.getDeathMessage();
			if(deathMsg != null && deathMsg.length() > 0)
			{
				deathMsg = deathMsg.replace("%Player%", player.getName())
						 		   .replace("%Alive_Players%", (this.getAlivePlayers().size() - 1) + "");
				this.broadcast(deathMsg);
			}
				
		}
	}
}
