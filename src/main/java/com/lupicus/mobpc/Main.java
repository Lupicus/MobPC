package com.lupicus.mobpc;

import com.lupicus.mobpc.config.MyConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main
{
	public static final String MODID = "mobpc";

	public Main(FMLJavaModLoadingContext context)
	{
		context.registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
	}
}
