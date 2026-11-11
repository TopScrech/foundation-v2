package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.FoundationItemProperties
import galaxyspace.core.configs.GSConfigCore
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import net.minecraft.resources.Identifier
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext

open class ItemSchematics : ItemSchematic(
    FoundationItemProperties.named("galaxyspace", "schematics").stacksTo(1).rarity(Rarity.RARE),
    models.zip(tooltips).map { (model, tooltip) -> Definition("galaxyspace:item/schematics/$model", tooltip) }, "item.schematics.name"
) {
    override fun creativeMetadata(): List<Int> = ((if (GSConfigCore.enableAdvancedRocketCraft) 0 else 5) until 7).toList()
    // GalaxySpace intentionally prohibited placing these schematics on walls
    override fun useOn(context: UseOnContext): InteractionResult = InteractionResult.FAIL
    companion object {
        @JvmField val models = listOf("schematic_cone", "schematic_body", "schematic_engine", "schematic_booster", "schematic_stabiliser", "schematic_oxygen_tank", "schematic_port_nuclear_reactor")
        @JvmField val tooltips = listOf("item.rocket_cone.name", "item.rocket_body.name", "item.rocket_engine.name", "item.rocket_booster.name", "item.rocket_stabiliser.name", "item.oxygen_tank_epp_1.name", "tile.port_nuclear_reactor.name")
        @JvmStatic fun registerSchematicItems() { registerFamily("galaxyspace:schematics", 7) }
        @JvmStatic fun registerTextures() {
            for (name in models) SchematicRegistry.registerTexture(Identifier.parse("galaxyspace:textures/items/schematics/$name.png"))
        }
    }
}
