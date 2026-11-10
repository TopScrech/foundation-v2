package galaxyspace.core.client.fx

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.entity.Entity
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class ParticleRainCustom constructor(
    worldIn: World,
    xCoordIn: Double,
    yCoordIn: Double,
    zCoordIn: Double,
    motionX: Double,
    motionY: Double,
    motionZ: Double,
    particle: Int,
    scale: Float,
    color: Vector3
) : Particle(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0) {
    @JvmField
    var smokeParticleScale: Float

    init {
        this.motionX *= 0.30000001192092896
        this.motionY = Math.random() * 0.20000000298023224 + 0.10000000149011612
        this.motionZ *= 0.30000001192092896
        this.particleRed = color.floatX() //(float)(Math.random() * 0.10000001192092896D + 0.8);
        this.particleGreen = color.floatY()
        this.particleBlue = color.floatZ() //(float)(Math.random() * 0.10000001192092896D);
        this.particleAlpha = 1.0f
        //        this.particleScale *= 0.75F;
        this.particleScale *= scale
        this.particleGravity = 0.06f
        this.smokeParticleScale = this.particleScale
        this.particleMaxAge = (8.0 / (Math.random() * 0.8 + 0.2)).toInt()
        //this.particleMaxAge = (int)((float)this.particleMaxAge * scale);
        this.canCollide = true
        this.setParticleTextureIndex(particle)
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
        /*  GlStateManager.disableLighting();
        float f = (float) Math.pow(this.particleAge / 11.0, 2.0F);
        f = Math.max(f, 0.1F);
        this.particleAlpha = this.particleAlpha * 0.994F;
        this.particleScale = this.smokeParticleScale * f;*/
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
        this.motionY -= this.particleGravity.toDouble()
        this.move(this.motionX, this.motionY, this.motionZ)
        this.motionX *= 0.9800000190734863
        this.motionY *= 0.9800000190734863
        this.motionZ *= 0.9800000190734863

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired()
        }

        if (this.onGround) {
            this.motionX *= 0.699999988079071
            this.motionZ *= 0.699999988079071
        }
    }
}
