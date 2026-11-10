package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.EnumCargoLoadingState
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.RemovalResult
import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.entity.ICargoEntity
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.blocks.BlockCargoLoader
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side

open class TileEntityCargoUnloader constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    ILandingPadAttachable {
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(15, ItemStack.EMPTY)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var targetEmpty: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var targetNoInventory: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var noTarget: Boolean = false

    @JvmField
    var attachedFuelable: ICargoEntity? = null

    init {
        this.storage.setMaxExtract(45f)
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            if (this.ticks % 100 == 0) {
                this.checkForCargoEntity()
            }

            if (this.attachedFuelable != null) {
                this.noTarget = false
                val result: ICargoEntity.RemovalResult = (this.attachedFuelable!!.removeCargo(false))!!

                if (!result.resultStack.isEmpty()) {
                    this.targetEmpty = false

                    val state: ICargoEntity.EnumCargoLoadingState = this.addCargo(result.resultStack, false)

                    this.targetEmpty = state == ICargoEntity.EnumCargoLoadingState.EMPTY

                    if (this.ticks % (if (this.poweredByTierGC > 1) 9 else 15) == 0 && state == ICargoEntity.EnumCargoLoadingState.SUCCESS && !this.disabled && this.hasEnoughEnergyToRun) {
                        this.addCargo((this.attachedFuelable!!.removeCargo(true))!!.resultStack, true)
                    }
                } else {
                    this.targetNoInventory = result.resultState == ICargoEntity.EnumCargoLoadingState.NOINVENTORY
                    this.noTarget = result.resultState == ICargoEntity.EnumCargoLoadingState.NOTARGET
                    this.targetEmpty = true
                }
            } else {
                this.noTarget = true
            }
        }
    }

    open fun checkForCargoEntity() {
        var foundFuelable: Boolean = false

        val thisVec: BlockVec3 = BlockVec3(this)
        for (dir: EnumFacing in EnumFacing.VALUES) {
            val pad: TileEntity? = thisVec.getTileEntityOnSide(this.world, dir)

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
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.writeStandardItemsToNBT(nbt, this.stacks)
        return nbt
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }


    //    @Override
    //    public boolean hasCustomName()
    //    {
    //        return true;
    //    }
    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.cargounloader.name")
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return if (side != this.getElectricInputDirection()) intArrayOf(
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

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (side != this.getElectricInputDirection()) {
            if (slotID == 0) {
                return ItemElectricBase.isElectricItemEmpty(itemstack)
            } else {
                return true
            }
        }

        return false
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        if (slotID == 0) {
            return ItemElectricBase.isElectricItem(itemstack.getItem())
        } else {
            return true
        }
    }

    override fun shouldUseEnergy(): Boolean {
        return !this.getDisabled(0)
    }

    open fun addCargo(stack: ItemStack, doAdd: Boolean): ICargoEntity.EnumCargoLoadingState {
        var count: Int = 1

        count = 1
        while (count < this.stacks.size) {
            val stackAt: ItemStack = this.stacks.get(count)

            if (!stackAt.isEmpty() && stackAt.getItem() === stack.getItem() && stackAt.getItemDamage() == stack.getItemDamage() && stackAt.getCount() < stackAt.getMaxStackSize()) {
                if (stackAt.getCount() + stack.getCount() <= stackAt.getMaxStackSize()) {
                    if (doAdd) {
                        stackAt.grow(stack.getCount())
                        this.markDirty()
                    }

                    return ICargoEntity.EnumCargoLoadingState.SUCCESS
                } else {
                    //Part of the stack can fill this slot but there will be some left over
                    val origSize: Int = stackAt.getCount()
                    val surplus: Int = origSize + stack.getCount() - stackAt.getMaxStackSize()

                    if (doAdd) {
                        stackAt.setCount(stackAt.getMaxStackSize())
                        this.markDirty()
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
                    this.markDirty()
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
}
