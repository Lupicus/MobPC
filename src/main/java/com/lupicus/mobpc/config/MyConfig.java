package com.lupicus.mobpc.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.lupicus.mobpc.Main;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Main.MODID)
@Config(modid = Main.MODID)
public class MyConfig
{
	@Config.Comment("Include Items that prevent setting the persistent flag when picked up")
	public static String[] includeItems = new String[] {"minecraft:stick", "minecraft:apple", "minecraft:dirt", "minecraft:cobblestone",
			"minecraft:egg", "minecraft:feather", "minecraft:chicken",
			"minecraft:rotten_flesh", "minecraft:arrow", "minecraft:bone", "minecraft:string",
			"minecraft:wheat_seeds", "minecraft:dye"};

	@Ignore
	public static boolean includeAllItems;
	@Ignore
	public static HashSet<Item> includeItemSet;

	public static boolean check(ItemStack stack)
	{
		return (includeAllItems) ? true : includeItemSet.contains(stack.getItem());
	}

	@SubscribeEvent
	public static void onConfig(OnConfigChangedEvent event)
	{
		if (event.getModID().equals(Main.MODID))
		{
			ConfigManager.sync(event.getModID(), Config.Type.INSTANCE);
			bakeConfig();
		}
	}

	public static void bakeConfig()
	{
		String[] temp = includeItems;
		includeAllItems = hasAll(temp);
		if (includeAllItems)
			temp = new String[0];
		includeItemSet = itemSet(temp, "includeItems");
	}

	private static boolean hasAll(String[] values)
	{
		for (String name : values)
		{
			if (name.equals("*"))
				return true;
		}
		return false;
	}

	private static HashSet<Item> itemSet(String[] values, String configName)
	{
		HashSet<Item> ret = new HashSet<>();
		IForgeRegistry<Item> reg = ForgeRegistries.ITEMS;
		for (String name : values)
		{
			List<String> list = expandItem(name);
			for (String entry : list)
			{
				try {
					ResourceLocation key = new ResourceLocation(entry);
					if (reg.containsKey(key))
					{
						Item item = reg.getValue(key);
						ret.add(item);
					}
					else
						Main.logger.warn("Unknown entry in " + configName + ": " + entry);
				}
				catch (Exception e)
				{
					Main.logger.warn("Bad entry in " + configName + ": " + entry);
				}
			}
		}
		return ret;
	}

	private static List<String> expandItem(String name)
	{
		List<String> ret = new ArrayList<>();
		ret.add(name);
		int i = name.indexOf(':');
		if (i >= 0 && name.indexOf('*') > 0)
		{
			String ns = name.substring(0, i + 1);
			String temp = name.substring(i + 1);
			if (temp.startsWith("colorset*"))
			{
				String type = temp.substring(9);
				ret.clear();
				for (EnumDyeColor dye : EnumDyeColor.values())
				{
					ret.add(ns + dye.toString() + "_" + type);
				}
			}
		}
		return ret;
	}
}
