package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.floor

open class TileEntityDish constructor() : TileBaseUniversalElectrical(), IMultiBlock, IDisableableMachine,
    IInventoryDefaults, ISidedInventory, IConnector {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileBaseUniversalElectrical>.getDisplayName()

    @JvmField
    var targetAngle: Float = 0f

    @JvmField
    var currentAngle: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disableCooldown: Int = 0
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)

    private var initialised = false
    private var renderAABB: AxisAlignedBB? = null

    init {
        this.storage.setMaxExtract(100f)
        this.setTierGC(1)
    }

    open fun rotation(partial: Float): Float {
        return (this.ticks + partial) / 12
    }

    open fun elevation(partial: Float): Float {
        return (MathHelper.sin(rotation(partial) / 40) + 1.0f) * 22.5f
    }

    override fun update() {
        super.update()

        if (!this.initialised) {
            this.initialised = this.initialiseMultiTiles(this.getPos(), this.world)
        }

        if (!this.world.isRemote) {
            if (this.disableCooldown > 0) {
                this.disableCooldown--
            }
        }

        val angle: Float = if (this.world.getCelestialAngle(1.0f) - 0.7845194f < 0) 1.0f - 0.7845194f else -0.7845194f
        var celestialAngle: Float = (this.world.getCelestialAngle(1.0f) + angle) * 360.0f

        celestialAngle %= 360f

        run {
            if (celestialAngle > 30 && celestialAngle < 150) {
                val difference: Float = this.targetAngle - celestialAngle

                this.targetAngle -= difference / 20.0f
            } else if (!this.world.isDaytime() || this.world.isRaining() || this.world.isThundering()) {
                this.targetAngle = 77.5f + 180.0f
            } else if (celestialAngle < 50) {
                this.targetAngle = 50f
            } else if (celestialAngle > 150) {
                this.targetAngle = 150f
            }
        }

        val difference: Float = this.targetAngle - this.currentAngle

        this.currentAngle += difference / 20.0f
    }

    protected open fun initialiseMultiTiles(pos: BlockPos, world: World): Boolean {
        //Client can create its own fake blocks and tiles - no need for networking in 1.8+
        if (world.isRemote) this.onCreate(world, pos)

        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(pos, (positions as MutableList<BlockPos?>?))
        var result: Boolean = true
        for (vecToAdd: BlockPos in positions) {
            val tile: TileEntity? = world.getTileEntity(vecToAdd)
            if (tile is TileEntityMulti) {
                tile.mainBlockPosition = pos
            } else {
                result = false
            }
        }
        return result
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        return false // TODO
        //        return this.getBlockType().onBlockActivated(this.world, this.getPos(), this.world.getBlockState(this.getPos()), entityPlayer, EnumFacing.DOWN, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock((world)!!, positions, placedPosition, this.getMultiType())
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return BlockMulti.EnumBlockMultiType.DISH_LARGE
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val buildHeight: Int = this.world.getHeight() - 1
        var y: Int = (placedPosition)!!.getY()

        if (++y > buildHeight) {
            return
        }
        (positions)!!.add(BlockPos((placedPosition)!!.getX(), y, (placedPosition)!!.getZ()))

        if (++y > buildHeight) {
            return
        }
        (positions)!!.add(BlockPos((placedPosition)!!.getX(), y, (placedPosition)!!.getZ()))

        if (++y > buildHeight) {
            return
        }
        (positions)!!.add(BlockPos((placedPosition)!!.getX(), y, (placedPosition)!!.getZ()))

        if (++y > buildHeight) {
            return
        }
        for (x: Int in -1..1) {
            for (z: Int in -1..1) {
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }

        if (++y > buildHeight) {
            return
        }
        for (x: Int in -1..1) {
            for (z: Int in -1..1) {
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }

        if (++y > buildHeight) {
            return
        }
        for (x: Int in -3..3) {
            for (z: Int in -3..3) {
                if (abs(x) + abs(z) == 6) continue
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }

        if (++y > buildHeight) {
            return
        }
        for (x: Int in -3..3) {
            for (z: Int in -3..3) {
                if (abs(x) + abs(z) == 6) continue
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }

        if (++y > buildHeight) {
            return
        }
        for (x: Int in -2..2) {
            for (z: Int in -2..2) {
                if (abs(x) + abs(z) == 4) continue
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === GCBlocks.fakeBlock && stateAt.getValue(BlockMulti.MULTI_TYPE) as BlockMulti.EnumBlockMultiType == BlockMulti.EnumBlockMultiType.DISH_LARGE) {
                if (this.world.isRemote && this.world.rand.nextDouble() < 0.05) {
                    FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(
                        pos,
                        this.world.getBlockState(pos)
                    )
                }
                this.world.setBlockToAir(pos)
            }
        }
        this.world.destroyBlock(thisBlock, true)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.storage.setCapacity(nbt.getFloat("maxEnergy"))
        this.currentAngle = nbt.getFloat("currentAngle")
        this.targetAngle = nbt.getFloat("targetAngle")
        this.setDisabled(0, nbt.getBoolean("disabled"))
        this.disableCooldown = nbt.getInteger("disabledCooldown")

        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        this.initialised = false
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setFloat("maxEnergy", this.getMaxEnergyStoredGC())
        nbt.setFloat("currentAngle", this.currentAngle)
        nbt.setFloat("targetAngle", this.targetAngle)
        nbt.setInteger("disabledCooldown", this.disableCooldown)
        nbt.setBoolean("disabled", this.getDisabled(0))

        ItemStackHelper.saveAllItems(nbt, this.stacks)

        return nbt
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos.add(-3, 0, -3), pos.add(3, 8, 3))
        }
        return this.renderAABB!!
    }

    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_MEDIUM
    }

    //    @Override
    //    public boolean hasCustomName()
    //    {
    //        return true;
    //    }
    override fun getName(): String {
        //TODO
        return ""
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown == 0) {
            this.disabled = disabled
            this.disableCooldown = 20
        }
    }

    override fun getDisabled(index: Int): Boolean {
        return this.disabled
    }

    open fun getScaledElecticalLevel(i: Int): Int {
        return floor((this.getEnergyStoredGC() * i / this.getMaxEnergyStoredGC()).toDouble()).toInt()
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

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return this.isItemValidForSlot(slotID, itemstack)
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem())
    }
}
