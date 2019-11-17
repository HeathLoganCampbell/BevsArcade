package com.bevelio.arcade.pages.gamemaster;

import org.bukkit.Material;

import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.module.display.Page;
import com.bevelio.arcade.module.display.click.ClickLog;
import com.bevelio.arcade.module.display.click.Clickable;
import com.bevelio.arcade.pages.BuildPage;
import com.bevelio.arcade.utils.MathUtils;

public class GameMasterPage extends Page
{
	public int slotId = 0;
	
	public GameMasterPage(String name, Display display)
	{
		super(name, display);
	}
	
	public void addItem(ItemStackBuilder item, Clickable clickable)
	{
		int slot = getLayoutSlot(slotId++);
		this.setIcon(slot, item);
		if(clickable != null)
			this.setClickable(slot, clickable);
	}
	
	private static int getLayoutSlot(int slotId)
	{
		double id = (slotId % 3) * 2;
		id += 1;
		id += getLayoutRow(slotId);
		return (int) id;
	}
	
	// ((2 * ceil(x / 3)) + 1) * 9
	private static double getLayoutRow(int slotId)
	{
		double id = MathUtils.ceil(slotId / 3) * 2;
		id += 1;
		id = id * 9;
		return id;
	}

	@Override
	public void init()
	{
		
	}
	
	public static final void main(String... args)
	{
		System.out.println(getLayoutSlot(0));//1 1 		(0) - 2 -> (0) - +1 -> (1)
		System.out.println(getLayoutSlot(1));//1 3 		(1) - 2 -> (2) - +1 -> (3)
		System.out.println(getLayoutSlot(2));//1 5 		(2) - 2 -> (4) - +1 -> (5)
		System.out.println(getLayoutSlot(3));//3 1		
		System.out.println(getLayoutSlot(4));//3 3		(n / 3) 
		System.out.println(getLayoutSlot(5));//3 5
	}
}
