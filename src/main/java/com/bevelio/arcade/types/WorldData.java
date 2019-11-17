package com.bevelio.arcade.types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.misc.XYZ;
import com.bevelio.arcade.utils.ItemUtils;
import com.bevelio.arcade.utils.ServerUtils;

public class WorldData
{
	
	protected YamlConfiguration config;
    private File configFile;

	public World world;
	public String name = "unknown";
	public String defaultKit = "none";
	public List<String> authors = Arrays.asList("Unknown");
	public String gameType = "dummy";
	public int maxPlayers = 100;
	public int maxSeconds = 600; //10 minutes
	public int worldTime = -1;//null
	public XYZ spectatorSpawn = new XYZ(0, 64, 0);
	public boolean buildMode = false;
	
	public List<String> kits = new ArrayList<>();
	public List<String> microComponents = new ArrayList<>();
	public HashMap<String, Team> teams = new HashMap<>();
	public HashMap<String, String> data = new HashMap<>();
	public HashMap<String, List<XYZ>> customs = new HashMap<>();
	

	public WorldData(String worldName) 
	{
		config = new YamlConfiguration();
		configFile = new File(worldName, "config.yml");
	}
	
	public WorldData(String worldName, boolean isBuildMode) 
	{
		config = new YamlConfiguration();
		configFile = new File(worldName, "config.yml");
		buildMode = isBuildMode;
	}
	
	
	public String getAuthors(boolean simple)
	{
		StringBuilder str = new StringBuilder();
		if(simple)
		{
			str.append(this.authors.get(0));
			str.append(" + ");
			if(this.authors.size() > 1)
				str.append(this.authors.size() - 1);
		} else {
			for(int i = 0; i < authors.size(); i++)
			{
				String author = authors.get(i);
				str.append((i != 0 ? ", " : "") + author);
			}
		}
		return str.toString();
	}
	
	public String getAuthors()
	{
		return this.getAuthors(this.authors.size() > 4);
	}
	
