package foundation.verification

import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import foundation.persistence.FoundationItemStackPersistence
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments

private fun legacyItem(name: String, count: Int = 1, damage: Int = 0): CompoundTag = CompoundTag().also {
    it.putString("id", name)
    it.putByte("Count", count.toByte())
    it.putShort("Damage", damage.toShort())
}

private fun slot(item: CompoundTag, slot: Int): CompoundTag = item.copy().also { it.putByte("Slot", slot.toByte()) }

fun verifyInventoryPersistence(registries: HolderLookup.Provider) {
    val persistence = FoundationItemStackPersistence
    val original = legacyItem("galacticraftcore:item_basic_moon", 7, 2)
    val custom = CompoundTag().also { it.putLong("linkedUUIDMost", 12345L); it.putString("module", "kept") }
    val capabilities = CompoundTag().also { it.put("galacticraftcore:energy", CompoundTag().also { it.putInt("stored", 4200) }) }
    original.put("tag", custom.copy())
    original.put("ForgeCaps", capabilities.copy())
    val before = original.copy()
    val migrated = persistence.read(original, registries)
    check(original == before)
    check(migrated.count == 7 && FoundationItemVariants.metadata(migrated) == 2)
    check(migrated.damageValue == 0)
    check(migrated.get(DataComponents.CUSTOM_DATA)?.copyTag() == custom)
    check(migrated.get(FoundationItemComponents.LEGACY_CAPABILITIES)?.copyTag() == capabilities)
    val encoded = persistence.write(migrated, registries)
    val restored = persistence.read(encoded, registries)
    check(ItemStack.isSameItemSameComponents(migrated, restored) && restored.count == 7)
    encoded.getCompoundOrEmpty("components").getCompoundOrEmpty("foundation:legacy_capabilities").putInt("changed", 1)
    check(migrated.get(FoundationItemComponents.LEGACY_CAPABILITIES)?.copyTag() == capabilities)

    val packet = RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY))
    try {
        ItemStack.STREAM_CODEC.encode(packet, migrated)
        check(ItemStack.isSameItemSameComponents(migrated, ItemStack.STREAM_CODEC.decode(packet)))
    } finally { packet.release() }

    val wool = persistence.read(legacyItem("minecraft:wool", 12, 14), registries)
    check(BuiltInRegistries.ITEM.getKey(wool.item).toString() == "minecraft:red_wool" && wool.count == 12)
    val pickaxe = persistence.read(legacyItem("minecraft:diamond_pickaxe", 1, 42), registries)
    check(pickaxe.damageValue == 42)
    val tool = persistence.read(legacyItem("foundation_test:damageable", 1, 37), registries)
    check(tool.damageValue == 37 && tool.get(FoundationItemComponents.LEGACY_METADATA) == null)
    val numeric = legacyItem("minecraft:planks", 3, 4).also { it.putShort("id", 5) }
    val planks = persistence.read(numeric, registries)
    check(BuiltInRegistries.ITEM.getKey(planks.item).toString() == "minecraft:acacia_planks" && planks.count == 3)
    val enchanted = legacyItem("minecraft:iron_sword", 1, 12).also { tag ->
        tag.put("tag", CompoundTag().also {
            it.put("ench", ListTag().also { list -> list.add(CompoundTag().also { e -> e.putShort("id", 16); e.putShort("lvl", 5) }) })
        })
    }
    val sword = persistence.read(enchanted, registries)
    val sharpness = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS)
    check(sword.damageValue == 12 && sword.get(DataComponents.ENCHANTMENTS)?.getLevel(sharpness) == 5)
    check(runCatching { persistence.read(legacyItem("foundation_test:unported_variant", 1, 3), registries) }.isFailure)
    check(runCatching { persistence.read(legacyItem("missing:gear"), registries) }.isFailure)
    check(runCatching { persistence.read(numeric.copy().also { it.putShort("id", 30000) }, registries) }.isFailure)
    check(persistence.read(CompoundTag(), registries).isEmpty)
    check(persistence.write(ItemStack.EMPTY, registries).isEmpty)

    val inventory = InventoryExtended()
    check(inventory.containerSize == 11 && inventory.isEmpty)
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    inventory.setItem(0, FoundationItemVariants.stack(moon, 80, 2))
    check(inventory.getItem(0).count == 64)
    val removed = inventory.removeItem(0, 5)
    check(removed.count == 5 && FoundationItemVariants.metadata(removed) == 2)
    check(inventory.getItem(0).count == 59)
    inventory.setItem(10, migrated.copy())
    val saved = inventory.writeToNBT(ListTag(), registries)
    val loaded = InventoryExtended()
    loaded.readFromNBT(saved, registries)
    check(loaded.getItem(0).count == 59 && loaded.getItem(10).count == 7)
    check(FoundationItemVariants.metadata(loaded.getItem(10)) == 2)
    check(loaded.getItem(10).get(FoundationItemComponents.LEGACY_CAPABILITIES)?.copyTag() == capabilities)
    val copied = InventoryExtended()
    copied.copyInventory(loaded)
    FoundationItemVariants.apply(copied.getItem(10), 1)
    check(FoundationItemVariants.metadata(loaded.getItem(10)) == 2)
    check(copied.removeItemNoUpdate(0).count == 59 && copied.getItem(0).isEmpty)

    val bad = ListTag().also {
        it.add(slot(legacyItem("minecraft:stone"), 0))
        it.add(slot(legacyItem("missing:gear"), 1))
    }
    val existing = loaded.writeToNBT(ListTag(), registries)
    check(runCatching { loaded.readFromNBT(bad, registries) }.isFailure)
    check(loaded.getItem(0).count == 59 && loaded.getItem(10).count == 7)
    check(runCatching { loaded.writeToNBT(ListTag(), registries) }.isFailure)
    loaded.readFromNBT(existing, registries)
    check(loaded.writeToNBT(ListTag(), registries) == existing)
    val withUnknown = saved.copy().also { it.add(slot(legacyItem("missing:future_equipment", 1, 3), 99)) }
    loaded.readFromNBT(withUnknown, registries)
    check(loaded.writeToNBT(ListTag(), registries) == withUnknown)
    val old = ListTag().also {
        it.add(slot(legacyItem("minecraft:stone"), 0))
        it.add(slot(legacyItem("galacticraftcore:item_basic_moon", 2, 2), 200))
        it.add(slot(legacyItem("galacticraftcore:item_basic_moon", 4, 1), 209))
    }
    val legacyInventory = InventoryExtended()
    legacyInventory.readFromNBTOld(old, registries)
    check(legacyInventory.getItem(0).count == 2 && legacyInventory.getItem(9).count == 4)
    check(legacyInventory.getItem(10).isEmpty)
    check(legacyInventory.writeToNBT(ListTag(), registries).size == 2)
    loaded.clearContent()
    check(loaded.isEmpty && loaded.containerSize == 11 && loaded.writeToNBT(ListTag(), registries).isEmpty())
    println("Passed modern and legacy equipment inventory, variant, capability, durability, and enchantment persistence")
}
