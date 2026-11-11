package foundation.verification

import foundation.item.FoundationItemVariants
import foundation.persistence.FoundationSavedData
import micdoodle8.mods.galacticraft.core.command.CommandGCInv
import micdoodle8.mods.galacticraft.core.command.GCInvSaveData
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.permissions.PermissionSet
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.world.level.storage.SavedDataStorage
import java.nio.file.Files
import java.util.Locale

@Suppress("NULL_FOR_NONNULL_TYPE")
fun verifyEquipmentArchives(lookup: HolderLookup.Provider) {
    fun roundTrip(data: GCInvSaveData): GCInvSaveData = GCInvSaveData.CODEC.parse(
        NbtOps.INSTANCE, GCInvSaveData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow()
    ).getOrThrow()
    val inventory = InventoryExtended()
    for (slot in 0 until inventory.containerSize) inventory.setItem(slot, ItemStack(Items.DIAMOND, slot + 1))
    val basic = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:basic_item")))
    inventory.setItem(5, FoundationItemVariants.stack(basic, 3, 19).also {
        it.set(DataComponents.CUSTOM_NAME, Component.literal("Archived frequency module"))
    })
    val original = (0 until inventory.containerSize).map { inventory.getItem(it).copy() }
    val archive = GCInvSaveData()
    archive.save("IUser", inventory, lookup)
    check(inventory.isEmpty && archive.isDirty)
    val beforeLocale = Locale.getDefault()
    try {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"))
        check(archive.hasSaved("iuser") && archive.hasSaved("IUSER"))
    } finally { Locale.setDefault(beforeLocale) }
    check(!archive.restore("IUser", inventory, lookup, automatic = true))
    inventory.setItem(0, ItemStack(Items.GOLD_INGOT))
    check(runCatching { archive.save("IUSER", inventory, lookup) }.isFailure)
    check(inventory.getItem(0).`is`(Items.GOLD_INGOT))
    inventory.clearContent()
    check(archive.queueRestore("IUSER"))
    check(archive.restore("iuser", inventory, lookup, automatic = true))
    check(!archive.hasSaved("IUser") && !archive.restore("IUser", inventory, lookup))
    for (slot in original.indices) check(ItemStack.matches(original[slot], inventory.getItem(slot)))
    archive.save("IUser", inventory, lookup)
    val restarted = roundTrip(archive)
    check(restarted.restore("IUser", inventory, lookup, automatic = true))
    for (slot in original.indices) check(ItemStack.matches(original[slot], inventory.getItem(slot)))
    check(archive.hasSaved("IUser")) // Restarted instance did not mutate the original server's archive
    val exported = archive.writeToNBT(CompoundTag())
    exported.getCompoundOrEmpty("foundation:equipment_archives").remove("iuser")
    check(archive.hasSaved("IUser"))

    val legacy = CompoundTag().also { root ->
        root.putString("unknown", "preserved")
        root.put("LegacyPlayer", ListTag().also { list ->
            list.add(CompoundTag().also {
                it.putString("id", "minecraft:diamond"); it.putByte("Count", 2); it.putByte("Slot", 200.toByte())
            })
            list.add(CompoundTag().also {
                it.putString("id", "galacticraftcore:basic_item"); it.putByte("Count", 1)
                it.putShort("Damage", 19); it.putByte("Slot", 205.toByte())
            })
        })
        root.put("UnloadedPlayer", ListTag())
    }
    val imported = GCInvSaveData.CODEC.parse(NbtOps.INSTANCE, legacy).getOrThrow()
    val target = InventoryExtended().also { it.setItem(10, ItemStack(Items.EMERALD)) }
    check(imported.restore("legacyplayer", target, lookup))
    check(target.getItem(0).`is`(Items.DIAMOND) && target.getItem(0).count == 2)
    check(FoundationItemVariants.metadata(target.getItem(5)) == 19)
    check((1..4).all { target.getItem(it).isEmpty })
    check(target.getItem(10).`is`(Items.EMERALD)) // Old six-slot files did not contain later equipment slots
    val preserved = imported.writeToNBT(CompoundTag())
    check(preserved.getStringOr("unknown", "") == "preserved" && preserved.getList("UnloadedPlayer").isPresent)
    check(legacy.contains("LegacyPlayer"))

    val validRecord = archive.writeToNBT(CompoundTag())
    fun rejectUnchanged(bad: CompoundTag) {
        val data = GCInvSaveData.CODEC.parse(NbtOps.INSTANCE, bad).getOrThrow()
        val live = InventoryExtended().also { it.setItem(0, ItemStack(Items.EMERALD, 7)) }
        val snapshot = data.writeToNBT(CompoundTag())
        check(runCatching { data.restore("IUser", live, lookup) }.isFailure)
        check(data.writeToNBT(CompoundTag()) == snapshot && live.getItem(0).count == 7 && live.getItem(0).`is`(Items.EMERALD))
    }
    val duplicate = validRecord.copy()
    val duplicateItems = duplicate.getCompoundOrEmpty("foundation:equipment_archives").getCompoundOrEmpty("iuser").getList("Items").orElseThrow()
    duplicateItems.add(duplicateItems.getCompoundOrEmpty(0).copy())
    rejectUnchanged(duplicate)
    val invalidItem = validRecord.copy()
    val badItems = invalidItem.getCompoundOrEmpty("foundation:equipment_archives").getCompoundOrEmpty("iuser").getList("Items").orElseThrow()
    badItems.getCompoundOrEmpty(1).putString("id", "foundation:unregistered_item")
    rejectUnchanged(invalidItem)
    val futureSize = validRecord.copy()
    futureSize.getCompoundOrEmpty("foundation:equipment_archives").getCompoundOrEmpty("iuser").putInt("Size", 64)
    rejectUnchanged(futureSize)
    val badLegacy = legacy.copy()
    badLegacy.getList("LegacyPlayer").orElseThrow().getCompoundOrEmpty(0).putByte("Slot", 207.toByte())
    val invalidLegacy = GCInvSaveData.CODEC.parse(NbtOps.INSTANCE, badLegacy).getOrThrow()
    check(runCatching { invalidLegacy.restore("LegacyPlayer", InventoryExtended(), lookup) }.isFailure)
    check(invalidLegacy.writeToNBT(CompoundTag()) == badLegacy)
    check(!GCInvSaveData().queueRestore("missing"))

    val root = Files.createTempDirectory("foundation-equipment-archive-")
    try {
        val directory = root.resolve("data")
        val legacyDirectory = root.resolve("galacticraft")
        Files.createDirectories(legacyDirectory)
        val legacyFile = legacyDirectory.resolve("GCInv_savefile.dat")
        NbtIo.writeCompressed(CompoundTag().also { it.put("data", legacy) }, legacyFile)
        val type = SavedDataType(FoundationSavedData.identifier(GCInvSaveData.SAVE_ID), ::GCInvSaveData, GCInvSaveData.CODEC, null)
        SavedDataStorage(directory, DataFixers.getDataFixer(), lookup).use { storage ->
            val data = FoundationSavedData.load(storage, directory, GCInvSaveData.SAVE_ID, type)
            check(data.hasSaved("LegacyPlayer"))
            data.save("NativePlayer", inventory, lookup)
        }
        SavedDataStorage(directory, DataFixers.getDataFixer(), lookup).use { storage ->
            val data = FoundationSavedData.load(storage, directory, GCInvSaveData.SAVE_ID, type)
            check(data.hasSaved("LegacyPlayer") && data.hasSaved("NativePlayer"))
            check(data.restore("NativePlayer", inventory, lookup, automatic = true))
            for (slot in original.indices) check(ItemStack.matches(original[slot], inventory.getItem(slot)))
            check(data.writeToNBT(CompoundTag()).getStringOr("unknown", "") == "preserved")
        }
        check(Files.exists(legacyFile))
    } finally { Files.walk(root).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach(Files::delete) } }

    val dispatcher = CommandDispatcher<CommandSourceStack>()
    CommandGCInv.register(dispatcher)
    val publicSource = Commands.createCompilationContext(PermissionSet.NO_PERMISSIONS)
    val operatorSource = Commands.createCompilationContext(PermissionSet.ALL_PERMISSIONS)
    val node = requireNotNull(dispatcher.root.getChild("gcinv"))
    check(!node.requirement.test(publicSource) && node.requirement.test(operatorSource))
    for (action in listOf("save", "restore", "drop", "clear", "RESTORE")) {
        val parsed = dispatcher.parse("gcinv $action OfflinePlayer", operatorSource)
        check(parsed.exceptions.isEmpty() && !parsed.reader.canRead() && parsed.context.command != null)
    }
    check(dispatcher.parse("gcinv save OfflinePlayer extra", operatorSource).reader.canRead())
    check(dispatcher.parse("gcinv save", operatorSource).context.command == null)
    val suggestions = dispatcher.getCompletionSuggestions(dispatcher.parse("gcinv ", operatorSource)).get()
    check(suggestions.list.map { it.text }.toSet() == setOf("save", "restore", "drop", "clear"))
    println("Passed native equipment archive command permissions and parsing, eleven-slot snapshots, six-slot legacy imports, offline restore flags, server separation, corrupt-data preservation, and saved-data disk reloads")
}
