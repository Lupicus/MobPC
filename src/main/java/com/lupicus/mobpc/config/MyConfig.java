package com.lupicus.mobpc.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lupicus.mobpc.Main;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig
{
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	static
	{
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static boolean includeAllItems;
	public static HashSet<Item> includeItemSet;

	public static boolean check(ItemStack stack)
	{
		return (includeAllItems) ? true : includeItemSet.contains(stack.getItem());
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}

	public static void bakeConfig()
	{
		String[] temp = extract(COMMON.includeItems.get());
		includeAllItems = hasAll(temp);
		if (includeAllItems)
			temp = new String[0];
		includeItemSet = itemSet(temp, "IncludeItems");
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
						LOGGER.warn("Unknown entry in " + configName + ": " + entry);
				}
				catch (Exception e)
				{
					LOGGER.warn("Bad entry in " + configName + ": " + entry);
				}
			}
		}
		return ret;
	}

	private static String[] extract(List<? extends String> value)
	{
		return value.toArray(new String[value.size()]);
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
				for (DyeColor dye : DyeColor.values())
				{
					ret.add(ns + dye.toString() + "_" + type);
				}
			}
		}
		return ret;
	}

	public static class Common
	{
		public final ConfigValue<List<? extends String>> includeItems;

		public Common(ForgeConfigSpec.Builder builder)
		{
			List<String> includeItemsList = Arrays.asList(
					"minecraft:stick", "minecraft:apple", "minecraft:dirt", "minecraft:cobblestone",
					"minecraft:egg", "minecraft:feather", "minecraft:chicken",
					"minecraft:rotten_flesh", "minecraft:arrow", "minecraft:bone", "minecraft:string",
					"minecraft:wheat_seeds", "minecraft:ink_sac");
			String baseTrans = Main.MODID + ".config.";
			String sectionTrans;

			sectionTrans = baseTrans + "general.";
			includeItems = builder
					.comment("Include Items that prevent marking the persistent flag")
					.translation(sectionTrans + "include_items")
					.defineList("IncludeItems", includeItemsList, Common::isString);
		}

		public static boolean isString(Object o)
		{
			return (o instanceof String);
		}
	}
}
