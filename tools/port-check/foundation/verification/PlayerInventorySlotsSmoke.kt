package foundation.verification

import foundation.inventory.FoundationInventorySlots
import foundation.inventory.FoundationPlayerInventories
import foundation.item.FoundationItemVariants
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

fun verifyPlayerInventorySlots() {
    val storage = SimpleContainer(43)
    val main = FoundationPlayerInventories.main(storage)
    val armor = FoundationPlayerInventories.armor(storage)
    val offhand = FoundationPlayerInventories.offhand(storage)
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    check(main.size == 36 && armor.size == 4 && offhand.size == 1)
    storage.setItem(41, ItemStack(Items.DIAMOND))
    storage.setItem(42, ItemStack(Items.EMERALD))
    main[35] = FoundationItemVariants.stack(moon, 7, 2)
    check(storage.getItem(35).count == 7 && FoundationItemVariants.metadata(storage.getItem(35)) == 2)
    val previous = main.set(35, ItemStack(Items.STONE))
    check(previous.count == 7 && FoundationItemVariants.metadata(previous) == 2)
    for (slot in armor.indices) {
        armor[slot] = FoundationItemVariants.stack(moon, slot + 1, slot % 3)
        check(storage.getItem(36 + slot) === armor[slot])
    }
    offhand[0] = previous
    check(storage.getItem(40) === previous)
    main.clear()
    check(main.size == 36 && main.all { it.isEmpty } && armor.all { !it.isEmpty })
    check(!offhand[0].isEmpty && !storage.getItem(41).isEmpty && !storage.getItem(42).isEmpty)
    armor.clear()
    check(armor.size == 4 && armor.all { it.isEmpty } && !offhand[0].isEmpty)
    offhand.clear()
    check(offhand.size == 1 && offhand[0].isEmpty)
    check(storage.getItem(41).item === Items.DIAMOND && storage.getItem(42).item === Items.EMERALD)
    check(runCatching { armor[4] }.isFailure && runCatching { armor[-1] }.isFailure)
    check(runCatching { main.add(ItemStack(Items.STONE)) }.isFailure)
    check(runCatching { main.removeAt(0) }.isFailure)
    check(runCatching { FoundationInventorySlots(storage, intArrayOf(0, 0)) }.isFailure)
    check(runCatching { FoundationInventorySlots(storage, intArrayOf(43)) }.isFailure)
    val slots = intArrayOf(0)
    val view = FoundationInventorySlots(storage, slots)
    slots[0] = 42
    view[0] = previous
    check(storage.getItem(0) === previous && storage.getItem(42).item === Items.EMERALD)
    println("Passed native player slot view mappings, fixed sizes, writes, clearing boundaries, and component preservation")
}
