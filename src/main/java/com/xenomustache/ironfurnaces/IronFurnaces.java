package com.xenomustache.ironfurnaces;

import com.xenomustache.ironfurnaces.blocks.IFBlocks;
import com.xenomustache.ironfurnaces.proxy.CommonProxy;
import com.xenomustache.ironfurnaces.tileentity.TileEntityModFurnace;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = IronFurnaces.MODID, name = IronFurnaces.NAME, version = IronFurnaces.VERSION)
public class IronFurnaces {
    public static final String MODID = "ironfurnaces";
    public static final String NAME = "Iron Furnaces";
    public static final String VERSION = "1.3";

    @SidedProxy(serverSide = "com.xenomustache.ironfurnaces.proxy.CommonProxy", clientSide = "com.xenomustache.ironfurnaces.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static IronFurnaces instance;

    public static final ModTab creativeTab = new ModTab();

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityModFurnace.class, "ironfurnaces:iron_furnace");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            IFBlocks.register(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            IFBlocks.registerItemBlocks(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerItems(ModelRegistryEvent event) {
            IFBlocks.registerModels();
        }
    }

    public static class ModTab extends CreativeTabs{
        public ModTab() {
            super(IronFurnaces.MODID);
        }

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(IFBlocks.ironFurnaceIdle));
        }
    }
}
