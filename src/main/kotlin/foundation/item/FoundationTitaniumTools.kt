package foundation.item

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ToolMaterial
import net.minecraft.world.item.component.BlockTransformers
import net.minecraft.world.level.block.Block

object FoundationTitaniumTools {
    @JvmField val INCORRECT_BLOCKS: TagKey<Block> = TagKey.create(Registries.BLOCK, Identifier.parse("galacticraftplanets:incorrect_for_titanium_tool"))
    @JvmField val REPAIR_MATERIALS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.parse("galacticraftplanets:titanium_tool_materials"))
    @JvmField val BASE_MATERIAL = ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 760, 14f, 4f, 16, REPAIR_MATERIALS)
    // The original initialization raised the pickaxe, axe, and shovel from material tier four to tier five
    @JvmField val HARVEST_MATERIAL = ToolMaterial(INCORRECT_BLOCKS, 760, 14f, 4f, 16, REPAIR_MATERIALS)
    // The original titanium axe used the steel material before its separate harvest-tier override
    @JvmField val AXE_MATERIAL = ToolMaterial(INCORRECT_BLOCKS, 768, 5f, 2f, 8, FoundationSteelTools.REPAIR_MATERIALS)

    fun axe(properties: Item.Properties): Item.Properties = properties.tool(AXE_MATERIAL, FoundationSteelTools.AXE_FAST_BLOCKS, 4f, -3f, 5f)
        .delayedComponent(DataComponents.BLOCK_TRANSFORMER) { it.getOrThrow(BlockTransformers.AXE) }
}
