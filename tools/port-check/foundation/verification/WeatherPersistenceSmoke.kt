package foundation.verification

import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import asmodeuscore.core.astronomy.dimension.world.data.MeteoricRainSaveData
import foundation.persistence.FoundationSavedData
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.util.RandomSource
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.world.level.storage.SavedDataStorage
import java.nio.file.Files

@Suppress("NULL_FOR_NONNULL_TYPE") // Forge supports null data fixers for custom saved data
fun verifyWeatherPersistence(lookup: HolderLookup.Provider) {
    val dust = DustStormSaveData("mars_stormdust")
    check(dust.tickClearWeather == 72000)
    dust.tickClearWeather = 0
    DustStormSaveData.calculateWeather(dust, RandomSource.create(42))
    check(dust.isDustStorm() && dust.tickDustStorm in 12000..35999 && dust.isDirty)
    dust.tickDustStorm = 0
    dust.stormStrength = 1f
    DustStormSaveData.calculateWeather(dust, RandomSource.create(43))
    check(!dust.isDustStorm() && dust.tickClearWeather in 12000..179999)
    dust.prevStormStrength = 0.2f
    dust.stormStrength = 0.8f
    check(kotlin.math.abs(dust.getStormStrength(0.5f) - 0.5f) < 0.0001f)
    val roundTrip = DustStormSaveData.CODEC.parse(NbtOps.INSTANCE, DustStormSaveData.CODEC.encodeStart(NbtOps.INSTANCE, dust).getOrThrow()).getOrThrow()
    check(roundTrip.writeToNBT(CompoundTag()) == dust.writeToNBT(CompoundTag()))

    val frozen = FrozenStormSaveData("barnarda_frozenstorm")
    check(frozen.tickClearWeather == 120000)
    frozen.tickClearWeather = 0
    FrozenStormSaveData.calculateWeather(frozen, RandomSource.create(44))
    check(frozen.isFrozenStorm() && frozen.tickStorm in 12000..17999)
    frozen.tickStorm = 0
    FrozenStormSaveData.calculateWeather(frozen, RandomSource.create(45))
    check(!frozen.isFrozenStorm() && frozen.tickClearWeather in 12000..179999)
    val frozenRoundTrip = FrozenStormSaveData.CODEC.parse(NbtOps.INSTANCE, FrozenStormSaveData.CODEC.encodeStart(NbtOps.INSTANCE, frozen).getOrThrow()).getOrThrow()
    check(frozenRoundTrip.writeToNBT(CompoundTag()) == frozen.writeToNBT(CompoundTag()))

    val meteors = MeteoricRainSaveData("meteor_planet")
    check(meteors.tickClearWeather == 120000)
    meteors.tickClearWeather = 0
    MeteoricRainSaveData.calculateWeather(meteors, RandomSource.create(46))
    check(meteors.isMeteoricStorm() && meteors.tickMeteoricRain in 6000..11999)
    meteors.tickMeteoricRain = 1
    MeteoricRainSaveData.calculateWeather(meteors, RandomSource.create(47))
    check(!meteors.isMeteoricStorm() && meteors.tickClearWeather in 12000..179999)
    meteors.tickClearWeather = 0
    MeteoricRainSaveData.calculateWeather(meteors, RandomSource.create(48))
    check(meteors.isMeteoricStorm())
    val meteorRoundTrip = MeteoricRainSaveData.CODEC.parse(NbtOps.INSTANCE, MeteoricRainSaveData.CODEC.encodeStart(NbtOps.INSTANCE, meteors).getOrThrow()).getOrThrow()
    check(meteorRoundTrip.writeToNBT(CompoundTag()) == meteors.writeToNBT(CompoundTag()))

    val directory = Files.createTempDirectory("foundation-weather-")
    try {
        val legacyName = "Mars_stormdust"
        val legacyFile = directory.resolve("$legacyName.dat")
        val legacy = CompoundTag().also { it.put("data", dust.writeToNBT(CompoundTag())) }
        NbtIo.writeCompressed(legacy, legacyFile)
        val type = SavedDataType(FoundationSavedData.identifier(legacyName), { DustStormSaveData(legacyName) }, DustStormSaveData.CODEC, null)
        val fixer = DataFixers.getDataFixer()
        val meteorType = SavedDataType(FoundationSavedData.identifier("meteor_planet"), { MeteoricRainSaveData("meteor_planet") }, MeteoricRainSaveData.CODEC, null)
        SavedDataStorage(directory, fixer, lookup).use { storage -> storage.set(meteorType, meteors) }
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            val loaded = requireNotNull(storage.get(meteorType))
            check(loaded.writeToNBT(CompoundTag()) == meteors.writeToNBT(CompoundTag()))
        }
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            val loaded = FoundationSavedData.load(storage, directory, legacyName, type)
            check(loaded.writeToNBT(CompoundTag()) == dust.writeToNBT(CompoundTag()))
            check(loaded === FoundationSavedData.load(storage, directory, legacyName, type))
            loaded.tickClearWeather = 1234
            loaded.setDirty()
        }
        check(Files.exists(legacyFile))
        check(Files.exists(type.id().withSuffix(".dat").resolveAgainst(directory)))
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            // Modern saved values take precedence over the retained legacy file
            val loaded = FoundationSavedData.load(storage, directory, legacyName, type)
            check(loaded.tickClearWeather == 1234)
        }
        val malformedName = "broken_stormdust"
        val malformedType = type.copyForName(malformedName)
        val malformedFile = malformedType.id().withSuffix(".dat").resolveAgainst(directory)
        Files.createDirectories(malformedFile.parent)
        val brokenBytes = byteArrayOf(1, 2, 3, 4)
        Files.write(malformedFile, brokenBytes)
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            check(runCatching { FoundationSavedData.load(storage, directory, malformedName, malformedType) }.isFailure)
        }
        check(Files.readAllBytes(malformedFile).contentEquals(brokenBytes))
        val missingPayload = directory.resolve("missing_payload.dat")
        NbtIo.writeCompressed(CompoundTag(), missingPayload)
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            check(runCatching { FoundationSavedData.load(storage, directory, "missing_payload", type.copyForName("missing_payload")) }.isFailure)
        }
        val other = directory.resolve("other-dimension")
        SavedDataStorage(other, fixer, lookup).use { storage ->
            check(FoundationSavedData.load(storage, other, legacyName, type).tickClearWeather == 72000)
        }
        check(FoundationSavedData.identifier("Mars") != FoundationSavedData.identifier("mars"))
        check(FoundationSavedData.identifier("a/b") != FoundationSavedData.identifier("a_b"))
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            check(runCatching { FoundationSavedData.load(storage, directory, "../outside", type.copyForName("unsafe")) }.isFailure)
        }
    } finally {
        Files.walk(directory).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach(Files::delete) }
    }
}

@Suppress("NULL_FOR_NONNULL_TYPE")
private fun SavedDataType<DustStormSaveData>.copyForName(name: String) = SavedDataType(
    FoundationSavedData.identifier(name), constructor(), codec(), null
)
