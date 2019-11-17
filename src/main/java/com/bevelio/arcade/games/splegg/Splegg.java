package com.bevelio.arcade.games.splegg;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.misc.ItemStackBuilder;

public class Splegg extends SoloGame
{
	private final String fireEggAction = "RIGHT_CLICK";
	private final Material spadeThatFiresEggs = Material.DIAMOND_SPADE;
	private boolean firingHandledByKits = false;
	
	public Splegg() 
	{
		super("Splegg", new String[] 
						{
							"Destroy the blocks under others", 
							"Causing them to fall to their deaths", 
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
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockTap(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();
		ItemStack item = e.getItem();
		
		if(!firingHandledByKits)
			if(e.getAction().name().contains(this.fireEggAction)) 
				if(item != null && item.getType() == this.spadeThatFiresEggs)
				{
//					player.throwEgg();
				}	
		
		
		
		if(block == null) return;
		if(block.getType() == Material.AIR) return;
		if(block.getType() == Material.BEDROCK) return;
		if(!this.isLive()) return;
		
		block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, block.getTypeId());
		block.setType(Material.AIR);
	}
	
	@EventHandler
	public void onBlockHit(ProjectileHitEvent e)
	{
		Entity entity = e.getEntity();
		if(entity instanceof ThrownPotion) return;
		Location locationHit = entity.getLocation().add(entity.getVelocity());
		Block blockHit = locationHit.getBlock();
		Block closestBlock = blockHit;
		
		if(!this.isLive()) return;
		
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
		
		if(closestBlock.getType() == Material.BEDROCK) return;
		closestBlock.getWorld().playEffect(closestBlock.getLocation(), Effect.TILE_BREAK, blockHit.getTypeId());
		closestBlock.setType(Material.AIR);
	}
	
	@EventHandler
	public void DamageCancel(CustomDamageEvent e)
	{
		if (e.getPlayer() == null)
			e.setCancelled("Not Player");
		if (e.getProjectile() == null)
			e.setCancelled("No Projectile");
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		this.getWinners().add(0, this.getAlivePlayers().get(0));
		this.checkEnd();
	}
}
