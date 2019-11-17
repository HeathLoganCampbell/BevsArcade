package com.bevelio.arcade.games;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.commands.DebugCommands;
import com.bevelio.arcade.configs.MiniGamesConfig;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.events.PlayerPlayStateEvent;
import com.bevelio.arcade.managers.GameManager;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.module.component.MicroComponent;
import com.bevelio.arcade.types.Ability;
import com.bevelio.arcade.types.DisconnectLog;
import com.bevelio.arcade.types.GameState;
import com.bevelio.arcade.types.Kit;
import com.bevelio.arcade.types.PlayState;
import com.bevelio.arcade.types.Team;
import com.bevelio.arcade.types.WorldData;
import com.bevelio.arcade.utils.MathUtils;

import lombok.Getter;
import lombok.Setter;

public abstract class Game implements Listener
{
	private @Getter String gameTypeName;
	private @Getter @Setter String displayName;
	private @Getter @Setter String[] description;
	private @Getter @Setter GameState registerAtState = GameState.PREGAME;
	private @Getter @Setter WorldData worldData;
	private @Getter @Setter Scoreboard gameScoreboard;
	
	public @Getter @Setter MiniGamesConfig configs;
	
	private @Getter long startTimeStamp;
	protected @Getter long gameTimeOut = 360000l;//6 Minutes
	private @Getter @Setter ItemStackBuilder icon;
	
	public boolean removeArrows = true;
	
	public boolean breakBlocks = true;
	public HashSet<Material> breakBlocksAllowed = new HashSet<>();
	public HashSet<Material> breakBlocksDeny = new HashSet<>();
	
	public boolean placeBlocks = false;
	public HashSet<Material> placeBlocksAllow = new HashSet<>();
	public HashSet<Material> placeBlocksDeny = new HashSet<>();
	
	public boolean dropItems = false;
	public HashSet<Material> dropItemsAllow = new HashSet<>();
	public HashSet<Material> dropItemsDeny = new HashSet<>();
	
	public boolean pickUpItems = false;
	public HashSet<Material> pickUpItemsAllow = new HashSet<>();
	public HashSet<Material> pickUpItemsDeny = new HashSet<>();
	
	public boolean worldWeatherEnabled = false;
	public boolean worldBlockBurnEnabled = false;
	public boolean worldFireSpreadEnabled = false;
	public boolean worldLeavesDecayEnabled = false;
	public boolean worldSoilTrampleEnabled = false;
	public boolean worldBoneMealEnabled = false;
	public boolean worldOutOfBoundaryDamageEnabled = false;
	public double worldOutOfBoundaryDamagePerSecond = 0.5;
	
	public int allowedTeamDifference = 1;
	
	public boolean waterDamageEnabled = false;
	public double waterDamagePerSecond = 0.5;
	
	public boolean inventoryOpenBlockEnabled = false;
	public boolean inventoryOpenChestEnabled = false;
	public boolean inventoryClickEnabled = false;
	
	public boolean pregameFreeze = true;
	public boolean pregameActionbarCountDown = true;
	
	public boolean soloTeamMode = false;
	
	public double maxRejoinSeconds = 120;
	public boolean deathOut = false;
	public boolean deathDropItems = false;
	public boolean deathMessages = false;
	public boolean automaticRespawn = false;
	public double deathSpecatatorSeconds = 2.0;
	public boolean quitOut = true;
	public boolean quitDropItems = false;
	public boolean idleKick = false;
	
	public double healthSet = -1; //default
	public double healthMaxSet = -1;
	public double hungerSet = -1; //default
	
	public boolean joinInProgress = false;
	public boolean disableKillCommand = false;
	public boolean privateBlocks = false;
	
	public boolean LivingEntitiesAllowed = false;
	public boolean LivingEntitiesAllowedOverride = false;
	
	public Location spectatorSpawn = null;
	public boolean teamArmor = false;
	public boolean armorCanBeTakenOff = false;
	public boolean teamArmorHotbar = false;
	
	protected boolean damage = true;
	protected boolean damagePvP = true;
	protected boolean damagePvE = false;
	protected boolean damageEvP = false;
	protected boolean damageSelf = false;
	protected boolean damageFall = true;
	protected boolean damageOwnTeam = false;
	protected boolean damageOtherTeam = true;
	
