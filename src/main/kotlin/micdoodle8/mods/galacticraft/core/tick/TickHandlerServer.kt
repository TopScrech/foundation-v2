package micdoodle8.mods.galacticraft.core.tick

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent

import java.awt.image.BufferedImage
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ConcurrentHashMap
import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch
import micdoodle8.mods.galacticraft.core.command.CommandGCHouston
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.dimension.WorldDataSpaceRaces
import micdoodle8.mods.galacticraft.core.energy.grid.EnergyNetwork
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.fluid.ThreadFindSeal
import micdoodle8.mods.galacticraft.core.network.GalacticraftPacketHandler
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTransmitter
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import micdoodle8.mods.galacticraft.core.tile.TileEntityPainter
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.MapUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledBlockChange
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledDimensionChange
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.gen.ChunkProviderServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.io.File
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.TreeMap
import kotlin.math.floor
import kotlin.math.max

open class TickHandlerServer {
    private val MAX_BLOCKS_PER_TICK = 50000

    @SubscribeEvent
    open fun worldUnloadEvent(event: WorldEvent.Unload) {
        for (packetHandler: GalacticraftPacketHandler in packetHandlers) {
            packetHandler.unload(event.getWorld())
        }
    }

    @SubscribeEvent
    open fun onServerTick(event: TickEvent.ServerTickEvent) {
        val server: MinecraftServer? = FMLCommonHandler.instance().getMinecraftServerInstance()
        //Prevent issues when clients switch to LAN servers
        if (server == null) {
            return
        }

        if (event.phase == TickEvent.Phase.START) {
            if (timerHoustonCommand > 0) {
                if (--timerHoustonCommand == 0) {
                    CommandGCHouston.reset()
                }
            }

            for (change: ScheduledDimensionChange in scheduledDimensionChanges) {
                try {
                    val stats: GCPlayerStats = (GCPlayerStats.get((change.getPlayer())!!))!!
                    val provider: WorldProvider? = if (change.getDimensionId() != null) WorldUtil.getProviderForDimensionServer(change.getDimensionId()!!) else change.getDimensionName()?.let { WorldUtil.getProviderForNameServer(it) }
                    if (provider != null) {
                        val dim: Int = GCCoreUtil.getDimensionID(provider)
                        GCLog.info("Found matching world (" + dim.toString() + ") for name: " + change.getDimensionName())

                        if ((change.getPlayer())!!.level() is WorldServer) {
                            val world: WorldServer? = (change.getPlayer())!!.level() as WorldServer?

                            WorldUtil.transferEntityToDimension((change.getPlayer())!!, dim, (world)!!)
                        }
                    } else {
                        GCLog.severe("World not found when attempting to transfer entity to dimension: " + change.getDimensionName())
                    }

                    stats.setTeleportCooldown(10)
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_CLOSE_GUI,
                            GCCoreUtil.getDimensionID((change.getPlayer())!!.level()),
                            arrayOf<Any?>()
                        ), change.getPlayer()
                    )
                } catch (e: Exception) {
                    GCLog.severe("Error occurred when attempting to transfer entity to dimension: " + change.getDimensionName())
                    e.printStackTrace()
                }
            }

            scheduledDimensionChanges.clear()

            if (MapUtil.calculatingMap.get()) {
                MapUtil.BiomeMapNextTick_MultiThreaded()
            } else if (!MapUtil.doneOverworldTexture) {
                MapUtil.makeOverworldTexture()
            }

            if (spaceRaceData == null) {
                val world: World = server.getWorld(0)
                spaceRaceData = world.getMapStorage()!!.getOrLoadData(
                    WorldDataSpaceRaces::class.java,
                    WorldDataSpaceRaces.saveDataID
                ) as WorldDataSpaceRaces?

                if (spaceRaceData == null) {
                    spaceRaceData = WorldDataSpaceRaces(WorldDataSpaceRaces.saveDataID)
                    world.getMapStorage()!!.setData(WorldDataSpaceRaces.saveDataID, spaceRaceData)
                }
            }

            SpaceRaceManager.tick()

            TileEntityOxygenSealer.onServerTick()

