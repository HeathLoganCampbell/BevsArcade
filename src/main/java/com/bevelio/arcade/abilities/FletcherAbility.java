package com.bevelio.arcade.abilities;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.types.Ability;
import com.bevelio.arcade.utils.InputUtils;
import com.bevelio.arcade.utils.ItemUtils;
import com.bevelio.arcade.utils.MathUtils;
import com.bevelio.arcade.utils.PlayerUtils;

/**
 * Slowly get Arrows
 * Uses: Spleef#Archer
 * 		 
 * 
 * Options
 *  Type 		Field			Default
 *-----------------------------------------
 *  Boolean		NonSticky		False
 * 	Double	 	SprintSeconds	2
 *  Int	 		MaxArrows		2
 */
public class FletcherAbility extends Ability
{
	private boolean nonSticky = false;
	private Double sprintSeconds = 2.0;
	private int maxArrows = 2;
	private String arrowDisplayName = CC.aqua + "Fletcher Arrow";
	
	public FletcherAbility()
	{
		super("FletcherAbility", "Get an arrow every so long");
	}
	
	public boolean isFletcherArrow(ItemStack item)
	{
		if(item == null) return false;
		if(item.getType() != Material.ARROW) return false;
		ItemMeta im = item.getItemMeta();
		if(!im.hasDisplayName()) return false;
		if(!im.getDisplayName().contains(ChatColor.stripColor(arrowDisplayName))) return false;
		return true;
	} 
	
	@EventHandler
	public void fletchDrop(PlayerDropItemEvent e) 
	{ 
		if (e.isCancelled()) return;
	    if (!this.isFletcherArrow(e.getItemDrop().getItemStack())) return;

	    e.setCancelled(true);
	}
	
	@EventHandler
	public void onFletcherDeath(PlayerDeathEvent e)
	{
		HashSet<ItemStack> removeDrops = new HashSet<ItemStack>();

		for (ItemStack item : e.getDrops()) 
			if (this.isFletcherArrow(item))
				removeDrops.add(item);
		
		for (ItemStack item : removeDrops)
			if(e.getDrops() != null)
				if(!e.getDrops().isEmpty())
					e.getDrops().remove(item);
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UpdateType.SECOND) return;
		Game game = ArcadePlugin.getInstance().getGameManager().getGame();
		for(Player player : game.getAlivePlayers())
		{
			if(!this.hasAbility(player)) continue;
			if(!game.isAllValid(player)) continue;
			if(!this.isActive(player.getUniqueId())) continue;
			
			if(PlayerUtils.contains(player, this.arrowDisplayName, Material.ARROW, (byte) 0, this.maxArrows))  continue;
			
			player.getInventory().addItem(new ItemStackBuilder(Material.ARROW).displayName(this.arrowDisplayName).build());
			this.setCooldown(player.getUniqueId(), this.sprintSeconds);
		}
	}
	
	@Override
	public void setOptions(HashMap<String, Object> options)
	{
		if(options.containsKey("NonSticky"))
			if(options.get("NonSticky") instanceof Boolean)
			{
				this.nonSticky = (Boolean) options.get("NonSticky");
			} else System.out.println(this.getName() + " the field " + "NonSticky" + " does't accept anything other than a boolean. You gave it" + options.get("Whitelist"));
	
		if(options.containsKey("SprintSeconds"))
			this.sprintSeconds = InputUtils.getDouble(options.get("SprintSeconds"));
		
		if(options.containsKey("MaxArrows"))
		{
			this.maxArrows =  ((Double)options.get("MaxArrows")).intValue();
		}
		
		if(options.containsKey("ArrowDisplayName"))
			this.arrowDisplayName =  ChatColor.translateAlternateColorCodes('&', ((String) options.get("ArrowDisplayName")).replaceAll("_", " "));
	}
}
