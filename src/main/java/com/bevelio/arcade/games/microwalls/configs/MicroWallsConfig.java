package com.bevelio.arcade.games.microwalls.configs;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.configs.TeamMiniGamesConfig;
import com.bevelio.arcade.games.microwalls.MicroWalls;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.utils.ItemUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MicroWallsConfig extends TeamMiniGamesConfig 
{
	private double wallsDropInSeconds = 10;
	private boolean teamHotbarSymbolEnabled = true;
	
	private int teamHotbarSymbolSlot = 8;

	private String redTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=255:00:00";
	private String blueTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=00:00:255";
	private String greenTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=00:255:00";//RGB
	private String yellowTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=255:255:00";
	private String goldTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=255:255:00";
	private String whiteTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=255:255:255";
	private String blackTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=00:00:00";
	private String grayTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=128:128:128";
	private String purpleTeamHotbarSymbol = "LEATHER_CHESTPLATE 0 1 Color=128:00:128";
	
	private HashMap<ChatColor, ItemStackBuilder> teamHotbarSymbols = new HashMap<ChatColor, ItemStackBuilder>();
	
	public void addTeamHotbarSymbol(ChatColor chatColor, String itemName)
	{
		ItemStack[] items = ItemUtils.parseItem(itemName);
		if(items[0] == null) return;
		ItemStackBuilder itemBuilder = new ItemStackBuilder(items[0]);
		this.teamHotbarSymbols.put(chatColor, itemBuilder);
	}
	
	public MicroWallsConfig(MicroWalls game) 
	{
		super(game);
		this.dontSave("teamHotbarSymbols");
	}
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
        addTeamHotbarSymbol(ChatColor.RED, this.redTeamHotbarSymbol);
        addTeamHotbarSymbol(ChatColor.DARK_RED, this.redTeamHotbarSymbol);
        
        addTeamHotbarSymbol(ChatColor.BLACK, this.blackTeamHotbarSymbol);
        
        addTeamHotbarSymbol(ChatColor.BLUE, this.blueTeamHotbarSymbol);
        addTeamHotbarSymbol(ChatColor.AQUA, this.blueTeamHotbarSymbol);
        addTeamHotbarSymbol(ChatColor.DARK_BLUE, this.blueTeamHotbarSymbol);
        
        addTeamHotbarSymbol(ChatColor.DARK_GREEN, this.greenTeamHotbarSymbol);
        addTeamHotbarSymbol(ChatColor.GREEN, this.greenTeamHotbarSymbol);
        
        addTeamHotbarSymbol(ChatColor.YELLOW, this.yellowTeamHotbarSymbol);
        
        addTeamHotbarSymbol(ChatColor.WHITE, this.whiteTeamHotbarSymbol);
        
        addTeamHotbarSymbol(ChatColor.GOLD, this.goldTeamHotbarSymbol);
        
        addTeamHotbarSymbol(ChatColor.DARK_PURPLE, this.purpleTeamHotbarSymbol);//
        addTeamHotbarSymbol(ChatColor.LIGHT_PURPLE, this.purpleTeamHotbarSymbol);//
        
        addTeamHotbarSymbol(ChatColor.DARK_GRAY, this.grayTeamHotbarSymbol);//
        addTeamHotbarSymbol(ChatColor.GRAY, this.grayTeamHotbarSymbol);//
        
	}
}
