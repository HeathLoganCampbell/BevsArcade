package com.bevelio.arcade.types;

import java.util.UUID;

public class Reward 
{
	public String name, reason;
	public double amount;
	public UUID uuid;
	
	public Reward(String name, String reason, double amount, UUID uuid) 
	{
		this.name = name;
		this.reason = reason;
		this.amount = amount;
		this.uuid = uuid;
	}
}
