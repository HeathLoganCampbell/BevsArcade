package com.bevelio.arcade.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager.GameSummary;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.module.display.Page;
import com.bevelio.arcade.types.Kit;
import com.bevelio.arcade.utils.MathUtils;

public class KitSelector extends Page
{
	private int numberOfSlots = 45;
	private int pageNum;
	private HashMap<UUID, Long> cooldown = new HashMap<>();

	public KitSelector(String name, int pageNum, Display display)
	{
		super(name, display);
		this.pageNum = pageNum;
	}

	@Override
	public void init()
	{
		Game game = ArcadePlugin.getInstance().getGameManager().getNextGame();
		if(game == null) return;
		HashMap<String, Kit> kits = game.getLoadedKits();
		List<String> kitNames = new ArrayList<String>(kits.keySet());
		Collections.sort(kitNames);
		
		int pages = MathUtils.ceil(kits.size() / numberOfSlots);
		int offset = (pageNum * numberOfSlots);
		for(int slot = 0; slot < numberOfSlots; slot++)
		{
			if(kits.size() <= slot + offset) continue;
			String kitName = kitNames.get(slot + offset);
			Kit kit = kits.get(kitName);
			if(kit == null) continue;
			this.setIcon(slot, kit.getIcon().displayName(kit.getDisplayName()).lore(kit.getDescription()));
			this.setClickable(slot, (clickLog) -> 
			{
				if(clickLog.getDisplay().getIdName() != display.getIdName())
					return;
				if(!clickLog.getDisplay().getInvetory().getName().contains(this.display.getInvetory().getName()))
					return;
				if(cooldown.get(clickLog.getPlayer().getUniqueId()) != null 
						&&  System.currentTimeMillis() - cooldown.get(clickLog.getPlayer().getUniqueId()) < 400 )
					return;
				Player player = clickLog.getPlayer();
				if(player.hasPermission("bevelioarcade.kit." + kitName.toLowerCase()) || player.isOp())
				{
					cooldown.put(clickLog.getPlayer().getUniqueId(), System.currentTimeMillis());
					game.setPlayersKit(player.getUniqueId(), kitName);
					player.sendMessage(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getSelectorKitClickMsg().replaceAll("%Kit%", kit.getDisplayName()));
				} else
					player.sendMessage(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getCommandKitMessageYouDontHaveKit().replaceAll("%Kit%", kit.getDisplayName()));
			});
		}
		
		this.setIcon(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15));
		if(pageNum != 0)
		{
			this.setIcon(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 13));
			this.setClickable(45, (clickLog) -> 
			{
				display.setPage(new KitSelector(this.getName(), pageNum - 1, display));
				display.update(clickLog.getPlayer());
			});
		}
		
		this.setIcon(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15));
		if(pageNum < pages)
		{
			this.setIcon(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 13));
			this.setClickable(53, (clickLog) -> 
			{
				display.setPage(new KitSelector(this.getName(), pageNum + 1, display));
				display.update(clickLog.getPlayer());
			});
		}
	}
}
