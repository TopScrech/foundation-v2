package galaxyspace.core.client.fx

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object GSEffectHandler {
    @JvmStatic
    fun spawnParticle(particleID: String, position: Vector3, motion: Vector3, vararg otherInfo: Any?) {
        val mc: Minecraft? = FMLClientHandler.instance().getClient()

        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
            val dX: Double = mc.getRenderViewEntity()!!.posX - position.x
            val dY: Double = mc.getRenderViewEntity()!!.posY - position.y
            val dZ: Double = mc.getRenderViewEntity()!!.posZ - position.z

            var particle: Particle? = null
            val viewDistance: Double = 64.0

            if (particleID == "waterbubbles") {
                particle = EntityParticles(
                    mc.world,
                    position,
                    motion,
                    otherInfo[0] as Int,
                    otherInfo[1] as Int,
                    otherInfo[2] as Boolean,
                    otherInfo[3] as Vector3?,
                    otherInfo[4] as Double
                )
            } else if (particleID == "waterbubbles1") {
                particle = EntityParticles(
                    mc.world,
                    position,
                    motion,
                    otherInfo[0] as Int,
                    otherInfo[1] as Int,
                    otherInfo[2] as Boolean,
                    otherInfo[3] as Vector3?,
                    otherInfo[4] as Double,
                    otherInfo[5] as Double
                )
            } else if (particleID == "launchFlameLaunched") {
                particle = GSParticleLaunchFlame(
                    mc.world,
                    position,
                    motion,
                    true,
                    otherInfo[0] as EntityLivingBase?,
                    (otherInfo[1] as Vector3?)!!
                )
            }

            val setting: Int = mc.gameSettings.particleSetting

            if (particle != null && mc.world.rand.nextInt(if (setting == 0) 1 else setting + 1) == 0) {
                mc.effectRenderer.addEffect(particle)
            }
        }
    }
}
