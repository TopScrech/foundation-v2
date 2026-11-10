package micdoodle8.mods.galacticraft.core.command

import java.util.LinkedList
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil

import com.mojang.authlib.GameProfile
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.*
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
import java.util.*

open class CommandGCHouston : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " [<player>]"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean {
        return true
    }

    override fun getName(): String {
        return "gchouston"
    }

    override fun getTabCompletions(
        server: MinecraftServer,
        sender: ICommandSender,
        args: Array<String?>,
        pos: BlockPos?
    ): MutableList<String?>? {
        if (args.size == 1 && this.isOp(server, sender)) {
            return getListOfStringsMatchingLastWord(args, *server.getOnlinePlayerNames())
        }
        return null
    }

    override fun isUsernameIndex(par1ArrayOfStr: Array<String?>, par2: Int): Boolean {
        return par2 == 1
    }

    private fun isOp(server: MinecraftServer, sender: ICommandSender): Boolean {
        val entitySender: Entity? = sender.getCommandSenderEntity()
        if (entitySender == null) {
            return true
        } else if (entitySender is EntityPlayer) {
            val prof: GameProfile = entitySender.getGameProfile()
            return server.getPlayerList().canSendCommands(prof)
        }
        return false
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        var playerBase: EntityPlayerMP? = null
        val isOp: Boolean = this.isOp(server, sender)

        if (args.size < 2) {
            try {
                if (args.size == 0) {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(sender.getName(), true)
                    if (!(playerBase)!!.capabilities.isCreativeMode) {
                        if (ConfigManagerCore.challengeMode || (playerBase)!!.world.provider !is IGalacticraftWorldProvider) {
                            notifyCommandListener(sender, this, "commands.gchouston.fail")
                            return
                        }
                    }

                    if (timerList.contains(playerBase)) {
                        timerList.remove(playerBase)
                    } else {
                        timerList.add(playerBase)
                        TickHandlerServer.timerHoustonCommand = 250
                        val msg: String =
                            EnumColor.YELLOW.toString() + GCCoreUtil.translate("commands.gchouston.confirm.1") + " " + EnumColor.WHITE + GCCoreUtil.translate(
                                "commands.gchouston.confirm.2"
                            )
                        notifyCommandListener(sender, this, msg)
                        return
                    }
                } else {
                    if (!isOp) {
                        notifyCommandListener(sender, this, "commands.gchouston.noop")
                        return
                    }

                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername((args[0])!!, true)
                }

                if (playerBase != null) {
                    var dimID: Int = ConfigManagerCore.idDimensionOverworld
                    var worldserver: WorldServer = server.getWorld(dimID)
                    if (worldserver == null) {
                        worldserver = server.getWorld(0)
                        if (worldserver == null) {
                            throw Exception("/gchouston could not find Overworld.")
                        }
                        dimID = 0
                    }
                    var spawnPoint: BlockPos? = null
                    val bedPos: BlockPos = playerBase.getBedLocation(dimID)
                    if (bedPos != null) {
                        spawnPoint = EntityPlayer.getBedSpawnLocation(worldserver, bedPos, false)
                    }
                    if (spawnPoint == null) {
                        spawnPoint = worldserver.getTopSolidOrLiquidBlock(worldserver.getSpawnPoint())
                    }
                    val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!
                    stats.setRocketStacks(NonNullList.withSize<ItemStack>(0, ItemStack.EMPTY))
                    stats.setRocketType(IRocketType.EnumRocketType.DEFAULT.ordinal)
                    stats.setRocketItem(null)
                    stats.setFuelLevel(0)
                    stats.setCoordsTeleportedFromX((spawnPoint)!!.getX().toDouble())
                    stats.setCoordsTeleportedFromZ((spawnPoint)!!.getZ().toDouble())
                    stats.setUsingPlanetSelectionGui(false)
                    playerBase.closeScreen()
                    val spawnPos: Vector3 =
                        Vector3((spawnPoint)!!.getX() + 0.5, (spawnPoint)!!.getY() + 0.25, (spawnPoint)!!.getZ() + 0.5)

                    try {
                        WorldUtil.teleportEntitySimple(worldserver, dimID, playerBase, spawnPos)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }

                    notifyCommandListener(
                        sender,
                        this,
                        "commands.gchouston.success",
                        *arrayOf<Any?>((EnumColor.GREY.toString() + "" + playerBase.getName()).toString())
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

    companion object {
        private val timerList: MutableList<EntityPlayerMP> = LinkedList<EntityPlayerMP>()

        @JvmStatic
        fun reset() {
            timerList.clear()
        }
    }
}
