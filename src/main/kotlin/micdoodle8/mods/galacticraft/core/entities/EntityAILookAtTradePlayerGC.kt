package micdoodle8.mods.galacticraft.core.entities

import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.world.entity.player.Player as EntityPlayer

open class EntityAILookAtTradePlayerGC constructor(private val theMerchant: EntityAlienVillager) :
    EntityAIWatchClosest(theMerchant, EntityPlayer::class.java, 8.0f) {
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    override fun shouldExecute(): Boolean {
        if (this.theMerchant.isTrading()) {
            this.closestEntity = this.theMerchant.getCustomer()
            return true
        } else {
            return false
        }
    }
}
