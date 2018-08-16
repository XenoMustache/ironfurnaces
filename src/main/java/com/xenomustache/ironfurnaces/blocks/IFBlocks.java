package com.xenomustache.ironfurnaces.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class IFBlocks {

    public static BlockModFurnace ironFurnaceIdle = new BlockModFurnace("iron_furnace_idle", 0, false);
    public static BlockModFurnace ironFurnaceActive = new BlockModFurnace("iron_furnace_active", 0, false);
    public static BlockModFurnace goldFurnaceIdle = new BlockModFurnace("gold_furnace_idle", 1, false);
    public static BlockModFurnace goldFurnaceActive = new BlockModFurnace("gold_furnace_active", 1, false);
    public static BlockModFurnace diamondFurnaceIdle = new BlockModFurnace("diamond_furnace_idle", 2, false);
    public static BlockModFurnace diamondFurnaceActive = new BlockModFurnace("diamond_furnace_active", 2, false);
    public static BlockModFurnace glassFurnaceIdle = new BlockModFurnace("glass_furnace_idle", 3, false);
    public static BlockModFurnace glassFurnaceActive = new BlockModFurnace("glass_furnace_active", 3, false);
    public static BlockModFurnace obsidianFurnaceIdle = new BlockModFurnace("obsidian_furnace_idle", 4, false);
    public static BlockModFurnace obsidianFurnaceActive = new BlockModFurnace("obsidian_furnace_active", 4, false);

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                ironFurnaceIdle,
                ironFurnaceActive,
                goldFurnaceIdle,
                goldFurnaceActive,
                diamondFurnaceIdle,
                diamondFurnaceActive,
                glassFurnaceIdle,
                glassFurnaceActive,
                obsidianFurnaceIdle,
                obsidianFurnaceActive);
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
                obsidianFurnaceActive.createItemBlock()
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

    }
}
