package galaxyspace.core.prefab.tile

import net.minecraft.network.chat.Component

import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component as ITextComponent

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import net.minecraftforge.items.wrapper.SidedInvWrapper

/** Shared electric inventory for machines that previously used the bundled Galacticraft API */
abstract class TileEntityElectricInventory(private val tileName: String?) : TileBaseElectricBlockWithInventory(), ISidedInventory {
    @JvmField
    var inventory: NonNullList<ItemStack> = NonNullList.create()

    open fun getInventory(): NonNullList<ItemStack> = inventory

    override fun getContainingItems(): NonNullList<ItemStack> = inventory

    override fun getName(): net.minecraft.network.chat.Component = Component.translatable(tileName ?: "container.inventory")

    override fun getDisplayName(): ITextComponent = getName()

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean = true
    override fun getSlotsForFace(side: EnumFacing): IntArray = IntArray(getContainerSize()) { it }
    override fun canPlaceItemThroughFace(index: Int, stack: ItemStack, side: EnumFacing?): Boolean = canPlaceItem(index, stack)
    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, side: EnumFacing): Boolean = true

    override fun clearContent() {
        if (isEmpty) return
        inventory.clear()
        setChanged()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, inventory)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, inventory)
        return nbt
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
        capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (if (this is ISidedInventory && facing != null) SidedInvWrapper(this, facing) else InvWrapper(this)) as T
        }
        return super.getCapability(capability, facing)
    }
}
