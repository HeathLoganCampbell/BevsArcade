package com.bevelio.arcade.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.managers.GameManager.GameSummary;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.module.display.Page;
import com.bevelio.arcade.utils.MathUtils;

public class BuildPage extends Page
{
	private static List<GameSummary> gameSummarizes;
	private int numberOfSlots = 45;
	private int pageNum;

	public BuildPage(String name, int pageNum, Display display)
	{
		super(name, display);
		this.pageNum = pageNum;
		if(gameSummarizes == null)
			gameSummarizes = ArcadePlugin.getInstance().getGameManager().getGameSummarizes().entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
	}

	@Override
	public void init()
	{
		int pages = MathUtils.ceil(gameSummarizes.size() / numberOfSlots);
		int offset = (pageNum * numberOfSlots);
		for(int slot = 0; slot < numberOfSlots; slot++)
		{
			if(gameSummarizes.size() <= slot + offset) continue;
			GameSummary gameSummary = gameSummarizes.get(slot + offset);
			if(gameSummary == null) continue;
			this.setIcon(slot, gameSummary.icon.displayName(CC.aqua + gameSummary.displayName));
		}
		
		this.setIcon(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15));
		if(pageNum != 0)
		{
			this.setIcon(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 13));
			this.setClickable(45, (clickLog) -> 
			{
				display.setPage(new BuildPage(this.getName(), pageNum - 1, display));
				display.update(clickLog.getPlayer());
			});
		}
		
		this.setIcon(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15));
		if(pageNum < pages)
		{
			this.setIcon(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 13));
			this.setClickable(53, (clickLog) -> 
			{
				display.setPage(new BuildPage(this.getName(), pageNum + 1, display));
				display.update(clickLog.getPlayer());
			});
		}
	}
}
