package com.bevelio.arcade.module.updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.arcade.module.Module;

public class Updater extends Module 
{
	public Updater(JavaPlugin plugin) 
	{
		super("Updater", plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						UpdateType[] types = UpdateType.values();
						
						for(int i = 0; i < types.length; i++)
							if(types[i].elapsed()) Bukkit.getPluginManager().callEvent(new UpdateEvent(types[i]));
					}
				}
		, 0l, 1l);
	}
}
