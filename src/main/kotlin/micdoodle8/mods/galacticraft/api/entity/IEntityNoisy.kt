package micdoodle8.mods.galacticraft.api.entity

import net.minecraft.client.resources.sounds.SoundInstance as ISound
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.resources.sounds.TickableSoundInstance as ITickable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist


/**
 * Implement into entities that make a sound all the time, like rockets
 */
interface IEntityNoisy {
    @OnlyIn(Dist.CLIENT)
    fun getSoundUpdater(): ITickable?

    @OnlyIn(Dist.CLIENT)
    fun setSoundUpdater(player: EntityPlayerSP?): ISound?
}
