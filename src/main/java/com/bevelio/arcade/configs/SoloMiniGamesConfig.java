package com.bevelio.arcade.configs;

import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.oitc.OITC;
import com.bevelio.arcade.games.oitc.config.OITCConfig;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SoloMiniGamesConfig extends MiniGamesConfig
{
	
	public SoloMiniGamesConfig(SoloGame soloGame)
	{
		super(soloGame);
	}
	
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
	}
}