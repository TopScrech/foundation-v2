package micdoodle8.mods.galacticraft.core.command

import com.google.common.collect.Sets
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

open class CommandSpaceStationRemoveOwner : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " <player>"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean {
        return true
    }

    override fun getName(): String {
        return "ssuninvite"
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        var var3: String? = null
        var playerBase: EntityPlayerMP? = null

        if (args.size > 0) {
            var3 = args[0]

            try {
                playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), false)

                if (playerBase != null) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

                    if ((stats.getSpaceStationDimensionData())!!.isEmpty()) {
                        throw WrongUsageException(
                            GCCoreUtil.translate("commands.ssinvite.not_found"),
                            *arrayOfNulls<Any>(0)
                        )
                    } else {
                        for (e: MutableMap.MutableEntry<Int,Int> in (stats.getSpaceStationDimensionData())!!.entries) {
                            val data: SpaceStationWorldData =
                                (SpaceStationWorldData.getStationData(playerBase.world, e.value!!, playerBase))!!

                            var str: String? = null
                            for (name in data.getAllowedPlayers()) {
                                if (name.equals(var3, ignoreCase = true)) {
                                    str = name
                                    break
                                }
                            }

                            if (str != null) {
                                data.getAllowedPlayers().remove(str)
                                data.markDirty()
                            } else {
                                throw CommandException(
                                    GCCoreUtil.translateWithFormat(
                                        "commands.ssuninvite.no_player",
                                        "\"" + var3 + "\""
                                    ), *arrayOfNulls<Any>(0)
                                )
                            }
                        }
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
                        "gui.spacestation.removesuccess",
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
            *this.getPlayers(server, sender)
        ) else null)!!
    }

    protected open fun getPlayers(server: MinecraftServer, sender: ICommandSender): Array<String?> {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), false)

        if (playerBase != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!
            if (!(stats.getSpaceStationDimensionData())!!.isEmpty()) {
                val allNames: Array<String?> = server.getOnlinePlayerNames()
                //data.getAllowedPlayers may include some in lowercase
                //Convert to correct case at least for those players who are online
                val allowedNames: HashSet<String?> = Sets.newHashSet<String?>()

                for (e: MutableMap.MutableEntry<Int,Int> in (stats.getSpaceStationDimensionData())!!.entries) {
                    val data: SpaceStationWorldData =
                        (SpaceStationWorldData.getStationData(playerBase.world, e.value!!, playerBase))!!
                    allowedNames.addAll(data.getAllowedPlayers())
                }

                val itName: MutableIterator<String> = (allowedNames.iterator() as MutableIterator<String>)
                val replaceNames: ArrayList<String?> = ArrayList<String?>()
                while (itName.hasNext()) {
                    val name: String = itName.next()
                    for (allEntry: String? in allNames) {
                        if (name.equals(allEntry, ignoreCase = true)) {
                            itName.remove()
                            replaceNames.add(allEntry!!)
                        }
                    }
                }
                //This does the conversion to correct case
                allowedNames.addAll(replaceNames)
                val rvsize: Array<String?> = arrayOfNulls<String>(allowedNames.size)
                return allowedNames.toArray<String?>(rvsize)
            }
        }

        val returnvalue: Array<String?> = arrayOf<String?>("")
        return returnvalue
    }

    override fun isUsernameIndex(par1ArrayOfStr: Array<String?>, par2: Int): Boolean {
        return par2 == 0
    }
}