	protected @Getter boolean winner;
	protected @Getter List<Player> winners = new ArrayList<Player>();
	private @Getter Team winnerTeams;
	
	private @Getter ArrayList<MicroComponent> microComponents = new ArrayList<>();
	private @Getter HashMap<String, Team> teams = new HashMap<>();
	private @Getter HashMap<UUID, PlayState> players = new HashMap<>();
	private @Getter HashMap<UUID, String> playersTeams = new HashMap<>();
	private @Getter HashMap<String, ArrayList<UUID>> playersPrefTeams = new HashMap<>();
	private @Getter HashMap<UUID, Long> respawnTimestamp = new HashMap<>();
	
	private @Getter HashMap<String, Kit> loadedKits = new HashMap<>();
	private HashMap<UUID, String> playersKit = new HashMap<>();
	private @Getter List<Listener> activeListeners = new ArrayList<>();
	private @Getter HashMap<UUID, DisconnectLog> disconnectLog = new HashMap<>();
	
	protected @Getter int preGameSeconds = GameState.PREGAME.getSeconds();
	private @Getter long preGameStartTimeStamp = 0;
	private @Getter long preGameEndTimeStamp = 0;
	
	private @Getter @Setter boolean published = false;
	
	public Game(String gameTypeName, String[] description, ItemStackBuilder icon)
	{
		this.gameTypeName = gameTypeName;
		this.description = description;
		this.icon = icon;
		
		this.gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		this.checkGameConfigs();
	}
	
	public void setConfigs(MiniGamesConfig minigamesConfigs)
	{
		this.configs = minigamesConfigs;
		ifNullConfigs();
		this.onServerStartUp();
	}
	
	public void checkGameConfigs()
	{
		if(configs == null)
		{
			configs = new MiniGamesConfig(this);
			ifNullConfigs();
			this.onServerStartUp();
		}
	}
	
	public void ifNullConfigs()
	{
		configs.setDisplayName(this.getGameTypeName());
		configs.setDescription(this.getDescription());
	}
	
