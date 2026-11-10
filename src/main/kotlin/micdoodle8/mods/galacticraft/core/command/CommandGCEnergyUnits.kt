package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import java.util.*

open class CommandGCEnergyUnits : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        var options: String = " [gJ"

        options = options + "|RF"
        return "/" + this.getName() + options + "]"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean {
        return true
    }

    override fun getName(): String {
        return "gcenergyunits"
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)
        if (playerBase == null) {
            return
        }

        if (args.size == 1) {
            val param: String = args[0]!!.lowercase(Locale.getDefault())
            if (param.length <= 2) {
                var paramvalue: Int = 0
                if ("gj" == param) {
                    paramvalue = 1
                } else if ("rf" == param) {
                    paramvalue = 5
                }

                if (paramvalue > 0) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_UPDATE_ENERGYUNITS,
                            GCCoreUtil.getDimensionID(playerBase.world),
                            arrayOf<Any>(paramvalue)
                        ), playerBase
                    )
                    return
                }
            }

            throw WrongUsageException(
                GCCoreUtil.translateWithFormat(
                    "commands.gcenergyunits.invalid_units",
                    this.getUsage(sender)
                ), *arrayOfNulls<Any>(0)
            )
        }

        throw WrongUsageException(
            GCCoreUtil.translateWithFormat(
                "commands.gcenergyunits.no_units",
                this.getUsage(sender)
            ), *arrayOfNulls<Any>(0)
        )
    }


    companion object {
        @JvmStatic
        fun handleParamClientside(param: Int) {
            if (param == 1) {
                EnergyConfigHandler.displayEnergyUnitsRF = false
                return
            }

            if (param == 5) {
                EnergyConfigHandler.displayEnergyUnitsRF = true
                return
            }
        }
    }
}
