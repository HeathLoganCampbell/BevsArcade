package com.bevelio.arcade.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.configs.files.TranslationConfig;
import com.bevelio.arcade.types.Reward;

public class RewardManager 
{
	//+10 Gems for Participation
	private HashMap<UUID, List<Reward>> rewards = new HashMap<>();
	
	public void addReward(UUID uuid, Reward reward)
	{
		List<Reward> listOfRewards = new ArrayList<>();
		if(rewards.containsKey(uuid))
			listOfRewards = rewards.get(uuid);
		listOfRewards.add(reward);
		rewards.put(uuid, listOfRewards);
	}
	
	public List<Reward> getRewards(UUID uuid)
	{
		return this.rewards.get(uuid);
	}
	
	public void applyReward(Player player)
	{
		List<Reward> listRewards = this.getRewards(player.getUniqueId());
		TranslationConfig tc = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig();
		
		player.sendMessage(tc.getRewardMessageHead());
		for(Reward reward : listRewards)
		{
			String rewardMsg = ArcadePlugin.getInstance().getConfigManager().getTranslationConfig().getRewardMessageContent();
			rewardMsg = rewardMsg.replaceAll("%Amount%", reward.amount + "")
								 .replaceAll("%Reason%", reward.reason);
			player.sendMessage(rewardMsg);
//			ArcadePlugin.getInstance().getHookManager().getIMoney().increaseBalance(player.getUniqueId(), reward.amount);
		}
		player.sendMessage(tc.getRewardMessageFooter());
	}
	
	public void clearRewards(UUID uuid)
	{
		rewards.remove(uuid);
	}
	
	public void clearAllRewards()
	{
		rewards.clear();
	}
}
