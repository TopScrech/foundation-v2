package micdoodle8.mods.galacticraft.core.client.fx

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class ParticleLaunchSmoke constructor(
    par1World: World,
    position: Vector3,
    motion: Vector3,
    size: Float,
    launched: Boolean
) : EntityFXLaunchParticle(par1World, position.x, position.y, position.z, 0.0, 0.0, 0.0) {
    @JvmField
    var smokeParticleScale: Float

    init {
        this.motionX *= 0.10000000149011612
        this.motionY *= 0.10000000149011612
        this.motionZ *= 0.10000000149011612
        this.setSize(0.2f, 0.2f)
        this.motionX += motion.x
        this.motionY += motion.y
        this.motionZ += motion.z
        this.particleAlpha = 1.0f
        this.particleBlue = (Math.random() * 0.30000001192092896).toFloat() + 0.6f
        this.particleGreen = this.particleBlue
        this.particleRed = this.particleGreen
        this.particleScale *= 0.75f
        this.particleScale *= size * 3
        this.smokeParticleScale = this.particleScale

        if (launched) {
            this.particleMaxAge = (this.particleMaxAge * size).toInt() + 10
        } else {
            this.motionX += par1World.random.nextDouble() / 2 - 0.25
            this.motionY += par1World.random.nextDouble() / 20
            this.motionZ += par1World.random.nextDouble() / 2 - 0.25
            this.particleMaxAge = 30 + this.particleMaxAge
        }

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
        GL11.glPushMatrix()
        GL11.glDepthMask(false)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
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

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glPopMatrix()
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

        //        if (this.posY == this.prevPosY)
//        {
////            this.motionX *= 1.0001D;
////            this.motionZ *= 1.0001D;
//        }
//        else
//        {
//            this.motionX *= 0.99D;
//            this.motionZ *= 0.99D;
//        }
    }
}
