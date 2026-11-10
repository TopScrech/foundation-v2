package micdoodle8.mods.galacticraft.core.client.fx

import java.util.HashMap

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class ParticleOxygen constructor(par1World: World, position: Vector3, motion: Vector3, color: Vector3) :
    Particle(par1World, position.x, position.y, position.z, motion.x, motion.y, motion.z) {
    private val portalParticleScale: Float
    private val portalPosX: Double
    private val portalPosY: Double
    private val portalPosZ: Double

    init {
        this.motionX = motion.x
        this.motionY = motion.y
        this.motionZ = motion.z
        this.posX = position.x
        this.portalPosX = this.posX
        this.posY = position.y
        this.portalPosY = this.posY
        this.posZ = position.z
        this.portalPosZ = this.posZ
        this.particleScale = 0.1f
        this.portalParticleScale = this.particleScale
        this.particleRed = color.floatX()
        this.particleGreen = color.floatY()
        this.particleBlue = color.floatZ()
        this.particleMaxAge = (Math.random() * 10.0).toInt() + 40
        this.canCollide = false
        this.setParticleTextureIndex((Math.random() * 8.0).toInt())
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
        var var8: Float = (this.particleAge + partialTicks) / this.particleMaxAge
        var8 = 1.0f - var8
        var8 *= var8
        var8 = 1.0f - var8
        this.particleScale = this.portalParticleScale * var8
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

    override fun getBrightnessForRender(par1: Float): Int {
        val time: Long = this.world.getWorldTime()
        if (time > tick) {
            cacheLighting.clear()
            tick = time
        }
        val blockpos: BlockPos = BlockPos(this.posX, this.posY + 0.17, this.posZ)
        val var2: Int
        if (cacheLighting.containsKey(blockpos)) {
            var2 = cacheLighting.get(blockpos)!!
        } else {
            var2 = if (this.world.isBlockLoaded(blockpos)) this.world.getCombinedLight(blockpos, 0) else 0
            cacheLighting.put(blockpos, var2)
        }
        var var3: Float = this.particleAge.toFloat() / this.particleMaxAge.toFloat()
        var3 *= var3
        var3 *= var3
        val var4: Int = var2 and 255
        var var5: Int = var2 shr 16 and 255
        var5 += (var3 * 15.0f * 16.0f).toInt()

        if (var5 > 240) {
            var5 = 240
        }

        return var4 or (var5 shl 16)
    }

    //    @Override
    //    public float getBrightness(float par1)
    //    {
    //        final float var2 = super.getBrightness(par1);
    //        float var3 = (float) this.particleAge / (float) this.particleMaxAge;
    //        var3 = var3 * var3 * var3 * var3;
    //        return var2 * (1.0F - var3) + var3;
    //    }
    override fun onUpdate() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        var var1: Float = this.particleAge.toFloat() / this.particleMaxAge.toFloat()
        val var2: Float = var1
        var1 = -var1 + var1 * var1 * 2.0f
        var1 = 1.0f - var1
        this.posX = this.portalPosX + this.motionX * var1
        this.posY = this.portalPosY + this.motionY * var1 + (1.0f - var2)
        this.posZ = this.portalPosZ + this.motionZ * var1

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired()
        }
    }

    companion object {
        private var tick = -1L
        private val cacheLighting: MutableMap<BlockPos?,Int> = HashMap<BlockPos?,Int>()
    }
}
