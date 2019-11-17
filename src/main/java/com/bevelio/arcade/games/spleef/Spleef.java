package com.bevelio.arcade.games.spleef;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.spleef.event.BlockFadeEvent;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.types.PlayState;

public class Spleef extends SoloGame
{
	private HashMap<UUID, Long> cooldown = new HashMap<>();
	private boolean canBreakBlocksWithHand = true;
	
	public Spleef() 
	{
		super("Spleef", new String[] 
						{
							"Destroy the blocks under others", 
							"Causing them to fall to their deaths", 
							"Last man standing wins"
						},
			new ItemStackBuilder(Material.DIAMOND_SPADE));
		this.deathOut = true;
		this.quitOut = true;
		this.damageOwnTeam = false;
		this.hungerSet = 20;
	}
	
	public static void damageBlock(Player player, Block block)
	{
		block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, block.getTypeId());
		block.setType(Material.AIR);
		BlockFadeEvent event = new BlockFadeEvent(player, block);
		Bukkit.getPluginManager().callEvent(event);
//		BlockBreakEvent event = new BlockBreakEvent(block, player);
//		Bukkit.getPluginManager().callEvent(event);
	}
	
	@EventHandler
	public void onBlockTap(BlockDamageEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(block == null) return;
		if(!canBreakBlocksWithHand) return;
		if(this.cooldown.containsKey(player.getUniqueId()) 
				&& (System.currentTimeMillis() - this.cooldown.get(player.getUniqueId())) < 30)
			return;
		if(block.getType() == Material.AIR) return;
		if(block.getType() == Material.BEDROCK) return;
		if(!isAllValid(player)) return;
		
		cooldown.put(player.getUniqueId(), System.currentTimeMillis());
		
		damageBlock(player, block);
		
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	@EventHandler
	public void onBlockHit(ProjectileHitEvent e)
	{
		Entity entity = e.getEntity();
		if(!(entity instanceof Snowball)) return;
		ProjectileSource projectileSource = ((Snowball)entity).getShooter();
		if(!(projectileSource instanceof Player)) return;
		Player player = (Player) projectileSource;
		if(!this.getGameManager().isInteractivePlayer(player)) return;
		if(this.getPlayState(player) != PlayState.IN) return;
		Location locationHit = entity.getLocation().add(entity.getVelocity());
		Block blockHit = locationHit.getBlock();
//		double velocity = entity.getVelocity().length();
//		double radius = 0.5D + velocity / 1.6D;
		
		
		Block closestBlock = blockHit;
		
		
		BlockIterator iterator = new BlockIterator(e.getEntity().getWorld(), e.getEntity().getLocation().toVector(), e.getEntity().getVelocity().normalize(), 0.0D, 4);
		while(iterator.hasNext()) 
		{
			Block curBlock = iterator.next();
			if(curBlock.getType() != Material.AIR)
			{
				closestBlock = curBlock;
				break;
			}
		}
		
		damageBlock(player, closestBlock);
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
}
