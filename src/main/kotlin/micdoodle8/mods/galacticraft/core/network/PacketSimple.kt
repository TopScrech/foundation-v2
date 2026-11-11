package micdoodle8.mods.galacticraft.core.network

import net.minecraft.network.chat.Component
import micdoodle8.mods.galacticraft.api.transmission.grid.splitUntyped

import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase

import io.netty.buffer.ByteBuf
import java.io.IOException
import com.google.common.collect.Lists
import com.mojang.authlib.GameProfile
import com.google.common.collect.Iterables
import com.google.common.collect.Maps
import com.mojang.authlib.properties.Property
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.tile.ITileClientUpdates
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass
import micdoodle8.mods.galacticraft.core.client.FootprintRenderer
import micdoodle8.mods.galacticraft.core.client.fx.ParticleSparks
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiBuggy
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiParaChest
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.command.CommandGCEnergyUnits
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy
import micdoodle8.mods.galacticraft.core.entities.IBubbleProvider
import micdoodle8.mods.galacticraft.core.entities.IControllableEntity
import micdoodle8.mods.galacticraft.core.entities.player.GCEntityPlayerMP
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.inventory.ContainerSchematic
import micdoodle8.mods.galacticraft.core.inventory.IInventorySettable
import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLockController
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.MapUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledDimensionChange
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.settings.GameSettings
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.INetHandler
import net.minecraft.network.protocol.Packet
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.server.SPacketRespawn
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Style

import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashMap

