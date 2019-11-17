package com.bevelio.arcade.commands;

import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.module.commandframework.Command;
import com.bevelio.arcade.module.commandframework.CommandArgs;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.pages.BuildPage;


//Build Create <WorldName>
//	1. Gui to select game type 
//	2. World is created
//	3. Configs are created
//  4. Starter plateform is created
//	5. Players are teleported to starter plateform

//Build Parse <Size>
//	1. Checks if this world is a build world
//	2. Gets a list of all chunks
//	3. teleports all players to the lobby world
//	4. Checks all chunks for signs
//	5. Compiles zip
//	6. Place zip in maps folder, ready to play


public class BuildCommands 
{	
	public boolean isValidplayer(Player player)
	{
		if(ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player))
		{
			player.sendMessage(CC.gray + "You are currently in a game!");
			return false;
		}
		
		return true;
	}
	
	@Command(name="build.create", inGameOnly=true, permission="bevsarcade.command.build.create")
    public void onJoin(CommandArgs args) 
	{
		Player player = args.getPlayer();
		if(this.isValidplayer(player))
		{
			String worldName = "";
			if(args.length() == 1)
			{
				worldName = args.getArgs(0);
			}
			else
			{
				player.sendMessage("Do full args.");
				return;
			}
			
			Display buildDisplay = ArcadePlugin.getInstance().getDisplayCore().getDisplay(player.getUniqueId(), "builder");
			if(buildDisplay == null)
			{
				buildDisplay = new Display("builder", player);
				buildDisplay.setPage(new BuildPage("Select a gametype.", 0, buildDisplay));
				ArcadePlugin.getInstance().getDisplayCore().registerDisplay(player.getUniqueId(), buildDisplay);
			}
			buildDisplay.open(player);
			
		}
	}
}
