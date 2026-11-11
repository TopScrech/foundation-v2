package micdoodle8.mods.galacticraft.planets.venus.client.fx

import kotlin.math.pow
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.max

@OnlyIn(Dist.CLIENT)
open class ParticleAcidVapor constructor(
    worldIn: World,
    xCoordIn: Double,
    yCoordIn: Double,
    zCoordIn: Double,
    motionX: Double,
    motionY: Double,
    motionZ: Double,
    p_i46348_14_: Float
) : Particle(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0) {
    @JvmField
    var smokeParticleScale: Float

    init {
        this.motionX *= 0.10000000149011612
        this.motionY *= 0.10000000149011612
        this.motionZ *= 0.10000000149011612
        this.motionX += motionX / 10.0f
        this.motionY += motionY
        this.motionZ += motionZ / 10.0f
        this.particleRed = (Math.random() * 0.10000001192092896 + 0.8).toFloat()
        this.particleGreen = particleRed
        this.particleBlue = (Math.random() * 0.10000001192092896).toFloat()
        this.particleAlpha = 1.0f
        //        this.particleScale *= 0.75F;
        this.particleScale *= p_i46348_14_
        this.smokeParticleScale = this.particleScale
        this.particleMaxAge = (8.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.particleMaxAge = (this.particleMaxAge.toFloat() * p_i46348_14_).toInt()
        this.canCollide = true
    }

    override fun renderParticle(
        worldRendererIn: BufferBuilder,
        entityIn: Entity,
        partialTicks: Float,
        p_180434_4_: Float,
        p_180434_5_: Float,
        p_180434_6_: Float,
        p_180434_7_: Float,
        p_180434_8_: Float
    ) {
        GlStateManager.disableLighting()
        var f: Float = (this.particleAge / 11.0).pow(2.0).toFloat()
        f = max(f, 0.1f)
        this.particleAlpha = this.particleAlpha * 0.994f
        this.particleScale = this.smokeParticleScale * f
        super.renderParticle(
            worldRendererIn,
            entityIn,
            partialTicks,
            p_180434_4_,
            p_180434_5_,
            p_180434_6_,
            p_180434_7_,
            p_180434_8_
        )
    }

    override fun onUpdate() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired()
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge)
        this.motionY += 0.004
        this.move(this.motionX, this.motionY, this.motionZ)

        this.motionY *= 0.9899999785423279
        this.motionX *= 1.001
        this.motionZ *= 1.001

        if (this.onGround) {
            this.motionX *= 0.699999988079071
            this.motionZ *= 0.699999988079071
        }
    }
}
