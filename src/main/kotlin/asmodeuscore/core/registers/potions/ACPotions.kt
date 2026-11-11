package asmodeuscore.core.registers.potions

import asmodeuscore.core.prefab.potions.Radiation
import asmodeuscore.core.prefab.potions.Overheat
import asmodeuscore.core.prefab.potions.Hypothermia
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.effect.MobEffect
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegisterEvent

object ACPotions {
    @JvmField val radiation: Radiation = Radiation(true, -4502242)
    @JvmField val overheat: Overheat = Overheat(true, -23703552)
    @JvmField val hypothermia: Hypothermia = Hypothermia(true, -31551233)
    val events: Map<Identifier, MobEffect> = mapOf(
        Identifier.parse("asmodeuscore:radiation") to radiation,
        Identifier.parse("asmodeuscore:overheat") to overheat,
        Identifier.parse("asmodeuscore:hypothermia") to hypothermia
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
