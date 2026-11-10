package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer

open class CommandPlanetTeleport : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " [<player>]"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 2
    }

    override fun getName(): String {
        return "dimensiontp"
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
                    val worldserver: WorldServer = server.getWorld(GCCoreUtil.getDimensionID(server.worlds[0]))
                    val spawnPoint: BlockPos = worldserver.getSpawnPoint()
                    val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!
                    stats.setRocketStacks(NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY))
                    stats.setRocketType(IRocketType.EnumRocketType.DEFAULT.ordinal)
                    stats.setRocketItem(GCItems.rocketTier1)
                    stats.setFuelLevel(1000)
                    stats.setCoordsTeleportedFromX(spawnPoint.getX().toDouble())
                    stats.setCoordsTeleportedFromZ(spawnPoint.getZ().toDouble())

                    try {
                        WorldUtil.toCelestialSelection(playerBase, stats, Int.MAX_VALUE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }

                    notifyCommandListener(
                        sender,
                        this,
                        "commands.dimensionteleport",
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
