package micdoodle8.mods.galacticraft.api.entity

import net.minecraft.client.audio.ISound
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.ITickable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * Implement into entities that make a sound all the time, like rockets
 */
interface IEntityNoisy {
    @SideOnly(Side.CLIENT)
    fun getSoundUpdater(): ITickable?

    @SideOnly(Side.CLIENT)
    fun setSoundUpdater(player: EntityPlayerSP?): ISound?
}
