package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData
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

open class CommandSpaceStationAddOwner : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " [ <player> | +all | -all ]"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean {
        return true
    }

    override fun getName(): String {
        return "ssinvite"
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        var var3: String? = null
        var playerBase: EntityPlayerMP? = null

        if (args.size > 0) {
            var3 = args[0]

            try {
                playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)

                if (playerBase != null) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

                    if ((stats.getSpaceStationDimensionData())!!.isEmpty()) {
                        throw WrongUsageException(
                            GCCoreUtil.translate("commands.ssinvite.not_found"),
                            *arrayOfNulls<Any>(0)
                        )
                    } else {
                        for (ownedStations: MutableMap.MutableEntry<Int,Int> in (stats.getSpaceStationDimensionData())!!.entries) {
                            val data: SpaceStationWorldData = (SpaceStationWorldData.getStationData(
                                playerBase.world,
                                ownedStations.value!!,
                                playerBase
                            ))!!

                            if (var3.equals("+all", ignoreCase = true)) {
                                data.setAllowedAll(true)
                                playerBase.sendMessage(TextComponentString(GCCoreUtil.translateWithFormat("gui.spacestation.allow_all_true")))
                                return
                            }
                            if (var3.equals("-all", ignoreCase = true)) {
                                data.setAllowedAll(false)
                                playerBase.sendMessage(
                                    TextComponentString(
                                        GCCoreUtil.translateWithFormat(
                                            "gui.spacestation.allow_all_false",
                                            var3
                                        )
                                    )
                                )
                                return
                            }

                            if (!data.getAllowedPlayers().contains(var3)) {
                                data.getAllowedPlayers().add(var3)
                                data.markDirty()
                            }
                        }
                    }

                    val playerToAdd: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername((var3)!!, true)

                    if (playerToAdd != null) {
                        playerToAdd.sendMessage(
                            TextComponentString(
                                GCCoreUtil.translateWithFormat(
                                    "gui.spacestation.added",
                                    PlayerUtil.getName(playerBase)
                                )
                            )
                        )
                    }
                }
            } catch (var6: Exception) {
                throw CommandException(var6.message, *arrayOfNulls<Any>(0))
            }
        } else {
            throw WrongUsageException(
                GCCoreUtil.translateWithFormat(
                    "commands.ssinvite.wrong_usage",
                    this.getUsage(sender)
                ), *arrayOfNulls<Any>(0)
            )
        }

        if (playerBase != null) {
            playerBase.sendMessage(
                TextComponentString(
                    GCCoreUtil.translateWithFormat(
                        "gui.spacestation.addsuccess",
                        var3
                    )
                )
            )
        }
    }

    override fun getTabCompletions(
        server: MinecraftServer,
        sender: ICommandSender,
        args: Array<String?>,
        pos: BlockPos?
    ): MutableList<String?> {
        return (if (args.size == 1) net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord(
            args,
            *server.getOnlinePlayerNames()
        ) else null)!!
    }

    override fun isUsernameIndex(par1ArrayOfStr: Array<String?>, par2: Int): Boolean {
        return par2 == 0
    }
}
