package com.bevelio.arcade.managers;

import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.pages.KitSelector;

public class GameMasterManager
{
	public GameMasterManager()
	{
		
	}
	
	public Display createOrGetGameMasterMenu(Player player)
	{
		Display selectorDisplay = ArcadePlugin.getInstance().getDisplayCore().getDisplay(player.getUniqueId(), "gamemaster_selector_" + player.getName());
		if(selectorDisplay == null)
		{
			selectorDisplay = new Display("gamemaster_selector_" + player.getName(), player);
			ArcadePlugin.getInstance().getDisplayCore().registerDisplay(player.getUniqueId(), selectorDisplay);
		}
		selectorDisplay.setPage(new KitSelector("GameMaster Menu", 0, selectorDisplay));
		selectorDisplay.open();
		return selectorDisplay;
	}
	
	private void generatePages(Display display)
	{
		
	}
}
