package micdoodle8.mods.galacticraft.planets.asteroids.client.fx

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.ref.WeakReference

@OnlyIn(Dist.CLIENT)
open class EntityFXTeleport constructor(
    par1World: World,
    position: Vector3,
    motion: Vector3,
    telepad: TileEntityShortRangeTelepad?,
    direction: Boolean
) : Particle(par1World, position.x, position.y, position.z, motion.x, motion.y, motion.z) {
    private val portalParticleScale: Float
    private val portalPosX: Double
    private val portalPosY: Double
    private val portalPosZ: Double
    private val telepad: WeakReference<TileEntityShortRangeTelepad?>
    private val direction: Boolean

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
        this.particleScale = this.rand.nextFloat() * 0.2f + 0.5f
        this.portalParticleScale = this.particleScale
        this.particleMaxAge = (Math.random() * 10.0).toInt() + 40
        this.canCollide = false
        this.canCollide = false
        this.setParticleTextureIndex((Math.random() * 8.0).toInt())
        this.telepad = WeakReference<TileEntityShortRangeTelepad?>(telepad)
        this.direction = direction
    }

    override fun renderParticle(
        worldRenderer: BufferBuilder,
        entity: Entity,
        f0: Float,
        f1: Float,
        f2: Float,
        f3: Float,
        f4: Float,
        f5: Float
    ) {
        var f6: Float = (this.particleAge + f0) / this.particleMaxAge
        f6 = 1.0f - f6
        f6 *= f6
        f6 = 1.0f - f6
        this.particleScale = this.portalParticleScale * f6
        super.renderParticle(worldRenderer, entity, f0, f1, f2, f3, f4, f5)
    }

    override fun getBrightnessForRender(par1: Float): Int {
        val i: Int = super.getBrightnessForRender(par1)
        var f1: Float = this.particleAge.toFloat() / this.particleMaxAge.toFloat()
        f1 *= f1
        f1 *= f1
        val j: Int = i and 255
        var k: Int = i shr 16 and 255
        k += (f1 * 15.0f * 16.0f).toInt()

        if (k > 240) {
            k = 240
        }

        return j or (k shl 16)
    }

    //    @Override
    //    public float getBrightness(float par1)
    //    {
    //        float f1 = super.getBrightness(par1);
    //        float f2 = (float) this.particleAge / (float) this.particleMaxAge;
    //        f2 = f2 * f2 * f2 * f2;
    //        return f1 * (1.0F - f2) + f2;
    //    }
    override fun onUpdate() {
        val telepad1: TileEntityShortRangeTelepad? = this.telepad.get()

        if (telepad1 != null) {
            val color: Vector3 = telepad1.getParticleColor(this.rand, this.direction)
            this.particleRed = color.floatX()
            this.particleGreen = color.floatY()
            this.particleBlue = color.floatZ()
        }

        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        var f: Float = this.particleAge.toFloat() / this.particleMaxAge.toFloat()
        val f1: Float = f
        f = -f + f * f * 2.0f
        f = 1.0f - f
        this.posX = this.portalPosX + this.motionX * f
        this.posY = this.portalPosY + this.motionY * f + (1.0f - f1)
        this.posZ = this.portalPosZ + this.motionZ * f

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired()
        }
    }
}
