package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.relauncher.Side
import kotlin.math.max
import kotlin.math.min

open class TileEntityCoalGenerator constructor() : TileBaseUniversalElectricalSource(), IInventoryDefaults,
    ISidedInventory, IConnector {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileBaseUniversalElectricalSource>.getDisplayName()

    @JvmField
    var prevGenerateWatts: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var heatGJperTick: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var itemCookTime: Int = 0
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)

    init {
        this.storage.setMaxExtract((MAX_GENERATE_GJ_PER_TICK - MIN_GENERATE_GJ_PER_TICK).toFloat())
    }

    override fun update() {
        if (!this.world.isRemote && this.heatGJperTick - MIN_GENERATE_GJ_PER_TICK > 0) {
            this.receiveEnergyGC(null, (this.heatGJperTick - MIN_GENERATE_GJ_PER_TICK), false)
        }

        super.update()

        if (!this.world.isRemote) {
            if (this.itemCookTime > 0) {
                this.itemCookTime--

                this.heatGJperTick = min(
                    this.heatGJperTick + max(this.heatGJperTick * 0.005f, BASE_ACCELERATION),
                    MAX_GENERATE_GJ_PER_TICK.toFloat()
                )
            }

            if (this.itemCookTime <= 0 && !this.stacks.get(0).isEmpty()) {
                if (this.stacks.get(0).getItem() === Items.COAL && this.stacks.get(0).getCount() > 0) {
                    this.itemCookTime = 320
                    this.decrStackSize(0, 1)
                } else if (this.stacks.get(0).getItem() === Item.getItemFromBlock(Blocks.COAL_BLOCK) && this.stacks.get(
                        0
                    ).getCount() > 0
                ) {
                    this.itemCookTime = 320 * 10
                    this.decrStackSize(0, 1)
                }
            }

            this.produce()

            if (this.itemCookTime <= 0) {
                this.heatGJperTick = max(this.heatGJperTick - 0.3f, 0f)
            }

            this.heatGJperTick = min(max(this.heatGJperTick, 0.0f), this.getMaxEnergyStoredGC())
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.itemCookTime = nbt.getInteger("itemCookTime")
        this.heatGJperTick = nbt.getInteger("generateRateInt").toFloat()

        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("itemCookTime", this.itemCookTime)
        nbt.setFloat("generateRate", this.heatGJperTick)

        ItemStackHelper.saveAllItems(nbt, this.stacks)
        return nbt
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun getStackInSlot(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
        }
        return oldstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): String {
        return GCCoreUtil.translate("tile.machine.0.name")
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    //    @Override
    //    public boolean hasCustomName()
    //    {
    //        return true;
    //    }
    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return itemstack.getItem() === Items.COAL || itemstack.getItem() === Item.getItemFromBlock(Blocks.COAL_BLOCK)
    }

    //    @Override
    //    public int[] getAccessibleSlotsFromSide(int var1)
    //    {
    //        return new int[] { 0 };
    //    }
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, direction: EnumFacing): Boolean {
        return this.isItemValidForSlot(slotID, itemstack)
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, direction: EnumFacing): Boolean {
        return slotID == 0
    }

    //    @Override
    //    public boolean canInsertItem(int slotID, ItemStack itemstack, int j)
    //    {
    //        return this.isItemValidForSlot(slotID, itemstack);
    //    }
    //
    //    @Override
    //    public boolean canExtractItem(int slotID, ItemStack itemstack, int j)
    //    {
    //        return slotID == 0;
    //    }
    override fun receiveElectricity(from: EnumFacing?, energy: Float, tier: Int, doReceive: Boolean): Float {
        return 0f
    }

    /*
    @Override
	public float getRequest(EnumFacing direction)
	{
		return 0;
	}
	*/
    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricOutputDirection())
    }

    open fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachine) {
            return state.getValue(BlockMachine.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null || type != NetworkType.POWER) {
            return false
        }

        return direction == this.getElectricOutputDirection()
    }

    companion object {
        //New energy rates:
        //
        //Tier 1 machine typically consumes 600 gJ/s = 30 gJ/t
        //Coal generator on max heat can power up to 4 Tier 1 machines
        //(fewer if one of them is an Electric Furnace)
        //Basic solar gen in full sun can power 1 Tier 1 machine
        //1 lump of coal is equivalent to 38400 gJ
        //because on max heat it produces 120 gJ/t over 320 ticks
        //Below the min_generate, all heat is wasted
        //At max generate, 100% efficient conversion coal energy -> electric makes 120 gJ/t
        const val MAX_GENERATE_GJ_PER_TICK: Int = 150

        const val MIN_GENERATE_GJ_PER_TICK: Int = 30

        private const val BASE_ACCELERATION = 0.3f
    }
}
