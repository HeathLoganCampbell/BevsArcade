package com.bevelio.arcade.module;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.utils.ServerUtils;

import lombok.Getter;

public class Module implements Listener
{
	private @Getter String name;
	private @Getter JavaPlugin plugin;
	
	public Module(String name, JavaPlugin plugin)
	{
		this.name = name;
		this.plugin = plugin;
		
		this.register(this);
		
		enable();
		onCommands();
	}
	
	public void enable()
	{
		
	}
	
	public void onCommands()
	{
		
	}
	
	public void registerCommand(Object command)
	{
		ArcadePlugin.getInstance().getCommandFramework().registerCommands(command);;
	}
	
	public void register(Listener listener)
	{
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}
	
	public void log(String message)
	{
		ServerUtils.log(this.getName() + " " + message);
	}
}
