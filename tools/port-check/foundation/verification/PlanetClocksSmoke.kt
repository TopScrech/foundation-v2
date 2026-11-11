package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import com.mojang.serialization.Lifecycle
import foundation.level.FoundationPlanetClocks
import foundation.level.FoundationSpaceEnvironment
import io.netty.buffer.Unpooled
import net.minecraft.client.ClientClockManager
import net.minecraft.core.HolderLookup
import net.minecraft.core.MappedRegistry
import net.minecraft.core.RegistrationInfo
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.RegistryOps
import net.minecraft.world.attribute.EnvironmentAttributeSystem
import net.minecraft.world.attribute.EnvironmentAttributes
import net.minecraft.world.attribute.WeatherAttributes
import net.minecraft.world.clock.ClockNetworkState
import net.minecraft.world.clock.ClockState
import net.minecraft.world.clock.PackedClockStates
import net.minecraft.world.clock.ServerClockManager
import net.minecraft.world.clock.WorldClock
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.phys.Vec3
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import java.util.stream.Stream
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

fun planetClockRegistry(resources: Path, lookup: HolderLookup.Provider): MappedRegistry<WorldClock> {
    val registry = MappedRegistry(Registries.WORLD_CLOCK, Lifecycle.stable())
    lookup.lookupOrThrow(Registries.WORLD_CLOCK).listElements().forEach { holder -> registry.register(holder.unwrapKey().orElseThrow(), holder.value(), RegistrationInfo.BUILT_IN) }
    for (id in planetClockNames) {
        val file = resources.resolve("data/${id.namespace}/world_clock/${id.path}.json")
        val clock = WorldClock.DIRECT_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(file))).getOrThrow()
        registry.register(ResourceKey.create(Registries.WORLD_CLOCK, id), clock, RegistrationInfo.BUILT_IN)
    }
    registry.freeze()
    return registry
}

fun withPlanetClocks(lookup: HolderLookup.Provider, clocks: MappedRegistry<WorldClock>): HolderLookup.Provider =
    HolderLookup.Provider.create(Stream.concat(lookup.listRegistries().filter { it.key() != Registries.WORLD_CLOCK }, Stream.of(clocks)))

private val planetClockNames = listOf("galacticraftcore:moon", "galacticraftplanets:mars", "galacticraftplanets:venus", "galacticraftplanets:asteroids").map(Identifier::parse)