open class PacketSimple : PacketBase, Packet<INetHandler> {
    enum class EnumSimplePacket
        (private val targetSide: Side, vararg decodeAs: Class<*>?) {
        // SERVER
        S_RESPAWN_PLAYER(Side.SERVER, String::class.java),
        S_TELEPORT_ENTITY(Side.SERVER, String::class.java),
        S_IGNITE_ROCKET(Side.SERVER),
        S_OPEN_SCHEMATIC_PAGE(Side.SERVER, Int::class.java, Int::class.java, Int::class.java, Int::class.java),
        S_OPEN_FUEL_GUI(Side.SERVER, String::class.java),
        S_UPDATE_SHIP_YAW(Side.SERVER, Float::class.java),
        S_UPDATE_SHIP_PITCH(Side.SERVER, Float::class.java),
        S_SET_ENTITY_FIRE(Side.SERVER, Int::class.java),
        S_BIND_SPACE_STATION_ID(Side.SERVER, Int::class.java),
        S_UNLOCK_NEW_SCHEMATIC(Side.SERVER),
        S_UPDATE_DISABLEABLE_BUTTON(Side.SERVER, BlockPos::class.java, Int::class.java),
        S_ON_FAILED_CHEST_UNLOCK(Side.SERVER, Int::class.java),
        S_RENAME_SPACE_STATION(Side.SERVER, String::class.java, Int::class.java),
        S_OPEN_EXTENDED_INVENTORY(Side.SERVER),
        S_ON_ADVANCED_GUI_CLICKED_INT(Side.SERVER, Int::class.java, BlockPos::class.java, Int::class.java),
        S_ON_ADVANCED_GUI_CLICKED_STRING(Side.SERVER, Int::class.java, BlockPos::class.java, String::class.java),
        S_UPDATE_SHIP_MOTION_Y(Side.SERVER, Int::class.java, Boolean::class.java),
        S_START_NEW_SPACE_RACE(
            Side.SERVER,
            Int::class.java,
            String::class.java,
            FlagData::class.java,
            Vector3::class.java,
            Array<String>::class.java
        ),
        S_REQUEST_FLAG_DATA(Side.SERVER, String::class.java),
        S_INVITE_RACE_PLAYER(Side.SERVER, String::class.java, Int::class.java),
        S_REMOVE_RACE_PLAYER(Side.SERVER, String::class.java, Int::class.java),
        S_ADD_RACE_PLAYER(Side.SERVER, String::class.java, Int::class.java),
        S_COMPLETE_CBODY_HANDSHAKE(Side.SERVER, String::class.java),
        S_REQUEST_GEAR_DATA(Side.SERVER, String::class.java),
        S_REQUEST_OVERWORLD_IMAGE(Side.SERVER),
        S_REQUEST_MAP_IMAGE(Side.SERVER, Int::class.java, Int::class.java, Int::class.java),
        S_REQUEST_PLAYERSKIN(Side.SERVER, String::class.java),
        S_BUILDFLAGS_UPDATE(Side.SERVER, Int::class.java),
        S_CONTROL_ENTITY(Side.SERVER, Int::class.java),
        S_NOCLIP_PLAYER(Side.SERVER, Boolean::class.java),
        S_REQUEST_DATA(Side.SERVER, Int::class.java, BlockPos::class.java),
        S_UPDATE_CHECKLIST(Side.SERVER, NBTTagCompound::class.java),
        S_REQUEST_MACHINE_DATA(Side.SERVER, BlockPos::class.java),

        // CLIENT
        C_AIR_REMAINING(Side.CLIENT, Int::class.java, Int::class.java, String::class.java),
        C_UPDATE_DIMENSION_LIST(Side.CLIENT, String::class.java, String::class.java, Boolean::class.java),
        C_SPAWN_SPARK_PARTICLES(Side.CLIENT, BlockPos::class.java),
        C_UPDATE_GEAR_SLOT(Side.CLIENT, String::class.java, Int::class.java, Int::class.java, Int::class.java),
        C_CLOSE_GUI(Side.CLIENT),
        C_RESET_THIRD_PERSON(Side.CLIENT),
        C_UPDATE_SPACESTATION_LIST(Side.CLIENT, Array<Int>::class.java),
        C_UPDATE_SPACESTATION_DATA(Side.CLIENT, Int::class.java, NBTTagCompound::class.java),
        C_UPDATE_SPACESTATION_CLIENT_ID(Side.CLIENT, String::class.java),
        C_UPDATE_PLANETS_LIST(Side.CLIENT, Array<Int>::class.java),
        C_UPDATE_CONFIGS(
            Side.CLIENT,
            Int::class.java,
            Double::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            String::class.java,
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Int::class.java,
            Array<String>::class.java
        ),
        C_UPDATE_STATS(
            Side.CLIENT,
            Int::class.java,
            String::class.java,
            Int::class.java,
            String::class.java,
            Int::class.java,
            String::class.java,
            Int::class.java,
            String::class.java,
            Int::class.java,
            String::class.java,
            Int::class.java,
            Int::class.java
        ),  //Note: Integer, PANELTYPES_LENGTH * <String, Integer>, Integer - see StatsCapability.getMiscNetworkedStats()
        C_ADD_NEW_SCHEMATIC(Side.CLIENT, Int::class.java),
        C_UPDATE_SCHEMATIC_LIST(Side.CLIENT, Array<Int>::class.java),
        C_PLAY_SOUND_BOSS_DEATH(Side.CLIENT, Float::class.java),
        C_PLAY_SOUND_EXPLODE(Side.CLIENT),
        C_PLAY_SOUND_BOSS_LAUGH(Side.CLIENT),
        C_PLAY_SOUND_BOW(Side.CLIENT),
        C_UPDATE_OXYGEN_VALIDITY(Side.CLIENT, Boolean::class.java),
        C_OPEN_PARACHEST_GUI(Side.CLIENT, Int::class.java, Int::class.java, Int::class.java),
        C_UPDATE_WIRE_BOUNDS(Side.CLIENT, BlockPos::class.java),
        C_OPEN_SPACE_RACE_GUI(Side.CLIENT),
        C_UPDATE_SPACE_RACE_DATA(
            Side.CLIENT,
            Int::class.java,
            String::class.java,
            FlagData::class.java,
            Vector3::class.java,
            Array<String>::class.java
        ),
        C_OPEN_JOIN_RACE_GUI(Side.CLIENT, Int::class.java),
        C_UPDATE_FOOTPRINT_LIST(Side.CLIENT, Long::class.java, Array<Footprint>::class.java),
        C_UPDATE_DUNGEON_DIRECTION(Side.CLIENT, Float::class.java),
        C_FOOTPRINTS_REMOVED(Side.CLIENT, Long::class.java, BlockVec3::class.java),
        C_UPDATE_STATION_SPIN(Side.CLIENT, Float::class.java, Boolean::class.java),
        C_UPDATE_STATION_DATA(Side.CLIENT, Double::class.java, Double::class.java),
        C_UPDATE_STATION_BOX(
            Side.CLIENT,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        ),
        C_UPDATE_THERMAL_LEVEL(Side.CLIENT, Int::class.java, Boolean::class.java),
        C_DISPLAY_ROCKET_CONTROLS(Side.CLIENT),
        C_GET_CELESTIAL_BODY_LIST(Side.CLIENT),
        C_UPDATE_ENERGYUNITS(Side.CLIENT, Int::class.java),
        C_RESPAWN_PLAYER(Side.CLIENT, String::class.java, Int::class.java, String::class.java, Int::class.java),
        C_UPDATE_TELEMETRY(
            Side.CLIENT,
            BlockPos::class.java,
            String::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            String::class.java
        ),
        C_SEND_PLAYERSKIN(Side.CLIENT, String::class.java, String::class.java, String::class.java, String::class.java),
        C_SEND_OVERWORLD_IMAGE(Side.CLIENT, Int::class.java, Int::class.java, ByteArray::class.java),
        C_RECOLOR_PIPE(Side.CLIENT, BlockPos::class.java),
        C_RECOLOR_ALL_GLASS(
            Side.CLIENT,
            Int::class.java,
            Int::class.java,
            Int::class.java
        ),  //Number of integers to match number of different blocks of PLAIN glass individually instanced and registered in GCBlocks
        C_UPDATE_MACHINE_DATA(
            Side.CLIENT,
            BlockPos::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        ),
        C_LEAK_DATA(Side.CLIENT, BlockPos::class.java, Array<Int>::class.java);

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

    private var type: EnumSimplePacket? = null
    private var data: MutableList<Any?>? = null

    constructor() : super()

    constructor(packetType: EnumSimplePacket, dimID: Int, data: Array<out Any?>) : this(
        packetType,
        dimID,
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: EnumSimplePacket, world: World, data: Array<out Any?>) : this(
        packetType,
        GCCoreUtil.getDimensionID(world),
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: EnumSimplePacket, dimID: Int, data: MutableList<Any?>) : super(dimID) {
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
            NetworkUtil.encodeData((buffer)!!, (this.data)!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.type = (EnumSimplePacket.entries)!![(buffer)!!.readInt()]

        try {
            if (this.type!!.getDecodeClasses()!!.size > 0) {
                this.data = NetworkUtil.decodeData(((this.type!!.getDecodeClasses())!! as Array<Class<*>>), (buffer)!!)
            }
            if ((buffer)!!.readableBytes() > 0 && (buffer)!!.writerIndex() < 0xfff00) {
                GCLog.severe("Galacticraft packet length problem for packet type " + this.type.toString())
            }
        } catch (e: Exception) {
            System.err.println("[Galacticraft] Error handling simple packet type: " + this.type.toString() + " " + buffer.toString())
            e.printStackTrace()
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun handleClientSide(player: EntityPlayer?) {
        var playerBaseClient: EntityPlayerSP? = null
        var stats: GCPlayerStatsClient? = null

        if (player is EntityPlayerSP) {
            playerBaseClient = player
            stats = GCPlayerStatsClient.get(playerBaseClient)
        } else {
            if (type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && type != EnumSimplePacket.C_UPDATE_CONFIGS) {
                return
            }
        }

        var tile: TileEntity? = null
lateinit var str: String
when (this.type) {
            EnumSimplePacket.C_AIR_REMAINING -> if (this.data!!.get(2).toString() == PlayerUtil.getName(player)
                    .toString()
            ) {
                TickHandlerClient.Companion.airRemaining = (this.data!!.get(0) as Int?)!!
                TickHandlerClient.Companion.airRemaining2 = (this.data!!.get(1) as Int?)!!
            }

            EnumSimplePacket.C_UPDATE_DIMENSION_LIST -> if (this.data!!.get(0)
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

                if (Minecraft.getInstance().level != null) {
                    if (Minecraft.getInstance().gui.screen() !is GuiCelestialSelection) {
                        val gui: GuiCelestialSelection = GuiCelestialSelection(
                            false,
                            possibleCelestialBodies,
                            (this.data!!.get(2) as kotlin.Boolean?)!!
                        )
                        gui.spaceStationMap = spaceStationData
                        //                        gui.spaceStationNames = spaceStationNames;
//                        gui.spaceStationIDs = spaceStationIDs;
                        Minecraft.getInstance().gui.setScreen(gui)
                    } else {
                        (FMLClientHandler.instance()
                            .getClient().currentScreen as GuiCelestialSelection).possibleBodies =
                            possibleCelestialBodies
                        (FMLClientHandler.instance()
                            .getClient().currentScreen as GuiCelestialSelection).spaceStationMap = spaceStationData
                        //                        ((GuiCelestialSelection) Minecraft.getInstance().gui.screen()).spaceStationNames = spaceStationNames;
//                        ((GuiCelestialSelection) Minecraft.getInstance().gui.screen()).spaceStationIDs = spaceStationIDs;
                    }
                }
            }

            EnumSimplePacket.C_SPAWN_SPARK_PARTICLES -> {
                val pos: BlockPos = this.data!!.get(0) as BlockPos
                val mc: Minecraft = Minecraft.getInstance()

                var i: Int = 0
                while (i < 4) {
                    if (mc.getRenderViewEntity() != null && mc.effectRenderer != null && mc.world != null) {
                        val fx: ParticleSparks = ParticleSparks(
                            mc.world,
                            pos.getX() - 0.15 + 0.5,
                            pos.getY() + 1.2,
                            pos.getZ() + 0.15 + 0.5,
                            mc.world.rand.nextDouble() / 20 - mc.world.rand.nextDouble() / 20,
                            mc.world.rand.nextDouble() / 20 - mc.world.rand.nextDouble() / 20
                        )

                        mc.effectRenderer.addEffect(fx)
                    }
                    i++
                }
            }

            EnumSimplePacket.C_UPDATE_GEAR_SLOT -> {
                val subtype: Int = this.data!!.get(3) as Int
                val gearName: String = this.data!!.get(0) as String

                val gearDataPlayer: EntityPlayer? = player!!.level().getPlayerEntityByName(gearName)

                if (gearDataPlayer != null) {
                    var gearData: PlayerGearData? =
                        ClientProxyCore.Companion.playerItemData.get(PlayerUtil.getName(gearDataPlayer))

                    if (gearData == null) {
                        gearData = PlayerGearData(player)
                        if (!ClientProxyCore.Companion.gearDataRequests.contains(gearName)) {
                            (GalacticraftCore.packetPipeline)!!.sendToServer(
                                PacketSimple(
                                    EnumSimplePacket.S_REQUEST_GEAR_DATA,
                                    getDimensionID(),
                                    arrayOf<Any>(gearName)
                                )
                            )
                            ClientProxyCore.Companion.gearDataRequests.add(gearName)
                        }
                    } else {
                        ClientProxyCore.Companion.gearDataRequests.remove(gearName)
                    }

                    val type: EnumExtendedInventorySlot =
                        EnumExtendedInventorySlot.entries[(this.data!!.get(2) as kotlin.Int?)!!]
                    val typeChange: GCPlayerHandler.EnumModelPacketType? =
                        micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler.EnumModelPacketType.entries.toTypedArray()[(this.data!!.get(
                            1
                        ) as Int?)!!]

                    when (type) {
                        EnumExtendedInventorySlot.MASK -> gearData.setMask(subtype)
                        EnumExtendedInventorySlot.GEAR -> gearData.setGear(subtype)
                        EnumExtendedInventorySlot.LEFT_TANK -> gearData.setLeftTank(subtype)
                        EnumExtendedInventorySlot.RIGHT_TANK -> gearData.setRightTank(subtype)
                        EnumExtendedInventorySlot.PARACHUTE -> if (typeChange == GCPlayerHandler.EnumModelPacketType.ADD) {
                            val name: String?

                            if (subtype != -1) {
                                name = ItemParaChute.names[subtype]
                                gearData.setParachute(
                                    ResourceLocation(
                                        Constants.ASSET_PREFIX,
                                        "textures/model/parachute/" + name + ".png"
                                    )
                                )
                            }
                        } else {
                            gearData.setParachute(null)
                        }

                        EnumExtendedInventorySlot.FREQUENCY_MODULE -> gearData.setFrequencyModule(subtype)
                        EnumExtendedInventorySlot.THERMAL_HELMET -> gearData.setThermalPadding(0, subtype)
                        EnumExtendedInventorySlot.THERMAL_CHESTPLATE -> gearData.setThermalPadding(1, subtype)
                        EnumExtendedInventorySlot.THERMAL_LEGGINGS -> gearData.setThermalPadding(2, subtype)
                        EnumExtendedInventorySlot.THERMAL_BOOTS -> gearData.setThermalPadding(3, subtype)
                        EnumExtendedInventorySlot.SHIELD_CONTROLLER -> gearData.setShieldController(subtype)
                        else -> {}
                    }

                    ClientProxyCore.Companion.playerItemData.put(gearName, gearData)
                }
            }

            EnumSimplePacket.C_CLOSE_GUI -> Minecraft.getInstance().gui.setScreen(null)
            EnumSimplePacket.C_RESET_THIRD_PERSON -> FMLClientHandler.instance()
                .getClient().gameSettings.thirdPersonView = stats!!.getThirdPersonView()

            EnumSimplePacket.C_UPDATE_SPACESTATION_LIST -> WorldUtil.decodeSpaceStationListClient((data)!!)
            EnumSimplePacket.C_UPDATE_SPACESTATION_DATA -> {
                val var4: SpaceStationWorldData = SpaceStationWorldData.getMPSpaceStationData(
                    player!!.level(),
                    (this.data!!.get(0) as kotlin.Int?)!!,
                    player
                )
                var4.readFromNBT((this.data!!.get(1) as NBTTagCompound?)!!)
            }

            EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID -> ClientProxyCore.Companion.clientSpaceStationID =
                WorldUtil.stringToSpaceStationData((this.data!!.get(0) as String?)!!)

            EnumSimplePacket.C_UPDATE_PLANETS_LIST -> WorldUtil.decodePlanetsListClient(((data)!! as MutableList<Any>))
            EnumSimplePacket.C_UPDATE_CONFIGS -> {
                ConfigManagerCore.saveClientConfigOverrideable()
                ConfigManagerCore.setConfigOverride((data)!!)
            }

            EnumSimplePacket.C_ADD_NEW_SCHEMATIC -> {
                val page: ISchematicPage? =
                    SchematicRegistry.getMatchingRecipeForID((this.data!!.get(0) as kotlin.Int?)!!)
                if (!(stats!!.getUnlockedSchematics())!!.contains(page)) {
                    (stats.getUnlockedSchematics())!!.add((page)!!)
                }
            }

            EnumSimplePacket.C_UPDATE_SCHEMATIC_LIST -> for (o: Any? in this.data!!) {
                val schematicID: Int = o as Int

                if (schematicID != -2) {
                    Collections.sort<ISchematicPage>((stats!!.getUnlockedSchematics())!!)

                    if (!(stats.getUnlockedSchematics())!!
                            .contains(SchematicRegistry.getMatchingRecipeForID(schematicID))
                    ) {
                        (stats.getUnlockedSchematics())!!.add((SchematicRegistry.getMatchingRecipeForID(schematicID))!!)
                    }
                }
            }

            EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH -> player!!.playSound(
                GCSounds.bossDeath,
                10.0f,
                (this.data!!.get(0) as kotlin.Float?)!!
            )

            EnumSimplePacket.C_PLAY_SOUND_EXPLODE -> player!!.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 10.0f, 0.7f)
            EnumSimplePacket.C_PLAY_SOUND_BOSS_LAUGH -> player!!.playSound(GCSounds.bossLaugh, 10.0f, 0.2f)
            EnumSimplePacket.C_PLAY_SOUND_BOW -> player!!.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 10.0f, 0.2f)
            EnumSimplePacket.C_UPDATE_OXYGEN_VALIDITY -> stats!!.setOxygenSetupValid((this.data!!.get(0) as kotlin.Boolean?)!!)
            EnumSimplePacket.C_OPEN_PARACHEST_GUI -> when (this.data!!.get(1) as Int?) {
                0 -> if (player!!.getVehicle() is EntityBuggy) {
                    Minecraft.getInstance().gui.setScreen(
                        GuiBuggy(
                            player.inventory,
                            (player.getVehicle() as EntityBuggy?)!!,
                            (player.getVehicle() as EntityBuggy).getType()
                        )
                    )
                    player.openContainer.windowId = ((this.data)!!.get(0) as kotlin.Int?)!!
                }

                1 -> {
                    val entityID: Int = this.data!!.get(2) as Int
                    val entity: Entity? = player!!.level().getEntityByID(entityID)

                    if (entity != null && entity is IInventorySettable) {
                        Minecraft.getInstance()
                            .displayGuiScreen(GuiParaChest(player.inventory, entity as IInventorySettable))
                    }

                    player.openContainer.windowId = ((this.data)!!.get(0) as kotlin.Int?)!!
                }
            }

            EnumSimplePacket.C_UPDATE_WIRE_BOUNDS -> {
                tile = player!!.level().getBlockEntity(this.data!!.get(0) as BlockPos?)

                if (tile is TileBaseConductor) {
                    tile.adjacentConnections = null
                    //                player.world.getBlockState(tile.getPos()).getBlock().setBlockBoundsBasedOnState(player.world, tile.getPos()); TODO
                }
            }

            EnumSimplePacket.C_OPEN_SPACE_RACE_GUI -> if (Minecraft.getInstance().gui.screen() == null) {
                TickHandlerClient.Companion.spaceRaceGuiScheduled = false
                player!!.openGui(
                    GalacticraftCore.instance,
                    GuiIdsCore.SPACE_RACE_START,
                    player.level(),
                    player.x.toInt(),
                    player.y.toInt(),
                    player.z.toInt()
                )
            } else {
                TickHandlerClient.Companion.spaceRaceGuiScheduled = true
            }

            EnumSimplePacket.C_UPDATE_SPACE_RACE_DATA -> {
                val teamID: Int = this.data!!.get(0) as Int
                val teamName: String? = this.data!!.get(1) as String?
                val flagData: FlagData? = this.data!!.get(2) as FlagData?
                val teamColor: Vector3? = this.data!!.get(3) as Vector3?
                val playerList: MutableList<String?> = ArrayList<String?>()

                var i: Int = 4
                while (i < this.data!!.size) {
                    val playerName: String? = this.data!!.get(i) as String?
                    ClientProxyCore.Companion.flagRequestsSent.remove(playerName)
                    playerList.add(playerName)
                    i++
                }

                val race: SpaceRace = SpaceRace(playerList, teamName, flagData, teamColor)
                race.setSpaceRaceID(teamID)
                SpaceRaceManager.addSpaceRace(race)
            }

            EnumSimplePacket.C_OPEN_JOIN_RACE_GUI -> {
                stats!!.setSpaceRaceInviteTeamID((this.data!!.get(0) as kotlin.Int?)!!)
                player!!.openGui(
                    GalacticraftCore.instance,
                    GuiIdsCore.SPACE_RACE_JOIN,
                    player.level(),
                    player.x.toInt(),
                    player.y.toInt(),
                    player.z.toInt()
                )
            }

            EnumSimplePacket.C_UPDATE_DUNGEON_DIRECTION -> stats!!.setDungeonDirection((this.data!!.get(0) as kotlin.Float?)!!)
            EnumSimplePacket.C_UPDATE_FOOTPRINT_LIST -> {
                val printList: MutableList<Footprint?> = ArrayList<Footprint?>()
                val chunkKey: Long = this.data!!.get(0) as Long
                var i: Int = 1
                while (i < this.data!!.size) {
                    val print: Footprint = this.data!!.get(i) as Footprint
                    if (print.owner != player!!.getName()) {
                        printList.add(print)
                    }
                    i++
                }
                FootprintRenderer.setFootprints(chunkKey, printList)
            }

            EnumSimplePacket.C_FOOTPRINTS_REMOVED -> {
                val chunkKey0: Long = this.data!!.get(0) as Long
                val position: BlockVec3 = this.data!!.get(1) as BlockVec3
                val footprintList: MutableList<Footprint>? = (FootprintRenderer.footprints.get(chunkKey0) as MutableList<Footprint>?)
                val toRemove: MutableList<Footprint?> = ArrayList<Footprint?>()

                if (footprintList != null) {
                    for (footprint: Footprint in footprintList) {
                        if ((footprint.position)!!.x > position.x && (footprint.position)!!.x < position.x + 1 && (footprint.position)!!.z > position.z && (footprint.position)!!.z < position.z + 1) {
                            toRemove.add(footprint)
                        }
                    }
                }

                if (!toRemove.isEmpty()) {
                    footprintList!!.removeAll(toRemove)
                    FootprintRenderer.footprints.put(chunkKey0, (footprintList as MutableList<Footprint?>))
                }
            }

            EnumSimplePacket.C_UPDATE_STATION_SPIN -> if (playerBaseClient!!.level().provider is WorldProviderSpaceStation) {
                (playerBaseClient.level().provider as WorldProviderSpaceStation).getSpinManager()
                    .setSpinRate((this.data!!.get(0) as kotlin.Float?)!!, (this.data!!.get(1) as kotlin.Boolean?)!!)
            }

            EnumSimplePacket.C_UPDATE_STATION_DATA -> if (playerBaseClient!!.level().provider is WorldProviderSpaceStation) {
                (playerBaseClient.level().provider as WorldProviderSpaceStation).getSpinManager()
                    .setSpinCentre((this.data!!.get(0) as kotlin.Double?)!!, (this.data!!.get(1) as kotlin.Double?)!!)
            }

            EnumSimplePacket.C_UPDATE_STATION_BOX -> if (playerBaseClient!!.level().provider is WorldProviderSpaceStation) {
                (playerBaseClient.level().provider as WorldProviderSpaceStation).getSpinManager().setSpinBox(
                    (this.data!!.get(0) as kotlin.Int?)!!,
                    (this.data!!.get(1) as kotlin.Int?)!!,
                    (this.data!!.get(2) as kotlin.Int?)!!,
                    (this.data!!.get(3) as kotlin.Int?)!!,
                    (this.data!!.get(4) as kotlin.Int?)!!,
                    (this.data!!.get(5) as kotlin.Int?)!!
                )
            }

            EnumSimplePacket.C_UPDATE_THERMAL_LEVEL -> {
                stats!!.setThermalLevel((this.data!!.get(0) as kotlin.Int?)!!)
                stats.setThermalLevelNormalising((this.data!!.get(1) as kotlin.Boolean?)!!)
            }

            EnumSimplePacket.C_DISPLAY_ROCKET_CONTROLS -> {
                player!!.sendSystemMessage(
                    Component.literal(
                        GameSettings.getKeyDisplayString((KeyHandlerClient.Companion.spaceKey)!!.getKeyCode()) + "  - " + GCCoreUtil.translate(
                            "gui.rocket.launch.name"
                        )
                    )
                )
                player.sendSystemMessage(
                    Component.literal(
                        GameSettings.getKeyDisplayString((KeyHandlerClient.Companion.leftKey)!!.getKeyCode()) + " / " + GameSettings.getKeyDisplayString(
                            (KeyHandlerClient.Companion.rightKey)!!.getKeyCode()
                        ) + "  - " + GCCoreUtil.translate("gui.rocket.turn.name")
                    )
                )
                player.sendSystemMessage(
                    Component.literal(
                        GameSettings.getKeyDisplayString((KeyHandlerClient.Companion.accelerateKey)!!.getKeyCode()) + " / " + GameSettings.getKeyDisplayString(
                            (KeyHandlerClient.Companion.decelerateKey)!!.getKeyCode()
                        ) + "  - " + GCCoreUtil.translate("gui.rocket.updown.name")
                    )
                )
                player.sendSystemMessage(
                    Component.literal(
                        GameSettings.getKeyDisplayString(KeyHandlerClient.Companion.openFuelGui.getKeyCode()) + "       - " + GCCoreUtil.translate(
                            "gui.rocket.inv.name"
                        )
                    )
                )
            }

            EnumSimplePacket.C_GET_CELESTIAL_BODY_LIST -> {
                str = ""

                for (cBody: CelestialBody in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
                    str = str + cBody.getTranslationKey() + ";"
                }

                for (cBody: CelestialBody in (GalaxyRegistry.getRegisteredMoons())!!.values) {
                    str = str + cBody.getTranslationKey() + ";"
                }

                for (cBody: CelestialBody in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
                    str = str + cBody.getTranslationKey() + ";"
                }

                for (solarSystem in (GalaxyRegistry.getRegisteredSolarSystems())!!.values) {
                    str = str + (solarSystem)!!.getTranslationKey() + ";"
                }

                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE,
                        getDimensionID(),
                        arrayOf<Any>(str)
                    )
                )
            }

            EnumSimplePacket.C_UPDATE_ENERGYUNITS -> CommandGCEnergyUnits.handleParamClientside((this.data!!.get(0) as kotlin.Int?)!!)
            EnumSimplePacket.C_RESPAWN_PLAYER -> {
                val provider: WorldProvider? = WorldUtil.getProviderForNameClient((this.data!!.get(0) as String?)!!)
                val dimID: Int = GCCoreUtil.getDimensionID((provider)!!)
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("DEBUG: Client receiving respawn packet for dim " + dimID)
                }
                val par2: Int = this.data!!.get(1) as Int
                val par3: String? = this.data!!.get(2) as String?
                val par4: Int = this.data!!.get(3) as Int
                WorldUtil.forceRespawnClient(dimID, par2, par3, par4)
            }

            EnumSimplePacket.C_UPDATE_STATS -> {
                stats!!.setBuildFlags((this.data!!.get(0) as kotlin.Int?)!!)
                BlockPanelLighting.updateClient((this.data)!!)
            }

            EnumSimplePacket.C_UPDATE_TELEMETRY -> {
                tile = player!!.level().getBlockEntity(this.data!!.get(0) as BlockPos?)
                if (tile is TileEntityTelemetry) {
                    (tile as TileEntityTelemetry).receiveUpdate((data)!!, this.getDimensionID())
                }
            }

            EnumSimplePacket.C_SEND_PLAYERSKIN -> {
                val strName: String? = this.data!!.get(0) as String?
                val s1: String? = this.data!!.get(1) as String?
                val s2: String? = this.data!!.get(2) as String?
                val strUUID: String? = this.data!!.get(3) as String?
                var gp: GameProfile? = PlayerUtil.getOtherPlayerProfile(strName)
                if (gp == null) {
                    gp = PlayerUtil.makeOtherPlayerProfile(strName, (strUUID)!!)
                }
                gp.getProperties().put("textures", Property("textures", s1, s2))
            }

            EnumSimplePacket.C_SEND_OVERWORLD_IMAGE -> try {
                val cx: Int = this.data!!.get(0) as Int
                val cz: Int = this.data!!.get(1) as Int
                val bytes: ByteArray? = this.data!!.get(2) as ByteArray?
                MapUtil.receiveOverworldImageCompressed(cx, cz, (bytes)!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            EnumSimplePacket.C_RECOLOR_PIPE -> {
                val tileEntity: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(0) as BlockPos?)
                if (tileEntity is TileEntityFluidPipe) {
                    val pipe: TileEntityFluidPipe = tileEntity
                    (pipe.getNetwork())!!.splitUntyped(pipe)
                    pipe.setNetwork(null)
                }
            }

            EnumSimplePacket.C_RECOLOR_ALL_GLASS -> BlockSpaceGlass.updateGlassColors(
                (this.data!!.get(0) as kotlin.Int?)!!,
                (this.data!!.get(1) as kotlin.Int?)!!,
                (this.data!!.get(2) as kotlin.Int?)!!
            )

            EnumSimplePacket.C_UPDATE_MACHINE_DATA -> {
                val tile3: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(0) as BlockPos?)
                if (tile3 is ITileClientUpdates) {
                    (tile3 as ITileClientUpdates).updateClient(this.data)
                }
            }

            EnumSimplePacket.C_LEAK_DATA -> {
                val tile4: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(0) as BlockPos?)
                if (tile4 is TileEntityOxygenSealer) {
                    (tile4 as ITileClientUpdates).updateClient(this.data)
                }
            }


