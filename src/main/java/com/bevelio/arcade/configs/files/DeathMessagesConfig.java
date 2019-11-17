package com.bevelio.arcade.configs.files;

import java.util.HashMap;

import com.bevelio.arcade.configs.BaseConfig;
import com.bevelio.arcade.misc.CC;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeathMessagesConfig extends BaseConfig
{
	private String[] normal_FellOutOfWorld = { "%Player% fell out of the world" };
	private String[] normal_PushedOutOfWorld = { "%Player% was pushed out of the world by %Attacker%" };
	private String[] normal_ShotOutOfWorld = { "%Player% was shot out of the world by %Attacker%" };
	private String[] normal_HitTheGroundTooHard = { "%Player% hit the ground too hard" };
	private String[] normal_BurntToACrisp = { "%Player% was burnt to a crisp" };
	private String[] normal_Drowned = { "%Player% drowned" };
	private String[] normal_BlewUp = { "%Player% blew up" };
	private String[] normal_Starved = { "%Player% starved", };
	private String[] normal_Potions = { "%Player% was killed by magic" };
	private String[] normal_Cactus = { "%Player% pricked to death" };
	private String[] normal_Wither = { "%Player% faded away" };
	private String[] normal_Thorns = { "%Player% was slain with %Attacker%'s thorn'ed armor" };
	private String[] normal_Lightning = { "%Player% was struck by lighting" };
	private String[] normal_Quiting = { "%Player% was killed because they quit" };
	private String[] normal_Shot = { "%Player% was shot by %Attacker%" };
	private String[] normal_Slain = { "%Player% was slain by %Attacker%" };
	private String[] normal_FlatternedByAnAnvil = { "%Player% was squashed by a anvil" };
	private String[] normal_FlatternedByAFallingBlock = { "%Player% was squished by an block" };
	private String[] normal_Suffocated = { "%Player% suffocated" };
	private String[] normal_Unknown = { "%Player% died" };
	
	private String[] simple_Attacker_Slain = { CC.gray + "%Player% was killed by you" };
	private String[] simple_Player_Slain = { CC.gray + "You were killed by %Attacker%" };
	private String[] simple_Unknown = { CC.gray + "%Player% died" };
	
	
	public DeathMessagesConfig()
	{
		super("death");
	}
}