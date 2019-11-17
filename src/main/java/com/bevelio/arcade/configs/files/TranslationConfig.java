package com.bevelio.arcade.configs.files;

import java.util.HashMap;

import com.bevelio.arcade.configs.BaseConfig;
import com.bevelio.arcade.misc.CC;
import com.bevelio.arcade.types.GameState;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TranslationConfig extends BaseConfig
{
	public String prefix = CC.bAqua + "BevsArcade ";
	
	public String commandBuildBase = prefix + CC.gray + "Use '/build help' for build commands.";
	public String commandBuildHelpHeading = CC.gray + "################[ " + prefix + CC.r + CC.gray +"]################";
	public String commandBuildHelpFooting = CC.gray + "For more commands use '/Build help <PageNumber>'";
	public String[] commandBuildHelp = {
			this.command("build", "Does pretty much nothing."),
			this.command("build help", "Gives you a list of all the command of build and details of using build."),
			this.command("build createGame <WorldName> <GameType>", "Create a new build world for you to create a new map on."),
			this.command("build load <WorldName>", "Load a build world that was created before."),
			this.command("build createTeam <TeamName>", "Creates a new team for that world (Note teamname must be lowercase)."),
			this.command("build addSpawn <TeamName>", "Add a new spawn for a team, You can have as many as you like for each team."),
			this.command("build wand", "Gives you the region selecting tool."),
			this.command("build setCustoms <CustomName>", "Used with the wand to add large number of custom tagged blocks."),
			this.command("build addCustom <CustomName>", "Adds a custom block in the location you are standing (Note: Your location not under your location)."),
			this.command("build publish", "Used to push your map to the maps folder, so it can be ran on the game"),
	};
	
	public String commandOnlyNumber = prefix + CC.gray + "You can only enter in a number!";
	public String commandNotPlayingArcade = prefix + CC.gray + "You aren't currently playing!";
	public String commandPlayerNotFound = prefix + CC.gray + "%Player% was not found!";
	public String commandPlayerPermissionNeeded = prefix + CC.gray + "You do not have the permission to do this!";
	public String commandIncorrect = prefix + CC.gray + "%Command% can't be found as a command";
	public String commandUsage = prefix + CC.gray + "Usage: %Command%";
	public String commandOnlyPlayersCanDoThisCommand = prefix + CC.gray + "Players can only do this command!";
	
	public String commandLagMessage =  prefix + CC.gray + "Currently the server is running at %Lag_Prefix%%Ticks% ( %Percentage%% )" + CC.gray + ".";
	public String[] commandLagDataMessage = 
		{
				"",
				"",
				"",
				"",
				CC.aqua + "Ticks Per second:        %Ticks_Per_Sec%",
				CC.aqua + "Avg Ticks Per Second:    %Avg_Ticks_Per_Sec%",
				CC.aqua + "Memory Free:             %Memory_Free%",
				CC.aqua + "Memory Used:             %Memory_Used%"
		};
	
	public String commandJoinMessage = prefix + CC.gray + "You have joined the game!";
	public String commandJoinSenderMessage = prefix + CC.gray + "You have added %Player% to the arcade!";
	
	public String commandForceStartMessage = prefix + CC.gray + "The game will start in 3 seconds!";
	public String commandForceStartErrorMessage = prefix + CC.gray + "Your gamestate needs to be " + GameState.STARTING.getDisplayName() + " or " + GameState.WAITING.getDisplayName() + " for this command to work! You are currently in %GameState%";
	
	public String commandQuitMessage = prefix + CC.gray + "You have left the game!";
	public String commandQuitSenderMessage = prefix + CC.gray + "You have removed %Player% from arcade!";
	
	public String commandSkipMessage = prefix + CC.gray + "You have skipped this game";
	public String commandSkipBroadcast = prefix + CC.gray + "%Player% has skipped this game!";
	public String commandSkipOnlyCanDoCommandIfInAGame = prefix + CC.gray + "You cannot do this command because you aren't in a game!";
	
	public String commandReloadMessage = prefix + CC.gray + "Reloaded.";
	
	public String selectorKitClickMsg = prefix + CC.gray + "You have selected %Kit%!";
	
	public String worldErrorKickPlayersOnWorld = prefix + CC.gray + "There has been an error when reloading\nplease reconnect!";
	
	public String compassTrackingNoOneFound = CC.red + "No one found";
	public String compassTrackingTargetFound = CC.green + "Now tracking %Target%!";
	
	public String commandSetCountdownGameIsntStarting =  prefix + CC.gray + "The game isn't starting!";
	public String commandSetCountdownHigherThanZero =  prefix + CC.gray + "The number has to be greater than zero!";
	public String commandSetCountdownSuccess =  prefix + CC.gray + "The game will now start in %Seconds% Seconds.";
	
	public String commandSetLobbySpawnSuccess =  prefix + CC.gray + "New Spawn location set.";
	
	public String commandKitMessageYouCantChangeYourKitWhilePlaying =  prefix + CC.gray + "You cannot change you kit while playing!";
	public String commandKitMessageKitNotFound =  prefix + CC.gray + "You cannot use the kit %Kit% this round!";
	public String commandKitMessageYouDontHaveKit =  prefix + CC.gray + "You do not have the kit %Kit%!";
	public String commandKitMessage = prefix + CC.gray + "You have selected the " + CC.white + "%Kit%" + CC.gray + " kit!";
	public String[] startAnnouncementMessages = {
													CC.b + "###############################",
													"",
													CC.b + " %WorldName% (%GameType%)",
													CC.b + " by %Authors%",
													"",
													CC.gray + "   %Description%",
													"",
													CC.b + "###############################"
												};
	
	public String[] endTeamAnnouncementMessages = {
													CC.b + "###############################",
													"",
													CC.b + CC.bGold + " %Winner_Team% Wins",
													"",
													CC.b + "###############################"
												};
	
	public String[] endSoloAnnouncementMessages = {
													CC.b + "###############################",
													"",
													CC.b + CC.bGold + " 1st %Winner_Player_Place_1%",
													CC.b + CC.bYellow + " 2nd %Winner_Player_Place_2%",
													CC.b + CC.bGreen + " 3rd %Winner_Player_Place_3%",
													"",
													CC.b + "###############################"
												};
	public HashMap<Integer, String> lobbyCountdownBroadcasts = new HashMap<Integer, String>();
	
	public String rewardMessageHead = CC.gray + "################[ " + prefix + CC.r + CC.gray +"]################";
	public String rewardMessageContent = CC.aqua + "+%Amount% Coins" + CC.gray + " for " + CC.aqua + "%Reason%" + CC.gray;
	public String rewardMessageFooter = CC.gray + "################[ " + prefix + CC.r + CC.gray +"]################";
	
	public String teamPreferenceSelected =  prefix + CC.gray + "You have selected " + CC.white + "%Team%" + CC.gray + " Team!";
	public String teamPreferenceIllegal =  prefix + CC.gray + "You cannot join " + CC.white + "%Team%" + CC.gray + " Team at the moment!";
	public String teamPreferenceYouAreAlreadyOnThatTeam =  prefix + CC.gray + "You are already on " + CC.white + "%Team%" + CC.gray + "!";
	
	public String teamMenuSelectorTitle = "Select a team";
	public String kitMenuSelectorTitle = "Select a kit";
	
	public String gameStateLoadingArcade = "Loading";
	public String gameStateWaitingForPlayers = "Waiting";
	public String gameStateStartingGameSoon = "Starting";
	public String gameStateGracePeriod = "PreGame";
	public String gameStateActiveGame = "Live";
	public String gameStateFinishingGame = "Finishing";
	public String gameStateServerFinished = "Ended";
	
	public String pregameActionBarPercentageBarFilled = CC.green + CC.shadedBlock;
	public String pregameActionBarPercentageBarUnfilled = CC.red + CC.shadedBlock;
	public String pregameActionBarPrefix = CC.white + "Game Starting ";
	public String pregameActionBarSuffix = CC.white + " %Seconds% Second%SOrNot%";
	
	public String respawnActionBarMessage = CC.green + "Respawning in: %s second%SOrNot%";
	
	public String joinSignsLineOne = this.prefix;
	public String joinSignsLineTwo = "%Current_Map%";
	public String joinSignsLineThree = "%Players%";
	public String joinSignsLineFour = CC.gray + "(Click to join)";
	public String joinSignUnknownCurrentMap = "Unknown";
	
	public String signPlaceSuccess =  prefix + CC.gray + "Sign has been placed";
	public String signPlaceBroken =  prefix + CC.gray + "Sign has been broken";
	public String signToolName =  CC.gray + "BevsArcade Sign";
	public String signToolSuccessfullyGiven =  prefix + CC.gray + "Sign tool given!";
	
	
	public String command(String label, String description)
	{
		return CC.white + "/" + label + CC.aqua + " - " + CC.gray + description;
	}
	
	@Override
	public void loadConfig()
	{
		super.loadConfig();
		GameState.LOADING.setDisplayName(this.getGameStateLoadingArcade());
		GameState.WAITING.setDisplayName(this.getGameStateWaitingForPlayers());
		GameState.STARTING.setDisplayName(this.getGameStateStartingGameSoon());
		GameState.PREGAME.setDisplayName(this.getGameStateGracePeriod());
		GameState.LIVE.setDisplayName(this.getGameStateActiveGame());
		GameState.FINISHING.setDisplayName(this.getGameStateFinishingGame());
		GameState.ENDED.setDisplayName(this.getGameStateServerFinished());
	}
	
	public TranslationConfig()
	{
		super("translation");
		this.lobbyCountdownBroadcasts.put(60, prefix + CC.gray + "Arcade will continue in " + CC.white + "60 seconds" + CC.gray + "!");
		this.lobbyCountdownBroadcasts.put(45, prefix + CC.gray + "Arcade will continue in " + CC.white + "45 seconds" + CC.gray + "!");
		this.lobbyCountdownBroadcasts.put(30, prefix + CC.gray + "Arcade will continue in " + CC.white + "30 seconds" + CC.gray + "!");
		this.lobbyCountdownBroadcasts.put(15, prefix + CC.gray + "Arcade will continue in " + CC.white + "15 seconds" + CC.gray + "!");
		this.lobbyCountdownBroadcasts.put(10, prefix + CC.gray + "Arcade will continue in " + CC.white + "10 seconds" + CC.gray + "...");
		for(int i = 9; i > 0; i--)
		{
			this.lobbyCountdownBroadcasts.put(i,  CC.white + i + " second" + ( i <= 1 ? "" : "s" ) + CC.gray + "!");
		}
		
	}
}