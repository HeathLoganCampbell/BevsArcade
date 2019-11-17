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
import org.bukkit.Material;
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
import com.bevelio.arcade.types.Kit;
import com.bevelio.arcade.utils.MathUtils;

import net.md_5.bungee.api.ChatColor;

public class KitConfig
{
	private YamlConfiguration config;
	private File configFile;
	
	public KitConfig()
	{
		config = new YamlConfiguration();
		configFile = new File(ArcadePlugin.getInstance().getDataFolder(), "kits.yml");
		
		try {
			this.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void save()
    {
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                ArcadePlugin.getInstance().saveResource("kits.yml", true);
                config = YamlConfiguration.loadConfiguration(configFile);
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
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(
                    "You have setup your translation configuration wrong. Please make sure you are properly setting up every single line"
                            + "\nProblems can be because of single quotes in the middle of the string, Not surrounding the string with single quotes."
                            + "\nFor more information, Please look up yaml configurations and how to properly do them");
        }
    }
    
	public Kit loadKit(String name)
	{
		name = name.toLowerCase();
		if(!this.config.contains("Kits." + name))
			return null;
		ConfigurationSection section = this.config.getConfigurationSection("Kits." + name);
		return this.loadKit(section);
	}
	
	private Kit loadKit(ConfigurationSection section)
	{
		String name = section.getName();
		if(section.contains("Name"))
			name = section.getString("Name");
		
		String displayName = name;
		if(section.contains("DisplayName"))
			displayName = section.getString("DisplayName");
		
		List<String> description = new ArrayList<>();
		if(section.contains("Description"))
		{
			List<String> description2 = section.getStringList("Description");
			for(String line : description2)
			{
				description.add(ChatColor.translateAlternateColorCodes('&', line));
			}
		}
		
		Kit kit = new Kit(name, displayName);
		kit.setDescription(description);
		
		if(section.contains("Slots"))
		{
			for(String slot : section.getConfigurationSection("Slots").getKeys(false))
			{
				if(!MathUtils.isNumeric(slot))
					continue;
				int slotNum = Integer.parseInt(slot);
				String itemStr = section.getString("Slots." + slot);
				ItemStack item = this.parseItem(itemStr)[0];
				kit.getItems().put(slotNum, item);
			}
		}
		
		if(section.contains("Helmet"))
		{
			String itemStr = section.getString("Helmet");
			ItemStack item = this.parseItem(itemStr)[0];
			kit.setHelmet(item);
		}
		
		if(section.contains("Chestplate"))
		{
			String itemStr = section.getString("Chestplate");
			ItemStack item = this.parseItem(itemStr)[0];
			kit.setChestplate(item);
		}
		
		if(section.contains("Leggings"))
		{
			String itemStr = section.getString("Leggings");
			ItemStack item = this.parseItem(itemStr)[0];
			kit.setLeggings(item);
		}
		
		if(section.contains("Boots"))
		{
			String itemStr = section.getString("Boots");
			ItemStack item = this.parseItem(itemStr)[0];
			kit.setBoots(item);
		}
		
		ItemStackBuilder icon = new ItemStackBuilder(Material.DIRT);
		if(section.contains("Icon"))
		{
			String itemStr = section.getString("Icon");
			ItemStack item = this.parseItem(itemStr)[0];
			if(item != null)
				icon = new ItemStackBuilder(item);
		}
		kit.setIcon(icon);
		
		List<PotionEffect> effects = new ArrayList<>();
		if(section.contains("Effects"))
		{
			for(String effectStr : (List<String>) section.getStringList("Effects"))
			{
				PotionEffect effect = this.parsePotionEffect(effectStr);
				if(effect == null) continue;
				effects.add(effect);
			}
		}
		kit.setEffects(effects);
		
		List<String> abilities = new ArrayList<>();
		if(section.contains("Abilities"))
		{
			abilities = section.getStringList("Abilities");
		}
		kit.setAbilities(abilities);
		
		int price = -1;
		if(section.contains("Price"))
		{
			price = section.getInt("Price");
		}
		kit.setPrice(price);
		
		return kit;
	}
	
	
	 public ItemStack[] parseItem(String string) {
	        if (string == null)
	            return new ItemStack[] { null };
	        String[] args = string.split(" ");
	        try {
	            double amount = Integer.parseInt(args[2]);
	            ItemStack[] items = new ItemStack[(int) Math.ceil(amount / 64)];
	            if (items.length <= 0)
	                return new ItemStack[] { null };
	            for (int i = 0; i < items.length; i++) {
	                int id = MathUtils.isNumeric(args[0]) ? Integer.parseInt(args[0])
	                        : (Material.getMaterial(args[0].toUpperCase()) == null ? Material.AIR : Material.getMaterial(args[0]
	                                .toUpperCase())).getId();
	                if (id == 0) {
	                    System.out.print("Unknown item " + args[0] + "!");
	                    return new ItemStack[] { null };
	                }
	                ItemStack item = new ItemStack(id, (int) amount, (short) Integer.parseInt(args[1]));
	                String[] newArgs = Arrays.copyOfRange(args, 3, args.length);
	                for (String argString : newArgs) {
	                    if (argString.contains("Name=")) {
	                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
	                        if (CC.getLastColors(name).equals(""))
	                            name = ChatColor.WHITE + name;
	                        ItemMeta meta = item.getItemMeta();
	                        String previous = meta.getDisplayName();
	                        if (previous == null)
	                            previous = "";
	                        meta.setDisplayName(name + previous);
	                        item.setItemMeta(meta);
	                    } else if (argString.contains("Color=") && item.getType().name().contains("LEATHER")) {
	                        String[] name = argString.substring(6).split(":");
	                        int[] ids = new int[3];
	                        for (int o = 0; o < 3; o++)
	                            ids[o] = Integer.parseInt(name[o]);
	                        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
	                        meta.setColor(Color.fromRGB(ids[0], ids[1], ids[2]));
	                        item.setItemMeta(meta);
	                    } else if (argString.equalsIgnoreCase("UniqueItem")) {
	                        ItemMeta meta = item.getItemMeta();
	                        String previous = meta.getDisplayName();
	                        if (previous == null)
	                            previous = "";
	                        meta.setDisplayName(previous + "UniqueIdentifier");
	                        item.setItemMeta(meta);
	                    }
	                    if (argString.contains("Lore=")) {
	                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
	                        ItemMeta meta = item.getItemMeta();
	                        List<String> lore = meta.getLore();
	                        if (lore == null)
	                        {
	                        	lore = new ArrayList<String>();
	                        }
	                        for (String a : name.split("\\n"))
	                        {
	                            lore.add(a);
	                        }
	                        meta.setLore(lore);
	                        item.setItemMeta(meta);
	                    }
	                }
	                
	                for (int n = 0; n < newArgs.length; n++)
	                {
	                    Enchantment ench = Enchantment.getByName(newArgs[n]);
	                    if (ench == null)
	                    {
	                        ench = Enchantment.getByName(newArgs[n].replace("_", " "));
	                    }
	                    if (ench == null)
	                    {
	                        ench = Enchantment.getByName(newArgs[n].replace("_", " ").toUpperCase());
	                    }
	                    if (ench == null)
	                    {
	                        continue;
	                    }
	                    System.out.println("New Ench added to an item " + ench.getName());
	                    item.addUnsafeEnchantment(ench, Integer.parseInt(newArgs[n + 1]));
	                    n++;
	                }
	                amount = amount - 64;
	                items[i] = item;
	            }
	            return items;
	        } catch (Exception ex) {
	            String message = ex.getMessage();
	            if (ex instanceof ArrayIndexOutOfBoundsException)
	                message = "java.lang.ArrayIndexOutOfBoundsException: " + message;
	            System.out.print("Not allowed " + string  + " " + message);
	            ex.printStackTrace();
	        }
	        return new ItemStack[] { null };
	    }
	
	private PotionEffect parsePotionEffect(String potionStr)
	{
		if(potionStr == null)
		{
			return null;
		}
		String[] args = potionStr.split(" ");
		
		try 
		{
			PotionEffectType potType = MathUtils.isNumeric(args[0]) ? PotionEffectType.getById(Integer.parseInt(args[0])) : PotionEffectType.getByName(args[0].toUpperCase());
			int seconds = Integer.parseInt(args[1]) * 20;
			int lvl = Integer.parseInt(args[2]);
			boolean hideEffect = false;
			if(args.length >= 4)
			{
				hideEffect = Boolean.getBoolean(args[3]);
			}
			return new PotionEffect(potType, seconds, lvl, hideEffect);
		}  
		catch (Exception ex)
		{
			String message = ex.getMessage();
	      	if (ex instanceof ArrayIndexOutOfBoundsException)
	      	{
	      		message = "java.lang.ArrayIndexOutOfBoundsException: " + message;
	      		System.out.print("Not allowed " + potionStr  + " " + message);
	      	}
	            ex.printStackTrace();
	            
		}
		return null;
	}
}
