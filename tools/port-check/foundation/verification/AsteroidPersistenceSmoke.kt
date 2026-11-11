package foundation.verification

import foundation.persistence.FoundationSavedData
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.KuiperBeltSaveData
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.AsteroidSaveData
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.world.level.storage.SavedDataStorage
import java.nio.file.Files

@Suppress("NULL_FOR_NONNULL_TYPE")
fun verifyAsteroidPersistence(lookup: HolderLookup.Provider) {
    val asteroid = AsteroidSaveData("")
    val record = CompoundTag().also {
        it.putInt("x", -143)
        it.putInt("y", -32)
        it.putInt("z", 81)
        it.putInt("coreAndFlag", 0x1005)
        it.putInt("sizeAndFlag", 0x8031)
    }
    asteroid.datacompound.put("coords", ListTag().also { it.add(record) })
    val encoded = AsteroidSaveData.CODEC.encodeStart(NbtOps.INSTANCE, asteroid).getOrThrow()
    val roundTrip = AsteroidSaveData.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow()
    check(roundTrip.datacompound == asteroid.datacompound)
    val kuiper = KuiperBeltSaveData("").also { it.datacompound = asteroid.datacompound.copy() }
    val kuiperEncoded = KuiperBeltSaveData.CODEC.encodeStart(NbtOps.INSTANCE, kuiper).getOrThrow()
    check(KuiperBeltSaveData.CODEC.parse(NbtOps.INSTANCE, kuiperEncoded).getOrThrow().datacompound == asteroid.datacompound)

    val root = Files.createTempDirectory("foundation-asteroids-")
    try {
        val directory = root.resolve("data")
        val legacyDirectory = root.resolve("galacticraft")
        Files.createDirectories(legacyDirectory)
        val legacyFile = legacyDirectory.resolve("GCAsteroidData.dat")
        val legacyTag = CompoundTag().also { it.put("data", encoded) }
        NbtIo.writeCompressed(legacyTag, legacyFile)
        val type = SavedDataType(FoundationSavedData.identifier(AsteroidSaveData.saveDataID), { AsteroidSaveData("") }, AsteroidSaveData.CODEC, null)
        val kuiperType = SavedDataType(FoundationSavedData.identifier(KuiperBeltSaveData.saveDataID), { KuiperBeltSaveData("") }, KuiperBeltSaveData.CODEC, null)
        val fixer = DataFixers.getDataFixer()
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            val loaded = FoundationSavedData.load(storage, directory, AsteroidSaveData.saveDataID, type)
            check(loaded.datacompound == asteroid.datacompound)
            check(loaded === FoundationSavedData.load(storage, directory, AsteroidSaveData.saveDataID, type))
            val separate = FoundationSavedData.load(storage, directory, KuiperBeltSaveData.saveDataID, kuiperType)
            check(separate.datacompound.isEmpty)
            separate.datacompound = kuiper.datacompound.copy()
            separate.setDirty()
            loaded.datacompound.putInt("migrated", 1)
            loaded.setDirty()
        }
        check(Files.exists(legacyFile))
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            val loaded = FoundationSavedData.load(storage, directory, AsteroidSaveData.saveDataID, type)
            check(loaded.datacompound.getIntOr("migrated", 0) == 1)
            check(loaded.datacompound.getListOrEmpty("coords").getCompoundOrEmpty(0) == record)
            val loadedKuiper = FoundationSavedData.load(storage, directory, KuiperBeltSaveData.saveDataID, kuiperType)
            check(loadedKuiper.datacompound == kuiper.datacompound)
        }
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            for (name in listOf("../outside", "../galacticraft/../../outside")) {
                val unsafeType = SavedDataType(FoundationSavedData.identifier(name), { AsteroidSaveData("") }, AsteroidSaveData.CODEC, null)
                check(runCatching { FoundationSavedData.load(storage, directory, name, unsafeType) }.isFailure)
            }
        }
    } finally {
        Files.walk(root).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach(Files::delete) }
    }
}
