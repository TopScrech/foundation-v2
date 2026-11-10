package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation

open class InventoryTreasureChest constructor(name: String, upper: IInventory?, lower: IInventory?) : IInventory {
    private val name: String
    private val upperChest: IInventory?
    private val lowerChest: IInventory?

    init {
        var upper: IInventory? = upper
        var lower: IInventory? = lower
        this.name = name

        if (upper == null) {
            upper = lower
        }

        if (lower == null) {
            lower = upper
        }

        this.upperChest = upper
        this.lowerChest = lower
    }

    override fun getSizeInventory(): Int {
        return this.upperChest!!.getSizeInventory() + this.lowerChest!!.getSizeInventory()
    }


    override fun getName(): String {
        return if (this.upperChest!!.hasCustomName()) this.upperChest.getName() else (if (this.lowerChest!!.hasCustomName()) this.lowerChest.getName() else this.name)
    }

    override fun hasCustomName(): Boolean {
        return this.upperChest!!.hasCustomName() || this.lowerChest!!.hasCustomName()
    }

    override fun getStackInSlot(p_70301_1_: Int): ItemStack {
        return if (p_70301_1_ >= this.upperChest!!.getSizeInventory()) this.lowerChest!!.getStackInSlot(p_70301_1_ - this.upperChest.getSizeInventory()) else this.upperChest.getStackInSlot(
            p_70301_1_
        )
    }

    override fun decrStackSize(p_70298_1_: Int, p_70298_2_: Int): ItemStack {
        return if (p_70298_1_ >= this.upperChest!!.getSizeInventory()) this.lowerChest!!.decrStackSize(
            p_70298_1_ - this.upperChest.getSizeInventory(),
            p_70298_2_
        ) else this.upperChest.decrStackSize(p_70298_1_, p_70298_2_)
    }

    override fun removeStackFromSlot(p_70304_1_: Int): ItemStack {
        return if (p_70304_1_ >= this.upperChest!!.getSizeInventory()) this.lowerChest!!.removeStackFromSlot(p_70304_1_ - this.upperChest.getSizeInventory()) else this.upperChest.removeStackFromSlot(
            p_70304_1_
        )
    }

    override fun setInventorySlotContents(p_70299_1_: Int, p_70299_2_: ItemStack) {
        if (p_70299_1_ >= this.upperChest!!.getSizeInventory()) {
            this.lowerChest!!.setInventorySlotContents(p_70299_1_ - this.upperChest.getSizeInventory(), p_70299_2_)
        } else {
            this.upperChest.setInventorySlotContents(p_70299_1_, p_70299_2_)
        }
    }

    override fun getInventoryStackLimit(): Int {
        return this.upperChest!!.getInventoryStackLimit()
    }

    override fun markDirty() {
        this.upperChest!!.markDirty()
        this.lowerChest!!.markDirty()
    }

    override fun isUsableByPlayer(p_70300_1_: EntityPlayer): Boolean {
        return this.upperChest!!.isUsableByPlayer(p_70300_1_) && this.lowerChest!!.isUsableByPlayer(p_70300_1_)
    }

    override fun openInventory(player: EntityPlayer) {
        this.upperChest!!.openInventory(player)
        this.lowerChest!!.openInventory(player)
    }

    override fun closeInventory(player: EntityPlayer) {
        this.upperChest!!.closeInventory(player)
        this.lowerChest!!.closeInventory(player)
    }

    override fun isItemValidForSlot(p_94041_1_: Int, p_94041_2_: ItemStack): Boolean {
        return true
    }

    override fun clear() {
        this.upperChest!!.clear()
        this.lowerChest!!.clear()
    }

    override fun isEmpty(): Boolean {
        return this.upperChest!!.isEmpty() && this.lowerChest!!.isEmpty()
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {
    }

    override fun getFieldCount(): Int {
        return 0
    }

    override fun getDisplayName(): ITextComponent {
        return if (this.hasCustomName()) TextComponentString(this.getName()) else TextComponentTranslation(
            this.getName(),
            *arrayOfNulls<Any>(0)
        )
    }
}
