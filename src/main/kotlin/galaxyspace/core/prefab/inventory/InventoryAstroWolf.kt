package galaxyspace.core.prefab.inventory

import net.minecraft.world.ContainerHelper
import galaxyspace.core.prefab.inventory.InventoryAstroWolf
import galaxyspace.core.prefab.entities.EntityAstroWolf
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component as ITextComponent

open class InventoryAstroWolf constructor(private val wolf: EntityAstroWolf) : IInventoryDefaults {
    private val stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(3, ItemStack.EMPTY)

    @JvmField
    var currentContainer: Container? = null

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    open fun getInventory(): NonNullList<ItemStack> {
        return this.stacks
    }

    override fun getItem(par1: Int): ItemStack {
        return if (par1 >= this.getContainerSize()) ItemStack.EMPTY else this.stacks.get(par1)
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.astro_wolf_inventory.name")
    }

    override fun removeItemNoUpdate(par1: Int): ItemStack {
        return ContainerHelper.takeItem(this.stacks, par1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getMaxStackSize(): Int {
        return 1
    }

    override fun setChanged() {
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return !this.wolf.isDead && par1EntityPlayer.getDistanceSq(this.wolf) <= 64.0
    }

    override fun canPlaceItem(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    //We don't use these because we use forge containers

    //We don't use these because we use forge containers




    override fun clearContent() {
        for (slot in 0 until containerSize) setItem(slot, ItemStack.EMPTY)
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getDisplayName(): ITextComponent = getName()
}
