package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.ITextComponent

open class CommandGCHelp : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName()
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean {
        return true
    }

    override fun getName(): String {
        return "gchelp"
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)
        if (playerBase == null) {
            return
        }
        playerBase.sendMessage(
            ITextComponent.Serializer.jsonToComponent(
                "[{\"text\":\"" + GCCoreUtil.translate("gui.message.help1") + ": \",\"color\":\"white\"}," + "{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + Constants.PREFIX + "com" + "\"," + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"" + GCCoreUtil.translate(
                    "gui.message.clicklink"
                ) + "\",\"color\":\"yellow\"}}," + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + "http://wiki." + Constants.PREFIX + "com/wiki" + "\"}}]"
            )
        )
    }
}
