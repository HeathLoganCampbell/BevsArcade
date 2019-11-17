package com.bevelio.arcade.module.display;

import org.bukkit.Material;

import com.bevelio.arcade.misc.ItemStackBuilder;

public class HudPage extends Page
{
	private static final ItemStackBuilder NEXT_ICON = new ItemStackBuilder(Material.ARROW)
										, BACK_ICON = new ItemStackBuilder(Material.ARROW)
										;
	
	public HudPage(String name, Display display) 
	{
		super(name, display);
	}
	
	@Override
	public void init()
	{
	}
}
