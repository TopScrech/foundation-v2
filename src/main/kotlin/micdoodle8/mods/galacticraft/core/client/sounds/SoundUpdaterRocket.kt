package micdoodle8.mods.galacticraft.core.client.sounds

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.Entity
import net.minecraft.sounds.SoundSource as SoundCategory

/**
 * This file is part of the Galacticraft project
 *
 * @author micdoodle8
 */
open class SoundUpdaterRocket constructor(
    private val thePlayer: EntityPlayerSP?,
    private val theRocket: EntityAutoRocket
) : AbstractTickableSoundInstance(GCSounds.shuttle, SoundCategory.NEUTRAL, SoundInstance.createUnseededRandom()) {
    private var soundStopped = false
    private var ignition = false

    init {
        this.attenuation = SoundInstance.Attenuation.NONE
        this.volume = 0.00001f //If it's zero it won't start playing
        this.pitch = 0.0f //pitch
        this.looping = true
        this.delay = 0 //repeat delay
        this.updateSoundLocation(theRocket)
    }

    /**
     * Updates the JList with a new model.
     */
    override fun tick() {
        if (!this.theRocket.isRemoved) {
            if (this.theRocket.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.IGNITED.ordinal) {
                if (!ignition) {
                    this.pitch = 0.0f
                    ignition = true
                }
                if (this.theRocket.timeUntilLaunch < this.theRocket.getPreLaunchWait()) {
                    if (this.pitch < 1.0f) {
                        this.pitch += 0.0025f
                    }

                    if (this.pitch > 1.0f) {
                        this.pitch = 1.0f
                    }
                }
            } else {
                this.pitch = 1.0f
            }

            if (this.theRocket.launchPhase >= EntitySpaceshipBase.EnumLaunchPhase.IGNITED.ordinal) {
                if (this.theRocket.y > 1000) {
                    this.volume = 0f
                    if (this.theRocket.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LANDING.ordinal) {
                        this.stop()
                    }
                } else if (this.theRocket.y > Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                    this.volume =
                        1.0f - ((this.theRocket.y - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / (1000.0 - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT)).toFloat()
                } else {
                    this.volume = 1.0f
                }
            }

            this.updateSoundLocation(this.theRocket)
        } else {
            this.stop()
        }
    }

    open fun stopRocketSound() {
        this.stop()
        this.soundStopped = true
    }

    open fun updateSoundLocation(e: Entity) {
        this.x = e.x
        this.y = e.y
        this.z = e.z
    }
}
