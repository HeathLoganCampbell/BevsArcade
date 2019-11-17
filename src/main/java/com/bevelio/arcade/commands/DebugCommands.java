package com.bevelio.arcade.commands;

import java.io.File;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.module.commandframework.Command;
import com.bevelio.arcade.module.commandframework.CommandArgs;
import com.bevelio.arcade.scoreboard.ArcadeScoreboard;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.utils.ActionBarUtils;

public class DebugCommands 
{
	private static HashSet<UUID> debugMsgAllow = new HashSet<>();
 	
	public static void message(CommandSender sender, String message)
	{
		boolean allow = true;
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			allow = debugMsgAllow.contains(player.getUniqueId());
		}
		
		if(allow)
			sender.sendMessage(CC.yellow + "Debug " + CC.dGray + "] " + CC.gray + message);
	}
	
	public static String spec(String word)
	{
		return CC.white + word + CC.gray;
	}
	
	public static String error(String word)
	{
		return CC.red + word + CC.gray;
	}
	
	@Command(name="debug.join", inGameOnly=true)
    public void onJoin(CommandArgs args) 
	{
		Player player = args.getPlayer();
		ArcadePlugin.getInstance().getGameManager().playerJoin(player);
		debugMsgAllow.add(player.getUniqueId());
		message(player, "You have been added to the game");
	}
	
	@Command(name="debug.quit", aliases={"debug.leave", "debug.exit"}, inGameOnly=true)
    public void onLeave(CommandArgs args) 
	{
		Player player = args.getPlayer();
		ArcadePlugin.getInstance().getGameManager().playerLeave(player);
		message(player, "You have been removed from the game");
		debugMsgAllow.remove(player.getUniqueId());
	}
	
	@Command(name="debug.time", aliases={"debug.s", "debug.t"}, inGameOnly=true)
    public void onTime(CommandArgs args) 
	{
		Player player = args.getPlayer();
		int sec = ArcadePlugin.getInstance().getGameManager().getSeconds();
		message(player, sec + " Seconds");
//		debugMsgAllow.remove(player.getUniqueId());
	}
	
	@SuppressWarnings("deprecation")
	@Command(name="debug.forcestart", aliases={"debug.faster"}, inGameOnly=true)
    public void onSpeedUp(CommandArgs args) 
	{
		Player player = args.getPlayer();
		GameState gamestate = ArcadePlugin.getInstance().getGameManager().getGameState();
		if(gamestate == GameState.STARTING)
		{
			ArcadePlugin.getInstance().getGameManager().setSeconds(3);
			message(player, "You have force started the game, It'll start in 3 seconds.");
		}
		else
		{
			message(player, "Your gamestate is currently " +  spec(gamestate.getDisplayName()) + " and it needs to be " + spec(StringUtils.capitalise(GameState.STARTING.getDisplayName())) + "!");
		}
	}
	
	@Command(name="debug.scoreboard", aliases={"debug.sb"}, inGameOnly=true)
    public void onScoreboard(CommandArgs args) 
	{
		Player player = args.getPlayer();
		ArcadeScoreboard scoreboard = new ArcadeScoreboard(player.getName());
		scoreboard.setUp();
		scoreboard.setLine("Hey look! it does work!", 1);
		scoreboard.send(player);
	}
	
	@Command(name="debug.worlds", aliases={"debug.ws"}, inGameOnly=true)
    public void onWorlds(CommandArgs args) 
	{
		Player player = args.getPlayer();
		player.sendMessage(CC.gray + "      ====[ Worlds ]====");
		for(File worldFile : ArcadePlugin.getInstance().getWorldCreatorManager().fetchWorlds())
			player.sendMessage(worldFile.getName());
	}
	
	@Command(name="debug.actionbar", aliases={"debug.ab"}, inGameOnly=true)
    public void onActiobar(CommandArgs args) 
	{
		Player player = args.getPlayer();
		ActionBarUtils.sendActionBar(player, "HEY THERE!");
	}
}
