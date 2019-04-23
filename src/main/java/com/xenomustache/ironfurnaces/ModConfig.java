package com.xenomustache.ironfurnaces;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = "ironfurnaces")
public class ModConfig {
    
    @RequiresMcRestart
    @Name("Iron Furnace Cook Time")
    public static int IronFurnaceCookTime = 100;
    
    @RequiresMcRestart
    @Name("Gold Furnace Cook Time")
    public static int GoldFurnaceCookTime = 66;

    @RequiresMcRestart
    @Name("Diamond Furnace Cook Time")
    public static int DiamondFurnaceCookTime = 50;

    @RequiresMcRestart
    @Name("Obsidian Furnace Cook Time")
    public static int ObsidianFurnaceCookTime = 50;

    @RequiresMcRestart
    @Name("Crystal Furnace Cook Time")
    public static int CrystalFunraceCookTime = 40;

    @Mod.EventBusSubscriber(modid = IronFurnaces.MODID)
	private static class EventHandler {
        @SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(IronFurnaces.MODID)) {
				ConfigManager.sync(IronFurnaces.MODID, Config.Type.INSTANCE);
			}
		}
	}
}