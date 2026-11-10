package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.block.IOxygenReliantBlock
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDistributor
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.entities.IBubbleProviderColored
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.HashSet
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

open class TileEntityOxygenDistributor constructor() : TileEntityOxygen(6000, 8), IInventoryDefaults,
    ISidedInventory, IBubbleProviderColored {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileEntityOxygen>.getDisplayName()

    @JvmField
    var active: Boolean = false

    @JvmField
    var lastActive: Boolean = false

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

    @JvmField
    var bubbleSize: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var shouldRenderBubble: Boolean = true

    override fun onLoad() {
        if (!this.world.isRemote) loadedTiles.add(BlockVec3Dim(this))
    }

    override fun onChunkUnload() {
        if (!this.world.isRemote) loadedTiles.remove(BlockVec3Dim(this))
        super.onChunkUnload()
    }

    override fun invalidate() {
        if (!this.world.isRemote /* && this.oxygenBubble != null*/) {
            val bubbleR: Int = MathHelper.ceil(bubbleSize)
            val bubbleR2: Int = (bubbleSize * bubbleSize).toInt()
            val xMin: Int = this.getPos().getX() - bubbleR
            val xMax: Int = this.getPos().getX() + bubbleR
            val yMin: Int = this.getPos().getY() - bubbleR
            val yMax: Int = this.getPos().getY() + bubbleR
            val zMin: Int = this.getPos().getZ() - bubbleR
            val zMax: Int = this.getPos().getZ() + bubbleR
            for (x: Int in xMin..<xMax) {
                for (z: Int in zMin..<zMax) {
                    //Doing y as the inner loop allows BlockVec3 to cache the chunks more efficiently
                    for (y: Int in yMin..<yMax) {
                        val state: IBlockState = (BlockVec3(x, y, z).getBlockState(this.world))!!

                        if (state.getBlock() is IOxygenReliantBlock && this.getDistanceFromServer(
                                x,
                                y,
                                z
                            ) <= bubbleR2
                        ) {
                            this.world.scheduleUpdate(BlockPos(x, y, z), state.getBlock(), 0)
                        }
                    }
                }
            }
            //        	this.oxygenBubble.setDead();
            loadedTiles.remove(BlockVec3Dim(this))
        }

        super.invalidate()
    }

    override fun getPacketRange(): Double {
        return 64.0
    }

    override fun addExtraNetworkedData(networkedList: MutableList<Any?>?) {
        if (!this.world.isRemote && !this.isInvalid()) {
//            networkedList.add(this.oxygenBubble != null);
//            if (this.oxygenBubble != null)
//            {
//                networkedList.add(this.oxygenBubble.getEntityId());
//            }
            if (this.world.getMinecraftServer()!!.isDedicatedServer()) {
                (networkedList)!!.add(loadedTiles.size)
                //TODO: Limit this to ones in the same dimension as this tile?
                for (distributor: BlockVec3Dim? in loadedTiles) {
                    if (distributor == null) {
                        (networkedList)!!.add(-1)
                        (networkedList)!!.add(-1)
                        (networkedList)!!.add(-1)
                        (networkedList)!!.add(-1)
                    } else {
                        (networkedList)!!.add(distributor.x)
                        (networkedList)!!.add(distributor.y)
                        (networkedList)!!.add(distributor.z)
                        (networkedList)!!.add(distributor.dim)
                    }
                }
            } else  //Signal integrated server, do not clear loadedTiles
            {
                (networkedList)!!.add(-1)
            }
            (networkedList)!!.add(this.bubbleSize)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(
            (this.getPos().getX() - this.bubbleSize).toDouble(),
            (this.getPos().getY() - this.bubbleSize).toDouble(),
            (this.getPos().getZ() - this.bubbleSize).toDouble(),
            (this.getPos().getX() + this.bubbleSize).toDouble(),
            (this.getPos().getY() + this.bubbleSize).toDouble(),
            (this.getPos().getZ() + this.bubbleSize).toDouble()
        )
    }

    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun readExtraNetworkedData(dataStream: ByteBuf?) {
        if (this.world.isRemote) {
//            if (dataStream.readBoolean())
//            {
//                this.oxygenBubble = (EntityBubble) worldObj.getEntityByID(dataStream.readInt());
//            }
            val size: Int = (dataStream)!!.readInt()
            if (size >= 0) {
                loadedTiles.clear()
                for (i: Int in 0..<size) {
                    val i1: Int = (dataStream)!!.readInt()
                    val i2: Int = (dataStream)!!.readInt()
                    val i3: Int = (dataStream)!!.readInt()
                    val i4: Int = (dataStream)!!.readInt()
                    if (i1 == -1 && i2 == -1 && i3 == -1 && i4 == -1) {
                        continue
                    }
                    loadedTiles.add(BlockVec3Dim(i1, i2, i3, i4))
                }
            }
            this.bubbleSize = (dataStream)!!.readFloat()
        }
    }

    open fun getDistanceFromServer(par1: Int, par3: Int, par5: Int): Int {
        val d3: Int = this.getPos().getX() - par1
        val d4: Int = this.getPos().getY() - par3
        val d5: Int = this.getPos().getZ() - par5
        return d3 * d3 + d4 * d4 + d5 * d5
    }

    override fun update() {
        if (!this.world.isRemote) {
            val oxygenItemStack: ItemStack = this.getStackInSlot(1)
            if (oxygenItemStack != null && oxygenItemStack.getItem() is IItemOxygenSupply) {
                val oxygenItem: IItemOxygenSupply = oxygenItemStack.getItem() as IItemOxygenSupply
                val oxygenDraw: Int = floor(
                    min(
                        this.oxygenPerTick * 2.5f,
                        (this.getMaxOxygenStored() - this.getOxygenStored()).toFloat()
                    ).toDouble()
                ).toInt()
                this.setOxygenStored(getOxygenStored() + oxygenItem.discharge(oxygenItemStack, oxygenDraw))
                if (this.getOxygenStored() > this.getMaxOxygenStored()) {
                    this.setOxygenStored(this.getOxygenStored())
                }
            }
        }

        super.update()

        if (!this.world.isRemote) {
            if (this.getEnergyStoredGC() > 0.0f && this.hasEnoughEnergyToRun && this.getOxygenStored() > this.oxygenPerTick) {
                this.bubbleSize += 0.01f
            } else {
                this.bubbleSize -= 0.1f
            }

            this.bubbleSize = min(max(this.bubbleSize, 0.0f), 10.0f)
        }

        //        if (!hasValidBubble && !this.world.isRemote && (this.oxygenBubble == null || this.ticks < 25))
//        {
//            //Check it's a world without a breathable atmosphere
//        	if (this.oxygenBubble == null && this.world.provider instanceof IGalacticraftWorldProvider && !((IGalacticraftWorldProvider)this.world.provider).hasBreathableAtmosphere())
//            {
//                this.oxygenBubble = new EntityBubble(this.world, new Vector3(this), this);
//                this.hasValidBubble = true;
//                this.world.spawnEntity(this.oxygenBubble);
//            }
//        }
        if (!this.world.isRemote /* && this.oxygenBubble != null*/) {
            this.active = bubbleSize >= 1 && this.hasEnoughEnergyToRun && this.getOxygenStored() > this.oxygenPerTick

            if (this.ticks % (if (this.active) 20 else 4) == 0) {
                val size: Double = bubbleSize.toDouble()
                val bubbleR: Int = MathHelper.floor(size) + 4
                val bubbleR2: Int = (size * size).toInt()
                for (x: Int in this.getPos().getX() - bubbleR..this.getPos().getX() + bubbleR) {
                    for (y: Int in this.getPos().getY() - bubbleR..this.getPos().getY() + bubbleR) {
                        for (z: Int in this.getPos().getZ() - bubbleR..this.getPos().getZ() + bubbleR) {
                            val pos: BlockPos = BlockPos(x, y, z)
                            val state: IBlockState = this.world.getBlockState(pos)
                            val block: Block = state.getBlock()

                            if (block is IOxygenReliantBlock) {
                                if (this.getDistanceFromServer(x, y, z) <= bubbleR2) {
                                    (block as IOxygenReliantBlock).onOxygenAdded(this.world, pos, state)
                                } else {
                                    //Do not necessarily extinguish it - it might be inside another oxygen system
                                    this.world.scheduleUpdate(pos, block, 0)
                                }
                            }
                        }
                    }
                }
            }
        }

        this.lastActive = this.active
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        if (nbt.hasKey("bubbleVisible")) {
            this.setBubbleVisible(nbt.getBoolean("bubbleVisible"))
        }

        if (nbt.hasKey("bubbleSize")) {
            this.bubbleSize = nbt.getFloat("bubbleSize")
        }

        //        this.hasValidBubble = nbt.getBoolean("hasValidBubble");
        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        nbt.setBoolean("bubbleVisible", this.shouldRenderBubble)
        nbt.setFloat("bubbleSize", this.bubbleSize)
        //        nbt.setBoolean("hasValidBubble", this.hasValidBubble);
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
        return GCCoreUtil.translate("container.oxygendistributor.name")
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                1 -> return itemstack.getItemDamage() < itemstack.getItem().getMaxDamage()
                else -> return false
            }
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItemEmpty(itemstack)
            1 -> return FluidUtil.isEmptyContainer(itemstack)
            else -> return false
        }
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        if (itemstack.isEmpty()) {
            return false
        }
        if (slotID == 0) {
            return ItemElectricBase.isElectricItem(itemstack.getItem())
        }
        if (slotID == 1) {
            return itemstack.getItem() is IItemOxygenSupply
        }
        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.getOxygenStored() > this.oxygenPerTick
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockOxygenDistributor) {
            return state.getValue(BlockOxygenDistributor.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getStackInSlot(0)
    }

    override fun shouldUseOxygen(): Boolean {
        return this.hasEnoughEnergyToRun
    }

    override fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricInputDirection().getOpposite())
    }

    override fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    //    @Override
    //    public IBubble getBubble()
    //    {
    //        return this.oxygenBubble;
    //    }
    open fun inBubble(pX: Double, pY: Double, pZ: Double): Boolean {
        var r: Double = bubbleSize.toDouble()
        r *= r
        var d3: Double = this.getPos().getX() + 0.5 - pX
        d3 *= d3
        if (d3 > r) {
            return false
        }
        var d4: Double = this.getPos().getZ() + 0.5 - pZ
        d4 *= d4
        if (d3 + d4 > r) {
            return false
        }
        val d5: Double = this.getPos().getY() + 0.5 - pY
        return d3 + d4 + d5 * d5 < r
    }

    override fun setBubbleVisible(shouldRender: Boolean) {
        this.shouldRenderBubble = shouldRender
        //        this.oxygenBubble.setShouldRender(shouldRender);
    }

    override fun getBubbleSize(): Float {
        return this.bubbleSize
    }

    override fun getBubbleVisible(): Boolean {
        return this.shouldRenderBubble
    }

    override fun getColor(): Vector3 {
        return Vector3(0.125, 0.125, 0.5)
    }

    companion object {
        @JvmField
        var loadedTiles: HashSet<BlockVec3Dim?> = HashSet<BlockVec3Dim?>()
    }
}
