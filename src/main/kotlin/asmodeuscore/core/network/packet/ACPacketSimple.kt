package asmodeuscore.core.network.packet

import java.util.Map
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import asmodeuscore.core.utils.ACCompatibilityManager
import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection
import asmodeuscore.core.event.AsmodeusClientEvent
import asmodeuscore.core.handler.ColorBlockHandler
import asmodeuscore.AsmodeusCore
import asmodeuscore.core.network.packet.ACPacketSimple
import asmodeuscore.core.network.packet.ACPacketSimple.ACEnumSimplePacket
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import io.netty.buffer.ByteBuf
import java.io.IOException
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.SpaceData
import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.NetworkUtil
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledDimensionChange
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.PacketBuffer
import net.minecraft.server.MinecraftServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Exception
import java.lang.RuntimeException
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.HashMap

open class ACPacketSimple : PacketBase, Packet<INetHandler> {
    enum class ACEnumSimplePacket
        (private val targetSide: Side, vararg decodeAs: Class<*>?) {
        // SERVER
        /**
         * Updates the gravity side of an artifical gravity block
         * - BlockVec3 pos: position of the block
         * - Integer gravityStrength
         */
        //CLIENT
        C_UPDATE_DIMENSION_LIST(
            Side.CLIENT,
            String::class.java,
            String::class.java,
            Boolean::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Boolean::class.java
        ),
        C_UPDATE_OXYGEN_STATUS(Side.CLIENT, Boolean::class.java),
        C_UPDATE_WORLD(Side.CLIENT),
        C_GET_CELESTIAL_BODY_LIST(Side.CLIENT),
        C_UPDATE_STORM_USER(
            Side.CLIENT,
            String::class.java,
            Boolean::class.java,
            Int::class.java,
            Int::class.java,
            Float::class.java,
            Float::class.java,
            Int::class.java
        ),

        //SERVER
        S_TELEPORT_ENTITY(Side.SERVER, Int::class.java, Int::class.java);


        private val decodeAs: Array<Class<*>?>

        init {
            this.decodeAs = (decodeAs as Array<Class<*>?>)
        }

        fun getTargetSide(): Side {
            return this.targetSide
        }

        fun getDecodeClasses(): Array<Class<*>?>? {
            return this.decodeAs
        }
    }

    private var type: ACEnumSimplePacket? = null
    private var data: MutableList<Any?>? = null

    constructor() : super()

    constructor(packetType: ACEnumSimplePacket, dimID: Int, vararg data: Any?) : this(
        packetType,
        dimID,
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: ACEnumSimplePacket, world: World, data: Array<out Any?>) : this(
        packetType,
        GCCoreUtil.getDimensionID(world),
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: ACEnumSimplePacket, dimID: Int, data: MutableList<Any?>) : super(dimID) {
        if (packetType.getDecodeClasses()!!.size != data.size) {
            GCLog.info("Simple Packet Core found data length different than packet type")
            RuntimeException().printStackTrace()
        }

        this.type = packetType
        this.data = data
    }

