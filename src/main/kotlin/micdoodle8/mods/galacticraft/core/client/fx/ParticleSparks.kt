package micdoodle8.mods.galacticraft.core.client.fx

import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level as World

open class ParticleSparks constructor(
    par1World: World,
    par2: Double,
    par4: Double,
    par6: Double,
    par8: Double,
    par12: Double
) : Particle(par1World, par2, par4, par6, 0.0, 0.0, 0.0) {
    @JvmField
    var smokeParticleScale: Float

    init {
        this.motionX *= 0.10000000149011612
        this.motionY *= 0.10000000149011612
        this.motionZ *= 0.10000000149011612
        this.motionX += par8
        this.motionY += 0.06
        this.motionZ += par12
        this.particleRed = 255f / 255f
        this.particleGreen = 255f / 255f
        this.particleBlue = 0f / 255f + this.rand.nextFloat() / 6
        this.particleScale *= 0.15f
        this.particleScale *= 1.0f * 3
        this.smokeParticleScale = this.particleScale
        this.particleMaxAge = 50.0.toInt()
        this.particleMaxAge = (this.particleMaxAge * 1.0f).toInt()
        this.canCollide = true
    }

    override fun renderParticle(
        worldRendererIn: BufferBuilder,
        entityIn: Entity,
        partialTicks: Float,
        rotationX: Float,
        rotationZ: Float,
        rotationYZ: Float,
        rotationXY: Float,
        rotationXZ: Float
    ) {
        var var8: Float = (this.particleAge + partialTicks) / this.particleMaxAge * 32.0f

        if (var8 < 0.0f) {
            var8 = 0.0f
        }

        if (var8 > 1.0f) {
            var8 = 1.0f
        }

        this.particleScale = this.smokeParticleScale * var8
        super.renderParticle(
            worldRendererIn,
            entityIn,
            partialTicks,
            rotationX,
            rotationZ,
            rotationYZ,
            rotationXY,
            rotationXZ
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
        this.move(this.motionX, this.motionY, this.motionZ)

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1
            this.motionZ *= 1.1
        }

        this.setParticleTextureIndex(160 + 7 - this.particleAge * 8 / this.particleMaxAge)
        this.motionY -= 0.01
        this.move(this.motionX, this.motionY, this.motionZ)
        this.motionX *= 0.9100000262260437
        this.motionY *= 0.9100000262260437
        this.motionZ *= 0.9100000262260437
    }
}
