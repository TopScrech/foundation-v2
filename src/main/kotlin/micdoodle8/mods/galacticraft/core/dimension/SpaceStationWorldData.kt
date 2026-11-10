package micdoodle8.mods.galacticraft.core.dimension

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.world.DimensionType
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.DimensionManager

open class SpaceStationWorldData constructor(par1Str: String) : WorldSavedData(par1Str) {
    private var spaceStationName = "NoName"
    private var owner = "NoOwner"
    private val allowedPlayers: ArrayList<String?>
    private var allowAllPlayers = false
    private var homePlanet = 0
    private var dimensionIdDynamic = 0
    private var dimensionIdStatic = 0
    private var dataCompound: NBTTagCompound? = null

    init {
        this.allowedPlayers = object : ArrayList<String?>() {
            // Override contains so it ignores case.
            override fun contains(o: String?): Boolean {
                if (o is String) {
                    val paramStr: String = o

                    for (s: String? in this) {
                        if (paramStr.equals(s, ignoreCase = true)) {
                            return true
                        }
                    }
                }

                return false
            }
        }
    }

    open fun getAllowedPlayers(): ArrayList<String?> {
        return this.allowedPlayers
    }

    open fun getAllowedAll(): Boolean {
        return this.allowAllPlayers
    }

    open fun setAllowedAll(b: Boolean) {
        this.allowAllPlayers = b
        this.markDirty()
    }

    open fun getOwner(): String? {
        return this.owner
    }

    open fun setOwner(name: String) {
        this.owner = name.replace(".", "")
        this.markDirty()
    }

    open fun getSpaceStationName(): String? {
        return this.spaceStationName
    }

    open fun getHomePlanet(): Int {
        return homePlanet
    }

    open fun setSpaceStationName(string: String) {
        this.spaceStationName = string
    }

    open fun getDimensionIdStatic(): Int {
        return dimensionIdStatic
    }

    open fun setDimensionIdStatic(dimensionIdStatic: Int) {
        this.dimensionIdStatic = dimensionIdStatic
    }

    open fun getDimensionIdDynamic(): Int {
        return dimensionIdDynamic
    }

    open fun setDimensionIdDynamic(dimensionIdDynamic: Int) {
        this.dimensionIdDynamic = dimensionIdDynamic
    }

    override fun readFromNBT(nbttagcompound: NBTTagCompound) {
        this.owner = nbttagcompound.getString("owner").replace(".", "")
        this.spaceStationName = nbttagcompound.getString("spaceStationName")

        if (nbttagcompound.hasKey("dataCompound")) {
            this.dataCompound = nbttagcompound.getCompoundTag("dataCompound")
        } else {
            this.dataCompound = NBTTagCompound()
        }

        if (nbttagcompound.hasKey("homePlanet")) {
            this.homePlanet = nbttagcompound.getInteger("homePlanet")
        } else {
            GCLog.info("Home planet data not found in space station save file for \"" + this.spaceStationName + "\". Using default overworld.")
            this.homePlanet = 0 // Overworld dimension ID
        }

        if (nbttagcompound.hasKey("dimensionIdStatic")) {
            this.dimensionIdStatic = nbttagcompound.getInteger("dimensionIdStatic")
        } else {
            GCLog.info("Static dimension ID not found in space station save file for \"" + this.spaceStationName + "\". Using default overworld.")
            this.dimensionIdStatic = ConfigManagerCore.idDimensionOverworldOrbitStatic
        }

        if (nbttagcompound.hasKey("dimensionIdDynamic")) {
            this.dimensionIdDynamic = nbttagcompound.getInteger("dimensionIdDynamic")
        } else {
            GCLog.info("Dynamic dimension ID not found in space station save file for \"" + this.spaceStationName + "\". Using default overworld.")
            this.dimensionIdDynamic = ConfigManagerCore.idDimensionOverworldOrbit
        }

        this.allowAllPlayers = nbttagcompound.getBoolean("allowedAll")

        val var2: NBTTagList = nbttagcompound.getTagList("allowedPlayers", 10)
        this.allowedPlayers.clear()

        for (var3: Int in 0..<var2.tagCount()) {
            val var4: NBTTagCompound = var2.getCompoundTagAt(var3)
            val var5: String = var4.getString("allowedPlayer")

            if (!this.allowedPlayers.contains(var5)) {
                this.allowedPlayers.add(var5)
            }
        }
    }

    override fun writeToNBT(nbttagcompound: NBTTagCompound): NBTTagCompound {
        nbttagcompound.setString("owner", this.owner)
        nbttagcompound.setString("spaceStationName", this.spaceStationName)
        nbttagcompound.setInteger("homePlanet", this.homePlanet)
        nbttagcompound.setInteger("dimensionIdDynamic", this.dimensionIdDynamic)
        nbttagcompound.setInteger("dimensionIdStatic", this.dimensionIdStatic)
        nbttagcompound.setTag("dataCompound", this.dataCompound)
        nbttagcompound.setBoolean("allowedAll", this.allowAllPlayers)

        val var2: NBTTagList = NBTTagList()

        for (var3: Int in this.allowedPlayers.indices) {
            val player: String? = this.allowedPlayers.get(var3)

            if (player != null) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.setString("allowedPlayer", player)
                var2.appendTag(var4)
            }
        }

