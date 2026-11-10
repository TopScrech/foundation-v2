package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.api.item.IModificationItem
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import galaxyspace.core.prefab.tile.TileEntityInventoryAdvanced
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList

open class TileEntityModificationTable constructor() : TileEntityInventoryAdvanced("tile.modification_table.name"),
    IInventoryDefaults, ISidedInventory {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent = super<TileEntityInventoryAdvanced>.getDisplayName()

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
                copied.getTagCompound()!!.setInteger(ItemSpaceSuit.mod_count, 1)
                this.getInventory().set(0, copied)
            }
        }
    }

    open fun readStandardItemsFromNBT(nbt: NBTTagCompound): NonNullList<ItemStack> {
        val stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
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

    override fun getInventoryStackLimit(): Int {
        return 1
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return false
    }

    protected override fun handleInventory(): Boolean {
        return false
    }

    override fun getSizeInventory(): Int {
        return super.getSizeInventory()
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        return super.getStackInSlot(par1)
    }

    override fun decrStackSize(slot: Int, amount: Int): ItemStack {
        return super.decrStackSize(slot, amount)
    }

    override fun removeStackFromSlot(slot: Int): ItemStack {
        return super.removeStackFromSlot(slot)
    }

    override fun setInventorySlotContents(slot: Int, stack: ItemStack) {
        super.setInventorySlotContents(slot, stack)
    }

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return !isInvalid() && this.world.isBlockLoaded(this.pos)
    }

    override fun getSlotsForFace(side: EnumFacing?): IntArray {
        return intArrayOf(0)
    }

    override fun canInsertItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return this.isItemValidForSlot(slotID, par2ItemStack)
    }

    override fun canExtractItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
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
    override fun openInventory(player: EntityPlayer) {
    }

    // We don't use these because we use forge containers
    override fun closeInventory(player: EntityPlayer) {
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {
    }

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
        for (i: Int in 0..<this.getInventory().size) {
            this.getInventory().set(i, ItemStack.EMPTY)
        }
    }

    override fun getName(): String {
        return super.getName()
    }

    override fun hasCustomName(): Boolean {
        return true
    }
}
