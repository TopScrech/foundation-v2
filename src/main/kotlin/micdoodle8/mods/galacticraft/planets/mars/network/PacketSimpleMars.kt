package micdoodle8.mods.galacticraft.planets.mars.network

import io.netty.buffer.ByteBuf
import java.io.IOException
import micdoodle8.mods.galacticraft.core.event.EventWakePlayer
import micdoodle8.mods.galacticraft.core.network.NetworkUtil
import micdoodle8.mods.galacticraft.core.network.PacketBase
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiLaunchControllerAdvanced
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiSlimelingInventory
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityCryogenicChamber
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import micdoodle8.mods.galacticraft.planets.mars.util.MarsUtil
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Arrays
import kotlin.math.min

open class PacketSimpleMars : PacketBase {
    enum class EnumSimplePacketMars
        (private val targetSide: Side, vararg decodeAs: Class<*>?) {
        // SERVER
        S_UPDATE_SLIMELING_DATA(Side.SERVER, Int::class.java, Int::class.java, String::class.java),
        S_WAKE_PLAYER(Side.SERVER),
        S_UPDATE_ADVANCED_GUI(Side.SERVER, Int::class.java, BlockPos::class.java, Int::class.java),
        S_UPDATE_CARGO_ROCKET_STATUS(Side.SERVER, Int::class.java, Int::class.java),
        S_SWITCH_LAUNCH_CONTROLLER_GUI(Side.SERVER, BlockPos::class.java, Int::class.java),

        // CLIENT
        C_OPEN_CUSTOM_GUI(Side.CLIENT, Int::class.java, Int::class.java, Int::class.java),
        C_OPEN_CUSTOM_GUI_TILE(Side.CLIENT, Int::class.java, Int::class.java, BlockPos::class.java),
        C_BEGIN_CRYOGENIC_SLEEP(Side.CLIENT, BlockPos::class.java);

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

    private var type: EnumSimplePacketMars? = null
    private var data: MutableList<Any?>? = null

    constructor() : super()

    constructor(packetType: EnumSimplePacketMars, dimID: Int, data: Array<out Any?>) : this(
        packetType,
        dimID,
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: EnumSimplePacketMars, dimID: Int, data: MutableList<Any?>) : super(dimID) {
        if (packetType.getDecodeClasses()!!.size != data.size) {
            GCLog.info("Mars Simple Packet found data length different than packet type: " + packetType.name)
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
        this.type = (EnumSimplePacketMars.entries)!![(buffer)!!.readInt()]

        if (this.type!!.getDecodeClasses()!!.size > 0) {
            this.data = NetworkUtil.decodeData(((this.type!!.getDecodeClasses())!! as Array<Class<*>>), (buffer)!!)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun handleClientSide(player: EntityPlayer?) {
        var playerBaseClient: EntityPlayerSP? = null

        if (player is EntityPlayerSP) {
            playerBaseClient = player
        }

        lateinit var pos: BlockPos
        var tile: TileEntity? = null
        when (this.type) {
            EnumSimplePacketMars.C_OPEN_CUSTOM_GUI -> {
                var entityID: Int = 0
                var entity: Entity? = null

                when (this.data!!.get(1) as Int?) {
                    0 -> {
                        entityID = ((this.data)!!.get(2) as kotlin.Int?)!!
                        entity = (player)!!.level().getEntityByID(entityID)

                        if (entity != null && entity is EntitySlimeling) {
                            FMLClientHandler.instance().getClient()
                                .displayGuiScreen(GuiSlimelingInventory((player)!!, entity))
                        }

                        (player)!!.openContainer.windowId = this.data!!.get(0) as Int
                    }

                    1 -> {
                        entityID = ((this.data)!!.get(2) as kotlin.Int?)!!
                        entity = (player)!!.level().getEntityByID(entityID)

                        if (entity != null && entity is EntityCargoRocket) {
                            FMLClientHandler.instance().getClient()
                                .displayGuiScreen(GuiCargoRocket((player)!!.inventory, entity))
                        }

                        (player)!!.openContainer.windowId = this.data!!.get(0) as Int
                    }
                }
            }

            EnumSimplePacketMars.C_OPEN_CUSTOM_GUI_TILE -> {

                when (this.data!!.get(1) as Int?) {
                    0 -> {
                        pos = this.data!!.get(2) as BlockPos
                        tile = (player)!!.level().getBlockEntity(pos)

                        if (tile != null && tile is TileEntityLaunchController) {
                            FMLClientHandler.instance().getClient()
                                .displayGuiScreen(GuiLaunchControllerAdvanced((player)!!.inventory, tile))
                        }

                        (player)!!.openContainer.windowId = this.data!!.get(0) as Int
                    }
                }
            }

            EnumSimplePacketMars.C_BEGIN_CRYOGENIC_SLEEP -> {
                pos = this.data!!.get(0) as BlockPos
                tile = (player)!!.level().getBlockEntity(pos)

                if (tile is TileEntityCryogenicChamber) {
                    (tile as TileEntityCryogenicChamber).sleepInBedAt((player)!!, pos.getX(), pos.getY(), pos.getZ())
                }
            }

            else -> {}
        }
    }

    override fun handleServerSide(player: EntityPlayer?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        when (this.type) {
            EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA -> {
                var entity: Entity? = player!!.level().getEntityByID((this.data!!.get(0) as kotlin.Int?)!!)

                if (entity is EntitySlimeling) {
                    val slimeling: EntitySlimeling = entity

                    val subType: Int = this.data!!.get(1) as Int

                    when (subType) {
                        0 -> if (player === slimeling.getOwner() && !slimeling.world.isRemote) {
                            slimeling.setSittingAI(!slimeling.isSitting())
                            slimeling.setJumping(false)
                            slimeling.getNavigator().clearPath()
                            slimeling.setAttackTarget(null)
                        }

                        1 -> if (player === slimeling.getOwner() && !slimeling.world.isRemote) {
                            slimeling.slimelingName = (this.data!!.get(2) as String?)!!
                            slimeling.setName(slimeling.slimelingName)
                        }

                        2 -> if (player === slimeling.getOwner() && !slimeling.world.isRemote) {
                            slimeling.age += 5000
                        }

                        3 -> if (!slimeling.isInLove() && player === slimeling.getOwner() && !slimeling.world.isRemote) {
                            slimeling.setInLove(playerBase)
                        }

                        4 -> if (player === slimeling.getOwner() && !slimeling.world.isRemote) {
                            slimeling.attackDamage = min(slimeling.attackDamage + 0.1f, 1.0f)
                        }

                        5 -> if (player === slimeling.getOwner() && !slimeling.world.isRemote) {
                            slimeling.setHealth(slimeling.getHealth() + 5.0f)
                        }

                        6 -> if (player === slimeling.getOwner() && !slimeling.world.isRemote) {
                            MarsUtil.openSlimelingInventory((playerBase)!!, slimeling)
                        }
                    }
                }
            }

            EnumSimplePacketMars.S_WAKE_PLAYER -> {
                val c: BlockPos? = playerBase!!.bedLocation

                if (c != null) {
                    val event: EventWakePlayer = EventWakePlayer(playerBase, c, true, true, false, true)
                    MinecraftForge.EVENT_BUS.post(event)
                    playerBase.wakeUpPlayer(true, true, false)
                }
            }

            EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI -> {
                val tile: TileEntity? = player!!.level().getBlockEntity(this.data!!.get(1) as BlockPos?)

                when (this.data!!.get(0) as Int?) {
                    0 -> if (tile is TileEntityLaunchController) {
                        val launchController: TileEntityLaunchController = tile
                        launchController.setFrequency((this.data!!.get(2) as kotlin.Int?)!!)
                    }

                    1 -> if (tile is TileEntityLaunchController) {
                        val launchController: TileEntityLaunchController = tile
                        launchController.setLaunchDropdownSelection((this.data!!.get(2) as kotlin.Int?)!!)
                    }

                    2 -> if (tile is TileEntityLaunchController) {
                        val launchController: TileEntityLaunchController = tile
                        launchController.setDestinationFrequency((this.data!!.get(2) as kotlin.Int?)!!)
                    }

                    3 -> if (tile is TileEntityLaunchController) {
                        val launchController: TileEntityLaunchController = tile
                        launchController.launchPadRemovalDisabled = this.data!!.get(2) as Int? == 1
                    }

                    4 -> if (tile is TileEntityLaunchController) {
                        val launchController: TileEntityLaunchController = tile
                        launchController.setLaunchSchedulingEnabled(this.data!!.get(2) as Int? == 1)
                    }

                    5 -> if (tile is TileEntityLaunchController) {
                        val launchController: TileEntityLaunchController = tile
                        launchController.requiresClientUpdate = true
                    }

                    else -> {}
                }
            }

            EnumSimplePacketMars.S_UPDATE_CARGO_ROCKET_STATUS -> {
                val entity2: Entity? = player!!.level().getEntityByID((this.data!!.get(0) as kotlin.Int?)!!)

                if (entity2 is EntityCargoRocket) {
                    val rocket: EntityCargoRocket = entity2

                    val subType: Int = this.data!!.get(1) as Int

                    when (subType) {
                        else -> rocket.statusValid = rocket.checkLaunchValidity()
                    }
                }
            }

            EnumSimplePacketMars.S_SWITCH_LAUNCH_CONTROLLER_GUI -> {
                val pos: BlockPos = this.data!!.get(0) as BlockPos
                val tile1: TileEntity? = player!!.level().getBlockEntity(pos)
                if (tile1 is TileEntityLaunchController) {
                    val launchController: TileEntityLaunchController = tile1
                    when (this.data!!.get(1) as Int?) {
                        0 -> MarsUtil.openAdvancedLaunchController((playerBase)!!, launchController)
                        1 -> player.openGui(
                            GalacticraftPlanets.instance,
                            GuiIdsPlanets.MACHINE_MARS,
                            player.level(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                        )
                    }
                }
            }

            else -> {}
        }
    }
}
