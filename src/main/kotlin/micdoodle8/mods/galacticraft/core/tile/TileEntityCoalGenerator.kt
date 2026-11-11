package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.world.ContainerHelper
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraftforge.fml.LogicalSide as Side
import kotlin.math.max
import kotlin.math.min

open class TileEntityCoalGenerator constructor() : TileBaseUniversalElectricalSource(), IInventoryDefaults,
    ISidedInventory, IConnector {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileBaseUniversalElectricalSource>.getDisplayName()

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
                    this.removeItem(0, 1)
                } else if (this.stacks.get(0).getItem() === Item.getItemFromBlock(Blocks.COAL_BLOCK) && this.stacks.get(
                        0
                    ).getCount() > 0
                ) {
                    this.itemCookTime = 320 * 10
                    this.removeItem(0, 1)
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
        this.itemCookTime = nbt.getIntOr("itemCookTime", 0)
        this.heatGJperTick = nbt.getIntOr("generateRateInt", 0).toFloat()

        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("itemCookTime", this.itemCookTime)
        nbt.putFloat("generateRate", this.heatGJperTick)

        ItemStackHelper.saveAllItems(nbt, this.stacks)
        return nbt
    }

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    override fun getItem(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("tile.machine.0.name")
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    //    @Override
    //    public boolean hasCustomName()
    //    {
    //        return true;
    //    }
    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
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

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, direction: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, direction: EnumFacing): Boolean {
        return slotID == 0
    }

    //    @Override
    //    public boolean canPlaceItemThroughFace(int slotID, ItemStack itemstack, int j)
    //    {
    //        return this.canPlaceItem(slotID, itemstack);
    //    }
    //
    //    @Override
    //    public boolean canTakeItemThroughFace(int slotID, ItemStack itemstack, int j)
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
