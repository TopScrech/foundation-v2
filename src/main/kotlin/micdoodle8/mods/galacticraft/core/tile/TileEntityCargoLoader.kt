package micdoodle8.mods.galacticraft.core.tile

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.EnumCargoLoadingState
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.RemovalResult
import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.entity.ICargoEntity
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.api.tile.ILockable
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.blocks.BlockCargoLoader
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.fml.LogicalSide as Side

open class TileEntityCargoLoader constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    ILandingPadAttachable, ILockable {
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(15, ItemStack.EMPTY)

    @JvmField
    var outOfItems: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var targetFull: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var targetNoInventory: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var noTarget: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var locked: Boolean = false

    @JvmField
    var attachedFuelable: ICargoEntity? = null

    init {
        this.storage.setMaxExtract(45f)
    }

    override fun update() {
        super.update()

        if (!this.getWorld().isRemote) {
            if (this.ticks % 100 == 0) {
                this.checkForCargoEntity()
            }

            if (this.attachedFuelable != null) {
                this.noTarget = false
                val stack: ItemStack = this.removeCargo(false).resultStack

                if (!stack.isEmpty()) {
                    this.outOfItems = false

                    val state: ICargoEntity.EnumCargoLoadingState? = this.attachedFuelable!!.addCargo(stack, false)

                    this.targetFull = state == ICargoEntity.EnumCargoLoadingState.FULL
                    this.targetNoInventory = state == ICargoEntity.EnumCargoLoadingState.NOINVENTORY
                    this.noTarget = state == ICargoEntity.EnumCargoLoadingState.NOTARGET

                    if (this.ticks % (if (this.poweredByTierGC > 1) 9 else 15) == 0 && state == ICargoEntity.EnumCargoLoadingState.SUCCESS && !this.disabled && this.hasEnoughEnergyToRun) {
                        this.attachedFuelable!!.addCargo(this.removeCargo(true).resultStack, true)
                    }
                } else {
                    this.outOfItems = true
                }
            } else {
                this.noTarget = true
            }
        }
    }

    open fun checkForCargoEntity() {
        var foundFuelable: Boolean = false

        val thisVec: BlockVec3 = BlockVec3(this)
        for (dir: EnumFacing in EnumFacing.values()) {
            val pad: TileEntity? = thisVec.getTileEntityOnSide(this.getWorld(), dir)

            if (pad != null && pad is TileEntityMulti) {
                val mainTile: TileEntity? = pad.getMainBlockTile()

                if (mainTile is ICargoEntity) {
                    this.attachedFuelable = mainTile as ICargoEntity
                    foundFuelable = true
                    break
                }
            } else if (pad != null && pad is ICargoEntity) {
                this.attachedFuelable = pad as ICargoEntity
                foundFuelable = true
                break
            }
        }

        if (!foundFuelable) {
            this.attachedFuelable = null
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.stacks = this.readStandardItemsFromNBT(nbt)
        this.locked = nbt.getBooleanOr("locked", false)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.writeStandardItemsToNBT(nbt, stacks)
        nbt.putBoolean("locked", this.locked)
        return nbt
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.cargoloader.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return if (side != this.getElectricInputDirection()) intArrayOf(
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14
        ) else intArrayOf()
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (side != this.getElectricInputDirection()) {
            if (slotID == 0) {
                return ItemElectricBase.isElectricItem(itemstack.getItem())
            } else {
                return true
            }
        }

        return false
    }


    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return false
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        if (slotID == 0) {
            return ItemElectricBase.isElectricItem(itemstack.getItem())
        } else {
            return true
        }
    }

    override fun shouldUseEnergy(): Boolean {
        return !this.getDisabled(0)
    }

    open fun removeCargo(doRemove: Boolean): ICargoEntity.RemovalResult {
        for (i: Int in 1..<this.stacks.size) {
            val stackAt: ItemStack = this.stacks.get(i)

            if (!stackAt.isEmpty()) {
                val resultStack: ItemStack = stackAt.copy()
                resultStack.setCount(1)

                if (doRemove) {
                    stackAt.shrink(1)
                    if (stackAt.isEmpty()) {
                        this.stacks.set(i, ItemStack.EMPTY)
                    }
                }

                if (doRemove) {
                    this.setChanged()
                }

                return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.SUCCESS, resultStack)
            }
        }

        return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.EMPTY, ItemStack.EMPTY)
    }

    //Used by Abandoned Base worldgen
    open fun addCargo(stack: ItemStack, doAdd: Boolean): ICargoEntity.EnumCargoLoadingState {
        var count: Int = 1

        count = 1
        while (count < this.stacks.size) {
            val stackAt: ItemStack = this.stacks.get(count)

            if (!stackAt.isEmpty() && stackAt.getItem() === stack.getItem() && stackAt.legacyItemValue == stack.legacyItemValue && stackAt.getCount() < stackAt.getMaxStackSize()) {
                if (stackAt.getCount() + stack.getCount() <= stackAt.getMaxStackSize()) {
                    if (doAdd) {
                        stackAt.grow(stack.getCount())
                        this.setChanged()
                    }

                    return ICargoEntity.EnumCargoLoadingState.SUCCESS
                } else {
                    //Part of the stack can fill this slot but there will be some left over
                    val origSize: Int = stackAt.getCount()
                    val surplus: Int = origSize + stack.getCount() - stackAt.getMaxStackSize()

                    if (doAdd) {
                        stackAt.setCount(stackAt.getMaxStackSize())
                        this.setChanged()
                    }

                    stack.setCount(surplus)
                    if (this.addCargo(stack, doAdd) == ICargoEntity.EnumCargoLoadingState.SUCCESS) {
                        return ICargoEntity.EnumCargoLoadingState.SUCCESS
                    }

                    stackAt.setCount(origSize)
                    return ICargoEntity.EnumCargoLoadingState.FULL
                }
            }
            count++
        }

        count = 1
        while (count < this.stacks.size) {
            val stackAt: ItemStack = this.stacks.get(count)

            if (stackAt.isEmpty()) {
                if (doAdd) {
                    this.stacks.set(count, stack)
                    this.setChanged()
                }

                return ICargoEntity.EnumCargoLoadingState.SUCCESS
            }
            count++
        }

        return ICargoEntity.EnumCargoLoadingState.FULL
    }

    override fun canAttachToLandingPad(world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockCargoLoader) {
            return (state.getValue(BlockCargoLoader.FACING))
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun clearLockedInventory() {
        for (i: Int in 1..14) {
            this.stacks.set(i, ItemStack.EMPTY)
        }
    }

    override fun getLocked(): Boolean {
        return this.locked
    }
}
