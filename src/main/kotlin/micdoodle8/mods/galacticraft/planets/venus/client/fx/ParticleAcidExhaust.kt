package micdoodle8.mods.galacticraft.planets.venus.client.fx

import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class ParticleAcidExhaust constructor(
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
        this.particleScale *= p_i46348_14_
        this.smokeParticleScale = this.particleScale
        this.particleMaxAge = (8.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.particleMaxAge = (this.particleMaxAge.toFloat() * p_i46348_14_ * 5.0f).toInt()
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
        var f: Float = (this.particleAge.toFloat() + partialTicks) / this.particleMaxAge.toFloat() * 32.0f
        f = MathHelper.clamp(f, 0.0f, 1.0f)
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

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1
            this.motionZ *= 1.1
        }

        this.motionX *= 0.9599999785423279
        this.motionY *= 0.9599999785423279
        this.motionZ *= 0.9599999785423279

        if (this.onGround) {
            this.motionX *= 0.699999988079071
            this.motionZ *= 0.699999988079071
        }
    }
}
