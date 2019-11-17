package com.bevelio.arcade.types;

import lombok.Data;

@Data
public class DamageLog 
{
	private double damage;
	private String source;
	private String reason;
	private boolean useReason;
	
	public DamageLog(double damage, String source, String reason, boolean useReason) 
	{
		this.damage = damage;
		this.source = source;
		this.reason = reason;
		this.useReason = useReason;
	}
}
