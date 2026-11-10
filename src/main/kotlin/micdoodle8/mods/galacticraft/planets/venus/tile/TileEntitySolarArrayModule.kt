package micdoodle8.mods.galacticraft.planets.venus.tile

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

open class TileEntitySolarArrayModule : TileEntitySolarTransmitter() {
    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        return type == NetworkType.SOLAR_MODULE && (direction)!!.getAxis() != EnumFacing.Axis.Y
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
    }

    override fun canTransmit(): Boolean {
        return true
    }
}
