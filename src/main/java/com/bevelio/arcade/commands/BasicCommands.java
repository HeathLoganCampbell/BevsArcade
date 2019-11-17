package com.bevelio.arcade.commands;

import java.util.Arrays;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.LobbyConfig;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.commandframework.Command;
import com.bevelio.arcade.module.commandframework.CommandArgs;
import com.bevelio.arcade.module.display.Display;
import com.bevelio.arcade.pages.KitSelector;
import com.bevelio.arcade.pages.TeamSelector;
import com.bevelio.arcade.types.DisconnectLog;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.Kit;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.types.Team;
import com.bevelio.arcade.utils.MathUtils;
import com.bevelio.arcade.utils.PlayerUtils;

//bevsarcade.command.<name>

public class BasicCommands
{
	private TranslationConfig tc = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
	
	/**
	 * Command '/join' will add the player into the game
	 * Command '/join <PlayerName>' will force add a player to the game
	 * </br>
	 * Aliases {'Play'}
	 */
	@Command(name="join", aliases={"play", "bevsarcade.join", "bevsarcade.play", "ba.join", "ba.play"}, permission="bevsarcade.command.join")
	public void onJoinCmd(CommandArgs args)
	{
		Player target = null;
		if(args.getPlayer() != null)
			target = args.getPlayer();
		
		if(args.length() == 1)
		{
			if(args.getSender().hasPermission("bevsarcade.command.forcejoin"))
			{
			
				String targetSr = args.getArgs(0);
				if(targetSr.equalsIgnoreCase("@a"))
				{
					for(Player player : Bukkit.getOnlinePlayers())
					{
						String targetsMsg = tc.getCommandJoinMessage();
						
						ArcadePlugin.getInstance().getGameManager().playerJoin(player);
						
						targetsMsg = targetsMsg.replaceAll("%Player%", (target == null ? args.getSender() : target).getName());
						player.sendMessage(targetsMsg);
						
						player.teleport(ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation());
					}
					
					String sendersMsg = tc.getCommandJoinSenderMessage();
					sendersMsg = sendersMsg.replaceAll("%Player%", "Everyone");
					args.getSender().sendMessage(sendersMsg);
					return;
				}
				
				if(Bukkit.getPlayer(targetSr) != null)
					target = Bukkit.getPlayer(targetSr);
				else
				{
					String notFoundMsg = tc.getCommandPlayerNotFound();
					notFoundMsg = notFoundMsg.replaceAll("%Player%", targetSr);
					args.getSender().sendMessage(notFoundMsg);
					return;
				}
			} else {
				args.getSender().sendMessage(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getCommandPlayerPermissionNeeded());
			}
		}
		
		if(target == null)
		{
			args.getSender().sendMessage(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getCommandOnlyPlayersCanDoThisCommand());
			return;
		}
		
		String targetsMsg = tc.getCommandJoinMessage();
		
		ArcadePlugin.getInstance().getGameManager().playerJoin(target);
		
		targetsMsg = targetsMsg.replaceAll("%Player%", target.getName());
		target.sendMessage(targetsMsg);
		
		target.teleport(ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation());
		
		if(args.getSender() != target)
		{
			String sendersMsg = tc.getCommandJoinSenderMessage();
			sendersMsg = sendersMsg.replaceAll("%Player%", target.getName());
			args.getSender().sendMessage(sendersMsg);
		}
	}
	
	@Command(name="bevsarcade.joinsign", aliases={"ba.joinsign","bevsarcade.js", "ba.js"}, permission="bevsarcade.command.joinsign", inGameOnly=true)
	public void onJoinSign(CommandArgs args)
	{
		Player player = args.getPlayer();
		ItemStackBuilder item = new ItemStackBuilder(Material.SIGN).displayName(tc.getSignToolName());
		player.getInventory().addItem(item.build());
		player.sendMessage(tc.getSignToolSuccessfullyGiven());
	}
	
