package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import kotlin.math.atan2

open class EntityAIThrowPlayer constructor(@JvmField var skeletonBoss: EntitySkeletonBoss) : EntityAIBase() {
    @JvmField
    var targetPlayer: EntityPlayer? = null

    init {
        this.setMutexBits(1)
    }

    override fun shouldExecute(): Boolean {
        val player: EntityPlayer? = this.skeletonBoss.world.getClosestPlayerToEntity(this.skeletonBoss, 5.0)

        if (player == null) {
            return false
        } else {
            this.targetPlayer = player
            return true
        }
    }

    override fun startExecuting() {
        this.skeletonBoss.setAttackTarget(this.targetPlayer)

        // if (this.skeletonBoss.getDistanceToEntity(this.targetPlayer) <= 5.0F)
        run {
            var d0: Double = this.skeletonBoss.posX - this.targetPlayer!!.x
            var d1: Double

            d1 = this.skeletonBoss.posZ - this.targetPlayer!!.z
            while (d0 * d0 + d1 * d1 < 1.0E-4) {
                d0 = (Math.random() - Math.random()) * 0.01
                d1 = (Math.random() - Math.random()) * 0.01
            }

            this.targetPlayer!!.attackedAtYaw =
                atan2(d1, d0).toFloat() * Constants.RADIANS_TO_DEGREES - this.targetPlayer!!.yRot
            this.targetPlayer!!.knockBack(this.skeletonBoss, 20f, d0, d1)
        }

        super.startExecuting()
    }
}
