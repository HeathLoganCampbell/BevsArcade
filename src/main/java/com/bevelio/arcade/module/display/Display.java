package com.bevelio.arcade.module.display;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.bevelio.arcade.module.display.click.ClickLog;
import com.bevelio.arcade.module.display.click.Clickable;
import com.bevelio.arcade.module.display.open.OpenLog;
import com.bevelio.arcade.utils.InventoryTitleHelper;

public class Display implements Destroyable, Listener
{
	private String idName;
	private Inventory inventory;
	private Page currentPage;
	private Player player;
	
	public Display(String idName, Player player)
	{
		this.idName = idName;
		this.inventory = Bukkit.createInventory(player, 54, idName);
		this.player = player;
	}
	
	public String getIdName()
	{
		return idName;
	}
	
	public void setPage(Page page)
	{
		this.currentPage = page;
	}
	
	public Page getPage()
	{
		return this.currentPage;
	}
	
	public Inventory getInvetory()
	{
		return this.inventory;
	}
	
	public void update()
	{
		this.update(this.player);
	}
	
	public void update(Player player)
	{
		InventoryTitleHelper.sendInventoryTitle(player, this.getPage().getName(), this.getPage().getSize());
		this.getPage().init();
		this.getPage().build(getInvetory(), player);
		player.updateInventory();
	}
	
	public void open()
	{
		this.open(this.player);
	}
	
	public void open(Player player)
	{
		OpenLog log = new OpenLog(this, player, this.getInvetory()); 
		this.onOpen(log);
		if(log.isCancelled()) return;
		player.openInventory(getInvetory());
		update(player);
	}
	
	public void onOpen(OpenLog log) {}
	
	@EventHandler
	public void onInventory(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		int slot = e.getSlot();
		if(inv == null) return;
		if(this.inventory == null) return;
		if(slot == -1) return;
		if(!inv.getTitle().equals(this.inventory.getTitle())) return;
		Clickable clickable = this.getPage().getSlot(slot);
		e.setCancelled(this.getPage().isStaticInventory());
		if(clickable == null) return;
		boolean right = e.getAction().name().contains("PICKUP");
		boolean left = e.getAction().name().contains("PLACE");
		ClickLog clicklog = new ClickLog(player, left, right, e.isShiftClick(), e.getCurrentItem(), slot, this);
		clicklog.setCancelled(e.isCancelled());
		clickable.onClick(clicklog);
		e.setCancelled(clicklog.isCancelled());
	}
	
	@Override
	public void destroy()
	{
		HandlerList.unregisterAll(this);
		this.inventory.clear();
		this.player = null;
		this.currentPage.destroy();
	}
}