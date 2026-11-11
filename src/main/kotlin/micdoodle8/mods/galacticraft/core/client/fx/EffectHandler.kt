package micdoodle8.mods.galacticraft.core.client.fx

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
object EffectHandler {
    @JvmStatic
    fun spawnParticle(particleID: String, position: Vector3, motion: Vector3, vararg otherInfo: Any?) {
        val mc: Minecraft? = Minecraft.getInstance()

        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
            val dX: Double = mc.getRenderViewEntity()!!.posX - position.x
            val dY: Double = mc.getRenderViewEntity()!!.posY - position.y
            val dZ: Double = mc.getRenderViewEntity()!!.posZ - position.z
            var particle: Particle? = null
            val viewDistance: Double = 64.0

            if (particleID == "whiteSmokeIdle") {
                particle = ParticleLaunchSmoke(mc.world, position, motion, 1.0f, false)
            } else if (particleID == "whiteSmokeLaunched") {
                particle = ParticleLaunchSmoke(mc.world, position, motion, 1.0f, true)
            } else if (particleID == "whiteSmokeLargeIdle") {
                particle = ParticleLaunchSmoke(mc.world, position, motion, 2.5f, false)
            } else if (particleID == "whiteSmokeLargeLaunched") {
                particle = ParticleLaunchSmoke(mc.world, position, motion, 2.5f, true)
            } else if (particleID == "launchFlameIdle") {
                particle = ParticleLaunchFlame(mc.world, position, motion, false, otherInfo[0] as EntityLivingBase?)
            } else if (particleID == "launchFlameLaunched") {
                particle = ParticleLaunchFlame(mc.world, position, motion, true, otherInfo[0] as EntityLivingBase?)
            } else if (particleID == "whiteSmokeTiny") {
                particle = ParticleSmokeSmall(mc.world, position, motion)
            } else if (particleID == "oilDrip") {
                particle = ParticleOilDrip(mc.world, position.x, position.y, position.z)
            }

            if (dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance) {
                if (particleID == "oxygen") {
                    particle = ParticleOxygen(mc.world, position, motion, (otherInfo[0] as Vector3?)!!)
                }
            }

            if (particle != null) {
                mc.effectRenderer.addEffect(particle)
            }
        }
    }
}
