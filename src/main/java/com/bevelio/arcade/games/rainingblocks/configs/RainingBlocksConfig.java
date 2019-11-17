package com.bevelio.arcade.games.rainingblocks.configs;

import java.util.HashMap;

import com.bevelio.arcade.configs.MiniGamesConfig;
import com.bevelio.arcade.configs.SoloMiniGamesConfig;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.games.oitc.OITC;
import com.bevelio.arcade.games.rainingblocks.RainingBlocks;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RainingBlocksConfig extends SoloMiniGamesConfig 
{
	private double blockFallingSpeedBase = 0.05;
	private double blockFallingSpeedIncreaser = 0.01;
	private String deathMessage = CC.aqua + "RainingBlocks" + CC.gray + " %Player% had died! %Alive_Players% left";
	
	private HashMap<Integer, String> difficultMessages = new HashMap<>();
	
	public RainingBlocksConfig(RainingBlocks game) 
	{
		super(game);
		
		difficultMessages.put(20, CC.aqua + "RainingBlocks" + CC.gray + " Getting Harder...");
		difficultMessages.put(40, CC.aqua + "RainingBlocks" + CC.gray + " Getting Harder...");
		difficultMessages.put(60, CC.aqua + "RainingBlocks" + CC.gray + " Getting Harder...");
		difficultMessages.put(80, CC.aqua + "RainingBlocks" + CC.gray + " Getting Harder...");
		difficultMessages.put(100, CC.aqua + "RainingBlocks" + CC.gray + " Getting Harder...");
		difficultMessages.put(120, CC.aqua + "RainingBlocks" + CC.gray + " Getting Harder...");
		difficultMessages.put(140, CC.aqua + "RainingBlocks" + CC.gray + " Getting Harder...");
		difficultMessages.put(160, CC.aqua + "RainingBlocks" + CC.gray + " Getting Impossible...");
	}
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
	}

}
