package micdoodle8.mods.galacticraft.planets.mars.inventory

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.core.NonNullList

open class InventorySlimeling constructor(private val slimeling: EntitySlimeling) : IInventoryDefaults {
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(30, ItemStack.EMPTY)

    @JvmField
    var currentContainer: Container? = null

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    override fun getItem(par1: Int): ItemStack {
        return if (par1 >= this.getContainerSize()) ItemStack.EMPTY else this.stacks.get(par1)
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.slimeling_inventory.name")
    }

    override fun removeItemNoUpdate(par1: Int): ItemStack {
        if (!this.stacks.get(par1).isEmpty()) {
            val var2: ItemStack = this.stacks.get(par1)
            this.stacks.set(par1, ItemStack.EMPTY)
            this.setChanged()
            return var2
        } else {
            return ItemStack.EMPTY
        }
    }

    private fun removeInventoryBagContents() {
        if (this.currentContainer is ContainerSlimeling) {
            ContainerSlimeling.Companion.removeSlots(this.currentContainer as ContainerSlimeling)
        }

        for (i: Int in 2..<this.stacks.size) {
            if (!this.stacks.get(i).isEmpty()) {
                if (!this.slimeling.world.isRemote) {
                    this.slimeling.entityDropItem(this.stacks.get(i), 0.5f)
                }

                this.stacks.set(i, ItemStack.EMPTY)
            }
        }
    }

    override fun removeItem(par1: Int, par2: Int): ItemStack {
        if (!this.stacks.get(par1).isEmpty()) {
            val var3: ItemStack

            //It's a removal of the Slimeling Inventory Bag
            if (par1 == 1 && this.stacks.get(par1).getCount() <= par2) {
                this.removeInventoryBagContents()
                var3 = this.stacks.get(par1)
                this.stacks.set(par1, ItemStack.EMPTY)
                this.setChanged()
                return var3
            } else  //Normal case of decrStackSize for a slot
            {
                var3 = this.stacks.get(par1).splitStack(par2)

                if (this.stacks.get(par1).isEmpty()) {
                    //Not sure if this is necessary again, given the above?
                    if (par1 == 1) {
                        this.removeInventoryBagContents()
                    }

                    this.stacks.set(par1, ItemStack.EMPTY)
                }

                this.setChanged()
                return var3
            }
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun setItem(par1: Int, par2ItemStack: ItemStack) {
        if (par1 == 1 && (par2ItemStack.isEmpty() && !this.stacks.get(par1).isEmpty() || !ItemStack.areItemStacksEqual(
                par2ItemStack,
                this.stacks.get(par1)
            ))
        ) {
            ContainerSlimeling.Companion.addAdditionalSlots(
                (this.currentContainer as ContainerSlimeling?)!!,
                this.slimeling,
                par2ItemStack
            )
        }

        this.stacks.set(par1, par2ItemStack)
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

    open fun readFromNBT(tagList: NBTTagList?) {
        if (tagList == null || tagList.size <= 0) {
            return
        }

        this.stacks = NonNullList.withSize<ItemStack>(this.stacks.size, ItemStack.EMPTY)

        for (i: Int in 0..<tagList.size) {
            val nbttagcompound: NBTTagCompound = tagList.getCompoundOrEmpty(i)
            val j: Int = nbttagcompound.getByteOr("Slot", 0).toInt() and 255
            val itemstack: ItemStack = ItemStack(nbttagcompound)

            if (!itemstack.isEmpty()) {
                this.stacks.set(j, itemstack)
            }
        }
    }

    open fun writeToNBT(tagList: NBTTagList): NBTTagList? {
        var nbttagcompound: NBTTagCompound

        for (i: Int in this.stacks.indices) {
            if (!this.stacks.get(i).isEmpty()) {
                nbttagcompound = NBTTagCompound()
                nbttagcompound.putByte("Slot", i.toByte())
                this.stacks.get(i).writeToNBT(nbttagcompound)
                tagList.add(nbttagcompound)
            }
        }

        return tagList
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun setChanged() {
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return !this.slimeling.isDead && par1EntityPlayer.getDistanceSq(this.slimeling) <= 64.0
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun canPlaceItem(i: Int, itemstack: ItemStack): Boolean {
        return false
    }
}
