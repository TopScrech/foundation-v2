package micdoodle8.mods.galacticraft.core.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC
import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import java.util.Locale

object CommandGCInv {
    private val invalidAction = DynamicCommandExceptionType { action -> Component.literal("Invalid GCInv action: $action") }
    private val missingPlayer = DynamicCommandExceptionType { name -> Component.literal("Could not find online player: $name") }
    private val failedOperation = DynamicCommandExceptionType { reason -> Component.literal("GCInv: $reason") }
    private val actions = listOf("save", "restore", "drop", "clear")

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("gcinv")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("action", StringArgumentType.word())
                    .suggests { _, builder -> SharedSuggestionProvider.suggest(actions, builder) }
                    .then(Commands.argument("player", StringArgumentType.word())
                        .suggests { context, builder -> SharedSuggestionProvider.suggest(context.source.onlinePlayerNames, builder) }
                        .executes { context ->
                            execute(context.source, StringArgumentType.getString(context, "action"), StringArgumentType.getString(context, "player"))
                        }))
        )
    }

    private fun execute(source: CommandSourceStack, action: String, name: String): Int {
        val operation = action.lowercase(Locale.ROOT)
        if (operation !in actions) throw invalidAction.create(action)
        val player = source.server.playerList.getPlayerByName(name)
        if (operation != "restore" && player == null) throw missingPlayer.create(name)
        try {
            val data = GCInvSaveData.get(source.level)
            if (player == null) {
                check(data.queueRestore(name)) { "No equipment archive exists for $name" }
                source.sendSuccess({ Component.literal("GCInv: queued equipment restoration for $name on next login") }, false)
                return 1
            }
            val inventory = requireNotNull(AccessInventoryGC.getGCInventoryForPlayer(player)) { "Equipment inventory is unavailable for $name" }
            when (operation) {
                "save" -> data.save(name, inventory, player.registryAccess())
                "restore" -> check(data.restore(name, inventory, player.registryAccess())) { "No equipment archive exists for $name" }
                "drop" -> inventory.dropExtendedItems(player)
                "clear" -> inventory.clearContent()
            }
            source.sendSuccess({ Component.literal("GCInv: $operation completed for $name") }, false)
            return 1
        } catch (cause: IllegalArgumentException) {
            throw failedOperation.create(cause.message)
        } catch (cause: IllegalStateException) {
            throw failedOperation.create(cause.message)
        }
    }

    /** Called with the inventory being decoded, before player capability attachment is necessarily complete */
    @JvmStatic
    fun restoreOnLogin(player: ServerPlayer, inventory: IInventoryGC) {
        try {
            GCInvSaveData.get(player.level()).restore(player.gameProfile.name, inventory, player.registryAccess(), automatic = true)
        } catch (cause: RuntimeException) {
            // A broken archived stack must not abort decoding the rest of the player's statistics
            foundation.logging.FoundationLog.log(
                org.apache.logging.log4j.Level.ERROR, cause,
                "GCInv archive for %s could not be restored and was retained", player.gameProfile.name
            )
        }
    }
}
