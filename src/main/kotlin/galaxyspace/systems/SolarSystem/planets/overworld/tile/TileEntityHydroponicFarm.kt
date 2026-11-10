package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicFarm
import net.minecraft.block.Block
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity

open class TileEntityHydroponicFarm : TileEntity() {
    @JvmField
    var plant: Block? = null

    @JvmField
    var metaplant: Int = 0

    open fun setPlant(stack: Block?) {
        plant = stack
    }

    open fun getPlant(): Block? {
        return plant
    }

    open fun setMetaPlant(meta: Int) {
        metaplant = meta
    }

    open fun getMetaPlant(): Int {
        return metaplant
    }

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        setPlant(plant)
        setMetaPlant(metaplant)
    }
}
