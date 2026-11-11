package micdoodle8.mods.galacticraft.planets.mars.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Rarity

open class ItemSchematicTier2 : ItemSchematic(
    FoundationItemProperties.named("galacticraftplanets", "schematic").stacksTo(1).rarity(Rarity.RARE),
    listOf(Definition("galacticraftplanets:item/schematic_rocket_t3", "schematic.rocket_t3.name"),
        Definition("galacticraftplanets:item/schematic_rocket_cargo", "schematic.cargo_rocket.name"),
        Definition("galacticraftplanets:item/schematic_astro_miner", "schematic.astro_miner.name"))
) {
    override fun getIndex(metadata: Int): Int = metadata + indexOffset
    companion object {
        private var indexOffset = 0
        @JvmStatic fun registerSchematicItems() { indexOffset = registerFamily("galacticraftplanets:schematic", 3) }
        @JvmStatic fun registerTextures() {
            for (name in listOf("schematic_rocket_t3", "schematic_rocket_cargo", "schematic_astro_miner")) {
                SchematicRegistry.registerTexture(Identifier.parse("galacticraftplanets:textures/items/$name.png"))
            }
        }
    }
}
