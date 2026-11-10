package micdoodle8.mods.galacticraft.planets.venus.entities.ai

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityMoveHelper
import net.minecraft.util.math.MathHelper

open class EntityMoveHelperCeiling constructor(entitylivingIn: EntityLiving) : EntityMoveHelper(entitylivingIn) {
    override fun onUpdateMoveHelper() {
        this.entity.setMoveForward(0.0f)

        if (this.isUpdating()) {
            this.action = Action.WAIT
            val i: Int = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5)
            val d0: Double = this.posX - this.entity.posX
            val d1: Double = this.posZ - this.entity.posZ
            val d2: Double = this.posY - i.toDouble()
            val d3: Double = d0 * d0 + d1 * d1

            if (d3 >= 2.500000277905201E-7) {
                val f: Float = MathHelper.atan2(d1, d0).toFloat() * Constants.RADIANS_TO_DEGREES - 90.0f
                this.entity.rotationYaw = f
                this.entity.setAIMoveSpeed(
                    (this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                        .getAttributeValue()).toFloat()
                )

                if (d2 > 0.0 && d0 * d0 + d1 * d1 < 1.0) {
                    this.entity.getJumpHelper().setJumping()
                }
            }
        }
    }
}