            if (tickCount % 33 == 0L) {
                val worlds: Array<WorldServer> = server.worlds

                for (i: Int in worlds.indices.reversed()) {
                    val world: WorldServer? = worlds[i]
                    TileEntityPainter.onServerTick((world)!!)
                }
            }
            if (tickCount % 100 == 0L) {
                val worlds: Array<WorldServer> = server.worlds

                for (i: Int in worlds.indices) {
                    val world: WorldServer = worlds[i]
                    val chunkProviderServer: ChunkProviderServer = world.getChunkProvider()

                    val footprintMap: MutableMap<Long, MutableList<Footprint?>?>? =
                        serverFootprintMap.get(GCCoreUtil.getDimensionID(world))

                    if (footprintMap != null) {
                        var mapChanged: Boolean = false

                        if (chunkProviderServer != null) {
                            val iterator: MutableIterator<*> = chunkProviderServer.getLoadedChunks().iterator()

                            while (iterator.hasNext()) {
                                val chunk: Chunk = iterator.next() as Chunk
                                val chunkKey: Long = ChunkPos.asLong(chunk.x, chunk.z)

                                val footprints: MutableList<Footprint?>? = footprintMap.get(chunkKey)

                                if (footprints != null) {
                                    val toRemove: MutableList<Footprint?> = ArrayList<Footprint?>()

                                    for (j: Int in footprints.indices) {
                                        footprints.get(j)!!.age = (footprints.get(j)!!.age + 100).toShort()

                                        if (footprints.get(j)!!.age >= Footprint.MAX_AGE) {
                                            toRemove.add(footprints.get(j))
                                        }
                                    }

                                    if (!toRemove.isEmpty()) {
                                        footprints.removeAll(toRemove)
                                    }

                                    footprintMap.put(chunkKey, footprints)
                                    mapChanged = true

                                    (GalacticraftCore.packetPipeline)!!.sendToDimension(
                                        PacketSimple(
                                            PacketSimple.EnumSimplePacket.C_UPDATE_FOOTPRINT_LIST,
                                            worlds[i].provider.getDimension(),
                                            arrayOf<Any>(chunkKey, footprints.toTypedArray<Footprint?>())
                                        ), worlds[i].provider.getDimension()
                                    )
                                }
                            }
                        }

                        if (mapChanged) {
                            serverFootprintMap.put(GCCoreUtil.getDimensionID(world), footprintMap)
                        }
                    }
                }
            }

            if (!footprintBlockChanges.isEmpty()) {
                for (targetPoint: BlockVec3Dim in footprintBlockChanges) {
                    val worlds: Array<WorldServer> = server.worlds

                    for (i: Int in worlds.indices) {
                        val world: WorldServer = worlds[i]

                        if (GCCoreUtil.getDimensionID(world) == targetPoint.dim) {
                            val chunkKey: Long = ChunkPos.asLong(targetPoint.x shr 4, targetPoint.z shr 4)
                            (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                                PacketSimple(
                                    PacketSimple.EnumSimplePacket.C_FOOTPRINTS_REMOVED,
                                    GCCoreUtil.getDimensionID(world),
                                    arrayOf<Any>(chunkKey, BlockVec3(targetPoint.x, targetPoint.y, targetPoint.z))
                                ),
                                NetworkRegistry.TargetPoint(
                                    targetPoint.dim,
                                    targetPoint.x.toDouble(),
                                    targetPoint.y.toDouble(),
                                    targetPoint.z.toDouble(),
                                    50.0
                                )
                            )


                            //                            Map<Long, List<Footprint>> footprintMap = TickHandlerServer.serverFootprintMap.get(world.provider.dimensionId);
//
//                            if (footprintMap != null && !footprintMap.isEmpty())
//                            {
//                                List<Footprint> footprints = footprintMap.get(chunkKey);
//                                if (footprints != null)
//                                	GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_UPDATE_FOOTPRINT_LIST, new Object[] { chunkKey, footprints.toArray(new Footprint[footprints.size()]) }), new NetworkRegistry.TargetPoint(targetPoint.dim, targetPoint.x, targetPoint.y, targetPoint.z, 50));
//                            }
                        }
                    }
                }

                footprintBlockChanges.clear()
            }

