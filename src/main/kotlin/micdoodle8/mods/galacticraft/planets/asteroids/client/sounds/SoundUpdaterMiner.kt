package micdoodle8.mods.galacticraft.planets.asteroids.client.sounds

import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import net.minecraft.client.audio.MovingSound
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.util.SoundCategory

/**
 * This file is part of the Galacticraft project
 *
 * @author micdoodle8, radfast
 */
open class SoundUpdaterMiner constructor(
    private val thePlayer: EntityPlayerSP?,
    private val theRocket: EntityAstroMiner
) : MovingSound(GCSounds.astroMiner, SoundCategory.AMBIENT) {
    private var soundStopped = false
    private var targetVolume: Float
    private var targetPitch: Float

    init {
        this.volume = 0.00001f //If it's zero it won't start playing
        this.targetVolume = 0.6f
        this.targetPitch = 1.0f
        this.pitch = 1.0f //pitch
        this.repeat = true
        this.repeatDelay = 0 //repeat delay
        this.updateSoundLocation(theRocket)
    }

    /**
     * Updates the JList with a new model.
     */
    override fun update() {
        if (!this.theRocket.isDead) {
            if (this.theRocket.AIstate == EntityAstroMiner.Companion.AISTATE_ATBASE || this.theRocket.AIstate == EntityAstroMiner.Companion.AISTATE_DOCKING) {
                this.targetVolume = 0.6f
                this.targetPitch = 0.1f
            } else {
                this.targetVolume = 1.0f
                this.targetPitch = 1.0f
            }
            if (this.volume < this.targetVolume) {
                this.volume += 0.1f
                if (this.volume > this.targetVolume) {
                    this.volume = this.targetVolume
                }
            } else if (this.volume > this.targetVolume) {
                this.volume -= 0.1f
                if (this.volume < this.targetVolume) {
                    this.volume = this.targetVolume
                }
            }
            if (this.pitch < this.targetPitch) {
                this.pitch += 0.05f
                if (this.pitch > this.targetPitch) {
                    this.pitch = this.targetPitch
                }
            } else if (this.pitch > this.targetPitch) {
                this.pitch -= 0.05f
                if (this.pitch < this.targetPitch) {
                    this.pitch = this.targetPitch
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
