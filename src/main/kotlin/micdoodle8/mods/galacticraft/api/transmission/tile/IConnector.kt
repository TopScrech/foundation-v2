package micdoodle8.mods.galacticraft.api.transmission.tile

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import net.minecraft.util.EnumFacing

/**
 * Applied to TileEntities that can connect to an electrical OR oxygen network.
 *
 * @author Calclavia, micdoodle8
 */
interface IConnector {
    /**
     * @return If the connection is possible.
     */
    fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean
}
