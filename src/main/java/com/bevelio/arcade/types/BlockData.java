package com.bevelio.arcade.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.bevelio.arcade.expcetion.ConfigureErrorException;

public class BlockData 
{
	private Material material;
	private byte data = 0;
	
	public BlockData(Material material, byte data)
	{
		this.material = material;
		this.data = data;
	}
	
	public BlockData(Material material)
	{
		this.material = material;
	}
	
	public BlockData()
	{
	}

	public Material getMaterial() 
	{
		return material;
	}

	public void setMaterial(Material material) 
	{
		this.material = material;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) 
	{
		this.data = data;
	}
	
	public void parseBlockData(Block block)
	{
		this.material = block.getType();
		this.data = block.getData();
	}
	
	public void parseBlockData(ItemStack item)
	{
		this.material = item.getType();
		this.data = item.getData().getData();
	}
	
	public String serialize() throws ConfigureErrorException
	{
		if(this.material == null)
			throw new ConfigureErrorException("Material can not be null!");
			
		return String.format("%s:%s", this.material.name(), this.data);
	}
	
	public void deserialize(String blockDataStr) throws ConfigureErrorException
	{
		String[] split = blockDataStr.split(":");
		Material material = Material.AIR;
		byte data = 0;
		
		String matStr = split[0];
		for(Material materialValue : Material.values())
			if(materialValue.name().equalsIgnoreCase(matStr))
				material = materialValue;
		
		if(split.length == 2)
			data = Byte.parseByte(split[1]);
		
		this.material = material;
		this.data = data;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(!(object instanceof BlockData))
			return false;
		BlockData bd = (BlockData) object;
		if(bd.getMaterial() != this.getMaterial())
			return false;
		if(bd.getData() != this.getData())
			return false;
		return true;
	}
}
