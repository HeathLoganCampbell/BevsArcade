package com.bevelio.arcade.managers;

import org.bukkit.event.Listener;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.pages.BuildPage;
import com.bevelio.build.BuildCore;

import lombok.Getter;

public class BuildManager implements Listener
{
	private Display buildDisplay;
	private @Getter BuildCore buildCore;
	
	public BuildManager()
	{
		this.buildCore = new BuildCore();
		
//		this.buildDisplay = new Display("builder", null);
//		this.buildDisplay.setPage(new BuildPage("Select a gametype.", 0, this.buildDisplay));
	}
	
//	public Display getBuilderDisplay()
//	{
//		return this.buildDisplay;
//	}
	
}
