package com.xenomustache.ironfurnaces.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class IFBlocks {

    public static BlockIronFurnace ironFurnaceIdle = new BlockIronFurnace("iron_furnace_idle", false);
    public static BlockIronFurnace ironFurnaceActive = new BlockIronFurnace("iron_furnace_active", true);
    public static BlockGoldFurnace goldFurnaceIdle = new BlockGoldFurnace("gold_furnace_idle", false);
    public static BlockGoldFurnace goldFurnaceActive = new BlockGoldFurnace("gold_furnace_active", true);
    public static BlockDiamondFurnace diamondFurnaceIdle = new BlockDiamondFurnace("diamond_furnace_idle", false);
    public static BlockDiamondFurnace diamondFurnaceActive = new BlockDiamondFurnace("diamond_furnace_active", true);
    public static BlockGlassFurnace glassFurnaceIdle = new BlockGlassFurnace("glass_furnace_idle", false);
    public static BlockGlassFurnace glassFurnaceActive = new BlockGlassFurnace("glass_furnace_active", true);
    public static BlockObsidianFurnace obsidianFurnaceIdle = new BlockObsidianFurnace("obsidian_furnace_idle", false);
    public static BlockObsidianFurnace obsidianFurnaceActive = new BlockObsidianFurnace("obsidian_furnace_active", true);
    
    public static BlockShulkerFurnace shulkerFurnaceIdle = new BlockShulkerFurnace("shulker_furnace_idle", false, null);
    public static BlockShulkerFurnace shulkerFurnaceActive = new BlockShulkerFurnace("shulker_furnace_active", true, null);

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                ironFurnaceIdle,
                ironFurnaceActive.setLightLevel(0.875F),
                goldFurnaceIdle,
                goldFurnaceActive.setLightLevel(0.875F),
                diamondFurnaceIdle,
                diamondFurnaceActive.setLightLevel(0.875F),
                glassFurnaceIdle,
                glassFurnaceActive.setLightLevel(0.875F),
                obsidianFurnaceIdle,
                obsidianFurnaceActive.setLightLevel(0.875F),
                shulkerFurnaceIdle,
                shulkerFurnaceActive.setLightLevel(0.875F));
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                ironFurnaceIdle.createItemBlock(),
                ironFurnaceActive.createItemBlock(),
                goldFurnaceIdle.createItemBlock(),
                goldFurnaceActive.createItemBlock(),
                diamondFurnaceIdle.createItemBlock(),
                diamondFurnaceActive.createItemBlock(),
                glassFurnaceIdle.createItemBlock(),
                glassFurnaceActive.createItemBlock(),
                obsidianFurnaceIdle.createItemBlock(),
                obsidianFurnaceActive.createItemBlock(),
                shulkerFurnaceIdle.createItemBlock(),
                shulkerFurnaceActive.createItemBlock()
        );
    }

    public static void registerModels() {
        ironFurnaceIdle.registerItemModel(Item.getItemFromBlock(ironFurnaceIdle));
        ironFurnaceActive.registerItemModel(Item.getItemFromBlock(ironFurnaceActive));
        goldFurnaceIdle.registerItemModel(Item.getItemFromBlock(goldFurnaceIdle));
        goldFurnaceActive.registerItemModel(Item.getItemFromBlock(goldFurnaceActive));
        diamondFurnaceIdle.registerItemModel(Item.getItemFromBlock(diamondFurnaceIdle));
        diamondFurnaceActive.registerItemModel(Item.getItemFromBlock(diamondFurnaceActive));
        glassFurnaceIdle.registerItemModel(Item.getItemFromBlock(glassFurnaceIdle));
        glassFurnaceActive.registerItemModel(Item.getItemFromBlock(glassFurnaceActive));
        obsidianFurnaceIdle.registerItemModel(Item.getItemFromBlock(obsidianFurnaceIdle));
        obsidianFurnaceActive.registerItemModel(Item.getItemFromBlock(obsidianFurnaceActive));
        shulkerFurnaceIdle.registerItemModel(Item.getItemFromBlock(shulkerFurnaceIdle));
        shulkerFurnaceActive.registerItemModel(Item.getItemFromBlock(shulkerFurnaceActive));

    }
}
