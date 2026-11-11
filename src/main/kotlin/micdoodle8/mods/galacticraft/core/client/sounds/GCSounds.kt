package micdoodle8.mods.galacticraft.core.client.sounds

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegisterEvent

/** Common sound identities retain their legacy package for callers on either physical side */
object GCSounds {
    @JvmField val bossDeath: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:entity.bossdeath"))
    @JvmField val bossLaugh: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:entity.bosslaugh"))
    @JvmField val bossOoh: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:entity.ooh"))
    @JvmField val bossOuch: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:entity.ouch"))
    @JvmField val slimeDeath: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:entity.slime_death"))
    @JvmField val astroMiner: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:entity.astrominer"))
    @JvmField val shuttle: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:shuttle.shuttle"))
    @JvmField val parachute: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:player.parachute"))
    @JvmField val openAirLock: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:player.openairlock"))
    @JvmField val closeAirLock: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:player.closeairlock"))
    @JvmField val singleDrip: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:ambience.singledrip"))
    @JvmField val scaryScape: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:ambience.scaryscape"))
    @JvmField val music: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:galacticraft.music_space"))
    @JvmField val deconstructor: SoundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("galacticraftcore:block.deconstructor"))

    val events: Map<Identifier, SoundEvent> = listOf(
        bossDeath,
        bossLaugh,
        bossOoh,
        bossOuch,
        slimeDeath,
        astroMiner,
        shuttle,
        parachute,
        openAirLock,
        closeAirLock,
        singleDrip,
        scaryScape,
        music,
        deconstructor
    ).associateBy { it.location() }

    fun install(context: FMLJavaModLoadingContext) {
        RegisterEvent.getBus(context.modBusGroup).addListener { event ->
            for ((id, sound) in events) event.register(Registries.SOUND_EVENT, id) { sound }
        }
    }

    @JvmStatic fun registerSounds(registry: IForgeRegistry<SoundEvent>) {
        for ((id, sound) in events) registry.register(id, sound)
    }
}
