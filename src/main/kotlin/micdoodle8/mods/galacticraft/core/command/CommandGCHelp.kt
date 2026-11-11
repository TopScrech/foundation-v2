package micdoodle8.mods.galacticraft.core.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import java.net.URI

/** Public player help command, rebuilt with the server's command dispatcher on resource reload */
object CommandGCHelp {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("gchelp")
                .executes { execute(it.source) }
                // The original command ignored trailing arguments
                .then(Commands.argument("arguments", StringArgumentType.greedyString()).executes { execute(it.source) })
        )
    }

    private fun execute(source: CommandSourceStack): Int {
        val player = source.player ?: return 0
        player.sendSystemMessage(message())
        return 1
    }

    fun message(): Component {
        val address = "wiki.${Constants.PREFIX}com"
        val link = Component.literal(" $address").withStyle { style ->
            style.withColor(ChatFormatting.GREEN)
                .withHoverEvent(HoverEvent.ShowText(Component.translatable("gui.message.clicklink").withStyle(ChatFormatting.YELLOW)))
                .withClickEvent(ClickEvent.OpenUrl(URI.create("http://$address/wiki")))
        }
        return Component.translatable("gui.message.help1").withStyle(ChatFormatting.WHITE)
            .append(": ").append(link)
    }
}
