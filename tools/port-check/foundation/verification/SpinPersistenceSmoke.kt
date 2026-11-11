package foundation.verification

import foundation.level.FoundationDimensionKeys
import foundation.persistence.FoundationSavedData
import micdoodle8.mods.galacticraft.core.dimension.OrbitSpinSaveData
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.world.level.storage.SavedDataStorage
import java.nio.file.Files

@Suppress("NULL_FOR_NONNULL_TYPE")
fun verifySpinPersistence(lookup: HolderLookup.Provider) {
    val first = FoundationDimensionKeys.parse("galacticraftcore:station_a")
    val second = FoundationDimensionKeys.parse("galacticraftcore:station_b")
    val legacy = CompoundTag().also { root ->
        root.put("2", CompoundTag().also { it.putFloat("omegaRad", 0.1f) })
        root.put("3", CompoundTag().also { it.putFloat("omegaRad", -0.2f) })
        root.put("4", CompoundTag().also { it.putFloat("omegaRad", 0.3f) })
        root.putString("unknown", "preserved")
    }
    val data = OrbitSpinSaveData.CODEC.parse(NbtOps.INSTANCE, legacy).getOrThrow()
    val a = data.forDimension(first, 2) { error("Existing station was reinitialized") }
    val b = data.forDimension(second, 3) { error("Existing station was reinitialized") }
    check(a.datacompound !== b.datacompound)
    check(a.datacompound.getFloatOr("omegaRad", 0f) == 0.1f)
    check(b.datacompound.getFloatOr("omegaRad", 0f) == -0.2f)
    a.datacompound.putFloat("omegaRad", 0.4f)
    data.setDirty(false)
    a.setDirty()
    check(data.isDirty)
    check(b.datacompound.getFloatOr("omegaRad", 0f) == -0.2f)
    check(data.forDimension(first, 99).datacompound === a.datacompound)
    val fresh = data.forDimension(FoundationDimensionKeys.parse("galacticraftcore:station_c"), 5) { it.putFloat("omegaRad", 0.5f) }
    check(fresh.datacompound.getFloatOr("omegaRad", 0f) == 0.5f)
    val snapshot = data.writeToNBT(CompoundTag())
    check(snapshot.getCompoundOrEmpty("4") == legacy.getCompoundOrEmpty("4"))
    check(snapshot.getStringOr("unknown", "") == "preserved")
    check(legacy.getCompoundOrEmpty("2").getFloatOr("omegaRad", 0f) == 0.1f)
    val restored = OrbitSpinSaveData.CODEC.parse(NbtOps.INSTANCE, snapshot).getOrThrow()
    check(restored.forDimension(first, 2).datacompound.getFloatOr("omegaRad", 0f) == 0.4f)
    check(restored.forDimension(second, 3).datacompound.getFloatOr("omegaRad", 0f) == -0.2f)

    val root = Files.createTempDirectory("foundation-spin-")
    try {
        val directory = root.resolve("data")
        val legacyDirectory = root.resolve("galacticraft")
        Files.createDirectories(legacyDirectory)
        val file = legacyDirectory.resolve("GCSpinData.dat")
        NbtIo.writeCompressed(CompoundTag().also { it.put("data", legacy) }, file)
        val type = SavedDataType(FoundationSavedData.identifier(OrbitSpinSaveData.saveDataID), { OrbitSpinSaveData("") }, OrbitSpinSaveData.CODEC, null)
        val fixer = DataFixers.getDataFixer()
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            val loaded = FoundationSavedData.load(storage, directory, OrbitSpinSaveData.saveDataID, type)
            loaded.forDimension(first, 2).apply {
                datacompound.putFloat("omegaRad", 0.7f)
                setDirty()
            }
        }
        SavedDataStorage(directory, fixer, lookup).use { storage ->
            val loaded = FoundationSavedData.load(storage, directory, OrbitSpinSaveData.saveDataID, type)
            check(loaded.forDimension(first, 2).datacompound.getFloatOr("omegaRad", 0f) == 0.7f)
            check(loaded.forDimension(second, 3).datacompound.getFloatOr("omegaRad", 0f) == -0.2f)
            check(loaded.writeToNBT(CompoundTag()).getCompoundOrEmpty("4") == legacy.getCompoundOrEmpty("4"))
        }
        check(Files.exists(file))
    } finally {
        Files.walk(root).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach(Files::delete) }
    }
}
