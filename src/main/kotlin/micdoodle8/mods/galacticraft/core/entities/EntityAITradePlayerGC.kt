package micdoodle8.mods.galacticraft.core.entities

import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container

open class EntityAITradePlayerGC constructor(private val villager: EntityAlienVillager) : EntityAIBase() {
    init {
        this.setMutexBits(5)
    }

    override fun shouldExecute(): Boolean {
        if (!this.villager.isEntityAlive()) {
            return false
        } else if (this.villager.isInWater()) {
            return false
        } else if (!this.villager.onGround) {
            return false
        } else if (this.villager.velocityChanged) {
            return false
        } else {
            val entityplayer: EntityPlayer? = this.villager.getCustomer()
            return if (entityplayer == null) false else (if (this.villager.getDistanceSq(entityplayer) > 16.0) false else entityplayer.openContainer is Container)
        }
    }

    override fun startExecuting() {
        this.villager.getNavigator().clearPath()
    }

    override fun resetTask() {
        this.villager.setCustomer(null as EntityPlayer?)
    }
}