	@Command(name="forcestart", aliases={"bevsarcade.forcestart", "bevsarcade.fs", "ba.forcestart", "ba.fs"}, permission="bevsarcade.command.forcestart")
	public void onForceStart(CommandArgs args)
	{
		Player player = args.getPlayer();
		GameState gamestate = ArcadePlugin.getInstance().getGameManager().getGameState();
		if(gamestate == GameState.STARTING || gamestate == GameState.WAITING)
		{
			if(gamestate == GameState.WAITING)
			{
				ArcadePlugin.getInstance().getGameManager().setGameState(GameState.STARTING);
			}
			ArcadePlugin.getInstance().getGameManager().setSeconds(3);
			String message = tc.getCommandForceStartMessage();
			player.sendMessage(message);
		}
		else
		{
			String message = tc.getCommandForceStartErrorMessage();
			message = message.replaceAll("%GameState%", ArcadePlugin.getInstance().getGameManager().getGameState().getDisplayName());
			player.sendMessage(message);
		}
	}
	
	@Command(name="leave", aliases={"quit", "bevsarcade.quit", "bevsarcade.leave", "ba.leave", "ba.quit"}, permission="bevsarcade.command.quit")
	public void onQuitCmd(CommandArgs args)
	{
		Player target = null;
		if(args.getPlayer() != null)
			target = args.getPlayer();
		
		if(args.getSender().hasPermission("bevsarcade.command.forcequit"))
		{
			if(args.length() == 1)
			{
				String targetSr = args.getArgs(0);
				
				if(targetSr.equalsIgnoreCase("@a"))
				{
					for(Player player : Bukkit.getOnlinePlayers())
					{
						String targetsMsg = tc.getCommandJoinMessage();
						

						Game game = ArcadePlugin.getInstance().getGameManager().getGame();
						Team team = game.removeMemeber(player.getUniqueId());
						ArcadePlugin.getInstance().getGameManager().playerLeave(player);
						
						Location lobbySpawn = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation();
						if(lobbySpawn.getWorld() != target.getWorld())
							target.teleport(ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation());
						PlayerUtils.clear(player);
						PlayerUtils.reset(player);
						player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

					    if (team != null)
					    	if(game.isLive())
					    		game.setPlayState(target, PlayState.OUT);

						targetsMsg = targetsMsg.replaceAll("%Player%", (target == null ? args.getSender() : target).getName());
						player.sendMessage(targetsMsg);
					}
					
					String sendersMsg = tc.getCommandJoinSenderMessage();
					sendersMsg = sendersMsg.replaceAll("%Player%", "Everyone");
					args.getSender().sendMessage(sendersMsg);
					return;
				}
				
				if(Bukkit.getPlayer(targetSr) != null)
					target = Bukkit.getPlayer(targetSr);
				else
				{
					String notFoundMsg = tc.getCommandPlayerNotFound();
					notFoundMsg = notFoundMsg.replaceAll("%Player%", targetSr);
					args.getSender().sendMessage(notFoundMsg);
					return;
				}
			}
		} else {
			args.getSender().sendMessage(tc.getCommandPlayerPermissionNeeded());
		}
		
		
		String targetsMsg = tc.getCommandQuitMessage();
		
		Game game = ArcadePlugin.getInstance().getGameManager().getGame();
		Team team = game.removeMemeber(target.getUniqueId());
		ArcadePlugin.getInstance().getGameManager().playerLeave(target);
		
		targetsMsg = targetsMsg.replaceAll("%Player%", (target == null ? args.getSender() : target).getName());
		target.sendMessage(targetsMsg);
		
		Location lobbySpawn = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation();
		if(lobbySpawn.getWorld() != target.getWorld())
			target.teleport(ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getSpawnLocation());
		PlayerUtils.clear(target);
		PlayerUtils.reset(target);
		target.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		

	    if (team != null)
	    	if(game.isLive())
	    		game.setPlayState(target, PlayState.OUT);
		
		if(args.getSender() != target)
		{
			String sendersMsg = tc.getCommandQuitSenderMessage();
			sendersMsg = sendersMsg.replaceAll("%Player%", target.getName());
			args.getSender().sendMessage(sendersMsg);
		}
		
	}
	
