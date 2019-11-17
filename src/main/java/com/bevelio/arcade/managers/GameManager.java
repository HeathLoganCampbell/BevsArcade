package com.bevelio.arcade.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Scoreboard;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.configs.files.KitConfig;
import com.bevelio.arcade.configs.files.LobbyConfig;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.GameStateChangeEvent;
import com.bevelio.arcade.events.PlayJoinTeamEvent;
import com.bevelio.arcade.events.PlayerEnterLobbyEvent;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.TeamGame;
import com.bevelio.arcade.games.microwalls.MicroWalls;
import com.bevelio.arcade.games.murdermystery.MurderMystery;
import com.bevelio.arcade.games.oitc.OITC;
import com.bevelio.arcade.games.rainingblocks.RainingBlocks;
import com.bevelio.arcade.games.slaparoo.Slaparoo;
import com.bevelio.arcade.games.spleef.Spleef;
import com.bevelio.arcade.games.splegg.Splegg;
import com.bevelio.arcade.games.tdm.TeamDeathMatch;
import com.bevelio.arcade.games.tntrun.TnTRun;
import com.bevelio.arcade.games.tnttag.TnTTag;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.updater.UpdateEvent;
import com.bevelio.arcade.module.updater.UpdateType;
import com.bevelio.arcade.scoreboard.ArcadeScoreboard;
import com.bevelio.arcade.types.DisconnectLog;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.types.Team;
import com.bevelio.arcade.utils.MathUtils;
import com.bevelio.arcade.utils.PlayerUtils;
import com.bevelio.arcade.utils.ServerUtils;

import lombok.Getter;
import lombok.Setter;

public class GameManager implements Listener
{
	private @Getter GameState 		gameState;
	private @Getter @Setter int 	seconds = 1;
	private @Getter int				currentGameId = 0;
	private TranslationConfig		tc					= ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
	private @Getter KitConfig 		kitConfig;
	private @Getter ArcadeScoreboard defaultScoreboard;
	
	private @Getter Game			currentGame, nextGame;		
	private @Getter HashSet<UUID> 	interactivePlayers = new HashSet<UUID>();
	private @Getter	HashMap<String, GameSummary> games = new HashMap<>();
	private @Getter HashMap<Integer, ItemStackBuilder> lobbyHotbar = new HashMap<>();
	private @Getter HashMap<Integer, ItemStackBuilder> lobbyHotbarForOps = new HashMap<>();
	
	private LobbyConfig lc = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig();
	
	public GameManager()
	{
		registerAllGames();
		
		this.nextGame = new Spleef();
		this.gameState = GameState.LOADING;
		this.kitConfig = new KitConfig();
		this.defaultScoreboard = new ArcadeScoreboard("Lobby");
		this.defaultScoreboard.setUp();
		
		this.initPreTeamColors();
		
		int secconds = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getDefaultLobbySeconds();
		GameState.STARTING.setSeconds(secconds);
		
		this.lobbyHotbar = ArcadePlugin.getInstance().getConfigManager().getLobbyConfig().getLobbyItemSet();
		this.lobbyHotbarForOps = ArcadePlugin.getInstance().getConfigManager().getGameMasterConfig().getLobbyItemSetForOps();
	}
	
	public void initPreTeamColors()
	{
		Scoreboard sb = this.getDefaultScoreboard().getScoreboard();
		for(ChatColor color : ChatColor.values())
		{
			org.bukkit.scoreboard.Team team = sb.registerNewTeam(color.name());
			team.setPrefix(color.toString());
		}
	}
	
	public void registerAllGames()
	{
		this.registerGame(Spleef.class);
		this.registerGame(Splegg.class);
		this.registerGame(OITC.class);
		this.registerGame(MicroWalls.class);
		this.registerGame(Slaparoo.class);
		this.registerGame(TnTTag.class);
//		this.registerGame(DestroyTheCore.class);
		this.registerGame(TnTRun.class);
		this.registerGame(TeamDeathMatch.class);
		this.registerGame(RainingBlocks.class);
		this.registerGame(MurderMystery.class);
	}
	
