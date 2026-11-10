package galaxyspace.core.prefab.tile

import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygen
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.SidedInvWrapper

abstract class TileEntityInventoryOxygen(private val tileName: String?, maxOxygen: Int, oxygenPerTick: Int) : TileEntityOxygen(maxOxygen, oxygenPerTick), MachineInventoryDefaults {
    @JvmField var inventory: NonNullList<ItemStack> = NonNullList.create()
    override fun getInventory(): NonNullList<ItemStack> = inventory
    override fun getName(): String = tileName ?: "container.inventory"
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent = net.minecraft.util.text.TextComponentTranslation(getName())
    override fun isUsableByPlayer(player: net.minecraft.entity.player.EntityPlayer): Boolean = super<TileEntityOxygen>.isUsableByPlayer(player)
    protected open fun handleInventory(): Boolean = true

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        if (handleInventory()) {
            inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY)
            ItemStackHelper.loadAllItems(nbt, inventory)
        }
    }
    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        if (handleInventory()) ItemStackHelper.saveAllItems(nbt, inventory)
        return nbt
    }
    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
        capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (if (facing == null) net.minecraftforge.items.wrapper.InvWrapper(this) else SidedInvWrapper(this, facing)) as T
        }
        return super.getCapability(capability, facing)
    }
}
