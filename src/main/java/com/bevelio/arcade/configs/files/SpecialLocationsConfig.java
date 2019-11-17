package com.bevelio.arcade.configs.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.misc.XYZ;
import com.bevelio.arcade.types.Kit;
import com.bevelio.arcade.utils.MathUtils;

import net.md_5.bungee.api.ChatColor;

public class SpecialLocationsConfig
{
	private YamlConfiguration config;
	private File configFile;
	private boolean newFile = false;
	private List<Sign> signs;
	
	public SpecialLocationsConfig()
	{
		config = new YamlConfiguration();
		configFile = new File(ArcadePlugin.getInstance().getDataFolder(), "data" + File.separator + "speciallocations.yml");
		
		try {
			this.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.signs = this.loadSign();
	}
	
    public void save()
    {
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
//                ArcadePlugin.getInstance().saveResource("speciallocations.yml", true);
                config = YamlConfiguration.loadConfiguration(configFile);
                newFile = true;
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void load() throws Exception
    {
        try {
            if (!configFile.exists())
                save();
            else
                newFile = false;
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(
                    "You have setup your translation configuration wrong. Please make sure you are properly setting up every single line"
                            + "\nProblems can be because of single quotes in the middle of the string, Not surrounding the string with single quotes."
                            + "\nFor more information, Please look up yaml configurations and how to properly do them");
        }
    }
    
    public List<Sign> getSigns()
    {
    	return this.signs;
    }
    
    public void saveSign(Location location) 
    {
    	XYZ xyz = new XYZ(location);
    	ArrayList<String> locations = new ArrayList<>();
    	if(this.config.contains("SignLocations"))
    		locations = (ArrayList<String>) config.getStringList("SignLocations");
    	locations.add(xyz.serialize());
    	this.config.set("SignLocations", locations);
    	this.save();
    }
    
    public void deleteSign(Location location) 
    {
    	XYZ xyz = new XYZ(location);
    	ArrayList<String> locations = new ArrayList<>();
    	if(this.config.contains("SignLocations"))
    		locations = (ArrayList<String>) config.getStringList("SignLocations");
    	locations.remove(xyz.serialize());
    	this.config.set("SignLocations", locations);
    	this.save();
    }
    
    public List<Sign> loadSign() 
    {
    	ArrayList<String> locationStr = new ArrayList<>();
    	ArrayList<Sign> signs = new ArrayList<>();
    	if(this.config.contains("SignLocations"))
    		locationStr = (ArrayList<String>) config.getStringList("SignLocations");
    	
    	for(String str : locationStr)
    	{
    		XYZ xyz = new XYZ();
    		xyz.deserialize(str);
    		if(xyz.getWorld() == null) 
    			continue;
    		Location loc = xyz.toLocation(xyz.getWorld());
    		Block block = loc.getBlock();
    		if(!block.getType().name().contains("SIGN"))
    			continue;
    		Sign signState = (Sign) block.getState();
    		signs.add(signState);
    	}
    	
    	this.signs = signs;
    	return signs;
    }
}
