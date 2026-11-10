package micdoodle8.mods.galacticraft.core.entities

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.IRangedAttackMob
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.util.math.MathHelper

open class EntityAIArrowAttack constructor(
    par1IRangedAttackMob: IRangedAttackMob?,
    par2: Double,
    par4: Int,
    par5: Int,
    par6: Float
) : EntityAIBase() {
    private val entityHost: EntityLiving

    private val rangedAttackEntityHost: IRangedAttackMob
    private var attackTarget: EntityLivingBase? = null

    private var rangedAttackTime: Int
    private var entityMoveSpeed = 0.0
    private var field_96561_g = 0

    private var maxRangedAttackTime = 0
    private var field_96562_i = 0f
    private var field_82642_h = 0f

    constructor(par1IRangedAttackMob: IRangedAttackMob?, par2: Double, par4: Int, par5: Float) : this(
        par1IRangedAttackMob,
        par2,
        par4,
        par4,
        par5
    )

    init {
        this.rangedAttackTime = -1

        require(par1IRangedAttackMob is EntityLivingBase) { "ArrowAttackGoal requires Mob implements RangedAttackMob" }
        this.rangedAttackEntityHost = par1IRangedAttackMob
        this.entityHost = par1IRangedAttackMob as EntityLiving
        this.entityMoveSpeed = par2
        this.field_96561_g = par4
        this.maxRangedAttackTime = par5
        this.field_96562_i = par6
        this.field_82642_h = par6 * par6
        this.setMutexBits(3)
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    override fun shouldExecute(): Boolean {
        val entitylivingbase: EntityLivingBase? = this.entityHost.getAttackTarget()

        if (entitylivingbase == null) {
            return false
        } else {
            this.attackTarget = entitylivingbase
            return true
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    override fun shouldContinueExecuting(): Boolean {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath()
    }

    /**
     * Resets the task
     */
    override fun resetTask() {
        this.attackTarget = null
        this.rangedAttackTime = -1
    }

    /**
     * Updates the task
     */
    override fun updateTask() {
        val d0: Double = this.entityHost.getDistanceSq(
            this.attackTarget!!.posX,
            this.attackTarget!!.getEntityBoundingBox().minY,
            this.attackTarget!!.posZ
        )
        val flag: Boolean = this.entityHost.getEntitySenses().canSee(this.attackTarget)

        this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed)

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0f, 30.0f)
        val f: Float

        if (--this.rangedAttackTime == 0) {
            if (d0 > this.field_82642_h || !flag) {
                return
            }

            f = MathHelper.sqrt(d0) / this.field_96562_i
            var f1: Float = f

            if (f < 0.1f) {
                f1 = 0.1f
            }

            if (f1 > 1.0f) {
                f1 = 1.0f
            }

            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, f1)
            this.rangedAttackTime =
                MathHelper.floor(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g)
        } else if (this.rangedAttackTime < 0) {
            f = MathHelper.sqrt(d0) / this.field_96562_i
            this.rangedAttackTime =
                MathHelper.floor(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g)
        }
    }
}
