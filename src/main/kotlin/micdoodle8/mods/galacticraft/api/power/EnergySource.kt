package micdoodle8.mods.galacticraft.api.power

import net.minecraft.util.EnumFacing

abstract class EnergySource {
    open class EnergySourceWireless constructor(@JvmField val nodes: MutableList<ILaserNode?>?) : EnergySource()

    open class EnergySourceAdjacent constructor(@JvmField val direction: EnumFacing?) : EnergySource()
}