	public void onServerStartUp()
	{
		try {
			configs.load();
			configs.loadConfig();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void registerTeam(Team team)
	{
		this.teams.put(team.getName(), team);
	}
	
	public void onWorldDataLoad(WorldData worldData)
	{
		this.teams = worldData.teams;
		loadKits();
	}
	
	public void setWorldData(WorldData worldData)
	{
		this.worldData = worldData;
		this.onWorldDataLoad(worldData);
		this.applyOption(worldData);
	}
	
	public void applyMicroComponents(WorldData worldData)
	{
		for(String compStr : worldData.microComponents)
		{
			Class<? extends MicroComponent> compClazz = ArcadePlugin.getInstance().getComponentManager().getMicroComponent(compStr);
			MicroComponent microComp = null;
			try {
				microComp = compClazz.getConstructor(Game.class).newInstance(null);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			if(microComp != null)
				this.addMicroComponent(microComp);
		}
	}
	
	public void addMicroComponent(MicroComponent microComponent)
	{
		this.microComponents.add(microComponent);
	}
	
	public void setSpawnOverride(boolean isOverride)
	{
		this.LivingEntitiesAllowed = isOverride;
		this.LivingEntitiesAllowedOverride = isOverride;
	}
	
	public void applyOption(WorldData worldData)
	{
		HashMap<String, String> data = worldData.data;
		if(data.containsKey("WorldWeatherEnabled"))
			this.worldWeatherEnabled = MathUtils.getBooleanicValue(data.get("WorldWeatherEnabled"));
		
		if(data.containsKey("WorldBlockBurnEnabled"))
			this.worldBlockBurnEnabled = MathUtils.getBooleanicValue(data.get("WorldBlockBurnEnabled"));

		if(data.containsKey("WorldFireSpreadEnabled"))
			this.worldFireSpreadEnabled = MathUtils.getBooleanicValue(data.get("WorldFireSpreadEnabled"));
		
		if(data.containsKey("WorldLeavesDecayEnabled"))
			this.worldLeavesDecayEnabled = MathUtils.getBooleanicValue(data.get("WorldLeavesDecayEnabled"));
		
		if(data.containsKey("WorldSoilTrampleEnabled"))
			this.worldSoilTrampleEnabled = MathUtils.getBooleanicValue(data.get("WorldSoilTrampleEnabled"));
		
		if(data.containsKey("WorldBoneMealEnabled"))
			this.worldBoneMealEnabled = MathUtils.getBooleanicValue(data.get("WorldBoneMealEnabled"));
		
		if(data.containsKey("WorldBoneMealEnabled"))
			this.worldBoneMealEnabled = MathUtils.getBooleanicValue(data.get("WorldBoneMealEnabled"));
		
		if(data.containsKey("WaterDamageEnabled"))
			this.waterDamageEnabled = MathUtils.getBooleanicValue(data.get("WaterDamageEnabled"));
		
		if(data.containsKey("WaterDamagePerSecond"))
			this.waterDamagePerSecond = Integer.parseInt(data.get("WaterDamagePerSecond"));
	}
	
	public void onPreStart()
	{
		
		for(Entry<String, Team> entryTeam : this.getTeams().entrySet())
		{
			Team team = entryTeam.getValue();
			for(Entry<String, Team> nestedEntryTeam : this.getTeams().entrySet())
			{
				Team nestedTeam = nestedEntryTeam.getValue();
				if(nestedTeam == team) continue;
				org.bukkit.scoreboard.Team nestedBukkitTeam = nestedTeam.getScoreboard().getScoreboard().registerNewTeam(team.getName());
				nestedBukkitTeam.setPrefix(team.getPrefix() + "");
			}
		}
		preGameStartTimeStamp = System.currentTimeMillis();
		preGameEndTimeStamp = (long) (System.currentTimeMillis() + (this.preGameSeconds * 1000));
		//sendScoreboards();
	}
	
	public void onStart()
	{
		for(Listener listener : this.activeListeners)
			this.register(listener);
		startTimeStamp = System.currentTimeMillis();
		this.getGameManager().setSeconds(this.getWorldData().maxSeconds);
		
//		for(Entry<UUID, PlayState> playerSet : this.getPlayers().entrySet())
//		{
//			Player player = Bukkit.getPlayer(playerSet.getKey());
//			if(player == null) continue;
//			Team team = this.getPlayersTeam(playerSet.getKey());
//			if(team != null)
//				team.getScoreboard().send(player);
//		}
	}
	
	public void onEnd()
	{
		for(Listener listener : this.activeListeners)
			this.unregister(listener);
		ArcadePlugin.getInstance().getGameManager().setGameState(GameState.FINISHING);
	}
	
	public void onFinish(Team winningTeam)
	{
		this.winnerTeams = winningTeam;
		this.winner = true;
		this.onEnd();
	}
	
	public void onFinish(List<Player> winingPlayers)
	{
		this.winners = winingPlayers;
		this.winner = true;
		this.onEnd();
	}
	
	public boolean isAlive(Player player)
	{
		if(this.getPlayState(player) != null)
			return this.getPlayState(player) == PlayState.IN;
		return false;
	}
	
	public void sendScoreboards()
	{
		for(Entry<UUID, PlayState> playerSet : this.getPlayers().entrySet())
		{
			Player player = Bukkit.getPlayer(playerSet.getKey());
			if(player == null) continue;
			Team team = this.getPlayersTeam(playerSet.getKey());
			if(team != null)
			{
				team.getScoreboard().send(player);
			}
		}
	}
	
	public void register(Listener listener, boolean addListener)
	{
		Bukkit.getPluginManager().registerEvents(listener, ArcadePlugin.getInstance());
		if(!addListener)
			this.activeListeners.add(listener);
	}
	
	public void register(Listener listener)
	{
		System.out.println(listener.getClass() + " was registered as an listener");
		Bukkit.getPluginManager().registerEvents(listener, ArcadePlugin.getInstance());
	}
	
	public void unregister(Listener listener)
	{
		HandlerList.unregisterAll(listener);
	}
	
	public void onStartAnnouncement()
	{
		for(String line : ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().startAnnouncementMessages)
		{
			line = line.replaceAll("%WorldName%", this.getWorldData().name)
					   .replaceAll("%GameType%", this.getWorldData().gameType)
					   .replaceAll("%Authors%", this.getWorldData().getAuthors())
					   ;
			
			if(line.contains("%Description%"))
			{
				for(String desc : this.getDescription())
				{
					String templine = line.replaceAll("%Description%", desc);
					this.broadcast(templine);
				}
				continue;
			}
			
			this.broadcast(line);
		}
	}
	
	public void onEndAnnouncement()
	{
		String[] lines = null;
		if(this.soloTeamMode == true)
			lines = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getEndSoloAnnouncementMessages();
		else 
			lines = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getEndTeamAnnouncementMessages();
		
		for(String line : lines)
		{
			line = line.replaceAll("%WorldName%", this.getWorldData().name)
					   .replaceAll("%GameType%", this.getWorldData().gameType)
					   .replaceAll("%Authors%", this.getWorldData().getAuthors())
					   ;
			
			if(this.soloTeamMode)
			{
				int announceNumOfwinners = lines.length;
				if(this.winners.size() > announceNumOfwinners) announceNumOfwinners = this.winners.size();
				if(5 > announceNumOfwinners) announceNumOfwinners = 5;
				
				for(int i = 0; i < announceNumOfwinners; i++)
				{
					line = line.replaceAll("%Winner_Player_Place_" + (i + 1) + "%",  this.winners.size() > i ? this.winners.get(i).getName() : "No one");
				}
			}
			
			if(this.winnerTeams != null)
			{
				line = line.replaceAll("%Winner_Team%", this.winnerTeams.getDisplayName(false));
			} else {
				line = line.replaceAll("%Winner_Team%", "No one");
			}
			
			if(line.contains("%Description%"))
			{
				for(String desc : this.getDescription())
				{
					line = line.replaceAll("%Description%", desc);
					this.broadcast(line);
				}
				continue;
			}
			
			this.broadcast(line);
		}
	}
	
	public boolean isInQueue(Player player)
	{
		return ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player);
	}
	
	public boolean isAllValid(Player player)
	{
		if(this.isLive() 
				&& this.isInQueue(player)
				&& this.isAlive(player))
				return true;
		return false;
	}
	
	public World getWorld()
	{
		return this.worldData.world;
	}
	
	public void addMember(Team team, Player player)
	{
		if(team == null) return;
		team.addMember(player.getUniqueId());
		this.getPlayersTeams().put(player.getUniqueId(), team.getName());
	}
	
	public Team getTeam(String name)
	{
		return this.getTeams().get(name);
	}
	
	public Team getPlayersTeam(UUID uuid)
	{
		String name = this.getPlayersTeams().get(uuid);
		if(name == null) return null;
		return this.getTeam(name);
	}
	
	public Team removeMemeber(UUID uuid)
	{
		String name = this.getPlayersTeams().get(uuid);
		if(name == null) return null;
		Team team = this.getTeam(name);
		this.getTeam(name).removeMember(uuid);
		this.getPlayersTeams().remove(uuid);
		return team;
	}
	
	public boolean isTeamAlive(Team team)
	{
		boolean isTeamAlive = false;
		for(Player player : Bukkit.getOnlinePlayers())
			if(ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player))
				if(team.isMember(player.getUniqueId()))
					if(this.getPlayState(player) == PlayState.IN)
						isTeamAlive = true;
		return isTeamAlive;
	}
	
	public void hanndleTeamPreferences()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player))
			{
				
				boolean isInTeam = false;
				
				for(Entry<String, ArrayList<UUID>> prefTeamSet : this.playersPrefTeams.entrySet())
				{
					if(prefTeamSet.getValue().contains(player.getUniqueId()))
					{
						isInTeam = true;
						break;
					}
				}
				
				if(!isInTeam)
				{
					String choiceTeam = null;
					int players = 100;
					
					for(Entry<String, Team> entry : this.getTeams().entrySet())
					{
						Team team = entry.getValue();
						if(choiceTeam == null || getPrefernceTeamSize(team.getName()) < players)
						{
							choiceTeam = team.getName();
							players = getPrefernceTeamSize(team.getName());
						}
					}

					setPrefernceTeam(choiceTeam, player);
				} 
			}
		}
	}
	
	public int getPrefernceTeamSize(String teamName)
	{
		if(this.playersPrefTeams.containsKey(teamName))
			return this.playersPrefTeams.get(teamName).size();
		return 0;
	}
	
	public void setPrefernceTeam(String teamName, Player player)
	{
		UUID uuid = player.getUniqueId();
		
		for(Entry<String, ArrayList<UUID>> prefTeamSet : this.playersPrefTeams.entrySet())
		{
			if(prefTeamSet.getValue().contains(uuid))
			{
				prefTeamSet.getValue().remove(uuid);
				this.playersPrefTeams.put(prefTeamSet.getKey(), prefTeamSet.getValue());
			}
		}
		
		ArrayList<UUID> teamPreferenceMembers = new ArrayList<>();
		if(this.playersPrefTeams.containsKey(teamName))
			teamPreferenceMembers = this.playersPrefTeams.get(teamName);
		teamPreferenceMembers.add(uuid);
		
		Team team = this.getTeam(teamName);
		if(team != null)
			this.getGameManager().addPreTeam(player, team.getPrefix());
		
		
		this.playersPrefTeams.put(teamName, teamPreferenceMembers);
	}
	
	private Team chooseTeam()
	{	
		Team choiceTeam = null;
		for(Entry<String, Team> entry : this.getTeams().entrySet())
		{
			Team team = entry.getValue();
			if(choiceTeam == null || team.size() < choiceTeam.size())
				choiceTeam = team;
		}
		return choiceTeam;
	}
	
	public void setPlayersKit(UUID uuid, String kitName)
	{
		this.playersKit.put(uuid, kitName);
	}
	
	public String getPlayersKitStr(UUID uuid)
	{
		return this.playersKit.get(uuid);
	}
	
	public Kit getPlayersKit(UUID uuid)
	{
		String kitStr = this.playersKit.get(uuid);
		if(this.worldData.defaultKit != null && kitStr == null)
			kitStr = this.worldData.defaultKit;
		Kit kit = this.loadedKits.get(kitStr);
		
		if(kitStr == null || kit == null)
			return Kit.NULL_KIT;
		return kit;
	}
	
	public void toSpectatorMode(Player player)
	{
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		
		Bukkit.getOnlinePlayers().forEach(viewer -> 
		{
			if(player != viewer && ArcadePlugin.getInstance().getGameManager().isInteractivePlayer(player))
				viewer.hidePlayer(player);
		});
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(1f);
		player.setExp(0);
		player.setLevel(0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1), true);
		player.setAllowFlight(true);
		player.setFlying(true);
		Vector vec = player.getLocation().getDirection().normalize();
		vec.multiply(0.3);
		vec.setY(1.25);
		player.setVelocity(vec);
	}
	
	public GameManager getGameManager()
	{
		return ArcadePlugin.getInstance().getGameManager();
	}
	
	public boolean canDamage(Player player, Player damager)
	{
		if(!(this.getGameManager().isInteractivePlayer(player) && this.getGameManager().isInteractivePlayer(damager)))
			return false;
		
		Team playersTeam = this.getPlayersTeam(player.getUniqueId());
		Team damagersTeam = this.getPlayersTeam(damager.getUniqueId());
		
		if((!this.damageSelf) && player == damager)
		{
			DebugCommands.message(player, "You can't damage self");
			return false;
		}
		if((!this.damagePvP) && player != damager)
		{
			DebugCommands.message(player, "You can't pvp");
			return false;
		}
		if((!this.damageOwnTeam) && playersTeam == damagersTeam)
		{
			DebugCommands.message(player, "You can't attack own team");
			return false;
		}
		if((!this.damageOtherTeam) && playersTeam != damagersTeam)
		{
			DebugCommands.message(player, "You can't attack other team");
			return false;
		}
		return true;
	}
	
	
	public void respawnPlayer(Player player)
	{
		this.respawnPlayer(player, true);
	}
	
	public void respawnPlayer(Player player, boolean applyKit)
	{
		this.setPlayState(player, PlayState.IN);
		DebugCommands.message(player, "Setting play state.");
		if(this.getPlayState(player) == PlayState.OUT) return;
		
		player.setFlying(false);
		player.setAllowFlight(false);
		DebugCommands.message(player, "You have been respawned.");
		
		player.setMaxHealth(healthMaxSet == -1 ? 20 : this.healthMaxSet);
		player.setHealth(this.healthSet == -1 ? 20 : this.healthSet);
		player.setFoodLevel((int) (this.hungerSet == -1 ? 20 : this.hungerSet));
		player.setSaturation(1f);
		
		Bukkit.getOnlinePlayers().forEach(viewer ->  
		{
			if(viewer != player)
				if(!viewer.canSee(player))
					viewer.showPlayer(player);
		});
		
		Team team = this.getPlayersTeam(player.getUniqueId());
		DebugCommands.message(player, "Is Team null for you? " + DebugCommands.spec(team == null ? "Yes!" : "No"));
		Location loc = team.getNewSpawnLocation(this.getWorld());
		loc.getChunk().load();
		
		DebugCommands.message(player, "Is spawn location null for you? " + DebugCommands.spec(loc == null ? "Yes!" : "No"));
		DebugCommands.message(player, "spawn location info " + DebugCommands.spec(loc.toString()));
		
		team.getScoreboard().send(player);
		
		if(applyKit)
		{
			Kit kit = this.getPlayersKit(player.getUniqueId());
			DebugCommands.message(player, "Default Kit " + DebugCommands.spec(this.worldData.defaultKit));
			if(kit.getName() == Kit.NULL_KIT.getName() && this.worldData.defaultKit !=null)
			{
				kit = this.loadedKits.get(this.worldData.defaultKit);
				if(kit == null)
				{
					DebugCommands.message(player, "Failed to use the default kit " + CC.white + this.worldData.defaultKit + CC.gray + "!");
					kit = Kit.NULL_KIT;
				}
			}
				
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
			kit.apply(player);
			DebugCommands.message(player, "You are using the kit " + CC.white + kit.getName() + CC.gray + "!");
			
			if(this.getRespawnTimestamp().containsKey(player.getUniqueId()))
				this.getRespawnTimestamp().remove(player.getUniqueId());
		}
		
		player.teleport(loc);
	}
	
	public void setPlayState(Player player, PlayState playstate)
	{
		UUID uuid = player.getUniqueId();
		PlayState from = null;
		if(this.getPlayers().containsKey(uuid))
			from = this.getPlayers().get(uuid);
		PlayerPlayStateEvent event = new PlayerPlayStateEvent(player, playstate, from);
		Bukkit.getPluginManager().callEvent(event);
		
		if(!event.isCancelled())
			this.getPlayers().put(player.getUniqueId(), event.getTo());
		DebugCommands.message(player, "Was the event Cancelled? " + (event.isCancelled() ? "Yes!" : "No"));
		DebugCommands.message(player, "Players play state is now " + DebugCommands.spec(this.getPlayers().get(uuid) + ""));
	}
	
	public PlayState getPlayState(Player player)
	{
		UUID uuid = player.getUniqueId();
		return this.getPlayers().get(uuid);
	}
	
	public List<Player> getAlivePlayers(Team team)
	{
		return this.getPlayers().entrySet().stream().filter(set -> set.getValue() == PlayState.IN).map(set -> Bukkit.getPlayer(set.getKey())).filter(player -> this.getPlayersTeam(player.getUniqueId()) == team).collect(Collectors.toList());
	}
	
	
	public List<Player> getAlivePlayers()
	{
		return this.getPlayers().entrySet().stream().filter(set -> set.getValue() == PlayState.IN).map(set -> Bukkit.getPlayer(set.getKey())).collect(Collectors.toList());
	}
	
	public abstract void checkEnd();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		if(!this.isAlive(player)) return;
		if(this.getGameManager().getGameState() != GameState.PREGAME) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		if(isLive()) return;
		if(!this.isAlive(e.getPlayer())) return;
		if(e.getTo().getWorld() != this.getWorld()) return;
		if(e.getTo().distance(e.getFrom()) != 0)
			if(this.pregameFreeze && ArcadePlugin.getInstance().getGameManager().getGameState() == GameState.PREGAME)
			{
				e.getFrom().setPitch(e.getTo().getPitch());
				e.getFrom().setYaw(e.getTo().getYaw());
				e.getFrom().setY(e.getTo().getY());
				e.setTo(e.getFrom());
//				e.setCancelled(true);
			}
	}
	
	@EventHandler
	public void onCustomDamage(CustomDamageEvent e)
	{
		if(!isLive()) return;
		if(e.getPlayer() != null && e.getDamagerPlayer() != null)
			if(!this.canDamage(e.getPlayer(),  e.getDamagerPlayer()))
				e.setCancelled("Attacking type disabled");
		Player player = e.getPlayer();
		if(player != null)
		{
			if(this.getPlayState(player) == PlayState.OUT)
				e.setCancelled("Already Dead");
		}
		
		if(e.getCause() == DamageCause.VOID)
			e.addMod("Void", "Falling of the the world", 9999, false);
	}
	
	public boolean isLive()
	{
		return this.getGameManager().getGameState() == GameState.LIVE;
	}
	
	public void loadAbilities(Kit kit)
	{
		for(String abilityName : kit.getAbilities())
		{
			HashMap<String, Object> options = null;
			if(abilityName.contains("("))
			{
				String[] splitIdAndParam = abilityName.split("\\(");
				abilityName = splitIdAndParam[0];
				splitIdAndParam[1] = splitIdAndParam[1].replaceAll("\\)", "");
				options = this.getOptions(splitIdAndParam[1]);
			}
			
			Ability abs = ArcadePlugin.getInstance().getAbilityManager().getAbility(abilityName);
			if(abs == null)
			{
				System.out.println("Failed to find the ability " + abilityName);
				continue;
			}
			this.activeListeners.add(abs);
			abs.setParent(kit);
			if(options != null)
				abs.setOptions(options);
		}
	}
	
	public HashMap<String, Object> getOptionsFromString(String str)
	{
		HashMap<String, Object> options = null;
		if(str.contains("("))
		{
			String[] splitIdAndParam = str.split("\\(");
			str = splitIdAndParam[0];
			splitIdAndParam[1] = splitIdAndParam[1].replaceAll("\\)", "");
			options = this.getOptions(splitIdAndParam[1]);
		}
		return options;
	}
	
	//ArcherSpleef(seconds=2.2,)
	public HashMap<String, Object> getOptions(String optionStr)
	{
		String[] values = optionStr.split(",");
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(String line : values)
		{
			if(!line.contains("="))
				continue;
			String[] args = line.split("=");
			String field = args[0];
			String value = args[1];
			Object finalValue = value;
			
			if(MathUtils.isNumeric(value))
			{
				finalValue = Double.parseDouble(value);
			} 
			else if(value.equalsIgnoreCase("true"))
			{
				finalValue = true;
			}
			else if(value.equalsIgnoreCase("false"))
			{
				finalValue = false;
			}
			
			map.put(field, finalValue);
		}
		
		return map;
	}
	
	public void registerKit(String name)
	{
		name = name.toLowerCase();
		System.out.println("New kit registered " + name);
		Kit kit = this.getGameManager().getKitConfig().loadKit(name);
		if(kit == null) {
			System.out.println("Failed to load the kit " + name);
			return;
		}
		loadedKits.put(kit.getName().toLowerCase(), kit);
		loadAbilities(kit);
	}
	
	public void loadKits()
	{
		this.worldData.kits.forEach(kitStr -> {
			this.registerKit(kitStr);
		});
	}
	
	public void registerKits()
	{
		this.loadedKits.forEach((key, value) ->{
			value.getAbilities();
		});
	}
	
	public void unregisterKits()
	{
		this.loadedKits.clear();
	}

	public void updateScoreboard() 
	{
		
	}
	
	public void broadcast(String message)
	{
		this.getGameManager().broadcast(message);
	}
	
	

//	public void onLoadDetails() 
//	{
//		try {
//			System.out.println("Loading data!");
//			this.worldData = new WorldData(ArcadePlugin.getInstance().getWorldManager().getNextWorld().getName());
//			this.worldData.load();
//			this.worldData.loadConfig();
//			this.onWorldDataLoad(worldData);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
	
}
