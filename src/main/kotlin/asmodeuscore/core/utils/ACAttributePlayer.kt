package asmodeuscore.core.utils

import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraftforge.event.entity.EntityAttributeModificationEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegisterEvent

object ACAttributePlayer {
    private val radiation = object : Attribute("attribute.asmodeuscore.radiation", 0.0) {}.setSyncable(true)
    private val test = object : Attribute("attribute.asmodeuscore.test", 0.0) {}.setSyncable(true)
    val entries: Map<Identifier, Attribute> = mapOf(Identifier.parse("asmodeuscore:radiation") to radiation, Identifier.parse("asmodeuscore:test") to test)

    val RADIATION_LVL: Holder<Attribute> get() = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(radiation)
    val TEST: Holder<Attribute> get() = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(test)

    fun install(context: FMLJavaModLoadingContext) {
        RegisterEvent.getBus(context.modBusGroup).addListener { event ->
            for ((id, attribute) in entries) event.register(Registries.ATTRIBUTE, id) { attribute }
        }
        EntityAttributeModificationEvent.BUS.addListener(::addPlayerAttributes)
    }

    fun register(registry: IForgeRegistry<Attribute>) {
        for ((id, attribute) in entries) registry.register(id, attribute)
    }

    fun addPlayerAttributes(event: EntityAttributeModificationEvent) {
        if (!event.has(EntityTypes.PLAYER, RADIATION_LVL)) event.add(EntityTypes.PLAYER, RADIATION_LVL)
    }
}
