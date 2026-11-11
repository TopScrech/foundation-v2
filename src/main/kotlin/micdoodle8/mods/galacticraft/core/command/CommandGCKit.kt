package micdoodle8.mods.galacticraft.core.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.ItemHandlerHelper

/** The emergency kit remains operator-only and targets one online player */
object CommandGCKit {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, emergencyKit: () -> Item) {
        dispatcher.register(
            Commands.literal("gckit")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes { give(it.source, it.source.playerOrException, emergencyKit()) }
                .then(
                    Commands.argument("player", EntityArgument.player())
                        .executes { give(it.source, EntityArgument.getPlayer(it, "player"), emergencyKit()) }
                )
        )
    }

    private fun give(source: CommandSourceStack, player: ServerPlayer, emergencyKit: Item): Int {
        ItemHandlerHelper.giveItemToPlayer(player, ItemStack(emergencyKit), 0)
        source.sendSuccess({ Component.translatable("commands.emergencykit", player.displayName) }, true)
        return 1
    }
}
