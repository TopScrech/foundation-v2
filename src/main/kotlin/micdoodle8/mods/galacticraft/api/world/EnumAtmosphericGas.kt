package micdoodle8.mods.galacticraft.api.world

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable
import java.util.Locale

enum class EnumAtmosphericGas : StringRepresentable {
    NITROGEN,
    OXYGEN,
    CO2,
    WATER,
    METHANE,
    HYDROGEN,
    HELIUM,
    ARGON;

    override fun getSerializedName(): String = name.lowercase(Locale.ROOT)

    companion object {
        @JvmField val CODEC: Codec<EnumAtmosphericGas> = StringRepresentable.fromEnum { entries.toTypedArray() }
    }
}
