package com.bevelio.hooks.wrappers.wrapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.hooks.wrappers.interfaces.IDatabase;
import com.bevelio.hooks.wrappers.records.BevsPlayer;

public class FlatFileDatabase implements IDatabase
{
	private YamlConfiguration config;
	private File configFile;
	private HashMap<UUID, BevsPlayer> player = new HashMap<>();

	@Override
	public void connectDatabase()
	{
		config = new YamlConfiguration();
		configFile = new File(ArcadePlugin.getInstance().getDataFolder(), "data" + File.separator + "datbase.yml");
	}

	@Override
	public void createDatabase() 
	{
		if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void registerPlayer(BevsPlayer player) 
	{
		this.player.put(player.getUuid(), player);
	}

	@Override
	public void updatePlayer(BevsPlayer player)
	{
		for (Field field : getClass().getDeclaredFields()) 
		{
			field.setAccessible(true);
			
			try {
                   Object playerField = field.get(player);
//                   playerField
			}
				catch(Exception e)
			{
				e.printStackTrace();
			}
			
			field.setAccessible(false);
		}
	}

	@Override
	public BevsPlayer getPlayer(UUID uuid)
	{
		return null;
	}

	@Override
	public BevsPlayer getPlayer(String name) 
	{
		return null;
	}

	@Override
	public boolean containsPlayer(UUID uuid) 
	{
		return false;
	}

	@Override
	public boolean containsPlayer(String name)
	{
		return false;
	}

	@Override
	public void unregisterPlayer(UUID uuid)
	{
		this.player.remove(uuid);
	}
}

//	Players.
//		<UUID>
//			.name: Bevelio
//			.wins
//				.spleef: 5
//				.oitc
//			.coins: 900
//			.kills: 10
//			.deaths: 50
//			
//
//
//