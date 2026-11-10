package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.api.tile.ITileClientUpdates
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenSealer
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.fluid.OxygenPressureProtocol
import micdoodle8.mods.galacticraft.core.fluid.ThreadFindSeal
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.floor
import kotlin.math.min

open class TileEntityOxygenSealer constructor() : TileEntityOxygen(10000, UNSEALED_OXYGENPERTICK),
    IInventoryDefaults, ISidedInventory, ITileClientUpdates {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileEntityOxygen>.getDisplayName()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var sealed: Boolean = false

    @JvmField
    var lastSealed: Boolean = false

    @JvmField
    var lastDisabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var active: Boolean = false
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(3, ItemStack.EMPTY)

    @JvmField
    var threadSeal: ThreadFindSeal? = null

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var stopSealThreadCooldown: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var threadCooldownTotal: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var calculatingSealed: Boolean = false

    @JvmField
    var leaksClient: MutableList<BlockVec3?>? = null


    init {
        this.noRedstoneControl = true
        this.storage.setMaxExtract(5.0f) //Half of a standard machine's power draw
        this.storage.setMaxReceive(25.0f)
        this.storage.setCapacity(STANDARD_CAPACITY * 2) //Large capacity so it can keep working for a while even if chunk unloads affect its power supply
    }

    override fun onLoad() {
        if (!this.world.isRemote) {
            if (!loadedTiles.contains(this)) {
                loadedTiles.add(this)
            }
            this.stopSealThreadCooldown = 126 + countEntities
        } else {
            this.clientOnLoad()
        }
    }

    override fun invalidate() {
        if (!this.world.isRemote) {
            loadedTiles.remove(this)
        }
        super.invalidate()
    }

    override fun onChunkUnload() {
        if (!this.world.isRemote) {
            loadedTiles.remove(this)
        }
        super.onChunkUnload()
    }

    open fun getScaledThreadCooldown(i: Int): Int {
        if (this.active) {
            return min(i, floor(this.stopSealThreadCooldown * i / this.threadCooldownTotal.toDouble()).toInt())
        }
        return 0
    }

    open fun getFindSealChecks(): Int {
        if (!this.active || this.getOxygenStored() < this.oxygenPerTick || !this.hasEnoughEnergyToRun) {
            return 0
        }
        val posAbove: BlockPos = BlockPos(this.getPos().getX(), this.getPos().getY() + 1, this.getPos().getZ())
        val stateAbove: IBlockState = this.world.getBlockState(posAbove)
        if (!(stateAbove.getBlock().isAir(stateAbove, this.world, posAbove)) && !OxygenPressureProtocol.canBlockPassAir(
                this.world,
                stateAbove.getBlock(),
                this.getPos().up(),
                EnumFacing.UP
            )
        ) {
            // The vent is blocked
            return 0
        }

        return 1250
    }

    open fun thermalControlEnabled(): Boolean {
        val oxygenItemStack: ItemStack = this.getStackInSlot(2)
        return oxygenItemStack != null && oxygenItemStack.getItem() === GCItems.basicItem && oxygenItemStack.getItemDamage() == 20 && this.hasEnoughEnergyToRun && !this.disabled
    }

    override fun update() {
        if (!this.world.isRemote) {
            val oxygenItemStack: ItemStack = this.getStackInSlot(1)
            if (oxygenItemStack != null && oxygenItemStack.getItem() is IItemOxygenSupply) {
                val oxygenItem: IItemOxygenSupply = oxygenItemStack.getItem() as IItemOxygenSupply
                val oxygenDraw: Int = floor(
                    min(
                        UNSEALED_OXYGENPERTICK * 2.5f,
                        (this.getMaxOxygenStored() - this.getOxygenStored()).toFloat()
                    ).toDouble()
                ).toInt()
                this.setOxygenStored(getOxygenStored() + oxygenItem.discharge(oxygenItemStack, oxygenDraw))
                if (this.getOxygenStored() > this.getMaxOxygenStored()) {
                    this.setOxygenStored(this.getOxygenStored())
                }
            }

            if (this.thermalControlEnabled()) {
                if (this.storage.getMaxExtract() != 20.0f) {
                    this.storage.setMaxExtract(20.0f)
                }
            } else if (this.storage.getMaxExtract() != 5.0f) {
                this.storage.setMaxExtract(5.0f)
                this.storage.setMaxReceive(25.0f)
            }
        }

        this.oxygenPerTick = if (this.sealed) 2 else UNSEALED_OXYGENPERTICK
        super.update()

        if (!this.world.isRemote) {
            // Some code to count the number of Oxygen Sealers being updated,
            // tick by tick - needed for queueing
            countTemp++

            this.active = this.getOxygenStored() >= 1 && this.hasEnoughEnergyToRun && !this.disabled
            if (this.disabled != this.lastDisabled) {
                this.lastDisabled = this.disabled
                if (!this.disabled) this.stopSealThreadCooldown = this.threadCooldownTotal * 3 / 5
            }

            //TODO: if multithreaded, this codeblock should not run if the current threadSeal is flagged looping
            if (this.stopSealThreadCooldown > 0) {
                this.stopSealThreadCooldown--
            } else if (!sealerCheckedThisTick) {
                // This puts any Sealer which is updated to the back of the queue for updates
                this.stopSealThreadCooldown = 75 + countEntities
                this.threadCooldownTotal = this.stopSealThreadCooldown
                if (this.active || this.sealed) {
                    sealerCheckedThisTick = true
                    OxygenPressureProtocol.updateSealerStatus(this)
                }
            }

            //TODO: if multithreaded, this.threadSeal needs to be atomic
            if (this.threadSeal != null) {
                if (this.threadSeal!!.looping.get()) {
                    this.calculatingSealed = this.active
                } else {
                    this.calculatingSealed = false
                    this.sealed = this.threadSeal!!.sealedFinal.get()
                }
            } else {
                this.calculatingSealed = true //Give an initial 'Check pending' in GUI when first placed
            }

            this.lastSealed = this.sealed
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
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
        return GCCoreUtil.translate("container.oxygensealer.name")
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
                2 -> return itemstack.getItem() === GCItems.basicItem && itemstack.getItemDamage() == 20
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
        if (slotID == 2) {
            return itemstack.getItem() === GCItems.basicItem && itemstack.getItemDamage() == 20
        }
        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.getOxygenStored() > this.oxygenPerTick && !this.getDisabled(0)
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockOxygenSealer) {
            return state.getValue(BlockOxygenSealer.FACING)
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
        return this.hasEnoughEnergyToRun && this.active
    }

    override fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricInputDirection().getOpposite())
    }

    override fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun sendUpdateToClient(player: EntityPlayerMP) {
        if (this.sealed || this.threadSeal == null || this.threadSeal!!.leakTrace == null || (this.threadSeal!!.leakTrace)!!.isEmpty()) {
            return
        }
        val data: Array<Int> = (arrayOfNulls<Int>((this.threadSeal!!.leakTrace)!!.size) as Array<Int>)
        var index: Int = 0
        for (vec in (this.threadSeal!!.leakTrace)!!) {
            val dx: Int = (vec)!!.x - this.pos.getX() + 128
            val dz: Int = (vec)!!.z - this.pos.getZ() + 128
            val dy: Int = (vec)!!.y
            val composite: Int
            if (dx < 0 || dx > 255 || dz < 0 || dz > 255 || dy < 0) composite = -1
            else composite = dz + ((dy + (dx shl 8)) shl 8)
            data[index++] = composite
        }
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_LEAK_DATA,
                GCCoreUtil.getDimensionID(player.world),
                arrayOf<Any>(this.getPos(), data)
            ), player
        )
    }

    override fun buildDataPacket(data: IntArray?) {
        //unused
    }

    override fun updateClient(data: MutableList<Any?>?) {
        this.leaksClient = ArrayList<BlockVec3?>()
        if ((data)!!.size > 1) {
            for (i: Int in 1..<(data)!!.size) {
                val comp: Int = (data)!!.get(i) as Int
                if (comp >= 0) {
                    val dx: Int = (comp shr 16) - 128
                    val dy: Int = (comp shr 8) and 255
                    val dz: Int = (comp and 255) - 128
                    this.leaksClient!!.add(BlockVec3(this.pos.getX() + dx, dy, this.pos.getZ() + dz))
                }
            }
        }
    }

    open fun getLeakTraceClient(): MutableList<BlockVec3?> {
        this.clientOnLoad()
        return this.leaksClient!!
    }

    companion object {
        @JvmField
        var countEntities: Int = 0
        private var countTemp = 0
        private var sealerCheckedThisTick = false

        @JvmField
        var loadedTiles: ArrayList<TileEntityOxygenSealer?> = ArrayList<TileEntityOxygenSealer?>()
        private const val UNSEALED_OXYGENPERTICK = 12

        @JvmStatic
        fun onServerTick() {
            countEntities = countTemp
            countTemp = 0
            sealerCheckedThisTick = false
        }

        @JvmStatic
        fun getSealersAround(
            world: World?,
            pos: BlockPos,
            rSquared: Int
        ): HashMap<BlockVec3?, TileEntityOxygenSealer?> {
            val ret: HashMap<BlockVec3?, TileEntityOxygenSealer?> = HashMap<BlockVec3?, TileEntityOxygenSealer?>()

            for (tile: TileEntityOxygenSealer? in ArrayList<TileEntityOxygenSealer?>(loadedTiles)) {
                if (tile != null && tile.getWorld() === world && tile.getDistanceSq(
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble()
                    ) < rSquared
                ) {
                    ret.put(BlockVec3(tile.getPos()), tile)
                }
            }

            return ret
        }

        @JvmStatic
        fun getNearestSealer(world: World, x: Double, y: Double, z: Double): TileEntityOxygenSealer? {
            var ret: TileEntityOxygenSealer? = null
            var dist: Double = 96 * 96.0

            for (tile: Any? in world.loadedTileEntityList) {
                if (tile is TileEntityOxygenSealer) {
                    val testDist: Double = tile.getDistanceSq(x, y, z)
                    if (testDist < dist) {
                        dist = testDist
                        ret = tile
                    }
                }
            }

            return ret
        }
    }
}
