package com.xenomustache.ironfurnaces.tileentity;

import java.util.List;

import javax.annotation.Nullable;

import com.xenomustache.ironfurnaces.blocks.BlockShulkerFurnace;
import com.xenomustache.ironfurnaces.blocks.IFBlocks;
import com.xenomustache.ironfurnaces.container.ContainerModFurnace;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.xenomustache.ironfurnaces.blocks.BlockShulkerFurnace.FACING;

public class TileEntityShulkerFurnace extends TileEntityLockableLoot implements ITickable, ISidedInventory {

    private NonNullList<ItemStack> furnaceItemStacks = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
    private boolean hasBeenCleared;
    private int openCount;
    private TileEntityShulkerFurnace.AnimationStatus animationStatus;
    private float progress;
    private float progressOld;
    private EnumDyeColor color;
    private boolean destroyedByCreativePlayer;
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 1};
    private static final int[] SLOTS_SIDES = new int[]{1};
    public static boolean keepInventory;
    public Block furnace;

    net.minecraftforge.items.IItemHandler handlerTop = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.UP);
    net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.DOWN);
    net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.WEST);

    private int furnaceBurnTime;
    private int currentItemBurnTime;
    private int cookTime;
    private int totalCookTime;
    private String furnaceCustomName;

    {
        this.markDirty();
    }

    public TileEntityShulkerFurnace()
    {
        this((EnumDyeColor)null);
    }

    public TileEntityShulkerFurnace(@Nullable EnumDyeColor colorIn)
    {
        this.furnaceItemStacks = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
        this.animationStatus = TileEntityShulkerFurnace.AnimationStatus.CLOSED;
        this.color = colorIn;
    }

    @SideOnly(Side.CLIENT)
    public static boolean isBurning(IInventory inventory) {
        return inventory.getField(0) > 0;
    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            int burnTime = net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack);
            if (burnTime >= 0) return burnTime;
            Item item = stack.getItem();

            if (item == Item.getItemFromBlock(Blocks.WOODEN_SLAB)) {
                return 150;
            } else if (item == Item.getItemFromBlock(Blocks.WOOL)) {
                return 100;
            } else if (item == Item.getItemFromBlock(Blocks.CARPET)) {
                return 67;
            } else if (item == Item.getItemFromBlock(Blocks.LADDER)) {
                return 300;
            } else if (item == Item.getItemFromBlock(Blocks.WOODEN_BUTTON)) {
                return 100;
            } else if (Block.getBlockFromItem(item).getDefaultState().getMaterial() == Material.WOOD) {
                return 300;
            } else if (item == Item.getItemFromBlock(Blocks.COAL_BLOCK)) {
                return 16000;
            } else if (item instanceof ItemTool && "WOOD".equals(((ItemTool) item).getToolMaterialName())) {
                return 200;
            } else if (item instanceof ItemSword && "WOOD".equals(((ItemSword) item).getToolMaterialName())) {
                return 200;
            } else if (item instanceof ItemHoe && "WOOD".equals(((ItemHoe) item).getMaterialName())) {
                return 200;
            } else if (item == Items.STICK) {
                return 100;
            } else if (item != Items.BOW && item != Items.FISHING_ROD) {
                if (item == Items.SIGN) {
                    return 200;
                } else if (item == Items.COAL) {
                    return 1600;
                } else if (item == Items.LAVA_BUCKET) {
                    return 20000;
                } else if (item != Item.getItemFromBlock(Blocks.SAPLING) && item != Items.BOWL) {
                    if (item == Items.BLAZE_ROD) {
                        return 2400;
                    } else if (item instanceof ItemDoor && item != Items.IRON_DOOR) {
                        return 200;
                    } else {
                        return item instanceof ItemBoat ? 400 : 0;
                    }
                } else {
                    return 100;
                }
            } else {
                return 300;
            }
        }
    }

    public static boolean isItemFuel(ItemStack stack) {
        return getItemBurnTime(stack) > 0;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        boolean flag = this.isBurning();
        boolean flag1 = false;

        this.updateAnimation();

        if (this.animationStatus == TileEntityShulkerFurnace.AnimationStatus.OPENING || this.animationStatus == TileEntityShulkerFurnace.AnimationStatus.CLOSING)
        {
            this.moveCollidedEntities();
        }

        if (this.isBurning()) {
            --this.furnaceBurnTime;
        }

        if (!this.world.isRemote) {
            ItemStack itemstack = this.furnaceItemStacks.get(1);

            if (this.isBurning() || !itemstack.isEmpty() && !((ItemStack) this.furnaceItemStacks.get(0)).isEmpty()) {
                if (!this.isBurning() && this.canSmelt()) {
                    this.furnaceBurnTime = getItemBurnTime(itemstack);
                    this.currentItemBurnTime = this.furnaceBurnTime;

                    if (this.isBurning()) {
                        flag1 = true;

                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);

                            if (itemstack.isEmpty()) {
                                ItemStack item1 = item.getContainerItem(itemstack);
                                this.furnaceItemStacks.set(1, item1);
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt()) {
                    ++this.cookTime;

                    if (this.cookTime == this.totalCookTime) {
                        this.cookTime = 0;
                        this.totalCookTime = this.getCookTime(this.furnaceItemStacks.get(0));
                        this.smeltItem();
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
            }

            if (flag != this.isBurning()) {
                IBlockState iblockstate = world.getBlockState(pos);
                TileEntity tileentity = world.getTileEntity(pos);

                keepInventory = true;

                if (flag1) {
                    world.setBlockState(pos, IFBlocks.shulkerFurnaceActive.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
                    world.setBlockState(pos, IFBlocks.shulkerFurnaceActive.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
                } else {
                    world.setBlockState(pos, IFBlocks.shulkerFurnaceIdle.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
                    world.setBlockState(pos, IFBlocks.shulkerFurnaceIdle.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
                }

                keepInventory = false;

                if (tileentity != null) {
                    tileentity.validate();
                    world.setTileEntity(pos, tileentity);
                }
            }
        }

        if (flag1) {
            this.markDirty();
        }
    }

    public int getCookTime(ItemStack stack) {
        return 200;
    }

    private boolean canSmelt() {
        if (((ItemStack) this.furnaceItemStacks.get(0)).isEmpty()) {
            return false;
        } else {
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks.get(0));

            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = this.furnaceItemStacks.get(2);

                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
                }
            }
        }
    }

    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack itemstack = this.furnaceItemStacks.get(0);
            ItemStack itemstack1 = FurnaceRecipes.instance().getSmeltingResult(itemstack);
            ItemStack itemstack2 = this.furnaceItemStacks.get(2);

            if (itemstack2.isEmpty()) {
                this.furnaceItemStacks.set(2, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (itemstack.getItem() == Item.getItemFromBlock(Blocks.SPONGE) && itemstack.getMetadata() == 1 && !((ItemStack) this.furnaceItemStacks.get(1)).isEmpty() && ((ItemStack) this.furnaceItemStacks.get(1)).getItem() == Items.BUCKET) {
                this.furnaceItemStacks.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
        }
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        } else if (index != 1) {
            return true;
        } else {
            ItemStack itemstack = this.furnaceItemStacks.get(1);
            return isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && itemstack.getItem() != Items.BUCKET;
        }
    }

    public ItemStack getStackInSlot(int index) {
        return this.furnaceItemStacks.get(index);
    }

    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.furnaceItemStacks, index, count);
    }

    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.furnaceItemStacks, index);
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = this.furnaceItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.furnaceItemStacks.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag) {
            this.totalCookTime = this.getCookTime(stack);
            this.cookTime = 0;
            this.markDirty();
        }
    }

    protected void updateAnimation()
    {
        this.progressOld = this.progress;

        switch (this.animationStatus)
        {
            case CLOSED:
                this.progress = 0.0F;
                break;
            case OPENING:
                this.progress += 0.1F;

                if (this.progress >= 1.0F)
                {
                    this.moveCollidedEntities();
                    this.animationStatus = TileEntityShulkerFurnace.AnimationStatus.OPENED;
                    this.progress = 1.0F;
                }

                break;
            case CLOSING:
                this.progress -= 0.1F;

                if (this.progress <= 0.0F)
                {
                    this.animationStatus = TileEntityShulkerFurnace.AnimationStatus.CLOSED;
                    this.progress = 0.0F;
                }

                break;
            case OPENED:
                this.progress = 1.0F;
        }
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    public TileEntityShulkerFurnace.AnimationStatus getAnimationStatus()
    {
        return this.animationStatus;
    }

    public AxisAlignedBB getBoundingBox(IBlockState p_190584_1_)
    {
        return this.getBoundingBox((EnumFacing)p_190584_1_.getValue(BlockShulkerFurnace.FACING));
    }

    public AxisAlignedBB getBoundingBox(EnumFacing p_190587_1_)
    {
        return Block.FULL_BLOCK_AABB.expand((double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getFrontOffsetX()), (double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getFrontOffsetY()), (double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getFrontOffsetZ()));
    }

    private AxisAlignedBB getTopBoundingBox(EnumFacing p_190588_1_)
    {
        EnumFacing enumfacing = p_190588_1_.getOpposite();
        return this.getBoundingBox(p_190588_1_).contract((double)enumfacing.getFrontOffsetX(), (double)enumfacing.getFrontOffsetY(), (double)enumfacing.getFrontOffsetZ());
    }

    private void moveCollidedEntities()
    {
        IBlockState iblockstate = this.world.getBlockState(this.getPos());

        if (iblockstate.getBlock() instanceof BlockShulkerFurnace)
        {
            EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockShulkerFurnace.FACING);
            AxisAlignedBB axisalignedbb = this.getTopBoundingBox(enumfacing).offset(this.pos);
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);

            if (!list.isEmpty())
            {
                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = list.get(i);

                    if (entity.getPushReaction() != EnumPushReaction.IGNORE)
                    {
                        double d0 = 0.0D;
                        double d1 = 0.0D;
                        double d2 = 0.0D;
                        AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();

                        switch (enumfacing.getAxis())
                        {
                            case X:

                                if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                                {
                                    d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                                }
                                else
                                {
                                    d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                                }

                                d0 = d0 + 0.01D;
                                break;
                            case Y:

                                if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                                {
                                    d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                                }
                                else
                                {
                                    d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                                }

                                d1 = d1 + 0.01D;
                                break;
                            case Z:

                                if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                                {
                                    d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                                }
                                else
                                {
                                    d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                                }

                                d2 = d2 + 0.01D;
                        }

                        entity.move(MoverType.SHULKER_BOX, d0 * (double)enumfacing.getFrontOffsetX(), d1 * (double)enumfacing.getFrontOffsetY(), d2 * (double)enumfacing.getFrontOffsetZ());
                    }
                }
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.furnaceItemStacks.size();
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.openCount = type;

            if (type == 0)
            {
                this.animationStatus = TileEntityShulkerFurnace.AnimationStatus.CLOSING;
            }

            if (type == 1)
            {
                this.animationStatus = TileEntityShulkerFurnace.AnimationStatus.OPENING;
            }

            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    public void openInventory(EntityPlayer player)
    {
        if (!player.isSpectator())
        {
            if (this.openCount < 0)
            {
                this.openCount = 0;
            }

            ++this.openCount;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.openCount);

            if (this.openCount == 1)
            {
                this.world.playSound((EntityPlayer)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    public void closeInventory(EntityPlayer player)
    {
        if (!player.isSpectator())
        {
            --this.openCount;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.openCount);

            if (this.openCount <= 0)
            {
                this.world.playSound((EntityPlayer)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerModFurnace(playerInventory, this);
    }

    public String getGuiID() {
        return "minecraft:furnace";
    }

    public int getField(int id) {
        switch (id) {
            case 0:
                return this.furnaceBurnTime;
            case 1:
                return this.currentItemBurnTime;
            case 2:
                return this.cookTime;
            case 3:
                return this.totalCookTime;
            default:
                return 0;
        }
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.furnaceBurnTime = value;
                break;
            case 1:
                this.currentItemBurnTime = value;
                break;
            case 2:
                this.cookTime = value;
                break;
            case 3:
                this.totalCookTime = value;
        }
    }

    public int getFieldCount() {
        return 4;
    }

    public void clear() {
        this.furnaceItemStacks.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    @javax.annotation.Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable EnumFacing facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName() {
        return this.hasCustomName() ? this.furnaceCustomName : "container.shulker_furnace_" + -1;
    }

    public void setCustomInventoryName(String p_145951_1_) {
        this.furnaceCustomName = p_145951_1_;
    }

    public static void registerFixesShulkerFurnace(DataFixer fixer)
    {
        fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityShulkerFurnace.class, new String[] {"Items"}));
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.furnaceItemStacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.furnaceItemStacks);
        this.furnaceBurnTime = compound.getInteger("BurnTime");
        this.cookTime = compound.getInteger("CookTime");
        this.totalCookTime = compound.getInteger("CookTimeTotal");
        this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks.get(1));
        this.loadFromNbt(compound);

        if (compound.hasKey("CustomName", 8)) {
            this.furnaceCustomName = compound.getString("CustomName");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("BurnTime", (short) this.furnaceBurnTime);
        compound.setInteger("CookTime", (short) this.cookTime);
        compound.setInteger("CookTimeTotal", (short) this.totalCookTime);
        ItemStackHelper.saveAllItems(compound, this.furnaceItemStacks);

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.furnaceCustomName);
        }

        return this.saveToNbt(compound);
    }

    public void loadFromNbt(NBTTagCompound compound)
    {
        this.furnaceItemStacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (!this.checkLootAndRead(compound) && compound.hasKey("Items", 9)) {
            ItemStackHelper.loadAllItems(compound, this.furnaceItemStacks);
        }

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    public NBTTagCompound saveToNbt(NBTTagCompound compound)
    {
        if (!this.checkLootAndWrite(compound))
        {
            ItemStackHelper.saveAllItems(compound, this.furnaceItemStacks, false);
        }

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }

        if (!compound.hasKey("Lock") && this.isLocked())
        {
            this.getLockCode().toNBT(compound);
        }

        return compound;
    }

    protected NonNullList<ItemStack> getItems()
    {
        return this.furnaceItemStacks;
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.furnaceItemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return SLOTS_BOTTOM;
        } else {
            return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES;
        }
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side.
     */
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side.
     */
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (direction == EnumFacing.DOWN && index == 1) {
            Item item = stack.getItem();

            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        }

        return true;
    }

    public boolean isCleared()
    {
        return this.hasBeenCleared;
    }

    public float getProgress(float p_190585_1_)
    {
        return this.progressOld + (this.progress - this.progressOld) * p_190585_1_;
    }

    @SideOnly(Side.CLIENT)
    public EnumDyeColor getColor()
    {
        if (this.color == null)
        {
            this.color = BlockShulkerFurnace.getColorFromBlock(this.getBlockType());
        }

        return this.color;
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 10, this.getUpdateTag());
    }

    public boolean isDestroyedByCreativePlayer()
    {
        return this.destroyedByCreativePlayer;
    }

    public void setDestroyedByCreativePlayer(boolean p_190579_1_)
    {
        this.destroyedByCreativePlayer = p_190579_1_;
    }

    public boolean shouldDrop()
    {
        return !this.isDestroyedByCreativePlayer() || !this.isEmpty() || this.hasCustomName() || this.lootTable != null;
    }

    public static enum AnimationStatus
    {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;
    }

    protected net.minecraftforge.items.IItemHandler createUnSidedHandler()
    {
        return new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.UP);
    }

    @Override
    public void tick() {

    }
}