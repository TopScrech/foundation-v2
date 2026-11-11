package foundation.level

import net.minecraft.util.ExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.attribute.AttributeType
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.level.Level
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent
import java.util.Optional
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas

data class FoundationSpaceEnvironment(
    val body: Optional<Identifier>,
    val saveName: String,
    val atmosphere: FoundationAtmosphere,
    val hasWeather: Boolean,
    val dayLength: Long = 24000L,
    val starScale: Float = 0.75f,
    val starOffset: Float = 0f
) {
    init { require(body.isEmpty || saveName.isNotBlank()) { "Space dimensions require a weather save name" } }

    companion object {
        @JvmField val ID: Identifier = Identifier.parse("foundation:space_environment")
        @JvmField val CODEC: Codec<FoundationSpaceEnvironment> = RecordCodecBuilder.create { instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("body").forGetter(FoundationSpaceEnvironment::body),
            Codec.STRING.optionalFieldOf("save_name", "").forGetter(FoundationSpaceEnvironment::saveName),
            FoundationAtmosphere.CODEC.fieldOf("atmosphere").forGetter(FoundationSpaceEnvironment::atmosphere),
            Codec.BOOL.optionalFieldOf("weather", false).forGetter(FoundationSpaceEnvironment::hasWeather),
            ExtraCodecs.NON_NEGATIVE_LONG.optionalFieldOf("day_length", 24000L).forGetter(FoundationSpaceEnvironment::dayLength),
            Codec.FLOAT.optionalFieldOf("star_scale", 0.75f).forGetter(FoundationSpaceEnvironment::starScale),
            Codec.FLOAT.optionalFieldOf("star_offset", 0f).forGetter(FoundationSpaceEnvironment::starOffset)
        ).apply(instance, ::FoundationSpaceEnvironment) }
        @JvmField val DEFAULT = FoundationSpaceEnvironment(Optional.empty(), "",
            FoundationAtmosphere(true, true, false, 0f, 1f, 1f,
                listOf(EnumAtmosphericGas.NITROGEN, EnumAtmosphericGas.OXYGEN, EnumAtmosphericGas.ARGON, EnumAtmosphericGas.WATER)), false)
        @JvmField val TYPE: AttributeType<FoundationSpaceEnvironment> = AttributeType.ofNotInterpolated(CODEC)
        @JvmField val ATTRIBUTE: EnvironmentAttribute<FoundationSpaceEnvironment> = EnvironmentAttribute.builder(TYPE)
            .defaultValue(DEFAULT).notPositional().syncable().build()

        fun install(context: FMLJavaModLoadingContext) {
            RegisterEvent.getBus(context.modBusGroup).addListener { event ->
                event.register(Registries.ATTRIBUTE_TYPE, ID) { TYPE }
                event.register(Registries.ENVIRONMENT_ATTRIBUTE, ID) { ATTRIBUTE }
            }
        }

        fun get(level: Level): FoundationSpaceEnvironment = level.environmentAttributes().getDimensionValue(ATTRIBUTE)
    }
}
