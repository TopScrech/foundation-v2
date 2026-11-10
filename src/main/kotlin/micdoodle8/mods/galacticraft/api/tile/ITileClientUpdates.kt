package micdoodle8.mods.galacticraft.api.tile

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/***
 * Sends basic tile load configuration data
 * e.g. facing  (if not obtainable from blockState)
 * to the client.
 *
 * IMPORTANT: call this.clientValidate() from the tile's validate() method
 */
interface ITileClientUpdates {
    /**
     * The supplied data array of ints
     * ALWAYS has length 4.  You don't
     * have to use all of them!
     */
    fun buildDataPacket(data: IntArray?)

    /**
     * The supplied data list has 4 ints
     * of data to use at positions 1 through 4.
     */
    @SideOnly(Side.CLIENT)
    fun updateClient(data: MutableList<Any?>?)

    /**
     * Implement validate() in the tile and call this!
     */
    fun clientOnLoad() {
        val tile: TileEntity = this as TileEntity
        if (tile.getWorld().isRemote) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_REQUEST_MACHINE_DATA,
                    tile.getWorld(),
                    arrayOf<Any>(tile.getPos())
                )
            )
        }
    }

    /**
     * Do not override unless you want to use a custom data packet
     * with more than 4 ints of data.
     * (If overriding this you must override all other methods in
     * ITileClientUpdates as well ... in which case, why are you using it?)
     */
    fun sendUpdateToClient(player: EntityPlayerMP) {
        val data: IntArray = IntArray(4)
        this.buildDataPacket(data)
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_MACHINE_DATA,
                GCCoreUtil.getDimensionID(player.world),
                arrayOf<Any>((this as TileEntity).getPos(), data[0], data[1], data[2], data[3])
            ), player
        )
    }

    /**
     * Used to push updates out to clients
     */
    fun updateAllInDimension() {
        val data: IntArray = IntArray(4)
        this.buildDataPacket(data)
        val dimID: Int = GCCoreUtil.getDimensionID((this as TileEntity).getWorld())
        (GalacticraftCore.packetPipeline)!!.sendToDimension(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_MACHINE_DATA,
                dimID,
                arrayOf<Any>((this as TileEntity).getPos(), data[0], data[1], data[2], data[3])
            ), dimID
        )
    }
}
