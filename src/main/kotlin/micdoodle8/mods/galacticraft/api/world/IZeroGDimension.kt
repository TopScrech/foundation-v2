package micdoodle8.mods.galacticraft.api.world

import net.minecraft.world.entity.Entity

interface IZeroGDimension {
    fun inFreefall(entity: Entity?): Boolean

    fun setInFreefall(entity: Entity?)
}
