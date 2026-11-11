package foundation.item

import foundation.persistence.containsTag
import foundation.level.FoundationDimensionKeys
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import net.minecraft.core.component.DataComponents
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import java.util.UUID

object FoundationFrequencyModules {
    data class TelemetryTarget(val dimension: ResourceKey<Level>, val position: BlockPos)
    private const val MOST = "linkedUUIDMost"
    private const val LEAST = "linkedUUIDLeast"

    fun isModule(stack: ItemStack): Boolean = !stack.isEmpty && stack.item is ItemBasic &&
        FoundationItemVariants.metadata(stack) == ItemBasic.FREQUENCY_MODULE

    fun canEquip(stack: ItemStack): Boolean = isModule(stack) &&
        (stack.get(DataComponents.CUSTOM_DATA)?.copyTag()?.isEmpty ?: true)

    fun link(stack: ItemStack, entity: UUID): Boolean {
        if (!isModule(stack)) return false
        val data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
        data.putLong(MOST, entity.mostSignificantBits)
        data.putLong(LEAST, entity.leastSignificantBits)
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data))
        return true
    }

    fun linkedEntity(stack: ItemStack): UUID? {
        if (!isModule(stack)) return null
        val data = stack.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return null
        if (!data.containsTag(MOST, 99) || !data.containsTag(LEAST, 99)) return null
        return UUID(data.getLongOr(MOST, 0L), data.getLongOr(LEAST, 0L))
    }

    fun hasTelemetry(stack: ItemStack): Boolean {
        if (!isModule(stack)) return false
        val data = stack.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return false
        return data.containsTag("teDimension", 8) || data.containsTag("teDim", 99)
    }

    fun bindTelemetry(stack: ItemStack, dimension: ResourceKey<Level>, position: BlockPos): Boolean {
        if (!isModule(stack)) return false
        val data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
        data.putString("teDimension", dimension.identifier().toString())
        data.remove("teDim")
        data.putInt("teCoordX", position.x)
        data.putInt("teCoordY", position.y)
        data.putInt("teCoordZ", position.z)
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data))
        return true
    }

    fun telemetry(stack: ItemStack): TelemetryTarget? {
        if (!hasTelemetry(stack)) return null
        val data = requireNotNull(stack.get(DataComponents.CUSTOM_DATA)).copyTag()
        if (!listOf("teCoordX", "teCoordY", "teCoordZ").all { data.containsTag(it, 99) }) return null
        val dimension = if (data.containsTag("teDimension", 8)) FoundationDimensionKeys.parse(data.getStringOr("teDimension", ""))
            else FoundationDimensionKeys.fromLegacyId(data.getIntOr("teDim", 0))
        return TelemetryTarget(dimension, BlockPos(data.getIntOr("teCoordX", 0), data.getIntOr("teCoordY", 0), data.getIntOr("teCoordZ", 0)))
    }
}
