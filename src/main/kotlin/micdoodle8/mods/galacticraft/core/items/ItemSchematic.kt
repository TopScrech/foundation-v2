package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.api.recipe.ISchematicItem
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.context.UseOnContext
import java.util.function.Consumer

open class ItemSchematic(
    properties: Item.Properties,
    protected val definitions: List<Definition>,
    private val translation: String = "item.schematic.name"
) : FoundationVariantMaterial(properties), ISchematicItem, ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).stacksTo(1).rarity(Rarity.RARE),
        listOf(Definition("galacticraftcore:item/schematic", "schematic.moonbuggy.name"), Definition("galacticraftcore:item/schematic_rocket_t2", "schematic.rocket_t2.name"))
    )
    data class Definition(val model: String, val tooltip: String)
    override fun variant(metadata: Int): FoundationItemVariant? = definitions.getOrNull(metadata)?.let {
        FoundationItemVariant(translation, Identifier.parse(it.model), ChatFormatting.BLUE)
    }
    override fun creativeMetadata(): List<Int> = definitions.indices.toList()
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.SCHEMATIC
    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        definitions.getOrNull(FoundationItemVariants.metadata(stack))?.let { builder.accept(Component.translatable(it.tooltip)) }
    }
    protected open fun getIndex(metadata: Int): Int = metadata
    fun hangingIndex(metadata: Int): Int = getIndex(metadata)
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.FAIL
        val face = context.clickedFace
        val stack = context.itemInHand
        val target = context.clickedPos.relative(face)
        if (!face.axis.isHorizontal || !player.mayUseItemAt(target, face, stack)) return InteractionResult.FAIL
        val metadata = FoundationItemVariants.metadata(stack)
        if (variant(metadata) == null) return InteractionResult.FAIL
        val level = context.level
        val hanging = EntityHangingSchematic(level, target, face, getIndex(metadata))
        if (hanging.survives()) {
            if (level is ServerLevel) {
                if (level.addFreshEntity(hanging)) { hanging.playPlacementSound(); stack.shrink(1) }
            } else stack.shrink(1)
        }
        return InteractionResult.SUCCESS
    }
    companion object {
        @JvmStatic fun registerSchematicItems() {
            registerFamily("galacticraftcore:schematic", 2)
        }
        fun registerFamily(id: String, count: Int): Int {
            val item = FoundationItemProperties.registered(Identifier.parse(id))
            var first = -1
            for (metadata in 0 until count) {
                val index = SchematicRegistry.registerSchematicItem(FoundationItemVariants.stack(item, 1, metadata))
                if (first < 0) first = index
            }
            return first
        }
        @JvmStatic fun registerTextures() {
            for (name in listOf("schematic_buggy", "schematic_rocket_t2")) {
                SchematicRegistry.registerTexture(Identifier.parse("galacticraftcore:textures/items/$name.png"))
            }
        }
    }
}
