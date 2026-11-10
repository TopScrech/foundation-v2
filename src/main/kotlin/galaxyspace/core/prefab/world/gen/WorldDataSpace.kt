package galaxyspace.core.prefab.world.gen

import galaxyspace.core.prefab.world.gen.WorldDataSpace
import galaxyspace.GalaxySpace
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.MapStorage
import net.minecraft.world.storage.WorldSavedData

open class WorldDataSpace : WorldSavedData {
    constructor() : super(DATA)


    constructor(name: String) : super(name)

    override fun readFromNBT(nbt: NBTTagCompound) {
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound? {
        return null
    }

    companion object {
        private val DATA = GalaxySpace.NAME + "_SpaceData"

        @JvmStatic
        fun get(world: World): WorldDataSpace {
            val storage: MapStorage? = world.getMapStorage()

            checkNotNull(storage) { "World#getMapStorage returned null. The following WorldSave failed to save data: " + DATA }

            var instance: WorldDataSpace? = storage.getOrLoadData(WorldDataSpace::class.java, DATA) as WorldDataSpace?

            if (instance == null) {
                instance = WorldDataSpace()
                storage.setData(DATA, instance)
            }

            return instance
        }
    }
}