	@Command(name="kit", aliases={"bevsarcade.kit", "ba.kit"}, inGameOnly=true, permission="bevsarcade.command.kit")
    public void onJoin(CommandArgs args) 
	{
		Player player = args.getPlayer();
		GameManager gm = ArcadePlugin.getInstance().getGameManager();
		
		Game game = gm.getNextGame();
		GameState gamestate = ArcadePlugin.getInstance().getGameManager().getGameState();
		if(gamestate != GameState.WAITING && gamestate != GameState.STARTING )
			game = gm.getGame();
		
		if(gm.isInteractivePlayer(player))
		{
			if(args.length() >= 1)
			{
				Player target = player;
				if(args.length() == 2)
				{
					String playerName = args.getArgs(1);
					target = Bukkit.getPlayer(playerName);
					if(target == null)
					{
						args.getSender().sendMessage(tc.getCommandPlayerNotFound().replaceAll("%Player%", playerName));
						return;
					}
				}
				
				
				String askedKit = args.getArgs(0);
				Kit kit = null;
				
				for(Entry<String, Kit> curKit : game.getLoadedKits().entrySet())
					if(curKit.getValue().getName().equalsIgnoreCase(askedKit)
							|| curKit.getValue().getDisplayName().equalsIgnoreCase(askedKit))
						kit = curKit.getValue();
				
				if(kit == null)
				{
					target.sendMessage(tc.getCommandKitMessageKitNotFound().replaceAll("%Kit%", askedKit));
					return;
				}
				
				if(gm.isRunning() && (game.isAlive(player) && !player.hasPermission("bevsarcade.command.kit.bypass")))
				{
					player.sendMessage(tc.getCommandKitMessageYouCantChangeYourKitWhilePlaying());
					return;
				}
				
				if(player.hasPermission("bevsarcade.kit." + kit.getName().toLowerCase()) || player.isOp())
				{
					game.setPlayersKit(target.getUniqueId(), kit.getName());
					target.sendMessage(tc.getCommandKitMessage().replaceAll("%Kit%", kit.getDisplayName()));
				} else
					player.sendMessage(ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getCommandKitMessageYouDontHaveKit().replaceAll("%Kit%", kit.getDisplayName()));
				return;
			}
			
			if(game != null)
				if(gm.isRunning() && (game.isAlive(player) && !player.hasPermission("bevsarcade.command.kit.bypass")))
				{
					player.sendMessage(tc.getCommandKitMessageYouCantChangeYourKitWhilePlaying());
					return;
				}
			
			Display selectorDisplay = ArcadePlugin.getInstance().getDisplayCore().getDisplay(player.getUniqueId(), "kit_selector");
			if(selectorDisplay == null)
			{
				selectorDisplay = new Display("kit_selector", player);
				ArcadePlugin.getInstance().getDisplayCore().registerDisplay(player.getUniqueId(), selectorDisplay);
			}
			selectorDisplay.setPage(new KitSelector(tc.getKitMenuSelectorTitle(), 0, selectorDisplay));
			selectorDisplay.open();
		} else {
			player.sendMessage(tc.getCommandNotPlayingArcade());
		}
	}
	
	@Command(name="team", aliases={"bevsarcade.team", "ba.team"}, inGameOnly=true, permission="bevsarcade.command.team")
    public void onTeam(CommandArgs args) 
	{
		Player player = args.getPlayer();
		GameManager gm = ArcadePlugin.getInstance().getGameManager();
		
		Game game = gm.getNextGame();
		GameState gamestate = ArcadePlugin.getInstance().getGameManager().getGameState();
		if(gamestate != GameState.WAITING && gamestate != GameState.STARTING )
			game = gm.getGame();
		
		if(gm.isInteractivePlayer(player))
		{
			if(game != null)
				if(gm.isRunning() && (game.isAlive(player) && !player.hasPermission("bevsarcade.command.team.bypass")))
				{
					player.sendMessage(tc.getCommandKitMessageYouCantChangeYourKitWhilePlaying());
					return;
				}
			
			Display selectorDisplay = ArcadePlugin.getInstance().getDisplayCore().getDisplay(player.getUniqueId(), "team_selector");
			if(selectorDisplay == null)
			{
				selectorDisplay = new Display("team_selector", player);
				ArcadePlugin.getInstance().getDisplayCore().registerDisplay(player.getUniqueId(), selectorDisplay);
			}
			selectorDisplay.setPage(new TeamSelector(tc.getTeamMenuSelectorTitle(), 0, selectorDisplay));
			selectorDisplay.open();
		} else {
			player.sendMessage(tc.getCommandNotPlayingArcade());
		}
	}
	
