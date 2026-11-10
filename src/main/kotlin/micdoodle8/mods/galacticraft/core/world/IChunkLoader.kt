package micdoodle8.mods.galacticraft.core.world

import net.minecraftforge.common.ForgeChunkManager.Ticket

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager

interface IChunkLoader {
    fun onTicketLoaded(ticket: ForgeChunkManager.Ticket?, placed: Boolean)

    fun getTicket(): ForgeChunkManager.Ticket?

    fun getWorld(): World?

    fun getCoords(): BlockPos?

    fun getOwnerName(): String?

    fun setOwnerName(ownerName: String?)
}
