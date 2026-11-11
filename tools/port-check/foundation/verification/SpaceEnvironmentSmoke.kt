package foundation.verification

import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import asmodeuscore.core.astronomy.dimension.world.data.MeteoricRainSaveData
import asmodeuscore.core.command.CommandDustStormToggle
import asmodeuscore.core.command.CommandFrozenStormToggle
import asmodeuscore.core.command.CommandMeteoricRainToggle
import com.google.gson.JsonParser
import com.mojang.brigadier.CommandDispatcher
import com.mojang.serialization.JsonOps
import foundation.level.FoundationAtmosphere
import foundation.level.FoundationSpaceEnvironment
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.HolderLookup
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.Identifier
import net.minecraft.server.permissions.PermissionSet
import net.minecraft.util.RandomSource
import net.minecraft.world.attribute.AttributeType
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.EnvironmentAttributeMap
import net.minecraft.world.attribute.EnvironmentAttributeSystem
import net.minecraft.world.attribute.EnvironmentAttributes
import net.minecraft.world.level.dimension.DimensionType
import net.minecraftforge.registries.RegistryManager
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional

fun verifySpaceEnvironment(resources: Path, lookup: HolderLookup.Provider) {
    fun <T : Any> register(registry: MappedRegistry<T>, value: T) {
        val forge = RegistryManager.ACTIVE.getRegistry<T>(registry.key())
        registry.unfreeze(); forge?.unfreeze()
        if (forge != null) forge.register(FoundationSpaceEnvironment.ID, value)
        else Registry.register(registry, FoundationSpaceEnvironment.ID, value)
        forge?.freeze(); registry.freeze()
    }
    register(BuiltInRegistries.ATTRIBUTE_TYPE as MappedRegistry<AttributeType<*>>, FoundationSpaceEnvironment.TYPE)
    register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE as MappedRegistry<EnvironmentAttribute<*>>, FoundationSpaceEnvironment.ATTRIBUTE)
    check(FoundationSpaceEnvironment.ATTRIBUTE.isSyncable && !FoundationSpaceEnvironment.ATTRIBUTE.isPositional)
    val defaults = EnvironmentAttributeSystem.builder().build().getDimensionValue(FoundationSpaceEnvironment.ATTRIBUTE)
    check(defaults.body.isEmpty && !defaults.hasWeather)
    val legacy = AtmosphereInfo(null, true, false, -2f, 0.3f, 0.1f)
    legacy.composition.add(EnumAtmosphericGas.OXYGEN)
    check(legacy.isBreathable())
    val snapshot = FoundationAtmosphere.from(legacy)
    legacy.composition.add(EnumAtmosphericGas.CO2)
    check(!legacy.isBreathable() && snapshot.breathable && EnumAtmosphericGas.CO2 !in snapshot.gases)
    val forced = AtmosphereInfo(true, false, true, 5f, 0f, 54f)
    forced.composition.add(EnumAtmosphericGas.CO2)
    check(FoundationAtmosphere.from(forced).breathable)
    val snapshotJson = FoundationAtmosphere.CODEC.encodeStart(JsonOps.INSTANCE, snapshot).getOrThrow()
    check(FoundationAtmosphere.CODEC.parse(JsonOps.INSTANCE, snapshotJson).getOrThrow() == snapshot)
    check(runCatching { (snapshot.gases as MutableList).add(EnumAtmosphericGas.ARGON) }.isFailure)
    check(FoundationAtmosphere.soundReduction(-1f) == 20f && FoundationAtmosphere.soundReduction(0f) == 20f)
    check(FoundationAtmosphere.soundReduction(0.1f) == 10f && FoundationAtmosphere.soundReduction(1f) == 1f)
    check(FoundationAtmosphere.soundReduction(5f) == 0.2f && FoundationAtmosphere.soundReduction(54f) == 0.2f)
    check(FoundationAtmosphere.meteorFrequency(0f) == 5.0)
    check(FoundationAtmosphere.meteorFrequency(0.1f) == 0.1f.toDouble() * 100.0)
    for (gas in EnumAtmosphericGas.entries) {
        val encoded = EnumAtmosphericGas.CODEC.encodeStart(JsonOps.INSTANCE, gas).getOrThrow()
        check(EnumAtmosphericGas.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow() == gas)
    }
    val ops = RegistryOps.create(JsonOps.INSTANCE, withPlanetClocks(lookup, planetClockRegistry(resources, lookup)))
    val weatherProfile = FoundationSpaceEnvironment(Optional.of(Identifier.parse("galaxyspace:weather_fixture")),
        "Legacy storm save name", snapshot, true)
    val weatherMap = EnvironmentAttributeMap.builder().set(FoundationSpaceEnvironment.ATTRIBUTE, weatherProfile).build()
    val weatherPacket = EnvironmentAttributeMap.NETWORK_CODEC.encodeStart(ops, weatherMap).getOrThrow()
    val weatherReceived = EnvironmentAttributeMap.NETWORK_CODEC.parse(ops, weatherPacket).getOrThrow()
    val weatherSystem = EnvironmentAttributeSystem.builder().addConstantLayer(weatherReceived).build()
    check(weatherSystem.getDimensionValue(FoundationSpaceEnvironment.ATTRIBUTE) == weatherProfile)
    for ((namespace, name, density, temperature) in listOf(
        Profile("galacticraftcore", "moon", 0f, 0f), Profile("galacticraftplanets", "mars", 0.1f, -1f),
        Profile("galacticraftplanets", "venus", 54f, 5f), Profile("galacticraftplanets", "asteroids", 0f, -1.5f)
    )) {
        val file = resources.resolve("data/$namespace/dimension_type/$name.json")
        val json = JsonParser.parseString(Files.readString(file))
        val type = DimensionType.DIRECT_CODEC.parse(ops, json).getOrThrow()
        check(type.minY() == 0 && type.height() == 256 && type.logicalHeight() == 256)
        check(type.hasSkyLight() && !type.hasCeiling() && !type.hasEnderDragonFight() && type.coordinateScale() == 1.0)
        check(!type.hasFixedTime())
        val evaluator = EnvironmentAttributeSystem.builder().addConstantLayer(type.attributes()).build()
        val profile = evaluator.getDimensionValue(FoundationSpaceEnvironment.ATTRIBUTE)
        check(profile.body.orElseThrow().toString() == "$namespace:$name" && profile.saveName == name && !profile.hasWeather)
        check(!profile.atmosphere.breathable && profile.atmosphere.density == density && profile.atmosphere.temperature == temperature)
        check(profile.atmosphere.precipitation == (name == "venus") && profile.atmosphere.corrosive == (name == "venus"))
        val bed = evaluator.getDimensionValue(EnvironmentAttributes.BED_RULE)
        check(bed.destroyOnUse() && bed.canSleep().serializedName == "never" && bed.canSetSpawn().serializedName == "never")
        val networkJson = DimensionType.NETWORK_CODEC.encodeStart(ops, type).getOrThrow()
        val received = DimensionType.NETWORK_CODEC.parse(ops, networkJson).getOrThrow()
        check(!received.attributes().contains(EnvironmentAttributes.BED_RULE))
        val client = EnvironmentAttributeSystem.builder().addConstantLayer(received.attributes()).build()
        check(client.getDimensionValue(FoundationSpaceEnvironment.ATTRIBUTE) == profile)
        val encoded = EnvironmentAttributeMap.CODEC.encodeStart(ops, type.attributes()).getOrThrow()
        check(EnvironmentAttributeMap.CODEC.parse(ops, encoded).getOrThrow() == type.attributes())
    }
    val publicSource = Commands.createCompilationContext(PermissionSet.NO_PERMISSIONS)
    val operator = Commands.createCompilationContext(PermissionSet.ALL_PERMISSIONS)
    repeat(2) {
        val dispatcher = CommandDispatcher<CommandSourceStack>()
        CommandDustStormToggle.register(dispatcher); CommandFrozenStormToggle.register(dispatcher); CommandMeteoricRainToggle.register(dispatcher)
        for (name in listOf("toggleduststorm", "togglefrozenstorm", "togglemeteoricrain")) {
            val command = requireNotNull(dispatcher.root.getChild(name))
            check(!command.requirement.test(publicSource) && command.requirement.test(operator))
            check(dispatcher.parse(name, publicSource).reader.canRead())
            for (input in listOf(name, "$name ignored arguments")) {
                val parsed = dispatcher.parse(input, operator)
                check(parsed.exceptions.isEmpty() && !parsed.reader.canRead() && parsed.context.command != null)
            }
        }
    }
    val random = RandomSource.create(77)
    val dust = DustStormSaveData("test")
    CommandDustStormToggle.toggleWeather(dust)
    check(dust.tickClearWeather == 1 && dust.isDirty)
    repeat(2) { DustStormSaveData.calculateWeather(dust, random) }; check(dust.isDustStorm())
    CommandDustStormToggle.toggleWeather(dust)
    repeat(2) { DustStormSaveData.calculateWeather(dust, random) }; check(!dust.isDustStorm())
    val frozen = FrozenStormSaveData("test")
    CommandFrozenStormToggle.toggleWeather(frozen)
    check(frozen.tickClearWeather == 1 && frozen.isDirty)
    repeat(2) { FrozenStormSaveData.calculateWeather(frozen, random) }; check(frozen.isFrozenStorm())
    CommandFrozenStormToggle.toggleWeather(frozen)
    repeat(2) { FrozenStormSaveData.calculateWeather(frozen, random) }; check(!frozen.isFrozenStorm())
    val meteors = MeteoricRainSaveData("test")
    CommandMeteoricRainToggle.toggleWeather(meteors)
    check(meteors.tickClearWeather == 1 && meteors.isDirty)
    repeat(2) { MeteoricRainSaveData.calculateWeather(meteors, random) }; check(meteors.isMeteoricStorm())
    CommandMeteoricRainToggle.toggleWeather(meteors)
    repeat(2) { MeteoricRainSaveData.calculateWeather(meteors, random) }; check(!meteors.isMeteoricStorm())
    println("Passed native atmosphere registration, immutable gas snapshots, breathing overrides, atmosphere codecs, four native dimension type and network codecs, environment evaluation, and three storm command permission, parse, and saved-weather transition checks")
}

private data class Profile(val namespace: String, val name: String, val density: Float, val temperature: Float)
