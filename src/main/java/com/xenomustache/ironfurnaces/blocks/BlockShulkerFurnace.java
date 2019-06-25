package com.xenomustache.ironfurnaces.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.xenomustache.ironfurnaces.IronFurnaces;
import com.xenomustache.ironfurnaces.tileentity.TileEntityShulkerFurnace;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockShulkerFurnace extends BlockContainer {
    public static final PropertyEnum<EnumFacing> FACING = PropertyDirection.create("facing");
    protected String name;
    protected static boolean isBurning;
    protected final EnumDyeColor color;

    public BlockShulkerFurnace(String name, boolean isBurning, EnumDyeColor colorIn)
    {
        super(Material.ROCK, MapColor.AIR);
        this.name = name;
        this.isBurning = isBurning;
        this.color = colorIn;

        //this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        
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

    @Override
    public BlockShulkerFurnace setCreativeTab(CreativeTabs tab) {
        if (isBurning == false) {
            super.setCreativeTab(tab);
        } else if (isBurning == true) {
            super.setCreativeTab(null);
        }
        return this;
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

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityShulkerFurnace(this.color);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean causesSuffocation(IBlockState state)
    {
        return true;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else if (playerIn.isSpectator())
        {
            return true;
        }
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityShulkerFurnace)
            {
                EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
                boolean flag;

                if (((TileEntityShulkerFurnace)tileentity).getAnimationStatus() == TileEntityShulkerFurnace.AnimationStatus.CLOSED)
                {
                    AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB.expand((double)(0.5F * (float)enumfacing.getFrontOffsetX()), (double)(0.5F * (float)enumfacing.getFrontOffsetY()), (double)(0.5F * (float)enumfacing.getFrontOffsetZ())).contract((double)enumfacing.getFrontOffsetX(), (double)enumfacing.getFrontOffsetY(), (double)enumfacing.getFrontOffsetZ());
                    flag = !worldIn.collidesWithAnyBlock(axisalignedbb.offset(pos.offset(enumfacing)));
                }
                else
                {
                    flag = true;
                }

                if (flag)
                {
                    playerIn.addStat(StatList.OPEN_SHULKER_BOX);
                    playerIn.displayGUIChest((IInventory)tileentity);
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, facing);
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually
     * collect this block
     */
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (worldIn.getTileEntity(pos) instanceof TileEntityShulkerFurnace)
        {
            TileEntityShulkerFurnace tileentityshulkerfurnace = (TileEntityShulkerFurnace)worldIn.getTileEntity(pos);
            tileentityshulkerfurnace.setDestroyedByCreativePlayer(player.capabilities.isCreativeMode);
            tileentityshulkerfurnace.fillWithLoot(player);
        }
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (stack.hasDisplayName())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityShulkerFurnace)
            {
                ((TileEntityShulkerFurnace)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityShulkerFurnace)
        {
            TileEntityShulkerFurnace tileentityshulkerfurnace = (TileEntityShulkerFurnace)tileentity;

            if (!tileentityshulkerfurnace.isCleared() && tileentityshulkerfurnace.shouldDrop())
            {
                ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound.setTag("BlockEntityTag", ((TileEntityShulkerFurnace)tileentity).saveToNbt(nbttagcompound1));
                itemstack.setTagCompound(nbttagcompound);

                if (tileentityshulkerfurnace.hasCustomName())
                {
                    itemstack.setStackDisplayName(tileentityshulkerfurnace.getName());
                    tileentityshulkerfurnace.setCustomName("");
                }

                spawnAsEntity(worldIn, pos, itemstack);
            }

            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
        }

        super.breakBlock(worldIn, pos, state);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        NBTTagCompound nbttagcompound = stack.getTagCompound();

        if (nbttagcompound != null && nbttagcompound.hasKey("BlockEntityTag", 10))
        {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("BlockEntityTag");

            if (nbttagcompound1.hasKey("LootTable", 8))
            {
                tooltip.add("???????");
            }

            if (nbttagcompound1.hasKey("Items", 9))
            {
                NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(nbttagcompound1, nonnulllist);
                int i = 0;
                int j = 0;

                for (ItemStack itemstack : nonnulllist)
                {
                    if (!itemstack.isEmpty())
                    {
                        ++j;

                        if (i <= 4)
                        {
                            ++i;
                            tooltip.add(String.format("%s x%d", itemstack.getDisplayName(), itemstack.getCount()));
                        }
                    }
                }

                if (j - i > 0)
                {
                    tooltip.add(String.format(TextFormatting.ITALIC + I18n.translateToLocal("container.shulkerFurnace.more"), j - i));
                }
            }
        }
    }

    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity tileentity = source.getTileEntity(pos);
        return tileentity instanceof TileEntityShulkerFurnace ? ((TileEntityShulkerFurnace)tileentity).getBoundingBox(state) : FULL_BLOCK_AABB;
    }

    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return Container.calcRedstoneFromInventory((IInventory)worldIn.getTileEntity(pos));
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        ItemStack itemstack = super.getItem(worldIn, pos, state);
        TileEntityShulkerFurnace tileentityshulkerfurnace = (TileEntityShulkerFurnace)worldIn.getTileEntity(pos);
        NBTTagCompound nbttagcompound = tileentityshulkerfurnace.saveToNbt(new NBTTagCompound());

        if (!nbttagcompound.hasNoTags())
        {
            itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
        }

        return itemstack;
    }

    public static Block getBlockByColor(EnumDyeColor colorIn)
    {
        switch (colorIn)
        {
            //case WHITE:
            //    return Blocks.WHITE_SHULKER_BOX;
            //case ORANGE:
            //    return Blocks.ORANGE_SHULKER_BOX;
            //case MAGENTA:
            //    return Blocks.MAGENTA_SHULKER_BOX;
            //case LIGHT_BLUE:
            //    return Blocks.LIGHT_BLUE_SHULKER_BOX;
            //case YELLOW:
            //    return Blocks.YELLOW_SHULKER_BOX;
            //case LIME:
            //    return Blocks.LIME_SHULKER_BOX;
            //case PINK:
            //    return Blocks.PINK_SHULKER_BOX;
            //case GRAY:
            //    return Blocks.GRAY_SHULKER_BOX;
            //case SILVER:
            //    return Blocks.SILVER_SHULKER_BOX;
            //case CYAN:
            //    return Blocks.CYAN_SHULKER_BOX;
            //case PURPLE:
            //default:
            //    return Blocks.PURPLE_SHULKER_BOX;
            //case BLUE:
            //    return Blocks.BLUE_SHULKER_BOX;
            //case BROWN:
            //    return Blocks.BROWN_SHULKER_BOX;
            //case GREEN:
            //    return Blocks.GREEN_SHULKER_BOX;
            //case RED:
            //    return Blocks.RED_SHULKER_BOX;
            //case BLACK:
            //    return Blocks.BLACK_SHULKER_BOX;
            default:
                if(isBurning)
                    return IFBlocks.shulkerFurnaceActive;
                else
                    return IFBlocks.shulkerFurnaceIdle;
        }
    }

    @SideOnly(Side.CLIENT)
    public static EnumDyeColor getColorFromItem(Item itemIn)
    {
        return getColorFromBlock(Block.getBlockFromItem(itemIn));
    }

    public static ItemStack getColoredItemStack(EnumDyeColor colorIn)
    {
        return new ItemStack(getBlockByColor(colorIn));
    }

    @SideOnly(Side.CLIENT)
    public static EnumDyeColor getColorFromBlock(Block blockIn)
    {
        return blockIn instanceof BlockShulkerFurnace ? ((BlockShulkerFurnace)blockIn).getColor() : EnumDyeColor.PURPLE;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     * 
     * @return an approximation of the form of the given face
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        state = this.getActualState(state, worldIn, pos);
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        TileEntityShulkerFurnace.AnimationStatus tileentityshulkerfurnace$animationstatus = ((TileEntityShulkerFurnace)worldIn.getTileEntity(pos)).getAnimationStatus();
        return tileentityshulkerfurnace$animationstatus != TileEntityShulkerFurnace.AnimationStatus.CLOSED && (tileentityshulkerfurnace$animationstatus != TileEntityShulkerFurnace.AnimationStatus.OPENED || enumfacing != face.getOpposite() && enumfacing != face) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @SideOnly(Side.CLIENT)
    public EnumDyeColor getColor()
    {
        return this.color;
    }
}