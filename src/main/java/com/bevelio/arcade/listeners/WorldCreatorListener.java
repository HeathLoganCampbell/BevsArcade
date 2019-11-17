package com.bevelio.arcade.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import com.bevelio.arcade.ArcadePlugin;

public class WorldCreatorListener implements Listener
{
	@EventHandler
	public void onWorldUnload(WorldUnloadEvent e)
	{
		if(!ArcadePlugin.getInstance().getWorldCreatorManager().isStopWorldUnload())
			e.setCancelled(true);
	}
}