	@Command(name="bevsarcade.skip", aliases={"ba.skip"}, inGameOnly=true, permission="bevsarcade.command.skip")
    public void onSkip(CommandArgs args) 
	{
		GameManager gameManager = ArcadePlugin.getInstance().getGameManager();
		if(gameManager.isRunning())
		{
			gameManager.getGame().onEnd();
			args.getSender().sendMessage(tc.getCommandSkipMessage().replaceAll("%Player%", args.getSender().getName()));
			gameManager.getGame().broadcast(tc.getCommandSkipBroadcast().replaceAll("%Player%", args.getSender().getName()));
		} else
			args.getSender().sendMessage(tc.getCommandSkipOnlyCanDoCommandIfInAGame());
	}
	
	@Command(name="creator", aliases={"bevsarcade.creator", "ba.creator"}, inGameOnly=true, permission="bevsarcade.command.creator")
    public void onCreator(CommandArgs args) 
	{
		CommandSender sender = args.getSender();
		sender.sendMessage(CC.bAqua + "BevsArcade " + CC.gray + "The plugin " + CC.white + "BevsArcade" + CC.gray + " was developed by " + CC.white + Arrays.asList(ArcadePlugin.getInstance().getDescription().getAuthors()) + CC.gray + ".");
	}
	
	@Command(name="bevsarcade.reload", aliases={"ba.reload"}, inGameOnly=true, permission="bevsarcade.command.reload")
    public void onReload(CommandArgs args) 
	{
		ArcadePlugin.getInstance().getConfigManager().loadConfigs();
		args.getSender().sendMessage(tc.commandReloadMessage);
	}
	
	//ba setcountdown <seconds>
	@Command(name="bevsarcade.setCountdown", aliases={"ba.setCountdown"}, inGameOnly=true, permission="bevsarcade.command.setCountdown")
    public void onSetCountdown(CommandArgs args) 
	{
		if(args.length() != 1)
		{
			args.getSender().sendMessage(tc.getCommandUsage().replaceAll("%Command%", "/BevelioArcade SetCountdown <Seconds>"));
			return;
		}
		
		GameManager gm = ArcadePlugin.getInstance().getGameManager();
		if(gm.getGameState() != GameState.STARTING)
		{
			args.getSender().sendMessage(tc.getCommandSetCountdownGameIsntStarting());
			return;
		}
		
		String secondStr = args.getArgs(0);
		if(!MathUtils.isNumeric(secondStr))
		{
			args.getSender().sendMessage(tc.getCommandOnlyNumber());
			return;
		}
		
		int seconds = Integer.parseInt(secondStr);
		if(seconds <= 0)
		{
			args.getSender().sendMessage(tc.getCommandSetCountdownHigherThanZero());
			return;
		}
		
		gm.setSeconds(seconds);
		args.getSender().sendMessage(tc.getCommandSetCountdownSuccess().replaceAll("%Seconds%", seconds + ""));
	}
	
	@Command(name="bevsarcade.setLobbySpawn", aliases={"ba.setLobbySpawn"}, inGameOnly=true, permission="bevsarcade.command.setLobbySpawn")
    public void onSetLobbySpawn(CommandArgs args) 
	{
		Player player = args.getPlayer();
		Location location = player.getLocation();
		LobbyConfig lobbyConfig = ArcadePlugin.getInstance().getInstance().getConfigManager().getLobbyConfig();
		lobbyConfig.setSpawnLocation(location);
		lobbyConfig.save();
		
		args.getSender().sendMessage(tc.getCommandSetLobbySpawnSuccess());
	}
}