	public void registerGame(Class<? extends Game> clazz)
	{
		try {
			Game game = (Game) clazz.newInstance();
			
			String name = game.getGameTypeName();
			this.games.put(name, new GameSummary(clazz, name, null, new ItemStackBuilder(Material.DIRT)));
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, GameSummary> getGameSummarizes()
	{
		return this.games;
	}
	
	public GameSummary getGameSummary(String gameType)
	{
		return this.games.get(gameType);
	}
	
	public void toLobby(Player player)
	{
		Bukkit.getPluginManager().callEvent(new PlayerEnterLobbyEvent(player));
		this.defaultScoreboard.send(player);
		PlayerUtils.clear(player);
		PlayerUtils.reset(player);
		player.setGameMode(GameMode.SURVIVAL);
		player.teleport(lc.getSpawnLocation());
		
		
		for(Entry<Integer, ItemStackBuilder> slottedItem : lobbyHotbar.entrySet())
			player.getInventory().setItem(slottedItem.getKey(), slottedItem.getValue().build());
		
		if(player.hasPermission("bevsarcade.gamemaster") || player.isOp())
			for(Entry<Integer, ItemStackBuilder> slottedItem : this.lobbyHotbarForOps.entrySet())
				player.getInventory().setItem(slottedItem.getKey(), slottedItem.getValue().build());
		
		if(this.getGameState() == GameState.STARTING)
			this.getGame().hanndleTeamPreferences();
	}
	
	public void playerJoin(Player player)
	{
		UUID uuid = player.getUniqueId();
		this.interactivePlayers.add(uuid);
		toLobby(player);
	}
	
	public void playerLeave(Player player)
	{
		UUID uuid = player.getUniqueId();
		this.interactivePlayers.remove(uuid);
		if(this.isRunning())
		{
			Team team = this.getGame().getPlayersTeam(uuid);
			if(team != null)
				team.removeMember(uuid);
		}
	}
	
	public boolean isInteractivePlayer(UUID uuid)
	{
		return this.getInteractivePlayers().contains(uuid);
	}
	
	public boolean isInteractivePlayer(Player player)
	{
		return this.isInteractivePlayer(player.getUniqueId());
	}
	
	public void setGameState(GameState gameState)
	{
//		System.out.println("GameState ]-> " + this.gameState + " -> " + gameState);
		GameStateChangeEvent event = new GameStateChangeEvent(gameState, this.gameState);
		
		ServerUtils.callEvent(event);
		
		if(event.isCancelled()) return;
		this.gameState = event.getTo();
		this.seconds = event.getSeconds();
	}
	
	public Game nextGame()
	{
		if(this.nextGame == null)
			this.nextGame = new Spleef();
		this.currentGame = this.nextGame;
		return this.currentGame;
	}
	
	public void setNextGame(GameSummary gameSummary)
	{
		try {
			this.nextGame = gameSummary.clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public Game getGame()
	{
		return this.currentGame;
	}
	
	private void updateGameState()
	{
		boolean foundCurrent = false;
		for(GameState state : GameState.values())
		{
			if(foundCurrent)
			{
				this.setGameState(state);
				break;
			}
			
			if(state == this.gameState)
				foundCurrent = true;
			
			if(this.getGameState() == GameState.FINISHING)
			{
				this.setGameState(GameState.WAITING);
				break;
			}
		}
	}
	
	public void nextGameId()
	{
		this.currentGameId++;
	}
	
	public boolean isRunning()
	{
		return this.getGameState() == GameState.PREGAME || this.getGameState() == GameState.LIVE;
	}
	
	public void broadcast(String message)
	{
		this.getInteractivePlayers().stream()
									.map(set -> Bukkit.getPlayer(set))
									.filter(player -> player != null)
									.forEach(player -> player.sendMessage(message));
	}
	
	public void setOffFirework(Location location, Color color) {
		Firework f = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

		FireworkMeta fm = f.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder()
          .flicker(true)
          .trail(true)
          .with(FireworkEffect.Type.BALL_LARGE)
          .withColor(color)
          .withFade(Color.WHITE)
          .build());
          fm.setPower(((int) MathUtils.random() * 2) + 1);
          f.setFireworkMeta(fm);
    }
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		
//		if(e.getType() != UpdateType.SLOWER && this.getGame() != null && this.getGameState() == GameState.LIVE)
//			this.getGame().updateScoreboard();
		
		if(e.getType() != UpdateType.SECOND) return;
		
		if(this.getGame() != null && (this.getGameState() == GameState.LIVE || this.getGameState() == GameState.PREGAME))
		{
			if(this.getGameState() == GameState.LIVE)
				this.getGame().checkEnd();
			this.getGame().updateScoreboard();
		}
		
		if(this.getGame() != null && this.getGameState() == GameState.FINISHING)
		{
			Game game = this.getGame();
			if(ArcadePlugin.getInstance().getConfigManager().getMainConfig().isFireworksOnWin())
			{
				Color fireworkColor = Color.RED;
				
				if(game instanceof SoloGame)
				{
					if(!((SoloGame)game).getWinners().isEmpty())
					{
						Player winner = ((SoloGame)game).getWinners().get(0);
						if(winner != null) 
							setOffFirework(winner.getLocation(), fireworkColor);
					}
				}
				else if (game instanceof TeamGame)
				{
					if(((TeamGame)game).getWinnerTeams() != null) 
					{
						fireworkColor = ((TeamGame)game).getWinnerTeams().getColor();
						for(UUID winnerUUID : ((TeamGame)game).getWinnerTeams().getMembers())
						{
							Player winner = Bukkit.getPlayer(winnerUUID);
							if(winner == null) continue;
							setOffFirework(winner.getLocation(), fireworkColor);
						}
					}
				}
			}
		}
		
		int minPlayers = ArcadePlugin.getInstance().getConfigManager().getMainConfig().getMinNumberOfPlayersToStart();
		
		
		if(this.seconds == 0)
			this.updateGameState();

		
		if(this.getGameState() == GameState.STARTING)
		{
			HashMap<Integer, String> countdownbroadcasts =  this.tc.getLobbyCountdownBroadcasts();
			if(countdownbroadcasts.containsKey(this.getSeconds()))
				this.broadcast(countdownbroadcasts.get(this.getSeconds()));
			
			if(minPlayers > this.getInteractivePlayers().size())
			{
				this.setGameState(GameState.WAITING);
				return;
			}
			
			if(this.seconds == 2)
			{
					this.getGame().hanndleTeamPreferences();
					Bukkit.getOnlinePlayers().forEach(player -> 
					{
						if(this.isInteractivePlayer(player))
						{
							String teamName = null;
							for(Entry<String, ArrayList<UUID>> prefTeamSet : this.getGame().getPlayersPrefTeams().entrySet())
							{
								DebugCommands.message(player, "Checking if " + prefTeamSet.getKey() + CC.gray + " is a good team!");
								if(prefTeamSet.getValue().contains(player.getUniqueId()))
								{
									teamName = prefTeamSet.getKey();
								}
								else
									DebugCommands.message(player, "It's not! " + Arrays.toString(prefTeamSet.getValue().toArray()));
							}
									
							if(teamName == null)
								this.getGame().hanndleTeamPreferences();
							
							Team team = this.getGame().getTeam(teamName);
							DebugCommands.message(player, "You are expected in team " + teamName + CC.gray + "!");
							if(team == null) return;
							
							this.getGame().addMember(team, player);
							DebugCommands.message(player, "You are in team " + team.getDisplayName() + CC.gray + "!");
						}
					});
			}
			
			int slots = lc.getScoreboardLines().length;
			this.defaultScoreboard.setTitle(this.applyLobbyScoreboardPlaceholders(lc.getScoreboardHeader()));
			for(int i = 0; i < slots; i++)
			{
				int slot = slots - i;
				String line = applyLobbyScoreboardPlaceholders(lc.getScoreboardLines()[i]);
				this.defaultScoreboard.setLine(line, slot);
			}
		} 
		else if(this.getGameState() == GameState.WAITING)
		{
			if(minPlayers <= this.getInteractivePlayers().size())
				this.setGameState(GameState.STARTING);
			int slots = lc.getScoreboardLines().length;
			this.defaultScoreboard.setTitle(this.applyLobbyScoreboardPlaceholders(lc.getScoreboardHeader()));
			for(int i = 0; i < slots; i++)
			{
				int slot = slots - i;
				String line = applyLobbyScoreboardPlaceholders(lc.getScoreboardLines()[i]);
				this.defaultScoreboard.setLine(line, slot);
			}
		}
		
		
		
//		System.out.println(this.getSeconds() + " Seconds");
		if(this.seconds > 0)
			this.seconds--;
		//ArcadePlugin.getInstance().getWorldManager().createNewWorld("DemoMap", "H" + System.currentTimeMillis() / 1000);
	}
	
	public String applyLobbyScoreboardPlaceholders(String replaceStr)
	{
		replaceStr = replaceStr.replaceAll("%Next_Game%", this.getGame().getGameTypeName() +"")
						 .replaceAll("%Player_Playing%", this.interactivePlayers.size() + "")
						 .replaceAll("%Starting_In%", this.seconds >= 0 ? this.seconds + "" : this.lc.getScoreboardWaitingForPlayers());
		if(this.getGame() != null)
			if(this.getGame().getWorldData() != null)
				replaceStr = replaceStr.replaceAll("%Next_Map%", this.getGame().getWorldData().name);
		return replaceStr;
	}
	
	@EventHandler
	public void onGameChange(GameStateChangeEvent e)
	{
		this.defaultScoreboard.clear();
		if(e.getTo() == GameState.STARTING)
			this.getInteractivePlayers().stream()
										.map(set -> Bukkit.getPlayer(set))
										.filter(player -> player != null)
										.forEach(player -> this.defaultScoreboard.send(player));
		if(e.getTo() == GameState.LIVE)
		{
			clearPreTeam();
			this.defaultScoreboard.clear();
		}
		
		if(e.getTo() == GameState.PREGAME)
			e.setSeconds(this.getGame().getPreGameSeconds());
	}
	
	@EventHandler
	public void onCustomDamage(CustomDamageEvent e)
	{
		if(this.getGameState() != GameState.LIVE)
			e.setCancelled("Game isn't live");
	}
	
	/**
	 * If player leaves while the game is running 
	 * they will die
	 * @param e
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		Game game = this.getGame();
	 	if (game == null) return;

	    if ((game.quitDropItems) &&  (game.isLive()) && (game.isAlive(player))) 
	    	PlayerUtils.drop(player, true);

	    Team team = game.removeMemeber(uuid);

	    if (!game.quitOut) 
	    {
	    	String kitName = game.getPlayersKitStr(uuid);
	    	double health = player.getHealth();
	    	String playerName = player.getName();
	    	long timestamp = System.currentTimeMillis();
	    	DisconnectLog log = new DisconnectLog(uuid, playerName, health, kitName, timestamp, team.getName(), player.getLocation());
	    	game.getDisconnectLog().put(uuid, log);
	    	return;
	    }

	    if (team != null)
	    	if(game.isLive())
	    		game.setPlayState(player, PlayState.OUT);
	    
	    playerLeave(player);
	}
	
	@EventHandler
	public void onTeamJoin(PlayJoinTeamEvent e)
	{
		Player player = e.getPlayer();
		Team team = e.getTeam();
		
		DebugCommands.message(player, "Called PlayJoinTeamEvent " + team);
		if(e.isCancelled()) return;
		ChatColor chatColor = team.getPrefix();
		if(chatColor == null) return;
		this.addPreTeam(player, chatColor);
	}
	
	public void addPreTeam(Player player, ChatColor color)
	{
		org.bukkit.scoreboard.Team team = this.defaultScoreboard.getScoreboard().getTeam(color.name());
		DebugCommands.message(player, "Color " + color.name() + "'s team " + team);
		if(team != null)
			team.addEntry(player.getName());
	}
	
	public void clearPreTeam()
	{
		for(org.bukkit.scoreboard.Team team : this.defaultScoreboard.getScoreboard().getTeams())
			for(String entry : team.getEntries())
				if(!entry.contains(ChatColor.COLOR_CHAR + ""))
					team.removeEntry(entry);
	}
	
//	@EventHandler(priority=EventPriority.LOWEST)
//	public void onPlayerLoginAllow(PlayerLoginEvent e)
//	{
//		if((!this.isRunning()) || (this.getGame().quitOut))
//			return;
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		
//		DisconnectLog log = this.getGame().getDisconnectLog().get(uuid);
//		Team team = this.getGame().getTeam(log.team);
//		team.addMember(uuid); //Reconnected
//		Kit kit = 
//		player.setHealth(log.health);
//		
//		
//	    GameTeam team = (GameTeam)this.RejoinTeam.remove(event.getPlayer().getName());
//	    if ((team != null) && (this._rejoinTime.remove(event.getPlayer().getName()) != null))
//	    {
//	      team.AddPlayer(event.getPlayer(), true);
//	      Announce(team.GetColor() + C.Bold + event.getPlayer().getName() + " has reconnected!", false);
//
//	      Kit kit = (Kit)this.RejoinKit.remove(event.getPlayer().getName());
//	      if (kit != null) {
//	        this._playerKit.put(event.getPlayer(), kit);
//	      }
//
//	      return;
//	    }
//	}
	
	public class GameSummary
	{
		public Class<? extends Game> clazz;
		public String displayName;
		public String[] description;
		public ItemStackBuilder icon;
		
		public GameSummary(Class<? extends Game> clazz, String displayName, String[] description, ItemStackBuilder icon)
		{
			this.clazz = clazz;
			this.displayName = displayName;
			this.description = description;
			this.icon = icon;
		}
	}
}
