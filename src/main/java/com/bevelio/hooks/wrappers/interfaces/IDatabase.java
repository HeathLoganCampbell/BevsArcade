package com.bevelio.hooks.wrappers.interfaces;

import java.util.UUID;

import com.bevelio.hooks.wrappers.records.BevsPlayer;

public interface IDatabase 
{
	public void connectDatabase();
	public void createDatabase();
	
	public void registerPlayer(BevsPlayer player);
	public void updatePlayer(BevsPlayer player);
	public void unregisterPlayer(UUID uuid);
	
	public boolean containsPlayer(UUID uuid);
	public boolean containsPlayer(String name);
	
	public BevsPlayer getPlayer(UUID uuid);	
	public BevsPlayer getPlayer(String name);	
}
