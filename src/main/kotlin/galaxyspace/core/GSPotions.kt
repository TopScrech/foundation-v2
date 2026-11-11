package galaxyspace.core

import galaxyspace.core.prefab.potions.AntiRadiation
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.effect.MobEffect
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegisterEvent

object GSPotions {
    @JvmField val antiradiation: AntiRadiation = AntiRadiation(true, 167709284)
    val events: Map<Identifier, MobEffect> = mapOf(
        Identifier.parse("galaxyspace:antiradiation") to antiradiation
    )

    fun holder(effect: MobEffect): Holder<MobEffect> = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)

    fun install(context: FMLJavaModLoadingContext) {
        RegisterEvent.getBus(context.modBusGroup).addListener { event ->
            for ((id, effect) in events) event.register(Registries.MOB_EFFECT, id) { effect }
        }
    }

    fun register(registry: IForgeRegistry<MobEffect>) {
        for ((id, effect) in events) registry.register(id, effect)
    }
}
