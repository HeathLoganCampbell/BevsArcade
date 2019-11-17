package com.bevelio.arcade.managers;

import java.io.File;

import com.bevelio.arcade.ArcadePlugin;

public class ExternalGameManager 
{
	public ExternalGameManager()
	{
		File file = new File(ArcadePlugin.getInstance().getDataFolder(), "externals");
		if(!file.exists())
			file.mkdirs();
	}
}
