package com.bevelio.arcade.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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
import com.bevelio.arcade.types.Team;
import com.bevelio.arcade.utils.MathUtils;

public class TeamSelector extends Page
{
	private int numberOfSlots = 45;
	private int pageNum;
	private HashMap<UUID, Long> cooldown = new HashMap<>();

	public TeamSelector(String name, int pageNum, Display display)
	{
		super(name, display);
		this.pageNum = pageNum;
	}

	@Override
	public void init()
	{
		Game game = ArcadePlugin.getInstance().getGameManager().getNextGame();
		if(game == null) return;
		HashMap<String, Team> teams = game.getTeams();
		List<String> teamNames = new ArrayList<String>(teams.keySet());
		Collections.sort(teamNames);
		
		int pages = MathUtils.ceil(teams.size() / numberOfSlots);
		int offset = (pageNum * numberOfSlots);
		for(int slot = 0; slot < numberOfSlots; slot++)
		{
			if(teamNames.size() <= slot)
				continue;
			String teamName = teamNames.get(slot + offset);
			Team team = game.getTeam(teamName);
			if(team == null) continue;
			ItemStackBuilder itemBuilder = team.getIcon().displayName(team.getDisplayName());
			if(itemBuilder.getType() == Material.WOOL)
			{
				itemBuilder.durability(team.getDyeColor().getWoolData());
			}
			
			this.setIcon(slot, itemBuilder);
			this.setClickable(slot, (clickLog) -> 
			{
				if(clickLog.getDisplay().getIdName() != display.getIdName())
					return;
				if(!clickLog.getDisplay().getInvetory().getName().contains(this.display.getInvetory().getName()))
					return;
				Player player = clickLog.getPlayer();
				if(cooldown.get(clickLog.getPlayer().getUniqueId()) != null 
						&&  System.currentTimeMillis() - cooldown.get(clickLog.getPlayer().getUniqueId()) < 800 )
					return;
				cooldown.put(clickLog.getPlayer().getUniqueId(), System.currentTimeMillis());
				
				List<UUID> members = game.getPlayersPrefTeams().get(teamName);
				if(members != null)
				{
					if(members.contains(player.getUniqueId()))
					{
						String message = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getTeamPreferenceYouAreAlreadyOnThatTeam();
						message = message.replaceAll("%Team%", teamName)
										 .replaceAll("%Team_Color%", team.getPrefix() + "");
						player.sendMessage(message);
						return;
					}
				}
				
				String minTeamName = null;
				int minTeamSize = 0;
				
				int teamSize = game.getPrefernceTeamSize(teamName) + 1;
				
				for(Entry<String, Team> entry : teams.entrySet())
				{
					if(minTeamName == null || minTeamSize > entry.getValue().size())
					{
						minTeamName = entry.getKey();
						minTeamSize = game.getPrefernceTeamSize(minTeamName);
					}
				}
				 
				boolean legal = true;
				int diff = teamSize - minTeamSize;
				
//				Bukkit.broadcastMessage("Diff: " + diff + ", Game Allow Diff:" + game.allowedTeamDifference);
//				Bukkit.broadcastMessage("Min: " + minTeamName + ", Your Team: " + teamName);
//				Bukkit.broadcastMessage("Min: " + minTeamSize + ", Your Team: " + teamSize);
								
				if(game.allowedTeamDifference != -1)
					if(diff > game.allowedTeamDifference)
						legal = false;
				
				String message = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getTeamPreferenceSelected();
				if(legal)
					game.setPrefernceTeam(teamName, player);
				else
				{
					message = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getTeamPreferenceIllegal();
				}
				message = message.replaceAll("%Team%", teamName)
								 .replaceAll("%Team_Color%", team.getPrefix() + "");
				player.sendMessage(message);
//				clickLog.getPlayer().sendMessage(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig());
			});
		}
//		{
//			if(teams.size() <= slot + offset) continue;
//			String teamName = teamNames.get(slot + offset);
//			Team team = teamNames.get(teamName);
//			if(team == null) continue;
//			this.setIcon(slot, team.getIcon().displayName(team.getDisplayName()).lore(team.getDescription()));
//			this.setClickable(slot, (clickLog) -> 
//			{
//				if(clickLog.getDisplay().getIdName() != display.getIdName())
//					return;
//				if(cooldown.get(clickLog.getPlayer().getUniqueId()) != null 
//						&&  System.currentTimeMillis() - cooldown.get(clickLog.getPlayer().getUniqueId()) < 800 )
///					return;
//				cooldown.put(clickLog.getPlayer().getUniqueId(), System.currentTimeMillis());
////				game.team
//				game.setPlayersKit(clickLog.getPlayer().getUniqueId(), kitName);
//				clickLog.getPlayer().sendMessage(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getSelectorKitClickMsg().replaceAll("%Kit%", kit.getDisplayName()));
//			});
//		}
		
		int backPosition = this.getSize() - 9;
		int forwardPosition = this.getSize() - 1;
		this.setIcon(backPosition, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15));
		if(pageNum != 0)
		{
			this.setIcon(backPosition, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 13));
			this.setClickable(backPosition, (clickLog) -> 
			{
				display.setPage(new TeamSelector(this.getName(), pageNum - 1, display));
				display.update(clickLog.getPlayer());
			});
		}
		
		this.setIcon(forwardPosition, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15));
		if(pageNum < pages)
		{
			this.setIcon(forwardPosition, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 13));
			this.setClickable(forwardPosition, (clickLog) -> 
			{
				display.setPage(new TeamSelector(this.getName(), pageNum + 1, display));
				display.update(clickLog.getPlayer());
			});
		}
	}
}
