package foundation.verification

import foundation.inventory.FoundationEquipment
import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

fun verifyEquipmentTransfer() {
    val item = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftplanets:basic_item_venus")))
    val inventory = InventoryExtended()
    val held = FoundationItemVariants.stack(item, 3, 0)
    held.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().also { it.putLong("owner", 42L) }))
    held.set(FoundationItemComponents.LEGACY_CAPABILITIES, CustomData.of(CompoundTag().also { it.putInt("energy", 17) }))
    val original = held.copy()
    check(FoundationEquipment.equipOne(inventory, 10, held))
    check(held.count == 2 && inventory.getItem(10).count == 1)
    check(ItemStack.isSameItemSameComponents(original, inventory.getItem(10)))
    check(inventory.getItem(10) !== held)
    check(!FoundationEquipment.equipOne(inventory, 10, held) && held.count == 2)
    check(!FoundationEquipment.equipOne(inventory, -1, held))
    check(!FoundationEquipment.equipOne(inventory, 11, held))
    check(!FoundationEquipment.equipOne(inventory, 0, ItemStack.EMPTY))
    inventory.clearContent()
    val last = original.copyWithCount(1)
    check(FoundationEquipment.equipOne(inventory, 10, last))
    check(last.isEmpty && inventory.getItem(10).count == 1)
    println("Passed single-item extended equipment transfer, retained held items, occupied-slot protection, and component preservation")
}
