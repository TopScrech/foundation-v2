package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.api.item.IModificationItem
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import galaxyspace.core.prefab.tile.TileEntityInventoryAdvanced
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList

open class TileEntityModificationTable constructor() : TileEntityInventoryAdvanced("tile.modification_table.name"),
    IInventoryDefaults, ISidedInventory {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileEntityInventoryAdvanced>.getDisplayName()

    init {
        this.inventory = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
    }

    override fun update() {
        super.update()

        val stack: ItemStack = this.getInventory().get(0)

        if (!stack.isEmpty() && stack.getItem() is IModificationItem) {
            if (!stack.hasTagCompound()) {
                val copied: ItemStack = stack
                copied.setTagCompound(NBTTagCompound())
                copied.getTagCompound()!!.putInt(ItemSpaceSuit.mod_count, 1)
                this.getInventory().set(0, copied)
            }
        }
    }

    open fun readStandardItemsFromNBT(nbt: NBTTagCompound): NonNullList<ItemStack> {
        val stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, stacks)
        return stacks
    }

    open fun writeStandardItemsToNBT(nbt: NBTTagCompound, stacks: NonNullList<ItemStack>) {
        ItemStackHelper.saveAllItems(nbt, stacks)
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())
        return par1NBTTagCompound
    }

    override fun getMaxStackSize(): Int {
        return 1
    }

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
        return false
    }

    protected override fun handleInventory(): Boolean {
        return false
    }

    override fun getContainerSize(): Int {
        return super.getContainerSize()
    }

    override fun getItem(par1: Int): ItemStack {
        return super.getItem(par1)
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        return super.removeItem(slot, amount)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return super.removeItemNoUpdate(slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        super.setItem(slot, stack)
    }

    override fun stillValid(player: EntityPlayer): Boolean {
        return !isInvalid() && this.world.isBlockLoaded(this.pos)
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, par2ItemStack)
    }

    override fun canTakeItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID == 1
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.getInventory()) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getPacketRange(): Double {
        return 12.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    // We don't use these because we use forge containers

    // We don't use these because we use forge containers




    override fun clearContent() {
        if (isEmpty) return
        getInventory().clear()
        setChanged()
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return super.getName()
    }

    override fun hasCustomName(): Boolean {
        return true
    }
}
