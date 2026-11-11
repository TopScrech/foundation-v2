package foundation.item

import foundation.inventory.FoundationEquipment
import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC
import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC
import micdoodle8.mods.galacticraft.core.items.IClickableItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

/** Shared single-item transfer for equipment and emergency-kit contents */
interface FoundationEquipmentItem : IClickableItem {
    val equipmentSlots: List<Int>

    fun equip(inventory: IInventoryGC, stack: ItemStack): Boolean {
        if (stack.isEmpty || stack.item !== this) return false
        if (this is FoundationVariantItem && variant(FoundationItemVariants.metadata(stack)) == null) return false
        return equipmentSlots.any { FoundationEquipment.equipOne(inventory, it, stack) }
    }

    fun useEquipment(player: Player, hand: InteractionHand): InteractionResult {
        if (player !is ServerPlayer) return InteractionResult.PASS
        val inventory = AccessInventoryGC.getGCInventoryForPlayer(player) ?: return InteractionResult.PASS
        return if (equip(inventory, player.getItemInHand(hand))) InteractionResult.SUCCESS else InteractionResult.PASS
    }

    override fun onItemRightClick(itemStack: ItemStack?, worldIn: Level?, player: Player?): ItemStack? {
        if (itemStack == null || player !is ServerPlayer) return itemStack
        AccessInventoryGC.getGCInventoryForPlayer(player)?.let { equip(it, itemStack) }
        return itemStack
    }
}
