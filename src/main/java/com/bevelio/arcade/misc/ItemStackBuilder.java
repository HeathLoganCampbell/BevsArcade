package com.bevelio.arcade.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;

public class ItemStackBuilder
{
    private ItemStack itemStack;

    public ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = new ItemStack(itemStack);
    }
    

    @SuppressWarnings("deprecation")
	public ItemStackBuilder(int id, int amount, short s) {
        this.itemStack = new ItemStack(id, amount, s);
    }
    
	public ItemStackBuilder(Material material, int amount, short s) {
        this.itemStack = new ItemStack(material, amount, s);
    }

    public ItemStackBuilder itemStack(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        return this;
    }

    public ItemStackBuilder amount(int amount) {
        this.build().setAmount(amount);
        return this;
    }

    public ItemStackBuilder durability(int amount) {
        this.build().setDurability((short)amount);
        return this;
    }

    public ItemStackBuilder durabilityLeft(int amount) {
        this.build().setDurability((short)(this.build().getType().getMaxDurability() - amount));
        return this;
    }

    public ItemStackBuilder type(Material material) {
        this.build().setType(material);
        return this;
    }

	public ItemStackBuilder unbreakable(boolean unbreakable) {
        this.build().getItemMeta().spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemStackBuilder skullOwner(String name) {
        if (this.build().getType() == Material.SKULL_ITEM) {
            this.durability(3);
            SkullMeta skullMeta = (SkullMeta)this.build().getItemMeta();
            skullMeta.setOwner(name);
            this.build().setItemMeta((ItemMeta)skullMeta);
        }
        return this;
    }

    public ItemStackBuilder skullOwner(Player player) {
        return this.skullOwner(player.getName());
    }

	public ItemStackBuilder potion(Potion potion) {
        if (this.build().getType() == Material.POTION) {
            potion.apply(this.itemStack);
        }
        return this;
    }

    public ItemStackBuilder lore(String ... lore) {
        ItemMeta itemMeta = this.build().getItemMeta();
        itemMeta.setLore(Arrays.asList(lore));
        this.build().setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder lore(List<String> lore) {
        this.lore(lore.toArray(new String[lore.size()]));
        return this;
    }

    public ItemStackBuilder colourLessLore(String ... lore) {
        ItemMeta itemMeta = this.build().getItemMeta();
        itemMeta.setLore(this.convertToFriendlyLore(5, false, lore));
        this.build().setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder colourLessLore(List<String> lore) {
        this.colourLessLore(lore.toArray(new String[lore.size()]));
        return this;
    }

    public ItemStackBuilder addLore(List<String> lore) {
        List<String> currentLore = this.build().getItemMeta().getLore();
        if(currentLore == null)
        	currentLore = new ArrayList<>();
        currentLore.addAll(lore);
        ItemMeta itemMeta = this.build().getItemMeta();
        itemMeta.setLore(currentLore);
        this.build().setItemMeta(itemMeta);
        return this;
    }

    public List<String> getLore() {
        return this.build().getItemMeta().getLore();
    }

    public ItemStackBuilder displayName(String displayName) {
        ItemMeta itemMeta = this.build().getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)displayName));
        this.build().setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder leatherColour(Color color) {
        try {
            LeatherArmorMeta itemMeta = (LeatherArmorMeta)this.build().getItemMeta();
            itemMeta.setColor(color);
            this.build().setItemMeta((ItemMeta)itemMeta);
            return this;
        }
        catch (ClassCastException ex) {
            return this;
        }
    }
    
    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        this.build().addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder enchantment(Map<Enchantment, Integer> enchantments) {
        this.build().addUnsafeEnchantments(enchantments);
        return this;
    }

    public ItemMeta getItemMeta()
    {
    	return this.itemStack.getItemMeta();
    }
    
    public Material getType()
    {
    	return this.itemStack.getType();
    }
    
    public ItemStackBuilder addUnsafeEnchantment(Enchantment ench, int level)
    {
    	this.itemStack.addUnsafeEnchantment(ench, level);
    	return this;
    }
    
    public ItemStackBuilder setItemMeta(ItemMeta im)
    {
    	this.itemStack.setItemMeta(im);
    	return this;
    }
    
    public ItemStack build() {
        return this.itemStack;
    }

    private List<String> convertToFriendlyLore(int wordsPerLine, boolean translateColourCodes, String ... lines) {
        ArrayList<String> result = new ArrayList<String>();
        for (String line : lines) {
            String[] words;
            if (translateColourCodes) {
                line = ChatColor.translateAlternateColorCodes((char)'&', (String)line);
            }
            line = line.replaceAll(" +(\u00a7[a-f0-9]) +", "$1");
            String currentLine = ChatColor.WHITE.toString();
            int wordCounter = 0;
            int iterationCounter = 0;
            for (String word : words = line.split(" ")) {
                if (!word.equals("<br>")) {
                    currentLine = currentLine + word + " ";
                    ++wordCounter;
                    ++iterationCounter;
                } else {
                    wordCounter = 0;
                }
                boolean lineBreak = word.equals("<br>");
                if (!(!lineBreak && wordCounter % wordsPerLine == 0 || lineBreak && iterationCounter % wordsPerLine != 0) && iterationCounter != words.length - StringUtils.countMatches((String)line, (String)"<br>")) continue;
                currentLine = currentLine.replaceAll("(\u00a7[a-f0-9])(\u00a7[a-f0-9])+", "$2");
                result.add(currentLine.trim());
                currentLine = ChatColor.getLastColors((String)currentLine);
            }
            if (line.equals(lines[lines.length - 1])) continue;
            result.add("");
        }
        return result;
    }

	public String serialize() 
	{
		String itemStr = "";
		itemStr += this.getType().name() + " " + ( this.itemStack.getData().getData()) + " " + this.itemStack.getAmount();
		if(this.itemStack.hasItemMeta())
		{
			if(this.itemStack.getItemMeta().hasDisplayName())
				itemStr += " Name=" + this.itemStack.getItemMeta().getDisplayName().replaceAll(" ", "_").replace("§", "&");
			if(this.itemStack.getItemMeta().hasLore())
			{
				String lore = "";
				for(int i = 0; i < this.itemStack.getItemMeta().getLore().size(); i++)
				{
					lore += (i == 0 ? "" : "\\n") + this.itemStack.getItemMeta().getLore().get(i);
					lore = lore.replaceAll(" ", "_").replace("§", "&");
				}
				itemStr += " Lore=" + lore;
			}
		}
		return itemStr;
	}
}

