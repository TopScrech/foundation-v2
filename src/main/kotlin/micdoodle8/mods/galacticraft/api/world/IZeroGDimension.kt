package micdoodle8.mods.galacticraft.api.world

import net.minecraft.entity.Entity

interface IZeroGDimension {
    fun inFreefall(entity: Entity?): Boolean

    fun setInFreefall(entity: Entity?)
}
