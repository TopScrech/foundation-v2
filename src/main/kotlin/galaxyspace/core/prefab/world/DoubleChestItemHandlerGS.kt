package galaxyspace.core.prefab.world

import javax.annotation.Nonnull

import com.google.common.base.Objects
import galaxyspace.core.prefab.tile.TileEntityChestGS
import galaxyspace.core.prefab.world.DoubleChestItemHandlerGS
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import java.lang.ref.WeakReference

open class DoubleChestItemHandlerGS constructor(
    private val mainChest: TileEntityChestGS?,
    other: TileEntityChestGS?,
    private val mainChestIsUpper: Boolean
) : WeakReference<TileEntityChestGS?>(other), IItemHandlerModifiable {
    private val hashCode: Int

    init {
        this.hashCode =
            Objects.hashCode(if (mainChestIsUpper) mainChest else other) * 31 + Objects.hashCode(if (!mainChestIsUpper) mainChest else other)
    }

    override fun getSlots(): Int {
        return 54
    }

    @Nonnull
    override fun getItem(slot: Int): ItemStack {
        val accessingUpperChest: Boolean = slot < 27
        val targetSlot: Int = if (accessingUpperChest) slot else slot - 27
        val chest: TileEntityChestGS? = this.getChest(accessingUpperChest)
        return if (chest != null) chest.getItem(targetSlot) else ItemStack.EMPTY
    }

    override fun setStackInSlot(slot: Int, @Nonnull itemStack: ItemStack) {
        val accessingUpperChest: Boolean = slot < 27
        val targetSlot: Int = if (accessingUpperChest) slot else slot - 27
        var chest: TileEntityChestGS? = this.getChest(accessingUpperChest)

        if (chest != null) {
            val singleHandler: IItemHandler? = chest.getSingleChestHandler()

            if (singleHandler is IItemHandlerModifiable) {
                singleHandler.setStackInSlot(targetSlot, itemStack)
            }
        }

        chest = this.getChest(!accessingUpperChest)

        if (chest != null) {
            chest.markDirty()
        }
    }

    @Nonnull
    override fun insertItem(slot: Int, @Nonnull itemStack: ItemStack, simulate: Boolean): ItemStack {
        val accessingUpperChest: Boolean = slot < 27
        val targetSlot: Int = if (accessingUpperChest) slot else slot - 27
        var chest: TileEntityChestGS? = this.getChest(accessingUpperChest)

        if (chest == null) {
            return itemStack
        }

        val starting: Int = itemStack.getCount()
        val ret: ItemStack = (chest.getSingleChestHandler())!!.insertItem(targetSlot, itemStack, simulate)

        if (ret.getCount() != starting && !simulate) {
            chest = this.getChest(!accessingUpperChest)

            if (chest != null) {
                chest.markDirty()
            }
        }
        return ret
    }

    @Nonnull
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        val accessingUpperChest: Boolean = slot < 27
        val targetSlot: Int = if (accessingUpperChest) slot else slot - 27
        var chest: TileEntityChestGS? = this.getChest(accessingUpperChest)

        if (chest == null) {
            return ItemStack.EMPTY
        }

        val ret: ItemStack = (chest.getSingleChestHandler())!!.extractItem(targetSlot, amount, simulate)

        if (!ret.isEmpty() && !simulate) {
            chest = this.getChest(!accessingUpperChest)

            if (chest != null) {
                chest.markDirty()
            }
        }
        return ret
    }

    override fun getSlotLimit(slot: Int): Int {
        val accessingUpperChest: Boolean = slot < 27
        return this.getChest(accessingUpperChest)!!.getMaxStackSize()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || this.javaClass != o.javaClass) {
            return false
        }

        val that: DoubleChestItemHandlerGS = o as DoubleChestItemHandlerGS

        if (this.hashCode != that.hashCode) {
            return false
        }

        val otherChest: TileEntityChestGS? = this.getOtherChest()

        if (this.mainChestIsUpper == that.mainChestIsUpper) {
            return Objects.equal(this.mainChest, that.mainChest) && Objects.equal(otherChest, that.getOtherChest())
        } else {
            return Objects.equal(this.mainChest, that.getOtherChest()) && Objects.equal(otherChest, that.mainChest)
        }
    }

    override fun hashCode(): Int {
        return this.hashCode
    }

    open fun needsRefresh(): Boolean {
        if (this === NO_ADJACENT_CHESTS_INSTANCE) {
            return false
        }
        val TileEntityChestMP: TileEntityChestGS? = this.get()
        return TileEntityChestMP == null || TileEntityChestMP.isInvalid()
    }

    private fun getChest(accessingUpper: Boolean): TileEntityChestGS? {
        if (accessingUpper == this.mainChestIsUpper) {
            return this.mainChest
        } else {
            return this.getOtherChest()
        }
    }

    private fun getOtherChest(): TileEntityChestGS? {
        val TileEntityChestMP: TileEntityChestGS? = this.get()
        return if (TileEntityChestMP != null && !TileEntityChestMP.isInvalid()) TileEntityChestMP else null
    }

    companion object {
        @JvmField
        val NO_ADJACENT_CHESTS_INSTANCE: DoubleChestItemHandlerGS = DoubleChestItemHandlerGS(null, null, false)

        @JvmStatic
        fun get(chest: TileEntityChestGS): DoubleChestItemHandlerGS? {
            val world: World = chest.getWorld()
            val pos: BlockPos = chest.getPos()

            if (world == null || pos == null || !world.isBlockLoaded(pos)) {
                return null // Still loading
            }

            val blockType: Block = chest.getBlockType()
            val horizontals: Array<EnumFacing> = EnumFacing.HORIZONTALS

            for (i: Int in horizontals.indices.reversed())  // Use reverse order so we can return early
            {
                val enumfacing: EnumFacing = horizontals[i]
                val blockpos: BlockPos = pos.offset(enumfacing)
                val block: Block = world.getBlockState(blockpos).getBlock()

                if (block === blockType) {
                    val otherTE: TileEntity? = world.getBlockEntity(blockpos)

                    if (otherTE is TileEntityChestGS) {
                        val otherChest: TileEntityChestGS = otherTE
                        return DoubleChestItemHandlerGS(
                            chest,
                            otherChest,
                            enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH
                        )
                    }
                }
            }
            return NO_ADJACENT_CHESTS_INSTANCE //All alone
        }
    }
}
