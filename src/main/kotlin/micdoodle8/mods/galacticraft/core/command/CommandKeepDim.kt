package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString

open class CommandKeepDim : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " <dimension id>"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 4
    }

    override fun getName(): String {
        return "gckeeploaded"
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        val playerBase: EntityPlayerMP?

        if (args.size > 1) {
            throw WrongUsageException(
                "Too many command arguments! Usage: " + this.getUsage(sender),
                *arrayOfNulls<Any>(0)
            )
        } else {
            try {
                playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)

                if (playerBase != null) {
                    val dimID: Int

                    if (args.size == 0) {
                        dimID = playerBase.dimension
                    } else {
                        try {
                            dimID = parseInt(args[0])
                        } catch (e: Exception) {
                            throw WrongUsageException(
                                "Needs a dimension number! Usage: " + this.getUsage(sender),
                                *arrayOfNulls<Any>(0)
                            )
                        }
                    }

                    if (ConfigManagerCore.setLoaded(dimID)) {
                        playerBase.sendMessage(TextComponentString("[GCKeepLoaded] Successfully set dimension " + dimID + " to load staticly"))
                    } else {
                        if (ConfigManagerCore.setUnloaded(dimID)) {
                            playerBase.sendMessage(TextComponentString("[GCKeepLoaded] Successfully set dimension " + dimID + " to not load staticly"))
                        } else {
                            playerBase.sendMessage(TextComponentString("[GCKeepLoaded] Failed to set dimension as not static"))
                        }
                    }
                }
            } catch (var6: Exception) {
                throw CommandException(var6.message, *arrayOfNulls<Any>(0))
            }
        }
    }
}
