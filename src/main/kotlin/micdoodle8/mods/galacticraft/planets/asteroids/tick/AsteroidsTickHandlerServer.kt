package micdoodle8.mods.galacticraft.planets.asteroids.tick

import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent

import java.util.concurrent.atomic.AtomicBoolean
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.ShortRangeTelepadHandler
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.lang.Exception
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.LinkedList

open class AsteroidsTickHandlerServer {
    @SubscribeEvent
    open fun onServerTick(event: TickEvent.ServerTickEvent) {
        val server: MinecraftServer? = FMLCommonHandler.instance().getMinecraftServerInstance()
        //Prevent issues when clients switch to LAN servers
        if (server == null) {
            return
        }

        if (event.phase == TickEvent.Phase.START) {
            TileEntityMinerBase.Companion.checkNewMinerBases()

            if (spaceRaceData == null) {
                val world: World = server.getWorld(0)
                spaceRaceData = world.getMapStorage()!!.getOrLoadData(
                    ShortRangeTelepadHandler::class.java,
                    ShortRangeTelepadHandler.saveDataID
                ) as ShortRangeTelepadHandler?

                if (spaceRaceData == null) {
                    spaceRaceData = ShortRangeTelepadHandler(ShortRangeTelepadHandler.saveDataID)
                    world.getMapStorage()!!.setData(ShortRangeTelepadHandler.saveDataID, spaceRaceData)
                }
            }

            var index: Int = -1
            for (miner: EntityAstroMiner in activeMiners) {
                index++
                if (miner.isDead) {
//                    minerIt.remove();  Don't remove it, we want the index number to be static for the others
                    continue
                }

                if (miner.playerMP != null) {
                    val stats: GCPlayerStats? = GCPlayerStats.get((miner.playerMP)!!)
                    if (stats != null) {
                        val list: MutableList<BlockVec3?> = (stats.getActiveAstroMinerChunks())!!
                        var inListAlready: Boolean = false
                        val it: MutableIterator<BlockVec3> = (list.iterator() as MutableIterator<BlockVec3>)
                        while (it.hasNext()) {
                            val data: BlockVec3 = it.next()
                            if (data.sideDoneBits == index)  //SideDoneBits won't be saved to NBT, but during an active server session we can use it as a cross-reference to the index here - it's a 4th data int hidden inside a BlockVec3
                            {
                                if (miner.isDead) {
                                    it.remove() //Player stats should not save position of dead AstroMiner entity (probably broken by player deliberately breaking it)
                                } else {
                                    data.x = miner.chunkCoordX
                                    data.z = miner.chunkCoordZ
                                }
                                inListAlready = true
                                break
                            }
                        }
                        if (!inListAlready && !miner.isDead) {
                            val data: BlockVec3 = BlockVec3(miner.chunkCoordX, miner.dimension, miner.chunkCoordZ)
                            data.sideDoneBits = index
                            list.add(data)
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    open fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            var index: Int = -1
            for (miner: EntityAstroMiner in activeMiners) {
                index++
                if (miner.isDead) {
//                    minerIt.remove();  Don't remove it, we want the index number to be static for the others
                    continue
                }
                if (miner.playerMP != null && miner.world === event.world) {
                    try {
                        if (droppedChunks == null) {
                            val clazz: Class<*> = (miner.world as WorldServer).getChunkProvider().javaClass
                            droppedChunks =
                                clazz.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "droppedChunksSet" else "field_73248_b")
                        }
                        val undrop: MutableSet<Long> =
                            droppedChunks!!.get((miner.world as WorldServer).getChunkProvider()) as MutableSet<Long>
                        undrop.remove(ChunkPos.asLong(miner.chunkCoordX, miner.chunkCoordZ))
                    } catch (ignore: Exception) {
                    }
                }
            }
        }
    }

    companion object {
        @JvmField
        var spaceRaceData: ShortRangeTelepadHandler? = null

        @JvmField
        var activeMiners: MutableList<EntityAstroMiner> = ArrayList<EntityAstroMiner>()

        @JvmField
        var loadingSavedChunks: AtomicBoolean = AtomicBoolean()
        private var droppedChunks: Field? = null

        @JvmStatic
        fun restart() {
            spaceRaceData = null
            activeMiners.clear()
        }

        @JvmStatic
        fun removeChunkData(stats: GCPlayerStats, entityToRemove: EntityAstroMiner?) {
            var index: Int = 0
            for (miner: EntityAstroMiner? in activeMiners) {
                if (miner === entityToRemove)  //Found it in the list here
                {
                    val list: MutableList<BlockVec3?> = (stats.getActiveAstroMinerChunks())!!
                    val it: MutableIterator<BlockVec3> = (list.iterator() as MutableIterator<BlockVec3>)
                    while (it.hasNext()) {
                        val data: BlockVec3 = it.next()
                        if (data.sideDoneBits == index)  //Found it in the player's stats
                        {
                            it.remove()
                            return
                        }
                    }
                    return
                }
                index++
            }
        }

        /**
         * How this works: every spawned or saved (in player stats) miner is added to the
         * activeMiners list here.
         * Once per server tick its position will be saved to player stats.
         * When the player quits, the saved miner positions will be saved with the player's stats NBT
         * When the player next loads, loadAstroChunkList will force load those chunks, therefore
         * reactivating AstroMiners if those chunks weren't already loaded.
         */
        @JvmStatic
        fun loadAstroChunkList(activeChunks: MutableList<BlockVec3?>) {
            val copyList: MutableList<BlockVec3> = LinkedList<BlockVec3>(activeChunks)
            activeChunks.clear()
            if (!(loadingSavedChunks.getAndSet(true))) {
                for (data: BlockVec3 in copyList) {
                    val p: WorldProvider? = WorldUtil.getProviderForDimensionServer(data.y)
                    if (p != null && p.world != null) {
                        GCLog.debug("Loading chunk " + data.y + ": " + data.x + "," + data.z + " - should contain a miner!")
                        val w: WorldServer = p.world as WorldServer
                        CompatibilityManager.forceLoadChunks(w)
                        w.getChunkProvider().loadChunk(data.x, data.z)
                        CompatibilityManager.forceLoadChunksEnd(w)
                    }
                }
                loadingSavedChunks.set(false)
            }
        }

        @JvmStatic
        fun monitorMiner(entityAstroMiner: EntityAstroMiner): Int {
            val result: Int = activeMiners.size
            activeMiners.add(entityAstroMiner)
            GCLog.debug("Monitoring miner " + result)
            return result
        }
    }
}
