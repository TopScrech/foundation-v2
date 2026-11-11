package micdoodle8.mods.galacticraft.core.dimension

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.Level as World
import net.minecraft.world.storage.WorldSavedData

open class WorldDataSpaceRaces constructor(id: String) : WorldSavedData(id) {
    private var dataCompound: NBTTagCompound? = null

    override fun readFromNBT(nbt: NBTTagCompound) {
        SpaceRaceManager.loadSpaceRaces(nbt)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        return SpaceRaceManager.saveSpaceRaces(nbt)
    }

    override fun isDirty(): Boolean {
        return true
    }

    companion object {
        @JvmField
        val saveDataID: String = Constants.GCDATAFOLDER + "GCSpaceRaceData"

        @JvmStatic
        fun initWorldData(world: World): WorldDataSpaceRaces {
            var worldData: WorldDataSpaceRaces? =
                world.loadData(WorldDataSpaceRaces::class.java, saveDataID) as WorldDataSpaceRaces?

            if (worldData == null) {
                worldData = WorldDataSpaceRaces(saveDataID)
                world.setData(saveDataID, worldData)
                worldData.dataCompound = NBTTagCompound()
                worldData.markDirty()
            }

            return worldData
        }
    }
}
