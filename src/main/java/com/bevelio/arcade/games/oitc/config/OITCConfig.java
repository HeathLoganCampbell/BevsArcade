package com.bevelio.arcade.games.oitc.config;

import com.bevelio.arcade.configs.MiniGamesConfig;
import com.bevelio.arcade.configs.SoloMiniGamesConfig;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.games.oitc.OITC;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class OITCConfig extends SoloMiniGamesConfig 
{
	private String arrowsNames = CC.aqua + "Super Arrow";
	
	public OITCConfig(OITC game) 
	{
		super(game);
		this.setSimpleScoreboard(new String[] 
				{
						"%Score_1%" + CC.green + " %Place_1%",
						"%Score_2%" + CC.green + " %Place_2%",
						"%Score_3%" + CC.green + " %Place_3%",
						"%Score_4%" + CC.green + " %Place_4%",
						"%Score_5%" + CC.green + " %Place_5%",
						"%Score_6%" + CC.green + " %Place_6%",
						"%Score_7%" + CC.green + " %Place_7%",
						"%Score_8%" + CC.green + " %Place_8%",
						"%Score_9%" + CC.green + " %Place_9%",
						"%Score_10%" + CC.green + " %Place_10%",
						"%Score_11%" + CC.green + " %Place_11%",
						"%Score_12%" + CC.green + " %Place_12%",
						"%Score_13%" + CC.green + " %Place_13%",
						"%Score_14%" + CC.green + " %Place_14%",
						CC.dGray + "------------------------",
				});
	}
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
	}

}
