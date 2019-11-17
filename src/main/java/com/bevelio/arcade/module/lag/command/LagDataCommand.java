package com.bevelio.arcade.module.lag.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.arcade.module.commandframework.Command;
import com.bevelio.arcade.module.commandframework.CommandArgs;
import com.bevelio.arcade.module.lag.LagMeter;

public class LagDataCommand
{
	private LagMeter lag;
	
	public LagDataCommand(LagMeter lag) 
	{
		this.lag = lag;
	}

	@Command(name="bevsarcade.lagdata", aliases={"ba.lagdata"}, inGameOnly=true, permission="bevsarcade.command.lagdata")
	public void onExecute(CommandArgs args)
	{
		lag.sendUpdate((Player) args.getSender());
	}
}
