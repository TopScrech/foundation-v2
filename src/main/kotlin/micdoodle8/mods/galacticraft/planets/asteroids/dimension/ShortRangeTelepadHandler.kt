package micdoodle8.mods.galacticraft.planets.asteroids.dimension

import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.planets.asteroids.tick.AsteroidsTickHandlerServer
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.world.storage.WorldSavedData
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

open class ShortRangeTelepadHandler constructor(saveDataID: String) : WorldSavedData(saveDataID) {
    open class TelepadEntry
    constructor(
        @JvmField var dimensionID: Int,
        @JvmField var position: BlockVec3,
        @JvmField var enabled: Boolean
    ) {
        override fun hashCode(): Int {
            return HashCodeBuilder().append(dimensionID).append(position.hashCode()).append(enabled).toHashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other is TelepadEntry) {
                return EqualsBuilder().append(other.dimensionID, this.dimensionID).append(other.position, this.position)
                    .append(other.enabled, this.enabled).isEquals()
            }

            return false
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        val tagList: NBTTagList = nbt.getTagList("TelepadList", 10)
        tileMap.clear()

        for (i: Int in 0..<tagList.tagCount()) {
            val nbt2: NBTTagCompound = tagList.getCompoundTagAt(i)
            val address: Int = nbt2.getInteger("Address")
            val dimID: Int = nbt2.getInteger("DimID")
            val posX: Int = nbt2.getInteger("PosX")
            val posY: Int = nbt2.getInteger("PosY")
            val posZ: Int = nbt2.getInteger("PosZ")
            var enabled: Boolean = true
            if (nbt2.hasKey("Enabled")) {
                enabled = nbt2.getBoolean("Enabled")
            }
            tileMap.put(address, TelepadEntry(dimID, BlockVec3(posX, posY, posZ), enabled))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        val tagList: NBTTagList = NBTTagList()

        for (e: MutableMap.MutableEntry<Int, TelepadEntry?> in tileMap.entries) {
            val nbt2: NBTTagCompound = NBTTagCompound()
            nbt2.setInteger("Address", e.key!!)
            nbt2.setInteger("DimID", e.value!!.dimensionID)
            nbt2.setInteger("PosX", e.value!!.position.x)
            nbt2.setInteger("PosY", e.value!!.position.y)
            nbt2.setInteger("PosZ", e.value!!.position.z)
            nbt2.setBoolean("Enabled", e.value!!.enabled)
            tagList.appendTag(nbt2)
        }

        nbt.setTag("TelepadList", tagList)
        return nbt
    }

    companion object {
        const val saveDataID: String = "ShortRangeTelepads"
        private val tileMap: MutableMap<Int, TelepadEntry?> = Maps.newHashMap<Int, TelepadEntry?>()

        @JvmStatic
        fun addShortRangeTelepad(telepad: TileEntityShortRangeTelepad) {
            if (!telepad.getWorld().isRemote) {
                if (telepad.addressValid) {
                    val newEntry: TelepadEntry = TelepadEntry(
                        telepad.getWorld().provider.getDimension(),
                        BlockVec3(telepad),
                        !telepad.getDisabled(0)
                    )
                    val previous: TelepadEntry? = tileMap.put(telepad.address, newEntry)

                    if (previous == null || previous != newEntry) {
                        (AsteroidsTickHandlerServer.spaceRaceData)!!.setDirty(true)
                    }
                }
            }
        }

        @JvmStatic
        fun removeShortRangeTeleporter(telepad: TileEntityShortRangeTelepad) {
            if (!telepad.getWorld().isRemote) {
                if (telepad.addressValid) {
                    tileMap.remove(telepad.address)
                    (AsteroidsTickHandlerServer.spaceRaceData)!!.setDirty(true)
                }
            }
        }

        @JvmStatic
        fun getLocationFromAddress(address: Int): TelepadEntry {
            return tileMap.get(address)!!
        }
    }
}
