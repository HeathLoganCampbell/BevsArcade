package com.bevelio.arcade.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;

public class ServerUtils 
{
	public static void callEvent(Event event)
	{
		Bukkit.getPluginManager().callEvent(event);
	}
	
	public static World createWorld(WorldCreator worldCreator)
	{
		return Bukkit.createWorld(worldCreator);
	}
	
	public static void unloadWorld(String worldName)
	{
		Bukkit.unloadWorld(worldName, false);
	}
	
	public static void log(String message)
	{
		Bukkit.getConsoleSender().sendMessage(message);
	}
	
	public static void alertBox(String title, String message)
	{
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < 45; i++)
		{
			str.append("#");
		}
	}
	
	//###############[BevsArcade]##############
	//#					Hello                 #
	//#										  #
	//# Hey Pals, You are currently running a #
	//#   test plugin of this plugin, which   #
	//#   isn't too good because you may run  #  
	//#   into a few bugs... a lot of bugs    #
	//#########################################
	
	
	public static void alertBox1(String title, String message)
	{
		
	}
	
	public static void main(String... args)
	{
		alertBox1("Hello", "Hey Pals, You are currently running a test plugin of this plugin, which isn't too good because you may run into a few bugs... a lot of bugs");
	}
}