	 public void save()
	 {
		 try {
			 if (!configFile.exists())
			 {
				 configFile.getParentFile().mkdirs();
				 configFile.createNewFile();
			 }
			 config.save(configFile);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	}
	
	public void load() throws Exception {
        try {
            if (!configFile.exists())
                save();
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(
                    "You have setup your translation configuration wrong. Please make sure you are properly setting up every single line"
                            + "\nProblems can be because of single quotes in the middle of the string, Not surrounding the string with single quotes."
                            + "\nFor more information, Please look up yaml configurations and how to properly do them");
        }
    }
	
	public void loadConfig()
	{
		if(config.contains("Name"))
			this.name = config.getString("Name");
		
		if(config.contains("Authors"))
			this.authors = config.getStringList("Authors");
		
		if(config.contains("GameType"))
			this.gameType = config.getString("GameType");
		
		if(config.contains("DefaultKit"))
			this.defaultKit = config.getString("DefaultKit").toLowerCase();
		
		if(config.contains("MaxPlayers"))
			this.maxPlayers = config.getInt("MaxPlayers");
		
		if(config.contains("MaxSeconds"))
			this.maxSeconds = config.getInt("MaxSeconds");
		
		if(config.contains("Kits"))
			this.kits = config.getStringList("Kits");
		
		if(config.contains("SpectatorSpawn"))
		{
			String specSpawnStr = config.getString("SpectatorSpawn");
			XYZ xyz = new XYZ();
			xyz.deserialize(specSpawnStr);
			this.spectatorSpawn = xyz;
		}  else {
			this.spectatorSpawn = new XYZ(0, 90, 0);
		}
		
		if(config.contains("WorldTime"))
			this.worldTime = config.getInt("WorldTime");
		
		if(config.contains("Customs"))
			for(String parent : config.getConfigurationSection("Customs").getKeys(false))
				loadCustoms(config.getConfigurationSection("Customs." + parent));
		
		if(config.contains("Datas"))
			for(String parent : config.getConfigurationSection("Datas").getKeys(false))
				loadDatas(config.getConfigurationSection("Datas." + parent));
		
		if(config.contains("MicroComponents"))
			this.microComponents = config.getStringList("MicroComponents");
		
		if(config.contains("Teams"))
		{
			for(String parent : config.getConfigurationSection("Teams").getKeys(false))
			{
				Team team = loadTeam(config.getConfigurationSection("Teams." + parent));
				teams.put(team.getName(), team);
			}
		} else {
			ServerUtils.log(CC.red + "It seems like you have failed to create a team in the map '" + this.name + "'!" );
			ServerUtils.log(CC.red + "So plugin will be disabled!" );
			Bukkit.getPluginManager().disablePlugin(ArcadePlugin.getInstance());
		}
	}
	
	private void loadDatas(ConfigurationSection section)
	{
		String value = section.getString("Value");
		String name  = section.getName();
		this.data.put(name, value);
	}

	public void loadCustoms(ConfigurationSection section)
	{
		 List<String> customListStr = section.getStringList("Values");
		 List<XYZ> customList = new ArrayList<>();
		 for(String LocationLine : customListStr)
		 {
			 XYZ xyz = new XYZ();
			 xyz.deserialize(LocationLine);
			 customList.add(xyz);
		 }
		 
		 String customName = section.getName();
		 this.customs.put(customName, customList);
	}
	
	//Teams
	//	
	public Team loadTeam(ConfigurationSection section)
	{
		String name = section.getName();
		if(name == null)
			name = section.getString("Name");
		System.out.println("Creating new team " + name);
		
		ChatColor color = ChatColor.valueOf(section.getString("ChatColor").toUpperCase());
		
		Team team = new Team(name, color, this.buildMode);
		
		for(String str : section.getStringList("Spawns"))
		{
			System.out.println("Spawn point found for " + name);
//			if(!XYZ.isXYZObj(str)) continue;
			XYZ xyz = new XYZ();
			xyz.deserialize(str);
			team.addSpawn(xyz);
			System.out.println("Spawn point added to " + name + " [" + xyz.x + ", " + xyz.y + ", " + xyz.z + "]");
		}
		
		if(section.contains("BlockedKits"))
		{
			team.setBlockedKits((String[]) section.getList("BlockedKits").toArray());
		}
		
		if(section.contains("DisplayName"))
		{
			team.setDisplayName(section.getString("DisplayName"));
		}
		
		if(section.contains("Icon"))
		{
			ItemStack item = ItemUtils.parseItem(section.getString("Icon"))[0];
			team.setIcon(new ItemStackBuilder(item));
		}
		
		return team;
	}
	
	public void saveSettings()
	{
		this.config.set("Name", this.name);
		this.config.set("Authors", this.authors);
		this.config.set("GameType", this.gameType);
		this.config.set("MaxPlayers", this.maxPlayers);
		this.config.set("MaxSeconds", this.maxSeconds);
		this.config.set("SpectatorSpawn", this.spectatorSpawn.serialize());
		this.config.set("DefaultKit", this.defaultKit);
		this.config.set("Kits", this.kits);
	}
	
	public void saveTeam(Team team)
	{
		String name = team.getName();
		this.config.set("Teams." + name + ".Name", name);
		this.config.set("Teams." + name + ".ChatColor", team.getPrefix().name());
		
		List<String> xyzList = new ArrayList<>();
		for(XYZ xyz : team.getSpawns())
			xyzList.add(xyz.serialize());
		
		this.config.set("Teams." + name + ".Spawns", xyzList);
		
		if(team.getIcon() != null)
			this.config.set("Teams." + name + ".Icon", team.getIcon().serialize());
	}
	
	public void saveCustom(String name, List<XYZ> list)
	{
		ArrayList<String> strList =  (ArrayList<String>) list.stream().map(XYZ::serialize).collect(Collectors.toList());
		this.config.set("Customs." + name + ".Values", strList);
	}
}
