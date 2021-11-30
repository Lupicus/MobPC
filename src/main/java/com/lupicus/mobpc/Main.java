package com.lupicus.mobpc;

import com.lupicus.mobpc.config.MyConfig;

import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

@Mod(Main.MODID)
public class Main
{
	public static final String MODID = "mobpc";

	public Main()
	{
		ModLoadingContext.get().registerExtensionPoint(DisplayTest.class,
				() -> new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
	}
}
