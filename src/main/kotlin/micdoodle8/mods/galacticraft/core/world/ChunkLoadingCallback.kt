package micdoodle8.mods.galacticraft.core.world

import net.minecraftforge.common.ForgeChunkManager.LoadingCallback
import net.minecraftforge.common.ForgeChunkManager.Ticket
import java.util.Map.Entry

import java.io.IOException
import java.io.FileInputStream
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.config.Configuration
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.lang.Exception
import java.util.HashMap
import java.util.HashSet

open class ChunkLoadingCallback : ForgeChunkManager.LoadingCallback {
    override fun ticketsLoaded(tickets: MutableList<ForgeChunkManager.Ticket>, world: World) {
        for (ticket: ForgeChunkManager.Ticket in tickets) {
            val nbt: NBTTagCompound? = ticket.getModData()

            if (nbt != null) {
                val tileX: Int = nbt.getInteger("ChunkLoaderTileX")
                val tileY: Int = nbt.getInteger("ChunkLoaderTileY")
                val tileZ: Int = nbt.getInteger("ChunkLoaderTileZ")
                val tile: TileEntity? = world.getTileEntity(BlockPos(tileX, tileY, tileZ))

                if (tile is IChunkLoader) {
                    (tile as IChunkLoader).onTicketLoaded(ticket, false)
                }
            }
        }
    }

