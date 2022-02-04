package com.lupicus.mobpc;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.lupicus.mobpc.config.MyConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "mobpc";
    public static final String NAME = "Mob Population Control";
    public static final String VERSION = "1.0.0.0";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MyConfig.bakeConfig();
    }

    @NetworkCheckHandler
    public boolean checkVersion(Map<String, String> modList, Side side)
    {
    	return true;
    }
}
