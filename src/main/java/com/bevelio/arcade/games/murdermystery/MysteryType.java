package com.bevelio.arcade.games.murdermystery;

public enum MysteryType 
{
	INNOCENT, DETECTIVE, MURDER;
	
	String displayName;
	
	MysteryType()
	{
		this.displayName = this.name().toLowerCase();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
