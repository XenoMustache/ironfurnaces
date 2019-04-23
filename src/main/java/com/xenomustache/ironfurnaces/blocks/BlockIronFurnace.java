package com.xenomustache.ironfurnaces.blocks;

import com.xenomustache.ironfurnaces.IronFurnaces;
import com.xenomustache.ironfurnaces.tileentity.*;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static com.xenomustache.ironfurnaces.tileentity.TileEntityIronFurnace.keepInventory;

public class BlockIronFurnace extends BlockContainer {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private int materialID = 0;
    protected String name;
    protected boolean isBurning;

    public BlockIronFurnace(String name, boolean isBurning) {
        super(Material.ROCK);

        this.name = name;
        this.isBurning = isBurning;

        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setHardness(3f);
        this.setResistance(5f);
        this.setHarvestLevel("pickaxe", 1);
        this.setCreativeTab(IronFurnaces.creativeTab);
    }

    public void registerItemModel(Item itemBlock) {
        IronFurnaces.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        if (materialID == 3) {
            return BlockRenderLayer.TRANSLUCENT;
        } else {
            return BlockRenderLayer.SOLID;
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        if (materialID != 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BlockIronFurnace setCreativeTab(CreativeTabs tab) {
        if (isBurning == false) {
            super.setCreativeTab(tab);
        } else if (isBurning == true) {
            super.setCreativeTab(null);
        }
        return this;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (materialID == 0) {
            return Item.getItemFromBlock(IFBlocks.ironFurnaceIdle);
        } else {
            return null;
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("incomplete-switch")
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (this.isBurning) {
            EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = (double) pos.getZ() + 0.5D;
            //double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;

            if (rand.nextDouble() < 0.1D) {
                worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            switch (enumfacing) {
                case WEST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    break;
                case EAST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    break;
                case NORTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
                    break;
                case SOUTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityIronFurnace) {
                playerIn.displayGUIChest((TileEntityIronFurnace) tileentity);
                playerIn.addStat(StatList.FURNACE_INTERACTION);
            } else if (tileentity instanceof TileEntityGoldFurnace) {
                playerIn.displayGUIChest((TileEntityGoldFurnace) tileentity);
                playerIn.addStat(StatList.FURNACE_INTERACTION);
            } else if (tileentity instanceof TileEntityDiamondFurnace) {
                playerIn.displayGUIChest((TileEntityDiamondFurnace) tileentity);
                playerIn.addStat(StatList.FURNACE_INTERACTION);
            } else if (tileentity instanceof TileEntityGlassFurnace) {
                playerIn.displayGUIChest((TileEntityGlassFurnace) tileentity);
                playerIn.addStat(StatList.FURNACE_INTERACTION);
            } else if (tileentity instanceof TileEntityObsidianFurnace) {
                playerIn.displayGUIChest((TileEntityObsidianFurnace) tileentity);
                playerIn.addStat(StatList.FURNACE_INTERACTION);
            }

            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (materialID == 0) {
            return new TileEntityIronFurnace();
        } else if (materialID == 1) {
            return new TileEntityGoldFurnace();
        } else if (materialID == 2) {
            return new TileEntityDiamondFurnace();
        } else if (materialID == 3) {
            return new TileEntityGlassFurnace();
        } else if (materialID == 4) {
            return new TileEntityObsidianFurnace();
        } else {
            return null;
        }
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityModFurnace) {
                ((TileEntityModFurnace) tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!keepInventory)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityIronFurnace)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityIronFurnace)tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        if (materialID == 0) {
            return new ItemStack(IFBlocks.ironFurnaceIdle);
        } else if (materialID == 1) {
            return new ItemStack(IFBlocks.goldFurnaceIdle);
        } else if (materialID == 2) {
            return new ItemStack(IFBlocks.diamondFurnaceIdle);
        } else if (materialID == 3) {
            return new ItemStack(IFBlocks.glassFurnaceIdle);
        } else if (materialID == 4) {
            return new ItemStack(IFBlocks.obsidianFurnaceActive);
        } else {
            return null;
        }
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }
}
