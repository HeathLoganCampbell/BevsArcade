package com.bevelio.arcade.types;

import java.util.UUID;

import org.bukkit.Location;

public class DisconnectLog
{
	public UUID uuid;
	public String name;
	public double health;
	public String kit;
	public Long dcTimeStamp;
	public String team;
	public Location location;
	
	public DisconnectLog(UUID uuid, String name, double health, String kit, Long dcTimeStamp, String team, Location location) 
	{
		this.uuid = uuid;
		this.name = name;
		this.health = health;
		this.kit = kit;
		this.dcTimeStamp = dcTimeStamp;
		this.team = team;
		this.location = location;
	}
}