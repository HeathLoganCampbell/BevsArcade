package com.bevelio.arcade.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.events.PostApplyKitEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.types.Team;

public class ColorTeamArmorListener implements Listener
{
	private GameManager gm = ArcadePlugin.getInstance().getGameManager();
	
	@EventHandler
	public void onPostKit(PostApplyKitEvent e)
	{
		Player player = e.getPlayer();
		Game game = gm.getGame();
		if(game == null) return;
		if(game.teamArmor)
		{
			Team team = game.getPlayersTeam(player.getUniqueId());
			PlayerInventory inv = player.getInventory();
			ItemStack[] armor = inv.getArmorContents();
			for(int i = 0; i < armor.length; i++)
			{
				ItemStack armorPart = armor[i];
				ItemMeta im = armorPart.getItemMeta();
				if(im instanceof LeatherArmorMeta)
					((LeatherArmorMeta) im).setColor(team.getColor());
				armorPart.setItemMeta(im);
				armor[i] = armorPart;
			}
			inv.setArmorContents(armor);
		}
		
		if(game.teamArmorHotbar)
		{
			
			Team team = game.getPlayersTeam(player.getUniqueId());
			PlayerInventory inv = player.getInventory();
			
			ItemStack armor = new ItemStack(Material.LEATHER_CHESTPLATE);
			ItemMeta im = armor.getItemMeta();
			if(im instanceof LeatherArmorMeta)
				((LeatherArmorMeta) im).setColor(team.getColor());
			armor.setItemMeta(im);
			
			inv.setItem(8, armor);
		}
	}
	
	@EventHandler
	public void onClickCancel(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		Game game = gm.getGame();
		if(game == null) return;
		if(!game.teamArmorHotbar) return;
		if(!game.isLive()) return;
		e.setCancelled(true);
	}
}
