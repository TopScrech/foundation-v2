package foundation.schematic

import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent

object FoundationSchematicEntities {
    @JvmField val ID: Identifier = Identifier.parse("galacticraftcore:hanging_schematic")
    @JvmField val TYPE: EntityType<EntityHangingSchematic> = EntityType.Builder.of(
        { type, level -> EntityHangingSchematic(type, level) }, MobCategory.MISC
    ).sized(2f, 2f).clientTrackingRange(10).updateInterval(5)
        .build(ResourceKey.create(Registries.ENTITY_TYPE, ID))

    fun install(context: FMLJavaModLoadingContext) {
        RegisterEvent.getBus(context.modBusGroup).addListener { event ->
            event.register(Registries.ENTITY_TYPE, ID) { TYPE }
        }
        foundation.bootstrap.FoundationBootstrap.installClient(context, "foundation.schematic.FoundationSchematicClient")
    }
}
