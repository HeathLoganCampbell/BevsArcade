package com.bevelio.arcade.module.display;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.security.auth.Destroyable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.display.click.Clickable;

public class Page implements Destroyable
{
	private String name;
	protected Display display;
	private boolean staticInventory = true;
	private int size;
	
	private HashMap<Integer, ItemStackBuilder> icons;
	private HashMap<Integer, Clickable> slots;
	
	public Page(String name, Display display)
	{
		this.name = name;
		this.display = display;
		this.icons = new HashMap<>();
		this.slots = new HashMap<>();
		this.size = display.getInvetory().getSize();
	}
	
	public Page(String name, Display display, int size)
	{
		this.name = name;
		this.display = display;
		this.icons = new HashMap<>();
		this.slots = new HashMap<>();
		this.size = size;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setIcon(int slot, ItemStackBuilder item)
	{
		this.icons.put(slot, item);
	}
	
	public void setClickable(int slot, Clickable clickable)
	{
		this.slots.put(slot, clickable);
	}
	
	public void setPage(Page page)
	{
		this.display.setPage(page);
	}
	
	public Clickable getSlot(int slot)
	{
		return this.slots.get(slot);
	}
	
	public void build(Inventory inventory, Player player)
	{
		inventory.clear();
		for(Entry<Integer, ItemStackBuilder> itemEntry : this.icons.entrySet())
		{
			ItemStackBuilder item = itemEntry.getValue();
			inventory.setItem(itemEntry.getKey(), item.build());
		}
	}
	
	public void init()
	{
		
	}
	
	@Override
	public void destroy()
	{
		this.name = null;
		this.display = null;
		this.icons.clear();
		this.slots.clear();
	}

	public boolean isStaticInventory() {
		return staticInventory;
	}

	public void setStaticInventory(boolean staticInventory) {
		this.staticInventory = staticInventory;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
