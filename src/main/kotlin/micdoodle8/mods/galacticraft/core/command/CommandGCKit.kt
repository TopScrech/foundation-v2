package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemHandlerHelper

open class CommandGCKit : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " [<player>]"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 2
    }

    override fun getName(): String {
        return "gckit"
    }

    override fun getTabCompletions(
        server: MinecraftServer,
        sender: ICommandSender,
        args: Array<String?>,
        pos: BlockPos?
    ): MutableList<String?>? {
        if (args.size == 1) {
            return getListOfStringsMatchingLastWord(args, *server.getOnlinePlayerNames())
        }
        return null
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        var playerBase: EntityPlayerMP? = null

        if (args.size < 2) {
            try {
                if (args.size == 1) {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername((args[0])!!, true)
                } else {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)
                }

                if (playerBase != null) {
                    ItemHandlerHelper.giveItemToPlayer(playerBase, ItemStack(GCItems.emergencyKit), 0)
                    notifyCommandListener(
                        sender,
                        this,
                        "commands.emergencykit",
                        *arrayOf<Any?>((EnumColor.GREY.toString() + "[" + playerBase.getName()).toString(), "]")
                    )
                } else {
                    throw Exception("Could not find player with name: " + args[0])
                }
            } catch (var6: Exception) {
                throw CommandException(var6.message, *arrayOfNulls<Any>(0))
            }
        } else {
            throw WrongUsageException(
                GCCoreUtil.translateWithFormat(
                    "commands.dimensiontp.too_many",
                    this.getUsage(sender)
                ), *arrayOfNulls<Any>(0)
            )
        }
    }
}
