package micdoodle8.mods.galacticraft.core.dimension

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData

open class OrbitSpinSaveData constructor(s: String?) : WorldSavedData(saveDataID) {
    @JvmField
    var datacompound: NBTTagCompound?
    private var alldata: NBTTagCompound? = null
    private var dim = 0

    init {
        this.datacompound = NBTTagCompound()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        this.alldata = nbt
        //world.loadData calls this but can't extract from alldata until we know the dimension ID
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        if (this.dim != 0) {
            nbt.setTag("" + this.dim, this.datacompound)
        }

        return nbt
    }

    companion object {
        @JvmField
        val saveDataID: String = Constants.GCDATAFOLDER + "GCSpinData"

        @JvmStatic
        fun initWorldData(world: World): OrbitSpinSaveData {
            var worldData: OrbitSpinSaveData? =
                world.loadData(OrbitSpinSaveData::class.java, saveDataID) as OrbitSpinSaveData?

            if (worldData == null) {
                worldData = OrbitSpinSaveData("")
                world.setData(saveDataID, worldData)
                if (world.provider is WorldProviderSpaceStation) {
                    worldData.dim = GCCoreUtil.getDimensionID(world)
                    (world.provider as WorldProviderSpaceStation).getSpinManager().writeToNBT((worldData.datacompound)!!)
                }
                worldData.markDirty()
            } else if (world.provider is WorldProviderSpaceStation) {
                worldData.dim = GCCoreUtil.getDimensionID(world)

                worldData.datacompound = null
                if (worldData.alldata != null) {
                    worldData.datacompound = worldData.alldata!!.getCompoundTag("" + worldData.dim)
                }
                if (worldData.datacompound == null) {
                    worldData.datacompound = NBTTagCompound()
                }
            }

            return worldData
        }
    }
}
