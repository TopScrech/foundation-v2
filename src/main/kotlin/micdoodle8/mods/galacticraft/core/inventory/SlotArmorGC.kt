package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class SlotArmorGC constructor(
    @JvmField val thePlayer: EntityPlayer,
    par2IInventory: IInventory,
    par3: Int,
    par4: Int,
    par5: Int,
    @JvmField val armorType: Int
) : Slot(par2IInventory, par3, par4, par5) {
    override fun getSlotStackLimit(): Int {
        return 1
    }

    override fun isItemValid(par1ItemStack: ItemStack): Boolean {
        val item: Item? = if (par1ItemStack.isEmpty()) null else par1ItemStack.getItem()
        return item != null && item.isValidArmor(par1ItemStack, VALID_EQUIPMENT_SLOTS[this.armorType], this.thePlayer)
    }

    @SideOnly(Side.CLIENT)
    override fun getSlotTexture(): String? {
        return ItemArmor.EMPTY_SLOT_NAMES[VALID_EQUIPMENT_SLOTS[this.armorType]!!.getIndex()]
    }

    companion object {
        private val VALID_EQUIPMENT_SLOTS: Array<EntityEquipmentSlot?> = ((arrayOf<EntityEquipmentSlot>(
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.FEET
        ) as Array<EntityEquipmentSlot?>?))!!
    }
}
