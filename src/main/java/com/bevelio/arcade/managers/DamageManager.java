package com.bevelio.arcade.managers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.configs.files.DeathMessagesConfig;
import com.bevelio.arcade.configs.files.MainConfig;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.utils.MathUtils;

public class DamageManager implements Listener
{
	private DeathMessageType type = DeathMessageType.SIMPLE;
	private DeathMessagesConfig dmc = ArcadePlugin.getInstance().getConfigManager().getDeathMessagesConfig();
	private MainConfig mainConfig = ArcadePlugin.getInstance().getConfigManager().getMainConfig();
	private HashMap<UUID, Long> damageCooldown = new HashMap<>();
	
	public DamageManager() {}
	
	
	private void damage(CustomDamageEvent e)
	{
		if(e.getEntity() == null)
			return;
		if(e.getEntity().getHealth() <= 0.0)
			return;
		
		e.getEntity().playEffect(EntityEffect.HURT);
		
		if(e.isKnockback() && e.getDamagerEntity() != null)
		{
			double knockback = e.getDamage();
			if (knockback < 2.0D) 
				knockback = 2.0D;
			knockback = Math.log10(knockback);
			
			for (Iterator<Double> localIterator = e.getKnockbackMod().values().iterator(); localIterator.hasNext(); )
			{ 
				double cur = ((Double)localIterator.next()).doubleValue();
				knockback *= cur;
	        }
			
			Location origin = e.getDamagerEntity().getLocation();
			if (e.getKnockbackBaseLoc() != null)
				origin = e.getKnockbackBaseLoc();
			
			Vector vector = origin.toVector().subtract(e.getEntity().getLocation().toVector()).normalize();
			vector.multiply(0.6D * knockback);
			
			vector.multiply(-0.1);
			double yValue = Math.abs(vector.getY());
//			if( e.getKnockbackMod().size() == 0 && yValue > 0.3)
//				yValue = 0.30;
			vector.setY(yValue);
			double vel = 0.2D + vector.length() * 0.8D;
			applyVelocity(e.getEntity(), vector, vel, false, 0.0D, Math.abs(0.2D * knockback), 0.4D + 0.04D * knockback, true);
		}
		
		double damage = e.getDamage();
		
		if(!e.isIgnoreArmor())
			damage += e.getDamageReductionArmor();
		
		if(e.getEntity().getHealth() - e.getDamage() <= 0 )
		{
//			
			if(e.getPlayer() != null)
			{
				Player player = e.getPlayer();
				
				String deathMessage = this.getDeathMessage(e);
				
				Player lastAttacker = e.getDamagerPlayer();
				
				if(lastAttacker != null)
					this.setKiller(player, lastAttacker);
				else 
					this.setKiller(player, null);
				
				Location location = player.getLocation().clone();
				List<ItemStack> dropItems = new ArrayList<>();
				
				dropItems.addAll(Arrays.asList(player.getInventory().getContents()));
				
				PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, dropItems, 0, 0, 0, 0, deathMessage);
				Bukkit.getPluginManager().callEvent(deathEvent);
				
				Game game = ArcadePlugin.getInstance().getGameManager().getGame();
				if(game != null)
				{
					if(game.deathDropItems)
						for(ItemStack item : deathEvent.getDrops())
							location.getWorld().dropItem(location, item);
				}
				
				DebugCommands.message(e.getPlayer(), "Called PlayerDeathEvent!");
				if(this.type == DeathMessageType.NORMAL)
					if(deathEvent.getDeathMessage() != null)
						ArcadePlugin.getInstance().getGameManager().broadcast(deathMessage);
				if(this.type == DeathMessageType.SIMPLE)
				{
					if(deathEvent.getDeathMessage() != null)
					{
						String[] playerDeathMsg = dmc.getSimple_Unknown();
						String[] attackerDeathMsg = dmc.getSimple_Unknown();
						
						if(lastAttacker != null)
						{
							attackerDeathMsg = dmc.getSimple_Attacker_Slain();
							playerDeathMsg =  dmc.getSimple_Player_Slain();
						}
						
						String finalPlayerDeathMessage = playerDeathMsg[MathUtils.random(attackerDeathMsg.length) - 1];
						String finalAttackerDeathMessage = attackerDeathMsg[MathUtils.random(attackerDeathMsg.length) - 1];
						if(lastAttacker != null)
						{
							finalAttackerDeathMessage = finalAttackerDeathMessage.replaceAll("%Player%", player.getName())
																				 .replaceAll("%Attacker%", "You");
							finalPlayerDeathMessage = finalPlayerDeathMessage.replaceAll("%Player%", "You")
																			  .replaceAll("%Attacker%", lastAttacker.getName());
							lastAttacker.sendMessage(finalAttackerDeathMessage);
						}
						finalPlayerDeathMessage = finalPlayerDeathMessage.replaceAll("%Player%", player.getName());
						player.sendMessage(finalPlayerDeathMessage);
						
					}
				}
				
				if(game != null)
					game.toSpectatorMode(player);
			}
		} else
		{
			if(e.getEntity() != null)
				if(damage > 0)
					e.getEntity().damage(damage);
		}
	}
	
	public String getDeathMessage(CustomDamageEvent e)
	{
		DamageCause cause = e.getCause();
		if(!(e.getEntity() instanceof Player)) return null;
		String[] deathMsgsToUse = dmc.getSimple_Unknown();
		String deathMessage = null;
		
		if(cause != null)
		{
			switch(cause)
			{
			case BLOCK_EXPLOSION:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_BlewUp();
				break;
			case CONTACT:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Cactus();
				break;
			case CUSTOM:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Unknown();
				break;
			case DROWNING:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Drowned();
				break;
			case ENTITY_ATTACK:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Slain();
				break;
			case ENTITY_EXPLOSION:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_BlewUp();
				break;
			case FALL:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_HitTheGroundTooHard();
				break;
			case FALLING_BLOCK:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_FlatternedByAFallingBlock();
				break;
			case FIRE:
			case FIRE_TICK:
			case LAVA:
			case MELTING:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_BurntToACrisp();
				break;
			case LIGHTNING:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Lightning();
				break;
			case MAGIC:
			case POISON:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Potions();
				break;
			case PROJECTILE:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Shot();
				break;
			case STARVATION:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Starved();
				break;
			case SUFFOCATION:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Suffocated();
				break;
			case SUICIDE:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Unknown();
				break;
			case THORNS:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Thorns();
				break;
			case VOID:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_FellOutOfWorld();
				break;
			case WITHER:
				if(this.type == DeathMessageType.NORMAL)
					deathMsgsToUse = dmc.getNormal_Wither();
				break;
			default:
				deathMsgsToUse = dmc.getNormal_Unknown();
				break;
			}
		} else
			deathMsgsToUse = dmc.getNormal_Unknown();
		
		deathMessage = deathMsgsToUse[MathUtils.random(deathMsgsToUse.length) - 1];
		Player player = e.getPlayer();
		deathMessage = deathMessage.replaceAll("%Player%", player.getName());
		if(e.getDamagerPlayer() != null)
			deathMessage = deathMessage.replaceAll("%Attacker%", player.getName());
		
		if(e.getDamagerEntity() != null)
		{
			String name = e.getDamagerEntity().getName();
			
			if(e.getDamagerEntity().isCustomNameVisible())
				name = e.getDamagerEntity().getCustomName();
			deathMessage = deathMessage.replaceAll("%Attacker%", name);
		}
		
		return deathMessage;
	}
	
	
	private Object getHandle(Player player)
	{
		try {
			Method getHandle = player.getClass().getMethod("getHandle");
	    	Object nmsPlayer = getHandle.invoke(player);
	    	
			return nmsPlayer;
		} catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	
    private void setKiller(Player player, Player killer) {
        try {
        	Object nmsPlayer = getHandle(player);
        	Object nmsKiller = null;
        	if(killer != null)
        		nmsKiller = getHandle(killer);
        	DebugCommands.message(player, player.getName() + "'s new killer was set to " + DebugCommands.spec(killer == null ? "No one" : killer.getName()));
        	DebugCommands.message(player, nmsPlayer + " P : K " + nmsKiller);
        	
        	Field playerKillerField = nmsPlayer.getClass().getField("killer");
        	
        	playerKillerField.setAccessible(true);
        	playerKillerField.set(nmsPlayer, nmsKiller);
        	DebugCommands.message(player,CC.aqua + "Final result " + player.getKiller());
        	playerKillerField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void applyVelocity(Entity ent, Vector vec, double str, boolean ySet, double yBase, double yAdd, double yMax, boolean groundBoost)
	{
		if ((Double.isNaN(vec.getX())) || (Double.isNaN(vec.getY())) || (Double.isNaN(vec.getZ())) || (vec.length() == 0.0D))
			return;

		if (ySet)
			vec.setY(yBase);

		vec.normalize();
		vec.multiply(str);

		vec.setY(vec.getY() + yAdd);

		if (vec.getY() > yMax)
			vec.setY(yMax);
		
		Block block = ent.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if ((groundBoost) && (block.getType().isSolid()))
			vec.setY(vec.getY() + 0.2D);
		ent.setFallDistance(0.0F);
		ent.setVelocity(vec);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e)
	{
		if(!(e.getEntity() instanceof LivingEntity))
			return;
		if(e.getCause() == DamageCause.CUSTOM) return;
		
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			boolean isPlaying = ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player);
			if(!isPlaying) return;
		}
		
		DamageCause cause = e.getCause();
		LivingEntity entity = (LivingEntity) e.getEntity();
		LivingEntity damagerEntity = null;
		Projectile projectile = null;
		double baseDamage = e.getDamage();
		if(e instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent edbe = ((EntityDamageByEntityEvent) e);
			if(edbe.getDamager() instanceof Projectile)
			{
				projectile = (Projectile) edbe.getDamager();
				damagerEntity = (LivingEntity) projectile.getShooter();
				if(projectile instanceof Arrow)
					baseDamage = projectile.getVelocity().length() * 3.0D;
			}
			else
				if(edbe.getDamager() instanceof LivingEntity)
					damagerEntity = (LivingEntity) edbe.getDamager();
		}
		
//		Location baseLoc = entity.getLocation();
		
		CustomDamageEvent myEvent = new CustomDamageEvent(entity, damagerEntity, projectile, baseDamage,  e.getDamage(DamageModifier.ARMOR), cause, null, null, null, e.isCancelled());
		Bukkit.getPluginManager().callEvent(myEvent);
		e.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void EndDamageEvent(CustomDamageEvent event)
	{
		if ((!event.isCancelled()) && (event.getDamage() > 0.0D))
		{
			
			damage(event);
			Player player = event.getPlayer();
			if(player == null) return;
			DebugCommands.message(player, "You have taken damage from " + DebugCommands.spec(event.getCause() + ""));
//			if ((event.getProjectile() != null) && ((event.getProjectile() instanceof Arrow)) && player != null)
//				if (player != null)
//					player.playSound(player.getLocation(), Soun, 0.5F, 0.5F);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void debugEvent(CustomDamageEvent event)
	{
		if ((event.isCancelled()))
		{
			if(event.getPlayer() == null) return;
			for(String str : event.getCancelledReasons())
				DebugCommands.message(event.getPlayer(), str);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void damageCooldown(CustomDamageEvent e)
	{
		if (!(e.isCancelled()))
			if(e.getPlayer() != null)
				if(e.getDamagerPlayer() != null)
				{
					UUID uuid = e.getPlayer().getUniqueId();
					if(this.damageCooldown.get(uuid) == null || this.damageCooldown.get(uuid) < System.currentTimeMillis())
					{
						long addCooldown = (long) (1000l * mainConfig.getHitDelaySeconds());
						this.damageCooldown.put(uuid, System.currentTimeMillis() + (addCooldown));
					} else if(this.damageCooldown.get(uuid) > System.currentTimeMillis())
						e.setCancelled("Cooldown");
				}
	}
	
	public enum DeathMessageType 
	{
		SIMPLE, NORMAL, COMPLEX
	}
}
