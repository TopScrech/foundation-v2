package micdoodle8.mods.galacticraft.planets.mars.client.fx

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class EntityCryoFX constructor(worldIn: World, position: Vector3, motion: Vector3) :
    Particle(worldIn, position.x, position.y, position.z, 0.0, 0.0, 0.0) {
    @JvmField
    var field_70569_a: Float

    init {
        val f: Float = 2.5f
        this.motionX *= 0.0
        this.motionY *= 0.10000000149011612
        this.motionZ *= 0.0
        this.motionX += motion.x
        this.motionY += motion.y
        this.motionZ += motion.z
        this.particleBlue = 1.0f - (Math.random() * 0.30000001192092896).toFloat()
        this.particleGreen = this.particleBlue
        this.particleRed = this.particleGreen
        this.particleRed *= 0.8f
        this.particleGreen *= 0.8f
        this.particleScale *= 0.25f
        this.particleScale *= f
        this.field_70569_a = this.particleScale
        this.particleMaxAge = (8.0 / (Math.random() * 0.8 + 0.3)).toInt()
        this.particleMaxAge = (this.particleMaxAge.toFloat() * f).toInt()
        this.canCollide = false
    }

    /**
     * Renders the particle
     */
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
        var f: Float = (this.particleAge.toFloat() + partialTicks) / this.particleMaxAge.toFloat() * 32.0f
        f = MathHelper.clamp(f, 0.0f, 1.0f)
        this.particleScale = this.field_70569_a * f
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

    /**
     * Called to update the entity's position/logic.
     */
    override fun onUpdate() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired()
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge)
        this.move(this.motionX, this.motionY, this.motionZ)
        this.motionX *= 0.9599999785423279
        this.motionY *= 0.9599999785423279
        this.motionZ *= 0.9599999785423279
        val entityplayer: EntityPlayer? = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0, false)

        if (entityplayer != null && this.posY > entityplayer.getBoundingBox().minY) {
            this.posY += (entityplayer.getBoundingBox().minY - this.posY) * 0.2
            this.motionY += (entityplayer.deltaMovement.y - this.motionY) * 0.2
            this.setPosition(this.posX, this.posY, this.posZ)
        }

        if (this.onGround) {
            this.motionX *= 0.699999988079071
            this.motionZ *= 0.699999988079071
        }
    }
}
