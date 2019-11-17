package com.bevelio.arcade.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.games.Game;
import com.bevelio.arcade.managers.GameManager;

import lombok.Getter;
import lombok.Setter;

public class Ability implements Listener
{
	private @Getter @Setter Kit parent;
	private @Getter @Setter String name;
	private @Getter @Setter String description;
	private HashMap<UUID, Long> cooldown = new HashMap<>();
	
	public Ability(Kit kit, String name, String description)
	{
		this.name = name;
		this.description = description;
		this.parent = kit;
		System.out.println("Ability was created " + name);
	}
	
	public Ability(String name, String description)
	{
		this(null, name, description);
	}
	
	public void setCooldown(UUID uuid, double seconds)
	{
		this.cooldown.put(uuid, System.currentTimeMillis() + ((long)(1000 * seconds)));
	}
	
	public long getMilliSeconds(UUID uuid)
	{
		if(!this.cooldown.containsKey(uuid))
			return 0l;
		return this.cooldown.get(uuid);
	}
	
	public double getRemainingSeconds(UUID uuid)
	{
		if(!this.cooldown.containsKey(uuid))
			return 0.0;
		return ((this.cooldown.get(uuid) - System.currentTimeMillis()) / 1000);
	}
	
	public boolean isActive(UUID uuid)
	{
		if(!this.cooldown.containsKey(uuid))
			return true;
		return this.cooldown.get(uuid) < System.currentTimeMillis();
	}
	
	public boolean isStillOnCooldown(UUID uuid)
	{
		return this.cooldown.get(uuid) > System.currentTimeMillis();
	}
	
	public boolean hasAbility(Player player)
	{
		UUID uuid = player.getUniqueId();
		GameManager gm = ArcadePlugin.getInstance().getGameManager();
		
		if(gm.getGameState() != GameState.LIVE) return false;
//		System.out.println("Game is live");
		if(gm.getGame() == null) return false;
//		System.out.println("Game is not null ");
		Game game = gm.getGame();
//		if(game.getPlayersKitStr(uuid) == null) return false;
//		System.out.println("Player has a kit ");
		if(!gm.isInteractivePlayer(player)) return false;
//		System.out.println("Player is playimg ");
		if(this.parent == null) return true;
		if(this.parent == game.getPlayersKit(uuid))
		{
//			System.out.println("player has parent kit ");
			return true;
		}
		return false;
	}
	
	public void setOptions(HashMap<String, Object> options)
	{
	}
}
