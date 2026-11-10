package micdoodle8.mods.galacticraft.core.client.sounds

import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.audio.ISound
import net.minecraft.client.audio.MovingSound
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.util.SoundCategory

/**
 * This file is part of the Galacticraft project
 *
 * @author micdoodle8
 */
open class SoundUpdaterRocket constructor(
    private val thePlayer: EntityPlayerSP?,
    private val theRocket: EntityAutoRocket
) : MovingSound(GCSounds.shuttle, SoundCategory.NEUTRAL) {
    private var soundStopped = false
    private var ignition = false

    init {
        this.attenuationType = ISound.AttenuationType.NONE
        this.volume = 0.00001f //If it's zero it won't start playing
        this.pitch = 0.0f //pitch
        this.repeat = true
        this.repeatDelay = 0 //repeat delay
        this.updateSoundLocation(theRocket)
    }

    /**
     * Updates the JList with a new model.
     */
    override fun update() {
        if (!this.theRocket.isDead) {
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
                if (this.theRocket.posY > 1000) {
                    this.volume = 0f
                    if (this.theRocket.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LANDING.ordinal) {
                        this.donePlaying = true
                    }
                } else if (this.theRocket.posY > Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                    this.volume =
                        1.0f - ((this.theRocket.posY - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / (1000.0 - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT)).toFloat()
                } else {
                    this.volume = 1.0f
                }
            }

            this.updateSoundLocation(this.theRocket)
        } else {
            this.donePlaying = true
        }
    }

    open fun stopRocketSound() {
        this.donePlaying = true
        this.soundStopped = true
    }

    open fun updateSoundLocation(e: Entity) {
        this.xPosF = e.posX.toFloat()
        this.yPosF = e.posY.toFloat()
        this.zPosF = e.posZ.toFloat()
    }
}
