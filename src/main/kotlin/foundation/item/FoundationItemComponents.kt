package foundation.item

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.component.CustomData
import net.minecraft.resources.Identifier
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent

/** Kept separate from durability and custom NBT, which have their own gameplay meanings */
object FoundationItemComponents {
    @JvmField val ENERGY_JOULES_ID: Identifier = Identifier.parse("foundation:energy_joules")
    @JvmField val ENERGY_JOULES: DataComponentType<Float> = DataComponentType.builder<Float>()
        .persistent(Codec.FLOAT.validate { value ->
            if (value.isFinite() && value >= 0f) com.mojang.serialization.DataResult.success(value)
            else com.mojang.serialization.DataResult.error { "Invalid stored item energy $value" }
        })
        .networkSynchronized(ByteBufCodecs.FLOAT)
        .build()
    @JvmField val LEGACY_METADATA_ID: Identifier = Identifier.fromNamespaceAndPath("foundation", "legacy_metadata")
    @JvmField val LEGACY_METADATA: DataComponentType<Int> = DataComponentType.builder<Int>()
        .persistent(Codec.intRange(0, Int.MAX_VALUE))
        .networkSynchronized(ByteBufCodecs.VAR_INT)
        .build()

    @JvmField val LEGACY_CAPABILITIES_ID: Identifier = Identifier.fromNamespaceAndPath("foundation", "legacy_capabilities")
    @JvmField val LEGACY_CAPABILITIES: DataComponentType<CustomData> = DataComponentType.builder<CustomData>()
        .persistent(CustomData.CODEC)
        .networkSynchronized(CustomData.STREAM_CODEC)
        .build()

    @JvmField val OXYGEN_USED_ID: Identifier = Identifier.fromNamespaceAndPath("foundation", "oxygen_used")
    @JvmField val OXYGEN_USED: DataComponentType<Int> = DataComponentType.builder<Int>()
        .persistent(Codec.intRange(0, Int.MAX_VALUE))
        .networkSynchronized(ByteBufCodecs.VAR_INT)
        .build()

    val types: Map<Identifier, DataComponentType<*>> = mapOf(
        LEGACY_METADATA_ID to LEGACY_METADATA, LEGACY_CAPABILITIES_ID to LEGACY_CAPABILITIES, OXYGEN_USED_ID to OXYGEN_USED,
        ENERGY_JOULES_ID to ENERGY_JOULES
    )

    fun install(context: FMLJavaModLoadingContext) {
        RegisterEvent.getBus(context.modBusGroup).addListener { event: RegisterEvent ->
            for ((id, type) in types) event.register(Registries.DATA_COMPONENT_TYPE, id) { type }
        }
    }
}
