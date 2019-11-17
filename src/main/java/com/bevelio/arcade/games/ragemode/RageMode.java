package com.bevelio.arcade.games.ragemode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.bevelio.arcade.ArcadePlugin;
import com.bevelio.arcade.events.CustomDamageEvent;
import com.bevelio.arcade.games.SoloGame;
import com.bevelio.arcade.games.oitc.OITC.OITCScore;
import com.bevelio.arcade.misc.ItemStackBuilder;
import com.bevelio.arcade.types.BlockData;

public class RageMode extends SoloGame 
{
	private String shortRange = "_SWORD";
	private String midRange = "_AXE";
	private float explodePower = 1.2f;
	private List<BlockData> breakableBlocks = (List<BlockData>) Arrays.asList(new BlockData(Material.THIN_GLASS));
	
	public RageMode() 
	{
		super("RageMode"
			 , new String[] {
					 		  "Everything one hits"
					 		, "Most kills to win"
					 		}
			 , new ItemStackBuilder(Material.COAL_BLOCK));
		
		this.deathOut = false;
		this.quitOut = true;
		this.automaticRespawn = true;
		this.deathSpecatatorSeconds = 1.2;
		this.hungerSet = 20;
		this.breakBlocks = false;
		this.placeBlocks = false;
	}
	
	@Override
	public void checkEnd()
	{
//		this.onFinish(this.getWinners());
	}
	
	@Override
	protected void setLine(String line, int slot)
	{
		super.setLine(line, slot);
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e)
	{
		for(Block block :  e.blockList())
		{
			if(block == null) continue;
			BlockData blockData = new BlockData(block.getType(), block.getData());
			if(!breakableBlocks.contains(blockData)) continue;
			e.blockList().remove(block);
		}
	}

	@EventHandler
	public void onArrowHit(CustomDamageEvent e)
	{
		if(e.getDamagerPlayer() == null) return;
		
		if(e.getDamagerPlayer().getItemInHand() != null)
		{
			if(e.getDamagerPlayer().getItemInHand().getType().name().contains(this.shortRange))
			{
				e.addMod("Short Range Damage", "Insta Kill", 999, false);
				
			}
		}
		
		if(e.getProjectile() == null) return;
		if(!(e.getProjectile() instanceof Arrow)) return;
		
		
		e.addMod("Arrow Damage", "Insta Kill", 999, false);
		Location loc = e.getProjectile().getLocation();
		loc.getWorld().createExplosion(loc, explodePower);
		e.getProjectile().remove();
		
	}
}