fun verifyPlanetClocks(resources: Path, lookup: HolderLookup.Provider) {
    // Golden result generated with the cached 1.12 Forge MathHelper in an isolated JVM
    var hash = 1L
    var cases = 0
    val periods = listOf(192000L, 24660L, 720000L)
    for ((index, period) in periods.withIndex()) {
        fun checkTime(time: Long) {
            for (partial in listOf(0f, 0.25f, 0.5f, 1f)) {
                val angle = FoundationPlanetClocks.celestialAngle(time, partial, period)
                hash = hash * 31 + angle.toBits()
                val stars = FoundationPlanetClocks.starBrightness(angle, if (index == 0) 0.5f else 0.75f, if (index == 0) 0.3f else 0f)
                hash = hash * 31 + stars.toBits()
                cases++
            }
        }
        for (time in 0L until period) checkTime(time)
        for (time in listOf(-1L, -24001L, Long.MIN_VALUE + 1, Long.MAX_VALUE)) checkTime(time)
    }
    check(cases == 3746688 && hash == -8729700543857642052L) { "Legacy planet clock mismatch: $cases / $hash" }
    check(runCatching { FoundationPlanetClocks.celestialAngle(0, 0f, 0) }.isFailure)
    val clocks = planetClockRegistry(resources, lookup)
    val provider = withPlanetClocks(lookup, clocks)
    val ops = RegistryOps.create(JsonOps.INSTANCE, provider)
    val instances = planetClockNames.associate { id ->
        clocks.get(id).orElseThrow() to ServerClockManager.ServerClockInstance()
    }
    for ((index, id) in planetClockNames.withIndex()) {
        val typeJson = JsonParser.parseString(Files.readString(resources.resolve("data/${id.namespace}/dimension_type/${id.path}.json")))
        val type = DimensionType.DIRECT_CODEC.parse(ops, typeJson).getOrThrow()
        val holder = type.defaultClock().orElseThrow()
        check(holder == clocks.get(id).orElseThrow())
        val profile = type.attributes().applyModifier(FoundationSpaceEnvironment.ATTRIBUTE, FoundationSpaceEnvironment.DEFAULT)
        val instance = requireNotNull(instances[holder])
        if (index == 3) {
            check(profile.dayLength == 0L)
            val fixed = EnvironmentAttributeSystem.builder().addConstantLayer(type.attributes()).build()
            check(fixed.getValue(EnvironmentAttributes.SUN_ANGLE, Vec3.ZERO) == 90f)
            check(fixed.getValue(EnvironmentAttributes.MOON_ANGLE, Vec3.ZERO) == 270f)
            check(fixed.getValue(EnvironmentAttributes.STAR_BRIGHTNESS, Vec3.ZERO) == 1f)
            check(!type.hasFixedTime() && (15f - fixed.getDimensionValue(EnvironmentAttributes.SKY_LIGHT_LEVEL)).toInt() < 4)
            continue
        }
        check(profile.dayLength == periods[index])
        val builder = EnvironmentAttributeSystem.builder().addConstantLayer(type.attributes())
        FoundationPlanetClocks.addLayers(builder, profile) { instance }
        val system = builder.build()
        for ((tick, expectedSun, expectedMoon, expectedStars, skyLight) in listOf(
            Sample(profile.dayLength / 4, 0f, 180f, profile.starOffset, 15f),
            Sample(profile.dayLength * 3 / 4, 180f, 0f, profile.starScale + profile.starOffset, 4f)
        )) {
            instance.loadFrom(ClockState(tick, 0f, 1f, false)); system.invalidateTickCache()
            check(system.getValue(EnvironmentAttributes.SUN_ANGLE, Vec3.ZERO) == expectedSun)
            check(system.getValue(EnvironmentAttributes.MOON_ANGLE, Vec3.ZERO) == expectedMoon)
            check(system.getValue(EnvironmentAttributes.STAR_BRIGHTNESS, Vec3.ZERO) == expectedStars)
            check(system.getDimensionValue(EnvironmentAttributes.SKY_LIGHT_LEVEL) == skyLight)
        }
        instance.loadFrom(ClockState(123456789012L + index, 0.5f, 0.5f, false))
        instance.tick()
        check(instance.totalTicks() == 123456789013L + index && instance.partialTick() == 0f)
        val saved = instance.packState()
        val reloaded = ServerClockManager.ServerClockInstance()
        reloaded.loadFrom(ClockState.CODEC.parse(JsonOps.INSTANCE, ClockState.CODEC.encodeStart(JsonOps.INSTANCE, saved).getOrThrow()).getOrThrow())
        check(reloaded.packState() == saved)
        val weatherBuilder = EnvironmentAttributeSystem.builder().addConstantLayer(type.attributes())
        FoundationPlanetClocks.addLayers(weatherBuilder, profile) { instance }
        FoundationPlanetClocks.addWeather(weatherBuilder, profile, object : WeatherAttributes.WeatherAccess {
            override fun rainLevel(): Float = 1f
            override fun thunderLevel(): Float = 0f
        })
        instance.loadFrom(ClockState(profile.dayLength * 3 / 4, 0f, 1f, false))
        val wet = weatherBuilder.build().getValue(EnvironmentAttributes.STAR_BRIGHTNESS, Vec3.ZERO)
        check(wet == if (profile.atmosphere.precipitation) 0f else profile.starScale + profile.starOffset)
    }
    val first = instances.values.first()
    first.loadFrom(ClockState(42, 0.25f, 1f, true)); first.tick()
    check(first.totalTicks() == 42L && first.partialTick() == 0.25f && first.isPaused)
    val others = instances.values.drop(1).map { it.packState() }
    first.loadFrom(ClockState(9000, 0f, 1f, false))
    check(instances.values.drop(1).map { it.packState() } == others)
    instances.values.last().loadFrom(ClockState(37, 0.25f, 3f, true))

    val access = RegistryAccess.ImmutableRegistryAccess(listOf(clocks))
    val states = PackedClockStates(instances.mapValues { it.value.packState() })
    val nbtOps = RegistryOps.create(NbtOps.INSTANCE, access)
    val encoded = PackedClockStates.CODEC.encodeStart(nbtOps, states).getOrThrow()
    val directory = Files.createTempDirectory("foundation-clock-")
    try {
        val file = directory.resolve("clocks.dat")
        NbtIo.writeCompressed(encoded as net.minecraft.nbt.CompoundTag, file)
        val loaded = PackedClockStates.CODEC.parse(nbtOps, NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap())).getOrThrow()
        check(loaded == states)
    } finally { Files.walk(directory).sorted(Comparator.reverseOrder()).use { it.forEach(Files::delete) } }
    val packet = ClientboundSetTimePacket(300, instances.mapValues { (_, instance) ->
        ClockNetworkState(instance.totalTicks(), instance.partialTick(), if (instance.isPaused) 0f else instance.rate())
    })
    val bytes = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
    try {
        ClientboundSetTimePacket.STREAM_CODEC.encode(bytes, packet)
        val received = ClientboundSetTimePacket.STREAM_CODEC.decode(bytes)
        check(received == packet && !bytes.isReadable)
        val client = ClientClockManager()
        client.handleUpdates(received.gameTime(), received.clockUpdates())
        for ((holder, state) in received.clockUpdates()) {
            check(client.getInstance(holder).totalTicks() == state.totalTicks())
            check(client.getInstance(holder).partialTick() == state.partialTick())
            check(client.getInstance(holder).isPaused == (state.rate() == 0f))
        }
        client.tick(310)
        for ((holder, state) in received.clockUpdates()) check(client.getInstance(holder).totalTicks() == state.totalTicks() + (state.rate() * 10).toLong())
    } finally { bytes.release() }
    val target = EnvironmentAttributeSystem.Builder::class.java.getDeclaredMethod("addDynamicLayers", net.minecraft.world.level.Level::class.java)
    check(target.returnType == EnvironmentAttributeSystem.Builder::class.java)
    val node = ClassNode()
    val mixinBytes = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("foundation/mixin/PlanetClockLayersMixin.class")).use { it.readAllBytes() }
    ClassReader(mixinBytes).accept(node, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
    val injection = node.methods.single { it.name == "foundationPlanetTime" }
    check(injection.desc == "(Lnet/minecraft/world/level/Level;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V")
    val annotation = (injection.visibleAnnotations.orEmpty() + injection.invisibleAnnotations.orEmpty()).single { it.desc == "Lorg/spongepowered/asm/mixin/injection/Inject;" }
    val options = annotation.values.chunked(2).associate { it[0] as String to it[1] }
    check(options["method"] == listOf("addDynamicLayers") && options["cancellable"] == true && options["remap"] == false)
    println("Passed $cases legacy planet angle and star samples, four native clock declarations, native dynamic environment evaluation, clock rates and pausing, independent states, compressed NBT clock data, native time packets, and client clock updates")
}

private data class Sample(val tick: Long, val sun: Float, val moon: Float, val stars: Float, val skyLight: Float)
