package foundation.verification

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.client.FoundationSoundAttenuation
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.resources.sounds.SoundEventRegistration
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.resources.sounds.TickableSoundInstance
import net.minecraft.core.MappedRegistry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryOps
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.JOrbisAudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import net.minecraft.server.packs.resources.ResourceProvider
import java.util.Optional
import java.util.concurrent.CompletableFuture
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import java.nio.file.Files
import java.nio.file.Path

fun verifySounds(resources: Path) {
    val registry = BuiltInRegistries.SOUND_EVENT as MappedRegistry<SoundEvent>
    val forge = ForgeRegistries.SOUND_EVENTS as ForgeRegistry<SoundEvent>
    registry.unfreeze(); forge.unfreeze()
    GCSounds.registerSounds(forge)
    forge.freeze(); registry.freeze()
    check(GCSounds.events.size == 14)
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val ops = RegistryOps.create(JsonOps.INSTANCE, access)
    for ((id, sound) in GCSounds.events) {
        check(BuiltInRegistries.SOUND_EVENT.getValue(id) === sound)
        check(sound.location() == id && sound.fixedRange().isEmpty)
        check(sound.getRange(0.5f) == 16f && sound.getRange(1f) == 16f && sound.getRange(2f) == 32f)
        check(SoundEvent.DIRECT_CODEC.parse(JsonOps.INSTANCE, SoundEvent.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, sound).getOrThrow()).getOrThrow() == sound)
        val direct = Unpooled.buffer()
        try {
            SoundEvent.DIRECT_STREAM_CODEC.encode(direct, sound)
            check(SoundEvent.DIRECT_STREAM_CODEC.decode(direct) == sound && !direct.isReadable)
        } finally { direct.release() }
        val holder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound)
        check(SoundEvent.CODEC.parse(ops, SoundEvent.CODEC.encodeStart(ops, holder).getOrThrow()).getOrThrow().value() === sound)
        val packet = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            SoundEvent.STREAM_CODEC.encode(packet, holder)
            check(SoundEvent.STREAM_CODEC.decode(packet).value() === sound && !packet.isReadable)
        } finally { packet.release() }
    }
    val gson = GsonBuilder().registerTypeAdapter(SoundEventRegistration::class.java, SoundEventRegistrationSerializer()).create()
    val json = JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftcore/sounds.json"))).asJsonObject
    check(GCSounds.events.keys.all { json.has(it.path) })
    var files = 0
    for ((name, entry) in json.entrySet()) {
        val definition = gson.fromJson(entry, SoundEventRegistration::class.java)
        check(!definition.isReplace && definition.subtitle == null && definition.sounds.isNotEmpty())
        for (sound in definition.sounds) {
            check(sound.type == Sound.Type.FILE && sound.weight == 1 && !sound.shouldPreload())
            check(sound.shouldStream() == (name == "galacticraft.music_space"))
            check(sound.attenuationDistance == 16 && sound.getAttenuationDistance(2f) == 32f)
            val path = sound.path
            val file = resources.resolve("assets/${path.namespace}/${path.path}")
            check(Files.exists(file)) { "Missing sound file $path" }
            Files.newInputStream(file).use { check(it.readNBytes(4).contentEquals("OggS".toByteArray())) { "Invalid Ogg header $path" } }
            JOrbisAudioStream(Files.newInputStream(file)).use { audio ->
                check(audio.format.channels in 1..2 && audio.format.sampleRate > 0f)
                var samples = 0
                var packets = 0
                while (samples == 0 && packets++ < 32 && audio.readChunk { sample -> check(sample.isFinite()); samples++ }) { }
                check(samples > 0) { "Native decoder found no samples in $path" }
            }
            files++
        }
    }
    val selected = gson.fromJson(json["entity.astrominer"], SoundEventRegistration::class.java).sounds.single()
    val moving = AttenuationTestSound(selected)
    val reduced = FoundationSoundAttenuation.reduce(moving, 4f) as TickableSoundInstance
    check(reduced.identifier == moving.identifier && reduced.source == SoundSource.AMBIENT)
    check(reduced.sound === selected && reduced.soundEvent === moving.soundEvent)
    check(reduced.volume == 0.25f && reduced.pitch == 0.75f && reduced.isLooping && reduced.delay == 3)
    check(reduced.canStartSilent() && reduced.canPlaySound() && !reduced.isRelative && !reduced.isStopped)
    val buffers = SoundBufferLibrary(ResourceProvider { Optional.empty() })
    check(reduced.getStream(buffers, selected, true) === moving.stream)
    check(moving.streamSound === selected && moving.streamLooping)
    reduced.tick()
    check(reduced.volume == 0.125f && reduced.pitch == 1.25f && reduced.x == 2.0 && reduced.y == 3.0 && reduced.z == 4.0)
    moving.finish()
    check(reduced.isStopped && !reduced.isLooping && !reduced.canPlaySound())
    val ui = SimpleSoundInstance.forUI(GCSounds.parachute, 1f)
    check(FoundationSoundAttenuation.reduce(ui, 4f) === ui)
    val fixed = object : SimpleSoundInstance(GCSounds.astroMiner, SoundSource.BLOCKS, 0.8f, 1f, SoundInstance.createUnseededRandom(), 1.0, 2.0, 3.0) {
        init { sound = selected }
    }
    val quiet = FoundationSoundAttenuation.reduce(fixed, 2f)
    check(quiet !is TickableSoundInstance && quiet.volume == 0.4f && quiet.source == SoundSource.BLOCKS)
    check(quiet.x == 1.0 && quiet.y == 2.0 && quiet.z == 3.0 && quiet.attenuation == fixed.attenuation)
    check(FoundationSoundAttenuation.reduce(fixed, 0f).volume == 80f)
    println("Passed 14 common sound registrations, native sound save and packet codecs, ${json.size()} native sound definitions and $files native Ogg decodes")
    println("Passed native sound attenuation, category and position preservation, non-positional sound bypass, dynamic tick updates, and stop propagation")
}

private class AttenuationTestSound(selected: Sound) : AbstractTickableSoundInstance(GCSounds.astroMiner, SoundSource.AMBIENT, SoundInstance.createUnseededRandom()) {
    val stream = CompletableFuture<AudioStream>()
    var streamSound: Sound? = null
    var streamLooping = false
    init { sound = selected; volume = 1f; pitch = 0.75f; looping = true; delay = 3 }
    override fun tick() { volume = 0.5f; pitch = 1.25f; x = 2.0; y = 3.0; z = 4.0 }
    override fun canStartSilent(): Boolean = true
    override fun canPlaySound(): Boolean = !isStopped
    override fun getStream(buffers: SoundBufferLibrary, sound: Sound, looping: Boolean): CompletableFuture<AudioStream> {
        streamSound = sound; streamLooping = looping
        return stream
    }
    fun finish() = stop()
}
