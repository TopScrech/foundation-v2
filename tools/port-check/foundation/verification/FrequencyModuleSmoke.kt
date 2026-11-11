package foundation.verification

import foundation.inventory.FoundationEquipment
import foundation.item.FoundationFrequencyModules
import foundation.item.FoundationItemVariants
import foundation.level.FoundationDimensionKeys
import foundation.persistence.FoundationItemStackPersistence
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import io.netty.buffer.Unpooled
import java.util.UUID

fun verifyFrequencyModules(lookup: HolderLookup.Provider) {
    val item = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:basic_item")))
    val module = FoundationItemVariants.stack(item, 3, 19)
    check(FoundationFrequencyModules.canEquip(module) && FoundationFrequencyModules.linkedEntity(module) == null)
    val inventory = InventoryExtended()
    check(FoundationEquipment.equipOne(inventory, 5, module))
    check(module.count == 2 && inventory.getItem(5).count == 1)
    val entity = UUID.fromString("01234567-89ab-cdef-fedc-ba9876543210")
    val telemetry = FoundationDimensionKeys.parse("galacticraftplanets:moon")
    val position = BlockPos(-37, -12, 194)
    check(FoundationFrequencyModules.bindTelemetry(module, telemetry, position))
    check(FoundationFrequencyModules.link(module, entity))
    check(FoundationFrequencyModules.linkedEntity(module) == entity && !FoundationFrequencyModules.canEquip(module))
    check(FoundationFrequencyModules.telemetry(module) == FoundationFrequencyModules.TelemetryTarget(telemetry, position))
    val copied = module.copy()
    check(FoundationFrequencyModules.link(copied, UUID(7L, 11L)))
    check(FoundationFrequencyModules.linkedEntity(module) == entity)
    val reloaded = FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(module, lookup), lookup)
    check(FoundationFrequencyModules.linkedEntity(reloaded) == entity)
    check(FoundationFrequencyModules.telemetry(reloaded)?.position == position)
    val old = CompoundTag().also {
        it.putString("id", "galacticraftcore:basic_item")
        it.putByte("Count", 1)
        it.putShort("Damage", 19)
        it.put("tag", CompoundTag().also { data ->
            data.putLong("linkedUUIDMost", entity.mostSignificantBits)
            data.putLong("linkedUUIDLeast", entity.leastSignificantBits)
            data.putInt("teDim", 0)
            data.putInt("teCoordX", position.x)
            data.putInt("teCoordY", position.y)
            data.putInt("teCoordZ", position.z)
            data.putInt("preserved", 42)
        })
    }
    val imported = FoundationItemStackPersistence.read(old, lookup)
    check(FoundationFrequencyModules.linkedEntity(imported) == entity)
    check(FoundationFrequencyModules.telemetry(imported)?.dimension == FoundationDimensionKeys.OVERWORLD)
    check(FoundationFrequencyModules.bindTelemetry(imported, telemetry, position))
    val data = requireNotNull(imported.get(DataComponents.CUSTOM_DATA)).copyTag()
    check(!data.contains("teDim") && data.getIntOr("preserved", 0) == 42)
    check(FoundationFrequencyModules.linkedEntity(imported) == entity)
    val malformed = module.copy().also {
        it.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().also { tag -> tag.putLong("linkedUUIDMost", 1L) }))
    }
    check(FoundationFrequencyModules.linkedEntity(malformed) == null)
    val unresolved = imported.copy().also {
        val unknown = requireNotNull(it.get(DataComponents.CUSTOM_DATA)).copyTag()
        unknown.remove("teDimension")
        unknown.putInt("teDim", Int.MAX_VALUE)
        it.set(DataComponents.CUSTOM_DATA, CustomData.of(unknown))
    }
    val before = requireNotNull(unresolved.get(DataComponents.CUSTOM_DATA)).copyTag()
    check(runCatching { FoundationFrequencyModules.telemetry(unresolved) }.isFailure)
    check(requireNotNull(unresolved.get(DataComponents.CUSTOM_DATA)).copyTag() == before)
    check(!FoundationFrequencyModules.link(ItemStack(Items.STONE), entity))
    check(!FoundationFrequencyModules.bindTelemetry(FoundationItemVariants.stack(item, 1, 0), telemetry, position))
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val wire = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
    try {
        ItemStack.STREAM_CODEC.encode(wire, imported)
        val received = ItemStack.STREAM_CODEC.decode(wire)
        check(FoundationFrequencyModules.linkedEntity(received) == entity)
        check(FoundationFrequencyModules.telemetry(received)?.dimension == telemetry)
    } finally { wire.release() }
    println("Passed frequency module entity and telemetry links, legacy dimensions, equipment transfer, copy isolation, native persistence, and packets")
}
