package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.server.MinecraftServer

open class CommandJoinSpaceRace : CommandBase() {
    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName()
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean {
        return true
    }

    override fun getName(): String {
        return "joinrace"
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        val playerBase: EntityPlayerMP? =
            PlayerUtil.getPlayerBaseServerFromPlayerUsername(server, sender.getName(), true)

        if (args.size == 0) {
            try {
                if (playerBase != null) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

                    if (stats.getSpaceRaceInviteTeamID() > 0) {
                        SpaceRaceManager.sendSpaceRaceData(
                            server,
                            playerBase,
                            SpaceRaceManager.getSpaceRaceFromID(stats.getSpaceRaceInviteTeamID())
                        )
                        (GalacticraftCore.packetPipeline)!!.sendTo(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.C_OPEN_JOIN_RACE_GUI,
                                GCCoreUtil.getDimensionID(playerBase.level()),
                                arrayOf<Any>(stats.getSpaceRaceInviteTeamID())
                            ), playerBase
                        )
                    } else {
                        throw Exception("You haven't been invited to a space race team!")
                    }
                } else {
                    throw Exception("Could not find player with name: " + args[0])
                }
            } catch (var6: Exception) {
                throw CommandException(var6.message, *arrayOfNulls<Any>(0))
            }
        } else {
            throw WrongUsageException(
                GCCoreUtil.translateWithFormat(
                    "commands.joinrace.no_team",
                    this.getUsage(sender)
                ), *arrayOfNulls<Any>(0)
            )
        }
    }
}