    companion object {
        private var loaded = false
        private val chunkLoaderList = HashMap<String?, HashMap<Int, HashSet<BlockPos?>?>?>()

        // private static HashMap<Integer, HashSet<IChunkLoader>> loadedChunks = new
        // HashMap<Integer, HashSet<IChunkLoader>>();
        private var configLoaded = false
        private var config: Configuration? = null

        // private static boolean keepLoadedOffline;
        private var loadOnLogin = false
        private var dirtyData = false

        @JvmStatic
        fun loadConfig(file: File) {
            if (!configLoaded) {
                config = Configuration(file)
            }

            try {
                // keepLoadedOffline = config.get("CHUNKLOADING",
                // "OfflineKeepLoaded", true,
                // "Set to false if you want each player's chunk loaders to unload when they log out.").getBoolean(true);
                loadOnLogin = config!!.get(
                    "CHUNKLOADING",
                    "LoadOnLogin",
                    true,
                    "If you don't want each player's chunks to load when they log in, set to false."
                ).getBoolean(true)
            } catch (e: Exception) {
                GCLog.severe("Problem loading chunkloading config (\"core.conf\")")
            } finally {
                if (config!!.hasChanged()) {
                    config!!.save()
                }

                configLoaded = true
            }
        }

        @JvmStatic
        fun addToList(world: World, x: Int, y: Int, z: Int, playerName: String?) {
            var dimensionMap: HashMap<Int, HashSet<BlockPos?>?>? = chunkLoaderList.get(playerName)

            if (dimensionMap == null) {
                dimensionMap = HashMap<Int, HashSet<BlockPos?>?>()
                chunkLoaderList.put(playerName, dimensionMap)
            }

            var chunkLoaders: HashSet<BlockPos?>? = dimensionMap.get(world.provider.getDimension())

            if (chunkLoaders == null) {
                chunkLoaders = HashSet<BlockPos?>()
            }

            chunkLoaders.add(BlockPos(x, y, z))
            dimensionMap.put(GCCoreUtil.getDimensionID(world), chunkLoaders)
            chunkLoaderList.put(playerName, dimensionMap)
            dirtyData = true
        }

        @JvmStatic
        fun forceChunk(ticket: ForgeChunkManager.Ticket?, world: World, x: Int, y: Int, z: Int, playerName: String?) {
            addToList(world, x, y, z, playerName)
            val chunkPos: ChunkPos = ChunkPos(x shr 4, z shr 4)
            ForgeChunkManager.forceChunk(ticket, chunkPos)
            //
            // TileEntity tile = world.getTileEntity(x, y, z);
            //
            // if (tile instanceof IChunkLoader)
            // {
            // IChunkLoader chunkLoader = (IChunkLoader) tile;
            // int dimID = world);
            //
            // HashSet<IChunkLoader> chunkList = loadedChunks.get(dimID);
            //
            // if (chunkList == null)
            // {
            // chunkList = new HashSet<IChunkLoader>();
            // }
            //
            // ForgeChunkManager.forceChunk(ticket, chunkPos);
            // chunkList.add(chunkLoader);
            // loadedChunks.put(dimID, chunkList);
            // }
        }

        @JvmStatic
        fun save(world: WorldServer?) {
            if (!dirtyData) {
                return
            }

            val saveDir: File? = getSaveDir()

            if (saveDir != null) {
                val saveFile: File = File(saveDir, "chunkloaders.dat")

                if (!saveFile.exists()) {
                    try {
                        if (!saveFile.createNewFile()) {
                            GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath())
                        }
                    } catch (e: IOException) {
                        GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath())
                        e.printStackTrace()
                    }
                }

                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(saveFile)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                if (fos != null) {
                    val dataStream: DataOutputStream = DataOutputStream(fos)
                    try {
                        dataStream.writeInt(chunkLoaderList.size)

                        for (playerEntry: MutableMap.MutableEntry<String?, HashMap<Int, HashSet<BlockPos?>?>?> in chunkLoaderList.entries) {
                            dataStream.writeUTF(playerEntry.key)
                            dataStream.writeInt(playerEntry.value!!.size)

                            for (dimensionEntry in playerEntry.value!!.entries) {
                                dataStream.writeInt(dimensionEntry.key!!)
                                dataStream.writeInt(dimensionEntry.value!!.size)

                                for (coords in dimensionEntry.value!!) {
                                    dataStream.writeInt((coords)!!.getX())
                                    dataStream.writeInt((coords)!!.getY())
                                    dataStream.writeInt((coords)!!.getZ())
                                }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    try {
                        dataStream.close()
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            dirtyData = false
        }

        @JvmStatic
        private fun getSaveDir(): File? {
            if (DimensionManager.getWorld(0) != null) {
                val saveDir: File = File(DimensionManager.getCurrentSaveRootDirectory(), "galacticraft")

                if (!saveDir.exists()) {
                    if (!saveDir.mkdirs()) {
                        GCLog.severe("Could not create chunk loader save data folder: " + saveDir.getAbsolutePath())
                    }
                }

                return saveDir
            }

            return null
        }

        @JvmStatic
        fun load(world: WorldServer?) {
            if (loaded) {
                return
            }

            var dataStream: DataInputStream? = null

            try {
                val saveDir: File? = getSaveDir()

                if (saveDir != null) {
                    if (!saveDir.exists()) {
                        if (!saveDir.mkdirs()) {
                            GCLog.severe("Could not create chunk loader save data folder: " + saveDir.getAbsolutePath())
                        }
                    }

                    val saveFile: File = File(saveDir, "chunkloaders.dat")

                    if (saveFile.exists()) {
                        dataStream = DataInputStream(FileInputStream(saveFile))

                        val playerCount: Int = dataStream.readInt()

                        for (l: Int in 0..<playerCount) {
                            val ownerName: String = dataStream.readUTF()

                            val mapSize: Int = dataStream.readInt()
                            val dimensionMap: HashMap<Int, HashSet<BlockPos?>?> = HashMap<Int, HashSet<BlockPos?>?>()

                            for (i: Int in 0..<mapSize) {
                                val dimensionID: Int = dataStream.readInt()
                                val coords: HashSet<BlockPos?> = HashSet<BlockPos?>()
                                dimensionMap.put(dimensionID, coords)
                                val coordSetSize: Int = dataStream.readInt()

                                for (j: Int in 0..<coordSetSize) {
                                    coords.add(
                                        BlockPos(
                                            dataStream.readInt(),
                                            dataStream.readInt(),
                                            dataStream.readInt()
                                        )
                                    )
                                }
                            }

                            chunkLoaderList.put(ownerName, dimensionMap)
                        }

                        dataStream.close()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                if (dataStream != null) {
                    try {
                        dataStream.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                }
            }

            loaded = true
            dirtyData = false
        }

        @JvmStatic
        fun onPlayerLogin(player: EntityPlayer?) {
            for (playerEntry: MutableMap.MutableEntry<String?, HashMap<Int, HashSet<BlockPos?>?>?> in chunkLoaderList.entries) {
                if (PlayerUtil.getName(player) == playerEntry.key) {
                    for (dimensionEntry: MutableMap.MutableEntry<Int, HashSet<BlockPos?>?> in playerEntry.value!!.entries) {
                        val dimID: Int = dimensionEntry.key!!

                        if (loadOnLogin) {
                            player!!.world.getMinecraftServer()!!.getWorld(dimID)
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun onPlayerLogout(player: EntityPlayer?) {
            // if (!keepLoadedOffline)
            // {
            // for (Entry<Integer, HashSet<IChunkLoader>> dimEntry :
            // loadedChunks.entrySet())
            // {
            // int dimID = dimEntry.getKey();
            //
            // for (IChunkLoader loader : new
            // ArrayList<IChunkLoader>(dimEntry.getValue()))
            // {
            // World world = loader.getWorldObj();
            //
            // BlockPos coords = loader.getCoords();
            // TileEntity tile = world.getTileEntity(coords.posX, coords.posY,
            // coords.posZ);
            //
            // if (tile != null && tile.equals(loader))
            // {
            // Chunk chunkAt = world.getChunkFromChunkCoords(coords.posX >> 4,
            // coords.posZ >> 4);
            // boolean foundOtherLoader = false;
            //
            // for (Object o : chunkAt.chunkTileEntityMap.values())
            // {
            // TileEntity otherTile = (TileEntity) o;
            //
            // if (otherTile != null && !otherTile.equals(tile))
            // {
            // if (otherTile instanceof IChunkLoader)
            // {
            // IChunkLoader otherLoader = (IChunkLoader) otherTile;
            //
            // if (!otherLoader.getOwnerName().equals(loader.getOwnerName()))
            // {
            // HashMap<Integer, HashSet<BlockPos>> otherDimMap =
            // chunkLoaderList.get(loader.getOwnerName());
            //
            // if (otherDimMap != null)
            // {
            // HashSet<BlockPos> otherLoaders = otherDimMap.get(dimID);
            //
            // if (otherLoaders != null && otherLoaders.contains(otherLoader))
            // {
            // foundOtherLoader = true;
            // break;
            // }
            // }
            // }
            // }
            // }
            // }
            //
            // if (!foundOtherLoader)
            // {
            // ForgeChunkManager.unforceChunk(loader.getTicket(), new
            // ChunkCoordIntPair(coords.posX >> 4, coords.posZ >> 4));
            // dimEntry.getValue().remove(loader);
            // }
            // }
            // else
            // {
            // dimEntry.getValue().remove(loader);
            //
            // HashMap<Integer, HashSet<BlockPos>> dimMap =
            // chunkLoaderList.get(PlayerUtil.getName(player));
            //
            // if (dimMap != null)
            // {
            // HashSet<BlockPos> coordSet = dimMap.get(dimID);
            //
            // if (coordSet != null)
            // {
            // coordSet.remove(loader.getCoords());
            // }
            //
            // dimm
            // chunkLoaderList.put(PlayerUtil.getName(player), dimMap);
            // }
            // }
            // }
            // }
            // }
        }
    }
}
