package micdoodle8.mods.galacticraft.core.client.sounds

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.MusicTicker
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.fml.client.FMLClientHandler
import kotlin.math.min

open class MusicTickerGC constructor(mc: Minecraft) : MusicTicker(mc) {
    override fun update() {
        var musictype: MusicType = this.mc.getAmbientMusicType()
        if (FMLClientHandler.instance().getWorldClient() != null && FMLClientHandler.instance()
                .getWorldClient().provider is IGalacticraftWorldProvider
        ) {
            musictype = (ClientProxyCore.MUSIC_TYPE_MARS)!!
        }

        if (this.currentMusic != null) {
            if (musictype.getMusicLocation().getSoundName() != this.currentMusic.getSoundLocation()) {
                this.mc.getSoundHandler().stopSound(this.currentMusic)
                this.timeUntilNextMusic = MathHelper.getInt(this.rand, 0, musictype.getMinDelay() / 2)
            }

            if (!this.mc.getSoundHandler().isSoundPlaying(this.currentMusic)) {
                this.currentMusic = null
                this.timeUntilNextMusic = min(
                    MathHelper.getInt(this.rand, musictype.getMinDelay(), musictype.getMaxDelay()),
                    this.timeUntilNextMusic
                )
            }
        }

        this.timeUntilNextMusic = min(this.timeUntilNextMusic, musictype.getMaxDelay())

        if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0) {
            this.playMusic(musictype)
        }
    }
}
