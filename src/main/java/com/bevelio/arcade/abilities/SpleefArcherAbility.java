package com.bevelio.arcade.abilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;

import com.bevelio.arcade.games.spleef.Spleef;
import com.bevelio.arcade.types.Ability;

public class SpleefArcherAbility extends Ability
{

	public SpleefArcherAbility() 
	{
		super("SpleefArcherAbility", "Break blocks with your bow");
	}
	
	@EventHandler
	public void onBlockHit(ProjectileHitEvent e)
	{
		Entity entity = e.getEntity();
		if(!(entity instanceof Arrow)) return;
		ProjectileSource projectileSource = ((Arrow)entity).getShooter();
		if(!(projectileSource instanceof Player)) return;
		Player player = (Player) projectileSource;
		if(!this.hasAbility(player)) return;
		
		Location locationHit = entity.getLocation().add(entity.getVelocity());
		Block blockHit = locationHit.getBlock();
		
		Block closestBlock = blockHit;
		
		BlockIterator iterator = new BlockIterator(entity.getWorld(), entity.getLocation().toVector(), entity.getVelocity().normalize(), 0.0D, 4);
		while(iterator.hasNext()) 
		{
			Block curBlock = iterator.next();
			if(curBlock.getType() != Material.AIR)
			{
				closestBlock = curBlock;
				break;
			}
		}
		
		for(BlockFace blockFace : BlockFace.values())
			Spleef.damageBlock(player, closestBlock.getRelative(blockFace));
	}
}
