package com.bevelio.arcade.managers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.Decompress;
import com.bevelio.arcade.utils.MathUtils;
import com.bevelio.arcade.utils.ServerUtils;

import lombok.Getter;
import lombok.Setter;

public class WorldCreatorManager 
{
	private @Setter @Getter boolean stopWorldUnload = true;
	
	public WorldCreatorManager()
	{
		
	}
	
	public World createNewWorld(String worldName, String outputWorld)
	{
//		System.out.println("Creating world");
		File selectedWorld = null;
		
		if(worldName != null)
			selectedWorld = this.getWorld(worldName);
		if(selectedWorld == null)
		{
			List<File> files = this.fetchWorlds();
			if(files == null || files.size() == 0)
				return null;
			File file = files.get(MathUtils.random(files.size() - 1));
			selectedWorld = file;
		}
		
		outputWorld = getFileWorld(outputWorld);
		
		File worldFile = new File(outputWorld);
		
		if(Bukkit.getWorld(outputWorld) != null)
			deleteWorld(outputWorld);
		
		try 
		{
			new Decompress(selectedWorld.getAbsolutePath(), worldFile.getAbsolutePath()).unzipSpeed();
		} 
			catch (IOException e)
		{
			e.printStackTrace();
		}
		
		World world = ServerUtils.createWorld(new WorldCreator(outputWorld)
				.generator(new ChunkGenerator() 
				{
					@Override
					public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
					{
						return new byte[world.getMaxHeight() >> 4][];
					}
				}
			));
		
		world.setAutoSave(false);
		
		return world;
		//TODO load world data
	}
	
	public String getFileWorld(String outputFileName)
	{
		if(outputFileName == null)
			return ArcadePlugin.getInstance().getDataFolder().getPath() + File.separator + "activeGames" + File.separator;
		return ArcadePlugin.getInstance().getDataFolder().getPath() + File.separator + "activeGames" + File.separator + outputFileName;
	}
	
	public void deleteWorld(String worldName)
	{
//		System.out.println("Deleteing the world...");
//		System.out.println("Delete " + worldName);
		World world = Bukkit.getWorld(worldName);
		if(world != null)
		{
			for(Entity en : world.getEntities())
			{
				if(!(en instanceof Player))
				{
					en.remove();
				} else
					((Player) en).kickPlayer(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getWorldErrorKickPlayersOnWorld());
			}
			
			
		}
		stopWorldUnload = false;
		ServerUtils.unloadWorld(worldName);
		File worldFile = new File(worldName);
		if(worldFile.exists())
		{
			try 
			{
				FileUtils.deleteDirectory(worldFile);
			} 
				catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		stopWorldUnload = true;
		
//		System.out.println("Deleted the world!");
	}
	
	public void deleteWorld(World world)
	{
//		System.out.println("Deleteing the world...");
//		System.out.println("Delete " + worldName);
		if(world != null)
		{
			for(Entity en : world.getEntities())
			{
				if(!(en instanceof Player))
				{
					en.remove();
				} else
					((Player) en).kickPlayer(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getWorldErrorKickPlayersOnWorld());
			}
		}
		ServerUtils.unloadWorld(world.getName());
		File worldFile = new File(world.getName());
		if(worldFile.exists())
		{
			try 
			{
				FileUtils.deleteDirectory(worldFile);
			} 
				catch (IOException e)
			{
				e.printStackTrace();
			}
		}
//		System.out.println("Deleted the world!");
	}
	
	public File getWorld(String worldName)
	{
		List<File> files = this.fetchWorlds();
		if(files == null) 
			return null;
		for(File file : files)
			if(file.getName().toLowerCase().contains(worldName.toLowerCase()))
				return file;
		return null;
	}
	
	public List<File> fetchWorlds() 
	{
		File pluginsFolder = ArcadePlugin.getInstance().getDataFolder();
		File mapsFolder = new File(pluginsFolder, "maps");
		
		if(!mapsFolder.exists() || mapsFolder.list().length == 0)
		{
			ServerUtils.log(CC.red + "Bevelio's Arcade ] No Maps found in '/plugins/Arcade/maps' !");
			ServerUtils.log(CC.red + "Bevelio's Arcade ] Thus the plugin will be disabled!");
//			Bukkit.getPluginManager().disablePlugin(ArcadePlugin.getInstance());
			return null;
		}
		
		List<File> filesInWorldsFolder = Arrays.asList(mapsFolder.listFiles());
		
		Predicate<File> isZipFile = (file) -> file.getAbsolutePath().contains(".zip");
		
		return filesInWorldsFolder.stream()
								  .filter(isZipFile)
								  .collect(Collectors.toList());
	}
}
