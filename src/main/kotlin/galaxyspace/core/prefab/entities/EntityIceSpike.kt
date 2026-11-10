package galaxyspace.core.prefab.entities

import galaxyspace.core.prefab.entities.EntityIceSpike
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random
import kotlin.math.min

open class EntityIceSpike : EntityThrowable {
    constructor(world: World) : super(world)

    constructor(world: World, x: Double, y: Double, z: Double) : super(world, x, y, z)

    constructor(world: World, entity: EntityLivingBase) : super(world, entity)

    override fun onUpdate() {
        super.onUpdate()



        if (this.ticksExisted <= 2) {
            return
        }

        if (this.world.isRemote) {
            this.spawnParticles(this.posX, this.posY, this.posZ)
        }

        //		this.igniteBlocks(this.posX, this.posY, this.posZ);
    }

    open fun spawnParticles(posX: Double, posY: Double, posZ: Double) {
        val rand: Random = Random()

        for (i: Int in 0..<min(20, this.ticksExisted)) {
            val randXMotion: Double = rand.nextGaussian() * 0.0025 * this.ticksExisted
            val randYMotion: Double = rand.nextDouble() * 0.025 * min(25, this.ticksExisted)
            val randZMotion: Double = rand.nextGaussian() * 0.0025 * this.ticksExisted

            this.world.spawnParticle(
                EnumParticleTypes.SNOWBALL,
                true,
                posX,
                posY,
                posZ,
                randXMotion,
                randYMotion,
                randZMotion
            )
        }
    }

    override fun getGravityVelocity(): Float {
        return 0.0003f
    }

    override fun onImpact(result: RayTraceResult) {
        val hit: Entity? = result.entityHit
        if (hit != null) {
            val entity: Entity = result.entityHit

            val dmgSrc: DamageSource = (DamageSource.causeMobDamage(this.getThrower()))
            val damage: Int = 10
            entity.attackEntityFrom(dmgSrc, damage.toFloat())
            if (entity is EntityPlayer) entity.addPotionEffect(PotionEffect(MobEffects.SLOWNESS, 120))
        } else {
            this.setDead()
        }
    }

    override fun getBrightness(): Float {
        return 1.0f
    }

    @SideOnly(Side.CLIENT)
    override fun getBrightnessForRender(): Int {
        return 15728880
    }
}
