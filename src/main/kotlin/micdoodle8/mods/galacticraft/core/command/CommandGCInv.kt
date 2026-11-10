package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

open class CommandGCInv : CommandBase() {
    override fun getUsage(var1: ICommandSender): String {
        return "/" + this.getName() + " [save|restore|drop|clear] <playername>"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 2
    }

    override fun getName(): String {
        return "gcinv"
    }

    override fun getTabCompletions(
        server: MinecraftServer,
        sender: ICommandSender,
        args: Array<String?>,
        pos: BlockPos?
    ): MutableList<String?>? {
        if (args.size == 1) {
            return getListOfStringsMatchingLastWord(args, "save", "restore", "drop", "clear")
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
        if (firstuse) {
            firstuse = false
            initialise()
        }

        if (args.size == 2) {
            try {
                val thePlayer: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername((args[1])!!, true)
                if (thePlayer != null && !thePlayer.isDead && thePlayer.world != null) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(thePlayer))!!

                    if (args[0].equals("drop", ignoreCase = true)) {
                        val gcInventory: InventoryExtended = (stats.getExtendedInventory())!!
                        gcInventory.dropExtendedItems(thePlayer)
                    } else if (args[0].equals("save", ignoreCase = true)) {
                        val gcInventory: InventoryExtended = (stats.getExtendedInventory())!!
                        val saveinv: Array<ItemStack?> = arrayOfNulls<ItemStack>(gcInventory.getSizeInventory())
                        for (i: Int in 0..<gcInventory.getSizeInventory()) {
                            saveinv[i] = gcInventory.getStackInSlot(i)
                            gcInventory.setInventorySlotContents(i, ItemStack.EMPTY)
                        }

                        savedata.put(args[1]!!.lowercase(Locale.getDefault()), saveinv)
                        dontload.add(args[1]!!.lowercase(Locale.getDefault()))
                        writefile()
                        println("[GCInv] Saving and clearing GC inventory slots of " + PlayerUtil.getName(thePlayer))
                    } else if (args[0].equals("restore", ignoreCase = true)) {
                        val saveinv: Array<ItemStack?>? = savedata.get(args[1]!!.lowercase(Locale.getDefault()))
                        dontload.remove(args[1]!!.lowercase(Locale.getDefault()))
                        if (saveinv == null) {
                            println("[GCInv] Tried to restore but player " + PlayerUtil.getName(thePlayer) + " had no saved GC inventory items.")
                            return
                        }

                        doLoad(thePlayer)
                    } else if (args[0].equals("clear", ignoreCase = true)) {
                        val gcInventory: InventoryExtended = (stats.getExtendedInventory())!!
                        for (i: Int in 0..<gcInventory.getSizeInventory()) {
                            gcInventory.setInventorySlotContents(i, ItemStack.EMPTY)
                        }
                    } else {
                        throw WrongUsageException("Invalid GCInv command. Usage: " + this.getUsage(sender))
                    }
                } else {
                    // Special rule for 'restore' command if player not found -
                    // look to see if the player is offline (i.e. had a saved
                    // inventory already)
                    if (args[0].equals("restore", ignoreCase = true)) {
                        val saveinv: Array<ItemStack?>? = savedata.get(args[1]!!.lowercase(Locale.getDefault()))
                        if (saveinv != null) {
                            println("[GCInv] Restore command for offline player " + args[1] + ", setting to restore GCInv on next login.")
                            dontload.remove(args[1]!!.lowercase(Locale.getDefault()))
                            // Now it can autoload on next player logon
                            return
                        }
                    }

                    // No player found, and not a 'restore' command
                    if (args[0].equals("clear", ignoreCase = true) || args[0].equals(
                            "save",
                            ignoreCase = true
                        ) || args[0].equals("drop", ignoreCase = true)
                    ) {
                        println("GCInv command: player " + args[1] + " not found.")
                    } else {
                        throw WrongUsageException(
                            "Invalid GCInv command. Usage: " + this.getUsage(sender),
                            *arrayOfNulls<Any>(0)
                        )
                    }
                }
            } catch (e: Exception) {
                println(e.toString())
                e.printStackTrace()
            }
        } else {
            throw WrongUsageException(
                "Not enough command arguments! Usage: " + this.getUsage(sender),
                *arrayOfNulls<Any>(0)
            )
        }
    }

    companion object {
        @JvmField
        val savedata: MutableMap<String?, Array<ItemStack?>?> = HashMap<String?, Array<ItemStack?>?>()
        private val dontload: MutableSet<String?> = HashSet<String?>()
        private var firstuse = true
        private var savefile: GCInvSaveData? = null

        @JvmStatic
        fun doLoad(thePlayer: EntityPlayerMP?) {
            val theName: String = (PlayerUtil.getName(thePlayer))!!.lowercase(Locale.getDefault())
            if (!dontload.contains(theName))  // This is a simple flag: if the playername is in dontload then no
            // restore command has yet been run.
            // Dontload resets to nothing on server restart so that all will
            // auto-restore on a server restart.
            {
                val saveinv: Array<ItemStack?> = savedata.get(theName)!!
                val gcInventory: InventoryExtended = ((GCPlayerStats.get((thePlayer)!!))!!.getExtendedInventory())!!
                for (i: Int in 0..<gcInventory.getSizeInventory()) {
                    gcInventory.setInventorySlotContents(i, (saveinv[i])!!)
                }
                savedata.remove(theName)
                writefile()
                println("[GCInv] Restored GC inventory slots of " + PlayerUtil.getName(thePlayer))
            } else {
                println("[GCInv] Player " + PlayerUtil.getName(thePlayer) + " was spawned without restoring the GCInv save.  Run /gcinv restore playername to restore it.")
            }
        }

        @JvmStatic
        private fun writefile() {
            savefile!!.writeToNBT(NBTTagCompound())
            savefile!!.markDirty()
        }

        @JvmStatic
        private fun initialise() {
            val world0: World? = WorldUtil.getWorldForDimensionServer(0)
            if (world0 == null) {
                return
            }
            savefile = world0.loadData(GCInvSaveData::class.java, GCInvSaveData.SAVE_ID) as GCInvSaveData?
            if (savefile == null) {
                savefile = GCInvSaveData()
                world0.setData(GCInvSaveData.SAVE_ID, savefile)
            }
        }

        @JvmStatic
        fun getSaveData(p: String?): Array<ItemStack?> {
            if (firstuse) {
                firstuse = false
                initialise()
            }

            return savedata.get(p)!!
        }
    }
}
