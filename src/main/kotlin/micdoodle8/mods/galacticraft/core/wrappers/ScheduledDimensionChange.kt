package micdoodle8.mods.galacticraft.core.wrappers

import net.minecraft.entity.player.EntityPlayerMP

open class ScheduledDimensionChange
constructor(private var player: EntityPlayerMP?, private var dimensionName: String?) {
    private var dimensionId: Int? = null

    constructor(player: EntityPlayerMP?, dimensionId: Int) : this(player, null) {
        this.dimensionId = dimensionId
    }

    fun getDimensionId(): Int? = dimensionId

    open fun getPlayer(): EntityPlayerMP? {
        return player
    }

    open fun getDimensionName(): String? {
        return dimensionName
    }

    open fun setPlayer(player: EntityPlayerMP?) {
        this.player = player
    }

    open fun setDimensionName(dimensionName: String?) {
        this.dimensionName = dimensionName
    }
}
