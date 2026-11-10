package galaxyspace.core.prefab.tile

import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
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

    override fun getName(): String = tileName ?: "container.inventory"

    override fun getDisplayName(): ITextComponent = TextComponentTranslation(getName())

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean = true
    override fun getSlotsForFace(side: EnumFacing?): IntArray = IntArray(getSizeInventory()) { it }
    override fun canInsertItem(index: Int, stack: ItemStack, side: EnumFacing?): Boolean = isItemValidForSlot(index, stack)
    override fun canExtractItem(index: Int, stack: ItemStack, side: EnumFacing?): Boolean = true

    override fun clear() { inventory.clear() }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY)
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
