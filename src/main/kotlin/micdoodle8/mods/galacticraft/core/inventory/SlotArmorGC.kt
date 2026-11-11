package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class SlotArmorGC constructor(
    @JvmField val thePlayer: EntityPlayer,
    par2IInventory: IInventory,
    par3: Int,
    par4: Int,
    par5: Int,
    @JvmField val armorType: Int
) : Slot(par2IInventory, par3, par4, par5) {
    override fun getMaxStackSize(): Int {
        return 1
    }

    override fun mayPlace(par1ItemStack: ItemStack): Boolean {
        val item: Item? = if (par1ItemStack.isEmpty()) null else par1ItemStack.getItem()
        return item != null && item.isValidArmor(par1ItemStack, VALID_EQUIPMENT_SLOTS[this.armorType], this.thePlayer)
    }

    @OnlyIn(Dist.CLIENT)
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
