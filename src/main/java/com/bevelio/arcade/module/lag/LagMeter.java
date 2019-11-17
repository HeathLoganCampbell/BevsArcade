package com.bevelio.arcade.module.lag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.module.Module;
import com.bevelio.arcade.module.lag.command.LagCommand;
import com.bevelio.arcade.module.lag.command.LagDataCommand;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;

public class LagMeter extends Module
{
	private long lastRun = -1L;
	private int count;
	private double ticksPerSecond;
	private double ticksPerSecondAverage;
	private long lastAverage;
	
	public LagMeter(JavaPlugin plugin) 
	{
		super("Lag'o'Meter", plugin);
	}
	
	@Override
	public void enable()
	{
		this.lastRun = System.currentTimeMillis();
	    this.lastAverage = System.currentTimeMillis();
	}
	
	@Override
	public void onCommands()
	{
		this.registerCommand(new LagCommand(this));
		this.registerCommand(new LagDataCommand(this));
	}
	
	public double getTicksPerSecond()
	{
		return this.ticksPerSecond;
	}
	
	public void sendUpdate(Player player)
	{
		String[] lagDataMsg = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getCommandLagDataMessage();
		for(String line : lagDataMsg)
		{
			line = this.setPlaceholders(line);
			player.sendMessage(line);
		}
	}
	
	public String setPlaceholders(String line)
	{
		return line.replaceAll("%Ticks_Per_Sec%", String.format("%.00f", this.ticksPerSecond))
				   .replaceAll("%Avg_Ticks_Per_Sec%", String.format("%.00f", this.ticksPerSecondAverage * 20.0D))
				   .replaceAll("%Memory_Free%", (Runtime.getRuntime().freeMemory() / 1048576L) + "MB")
				   .replaceAll("%Memory_Used%", (Runtime.getRuntime().maxMemory() / 1048576L) + "MB");
	}

	@EventHandler
	public void update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SECOND) return;
		long now = System.currentTimeMillis();
	    this.ticksPerSecond = (1000.0D / (now - this.lastRun) * 20.0D);
	    
	    if (this.count % 30 == 0)
	    {
	    	this.ticksPerSecondAverage = (30000.0D / (now - this.lastAverage) * 20.0D);
	    	this.lastAverage = now;
	    }
	    this.lastRun = now;
	    
	    this.count += 1;
	}
}
