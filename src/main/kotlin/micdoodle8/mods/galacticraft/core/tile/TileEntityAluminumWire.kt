package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalConductor
import net.minecraft.nbt.NBTTagCompound

open class TileEntityAluminumWire @JvmOverloads constructor(@JvmField var tier: Int = 1) :
    TileBaseUniversalConductor() {
    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.tier = nbt.getInteger("tier")
        //For legacy worlds (e.g. converted from 1.6.4)
        if (this.tier == 0) {
            this.tier = 1
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("tier", this.tier)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getTierGC(): Int {
        return this.tier
    }
}
