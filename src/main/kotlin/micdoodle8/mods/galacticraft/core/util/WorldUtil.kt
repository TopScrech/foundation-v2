package micdoodle8.mods.galacticraft.core.util

import foundation.level.offset

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import com.google.common.collect.MapMaker
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import java.io.FileInputStream
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.entity.IAntiGrav
import micdoodle8.mods.galacticraft.api.entity.IWorldTransferCallback
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.api.world.SpaceStationType
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.play.server.SPacketEntityEffect
import net.minecraft.network.play.server.SPacketPlayerAbilities
import net.minecraft.network.play.server.SPacketRespawn
import net.minecraft.network.play.server.SPacketSetExperience
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.server.MinecraftServer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.Difficulty as EnumDifficulty
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.WorldSettings
import net.minecraft.world.WorldType
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.server.permission.PermissionAPI
import org.apache.commons.lang3.ArrayUtils
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.Random
import java.util.TreeMap
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

//import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner;
object WorldUtil {
    @JvmField
    var registeredSpaceStations: HashMap<Int,Int>? =
        null //Dimension IDs and providers (providers are -26 or -27 by default)

    @JvmField
    var dimNames: MutableMap<Int, String?> = TreeMap<Int, String?>() //Dimension IDs and provider names

    @JvmField
    var celestialMapCache: MutableMap<EntityPlayerMP?, HashMap<String?,Int>?> =
        MapMaker().weakKeys().makeMap<EntityPlayerMP?, HashMap<String?,Int>?>()

    @JvmField
    var registeredPlanets: MutableList<Int>? = null

    @JvmStatic
    fun getGravityFactor(entity: Entity): Float {
        if (entity.level().provider is IGalacticraftWorldProvider) {
            val customProvider: IGalacticraftWorldProvider = entity.level().provider as IGalacticraftWorldProvider
            var returnValue: Float = MathHelper.sqrt(0.08f / (0.08f - customProvider.getGravity()))
            if (returnValue > 2.5f) {
                returnValue = 2.5f
            }
            if (returnValue < 0.75f) {
                returnValue = 0.75f
            }
            return returnValue
        } else if (entity is IAntiGrav) {
            return 1f
        } else {
            return 1f
        }
    }

    @JvmStatic
    fun getWorldColor(world: World): Vector3 {
        if (GalacticraftCore.isPlanetsLoaded && world.provider is WorldProviderVenus) {
            return Vector3(1.0, 0.8, 0.6)
        }

        return Vector3(1.0, 1.0, 1.0)
    }

