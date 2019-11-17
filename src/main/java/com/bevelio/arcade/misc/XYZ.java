package com.bevelio.arcade.misc;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.bevelio.arcade.utils.MathUtils;

public class XYZ implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	public double x, y, z;
	public float yaw, pitch;
	public String worldName;
	
	public XYZ(Location location) 
	{
		this(MathUtils.round(location.getX(),2),
			 MathUtils.round(location.getY() + 0.5,2),
			 MathUtils.round(location.getZ(),2), 
			(float) MathUtils.round(location.getPitch(),2), 
			(float) MathUtils.round(location.getYaw(),2));
		this.worldName = location.getWorld().getName();
	}
	
	public XYZ() 
	{
		this(0, 0, 0, 0, 0);
	}

	public XYZ(double x, double y, double z) 
	{
		this(x, y, z, 0, 0);
	}

	public XYZ(double x, double y, double z, float pitch, float yaw) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public double getBlockX()
	{
		return MathUtils.floor((float) this.x);
	}
	
	public double getBlockY()
	{
		return MathUtils.floor((float) this.y);
	}
	
	public double getBlockZ()
	{
		return MathUtils.floor((float) this.z);
	}
	
	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setWorld(World world)
	{
		this.worldName = world.getName();
	}
	
	public void setWorld(String worldName)
	{
		this.worldName = worldName;
	}
	
	public World getWorld()
	{
		if(this.worldName == null)
			return null;
		return Bukkit.getWorld(this.worldName);
	}
	
	public Location toLocation(World world)
	{
		Location location = new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
		return location;
	}
	
	private double getValue(String number)
	{
		if(MathUtils.isFloat(number) || MathUtils.isNumeric(number))
			return Double.parseDouble(number);
		return 0;
	}
	
	public String serialize()
	{
		return  this.x + "%" + this.y + "%" + this.z + "%" + this.yaw + "%" + this.pitch + ( this.worldName != null ? "%" + this.worldName : "" );
	}
	
	public void deserialize(String value)
	{
		if(value == null) return;
		String[] split = value.split("%");
//		System.out.println("XYZ ]- deserialize " + value);
		if(split.length >= 3)
		{
			this.x = this.getValue(split[0]);
			this.y = this.getValue(split[1]);
			this.z = this.getValue(split[2]);
			if(split.length >= 5)
			{
				this.yaw = (float) this.getValue(split[3]);
				this.pitch = (float) this.getValue(split[4]);
				if(split.length >= 6)
					this.worldName = split[5];
			}
		}
	}
	
	@Override
	public String toString()
	{
		return String.format("XYZ( %s, %s, %s)", this.x, this.y, this.z);
	}
}
