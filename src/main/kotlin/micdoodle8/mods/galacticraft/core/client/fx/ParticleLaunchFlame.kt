package micdoodle8.mods.galacticraft.core.client.fx

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class ParticleLaunchFlame constructor(
    par1World: World,
    position: Vector3,
    motion: Vector3,
    launched: Boolean,
    ridingEntity: EntityLivingBase?
) : Particle(par1World, position.x, position.y, position.z, 0.0, 0.0, 0.0) {
    private val smokeParticleScale: Float
    private val spawnSmokeShort: Boolean
    private val ridingEntity: EntityLivingBase?

    init {
        this.motionX = motion.x
        this.motionY = motion.y
        this.motionZ = motion.z
        this.particleRed = 255f / 255f
        this.particleGreen = 120f / 255f + this.rand.nextFloat() / 3
        this.particleBlue = 55f / 255f
        this.particleScale *= if (launched) 4f else 0.1f
        this.smokeParticleScale = this.particleScale
        this.particleMaxAge = (this.particleMaxAge * 1f).toInt()
        this.canCollide = true
        this.spawnSmokeShort = launched
        this.ridingEntity = ridingEntity
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
    }

    override fun onUpdate() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.particleAge++ >= this.particleMaxAge) {
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.spawnSmokeShort) "whiteSmokeLaunched" else "whiteSmokeIdle",
                Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ),
                Vector3(this.motionX, this.motionY, this.motionZ),
                arrayOf<Any?>()
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.spawnSmokeShort) "whiteSmokeLargeLaunched" else "whiteSmokeLargeIdle",
                Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ),
                Vector3(this.motionX, this.motionY, this.motionZ),
                arrayOf<Any?>()
            )
            if (!this.spawnSmokeShort) {
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "whiteSmokeIdle",
                    Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ),
                    Vector3(this.motionX, this.motionY, this.motionZ),
                    arrayOf<Any?>()
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "whiteSmokeLargeIdle",
                    Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ),
                    Vector3(this.motionX, this.motionY, this.motionZ),
                    arrayOf<Any?>()
                )
            }
            this.setExpired()
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge)
        this.motionY += 0.001
        this.move(this.motionX, this.motionY, this.motionZ)

        this.particleGreen += 0.01f

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1
            this.motionZ *= 1.1
        }

        this.motionX *= 0.9599999785423279
        this.motionY *= 0.9599999785423279
        this.motionZ *= 0.9599999785423279

        if (this.world.rand.nextInt(5) == 1) {
            val var3: MutableList<*> =
                this.world.getEntitiesWithinAABB<Entity?>(Entity::class.java, this.getBoundingBox().grow(1.0, 0.5, 1.0))

            if (var3 != null) {
                for (var4: Int in var3.indices) {
                    val var5: Entity? = var3.get(var4) as Entity?

                    if (var5 is EntityLivingBase && !var5.isRemoved() && !var5.isBurning() && (var5 != this.ridingEntity)) {
                        var5.setFire(3)
                        (GalacticraftCore.packetPipeline)!!.sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_SET_ENTITY_FIRE,
                                GCCoreUtil.getDimensionID(var5.level()),
                                arrayOf<Any>(var5.getId())
                            )
                        )
                    }
                }
            }
        }
    }

    override fun getBrightnessForRender(par1: Float): Int {
        return 15728880
    } //    @Override
    //    public float getBrightness(float par1)
    //    {
    //        return 1.0F;
    //    }
}
