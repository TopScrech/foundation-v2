package foundation.level

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas

/** Immutable atmosphere values can be shared by native dimension and network codecs */
class FoundationAtmosphere(
    val breathable: Boolean,
    val precipitation: Boolean,
    val corrosive: Boolean,
    val temperature: Float,
    val wind: Float,
    val density: Float,
    gases: List<EnumAtmosphericGas>
) {
    val gases: List<EnumAtmosphericGas> = java.util.List.copyOf(gases)

    fun soundReduction(): Float = soundReduction(density)
    fun meteorFrequency(): Double = meteorFrequency(density)

    override fun equals(other: Any?): Boolean = other is FoundationAtmosphere &&
        breathable == other.breathable && precipitation == other.precipitation && corrosive == other.corrosive &&
        temperature.toBits() == other.temperature.toBits() && wind.toBits() == other.wind.toBits() &&
        density.toBits() == other.density.toBits() && gases == other.gases

    override fun hashCode(): Int = java.util.Objects.hash(breathable, precipitation, corrosive, temperature, wind, density, gases)

    companion object {
        @JvmField val CODEC: Codec<FoundationAtmosphere> = RecordCodecBuilder.create { instance -> instance.group(
            Codec.BOOL.fieldOf("breathable").forGetter(FoundationAtmosphere::breathable),
            Codec.BOOL.fieldOf("precipitation").forGetter(FoundationAtmosphere::precipitation),
            Codec.BOOL.fieldOf("corrosive").forGetter(FoundationAtmosphere::corrosive),
            Codec.FLOAT.fieldOf("temperature").forGetter(FoundationAtmosphere::temperature),
            Codec.FLOAT.fieldOf("wind").forGetter(FoundationAtmosphere::wind),
            Codec.FLOAT.fieldOf("density").forGetter(FoundationAtmosphere::density),
            EnumAtmosphericGas.CODEC.listOf().fieldOf("gases").forGetter(FoundationAtmosphere::gases)
        ).apply(instance, ::FoundationAtmosphere) }

        fun from(info: AtmosphereInfo): FoundationAtmosphere = FoundationAtmosphere(
            info.isBreathable(), info.hasPrecipitation(), info.isCorrosive(), info.thermalLevel(),
            info.windLevel(), info.relativeDensity(), info.composition
        )

        fun soundReduction(density: Float): Float = when {
            density <= 0f -> 20f
            density > 5f -> 0.2f
            else -> 1f / density
        }

        fun meteorFrequency(density: Float): Double = if (density <= 0f) 5.0 else density.toDouble() * 100.0
    }
}
