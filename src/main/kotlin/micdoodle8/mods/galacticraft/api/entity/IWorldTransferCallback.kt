package micdoodle8.mods.galacticraft.api.entity

import net.minecraft.world.World

interface IWorldTransferCallback {
    fun onWorldTransferred(world: World?)
}