            else -> {}
        }
    }

    override fun handleServerSide(player: EntityPlayer?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        if (playerBase == null) {
            return
        }

        val server: MinecraftServer = playerBase.server
        val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

        var teamInvitedTo: Int = 0
var race: SpaceRace? = null
when (this.type) {
            EnumSimplePacket.S_RESPAWN_PLAYER -> playerBase.connection.sendPacket(
                SPacketRespawn(
                    player!!.dimension,
                    player.level().getDifficulty(),
                    player.level().getWorldInfo().getTerrainType(),
                    playerBase.interactionManager.getGameType()
                )
            )

            EnumSimplePacket.S_TELEPORT_ENTITY -> TickHandlerServer.scheduleNewDimensionChange(
                ScheduledDimensionChange(
                    playerBase,
                    this@PacketSimple.data!!.get(0) as String?
                )
            )

            EnumSimplePacket.S_IGNITE_ROCKET -> if (!player!!.level().isRemote && !player.isRemoved() && player.getVehicle() != null && !player.getVehicle()!!.isDead && player.getVehicle() is EntityTieredRocket) {
                val ship: EntityTieredRocket? = player.getVehicle() as EntityTieredRocket?

                if (ship!!.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LANDING.ordinal) {
                    if (ship.hasValidFuel()) {
                        val stack2: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(4)

                        if (stack2 != null && stack2.getItem() is ItemParaChute || stats.getLaunchAttempts() > 0) {
                            ship.igniteCheckingCooldown()
                            stats.setLaunchAttempts(0)
                        } else if (stats.getChatCooldown() == 0 && stats.getLaunchAttempts() == 0) {
                            player.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.rocket.warning.noparachute")))
                            stats.setChatCooldown(250)
                            stats.setLaunchAttempts(1)
                        }
                    } else if (stats.getChatCooldown() == 0) {
                        player.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.rocket.warning.nofuel")))
                        stats.setChatCooldown(250)
                    }
                }
            }

            EnumSimplePacket.S_OPEN_SCHEMATIC_PAGE -> if (player != null) {
                val page: ISchematicPage? =
                    SchematicRegistry.getMatchingRecipeForID((this.data!!.get(0) as kotlin.Int?)!!)

                player.openGui(
                    GalacticraftCore.instance,
                    page!!.getGuiID(),
                    player.level(),
                    (this.data!!.get(1) as kotlin.Int?)!!,
                    (this.data!!.get(2) as kotlin.Int?)!!,
                    (this.data!!.get(3) as kotlin.Int?)!!
                )
            }

            EnumSimplePacket.S_OPEN_FUEL_GUI -> if (player!!.getVehicle() is EntityBuggy) {
                GCCoreUtil.openBuggyInv(
                    playerBase,
                    (player.getVehicle() as EntityBuggy?)!!,
                    (player.getVehicle() as EntityBuggy).getType()
                )
            } else if (player.getVehicle() is EntitySpaceshipBase) {
                player.openGui(
                    GalacticraftCore.instance,
                    GuiIdsCore.ROCKET_INVENTORY,
                    player.level(),
                    player.x.toInt(),
                    player.y.toInt(),
                    player.z.toInt()
                )
            }

            EnumSimplePacket.S_UPDATE_SHIP_YAW -> if (player!!.getVehicle() is EntitySpaceshipBase) {
                val ship: EntitySpaceshipBase? = player.getVehicle() as EntitySpaceshipBase?

                if (ship != null) {
                    ship.rotationYaw = ((this.data)!!.get(0) as kotlin.Float?)!!
                }
            }

            EnumSimplePacket.S_UPDATE_SHIP_PITCH -> if (player!!.getVehicle() is EntitySpaceshipBase) {
                val ship: EntitySpaceshipBase? = player.getVehicle() as EntitySpaceshipBase?

                if (ship != null) {
                    ship.rotationPitch = ((this.data)!!.get(0) as kotlin.Float?)!!
                }
            }

            EnumSimplePacket.S_SET_ENTITY_FIRE -> {
                val entity: Entity? = player!!.level().getEntityByID((this.data!!.get(0) as kotlin.Int?)!!)

                if (entity is EntityLivingBase) {
                    entity.setFire(3)
                }
            }

            EnumSimplePacket.S_BIND_SPACE_STATION_ID -> {
                val homeID: Int = this.data!!.get(0) as Int
                if ((!(stats.getSpaceStationDimensionData())!!.containsKey(homeID) || (stats.getSpaceStationDimensionData())!!
                        .get(homeID) == -1 || (stats.getSpaceStationDimensionData())!!.get(homeID) == 0)
                    && !ConfigManagerCore.disableSpaceStationCreation
                ) {
                    if (playerBase.capabilities.isCreativeMode || (WorldUtil.getSpaceStationRecipe(homeID))!!
                            .matches(playerBase, true)
                    ) {
                        WorldUtil.bindSpaceStationToNewDimension(playerBase.level(), playerBase, homeID)
                    }
                }
            }

            EnumSimplePacket.S_UNLOCK_NEW_SCHEMATIC -> {
                val container: Container? = player!!.openContainer

                if (container is ContainerSchematic) {
                    val schematicContainer: ContainerSchematic = container

                    val stack: ItemStack = schematicContainer.craftMatrix.getStackInSlot(0)

                    if (!stack.isEmpty()) {
                        val page: ISchematicPage? = SchematicRegistry.getMatchingRecipeForItemStack(stack)

                        if (page != null) {
                            SchematicRegistry.unlockNewPage(playerBase, stack)
                            SpaceRaceManager.teamUnlockSchematic(playerBase, stack)
                            stack.shrink(1)

                            schematicContainer.craftMatrix.setInventorySlotContents(0, stack)
                            schematicContainer.craftMatrix.markDirty()

                            (GalacticraftCore.packetPipeline)!!.sendTo(
                                PacketSimple(
                                    EnumSimplePacket.C_ADD_NEW_SCHEMATIC,
                                    getDimensionID(),
                                    arrayOf<Any>(page.getPageID())
                                ), playerBase
                            )
                        }
                    }
                }
            }

            EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON -> {
                val tileAt: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(0) as BlockPos?)

                if (tileAt is IDisableableMachine) {
                    val machine: IDisableableMachine = tileAt as IDisableableMachine

                    machine.setDisabled(
                        (this.data!!.get(1) as kotlin.Int?)!!,
                        !machine.getDisabled((this.data!!.get(1) as kotlin.Int?)!!)
                    )
                }
            }

            EnumSimplePacket.S_ON_FAILED_CHEST_UNLOCK -> if (stats.getChatCooldown() == 0) {
                player!!.sendSystemMessage(
                    Component.literal(
                        GCCoreUtil.translateWithFormat(
                            "gui.chest.warning.wrongkey",
                            this.data!!.get(0)
                        )
                    )
                )
                stats.setChatCooldown(100)
            }

            EnumSimplePacket.S_RENAME_SPACE_STATION -> {
                val ssdata: SpaceStationWorldData? = SpaceStationWorldData.getStationData(
                    playerBase.level(),
                    (this.data!!.get(1) as kotlin.Int?)!!,
                    playerBase
                )

                if (ssdata != null && ssdata.getOwner().equals(PlayerUtil.getName(player), ignoreCase = true)) {
                    ssdata.setSpaceStationName((this.data!!.get(0) as String?)!!)
                    ssdata.setDirty(true)
                }
            }

            EnumSimplePacket.S_OPEN_EXTENDED_INVENTORY -> player!!.openGui(
                GalacticraftCore.instance,
                GuiIdsCore.EXTENDED_INVENTORY,
                player.level(),
                0,
                0,
                0
            )

            EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT -> {
                val tile1: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(1) as BlockPos?)

                when (this.data!!.get(0) as Int?) {
                    0 -> if (tile1 is TileEntityAirLockController) {
                        val airlockController: TileEntityAirLockController = tile1
                        airlockController.redstoneActivation = this.data!!.get(2) as Int? == 1
                    }

                    1 -> if (tile1 is TileEntityAirLockController) {
                        val airlockController: TileEntityAirLockController = tile1
                        airlockController.playerDistanceActivation = this.data!!.get(2) as Int? == 1
                    }

                    2 -> if (tile1 is TileEntityAirLockController) {
                        val airlockController: TileEntityAirLockController = tile1
                        airlockController.playerDistanceSelection = ((this.data)!!.get(2) as kotlin.Int?)!!
                    }

                    3 -> if (tile1 is TileEntityAirLockController) {
                        val airlockController: TileEntityAirLockController = tile1
                        airlockController.playerNameMatches = this.data!!.get(2) as Int? == 1
                    }

                    4 -> if (tile1 is TileEntityAirLockController) {
                        val airlockController: TileEntityAirLockController = tile1
                        airlockController.invertSelection = this.data!!.get(2) as Int? == 1
                    }

                    5 -> if (tile1 is TileEntityAirLockController) {
                        val airlockController: TileEntityAirLockController = tile1
                        airlockController.lastHorizontalModeEnabled = airlockController.horizontalModeEnabled
                        airlockController.horizontalModeEnabled = this.data!!.get(2) as Int? == 1
                    }

                    6 -> if (tile1 is IBubbleProvider) {
                        val distributor: IBubbleProvider = tile1 as IBubbleProvider
                        distributor.setBubbleVisible(this.data!!.get(2) as Int? == 1)
                    }

                    else -> {}
                }
            }

            EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_STRING -> {
                val tile2: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(1) as BlockPos?)

                when (this.data!!.get(0) as Int?) {
                    0 -> if (tile2 is TileEntityAirLockController) {
                        val airlockController: TileEntityAirLockController = tile2
                        airlockController.playerToOpenFor = (this.data!!.get(2) as String?)!!
                    }

                    else -> {}
                }
            }

            EnumSimplePacket.S_UPDATE_SHIP_MOTION_Y -> {
                val entityID: Int = this.data!!.get(0) as Int
                val up: Boolean = this.data!!.get(1) as Boolean

                val entity2: Entity? = player!!.level().getEntityByID(entityID)

                if (entity2 is EntityAutoRocket) {
                    val autoRocket: EntityAutoRocket = entity2
                    autoRocket.motionY += (if (up) 0.02f else -0.02f).toDouble()
                }
            }

            EnumSimplePacket.S_START_NEW_SPACE_RACE -> {
                val teamID: Int = this.data!!.get(0) as Int
                val teamName: String? = this.data!!.get(1) as String?
                val flagData: FlagData? = this.data!!.get(2) as FlagData?
                val teamColor: Vector3? = this.data!!.get(3) as Vector3?
                val playerList: MutableList<String?> = ArrayList<String?>()

                var i: Int = 4
                while (i < this.data!!.size) {
                    playerList.add(this.data!!.get(i) as String?)
                    i++
                }

                val previousData: Boolean = SpaceRaceManager.getSpaceRaceFromID(teamID) != null

                val newRace: SpaceRace = SpaceRace(playerList, teamName, flagData, teamColor)

                if (teamID > 0) {
                    newRace.setSpaceRaceID(teamID)
                }

                SpaceRaceManager.addSpaceRace(newRace)

                if (previousData) {
                    SpaceRaceManager.sendSpaceRaceData(
                        server,
                        null,
                        SpaceRaceManager.getSpaceRaceFromPlayer(PlayerUtil.getName(playerBase))
                    )
                }
            }

            EnumSimplePacket.S_REQUEST_FLAG_DATA -> SpaceRaceManager.sendSpaceRaceData(
                server,
                playerBase,
                SpaceRaceManager.getSpaceRaceFromPlayer(this.data!!.get(0) as String?)
            )

            EnumSimplePacket.S_INVITE_RACE_PLAYER -> {
                val playerInvited: EntityPlayerMP? =
                    PlayerUtil.getPlayerBaseServerFromPlayerUsername(server, (this.data!!.get(0) as String?)!!, true)
                if (playerInvited != null) {
                    val teamInvitedTo: Int = this.data!!.get(1) as Int
                    val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromID(teamInvitedTo)

                    if (race != null) {
                        (GCPlayerStats.get(playerInvited))!!.setSpaceRaceInviteTeamID(teamInvitedTo)
                        val dA: String? = EnumColor.DARK_AQUA.getCode()
                        val bG: String? = EnumColor.BRIGHT_GREEN.getCode()
                        val dB: String? = EnumColor.PURPLE.getCode()
                        var teamNameTotal: String = ""
                        val teamNameSplit: Array<String> =
                            (race.getTeamName())!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        for (teamNamePart: String? in teamNameSplit) {
                            teamNameTotal = teamNameTotal + dB + teamNamePart + " "
                        }
                        playerInvited.sendSystemMessage(
                            Component.literal(
                                dA + GCCoreUtil.translateWithFormat(
                                    "gui.space_race.chat.invite_received",
                                    bG + PlayerUtil.getName(player) + dA
                                ) + "  " + GCCoreUtil.translateWithFormat(
                                    "gui.space_race.chat.to_join",
                                    teamNameTotal,
                                    EnumColor.AQUA.toString() + "/joinrace" + dA
                                )
                            ).setStyle(Style().setColor(TextFormatting.DARK_AQUA))
                        )
                    }
                }
            }

            EnumSimplePacket.S_REMOVE_RACE_PLAYER -> {
                teamInvitedTo = this.data!!.get(1) as Int
                race = SpaceRaceManager.getSpaceRaceFromID(teamInvitedTo)

                if (race != null) {
                    val playerToRemove: String? = this.data!!.get(0) as String?

                    if (!(race.getPlayerNames())!!.remove((playerToRemove)!!)) {
                        player!!.sendSystemMessage(
                            Component.literal(
                                GCCoreUtil.translateWithFormat(
                                    "gui.space_race.chat.not_found",
                                    playerToRemove
                                )
                            )
                        )
                    } else {
                        SpaceRaceManager.onPlayerRemoval(server, playerToRemove, race)
                    }
                }
            }

            EnumSimplePacket.S_ADD_RACE_PLAYER -> {
                val teamToAddPlayer: Int = this.data!!.get(1) as Int
                val spaceRaceToAddPlayer: SpaceRace? = SpaceRaceManager.getSpaceRaceFromID(teamToAddPlayer)

                if (spaceRaceToAddPlayer != null) {
                    val playerToAdd: String? = this.data!!.get(0) as String?

                    if (!(spaceRaceToAddPlayer.getPlayerNames())!!.contains((playerToAdd)!!)) {
                        var oldRace: SpaceRace? = null
                        while ((SpaceRaceManager.getSpaceRaceFromPlayer(playerToAdd).also { oldRace = it }) != null) {
                            SpaceRaceManager.removeSpaceRace(oldRace)
                        }

                        (spaceRaceToAddPlayer.getPlayerNames())!!.add((playerToAdd)!!)
                        SpaceRaceManager.sendSpaceRaceData(server, null, spaceRaceToAddPlayer)

                        for (member: String? in (spaceRaceToAddPlayer.getPlayerNames())!!) {
                            val memberObj: EntityPlayerMP? = PlayerUtil.getPlayerForUsernameVanilla(server, (member)!!)

                            if (memberObj != null) {
                                memberObj.sendSystemMessage(
                                    Component.literal(
                                        EnumColor.DARK_AQUA.toString() + GCCoreUtil.translateWithFormat(
                                            "gui.space_race.chat.add_success",
                                            EnumColor.BRIGHT_GREEN.toString() + playerToAdd + EnumColor.DARK_AQUA
                                        )
                                    ).setStyle(Style().setColor(TextFormatting.DARK_AQUA))
                                )
                            }
                        }
                    } else {
                        player!!.sendSystemMessage(
                            Component.literal(GCCoreUtil.translate("gui.space_race.chat.already_part")).setStyle(
                                Style().setColor(TextFormatting.DARK_RED)
                            )
                        )
                    }
                }
            }

            EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE -> {
                val completeList: String = this.data!!.get(0) as String
                val clientObjects: MutableList<String?> =
                    Arrays.asList<String?>(*completeList.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray())
                val serverObjects: MutableList<String?> = Lists.newArrayList<String?>()
                var missingObjects: String = ""

                for (cBody: CelestialBody in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
                    serverObjects.add(cBody.getTranslationKey())
                }

                for (cBody: CelestialBody in (GalaxyRegistry.getRegisteredMoons())!!.values) {
                    serverObjects.add(cBody.getTranslationKey())
                }

                for (cBody: CelestialBody in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
                    serverObjects.add(cBody.getTranslationKey())
                }

                for (solarSystem in (GalaxyRegistry.getRegisteredSolarSystems())!!.values) {
                    serverObjects.add((solarSystem)!!.getTranslationKey())
                }

                for (str: String? in serverObjects) {
                    if (!clientObjects.contains(str)) {
                        missingObjects = missingObjects + str + "\n"
                    }
                }

                if (missingObjects.length > 0) {
                    playerBase.connection.disconnect(Component.literal("Missing Galacticraft Celestial Objects:\n\n " + missingObjects))
                }
            }

            EnumSimplePacket.S_REQUEST_GEAR_DATA -> {
                val name: String? = this.data!!.get(0) as String?
                val e: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername((name)!!, true)
                if (e != null) {
                    GCPlayerHandler.checkGear(e, (GCPlayerStats.get(e))!!, true)
                }
            }

            EnumSimplePacket.S_BUILDFLAGS_UPDATE -> stats.setBuildFlags((this.data!!.get(0) as kotlin.Int?)!!)
            EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE -> MapUtil.sendOverworldToClient(playerBase)
            EnumSimplePacket.S_REQUEST_MAP_IMAGE -> {
                val dim: Int = this.data!!.get(0) as Int
                val cx: Int = this.data!!.get(1) as Int
                val cz: Int = this.data!!.get(2) as Int
                MapUtil.sendOrCreateMap((WorldUtil.getProviderForDimensionServer(dim))!!.world, cx, cz, playerBase)
            }

            EnumSimplePacket.S_REQUEST_PLAYERSKIN -> {
                val strName: String = this.data!!.get(0) as String
                val playerRequested: EntityPlayerMP? = server.getPlayerList().getPlayerByUsername(strName)

                //Player not online
                if (playerRequested == null) {
                    return
                }

                var gp: GameProfile = playerRequested.getGameProfile()
                if (gp == null) {
                    return
                }

                val property: Property? =
                    Iterables.getFirst<Any?>(gp.getProperties().get("textures"), null as Any?) as Property?
                if (property == null) {
                    return
                }
                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimple(
                        EnumSimplePacket.C_SEND_PLAYERSKIN,
                        getDimensionID(),
                        arrayOf<Any?>(
                            strName,
                            property.getValue(),
                            property.getSignature(),
                            playerRequested.getUUID().toString()
                        )
                    ), playerBase
                )
            }

            EnumSimplePacket.S_CONTROL_ENTITY -> if (player!!.getVehicle() != null && player.getVehicle() is IControllableEntity) {
                (player.getVehicle() as IControllableEntity).pressKey((this.data!!.get(0) as kotlin.Int?)!!)
            }

            EnumSimplePacket.S_NOCLIP_PLAYER -> {
                val noClip: Boolean = this.data!!.get(0) as Boolean
                if (player is GCEntityPlayerMP) {
                    (GalacticraftCore.proxy)!!.player.setNoClip(player as EntityPlayerMP, noClip)
                    if (noClip == false) {
                        player.fallDistance = 0.0f
                        (player as EntityPlayerMP).connection.floatingTickCount = 0
                    }
                } else if (player is EntityPlayerMP) {
                    val emp: EntityPlayerMP = player
                    try {
                        val f: Field =
                            emp.interactionManager.javaClass.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "gameType" else "field_73091_c")
                        f.setAccessible(true)
                        if (noClip == false) {
                            emp.fallDistance = 0.0f
                            emp.connection.floatingTickCount = 0
                            val gt: GameType? = savedSettings.get(emp)
                            if (gt != null) {
                                savedSettings.remove(emp)
                                f.set(emp.interactionManager, gt)
                            }
                        } else {
                            savedSettings.put(emp, emp.interactionManager.getGameType())
                            f.set(emp.interactionManager, GameType.SPECTATOR)
                        }
                    } catch (ee: Exception) {
                        ee.printStackTrace()
                    }
                }
            }

            EnumSimplePacket.S_REQUEST_DATA -> {
                val worldServer: WorldServer = server.getWorld((this.data!!.get(0) as kotlin.Int?)!!)
                if (worldServer != null) {
                    val requestedTile: TileEntity? = worldServer.getBlockEntity(this.data!!.get(1) as BlockPos?)
                    if (requestedTile is INetworkProvider) {
                        if ((requestedTile as INetworkProvider).getNetwork() is FluidNetwork) {
                            val network: FluidNetwork = (requestedTile as INetworkProvider).getNetwork() as FluidNetwork
                            network.addUpdate(playerBase)
                        }
                    }
                }
            }

            EnumSimplePacket.S_UPDATE_CHECKLIST -> for (enumhand: EnumHand in EnumHand.entries) {
                val stack: ItemStack = player!!.getItemInHand(enumhand)
                if (stack != null && stack.getItem() === GCItems.prelaunchChecklist) {
                    var tagCompound: NBTTagCompound? = stack.getTagCompound()
                    if (tagCompound == null) {
                        tagCompound = NBTTagCompound()
                    }
                    val tagCompoundRead: NBTTagCompound = this.data!!.get(0) as NBTTagCompound
                    tagCompound.put("checklistData", tagCompoundRead)
                    stack.setTagCompound(tagCompound)
                }
            }

            EnumSimplePacket.S_REQUEST_MACHINE_DATA -> {
                val tile3: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(0) as BlockPos?)
                if (tile3 is ITileClientUpdates) {
                    (tile3 as ITileClientUpdates).sendUpdateToClient(playerBase)
                }
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

    @OnlyIn(Dist.CLIENT)
    override fun processPacket(var1: INetHandler) {
        if (this.type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && this.type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && this.type != EnumSimplePacket.C_UPDATE_CONFIGS) {
            return
        }

        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
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
        private val savedSettings: MutableMap<EntityPlayerMP?, GameType?> = HashMap<EntityPlayerMP?, GameType?>()
    }
}