    override fun encodeInto(buffer: ByteBuf?) {
        super.encodeInto(buffer)
        (buffer)!!.writeInt(this.type!!.ordinal)

        try {
            NetworkUtil.encodeData(buffer, (this.data)!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.type = (ACEnumSimplePacket.entries)!![(buffer)!!.readInt()]

        try {
            if (this.type!!.getDecodeClasses()!!.size > 0) {
                this.data = NetworkUtil.decodeData(((this.type!!.getDecodeClasses())!! as Array<Class<*>>), buffer)
            }
            if ((buffer)!!.readableBytes() > 0) {
                GCLog.severe("AsmodeusCore packet length problem for packet type " + this.type.toString())
            }
        } catch (e: Exception) {
            System.err.println("[AsmodeusCore] Error handling simple packet type: " + this.type.toString() + " " + buffer.toString())
            e.printStackTrace()
        }
    }

    @SideOnly(Side.CLIENT)
    override fun handleClientSide(player: EntityPlayer?) {
        var playerBaseClient: EntityPlayerSP? = null

        if (player is EntityPlayerSP) {
            playerBaseClient = player
        }
        /*if(ACCompatibilityManager.isGalacticraftLoaded()) {
        	GCPlayerStatsClient stats = null;
        	stats = GCPlayerStatsClient.get(playerBaseClient);
        }*/
        /*else
        {/ *
            if (type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && type != EnumSimplePacket.C_UPDATE_CONFIGS)
            {
                return;
            }
        }*/
        lateinit var str: String
do {
when (this.type) {
            ACEnumSimplePacket.C_UPDATE_STORM_USER -> {
                val name: String? = this.data!!.get(0) as String?
                val isDustStorm: Boolean = this.data!!.get(1) as Boolean
                val tickStorm: Int = this.data!!.get(2) as Int
                val clearTick: Int = this.data!!.get(3) as Int
                val prevStrenghtStorm: Float = this.data!!.get(4) as Float
                val strenghtStorm: Float = this.data!!.get(5) as Float
                val type_storm: Int = this.data!!.get(6) as Int


                if (type_storm == 0) {
                    val msd: DustStormSaveData = DustStormSaveData.get((player)!!.world, name)
                    msd.setDustStorm(isDustStorm)
                    msd.tickDustStorm = tickStorm
                    msd.tickClearWeather = clearTick
                    msd.prevStormStrength = prevStrenghtStorm
                    msd.stormStrength = strenghtStorm
                }

                if (type_storm == 1) {
                    val msd: FrozenStormSaveData = FrozenStormSaveData.get((player)!!.world, name)
                    msd.setFrozenStorm(isDustStorm)
                    msd.tickStorm = tickStorm
                    msd.tickClearWeather = clearTick
                    msd.prevStormStrength = prevStrenghtStorm
                    msd.stormStrength = strenghtStorm
                }
            }

            ACEnumSimplePacket.C_UPDATE_DIMENSION_LIST -> if (this.data!!.get(0)
                    .toString() == PlayerUtil.getName(player)
            ) {
                val dimensionList: String = this.data!!.get(1) as String
                if (ConfigManagerCore.enableDebug) {
                    if (dimensionList != spamCheckString) {
                        GCLog.info("DEBUG info: " + dimensionList)
                        spamCheckString = dimensionList
                    }
                }
                val destinations: Array<String> =
                    dimensionList.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val possibleCelestialBodies: MutableList<CelestialBody?> = Lists.newArrayList<CelestialBody?>()
                val spaceStationData: MutableMap<Int, MutableMap<String?, GuiCelestialSelection.StationDataGUI?>?> =
                    Maps.newHashMap<Int, MutableMap<String?, GuiCelestialSelection.StationDataGUI?>?>()

                //                Map<String, String> spaceStationNames = Maps.newHashMap();
                //                Map<String, Integer> spaceStationIDs = Maps.newHashMap();
                //                Map<String, Integer> spaceStationHomes = Maps.newHashMap();
                for (str: String in destinations) {
                    var celestialBody: CelestialBody? = WorldUtil.getReachableCelestialBodiesForName(str)

                    if (celestialBody == null && str.contains("$")) {
                        val values: Array<String> =
                            str.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                        val homePlanetID: Int = values[4].toInt()

                        for (satellite: Satellite in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
                            if ((satellite.getParentPlanet())!!.getDimensionID() == homePlanetID) {
                                celestialBody = satellite
                                break
                            }
                        }

                        if (!spaceStationData.containsKey(homePlanetID)) {
                            spaceStationData.put(
                                homePlanetID,
                                HashMap<String?, GuiCelestialSelection.StationDataGUI?>()
                            )
                        }

                        spaceStationData.get(homePlanetID)!!
                            .put(values[1], GuiCelestialSelection.StationDataGUI(values[2], values[3].toInt()))


                        //                        spaceStationNames.put(values[1], values[2]);
                        //                        spaceStationIDs.put(values[1], Integer.parseInt(values[3]));
                        //                        spaceStationHomes.put(values[1], Integer.parseInt(values[4]));
                    }

                    if (celestialBody != null) {
                        possibleCelestialBodies.add(celestialBody)
                    }
                }

                if (FMLClientHandler.instance().getClient().world != null) {
                    val data: SpaceData = SpaceData()
                    data.addToMap(data.rocket_tier, this.data!!.get(3) as Int)
                    data.addToMap(data.rocket_engine, Engine_Type.byID(this.data!!.get(4) as Int))
                    data.addToMap(data.rocket_count_fuel, this.data!!.get(5) as Int)
                    data.addToMap(data.second_passenger, this.data!!.get(6) as Boolean)

                    if (FMLClientHandler.instance().getClient().currentScreen !is NewGuiCelestialSelection) {
                        val gui: NewGuiCelestialSelection = NewGuiCelestialSelection(
                            false,
                            possibleCelestialBodies,
                            (this.data!!.get(2) as kotlin.Boolean?)!!,
                            data
                        )
                        gui.spaceStationMap = spaceStationData
                        //                        gui.spaceStationNames = spaceStationNames;
                        //                        gui.spaceStationIDs = spaceStationIDs;
                        FMLClientHandler.instance().getClient().displayGuiScreen(gui)
                    } else {
                        (FMLClientHandler.instance()
                            .getClient().currentScreen as NewGuiCelestialSelection).possibleBodies =
                            possibleCelestialBodies
                        (FMLClientHandler.instance()
                            .getClient().currentScreen as NewGuiCelestialSelection).spaceStationMap = spaceStationData
                        (FMLClientHandler.instance().getClient().currentScreen as NewGuiCelestialSelection).data = data
                        //                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationNames = spaceStationNames;
                        //                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationIDs = spaceStationIDs;
                    }
                }
            }

            ACEnumSimplePacket.C_UPDATE_WORLD -> {
                //FIXME: NOT WORK ON DEDICATED SERVERS!!!
                val server: MinecraftServer? = FMLCommonHandler.instance().getMinecraftServerInstance()


                //System.out.println(player.world);
                if (server == null) break

                if (playerBaseClient == null) break

                val world: World = server.getWorld(this.getDimensionID())
                if (world != null) {
                    ColorBlockHandler.Companion.world = world
                }
            }

            ACEnumSimplePacket.C_UPDATE_OXYGEN_STATUS -> AsmodeusClientEvent.Companion.inOxygenBlock =
                (this.data!!.get(0) as Boolean?)!!

            ACEnumSimplePacket.C_GET_CELESTIAL_BODY_LIST -> {
                str = ""

                val bodies: MutableList<MutableCollection<out CelestialBody?>> =
                    Arrays.asList<MutableCollection<out CelestialBody?>>(
                        (GalaxyRegistry.getRegisteredPlanets())!!.values,
                        (GalaxyRegistry.getRegisteredMoons())!!.values,
                        (GalaxyRegistry.getRegisteredSatellites())!!.values
                    )

                for (body in bodies) {
                    for (cBody in body) {
                        val size: Int = ByteBufUtils.varIntByteCount(str.toByteArray(StandardCharsets.UTF_8).size)
                        if (size >= 2 && size < 3) {
                            (GalacticraftCore.packetPipeline)!!.sendToServer(
                                PacketSimple(
                                    PacketSimple.EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE,
                                    getDimensionID(),
                                    arrayOf<Any>(str)
                                )
                            )
                            str = ""
                        }

                        str = str + (cBody)!!.getTranslationKey() + ";"
                    }
                }

                for (solarSystem in (GalaxyRegistry.getRegisteredSolarSystems())!!.values) {
                    val size: Int = ByteBufUtils.varIntByteCount(str.toByteArray(StandardCharsets.UTF_8).size)
                    if (size >= 2 && size < 3) {
                        (GalacticraftCore.packetPipeline)!!.sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE,
                                getDimensionID(),
                                arrayOf<Any>(str)
                            )
                        )
                        str = ""
                    }
                    str = str + (solarSystem)!!.getTranslationKey() + ";"
                }

                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE,
                        getDimensionID(),
                        arrayOf<Any>(str)
                    )
                )
            }

            else -> {}
        }
} while (false)
    }

    override fun handleServerSide(player: EntityPlayer?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        if (playerBase == null) {
            return
        }

        val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!
        var tileEntity: TileEntity?

        when (this.type) {
            ACEnumSimplePacket.S_TELEPORT_ENTITY -> {
                stats.setFuelLevel((this.data!!.get(1) as kotlin.Int?)!!)
                TickHandlerServer.scheduleNewDimensionChange(
                    ScheduledDimensionChange(
                        playerBase,
                        (this.data!!.get(0) as Int?)!!
                    )
                )
            }

            else -> {}
        }
    }

    /*
     *
	 * BEGIN "net.minecraft.network.Packet" IMPLEMENTATION
	 * 
	 * This is for handling server->client packets before the player has joined the world
	 * 
	 */
    override fun readPacketData(var1: PacketBuffer) {
        this.decodeInto(var1)
    }

    override fun writePacketData(var1: PacketBuffer) {
        this.encodeInto(var1)
    }

    @SideOnly(Side.CLIENT)
    override fun processPacket(var1: INetHandler) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            this.handleClientSide(FMLClientHandler.instance().getClientPlayerEntity())
        }
    } /*
	 * 
	 * END "net.minecraft.network.Packet" IMPLEMENTATION
	 * 
	 * This is for handling server->client packets before the player has joined the world
	 * 
	 */

    companion object {
        private var spamCheckString: String? = null
    }
}
