package galaxyspace.core.network.packet

import net.minecraft.util.math.RayTraceResult.Type

import galaxyspace.core.prefab.entities.EntityAstroWolf
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidSeparator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModificationTable
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import galaxyspace.core.handler.capabilities.StatsCapabilityClient
import galaxyspace.core.handler.capabilities.GSStatsCapability
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import io.netty.buffer.ByteBuf
import java.io.IOException
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.prefab.inventory.InventoryAstroWolf
import galaxyspace.core.events.GSClientTickHandler
import asmodeuscore.core.utils.worldengine.WE_WorldProvider
import galaxyspace.core.util.researches.ResearchUtil
import galaxyspace.core.events.GSEventHandler
import galaxyspace.api.tile.ITileEffects
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.api.item.IModificationItem
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import galaxyspace.core.handler.capabilities.StatsCapability
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiTeleporting
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.network.NetworkUtil
import micdoodle8.mods.galacticraft.core.network.PacketBase
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.PacketBuffer
import net.minecraft.server.MinecraftServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import java.lang.Exception
import java.lang.RuntimeException
import java.util.Arrays

open class GSPacketSimple : PacketBase, Packet<INetHandler> {
    enum class GSEnumSimplePacket
        (//C_OPEN_CUSTOM_GUI(Side.CLIENT, Integer.class, Integer.class, Integer.class),
        //C_OPEN_ASTRO_WOLF_GUI(Side.CLIENT, Integer.class, Integer.class); // windowID, wolfID
        private val targetSide: Side, vararg decodeAs: Class<*>?
    ) {
        // SERVER
        /**
         * Updates the gravity side of an artifical gravity block
         * - BlockVec3 pos: position of the block
         * - Integer gravityStrength
         */
        S_GRAVITY_DATA(Side.SERVER, BlockVec3::class.java, Int::class.java, Float::class.java),
        S_ON_ADVANCED_GUI_CLICKED_INT(
            Side.SERVER,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        ),
        S_CHANGE_FLIGHT_STATE(Side.SERVER, Boolean::class.java),
        S_REVERSE_SEPATATOR(Side.SERVER, BlockVec3::class.java),
        S_UPDATE_NBT_ITEM_ON_GUI(Side.SERVER, BlockVec3::class.java, String::class.java),
        S_UPDATE_NBT_ITEM_IN_ARMOR(Side.SERVER, Int::class.java, String::class.java),
        S_UPDATE_WOLF_INV(Side.SERVER, Int::class.java),
        S_GET_CAGE_ENTITY(Side.SERVER, Int::class.java),
        S_UPDATE_RESEARCH_USER(Side.SERVER, Int::class.java),


        //CLIENT
        C_UPDATE_WORLD(Side.CLIENT),
        C_UPDATE_RESEARCHES(Side.CLIENT, Array<Int>::class.java),
        C_UPDATE_RESEARCH(Side.CLIENT, Int::class.java, Int::class.java),  // id, count
        C_GLOW_BLOCK(Side.CLIENT, BlockVec3::class.java, Int::class.java),
        C_UPDATE_WOLF_INV(Side.CLIENT, Int::class.java, NBTTagCompound::class.java),
        C_GET_CAGE_ENTITY(Side.CLIENT),
        C_SHOW_SCANNER_BLOCK(Side.CLIENT, Int::class.java),

        // Added code
        C_OPEN_WAITFORDRIVER_GUI(Side.CLIENT),
        C_CLOSE_WAITFORDRIVER_GUI(Side.CLIENT); //

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

    private var type: GSEnumSimplePacket? = null
    private var data: MutableList<Any?>? = null

    constructor() : super()

    constructor(packetType: GSEnumSimplePacket, dimID: Int, vararg data: Any?) : this(
        packetType,
        dimID,
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: GSEnumSimplePacket, world: World, data: Array<out Any?>) : this(
        packetType,
        GCCoreUtil.getDimensionID(world),
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: GSEnumSimplePacket, dimID: Int, data: MutableList<Any?>) : super(dimID) {
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
        this.type = (GSEnumSimplePacket.entries)!![(buffer)!!.readInt()]

        try {
            if (this.type!!.getDecodeClasses()!!.size > 0) {
                this.data = NetworkUtil.decodeData(((this.type!!.getDecodeClasses())!! as Array<Class<*>>), buffer)
            }
            if ((buffer)!!.readableBytes() > 0) {
                GCLog.severe("Galacticraft packet length problem for packet type " + this.type.toString())
            }
        } catch (e: Exception) {
            System.err.println("[Galacticraft] Error handling simple packet type: " + this.type.toString() + " " + buffer.toString())
            e.printStackTrace()
        }
    }

    @SideOnly(Side.CLIENT)
    override fun handleClientSide(player: EntityPlayer?) {
        var playerBaseClient: EntityPlayerSP? = null
        var stats: GCPlayerStatsClient? = null

        //StatsCapability gs_stats = null;
        var gs_stats_client: StatsCapabilityClient? = null

        if (player is EntityPlayerSP) {
            playerBaseClient = player
            stats = GCPlayerStatsClient.get(playerBaseClient)
            //gs_stats = galaxyspace.core.handler.capabilities.StatsCapability.get(player);
            gs_stats_client = StatsCapabilityClient.Companion.get(playerBaseClient)
        }
        /*else
        {/ *
            if (type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && type != EnumSimplePacket.C_UPDATE_CONFIGS)
            {
                return;
            }
        }*/
        var id: Int = 0
do {
when (this.type) {
            GSEnumSimplePacket.C_GET_CAGE_ENTITY -> {
                val result: RayTraceResult? = Minecraft.getMinecraft().objectMouseOver
                if (result != null && result.typeOfHit != null) {
                    if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
                        val entity: Entity? = result.entityHit
                        if (entity is EntityAnimal) (GalaxySpace.packetPipeline)!!.sendToServer(
                            GSPacketSimple(
                                GSEnumSimplePacket.S_GET_CAGE_ENTITY,
                                GCCoreUtil.getDimensionID((player)!!.world),
                                entity.getEntityId()
                            )
                        )
                        //Main.network.sendToServer(new SGetEntity(entity.getEntityId()));
                    }
                }
            }

            GSEnumSimplePacket.C_UPDATE_WOLF_INV -> {
                val wolfID: Int = this.data!!.get(0) as Int
                val wolfInvTag: NBTTagCompound = this.data!!.get(1) as NBTTagCompound

                val wolf: EntityAstroWolf? = playerBaseClient!!.world.getEntityByID(wolfID) as EntityAstroWolf?


                // set client inventory
                if (wolf != null) {
                    val wolfInv: InventoryAstroWolf = InventoryAstroWolf(wolf)
                    ItemStackHelper.loadAllItems(wolfInvTag, wolfInv.getInventory())

                    wolf.setWolfInventory(wolfInv)
                    wolf.needSync = false
                }
            }

            GSEnumSimplePacket.C_SHOW_SCANNER_BLOCK -> {
                val index: Int = this.data!!.get(0) as Int
                GSClientTickHandler.ticks = 1000
            }

            GSEnumSimplePacket.C_GLOW_BLOCK -> {
                val block: BlockVec3 = this.data!!.get(0) as BlockVec3
                val level: Int = this.data!!.get(1) as Int
                GlStateManager.pushMatrix()
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "textures/misc/gradient.png"
                    )
                )
                GlStateManager.disableLighting()
                GlStateManager.disableCull()
                GlStateManager.disableAlpha()
                GlStateManager.depthMask(false)
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
                GlStateManager.enableBlend()
                GlStateManager.translate(block.x.toFloat(), block.y.toFloat(), block.z.toFloat())
                val tess: Tessellator = Tessellator.getInstance()
                val worldRenderer: BufferBuilder = tess.getBuffer()
                GlStateManager.color(1.0f, 0.7f, 0.7f, 0.016667f * (12 - level))
                val cA: Float = -0.01f
                val cB: Float = 1.01f
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(cA.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 1.0).endVertex()
                worldRenderer.pos(cB.toDouble(), cB.toDouble(), cA.toDouble()).tex(1.0, 1.0).endVertex()
                worldRenderer.pos(cB.toDouble(), cB.toDouble(), cB.toDouble()).tex(1.0, 0.0).endVertex()
                worldRenderer.pos(cA.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 0.0).endVertex()
                tess.draw()
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
                worldRenderer.pos(cA.toDouble(), cA.toDouble(), cB.toDouble()).tex(0.0, 1.0).endVertex()
                worldRenderer.pos(cB.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 1.0).endVertex()
                worldRenderer.pos(cB.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
                tess.draw()
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
                worldRenderer.pos(cA.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
                worldRenderer.pos(cA.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 1.0).endVertex()
                worldRenderer.pos(cA.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 1.0).endVertex()
                tess.draw()
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(cB.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 1.0).endVertex()
                worldRenderer.pos(cB.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 0.0).endVertex()
                worldRenderer.pos(cB.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 0.0).endVertex()
                worldRenderer.pos(cB.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 1.0).endVertex()
                tess.draw()
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
                worldRenderer.pos(1.0, cA.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
                worldRenderer.pos(1.0, 1.0, cA.toDouble()).tex(0.0, 1.0).endVertex()
                worldRenderer.pos(cA.toDouble(), 1.0, cA.toDouble()).tex(1.0, 1.0).endVertex()
                tess.draw()
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(1.0, cA.toDouble(), 1.0).tex(1.0, 1.0).endVertex()
                worldRenderer.pos(cA.toDouble(), cA.toDouble(), 1.0).tex(1.0, 0.0).endVertex()
                worldRenderer.pos(cA.toDouble(), 1.0, 1.0).tex(0.0, 0.0).endVertex()
                worldRenderer.pos(1.0, 1.0, 1.0).tex(0.0, 1.0).endVertex()
                tess.draw()
                GlStateManager.popMatrix()
            }

            GSEnumSimplePacket.C_UPDATE_WORLD -> {
                val server: MinecraftServer? = FMLCommonHandler.instance().getMinecraftServerInstance()

                if (server == null) break

                if (playerBaseClient == null) break

                val world: World = server.getWorld(this.getDimensionID())

                if (world != null && playerBaseClient.world != null && playerBaseClient.world.provider is WE_WorldProvider && world.provider != null && world.provider is WE_WorldProvider) {
                    (playerBaseClient.world.provider as WE_WorldProvider).chunk_provider =
                        (world.provider as WE_WorldProvider).chunk_provider
                }
            }

            GSEnumSimplePacket.C_UPDATE_RESEARCHES -> if (gs_stats_client != null) {
                var i: Int = 0
                for (id: Any? in this.data!!) gs_stats_client.setKnowledgeResearch(i++, id as Int)
                //System.out.print((int)id + " ");
            }

            GSEnumSimplePacket.C_UPDATE_RESEARCH -> {
                id = this.data!!.get(0) as Int
                val count: Int = this.data!!.get(1) as Int

                if (gs_stats_client != null) gs_stats_client.setKnowledgeResearch(id, count)
            }

            GSEnumSimplePacket.C_OPEN_WAITFORDRIVER_GUI -> {
                val waitforGUI: GuiTeleporting = GuiTeleporting(Int.MAX_VALUE)

                if (Minecraft.getMinecraft().currentScreen !== waitforGUI) {
                    Minecraft.getMinecraft().displayGuiScreen(waitforGUI)
                }
            }

            GSEnumSimplePacket.C_CLOSE_WAITFORDRIVER_GUI -> {
                Minecraft.getMinecraft().displayGuiScreen(null)

                if (TickHandlerClient.teleportingGui != null) {
                    TickHandlerClient.teleportingGui = null
                }
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

        val stats: GCPlayerStats? = GCPlayerStats.get(playerBase)
        val gs_stats: StatsCapability = (StatsCapability.Companion.get(playerBase))!!

        var tileEntity: TileEntity?

        lateinit var tag: String
var consumed: Boolean = false
do {
when (this.type) {
            GSEnumSimplePacket.S_UPDATE_RESEARCH_USER -> {
                var id: Int = 0
                val count: Int = this.data!!.get(0) as Int


                var flag: Boolean = false
                if (count == -1) {
                    var i: Int = 0
                    while (i < (gs_stats.getKnowledgeResearches())!!.size) {
                        if (((gs_stats)!!.getKnowledgeResearches())!![i] != 0) {
                            gs_stats.setKnowledgeResearch(i, 0)
                            (GalaxySpace.packetPipeline)!!.sendTo(
                                GSPacketSimple(
                                    GSEnumSimplePacket.C_UPDATE_RESEARCH,
                                    GCCoreUtil.getDimensionID(playerBase.world),
                                    *arrayOf<Any>(i, 0)
                                ), playerBase
                            )
                        }
                        i++
                    }


                    break
                }
                var i: Int = 0
                while (i < (gs_stats.getKnowledgeResearches())!!.size) {
                    if (((gs_stats)!!.getKnowledgeResearches())!![i] == count) flag = true
                    if (((gs_stats)!!.getKnowledgeResearches())!![i] == 0) {
                        id = i
                        break
                    }
                    i++
                }

                if (ResearchUtil.getResearch(count) == null) flag = true
                /*
    		for(ItemStack stack : ResearchUtil.getResearch(count).getNeedItems())
    		{
				for(ItemStack inv : player.inventory.mainInventory) {
					if(inv.isItemEqual(stack)) { break; }
					else flag = true;
				}
    		}*/
                if (!flag) {
                    var consumed: Boolean = false
                    for (stack: ItemStack? in ((ResearchUtil.getResearch(count))!!.getNeedItems())!!) {
                        consumed = GSEventHandler.consumeItemStack(playerBase.inventory, (stack)!!)
                    }
                    if (consumed || !player!!.capabilities.isCreativeMode) {
                        gs_stats.setKnowledgeResearch(id, count)
                        (GalaxySpace.packetPipeline)!!.sendTo(
                            GSPacketSimple(
                                GSEnumSimplePacket.C_UPDATE_RESEARCH,
                                GCCoreUtil.getDimensionID(playerBase.world),
                                *arrayOf<Any>(id, count)
                            ), playerBase
                        )
                    } //GalaxySpace.packetPipeline.sendTo(new GSPacketSimple(GSEnumSimplePacket.C_UPDATE_RESEARCHES, GCCoreUtil.getDimensionID(playerBase.world), gs_stats.getKnowledgeResearches()), playerBase);
                }
            }

            GSEnumSimplePacket.S_GET_CAGE_ENTITY -> {
                val entityID: Int = this.data!!.get(0) as Int

                val entity: Entity? = player!!.world.getEntityByID(entityID)
                val copyEntity: Entity? = EntityList.createEntityFromNBT(entity!!.serializeNBT(), player.world)

                if (!entity.isDead) entity.setDead()

                val stackM: ItemStack = player.getHeldItemMainhand()
                val stackO: ItemStack = player.getHeldItemOffhand()

                stackM.getTagCompound()!!.setBoolean("hasMob", true)
                stackM.getTagCompound()!!.setTag("entityData", entity.serializeNBT())
            }

            GSEnumSimplePacket.S_UPDATE_WOLF_INV -> {
                val wolfID: Int = this.data!!.get(0) as Int
                val wolf: EntityAstroWolf? = playerBase.world.getEntityByID(wolfID) as EntityAstroWolf?


                // get server wolf inventory and send to client
                if (wolf != null) {
                    val wolfInv: InventoryAstroWolf = wolf.wolfInventory
                    val wolfInvTag: NBTTagCompound = NBTTagCompound()
                    ItemStackHelper.saveAllItems(wolfInvTag, wolfInv.getInventory())

                    (GalaxySpace.packetPipeline)!!.sendTo(
                        GSPacketSimple(
                            GSEnumSimplePacket.C_UPDATE_WOLF_INV,
                            GCCoreUtil.getDimensionID(playerBase.world),
                            *arrayOf<Any>(wolfID, wolfInvTag)
                        ), playerBase
                    )
                }
            }

            GSEnumSimplePacket.S_CHANGE_FLIGHT_STATE -> {
                val state: Boolean = this.data!!.get(0) as Boolean
                GSEventHandler.enableFlight((player)!!, state)
            }

            GSEnumSimplePacket.S_GRAVITY_DATA -> {
                val pos: BlockVec3 = this.data!!.get(0) as BlockVec3
                val radius: Int = this.data!!.get(1) as Int
                val strength: Float = this.data!!.get(2) as Float

                tileEntity = pos.getTileEntity(playerBase.world)
                if (tileEntity is TileEntityGravitationModule) {
                    tileEntity.setGravityRadius(radius)
                    tileEntity.setGravityStrength(strength)
                    tileEntity.markDirty()
                    //playerBase.world.update(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                }
            }

            GSEnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT -> {
                val tile1: TileEntity? = player!!.world.getTileEntity(
                    BlockPos(
                        (this.data!!.get(1) as kotlin.Int?)!!,
                        (this.data!!.get(2) as kotlin.Int?)!!,
                        (this.data!!.get(3) as kotlin.Int?)!!
                    )
                )

                when (this.data!!.get(0) as Int?) {
                    6 -> if (tile1 is ITileEffects) {
                        val distributor: ITileEffects = tile1 as ITileEffects
                        distributor.setEffectsVisible(this.data!!.get(4) as Int? == 1)
                    }
                }
            }

            GSEnumSimplePacket.S_REVERSE_SEPATATOR -> {
                val posSep: BlockVec3 = this.data!!.get(0) as BlockVec3

                tileEntity = posSep.getTileEntity(playerBase.world)
                if (tileEntity is TileEntityLiquidSeparator) {
                    tileEntity.setReverse(!tileEntity.getReverse())
                    tileEntity.markDirty()
                    playerBase.world.notifyBlockUpdate(
                        tileEntity.getPos(),
                        tileEntity.getBlockType().getDefaultState(),
                        tileEntity.getBlockType().getDefaultState(),
                        0
                    )
                }
            }

            GSEnumSimplePacket.S_UPDATE_NBT_ITEM_ON_GUI -> {
                val position: BlockVec3 = this.data!!.get(0) as BlockVec3
                tag = this.data!!.get(1) as String
                var turn: Boolean = false
                consumed = false

                tileEntity = position.getTileEntity(playerBase.world)
                if (tileEntity is TileEntityModificationTable) {
                    val stack: ItemStack = tileEntity.getStackInSlot(0)
                    var check: Boolean = true
                    var get_module: ItemModule? = null

                    if (stack.getItem() is IModificationItem) {
                        for (module in ((stack.getItem() as IModificationItem).getAvailableModules())!!) {
                            if ((module)!!.getName() == tag) {
                                /*for(ItemModule forb_module : module.getForrbidenModules()) {
	        					if(stack.getTagCompound().hasKey(forb_module.getName())) check = false;
	        				}*/
                                get_module = module
                                break
                            }
                        }
                    }

                    if (get_module != null) {
                        if (get_module.getForrbidenModules() != null) for (forb_module in (get_module.getForrbidenModules())!!) {
                            if (stack.getTagCompound()!!.hasKey((forb_module)!!.getName())) {
                                check = false
                                break
                            }
                        }

                        if (!stack.getTagCompound()!!.hasKey(tag) || !stack.getTagCompound()!!.getBoolean(tag)) turn =
                            true
                        else if (stack.getTagCompound()!!.hasKey(tag)) turn = false

                        if (turn) {
                            if (check && stack.getTagCompound()!!.getInteger(ItemSpaceSuit.mod_count) > 0) {
                                for (con: ItemStack? in (get_module.getItemsForModule())!!) {
                                    consumed = GSEventHandler.consumeItemStack(playerBase.inventory, (con)!!)
                                }
                                if (consumed || playerBase.capabilities.isCreativeMode) {
                                    val copied: ItemStack = stack

                                    copied.getTagCompound()!!.setBoolean(tag, true)
                                    copied.getTagCompound()!!.setInteger(
                                        ItemSpaceSuit.mod_count,
                                        copied.getTagCompound()!!.getInteger(ItemSpaceSuit.mod_count) - 1
                                    )
                                    tileEntity.setInventorySlotContents(0, copied)
                                    tileEntity.markDirty()
                                    playerBase.world.scheduleUpdate(tileEntity.getPos(), tileEntity.getBlockType(), 0)
                                }
                            }
                        } else {
                            if (!playerBase.capabilities.isCreativeMode) for (con in (get_module.getItemsForModule())!!) {
                                if ((con)!!.getItemDamage() == OreDictionary.WILDCARD_VALUE) playerBase.addItemStackToInventory(
                                    ItemStack((con)!!.getItem(), 1, (con)!!.getMaxDamage())
                                )
                                else playerBase.addItemStackToInventory(con)
                            }

                            val copied: ItemStack = stack
                            copied.getTagCompound()!!.removeTag(tag)
                            copied.getTagCompound()!!.setInteger(
                                ItemSpaceSuit.mod_count,
                                copied.getTagCompound()!!.getInteger(ItemSpaceSuit.mod_count) + 1
                            )
                            tileEntity.setInventorySlotContents(0, copied)
                            tileEntity.markDirty()
                            playerBase.world.scheduleUpdate(tileEntity.getPos(), tileEntity.getBlockType(), 0)
                        }
                    }
                }
            }

            GSEnumSimplePacket.S_UPDATE_NBT_ITEM_IN_ARMOR -> {
                val armor_slot: Int = this.data!!.get(0) as Int
                tag = this.data!!.get(1) as String

                val armor: ItemStack = playerBase.inventory.armorInventory.get(armor_slot)
                if (armor != ItemStack.EMPTY && armor.getItem() is ItemSpaceSuit) {
                    val copied: ItemStack = armor
                    copied.getTagCompound()!!.setBoolean(tag, !copied.getTagCompound()!!.getBoolean(tag))
                    playerBase.inventory.armorInventory.set(armor_slot, copied)

                    //GalaxySpace.debug(copied.getTagCompound().toString());
                }
            }

            else -> {}
        }
} while (false)
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
