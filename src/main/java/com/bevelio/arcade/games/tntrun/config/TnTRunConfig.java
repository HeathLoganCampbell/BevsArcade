package com.bevelio.arcade.games.tntrun.config;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.configs.SoloMiniGamesConfig;
import com.bevelio.arcade.configs.TeamMiniGamesConfig;
import com.bevelio.arcade.games.microwalls.MicroWalls;
import com.bevelio.arcade.games.tntrun.TnTRun;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.utils.ItemUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TnTRunConfig extends SoloMiniGamesConfig 
{
	private String deathMessage = CC.aqua + "TnTRun" + CC.gray + " %Player% had died! %Alive_Players% left";
	private double destroyBlockAfterWalking = 0.1;
	
	public TnTRunConfig(TnTRun game) 
	{
		super(game);
		this.setSimpleScoreboard(new String[] 
				{
						"%Alive_Players%" + CC.green + " Players",
						CC.dGray + "------------------------",
				});
	}
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
	}
}
