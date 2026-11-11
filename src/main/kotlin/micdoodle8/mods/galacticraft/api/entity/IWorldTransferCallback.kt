package micdoodle8.mods.galacticraft.api.entity

import net.minecraft.world.level.Level as World

interface IWorldTransferCallback {
    fun onWorldTransferred(world: World?)
}
