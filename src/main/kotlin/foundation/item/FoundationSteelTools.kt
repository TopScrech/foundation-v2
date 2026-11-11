package foundation.item

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.component.BlockTransformers
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.Tool
import net.minecraft.world.item.component.Weapon
import net.minecraft.world.item.Item
import net.minecraft.world.item.ToolMaterial
import net.minecraft.world.level.block.Block

/** Steel retained its original durability, speed, damage, enchantability, and configured harvest tier four */
object FoundationSteelTools {
    @JvmField val INCORRECT_BLOCKS: TagKey<Block> = TagKey.create(Registries.BLOCK, Identifier.parse("galacticraftcore:incorrect_for_steel_tool"))
    @JvmField val REPAIR_MATERIALS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.parse("galacticraftcore:steel_tool_materials"))
    @JvmField val AXE_FAST_BLOCKS: TagKey<Block> = TagKey.create(Registries.BLOCK, Identifier.parse("galacticraftcore:steel_axe_fast_blocks"))

    fun axe(properties: Item.Properties): Item.Properties = properties.tool(MATERIAL, AXE_FAST_BLOCKS, 4f, -3f, 5f)
        .delayedComponent(DataComponents.BLOCK_TRANSFORMER) { it.getOrThrow(BlockTransformers.AXE) }

    /** The old hoe had no enchanting-table value and used one durability per entity hit */
    fun hoe(properties: Item.Properties, material: ToolMaterial = MATERIAL): Item.Properties {
        val blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK)
        return properties.durability(material.durability()).repairable(material.repairItems())
            .component(DataComponents.TOOL, Tool(listOf(
                Tool.Rule.deniesDrops(blocks.getOrThrow(material.incorrectBlocksForDrops())),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_HOE), material.speed())
            ), 1f, 1, true))
            .component(DataComponents.WEAPON, Weapon(1, 0f))
            .attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (material.attackDamageBonus() - 3f).toDouble(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build())
            .delayedComponent(DataComponents.BLOCK_TRANSFORMER) { it.getOrThrow(BlockTransformers.HOE) }
    }

    @JvmField val MATERIAL = ToolMaterial(INCORRECT_BLOCKS, 768, 5f, 2f, 8, REPAIR_MATERIALS)
}