    @JvmStatic
    fun getProviderForNameServer(par1String: String): WorldProvider? {
        var nameToFind: String? = par1String
        if (par1String.contains("$")) {
            val twoDimensions: Array<String?> =
                par1String.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            nameToFind = twoDimensions[0]
        }
        if (nameToFind == null) {
            return null
        }

        for (element: MutableMap.MutableEntry<Int, String?> in dimNames.entries) {
            if (nameToFind.equals(element.value, ignoreCase = true)) {
                return WorldUtil.getProviderForDimensionServer(element.key!!)
            }
        }

        GCLog.info("Failed to find matching world for '" + par1String + "'")
        return null
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun getProviderForNameClient(par1String: String): WorldProvider? {
        var nameToFind: String? = par1String
        if (par1String.contains("$")) {
            val twoDimensions: Array<String?> =
                par1String.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            nameToFind = twoDimensions[0]
        }
        if (nameToFind == null) {
            return null
        }

        for (element: MutableMap.MutableEntry<Int, String?> in dimNames.entries) {
            if (nameToFind.equals(element.value, ignoreCase = true)) {
                return WorldUtil.getProviderForDimensionClient(element.key!!)
            }
        }

        GCLog.info("Failed to find matching world for '" + par1String + "'")
        return null
    }

    @JvmStatic
    fun initialiseDimensionNames() {
        val provider: WorldProvider? = getProviderForDimensionServer(ConfigManagerCore.idDimensionOverworld)
        dimNames.put(ConfigManagerCore.idDimensionOverworld, provider!!.getDimensionType().getName())
    }

    /**
     * This will *load* all the GC dimensions which the player has access to (taking account of space station permissions).
     * Loading the dimensions through Forge activates any chunk loaders or forced chunks in that dimension,
     * if the dimension was not previously loaded.  This may place load on the server.
     *
     * @param tier       - the rocket tier to test
     * @param playerBase - the player who will be riding the rocket (needed for space station permissions)
     * @return a List of integers which are the dimension IDs
     */
    @JvmStatic
    fun getPossibleDimensionsForSpaceshipTier(tier: Int, playerBase: EntityPlayerMP?): MutableList<Int> {
        val temp: MutableList<Int> = ArrayList<Int>()

        if (!ConfigManagerCore.disableRocketsToOverworld) {
            temp.add(ConfigManagerCore.idDimensionOverworld)
        }

        for (element: Int in registeredPlanets!!) {
            if (element == ConfigManagerCore.idDimensionOverworld) {
                continue
            }
            val provider: WorldProvider? = getProviderForDimensionServer(element)

            if (provider != null) {
                if (provider is IGalacticraftWorldProvider) {
                    if ((provider as IGalacticraftWorldProvider).canSpaceshipTierPass(tier)) {
                        temp.add(element)
                    }
                } else {
                    temp.add(element)
                }
            }
        }

        for (element: Int in registeredSpaceStations!!.keys) {
            val data: SpaceStationWorldData = (SpaceStationWorldData.getStationData(playerBase!!.level(), element, null))!!

            if (!ConfigManagerCore.spaceStationsRequirePermission || data.getAllowedAll() || data.getAllowedPlayers()
                    .contains(PlayerUtil.getName(playerBase)) || ArrayUtils.contains(
                    playerBase.server.getPlayerList().getOppedPlayerNames(), playerBase.getName()
                )
            ) {
                //Satellites always reachable from their own homeworld or from its other satellites
                if (playerBase != null) {
                    val currentWorld: Int = playerBase.dimension
                    //Player is on homeworld
                    if (currentWorld == data.getHomePlanet()) {
                        temp.add(element)
                        continue
                    }
                    if (playerBase.level().provider is IOrbitDimension) {
                        //Player is currently on another space station around the same planet
                        val dataCurrent: SpaceStationWorldData =
                            (SpaceStationWorldData.getStationData(playerBase.level(), playerBase.dimension, null))!!
                        if (dataCurrent.getHomePlanet() == data.getHomePlanet()) {
                            temp.add(element)
                            continue
                        }
                    }
                }

                //Testing dimension is a satellite, but with a different homeworld - test its tier
                val homeWorld: WorldProvider? = getProviderForDimensionServer(data.getHomePlanet())

                if (homeWorld != null) {
                    if (homeWorld is IGalacticraftWorldProvider) {
                        if ((homeWorld as IGalacticraftWorldProvider).canSpaceshipTierPass(tier)) {
                            temp.add(element)
                        }
                    } else {
                        temp.add(element)
                    }
                }
            }
        }

        return temp
    }

    @JvmStatic
    fun getReachableCelestialBodiesForDimensionID(id: Int): CelestialBody? {
        val celestialBodyList: MutableList<CelestialBody> = Lists.newArrayList<CelestialBody>()
        celestialBodyList.addAll((GalaxyRegistry.getRegisteredMoons())!!.values)
        celestialBodyList.addAll((GalaxyRegistry.getRegisteredPlanets())!!.values)
        celestialBodyList.addAll((GalaxyRegistry.getRegisteredSatellites())!!.values)

        for (cBody: CelestialBody in celestialBodyList) {
            if (cBody.getReachable()) {
                if (cBody.getDimensionID() == id) {
                    return cBody
                }
            }
        }

        return null
    }

    @JvmStatic
    fun getReachableCelestialBodiesForName(name: String?): CelestialBody? {
        val celestialBodyList: MutableList<CelestialBody> = Lists.newArrayList<CelestialBody>()
        celestialBodyList.addAll((GalaxyRegistry.getRegisteredMoons())!!.values)
        celestialBodyList.addAll((GalaxyRegistry.getRegisteredPlanets())!!.values)
        celestialBodyList.addAll((GalaxyRegistry.getRegisteredSatellites())!!.values)

        for (cBody: CelestialBody in celestialBodyList) {
            if (cBody.getReachable()) {
                if (cBody.getName() == name) {
                    return cBody
                }
            }
        }

        return null
    }

    /**
     * CAUTION: this loads the dimension if it is not already loaded.  This can cause
     * server load if used too frequently or with a list of multiple dimensions.
     *
     * @param id
     * @return
     */
    @JvmStatic
    fun getWorldForDimensionServer(id: Int): World? {
        val theServer: MinecraftServer? = GCCoreUtil.getServer()
        if (theServer == null) {
            GCLog.debug("Called WorldUtil server side method but FML returned no server - is this a bug?")
            return null
        }
        return theServer.getWorld(id)
    }

    /**
     * CAUTION: this loads the dimension if it is not already loaded.  This can cause
     * server load if used too frequently or with a list of multiple dimensions.
     *
     * @param id
     * @return
     */
    @JvmStatic
    fun getProviderForDimensionServer(id: Int): WorldProvider? {
        val ws: World? = getWorldForDimensionServer(id)
        if (ws != null) {
            return ws.provider
        }
        return null
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun getProviderForDimensionClient(id: Int): WorldProvider? {
        val ws: World? = ClientProxyCore.mc.world
        if (ws != null && GCCoreUtil.getDimensionID(ws) == id) {
            return ws.provider
        }
        return DimensionManager.createProviderFor(id)
    }

    /**
     * This will *load* all the GC dimensions which the player has access to (taking account of space station permissions).
     * Loading the dimensions through Forge activates any chunk loaders or forced chunks in that dimension,
     * if the dimension was not previously loaded.  This may place load on the server.
     *
     * @param tier       - the rocket tier to test
     * @param playerBase - the player who will be riding the rocket (needed for checking space station permissions)
     * @return a Map of the names of the dimension vs. the dimension IDs
     */
    @JvmStatic
    fun getArrayOfPossibleDimensions(tier: Int, playerBase: EntityPlayerMP?): HashMap<String?,Int> {
        val ids: MutableList<Int> = getPossibleDimensionsForSpaceshipTier(tier, playerBase)
        val map: HashMap<String?,Int> = HashMap<String?,Int>(ids.size, 1f)

        for (id: Int in ids) {
            var celestialBody: CelestialBody? = getReachableCelestialBodiesForDimensionID(id)

            //It's a space station
            if (id > 0 && celestialBody == null) {
                celestialBody = GalacticraftCore.satelliteSpaceStation
                //This no longer checks whether a WorldProvider can be created, for performance reasons (that causes the dimension to load unnecessarily at map building stage)
                if (playerBase != null) {
                    val data: SpaceStationWorldData = (SpaceStationWorldData.getStationData(playerBase.level(), id, null))!!
                    map.put(
                        (celestialBody)!!.getName() + "$" + data.getOwner() + "$" + data.getSpaceStationName() + "$" + id + "$" + data.getHomePlanet(),
                        id
                    )
                }
            } else  //It's a planet or moon
            {
                if (celestialBody === GalacticraftCore.planetOverworld) {
                    map.put((celestialBody)!!.getName(), id)
                } else {
                    val provider: WorldProvider? = getProviderForDimensionServer(id)
                    if (celestialBody != null && provider != null) {
                        if (provider is IGalacticraftWorldProvider && provider !is IOrbitDimension || GCCoreUtil.getDimensionID(
                                provider
                            ) == 0
                        ) {
                            map.put(celestialBody.getName(), GCCoreUtil.getDimensionID(provider))
                        }
                    }
                }
            }
        }

        val cBodyList: ArrayList<CelestialBody> = ArrayList<CelestialBody>()
        cBodyList.addAll((GalaxyRegistry.getRegisteredPlanets())!!.values)
        cBodyList.addAll((GalaxyRegistry.getRegisteredMoons())!!.values)

        for (body: CelestialBody in cBodyList) {
            if (!body.getReachable()) {
                map.put(body.getLocalizedName() + "*", body.getDimensionID())
            }
        }

        celestialMapCache.put(playerBase, map)
        return map
    }

    /**
     * Get the cached version of getArrayOfPossibleDimensions() to reduce server load + unwanted dimension loading
     * The cache will be updated every time the 'proper' version of getArrayOfPossibleDimensions is called.
     *
     * @param tier       - the rocket tier to test
     * @param playerBase - the player who will be riding the rocket (needed for checking space station permissions)
     * @return a Map of the names of the dimension vs. the dimension IDs
     */
    @JvmStatic
    fun getArrayOfPossibleDimensionsAgain(tier: Int, playerBase: EntityPlayerMP?): HashMap<String?,Int> {
        val map: HashMap<String?,Int>? = celestialMapCache.get(playerBase)
        if (map != null) {
            return map
        }
        return getArrayOfPossibleDimensions(tier, playerBase)
    }

    @JvmStatic
    fun unregisterSpaceStations() {
        if (registeredSpaceStations != null) {
            for (registeredID: Int in registeredSpaceStations!!.keys) {
                DimensionManager.unregisterDimension(registeredID)
            }

            registeredSpaceStations = null
        }
    }

    @JvmStatic
    fun registerSpaceStations(theServer: MinecraftServer?, spaceStationList: File) {
//        WorldUtil.registeredSpaceStations = WorldUtil.getExistingSpaceStationList(spaceStationList);
        registeredSpaceStations = Maps.newHashMap<Int,Int>()
        if (theServer == null || !spaceStationList.exists() && !spaceStationList.isDirectory()) {
            return
        }

        val var2: Array<File>? = spaceStationList.listFiles()

        if (var2 != null) {
            for (var5: File in var2) {
                if (var5.getName().startsWith("spacestation_") && var5.getName().endsWith(".dat")) {
                    try {
                        // Note: this is kind of a hacky way of doing this, loading the NBT from each space station file
                        // during dimension registration, to find out what each space station's provider IDs are.

                        var name: String = var5.getName()
                        val worldDataTemp: SpaceStationWorldData = SpaceStationWorldData(name)
                        name = name.substring(13, name.length - 4)
                        val registeredID: Int = name.toInt()

                        val fileinputstream: FileInputStream = FileInputStream(var5)
                        val nbttagcompound: NBTTagCompound = CompressedStreamTools.readCompressed(fileinputstream)
                        fileinputstream.close()
                        worldDataTemp.readFromNBT(nbttagcompound.getCompoundOrEmpty("data"))

                        // Search for id in server-defined statically loaded dimensions
                        val id: Int = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, registeredID)

                        val providerID: Int =
                            if (id >= 0) worldDataTemp.getDimensionIdStatic() else worldDataTemp.getDimensionIdDynamic()
                        var registrationOK: Boolean = false
                        if (!DimensionManager.isDimensionRegistered(registeredID)) {
                            DimensionManager.registerDimension(registeredID, getDimensionTypeById(providerID))
                            registrationOK = true
                        } else if (GalacticraftRegistry.isDimensionTypeIDRegistered(providerID)) {
                            registrationOK = DimensionManager.getProviderType(id).getId() == providerID
                            if (!registrationOK) {
                                try {
                                    val sponge: Class<*> = Class.forName("org.spongepowered.common.world.WorldManager")
                                    val dtDI: Field = sponge.getDeclaredField("dimensionTypeByDimensionId")
                                    dtDI.setAccessible(true)
                                    val result: Int2ObjectMap<DimensionType?>? =
                                        dtDI.get(null) as Int2ObjectMap<DimensionType?>?
                                    if (result != null) {
                                        result.put(id, getDimensionTypeById(providerID))
                                        GCLog.info("Re-registered dimension type " + providerID)
                                    }
                                    registrationOK = true
                                } catch (ignore: ClassNotFoundException) {
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        if (registrationOK) {
                            registeredSpaceStations!!.put(registeredID, providerID)
                            if (id >= 0) {
                                theServer.getWorld(registeredID)
                            }
                            dimNames.put(registeredID, "Space Station " + registeredID)
                        } else {
                            GCLog.severe("Dimension already registered to another mod: unable to register space station dimension " + registeredID)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        //        for (Integer registeredID : WorldUtil.registeredSpaceStations)
//        {
//            int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, registeredID);
//
//            if (!DimensionManager.isDimensionRegistered(registeredID))
//            {
//	            if (id >= 0)
//	            {
//	                DimensionManager.registerDimension(registeredID, ConfigManagerCore.idDimensionOverworldOrbitStatic);
//	                theServer.getWorld(registeredID);
//                }
//	            else
//	            {
//	                DimensionManager.registerDimension(registeredID, ConfigManagerCore.idDimensionOverworldOrbit);
//	            }
//            }
//            else
//            {
//                GCLog.severe("Dimension already registered to another mod: unable to register space station dimension " + registeredID);
//            }
//        }
    }

    /**
     * Call this on FMLServerStartingEvent to register a planet which has a dimension ID.
     * Now returns a boolean to indicate whether registration was successful.
     *
     *
     * NOTE: Planets and Moons dimensions should normally be initialised at server init
     * If you do not do this, you must find your own way to register the dimension in DimensionManager
     * and you must find your own way to include the cached provider name in WorldUtil.dimNames
     *
     *
     * IMPORTANT: GalacticraftRegistry.registerDimension() must always be called in parallel with this
     * meaning the CelestialBodies are iterated in the same order when registered there and here.
     *
     * The defaultID should be 0, and the id should be both a dimension ID and a DimensionType id.
     */
    @JvmStatic
    fun registerPlanet(id: Int, initialiseDimensionAtServerInit: Boolean, defaultID: Int): Boolean {
        if (registeredPlanets == null) {
            registeredPlanets = ArrayList<Int>()
        }

        if (initialiseDimensionAtServerInit) {
            if (!DimensionManager.isDimensionRegistered(id)) {
                DimensionManager.registerDimension(id, getDimensionTypeById(id))
                GCLog.info("Registered Dimension: " + id)
                registeredPlanets!!.add(id)
            } else {
                if (DimensionManager.getProviderType(id)
                        .getId() == id && GalacticraftRegistry.isDimensionTypeIDRegistered(id)
                ) {
                    GCLog.info("Re-registered dimension: " + id)
                    registeredPlanets!!.add(id)
                } else {
                    GCLog.severe("Dimension already registered: unable to register planet dimension " + id)
                    //Add 0 to the list to preserve the correct order of the other planets (e.g. if server/client initialise with different dimension IDs in configs, the order becomes important for figuring out what is going on)
                    registeredPlanets!!.add(defaultID)
                    return false
                }
            }
            val dt: DimensionType? = getDimensionTypeById(id)
            val wp: WorldProvider = dt!!.createDimension()
            dimNames.put(id, getDimensionName(wp))
            return true
        }

        //Not to be initialised - still add to the registered planets list (for hotloading later?)
        registeredPlanets!!.add(id)
        return true
    }

    @JvmStatic
    fun unregisterPlanets() {
        if (registeredPlanets != null) {
            for (var1: Int in (registeredPlanets)!!) {
                DimensionManager.unregisterDimension(var1)
                GCLog.info("Unregistered Dimension: " + var1)
            }

            registeredPlanets = null
        }
        dimNames.clear()
    }

    @JvmStatic
    fun registerPlanetClient(dimID: Int, providerIndex: Int) {
        val typeID: Int = GalacticraftRegistry.getDimensionTypeID(providerIndex)

        if (typeID == 0) {
            GCLog.severe("Server dimension " + dimID + " has no match on client due to earlier registration problem.")
        } else if (dimID == 0) {
            GCLog.severe("Client dimension " + providerIndex + " has no match on server - probably a server dimension ID conflict problem.")
        } else {
            if (!registeredPlanets!!.contains(dimID)) {
                registeredPlanets!!.add(dimID)
                DimensionManager.registerDimension(dimID, getDimensionTypeById(typeID))
            } else {
                GCLog.severe("Dimension already registered to another mod: unable to register planet dimension " + dimID)
            }
        }
    }

    @JvmStatic
    fun bindSpaceStationToNewDimension(
        world: World,
        player: EntityPlayerMP,
        homePlanetID: Int
    ): SpaceStationWorldData? {
        var dynamicProviderID: Int = -1
        var staticProviderID: Int = -1
        for (satellite: Satellite in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
            if ((satellite.getParentPlanet())!!.getDimensionID() == homePlanetID) {
                dynamicProviderID = satellite.getDimensionID()
                staticProviderID = satellite.getDimensionIdStatic()
            }
        }
        if (dynamicProviderID == -1 || staticProviderID == -1) {
            throw RuntimeException("Space station being bound on bad provider IDs!")
        }
        val newID: Int = DimensionManager.getNextFreeDimId()
        val data: SpaceStationWorldData? =
            createSpaceStation(world, newID, homePlanetID, dynamicProviderID, staticProviderID, player)
        dimNames.put(newID, "Space Station " + newID)
        val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
        (stats.getSpaceStationDimensionData())!!.put(homePlanetID, newID)
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID,
                GCCoreUtil.getDimensionID(player.level()),
                arrayOf<Any>(spaceStationDataToString((stats.getSpaceStationDimensionData())!!))
            ), player
        )
        return data
    }

    @JvmStatic
    fun createSpaceStation(
        world: World,
        dimID: Int,
        homePlanetID: Int,
        dynamicProviderID: Int,
        staticProviderID: Int,
        player: EntityPlayerMP?
    ): SpaceStationWorldData? {
        if (!DimensionManager.isDimensionRegistered(dimID)) {
            if (ConfigManagerCore.keepLoadedNewSpaceStations) {
                ConfigManagerCore.setLoaded(dimID)
            }

            val id: Int = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, dimID)

            if (id >= 0) {
                DimensionManager.registerDimension(dimID, getDimensionTypeById(staticProviderID))
                registeredSpaceStations!!.put(dimID, staticProviderID)
            } else {
                DimensionManager.registerDimension(dimID, getDimensionTypeById(dynamicProviderID))
                registeredSpaceStations!!.put(dimID, dynamicProviderID)
            }
        } else {
            GCLog.severe("Dimension already registered to another mod: unable to register space station dimension " + dimID)
        }

        for (server: WorldServer in world.getMinecraftServer()!!.worlds) {
            (GalacticraftCore.packetPipeline)!!.sendToDimension(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_LIST,
                    GCCoreUtil.getDimensionID(server),
                    getSpaceStationList()
                ), GCCoreUtil.getDimensionID(server)
            )
        }
        return SpaceStationWorldData.getStationData(
            world,
            dimID,
            homePlanetID,
            dynamicProviderID,
            staticProviderID,
            player
        )
    }

    @JvmStatic
    fun transferEntityToDimension(entity: Entity, dimensionID: Int, world: WorldServer): Entity? {
        return transferEntityToDimension(entity, dimensionID, world, true, null)
    }

    /**
     * It is not necessary to use entity.setDead() following calling this method.
     * If the entity left the old world it was in, it will now automatically be removed from that old world before the next update tick.
     * (See WorldUtil.removeEntityFromWorld())
     */
    @JvmStatic
    fun transferEntityToDimension(
        entity: Entity,
        dimensionID: Int,
        world: WorldServer,
        transferInv: Boolean,
        ridingRocket: EntityAutoRocket?
    ): Entity? {
        if (!world.isClientSide) {
            //GalacticraftCore.packetPipeline.sendToAll(new PacketSimple(EnumSimplePacket.C_UPDATE_PLANETS_LIST, WorldUtil.getPlanetList()));

            val server: MinecraftServer? = world.getMinecraftServer()

            if (server != null) {
                val var6: WorldServer = server.getWorld(dimensionID)

                if (var6 == null) {
                    System.err.println("Cannot Transfer Entity to Dimension: Could not get World for Dimension " + dimensionID)
                    return null
                }

                val type: ITeleportType? = GalacticraftRegistry.getTeleportTypeForDimension(var6.provider.javaClass)

                if (type != null) {
                    return teleportEntity(var6, entity, dimensionID, type, transferInv, ridingRocket)
                }
            }
        }

        return null
    }

    @JvmStatic
    private fun teleportEntity(
        worldNew: World,
        entity: Entity?,
        dimID: Int,
        type: ITeleportType,
        transferInv: Boolean,
        ridingRocket: EntityAutoRocket?
    ): Entity? {
        var entity: Entity? = entity
        var ridingRocket: EntityAutoRocket? = ridingRocket
        var otherRiddenEntity: Entity? = null
        if (entity!!.getVehicle() != null) {
            if (entity.getVehicle() is EntitySpaceshipBase) {
                entity.startRiding(entity.getVehicle())
            } else if (entity.getVehicle() is EntityCelestialFake) {
                val e: Entity? = entity.getVehicle()
                e!!.removePassengers()
                e.setDead()
            } else {
                otherRiddenEntity = entity.getVehicle()
                entity.dismountRidingEntity()
            }
        }

        val dimChange: Boolean = entity.level() !== worldNew
        //Make sure the entity is added to the correct chunk in the OLD world so that it will be properly removed later if it needs to be unloaded from that world
        entity.level().updateEntityWithOptionalForce(entity, false)
        var player: EntityPlayerMP? = null
        var spawnPos: Vector3? = null
        val oldDimID: Int = GCCoreUtil.getDimensionID(entity.level())

        if (ridingRocket != null) {
            val tList: ArrayList<TileEntityTelemetry?>? = ridingRocket.getTelemetry()
            val nbt: NBTTagCompound = NBTTagCompound()
            ridingRocket.isDead = false
            ridingRocket.removePassengers()
            ridingRocket.writeToNBTOptional(nbt)

            (ridingRocket.world as WorldServer).getEntityTracker().untrack(ridingRocket)
            removeEntityFromWorld(ridingRocket.world, ridingRocket, true)

            ridingRocket = EntityList.createEntityFromNBT(nbt, worldNew) as EntityAutoRocket?

            if (ridingRocket != null) {
                ridingRocket.setWaitForPlayer(true)

                if (ridingRocket is IWorldTransferCallback) {
                    (ridingRocket as IWorldTransferCallback).onWorldTransferred(worldNew)
                }
            }
        }

        if (dimChange) {
            if (entity is EntityPlayerMP) {
                player = entity
                val worldOld: World = player.level()

                val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                stats.setUsingPlanetSelectionGui(false)

                player.dimension = dimID
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("DEBUG: Sending respawn packet to player for dim " + dimID)
                }
                player.connection.sendPacket(
                    SPacketRespawn(
                        dimID,
                        worldNew.getDifficulty(),
                        worldNew.getWorldInfo().getTerrainType(),
                        player.interactionManager.getGameType()
                    )
                )
                player.server.getPlayerList().updatePermissionLevel(player)
                if (worldNew.provider is WorldProviderSpaceStation) {
                    if (registeredSpaceStations!!.containsKey(dimID))  //TODO This has never been effective before due to the earlier bug - what does it actually do?
                    {
                        val var2: NBTTagCompound = NBTTagCompound()
                        (SpaceStationWorldData.getStationData(worldNew, dimID, player))!!.writeToNBT(var2)
                        (GalacticraftCore.packetPipeline)!!.sendTo(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_DATA,
                                GCCoreUtil.getDimensionID(player.level()),
                                arrayOf<Any>(dimID, var2)
                            ), player
                        )
                    }
                }

                removeEntityFromWorld(worldOld, player, true)

                if (ridingRocket != null) {
                    spawnPos = Vector3(ridingRocket)
                } else {
                    spawnPos = type.getPlayerSpawnLocation(worldNew as WorldServer, player)
                }
                forceMoveEntityToPos(entity, worldNew as WorldServer, spawnPos, true)

                GCLog.info(
                    "Server attempting to transfer player " + PlayerUtil.getName(player) + " to dimension " + GCCoreUtil.getDimensionID(
                        worldNew
                    )
                )
                if (worldNew.provider is WorldProviderSpaceStation) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON,
                            GCCoreUtil.getDimensionID(worldNew),
                            arrayOf<Any?>()
                        ), player
                    )
                }
                player.capabilities.isFlying = false

                player.server.getPlayerList().preparePlayer(player, worldOld as WorldServer?)
                player.interactionManager.setWorld(worldNew)
                player.connection.sendPacket(SPacketPlayerAbilities(player.capabilities))
                player.server.getPlayerList().updateTimeAndWeatherForPlayer(player, worldNew)
                player.server.getPlayerList().syncPlayerInventory(player)

                for (o: Any? in player.getActiveEffects()) {
                    val var10: PotionEffect = o as PotionEffect
                    player.connection.sendPacket(SPacketEntityEffect(player.getId(), var10))
                }

                player.connection.sendPacket(
                    SPacketSetExperience(
                        player.experience,
                        player.experienceTotal,
                        player.experienceLevel
                    )
                )
            } else  //Non-player entity transfer i.e. it's an EntityCargoRocket or an empty rocket
            {
                var tList: ArrayList<TileEntityTelemetry?>? = null
                if (entity is EntitySpaceshipBase) {
                    tList = entity.getTelemetry()
                }
                WorldUtil.removeEntityFromWorld(entity.level(), entity, true)

                val nbt: NBTTagCompound = NBTTagCompound()
                entity.isDead = false
                entity.writeToNBTOptional(nbt)
                entity = EntityList.createEntityFromNBT(nbt, worldNew)

                if (entity == null) {
                    return null
                }

                if (entity is IWorldTransferCallback) {
                    (entity as IWorldTransferCallback).onWorldTransferred(worldNew)
                }

                forceMoveEntityToPos(entity, worldNew as WorldServer, Vector3(entity), true)

                if (tList != null && tList.size > 0) {
                    for (tEntry in tList) {
                val t = tEntry!!
                        t.addTrackedEntity(entity)
                    }
                }
            }
        } else {
            //Same dimension player transfer
            if (entity is EntityPlayerMP) {
                player = entity
                player.closeScreen()
                val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                stats.setUsingPlanetSelectionGui(false)

                if (ridingRocket != null) {
                    spawnPos = Vector3(ridingRocket)
                } else {
                    spawnPos = type.getPlayerSpawnLocation(entity.level() as WorldServer?, entity)
                }
                forceMoveEntityToPos(entity, worldNew as WorldServer, spawnPos, false)

                GCLog.info(
                    "Server attempting to transfer player " + PlayerUtil.getName(player) + " within same dimension " + GCCoreUtil.getDimensionID(
                        worldNew
                    )
                )
                if (worldNew.provider is WorldProviderSpaceStation) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON,
                            GCCoreUtil.getDimensionID(worldNew),
                            arrayOf<Any?>()
                        ), player
                    )
                }
                player.capabilities.isFlying = false
            }

            //Cargo rocket does not needs its location setting here, it will do that itself
        }

        //Update PlayerStatsGC
        if (player != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            if (ridingRocket == null && type.useParachute() && (stats.getExtendedInventory())!!
                    .getStackInSlot(4) != null && (stats.getExtendedInventory())!!.getStackInSlot(4)
                    .getItem() is ItemParaChute
            ) {
                GCPlayerHandler.setUsingParachute(player, stats, true)
            } else {
                GCPlayerHandler.setUsingParachute(player, stats, false)
            }

            if (stats.getRocketStacks() != null && !((stats.getRocketStacks())!!.isEmpty())!!) {
                for (stack: Int in ((stats.getRocketStacks())!!.indices)!!) {
                    if (transferInv) {
                        if (((stats.getRocketStacks())!!.get(stack).isEmpty())!!) {
                            if (stack == ((stats.getRocketStacks())!!.size - 1)!!) {
                                if (stats.getRocketItem() != null) {
                                    ((stats.getRocketStacks())!!
                                        .set(stack, ItemStack(stats.getRocketItem(), 1, stats.getRocketType())))!!
                                }
                            } else if (stack == ((stats.getRocketStacks())!!.size - 2)!!) {
                                val launchpad: ItemStack? = stats.getLaunchpadStack()
                                ((stats.getRocketStacks())!!
                                    .set(stack, if (launchpad == null) ItemStack.EMPTY else launchpad))!!
                                stats.setLaunchpadStack(null)
                            }
                        }
                    } else {
                        ((stats.getRocketStacks())!!.set(stack, ItemStack.EMPTY))!!
                    }
                }
            }

            if (transferInv && stats.getChestSpawnCooldown() == 0) {
                stats.setChestSpawnVector(
                    type.getParaChestSpawnLocation(
                        entity.level() as WorldServer?,
                        player,
                        Random()
                    )
                )
                stats.setChestSpawnCooldown(200)
            }
        }

        if (ridingRocket != null) {
            CompatibilityManager.forceLoadChunks(worldNew as WorldServer)
            ridingRocket.forceSpawn = true
            worldNew.addFreshEntity(ridingRocket)
            ridingRocket.setWorld(worldNew)
            worldNew.updateEntityWithOptionalForce(ridingRocket, true)
            CompatibilityManager.forceLoadChunksEnd(worldNew)
            entity.startRiding(ridingRocket)
            GCLog.debug("Entering rocket at : " + entity.x + "," + entity.z + " rocket at: " + ridingRocket.posX + "," + ridingRocket.posZ)
        } else if (otherRiddenEntity != null) {
            if (dimChange) {
                val worldOld: World = otherRiddenEntity.level()
                val nbt: NBTTagCompound = NBTTagCompound()
                otherRiddenEntity.writeToNBTOptional(nbt)
                removeEntityFromWorld(worldOld, otherRiddenEntity, true)
                otherRiddenEntity = EntityList.createEntityFromNBT(nbt, worldNew)
                worldNew.addFreshEntity(otherRiddenEntity)
                otherRiddenEntity!!.setWorld(worldNew)
            }
            otherRiddenEntity.setPositionAndRotation(
                entity.x,
                entity.y - 10,
                entity.z,
                otherRiddenEntity.yRot,
                otherRiddenEntity.xRot
            )
            worldNew.updateEntityWithOptionalForce(otherRiddenEntity, true)
        }

        if (entity is EntityPlayerMP) {
            if (dimChange) FMLCommonHandler.instance().firePlayerChangedDimensionEvent(entity, oldDimID, dimID)

            //Spawn in a lander if appropriate
            type.onSpaceDimensionChanged(worldNew, entity, ridingRocket != null)
        }

        return entity
    }

    @JvmStatic
    fun teleportEntitySimple(worldNew: World, dimID: Int, player: EntityPlayerMP, spawnPos: Vector3): Entity {
        if (player.getVehicle() != null) {
            player.getVehicle()!!.setDead()
            player.dismountRidingEntity()
        }

        val worldOld: World = player.level()
        val oldDimID: Int = GCCoreUtil.getDimensionID(worldOld)
        val dimChange: Boolean = worldOld !== worldNew
        //Make sure the entity is added to the correct chunk in the OLD world so that it will be properly removed later if it needs to be unloaded from that world
        worldOld.updateEntityWithOptionalForce(player, false)

        if (dimChange) {
            player.dimension = dimID
            if (ConfigManagerCore.enableDebug) {
                GCLog.info("DEBUG: Sending respawn packet to player for dim " + dimID)
            }
            player.connection.sendPacket(
                SPacketRespawn(
                    dimID,
                    player.level().getDifficulty(),
                    player.level().getWorldInfo().getTerrainType(),
                    player.interactionManager.getGameType()
                )
            )
            if (worldNew.provider is WorldProviderSpaceStation) {
                if (registeredSpaceStations!!.containsKey(dimID))  //TODO This has never been effective before due to the earlier bug - what does it actually do?
                {
                    val var2: NBTTagCompound = NBTTagCompound()
                    (SpaceStationWorldData.getStationData(worldNew, dimID, player))!!.writeToNBT(var2)
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_DATA,
                            GCCoreUtil.getDimensionID(player.level()),
                            arrayOf<Any>(dimID, var2)
                        ), player
                    )
                }
            }
            removeEntityFromWorld(worldOld, player, true)
            forceMoveEntityToPos(player, worldNew as WorldServer, spawnPos, true)
            GCLog.info(
                "Server attempting to transfer player " + PlayerUtil.getName(player) + " to dimension " + GCCoreUtil.getDimensionID(
                    worldNew
                )
            )

            player.server.getPlayerList().preparePlayer(player, worldOld as WorldServer)
            player.interactionManager.setWorld(worldNew)
            player.server.getPlayerList().updateTimeAndWeatherForPlayer(player, worldNew)
            player.server.getPlayerList().syncPlayerInventory(player)

            for (o: Any? in player.getActiveEffects()) {
                val var10: PotionEffect = o as PotionEffect
                player.connection.sendPacket(SPacketEntityEffect(player.getId(), var10))
            }

            player.connection.sendPacket(
                SPacketSetExperience(
                    player.experience,
                    player.experienceTotal,
                    player.experienceLevel
                )
            )
            FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimID, dimID)
        } else {
            forceMoveEntityToPos(player, worldNew as WorldServer, spawnPos, false)
            GCLog.info(
                "Server attempting to transfer player " + PlayerUtil.getName(player) + " within same dimension " + GCCoreUtil.getDimensionID(
                    worldNew
                )
            )
        }
        player.capabilities.isFlying = false
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON,
                GCCoreUtil.getDimensionID(player.level()),
                arrayOf<Any?>()
            ), player
        )

        // Update PlayerStatsGC
        val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
        GCPlayerHandler.setUsingParachute(player, stats, false)

        return player
    }


    /**
     * This correctly positions an entity at spawnPos in worldNew
     * loading and adding it to the chunk as required.
     *
     * @param entity
     * @param worldNew
     * @param spawnPos
     * @param spawnRequired - set this to true if the entity changed dimension so that a spawn in the new world is needed
     */
    @JvmStatic
    fun forceMoveEntityToPos(entity: Entity, worldNew: WorldServer, spawnPos: Vector3?, spawnRequired: Boolean) {
        CompatibilityManager.forceLoadChunks(worldNew)
        val pair: ChunkPos = worldNew.getChunk(spawnPos!!.intX() shr 4, spawnPos.intZ() shr 4).getPos()
        GCLog.debug("Loading first chunk in new dimension at " + pair.x + "," + pair.z)
        worldNew.getChunkProvider().loadChunk(pair.x, pair.z)
        entity.setLocationAndAngles((spawnPos)!!.x, (spawnPos)!!.y, (spawnPos)!!.z, entity.yRot, entity.xRot)
        val fromWorld: WorldServer? = (entity.level() as WorldServer?)
        if (spawnRequired) {
            (entity.level() as WorldServer).getEntityTracker().untrack(entity)
            entity.forceSpawn = true
            worldNew.addFreshEntity(entity)
            entity.setWorld(worldNew)
        }
        worldNew.updateEntityWithOptionalForce(entity, true)
        if (entity is EntityPlayerMP) {
            entity.connection.setPlayerLocation(
                (spawnPos)!!.x,
                (spawnPos)!!.y,
                (spawnPos)!!.z,
                entity.yRot,
                entity.xRot
            )
        }
        CompatibilityManager.forceLoadChunksEnd(worldNew)
    }

    @JvmStatic
    fun getStartWorld(unchanged: WorldServer?): WorldServer? {
        if (ConfigManagerCore.challengeSpawnHandling) {
            val wp: WorldProvider? = getProviderForNameServer("planet.asteroids")
            val worldNew: WorldServer? = if (wp == null) null else wp.world as WorldServer?
            if (worldNew != null) {
                return worldNew
            }
        }
        return unchanged
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun forceRespawnClient(dimID: Int, par2: Int, par3: String?, par4: Int): EntityPlayer? {
        val fakePacket: SPacketRespawn = SPacketRespawn(
            dimID,
            EnumDifficulty.byId(par2),
            WorldType.byName(par3),
            WorldSettings.getGameTypeById(par4)
        )
        FMLClientHandler.instance().getClient().player.connection.handleRespawn(fakePacket)
        return FMLClientHandler.instance().getClientPlayerEntity()
    }

    /**
     * This is similar to World.removeEntityDangerously() but without the risk of concurrent modification error
     */
    @JvmStatic
    private fun removeEntityFromWorld(var0: World, var1: Entity, directlyRemove: Boolean) {
        if (var1 is EntityPlayer) {
            val var2: EntityPlayer = var1
            var2.closeScreen()
            var0.players().remove(var2)
            var0.updateAllPlayersSleepingFlag()
        }

        val i: Int = var1.chunkCoordX
        val j: Int = var1.chunkCoordZ

        if (var1.addedToChunk && var0.isBlockLoaded(BlockPos(i shl 4, 63, j shl 4), true)) {
            var0.getChunk(i, j).removeEntity(var1)
        }

        if (directlyRemove) {
            val l: MutableList<Entity?> = ArrayList<Entity?>()
            l.add(var1)
            var0.unloadEntities(l)
            //This will automatically remove the entity from the world and the chunk prior to the world's next update entities tick
            //It is important NOT to directly modify World.loadedEntityList here, as the World will be currently iterating through that list when updating each entity (see the line "this.loadedEntityList.remove(i--);" in World.updateEntities()
        }

        var1.isDead = false
    }

    @JvmStatic
    fun getSpaceStationRecipe(planetID: Int): SpaceStationRecipe? {
        for (type: SpaceStationType in GalacticraftRegistry.getSpaceStationData()) {
            if (type.getWorldToOrbitID() == planetID) {
                return type.getRecipeForSpaceStation()
            }
        }

        return null
    }

    /**
     * This must return planets in the same order their provider IDs
     * were registered in GalacticraftRegistry by GalacticraftCore.
     */
    @JvmStatic
    fun getPlanetList(): MutableList<Any?> {
        val objList: MutableList<Any?> = ArrayList<Any?>()
        objList.add(getPlanetListInts())
        return objList
    }

    @JvmStatic
    fun getPlanetListInts(): Array<Int> {
        val iArray: Array<Int> = (arrayOfNulls<Int>(registeredPlanets!!.size) as Array<Int>)

        for (i: Int in iArray.indices) {
            iArray[i] = registeredPlanets!!.get(i)
        }

        return iArray
    }

    /**
     * What's important here is that Galacticraft and the server both register
     * the same reachable Galacticraft planets (and their provider types) in the same order.
     * See WorldUtil.registerPlanet().
     *
     * Even if there are dimension conflicts or other problems, the planets must be
     * registered in the same order on both client and server.  This should happen
     * automatically if Galacticraft versions match, and if planets modules
     * match  (including Galacticraft-Planets and any other sub-mods).
     *
     * It is NOT a good idea for sub-mods to make the registration order of planets variable
     * or dependent on configs.
     */
    @JvmStatic
    fun decodePlanetsListClient(data: MutableList<Any>) {
        try {
            if (ConfigManagerCore.enableDebug) {
                GCLog.info("GC connecting to server: received planets dimension ID list.")
            }
            if (registeredPlanets != null) {
                for (registeredID: Int in (registeredPlanets)!!) {
                    if (DimensionManager.isDimensionRegistered(registeredID)) {
                        DimensionManager.unregisterDimension(registeredID)
                    }
                }
            }
            registeredPlanets = ArrayList<Int>()

            var ids: String = ""
            if (data.size > 0) {
                //Start the provider index at offset 2 to skip the two Overworld Orbit dimensions
                //(this will be iterating through GalacticraftRegistry.worldProviderIDs)
                var providerIndex: Int = (GalaxyRegistry.getRegisteredSatellites())!!.size * 2
                if (data.get(0) is Int) {
                    for (o: Any in data) {
                        WorldUtil.registerPlanetClient((o as kotlin.Int?)!!, providerIndex)
                        providerIndex++
                        ids += o.toString() + " "
                    }
                } else if ((data.get(0) is Array<*> && Int::class.javaObjectType.isAssignableFrom(data.get(0)!!.javaClass.componentType))) {
                    for (o: Any in (data.get(0) as kotlin.Array<kotlin.Int>?)!!) {
                        WorldUtil.registerPlanetClient((o as kotlin.Int?)!!, providerIndex)
                        providerIndex++
                        ids += o.toString() + " "
                    }
                }
            }
            if (ConfigManagerCore.enableDebug) {
                GCLog.debug("GC clientside planet dimensions registered: " + ids)
                val dimMoon: WorldProvider? = getProviderForNameClient("moon.moon")
                if (dimMoon != null) {
                    GCLog.debug("Crosscheck: Moon is " + GCCoreUtil.getDimensionID(dimMoon))
                }
                val dimMars: WorldProvider? = getProviderForNameClient("planet.mars")
                if (dimMars != null) {
                    GCLog.debug("Crosscheck: Mars is " + GCCoreUtil.getDimensionID(dimMars))
                }
                val dimAst: WorldProvider? = getProviderForNameClient("planet.asteroids")
                if (dimAst != null) {
                    GCLog.debug("Crosscheck: Asteroids is " + GCCoreUtil.getDimensionID(dimAst))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getSpaceStationList(): MutableList<Any?> {
        val objList: MutableList<Any?> = ArrayList<Any?>()
        objList.add(getSpaceStationListInts())
        return objList
    }

    @JvmStatic
    fun getSpaceStationListInts(): Array<Int> {
        val iArray: Array<Int> = (arrayOfNulls<Int>(registeredSpaceStations!!.size * 2) as Array<Int>)

        var i: Int = 0
        for (e: MutableMap.MutableEntry<Int,Int> in registeredSpaceStations!!.entries) {
            iArray[i] = e.key
            iArray[i + 1] = e.value
            i += 2
        }

        //        for (int i = 0; i < iArray.length; i++)
//        {
//            iArray[i] = WorldUtil.registeredSpaceStations.get(i);
//        }
        return iArray
    }

    @JvmStatic
    fun decodeSpaceStationListClient(data: MutableList<Any?>) {
        try {
            if (registeredSpaceStations != null) {
                for (registeredID: Int in registeredSpaceStations!!.keys) {
                    DimensionManager.unregisterDimension(registeredID)
                }
            }
            registeredSpaceStations = Maps.newHashMap<Int,Int>()

            if (data.size > 0) {
                if (data.get(0) is Int) {
                    var i: Int = 0
                    while (i < data.size) {
                        WorldUtil.registerSSdim((data.get(i) as kotlin.Int?)!!, (data.get(i + 1) as kotlin.Int?)!!)
                        i += 2
                    }
                    //                    for (Object dimID : data)
//                    {
//                        registerSSdim((Integer) dimID);
//                    }
                } else if ((data.get(0) is Array<*> && Int::class.javaObjectType.isAssignableFrom(data.get(0)!!.javaClass.componentType))) {
                    val array: Array<Int> = (data.get(0) as Array<Int>)
                    var i: Int = 0
                    while (i < array.size) {
                        WorldUtil.registerSSdim(array[i]!!, array[i + 1]!!)
                        i += 2
                    }
                    //                    for (Object dimID : (Integer[]) data.get(0))
//                    {
//                        registerSSdim((Integer) dimID);
//                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    private fun registerSSdim(dimID: Int, providerKey: Int) {
        if (!registeredSpaceStations!!.containsKey(dimID)) {
            if (!DimensionManager.isDimensionRegistered(dimID)) {
                registeredSpaceStations!!.put(dimID, providerKey)
                DimensionManager.registerDimension(dimID, getDimensionTypeById(providerKey))
            } else {
                GCLog.severe("Dimension already registered on client: unable to register space station dimension " + dimID)
            }
        }
    }

    @JvmStatic
    fun toCelestialSelection(player: EntityPlayerMP, stats: GCPlayerStats, tier: Int) {
        player.dismountRidingEntity()
        stats.setSpaceshipTier(tier)

        val map: HashMap<String?,Int> = getArrayOfPossibleDimensions(tier, player)
        var dimensionList: String = ""
        var count: Int = 0
        for (entry: MutableMap.MutableEntry<String?,Int> in map.entries) {
            dimensionList = dimensionList + entry.key + (if (count < map.entries.size - 1) "?" else "")
            count++
        }

        val canCreateStations: Boolean = PermissionAPI.hasPermission(player, Constants.PERMISSION_CREATE_STATION)
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_DIMENSION_LIST,
                GCCoreUtil.getDimensionID(player.level()),
                arrayOf<Any?>(PlayerUtil.getName(player), dimensionList, canCreateStations)
            ), player
        )
        stats.setUsingPlanetSelectionGui(true)
        stats.setSavedPlanetList(dimensionList)
        val fakeEntity: Entity = EntityCelestialFake(player.level(), player.x, player.y, player.z)
        player.level().spawnEntity(fakeEntity)
        player.startRiding(fakeEntity)
    }

    @JvmStatic
    fun getFootprintPosition(world: World, rotation: Float, startPosition: Vector3, playerCenter: BlockVec3): Vector3 {
        val position: Vector3 = startPosition.clone()
        val footprintScale: Float = 0.375f

        var mainPosX: Int = position.intX()
        val mainPosY: Int = position.intY()
        var mainPosZ: Int = position.intZ()
        val posMain: BlockPos = BlockPos(mainPosX, mainPosY, mainPosZ)

        // If the footprint is hovering over air...
        if (world.getBlockState(posMain).getBlock().isAir(world.getBlockState(posMain), world, posMain)) {
            position.x += (playerCenter.x - mainPosX).toDouble()
            position.z += (playerCenter.z - mainPosZ).toDouble()

            val pos1: BlockPos = BlockPos(position.intX(), position.intY(), position.intZ())
            // If the footprint is still over air....
            val b2: Block = world.getBlockState(pos1).getBlock()
            if (b2 != null && b2.isAir(world.getBlockState(pos1), world, pos1)) {
                for (direction: EnumFacing in EnumFacing.values()) {
                    val offsetPos: BlockPos = posMain.offset(direction)
                    if (direction != EnumFacing.DOWN && direction != EnumFacing.UP) {
                        if (!world.getBlockState(offsetPos).getBlock()
                                .isAir(world.getBlockState(offsetPos), world, offsetPos)
                        ) {
                            position.x += direction.getStepX()
                            position.z += direction.getStepZ()
                            break
                        }
                    }
                }
            }
        }

        mainPosX = position.intX()
        mainPosZ = position.intZ()

        val x0: Double = (sin((45 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.x
        val x1: Double = (sin((135 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.x
        val x2: Double = (sin((225 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.x
        val x3: Double = (sin((315 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.x
        val z0: Double = (cos((45 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.z
        val z1: Double = (cos((135 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.z
        val z2: Double = (cos((225 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.z
        val z3: Double = (cos((315 - rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale) + position.z

        val xMin: Double = min(min(x0, x1), min(x2, x3))
        val xMax: Double = max(max(x0, x1), max(x2, x3))
        val zMin: Double = min(min(z0, z1), min(z2, z3))
        val zMax: Double = max(max(z0, z1), max(z2, z3))

        if (xMin < mainPosX) {
            position.x += mainPosX - xMin
        }

        if (xMax > mainPosX + 1) {
            position.x -= xMax - (mainPosX + 1)
        }

        if (zMin < mainPosZ) {
            position.z += mainPosZ - zMin
        }

        if (zMax > mainPosZ + 1) {
            position.z -= zMax - (mainPosZ + 1)
        }

        return position
    }

    @JvmStatic
    fun spaceStationDataToString(data: HashMap<Int,Int>): String {
        val builder: StringBuilder = StringBuilder()
        val it: MutableIterator<MutableMap.MutableEntry<Int,Int>> = data.entries.iterator()
        while (it.hasNext()) {
            val e: MutableMap.MutableEntry<Int,Int> = it.next()
            builder.append(e.key)
            builder.append("$")
            builder.append(e.value)
            if (it.hasNext()) {
                builder.append("?")
            }
        }
        return builder.toString()
    }

    @JvmStatic
    fun stringToSpaceStationData(input: String): HashMap<Int,Int> {
        val data: HashMap<Int,Int> = Maps.newHashMap<Int,Int>()
        if (!input.isEmpty()) {
            val str0: Array<String?> = input.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i: Int in str0.indices) {
                val str1: Array<String?> =
                    str0[i]!!.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                data.put(str1[0]!!.toInt(), str1[1]!!.toInt())
            }
        }
        return data
    }

    @JvmStatic
    fun getDimensionName(wp: WorldProvider): String? {
        if (wp is IGalacticraftWorldProvider) {
            val cb: CelestialBody? = (wp as IGalacticraftWorldProvider).getCelestialBody()
            if (cb != null && cb !is Satellite) {
                return cb.getTranslationKey()
            }
        }

        if (GCCoreUtil.getDimensionID(wp) == ConfigManagerCore.idDimensionOverworld) {
            return "overworld"
        }

        return wp.getDimensionType().getName()
    }

    @JvmStatic
    fun getAllChecklistKeys(): MutableMap<String?, MutableList<String?>?> {
        val checklistMap: MutableMap<String?, MutableList<String?>?> = Maps.newHashMap<String?, MutableList<String?>?>()

        for (planet: Planet in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
            if (planet.getReachable()) {
                checklistMap.put(planet.getTranslationKey(), planet.getChecklistKeys())
            }
        }

        for (moon: Moon in (GalaxyRegistry.getRegisteredMoons())!!.values) {
            if (moon.getReachable()) {
                checklistMap.put(moon.getTranslationKey(), moon.getChecklistKeys())
            }
        }

        for (satellite: Satellite in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
            if (satellite.getReachable()) {
                checklistMap.put(satellite.getTranslationKey(), satellite.getChecklistKeys())
            }
        }

        return checklistMap
    }

    @JvmStatic
    fun getDimensionTypeById(id: Int): DimensionType? {
        for (dimensiontype: DimensionType in DimensionType.values()) {
            if (dimensiontype.getId() == id) {
                return dimensiontype
            }
        }

        GCLog.severe("There was a problem getting WorldProvider type " + id)
        GCLog.severe("(possibly this is a conflict, check Galacticraft config.)")
        return null
    }

    @JvmStatic
    fun markAdjacentPadForUpdate(worldIn: World, pos: BlockPos) {
        var offsetPos: BlockPos
        for (dX: Int in -2..2) {
            for (dZ: Int in -2..2) {
                offsetPos = pos.offset(dX, 0, dZ)
                val blockState: IBlockState = worldIn.getBlockState(offsetPos)

                if (blockState.getBlock() === GCBlocks.landingPadFull) {
                    worldIn.sendBlockUpdated(offsetPos, blockState, blockState, 3)
                }
            }
        }
    }

    @JvmStatic
    fun setNextMorning(world: WorldServer) {
        if (world.provider is WorldProviderSpace) {
            val current: Long = (world.provider as WorldProviderSpace).preTickTime
            val dayLength: Long = (world.provider as WorldProviderSpace).getDayLength()
            if (dayLength <= 0) return
            world.setWorldTime(current - current % dayLength + dayLength)
        } else {
            val newTime: Long = world.getWorldTime()
            for (worldServer: WorldServer in (GCCoreUtil.getWorldServerList(world))!!) {
                if (worldServer === world) continue
                if (worldServer.provider is WorldProviderSpace) {
                    (worldServer.provider as WorldProviderSpace).adjustTime(newTime)
                }
            }
        }
    }
}
