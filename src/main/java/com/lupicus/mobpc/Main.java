package com.lupicus.mobpc;

import com.lupicus.mobpc.config.MyConfig;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Main.MODID)
public class Main
{
	public static final String MODID = "mobpc";

	public Main()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
	}
}
