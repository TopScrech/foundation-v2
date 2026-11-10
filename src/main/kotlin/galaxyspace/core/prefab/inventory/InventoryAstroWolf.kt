package galaxyspace.core.prefab.inventory

import galaxyspace.core.prefab.inventory.InventoryAstroWolf
import galaxyspace.core.prefab.entities.EntityAstroWolf
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation

open class InventoryAstroWolf constructor(private val wolf: EntityAstroWolf) : IInventory {
    private val stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(3, ItemStack.EMPTY)

    @JvmField
    var currentContainer: Container? = null

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    open fun getInventory(): NonNullList<ItemStack> {
        return this.stacks
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        return if (par1 >= this.getSizeInventory()) ItemStack.EMPTY else this.stacks.get(par1)
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.astro_wolf_inventory.name")
    }

    override fun removeStackFromSlot(par1: Int): ItemStack {
        return ItemStackHelper.getAndRemove(this.stacks, par1)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
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
        return 1
    }

    override fun markDirty() {
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return !this.wolf.isDead && par1EntityPlayer.getDistanceSq(this.wolf) <= 64.0
    }

    override fun isItemValidForSlot(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    //We don't use these because we use forge containers
    override fun openInventory(player: EntityPlayer) {
    }

    //We don't use these because we use forge containers
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
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getDisplayName(): ITextComponent {
        return if (this.hasCustomName()) TextComponentString(this.getName()) else TextComponentTranslation(
            this.getName(),
            *arrayOfNulls<Any>(0)
        )
    }
}
