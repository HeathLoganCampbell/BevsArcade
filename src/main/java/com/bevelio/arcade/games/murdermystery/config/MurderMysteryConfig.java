package com.bevelio.arcade.games.murdermystery.config;

import com.bevelio.arcade.configs.SoloMiniGamesConfig;
import com.bevelio.arcade.games.murdermystery.MurderMystery;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MurderMysteryConfig extends SoloMiniGamesConfig 
{
	private String[] endAnnouncementMessages = {
			CC.b + "###############################",
			"",
			CC.gray + "    Dectective: %Alive_State_Murder%%Dective%",
			CC.gray + "    Murder: %Alive_State_Murder%%Murder% (" + CC.green +"%Murder_Kills%" + CC.gray + ")",
			CC.gray + "    Hero:   %Hero%",
			"",
			CC.b + "###############################"
	};
	private String aliveStatePrefix = CC.strikeThrough + CC.gray;
	private String MysteryTypeDisplayNameMurder = "Murder";
	private String MysteryTypeDisplayNameDetective = "Detective";
	private String MysteryTypeDisplayNameInnocent = "Innocent";
	
	public MurderMysteryConfig(MurderMystery game) 
	{
		super(game);
		
		this.setSimpleScoreboard(new String[] {
				"",
				"Role: " + CC.green + "%Role%",
				"",
				"Innocents Left: " + CC.green + "%Innocent_Count%",
				"Time Left: " + CC.green + "%Time%",
				"",
				"%Bow_State%",
				"",
				CC.b + "BevsArcade"
				
		});
	}
	
	@Override
    public void loadConfig() 
	{
        super.loadConfig();
	}
}
