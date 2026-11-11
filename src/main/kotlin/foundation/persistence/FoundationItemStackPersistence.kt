package foundation.persistence

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import net.minecraft.core.component.DataComponents
import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantItem
import net.minecraft.SharedConstants
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
import net.minecraft.util.datafix.DataFixers
import net.minecraft.util.datafix.fixes.ItemIdFix
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

/** Registry-aware stacks, including the metadata and capability data used by old mod items */
object FoundationItemStackPersistence {
    fun read(tag: CompoundTag, registries: HolderLookup.Provider): ItemStack {
        if (tag.isEmpty) return ItemStack.EMPTY
        val legacy = tag.contains("Count") || tag.contains("Damage") || tag.contains("tag")
        val oldCount = if (legacy) tag.getByteOr("Count", 0).toInt() else tag.getIntOr("count", 1)
        if (oldCount <= 0) return ItemStack.EMPTY
        val oldName = tag.getString("id").orElse(null)
            ?: if (tag.containsTag("id", 99)) ItemIdFix.getItem(tag.getIntOr("id", 0)) else null
        requireNotNull(oldName) { "Saved item has no identifier" }
        if (oldName == "minecraft:air") {
            check(tag.getIntOr("id", 0) == 0 || tag.getStringOr("id", "") == "minecraft:air") { "Unknown numeric legacy item ID" }
            return ItemStack.EMPTY
        }
        val currentVersion = SharedConstants.getCurrentVersion().dataVersion().version()
        val sourceVersion = tag.getIntOr("DataVersion", if (legacy) {
            if (tag.containsTag("id", 99)) 99 else 1343
        } else currentVersion)
        val saved = tag.copy()
        val damage = if (legacy) tag.getShortOr("Damage", 0).toInt().coerceAtLeast(0) else 0
        val upgraded = if (sourceVersion < currentVersion) DataFixers.getDataFixer().update(
            References.ITEM_STACK, com.mojang.serialization.Dynamic(NbtOps.INSTANCE, saved), sourceVersion, currentVersion
        ).value else saved
        val ops = RegistryOps.create(NbtOps.INSTANCE, registries)
        val stack = ItemStack.OPTIONAL_CODEC.parse(ops, upgraded).getOrThrow()
        check(!stack.isEmpty) { "Saved item $oldName could not be migrated" }
        if (legacy && !oldName.startsWith("minecraft:")) {
            when {
                stack.item is ItemElectricBase -> {
                    stack.remove(DataComponents.MAX_DAMAGE)
                    stack.remove(DataComponents.DAMAGE)
                    (stack.item as ItemElectricBase).importLegacy(stack, damage, tag.getCompound("tag").orElse(null))
                }
                stack.item is ItemOxygenTank -> {
                    require(damage <= ItemOxygenTank.capacityOf(stack)) { "Invalid saved oxygen tank usage $damage" }
                    stack.remove(DataComponents.MAX_DAMAGE)
                    stack.remove(DataComponents.DAMAGE)
                    ItemOxygenTank.setUsed(stack, damage)
                }
                stack.item is FoundationVariantItem -> FoundationItemVariants.apply(stack, damage)
                stack.isDamageableItem -> stack.damageValue = damage
                else -> check(damage == 0) { "Item $oldName needs an explicit migration for metadata $damage" }
            }
        }
        if (stack.item is ItemOxygenTank) {
            require(ItemOxygenTank.used(stack) <= ItemOxygenTank.capacityOf(stack)) { "Saved oxygen usage exceeds tank capacity" }
            require(!stack.has(DataComponents.MAX_DAMAGE) && !stack.has(DataComponents.DAMAGE)) { "Saved oxygen tank used obsolete tool damage components" }
        }
        tag.getCompound("ForgeCaps").ifPresent { caps ->
            val existing = stack.get(FoundationItemComponents.LEGACY_CAPABILITIES)
            check(existing == null || existing.copyTag() == caps) { "Conflicting saved item capability data" }
            stack.set(FoundationItemComponents.LEGACY_CAPABILITIES, CustomData.of(caps.copy()))
        }
        return stack
    }

    fun write(stack: ItemStack, registries: HolderLookup.Provider): CompoundTag {
        if (stack.isEmpty) return CompoundTag()
        val encoded = ItemStack.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries), stack).getOrThrow()
        require(encoded is CompoundTag)
        val result = encoded.copy()
        result.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version())
        return result
    }
}
