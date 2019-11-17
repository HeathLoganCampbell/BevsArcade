package com.bevelio.arcade.managers;

import com.bevelio.arcade.configs.BaseConfig;
import com.bevelio.arcade.configs.files.DeathMessagesConfig;
import com.bevelio.arcade.configs.files.GameMasterConfig;
import com.bevelio.arcade.configs.files.LobbyConfig;
import com.bevelio.arcade.configs.files.MainConfig;
import com.bevelio.arcade.configs.files.SpecialLocationsConfig;
import com.bevelio.arcade.configs.files.TranslationConfig;

import lombok.Data;

@Data
public class ConfigManager 
{
	private LobbyConfig lobbyConfig = new LobbyConfig();
	private MainConfig mainConfig = new MainConfig();
	private TranslationConfig translationConfig = new TranslationConfig();
	private DeathMessagesConfig deathMessagesConfig = new DeathMessagesConfig();
	private GameMasterConfig gameMasterConfig = new GameMasterConfig();
	private SpecialLocationsConfig specialLocationsConfig = new SpecialLocationsConfig();
	
	public ConfigManager()
	{
		loadConfigs();
	}
	
	public void loadConfigs()
	{
		for (BaseConfig config : new BaseConfig[] { deathMessagesConfig, lobbyConfig, mainConfig, translationConfig, gameMasterConfig }) 
		{
			try {
				config.load();
				config.loadConfig();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
