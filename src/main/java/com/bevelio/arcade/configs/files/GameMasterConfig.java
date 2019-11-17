package com.bevelio.arcade.configs.files;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.configs.BaseConfig;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.utils.ItemUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper=false)
public class GameMasterConfig extends BaseConfig
{

	public String lobbyOpenGameMasterMenu =  Material.WATCH.name();
	
	public String lobbySlotOneForOps = "LEATHER_CHESTPLATE 0 1 Name=&CTeam_Selector";
	public String lobbySlotTwoForOps = "BEACON 0 1 Name=&CKit_Selector";
	public String lobbySlotThreeForOps = "AIR 0 1";
	public String lobbySlotFourForOps = "AIR 0 1";
	public String lobbySlotFiveForOps = "WATCH 0 1 Name=&CGameMaster_Tool";
	public String lobbySlotSixForOps = "AIR 0 1";
	public String lobbySlotSevenForOps = "AIR 0 1";
	public String lobbySlotEightForOps = "AIR 0 1";
	public String lobbySlotNineForOps = "AIR 0 1";
	
	public @Getter HashMap<Integer, ItemStackBuilder> lobbyItemSetForOps = new HashMap<>();
	
	public void addToItemSet(Integer slot, String itemStr)
	{
		
		if(itemStr.startsWith("AIR")) return;
		ItemStack[] item = ItemUtils.parseItem(itemStr);
		if(item[0] == null) return;
		ItemStackBuilder itemBuilder = new ItemStackBuilder(item[0]);
		this.lobbyItemSetForOps.put(slot, itemBuilder);
	}
	
	@Override
	public void loadConfig()
	{
		super.loadConfig();
		this.addToItemSet(0, this.lobbySlotOneForOps);
		this.addToItemSet(1, this.lobbySlotTwoForOps);
		this.addToItemSet(2, this.lobbySlotThreeForOps);
		this.addToItemSet(3, this.lobbySlotFourForOps);
		this.addToItemSet(4, this.lobbySlotFiveForOps);
		this.addToItemSet(5, this.lobbySlotSixForOps);
		this.addToItemSet(6, this.lobbySlotSevenForOps);
		this.addToItemSet(7, this.lobbySlotEightForOps);
		this.addToItemSet(8, this.lobbySlotNineForOps);
	}
	
	public GameMasterConfig()
	{
		super("gamemaster");
		this.dontSave("lobbyItemSet");
	}
}