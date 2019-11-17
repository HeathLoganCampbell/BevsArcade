package com.bevelio.arcade.types;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

public enum GameState 
{
	LOADING(-1),
	WAITING(-1),
	STARTING(61),
	PREGAME(5),
	LIVE(70),
	FINISHING(9),
	ENDED(-1);

	private @Getter @Setter int seconds;
	private @Getter @Setter String displayName;
	
	@SuppressWarnings("deprecation")
	private GameState(int seconds) {
		this.seconds = seconds;
		this.displayName = StringUtils.capitalise(this.name().toLowerCase());
	}
	
	public boolean isTimable()
	{
		return this.getSeconds() != -1;
	}
}
