package com.bevelio.arcade.configs.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.BaseConfig;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.utils.ItemUtils;
import com.bevelio.arcade.utils.MathUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Data
@EqualsAndHashCode(callSuper=false)
public class LobbyConfig extends BaseConfig
{
	private int defaultLobbySeconds = 60;
	private boolean simpleLobbyProtection = true;
	private Location spawnLocation = new Location(Bukkit.getWorlds().get(0), 0.5, 120, 0.5);
	
	private boolean plateformGenerate = false;
	private ItemStack plateformFloorBlocks = new ItemStack(Material.IRON_BLOCK, 1);
	private ItemStack plateformWallsBlocks = new ItemStack(Material.GLASS, 1);
	private int plateformRadius = 10;
	
	private boolean disableItemDropping = true;
	private boolean disableRain = true;
	
	public String scoreboardHeader = CC.bAqua + "BevsArcade";
	public String lobbyOpenKitMenu =  Material.BEACON.name();
	public String lobbyOpenTeamMenu =  Material.LEATHER_CHESTPLATE.name();
	public String[] scoreboardLines = {
			"",
			CC.aqua + "Next Game",
			CC.gray + "%Next_Game%",
			"",
			CC.aqua + "Players",
			CC.gray + "%Player_Playing%",
			"",
			CC.aqua + "Starting in",
			CC.gray + "%Starting_In%",
			"",
			CC.bold + "Arcade.Bevelio.Com"
	};
	
	public String scoreboardWaitingForPlayers = CC.gray + "Waiting for players";
	public String lobbySlotOne = "LEATHER_CHESTPLATE 0 1 Name=&CTeam_Selector";
	public String lobbySlotTwo = "BEACON 0 1 Name=&CKit_Selector";
	public String lobbySlotThree = "AIR 0 1";
	public String lobbySlotFour = "AIR 0 1";
	public String lobbySlotFive = "AIR 0 1";
	public String lobbySlotSix = "AIR 0 1";
	public String lobbySlotSeven = "AIR 0 1";
	public String lobbySlotEight = "AIR 0 1";
	public String lobbySlotNine = "AIR 0 1";
	
	public @Getter HashMap<Integer, ItemStackBuilder> lobbyItemSet = new HashMap<>();
	
	public void addToItemSet(Integer slot, String itemStr)
	{
		
		if(itemStr.startsWith("AIR")) return;
		ItemStack[] item = ItemUtils.parseItem(itemStr);
		if(item[0] == null) return;
		ItemStackBuilder itemBuilder = new ItemStackBuilder(item[0]);
		this.lobbyItemSet.put(slot, itemBuilder);
	}
	
	@Override
	public void loadConfig()
	{
		super.loadConfig();
		this.addToItemSet(0, this.lobbySlotOne);
		this.addToItemSet(1, this.lobbySlotTwo);
		this.addToItemSet(2, this.lobbySlotThree);
		this.addToItemSet(3, this.lobbySlotFour);
		this.addToItemSet(4, this.lobbySlotFive);
		this.addToItemSet(5, this.lobbySlotSix);
		this.addToItemSet(6, this.lobbySlotSeven);
		this.addToItemSet(7, this.lobbySlotEight);
		this.addToItemSet(8, this.lobbySlotNine);
	}
	 
	public LobbyConfig() 
	{
		super("lobby");
		this.dontSave("lobbyItemSet");
	}
}
