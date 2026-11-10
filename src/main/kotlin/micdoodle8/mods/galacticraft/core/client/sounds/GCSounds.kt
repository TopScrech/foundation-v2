package micdoodle8.mods.galacticraft.core.client.sounds

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.registries.IForgeRegistry

object GCSounds {
    @JvmField
    var bossDeath: SoundEvent? = null

    @JvmField
    var bossLaugh: SoundEvent? = null

    @JvmField
    var bossOoh: SoundEvent? = null

    @JvmField
    var bossOuch: SoundEvent? = null

    @JvmField
    var slimeDeath: SoundEvent? = null

    @JvmField
    var astroMiner: SoundEvent? = null

    @JvmField
    var shuttle: SoundEvent? = null

    @JvmField
    var parachute: SoundEvent? = null

    @JvmField
    var openAirLock: SoundEvent? = null

    @JvmField
    var closeAirLock: SoundEvent? = null

    @JvmField
    var singleDrip: SoundEvent? = null

    @JvmField
    var scaryScape: SoundEvent? = null

    @JvmField
    var deconstructor: SoundEvent? = null

    @JvmField
    var music: SoundEvent? = null

    @JvmStatic
    fun registerSounds(registry: IForgeRegistry<SoundEvent?>) {
        bossDeath = registerSound("entity.bossdeath", registry)
        bossLaugh = registerSound("entity.bosslaugh", registry)
        bossOoh = registerSound("entity.ooh", registry)
        bossOuch = registerSound("entity.ouch", registry)
        slimeDeath = registerSound("entity.slime_death", registry)
        astroMiner = registerSound("entity.astrominer", registry)
        shuttle = registerSound("shuttle.shuttle", registry)
        parachute = registerSound("player.parachute", registry)
        openAirLock = registerSound("player.openairlock", registry)
        closeAirLock = registerSound("player.closeairlock", registry)
        singleDrip = registerSound("ambience.singledrip", registry)
        scaryScape = registerSound("ambience.scaryscape", registry)
        music = registerSound("galacticraft.music_space", registry)
        deconstructor = registerSound("block.deconstructor", registry)
    }

    @JvmStatic
    private fun registerSound(soundName: String, registry: IForgeRegistry<SoundEvent?>): SoundEvent {
        val soundID: ResourceLocation = ResourceLocation(Constants.ASSET_PREFIX, soundName)
        val result: SoundEvent = SoundEvent(soundID).setRegistryName(soundID)
        registry.register(result)
        return result
    }
}
