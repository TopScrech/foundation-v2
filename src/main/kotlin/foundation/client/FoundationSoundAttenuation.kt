package foundation.client

import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.resources.sounds.TickableSoundInstance
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import java.util.concurrent.CompletableFuture

/** Reduce the selected sound without replaying it, resolving it twice, or losing its runtime behavior */
object FoundationSoundAttenuation {
    fun reduce(sound: SoundInstance, reduction: Float): SoundInstance {
        if (sound.attenuation == SoundInstance.Attenuation.NONE) return sound
        val divisor = maxOf(0.01f, reduction)
        return if (sound is TickableSoundInstance) ReducedTickable(sound, divisor) else Reduced(sound, divisor)
    }

    private class Reduced(private val original: SoundInstance, private val divisor: Float) : SoundInstance by original {
        override fun getVolume(): Float = original.volume / divisor
        override fun canStartSilent(): Boolean = original.canStartSilent()
        override fun canPlaySound(): Boolean = original.canPlaySound()
        override fun getStream(buffers: SoundBufferLibrary, sound: Sound, looping: Boolean): CompletableFuture<AudioStream> = original.getStream(buffers, sound, looping)
    }

    private class ReducedTickable(private val original: TickableSoundInstance, private val divisor: Float) : TickableSoundInstance by original {
        override fun getVolume(): Float = original.volume / divisor
        override fun canStartSilent(): Boolean = original.canStartSilent()
        override fun canPlaySound(): Boolean = original.canPlaySound()
        override fun getStream(buffers: SoundBufferLibrary, sound: Sound, looping: Boolean): CompletableFuture<AudioStream> = original.getStream(buffers, sound, looping)
    }
}
