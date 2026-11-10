package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString

open class CommandSpaceStationChangeOwner : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " <dim#> <player>"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 2
    }

    override fun getName(): String {
        return "ssnewowner"
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        var oldOwner: String? = null
        var newOwner: String = "ERROR"
        var stationID: Int = -1
        val playerAdmin: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)

        if (args.size > 1) {
            newOwner = args[1]

            try {
                stationID = args[0].toInt()
            } catch (var6: Exception) {
                throw WrongUsageException(
                    GCCoreUtil.translateWithFormat(
                        "commands.ssnewowner.wrong_usage",
                        this.getUsage(sender)
                    ), *arrayOfNulls<Any>(0)
                )
            }

            if (stationID < 2) {
                throw WrongUsageException(
                    GCCoreUtil.translateWithFormat(
                        "commands.ssnewowner.wrong_usage",
                        this.getUsage(sender)
                    ), *arrayOfNulls<Any>(0)
                )
            }

            try {
                val stationData: SpaceStationWorldData =
                    SpaceStationWorldData.getMPSpaceStationData(null, stationID, null)
                if (stationData == null) {
                    throw WrongUsageException(
                        GCCoreUtil.translateWithFormat(
                            "commands.ssnewowner.wrong_usage",
                            this.getUsage(sender)
                        ), *arrayOfNulls<Any>(0)
                    )
                }

                oldOwner = stationData.getOwner()
                stationData.getAllowedPlayers().remove(oldOwner)
                if (stationData.getSpaceStationName() == "Station: " + oldOwner) {
                    stationData.setSpaceStationName("Station: " + newOwner)
                }
                stationData.getAllowedPlayers().add(newOwner)
                stationData.setOwner(newOwner)

                val oldPlayer: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername((oldOwner)!!, true)
                val newPlayer: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername(newOwner, true)
                if (oldPlayer != null) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(oldPlayer))!!
                    SpaceStationWorldData.updateSSOwnership(oldPlayer, oldOwner, stats, stationID, stationData)
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID,
                            GCCoreUtil.getDimensionID(oldPlayer.world),
                            arrayOf<Any>(WorldUtil.spaceStationDataToString((stats.getSpaceStationDimensionData())!!))
                        ), oldPlayer
                    )
                }
                if (newPlayer != null) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(newPlayer))!!
                    SpaceStationWorldData.updateSSOwnership(
                        newPlayer,
                        newOwner.replace(".", ""),
                        stats,
                        stationID,
                        stationData
                    )
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID,
                            GCCoreUtil.getDimensionID(oldPlayer!!.world),
                            arrayOf<Any>(WorldUtil.spaceStationDataToString((stats.getSpaceStationDimensionData())!!))
                        ), newPlayer
                    )
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

        if (playerAdmin != null) {
            playerAdmin.sendMessage(
                TextComponentString(
                    GCCoreUtil.translateWithFormat(
                        "gui.spacestation.changesuccess",
                        oldOwner,
                        newOwner
                    )
                )
            )
        } else  //Console
        {
            println(GCCoreUtil.translateWithFormat("gui.spacestation.changesuccess", oldOwner, newOwner))
        }
    }
}
