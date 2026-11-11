package galaxyspace.core.prefab.tile

import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygen
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.SidedInvWrapper

abstract class TileEntityInventoryOxygen(private val tileName: String?, maxOxygen: Int, oxygenPerTick: Int) : TileEntityOxygen(maxOxygen, oxygenPerTick), MachineInventoryDefaults {
    @JvmField var inventory: NonNullList<ItemStack> = NonNullList.create()
    override fun getInventory(): NonNullList<ItemStack> = inventory
    override fun getInventoryBlockEntity(): net.minecraft.world.level.block.entity.BlockEntity = this
    override fun getName(): net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.translatable(tileName ?: "container.inventory")
    override fun getDisplayName(): net.minecraft.network.chat.Component = getName()
    override fun stillValid(player: net.minecraft.entity.player.EntityPlayer): Boolean = super<TileEntityOxygen>.stillValid(player)
    protected open fun handleInventory(): Boolean = true

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        if (handleInventory()) {
            inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY)
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
