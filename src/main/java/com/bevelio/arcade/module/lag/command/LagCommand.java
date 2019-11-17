package com.bevelio.arcade.module.lag.command;

import org.bukkit.command.CommandSender;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.module.commandframework.CommandArgs;
import com.bevelio.arcade.module.lag.LagMeter;

import com.bevelio.arcade.module.commandframework.Command;
import com.bevelio.arcade.module.commandframework.CommandArgs;

public class LagCommand
{
	private LagMeter lag;
	
	public LagCommand(LagMeter lag) 
	{
		this.lag = lag;
	}

	@Command(name="bevsarcade.lag", aliases={"ba.lag"}, inGameOnly=true, permission="bevsarcade.command.lag")
	public boolean onExecute(CommandArgs args)
	{
		double ticks = this.lag.getTicksPerSecond();
		double percentage = ((double) ticks / 20) * 100;
		String prefix = CC.dGreen;
		if(ticks < 18.0) prefix = CC.green;
		else if(ticks < 16.0) prefix = CC.gold;
		else if(ticks < 14.0) prefix = CC.yellow;
		else if(ticks < 10.0) prefix = CC.red;
		else if(ticks < 8.0) prefix = CC.dRed;
		String msg = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getCommandLagMessage();
		msg = msg.replaceAll("%Lag_Prefix%", prefix)
			     .replaceAll("%Ticks%", String.format("%.2f", ticks))
			     .replaceAll("%Percentage%", String.format("%.2f", percentage));
		args.getSender().sendMessage(msg);
		return false;
	}

}
