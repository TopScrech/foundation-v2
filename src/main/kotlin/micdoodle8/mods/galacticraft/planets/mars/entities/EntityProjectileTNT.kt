package micdoodle8.mods.galacticraft.planets.mars.entities

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.projectile.EntityFireball
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.Difficulty as EnumDifficulty
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class EntityProjectileTNT : EntityFireball {
    constructor(par1World: World) : super(par1World) {
        this.setSize(1.0f, 1.0f)
    }

    constructor(
        par1World: World,
        par2EntityLivingBase: EntityLivingBase,
        par3: Double,
        par5: Double,
        par7: Double
    ) : super(par1World, par2EntityLivingBase, par3, par5, par7) {
        this.setSize(1.0f, 1.0f)
    }

    @OnlyIn(Dist.CLIENT)
    constructor(
        par1World: World,
        par2: Double,
        par4: Double,
        par6: Double,
        par8: Double,
        par10: Double,
        par12: Double
    ) : super(par1World, par2, par4, par6, par8, par10, par12) {
        this.setSize(0.3125f, 0.3125f)
    }

    override fun isBurning(): Boolean {
        return false
    }

    override fun onImpact(movingObjectPosition: RayTraceResult) {
        if (!this.world.isRemote) {
            if (movingObjectPosition.entityHit != null && movingObjectPosition.entityHit !is EntityCreeper) {
                var difficulty: Float = 0f
                when (this.world.getDifficulty()) {
                    EnumDifficulty.HARD -> difficulty = 2f
                    EnumDifficulty.NORMAL -> difficulty = 1f
                
else -> {}
}
                movingObjectPosition.entityHit.attackEntityFrom(
                    DamageSource.causeFireballDamage(
                        this,
                        this.shootingEntity
                    ), 6.0f + 3.0f * difficulty
                )
            }

            this.world.newExplosion(
                null as Entity?,
                this.posX,
                this.posY,
                this.posZ,
                1.0f,
                false,
                this.world.getGameRules().getBoolean("mobGriefing")
            )
            this.setDead()
        }
    }

    override fun canBeCollidedWith(): Boolean {
        return true
    }
}