            if (tickCount % 20 == 0L) {
                if (!playersRequestingMapData.isEmpty()) {
                    val baseFolder: File =
                        File(DimensionManager.getCurrentSaveRootDirectory(), "galacticraft/overworldMap")
                    if (!baseFolder.exists() && !baseFolder.mkdirs()) {
                        GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath())
                    } else {
                        val copy: ArrayList<EntityPlayerMP> = ArrayList<EntityPlayerMP>(playersRequestingMapData)
                        val reusable: BufferedImage = BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB)
                        for (playerMP: EntityPlayerMP in copy) {
                            val stats: GCPlayerStats = (GCPlayerStats.get(playerMP))!!
                            MapUtil.makeVanillaMap(
                                playerMP.dimension,
                                floor(stats.getCoordsTeleportedFromZ()).toInt() shr 4,
                                floor(stats.getCoordsTeleportedFromZ()).toInt() shr 4,
                                baseFolder,
                                reusable
                            )
                        }
                        playersRequestingMapData.removeAll(copy)
                    }
                }
            }

            tickCount++

            EnergyNetwork.tickCount++
        } else if (event.phase == TickEvent.Phase.END) {
            for (network: FluidNetwork in ArrayList<FluidNetwork>(fluidNetworks)) {
                if (!network.pipes.isEmpty()) {
                    network.tickEnd()
                } else {
                    fluidNetworks.remove(network)
                }
            }

            var maxPasses: Int = 10
            while (!networkTicks.isEmpty()) {
                val pass: LinkedList<EnergyNetwork> = LinkedList<EnergyNetwork>()
                pass.addAll((networkTicks as Collection<EnergyNetwork>))
                networkTicks.clear()
                for (grid: EnergyNetwork in pass) {
                    grid.tickEnd()
                }

                if (--maxPasses <= 0) {
                    break
                }
            }

            maxPasses = 10
            while (!oxygenTransmitterUpdates.isEmpty()) {
                val pass: LinkedList<TileEntityFluidTransmitter> = LinkedList<TileEntityFluidTransmitter>()
                pass.addAll((oxygenTransmitterUpdates as Collection<TileEntityFluidTransmitter>))
                oxygenTransmitterUpdates.clear()
                for (newTile: TileEntityFluidTransmitter in pass) {
                    if (!newTile.isInvalid()) {
                        newTile.refresh()
                    }
                }

                if (--maxPasses <= 0) {
                    break
                }
            }

            maxPasses = 10
            while (!energyTransmitterUpdates.isEmpty()) {
                val pass: LinkedList<TileBaseConductor> = LinkedList<TileBaseConductor>()
                pass.addAll((energyTransmitterUpdates as Collection<TileBaseConductor>))
                energyTransmitterUpdates.clear()
                for (newTile: TileBaseConductor in pass) {
                    if (!newTile.isInvalid()) {
                        newTile.refresh()
                    }
                }

                if (--maxPasses <= 0) {
                    break
                }
            }
        }
    }

    @SubscribeEvent
    open fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            val world: WorldServer = event.world as WorldServer

            val changeList: CopyOnWriteArrayList<ScheduledBlockChange?>? =
                scheduledBlockChanges.get(GCCoreUtil.getDimensionID(world))

            if (changeList != null && !changeList.isEmpty()) {
                var blockCount: Int = 0
                val blockCountMax: Int = max(this.MAX_BLOCKS_PER_TICK, changeList.size / 4)
                val newList: MutableList<ScheduledBlockChange?> =
                    ArrayList<ScheduledBlockChange?>(max(0, changeList.size - blockCountMax))

                for (change: ScheduledBlockChange? in changeList) {
                    if (++blockCount > blockCountMax) {
                        newList.add(change)
                    } else {
                        if (change != null) {
                            val changePosition: BlockPos = (change.getChangePosition())!!
                            val block: Block = world.getBlockState(changePosition).getBlock()
                            //Only replace blocks of type BlockAir or fire - this is to prevent accidents where other mods have moved blocks
                            if (changePosition != null && (block is BlockAir || block === Blocks.FIRE)) {
                                world.setBlock(
                                    changePosition,
                                    change.getChangeState(),
                                    change.getChangeUpdateFlag()
                                )
                            }
                        }
                    }
                }

                changeList.clear()
                scheduledBlockChanges.remove(GCCoreUtil.getDimensionID(world))
                if (newList.size > 0) {
                    scheduledBlockChanges.put(
                        GCCoreUtil.getDimensionID(world),
                        CopyOnWriteArrayList<ScheduledBlockChange?>(newList)
                    )
                }
            }

            val torchList: CopyOnWriteArrayList<BlockVec3?>? =
                scheduledTorchUpdates.get(GCCoreUtil.getDimensionID(world))

            if (torchList != null && !torchList.isEmpty()) {
                for (torch: BlockVec3? in torchList) {
                    if (torch != null) {
                        val pos: BlockPos = BlockPos(torch.x, torch.y, torch.z)
                        val b: Block = world.getBlockState(pos).getBlock()
                        if (b is BlockUnlitTorch) {
                            world.scheduleUpdate(pos, b, 2 + world.random.nextInt(30))
                        }
                    }
                }

                torchList.clear()
                scheduledTorchUpdates.remove(GCCoreUtil.getDimensionID(world))
            }

            if (world.provider is IOrbitDimension) {
                try {
                    val dim: Int =
                        GCCoreUtil.getDimensionID((WorldUtil.getProviderForNameServer(((world.provider as IOrbitDimension).getPlanetToOrbit())!!))!!)
                    val minY: Int = (world.provider as IOrbitDimension).getYCoordToTeleportToPlanet()

                    val entityList: Array<Entity> = ((world.loadedEntityList.toTypedArray<Entity?>() as Array<Entity>?))!!
                    for (e: Entity in entityList) {
                        if (e.y <= minY && e.level() === world) {
                            WorldUtil.transferEntityToDimension(e, dim, world, false, null)
                        }
                    }
                } catch (ex: Exception) {
                }
            }

            val dimensionID: Int = GCCoreUtil.getDimensionID(world)
            if (worldsNeedingUpdate.contains(dimensionID)) {
                worldsNeedingUpdate.remove(dimensionID)
                for (obj: Any? in event.world.loadedTileEntityList) {
                    val tile: TileEntity? = obj as TileEntity?
                    if (tile is TileEntityFluidTank) {
                        tile.updateClient = true
                    }
                }
            }
        } else if (event.phase == TickEvent.Phase.END) {
            val world: WorldServer = event.world as WorldServer

            for (handler: GalacticraftPacketHandler in packetHandlers) {
                handler.tick(world)
            }

            val dimID: Int = GCCoreUtil.getDimensionID(world)
            val edgesList: MutableSet<BlockPos?>? = edgeChecks.get(dimID)
            val checkedThisTick: HashSet<BlockPos?> = HashSet<BlockPos?>()

            if (edgesList != null && !edgesList.isEmpty()) {
                val edgesListCopy: MutableList<BlockPos?> = ArrayList<BlockPos?>()
                edgesListCopy.addAll(edgesList)
                for (edgeBlock: BlockPos? in edgesListCopy) {
                    if (edgeBlock != null && !checkedThisTick.contains(edgeBlock)) {
                        if (scheduledForChange(dimID, edgeBlock)) {
                            continue
                        }

                        val done: ThreadFindSeal =
                            ThreadFindSeal(world, edgeBlock, 0, (ArrayList<TileEntityOxygenSealer?>() as MutableList<TileEntityOxygenSealer>))
                        checkedThisTick.addAll(done.checkedAll())
                    }
                }

                edgeChecks.remove(GCCoreUtil.getDimensionID(world))
            }
        }
    }

    companion object {
        private val scheduledBlockChanges: MutableMap<Int, CopyOnWriteArrayList<ScheduledBlockChange?>?> =
            ConcurrentHashMap<Int, CopyOnWriteArrayList<ScheduledBlockChange?>?>()
        private val scheduledTorchUpdates: MutableMap<Int, CopyOnWriteArrayList<BlockVec3?>?> =
            ConcurrentHashMap<Int, CopyOnWriteArrayList<BlockVec3?>?>()
        private val edgeChecks: MutableMap<Int, MutableSet<BlockPos?>?> = TreeMap<Int, MutableSet<BlockPos?>?>()
        private val networkTicks = LinkedList<EnergyNetwork?>()

        @JvmField
        var serverFootprintMap: MutableMap<Int, MutableMap<Long, MutableList<Footprint?>?>?> =
            TreeMap<Int, MutableMap<Long, MutableList<Footprint?>?>?>()

        @JvmField
        var footprintBlockChanges: MutableList<BlockVec3Dim> = Lists.newArrayList<BlockVec3Dim>()

        @JvmField
        var spaceRaceData: WorldDataSpaceRaces? = null

        @JvmField
        var playersRequestingMapData: ArrayList<EntityPlayerMP> = Lists.newArrayList<EntityPlayerMP>()
        private var tickCount: Long = 0

        @JvmField
        var oxygenTransmitterUpdates: LinkedList<TileEntityFluidTransmitter?> =
            LinkedList<TileEntityFluidTransmitter?>()

        @JvmField
        var energyTransmitterUpdates: LinkedList<TileBaseConductor?> = LinkedList<TileBaseConductor?>()
        private val scheduledDimensionChanges = CopyOnWriteArrayList<ScheduledDimensionChange>()
        private val packetHandlers: MutableList<GalacticraftPacketHandler> =
            Lists.newCopyOnWriteArrayList<GalacticraftPacketHandler>()
        private val fluidNetworks: MutableList<FluidNetwork> = Lists.newArrayList<FluidNetwork>()

        @JvmField
        var timerHoustonCommand: Int = 0

        @JvmStatic
        fun addFluidNetwork(network: FluidNetwork) {
            fluidNetworks.add(network)
        }

        @JvmStatic
        fun removeFluidNetwork(network: FluidNetwork?) {
            fluidNetworks.remove(network)
        }

        @JvmStatic
        fun addPacketHandler(handler: GalacticraftPacketHandler) {
            packetHandlers.add(handler)
        }

        @JvmStatic
        fun restart() {
            scheduledBlockChanges.clear()
            scheduledTorchUpdates.clear()
            edgeChecks.clear()
            networkTicks.clear()
            serverFootprintMap.clear()
            oxygenTransmitterUpdates.clear()
            //        TickHandlerServer.hydrogenTransmitterUpdates.clear();
            energyTransmitterUpdates.clear()
            playersRequestingMapData.clear()
            networkTicks.clear()

            for (race: SpaceRace? in (SpaceRaceManager.getSpaceRaces())!!) {
                SpaceRaceManager.removeSpaceRace(race)
            }

            spaceRaceData = null
            tickCount = 0L
            fluidNetworks.clear()
            MapUtil.reset()
        }

        @JvmStatic
        fun addFootprint(chunkKey: Long, print: Footprint?, dimID: Int) {
            var footprintMap: MutableMap<Long, MutableList<Footprint?>?>? = serverFootprintMap.get(dimID)
            var footprints: MutableList<Footprint?>?

            if (footprintMap == null) {
                footprintMap = HashMap<Long, MutableList<Footprint?>?>()
                serverFootprintMap.put(dimID, footprintMap)
                footprints = ArrayList<Footprint?>()
                footprintMap.put(chunkKey, footprints)
            } else {
                footprints = footprintMap.get(chunkKey)

                if (footprints == null) {
                    footprints = ArrayList<Footprint?>()
                    footprintMap.put(chunkKey, footprints)
                }
            }

            footprints.add(print)
        }

        @JvmStatic
        fun scheduleNewBlockChange(dimID: Int, change: ScheduledBlockChange) {
            var changeList: CopyOnWriteArrayList<ScheduledBlockChange?>? = scheduledBlockChanges.get(dimID)

            if (changeList == null) {
                changeList = CopyOnWriteArrayList<ScheduledBlockChange?>()
            }

            changeList.add(change)
            scheduledBlockChanges.put(dimID, changeList)
        }

        /**
         * Only use this for AIR blocks (any type of BlockAir)
         *
         * @param dimID
         * @param changeAdd List of <ScheduledBlockChange>
        </ScheduledBlockChange> */
        @JvmStatic
        fun scheduleNewBlockChange(dimID: Int, changeAdd: MutableList<ScheduledBlockChange?>?) {
            var changeList: CopyOnWriteArrayList<ScheduledBlockChange?>? = scheduledBlockChanges.get(dimID)

            if (changeList == null) {
                changeList = CopyOnWriteArrayList<ScheduledBlockChange?>()
            }

            changeList.addAll(changeAdd!!)
            scheduledBlockChanges.put(dimID, changeList)
        }

        @JvmStatic
        fun scheduleNewDimensionChange(change: ScheduledDimensionChange) {
            scheduledDimensionChanges.add(change)
        }

        @JvmStatic
        fun scheduleNewTorchUpdate(dimID: Int, torches: MutableList<BlockVec3?>?) {
            var updateList: CopyOnWriteArrayList<BlockVec3?>? = scheduledTorchUpdates.get(dimID)

            if (updateList == null) {
                updateList = CopyOnWriteArrayList<BlockVec3?>()
            }

            updateList.addAll(torches!!)
            scheduledTorchUpdates.put(dimID, updateList)
        }

        @JvmStatic
        fun scheduleNewEdgeCheck(dimID: Int, edgeBlock: BlockPos?) {
            var updateList: MutableSet<BlockPos?>? = edgeChecks.get(dimID)

            if (updateList == null) {
                updateList = HashSet<BlockPos?>()
            }

            updateList.add(edgeBlock)
            edgeChecks.put(dimID, updateList)
        }

        @JvmStatic
        fun scheduledForChange(dimID: Int, test: BlockPos): Boolean {
            val changeList: CopyOnWriteArrayList<ScheduledBlockChange?>? = scheduledBlockChanges.get(dimID)

            if (changeList != null) {
                for (changeEntry in changeList) {
                val change = changeEntry!!
                    if (test == change.getChangePosition()) {
                        return true
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun scheduleNetworkTick(grid: EnergyNetwork?) {
            networkTicks.add(grid)
        }

        @JvmStatic
        fun removeNetworkTick(grid: EnergyNetwork?) {
            networkTicks.remove(grid)
        }

        private val worldsNeedingUpdate: MutableSet<Int> = HashSet<Int>()

        @JvmStatic
        fun markWorldNeedsUpdate(dimension: Int) {
            worldsNeedingUpdate.add(dimension)
        }
    }
}