        nbttagcompound.setTag("allowedPlayers", var2)
        return nbttagcompound
    }

    companion object {
        /**
         * Retrieve an already created space station date entry
         */
        @JvmStatic
        fun getStationData(world: World, stationID: Int, player: EntityPlayer?): SpaceStationWorldData? {
            return getStationData(world, stationID, -1, -1, -1, player)
        }

        /**
         * Retrieve a space station data entry, creating if necessary (with provided data)
         */
        @JvmStatic
        fun getStationData(
            world: World,
            stationID: Int,
            homeID: Int,
            providerIdDynamic: Int,
            providerIdStatic: Int,
            owner: EntityPlayer?
        ): SpaceStationWorldData? {
            val providerType: DimensionType = DimensionManager.getProviderType(stationID)

            var foundMatch: Boolean = false

            // Loop through all registered satellites, checking for a provider ID match. If none is found, this method is
            // being called on an incorrect
            for (satellite: Satellite in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
                if (satellite.getDimensionIdStatic() == providerType.getId() || satellite.getDimensionID() == providerType.getId()) {
                    foundMatch = true
                    break
                }
            }

            if (!foundMatch) {
                return null
            } else {
                val stationIdentifier: String = getSpaceStationID(stationID)
                var stationData: SpaceStationWorldData? = world.loadData(
                    SpaceStationWorldData::class.java,
                    Constants.GCDATAFOLDER + stationIdentifier
                ) as SpaceStationWorldData?

                if (stationData == null) {
                    stationData = SpaceStationWorldData(stationIdentifier)
                    world.setData(Constants.GCDATAFOLDER + stationIdentifier, stationData)
                    stationData.dataCompound = NBTTagCompound()

                    if (owner != null) {
                        stationData.owner = (PlayerUtil.getName(owner))!!.replace(".", "")
                    }

                    stationData.spaceStationName = "Station: " + stationData.owner

                    if (owner != null) {
                        stationData.allowedPlayers.add(PlayerUtil.getName(owner))
                    }

                    if (homeID == -1) {
                        throw RuntimeException("Space station being created on bad home planet ID!")
                    } else {
                        stationData.homePlanet = homeID
                    }

                    if (providerIdDynamic == -1 || providerIdStatic == -1) {
                        throw RuntimeException("Space station being created on bad provider IDs!")
                    } else {
                        stationData.dimensionIdDynamic = providerIdDynamic
                        stationData.dimensionIdStatic = providerIdStatic
                    }

                    stationData.markDirty()
                }

                if (stationData.getSpaceStationName()!!.replace(" ", "").isEmpty()) {
                    stationData.setSpaceStationName("Station: " + stationData.owner)
                    stationData.markDirty()
                }

                return stationData
            }
        }

        @JvmStatic
        fun getMPSpaceStationData(var0: World?, var1: Int, player: EntityPlayer?): SpaceStationWorldData {
            var var0: World? = var0
            val var2: String = getSpaceStationID(var1)
            if (var0 == null) {
                var0 = WorldUtil.getWorldForDimensionServer(0)
            }
            var var3: SpaceStationWorldData? = null

            if (var0 != null) {
                var3 = var0.loadData(
                    SpaceStationWorldData::class.java,
                    Constants.GCDATAFOLDER + var2
                ) as SpaceStationWorldData?
            } else {
                GCLog.severe("No world for dimension 0?  That should be unpossible!  Please report at https://github.com/micdoodle8/Galacticraft/issues/2617")
            }

            if (var3 == null) {
                var3 = SpaceStationWorldData(var2)
                (var0)!!.setData(Constants.GCDATAFOLDER + var2, var3)
                var3.dataCompound = NBTTagCompound()

                if (player != null) {
                    var3.owner = (PlayerUtil.getName(player))!!.replace(".", "")
                }

                var3.spaceStationName = "Station: " + var3.owner

                if (player != null) {
                    var3.allowedPlayers.add(PlayerUtil.getName(player))
                }

                var3.markDirty()
            }

            if (var3.getSpaceStationName()!!.replace(" ", "").isEmpty()) {
                var3.setSpaceStationName("Station: " + var3.owner)
                var3.markDirty()
            }

            return var3
        }

        @JvmStatic
        fun getSpaceStationID(dimID: Int): String {
            return "spacestation_" + dimID
        }

        @JvmStatic
        fun updateSSOwnership(
            player: EntityPlayerMP?,
            playerName: String?,
            stats: GCPlayerStats,
            stationID: Int,
            stationData: SpaceStationWorldData?
        ) {
            var stationData: SpaceStationWorldData? = stationData
            if (stationData == null) {
                stationData = getMPSpaceStationData(null, stationID, null)
            }

            if (stationData.owner == playerName) {
                //This player is the owner of the station - ensure stats data matches
                if (!((stats.getSpaceStationDimensionData())!!.values.contains(stationID))) {
                    GCLog.debug("Player owns station: " + stationData.getSpaceStationName() + " with home planet " + stationData.getHomePlanet())
                    (stats.getSpaceStationDimensionData())!!.put(stationData.getHomePlanet(), stationID)
                }
            } else {
                //This player is the owner of the station - remove from stats data
                val savedOwned: Int? = (stats.getSpaceStationDimensionData())!!.get(stationData.getHomePlanet())
                if (savedOwned != null && savedOwned == stationID) {
                    GCLog.debug("Player does not own station: " + stationData.getSpaceStationName() + " with home planet " + stationData.getHomePlanet())
                    (stats.getSpaceStationDimensionData())!!.remove(savedOwned)
                }
            }
        }

        @JvmStatic
        fun checkAllStations(thePlayer: EntityPlayerMP?, stats: GCPlayerStats) {
            val name: String? = (PlayerUtil.getName(thePlayer))!!.replace(".", "")
            for (id: Int in (WorldUtil.registeredSpaceStations)!!.keys) {
                updateSSOwnership(thePlayer, name, stats, id, null)
            }
        }
    }
}
