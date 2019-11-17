package com.bevelio.arcade.module.component;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.arcade.module.Module;
import com.bevelio.arcade.module.component.micros.crumble.Crumble;

public class ComponentManager extends Module
{
	private HashMap<String, Class<? extends MicroComponent>> microComponents = new HashMap<>();

	public ComponentManager(JavaPlugin plugin) 
	{
		super("Component", plugin);
		
		registerMicroComponent();
	}
	
	private void registerMicroComponent()
	{
		this.registerMicroComponent(Crumble.class);
	}
	
	public Class<? extends MicroComponent> getMicroComponent(String name)
	{
		return this.microComponents.get(name.toLowerCase());
	}

	public void registerMicroComponent(Class<? extends MicroComponent> microComponent)
	{
		this.microComponents.put(microComponent.getName().toLowerCase(), microComponent);
	}
}
