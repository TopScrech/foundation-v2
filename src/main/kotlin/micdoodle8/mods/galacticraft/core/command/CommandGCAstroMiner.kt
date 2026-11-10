package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString

open class CommandGCAstroMiner : CommandBase() {
    override fun getName(): String {
        return "gcastrominer"
    }

    override fun getUsage(sender: ICommandSender): String {
        return "/" + this.getName() + " [show|reset|set<number>] <playername>"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 2
    }

    override fun getTabCompletions(
        server: MinecraftServer,
        sender: ICommandSender,
        args: Array<String?>,
        pos: BlockPos?
    ): MutableList<String?>? {
        if (args.size == 1) {
            return getListOfStringsMatchingLastWord(args, "show", "set", "reset")
        }
        if (args.size == 2) {
            return getListOfStringsMatchingLastWord(args, *server.getOnlinePlayerNames())
        }
        return null
    }

    override fun isUsernameIndex(par1ArrayOfStr: Array<String?>, par2: Int): Boolean {
        return par2 == 1
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        if (args.size > 2) {
            throw WrongUsageException(
                GCCoreUtil.translateWithFormat(
                    "commands.dimensiontp.too_many",
                    this.getUsage(sender)
                ), *arrayOfNulls<Any>(0)
            )
        }
        if (args.size < 1) {
            throw WrongUsageException(
                GCCoreUtil.translateWithFormat(
                    "commands.ssinvite.wrong_usage",
                    this.getUsage(sender)
                ), *arrayOfNulls<Any>(0)
            )
        }

        var type: Int = 0
        var newvalue: Int = 0
        if (args[0].equals("show", ignoreCase = true)) {
            type = 1
        } else if (args[0].equals("reset", ignoreCase = true)) {
            type = 2
        } else if (args[0]!!.length > 3 && args[0]!!.substring(0, 3).equals("set", ignoreCase = true)) {
            val number: String = args[0]!!.substring(3)
            try {
                newvalue = number.toInt()
                if (newvalue > 0) {
                    type = 3
                }
            } catch (ex: NumberFormatException) {
            }
        }

        //Proceed if syntax of show|reset|set<number> was correct
        if (type > 0) {
            var playerBase: EntityPlayerMP? = null
            try {
                if (args.size == 2) {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername((args[1])!!, true)
                } else {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)
                }

                if (playerBase != null) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!
                    when (type) {
                        1 -> sender.sendMessage(
                            TextComponentString(
                                GCCoreUtil.translateWithFormat(
                                    "command.gcastrominer.count",
                                    PlayerUtil.getName(playerBase),
                                    "" + stats.getAstroMinerCount()
                                )
                            )
                        )

                        2 -> {
                            stats.setAstroMinerCount(0)
                            sender.sendMessage(
                                TextComponentString(
                                    GCCoreUtil.translateWithFormat(
                                        "command.gcastrominer.count",
                                        PlayerUtil.getName(playerBase),
                                        "" + 0
                                    )
                                )
                            )
                        }

                        3 -> {
                            stats.setAstroMinerCount(newvalue)
                            sender.sendMessage(
                                TextComponentString(
                                    GCCoreUtil.translateWithFormat(
                                        "command.gcastrominer.count",
                                        PlayerUtil.getName(playerBase),
                                        "" + newvalue
                                    )
                                )
                            )
                        }
                    }
                } else {
                    throw Exception("Could not find player with name: " + args[1])
                }
            } catch (e: Exception) {
                throw CommandException(e.message, *arrayOfNulls<Any>(0))
            }
            return
        }
    }
}
