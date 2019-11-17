package com.bevelio.arcade.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.bevelio.arcade.misc.CC;

import net.md_5.bungee.api.ChatColor;

public class ItemUtils
{
	 public static ItemStack[] parseItem(String string) {
	        if (string == null)
	            return new ItemStack[] { null };
	        String[] args = string.split(" ");
	        try {
	            double amount = Integer.parseInt(args[2]);
	            ItemStack[] items = new ItemStack[(int) Math.ceil(amount / 64)];
	            if (items.length <= 0)
	                return new ItemStack[] { null };
	            for (int i = 0; i < items.length; i++) {
	                int id = MathUtils.isNumeric(args[0]) ? Integer.parseInt(args[0])
	                        : (Material.getMaterial(args[0].toUpperCase()) == null ? Material.AIR : Material.getMaterial(args[0]
	                                .toUpperCase())).getId();
	                if (id == 0) {
	                    System.out.print("Unknown item " + args[0] + "!");
	                    return new ItemStack[] { null };
	                }
	                ItemStack item = new ItemStack(id, (int) amount, (short) Integer.parseInt(args[1]));
	                String[] newArgs = Arrays.copyOfRange(args, 3, args.length);
	                for (String argString : newArgs) {
	                    if (argString.contains("Name=")) {
	                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
	                        if (CC.getLastColors(name).equals(""))
	                            name = ChatColor.WHITE + name;
	                        ItemMeta meta = item.getItemMeta();
	                        String previous = meta.getDisplayName();
	                        if (previous == null)
	                            previous = "";
	                        meta.setDisplayName(name + previous);
	                        item.setItemMeta(meta);
	                    } else if (argString.contains("Color=") && item.getType().name().contains("LEATHER")) {
	                        String[] name = argString.substring(6).split(":");
	                        int[] ids = new int[3];
	                        for (int o = 0; o < 3; o++)
	                            ids[o] = Integer.parseInt(name[o]);
	                        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
	                        meta.setColor(Color.fromRGB(ids[0], ids[1], ids[2]));
	                        item.setItemMeta(meta);
	                    } else if (argString.equalsIgnoreCase("UniqueItem")) {
	                        ItemMeta meta = item.getItemMeta();
	                        String previous = meta.getDisplayName();
	                        if (previous == null)
	                            previous = "";
	                        meta.setDisplayName(previous + "UniqueIdentifier");
	                        item.setItemMeta(meta);
	                    }
	                    if (argString.contains("Lore=")) {
	                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
	                        ItemMeta meta = item.getItemMeta();
	                        List<String> lore = meta.getLore();
	                        if (lore == null)
	                        {
	                        	lore = new ArrayList<String>();
	                        }
	                        for (String a : name.split("\\n"))
	                        {
	                            lore.add(a);
	                        }
	                        meta.setLore(lore);
	                        item.setItemMeta(meta);
	                    }
	                }
	                
	                for (int n = 0; n < newArgs.length; n++)
	                {
	                    Enchantment ench = Enchantment.getByName(newArgs[n]);
	                    if (ench == null)
	                    {
	                        ench = Enchantment.getByName(newArgs[n].replace("_", " "));
	                    }
	                    if (ench == null)
	                    {
	                        ench = Enchantment.getByName(newArgs[n].replace("_", " ").toUpperCase());
	                    }
	                    if (ench == null)
	                    {
	                        continue;
	                    }
	                    System.out.println("New Ench added to an item " + ench.getName());
	                    item.addUnsafeEnchantment(ench, Integer.parseInt(newArgs[n + 1]));
	                    n++;
	                }
	                amount = amount - 64;
	                items[i] = item;
	            }
	            return items;
	        } catch (Exception ex) {
	            String message = ex.getMessage();
	            if (ex instanceof ArrayIndexOutOfBoundsException)
	                message = "java.lang.ArrayIndexOutOfBoundsException: " + message;
	            System.out.print("Not allowed " + string  + " " + message);
	            ex.printStackTrace();
	        }
	        return new ItemStack[] { null };
	    }
}
