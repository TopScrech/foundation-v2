package micdoodle8.mods.galacticraft.api.item

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.world.entity.player.Player as EntityPlayer

interface IHoldableItemCustom : IHoldableItem {
    fun getLeftHandRotation(player: EntityPlayer?): Vector3?

    fun getRightHandRotation(player: EntityPlayer?): Vector3?
}
