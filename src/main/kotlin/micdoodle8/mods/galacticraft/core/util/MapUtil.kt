package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.awt.image.BufferedImage
import javax.imageio.stream.ImageInputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.ImageReadParam
import javax.imageio.ImageTypeSpecifier
import javax.imageio.stream.ImageOutputStream
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.IIOImage
import java.util.zip.Deflater
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.DynamicTextureProper
import micdoodle8.mods.galacticraft.core.client.screen.DrawGameScreen
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.resources.IResourceManager
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldProviderSurface
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.commons.io.FileUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedList
import java.util.Random
import java.util.TreeMap
import kotlin.math.max

object MapUtil {
    //Mapgen management
    @JvmField
    var calculatingMap: AtomicBoolean = AtomicBoolean()

    @JvmField
    var resetClientFlag: AtomicBoolean = AtomicBoolean()
    private var currentMap: MapGen? = null
    private var slowMap: MapGen? = null
    private var threadCurrentMap: Thread? = null
    private var threadSlowMap: Thread? = null

    @JvmField
    var doneOverworldTexture: Boolean = false
    private val queuedMaps = LinkedList<MapGen?>()

    @JvmField
    var clientRequests: LinkedList<String?> = LinkedList<String?>()

    @JvmField
    var biomeColours: ArrayList<BlockVec3?> = ArrayList<BlockVec3?>(40)
    private val rand = Random()
    private var overworldImageBytesPart: ByteArray? = null //Used client side only? = null
    private var overworldImageCompressed: ByteArray? = null

    //Map size definitions
    private const val SIZE_STD = 176

    @JvmField
    val SIZE_STD2: Int = SIZE_STD * 2

    const val OVERWORLD_LARGEMAP_WIDTH: Int =
        1536 //Do not make a large map whose raw binary exceeds 2MB otherwise sendMapPacket() will not send it.  This raw binary is 576kB
    private const val OVERWORLD_LARGEMAP_HEIGHT = 960
    private const val OVERWORLD_MAP_SCALE =
        4 //Recommended is 4.  This gives a large overworld map of size (1536 x 16) by (960 x 16): that extends 12000 blocks from spawn in both EW directions and 7600 blocks from spawn north and south
    private const val OVERWORLD_TEXTURE_WIDTH = 192 //Do not change - planet texture needs to be this size
    private const val OVERWORLD_TEXTURE_HEIGHT = 48 //Do not change - planet texture needs to be this size
    private const val OVERWORLD_TEXTURE_SCALE = 7

    private const val LARGEMAP_MARKER =
        30000001 //This is a marker to flag world map packets, it must be an impossible cx coordinate

    //Color related constants
    private const val OCEAN_HEIGHT = 63
    private const val DEEP_OCEAN = 56


    init {
        //TODO: Deal with mods like ExtraBiomes
        setupColours()
    }

    @JvmStatic
    fun reset() {
        if (currentMap != null) currentMap!!.abort()
        currentMap = null
        saveMapProgress()
        threadCurrentMap = null
        threadSlowMap = null
        queuedMaps.clear()
        calculatingMap.set(false)
        doneOverworldTexture = false
        overworldImageCompressed = null
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun resetClient() {
        resetClientFlag.set(true)
        //Threadsafe
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun resetClientBody() {
        ClientProxyCore.overworldTexturesValid = false
        clientRequests.clear()
        overworldImageBytesPart = null
        val baseFolder: File = File(FMLClientHandler.instance().getClient().gameDir, "assets/galacticraftMaps")
        if (baseFolder.exists() && baseFolder.isDirectory()) {
            for (f: File in baseFolder.listFiles()) {
                if (f.isFile()) {
                    f.delete()
                }
            }
        }
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE,
                GCCoreUtil.getDimensionID(FMLClientHandler.instance().getClient().world),
                arrayOf<Any?>()
            )
        )
        ClientProxyCore.overworldTextureRequestSent = true
        DrawGameScreen.reusableMap = DynamicTexture(SIZE_STD2, SIZE_STD2)
        biomeColours.clear()
        setupColours()
    }

    /**
     * The BufferedImage needs to be already set up as a sized image of TYPE_INT_RGB
     */
    @JvmStatic
    fun getLocalMap(world: World, chunkXPos: Int, chunkZPos: Int, image: BufferedImage) {
        for (x0: Int in -12..12) {
            for (z0: Int in -12..12) {
                val chunk: Chunk? = world.getChunk(chunkXPos + x0, chunkZPos + z0)
                val pos: BlockPos? = null

                if (chunk != null) {
                    for (z: Int in 0..15) {
                        for (x: Int in 0..15) {
                            var l4: Int = chunk.getHeight(BlockPos(x, 0, z)) + 1
                            var state: IBlockState = Blocks.AIR.getDefaultState()

                            if (l4 > 1) {
                                do {
                                    --l4
                                    state = chunk.getBlockState(x, l4, z)
                                } while (state.getMapColor(world, pos) === MapColor.AIR && l4 > 0)
                            }

                            val col: Int = if (pos != null) state.getMapColor(world, pos).colorValue else 0
                            image.setRGB(x + (x0 + 12) * 16, z + (z0 + 12) * 16, col)
                        }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun makeOverworldTexture() {
        if (doneOverworldTexture) {
            return
        }
        val overworld: World? = (WorldUtil.getProviderForDimensionServer(ConfigManagerCore.idDimensionOverworld))!!.world
        if (overworld == null) {
            return
        }

        if (overworld.getWorldType() === WorldType.FLAT || overworld.provider !is WorldProviderSurface) {
            doneOverworldTexture = true
            return
        }

        val baseFolder: File = getBaseFolder()

        if (!baseFolder.exists() && !baseFolder.mkdirs()) {
            GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath())
            doneOverworldTexture = true
            return
        }

        if (getBiomeMapForCoords(
                overworld,
                0,
                0,
                OVERWORLD_TEXTURE_SCALE,
                OVERWORLD_TEXTURE_WIDTH,
                OVERWORLD_TEXTURE_HEIGHT,
                baseFolder
            )
        ) {
            doneOverworldTexture = true
        }

        //This will make the 'slow map', a map covering a large part of the world around spawn
        //(On a typical modern PC, this should take 20-30 minutes to generate in its own thread)
        getBiomeMapForCoords(
            overworld,
            0,
            0,
            OVERWORLD_MAP_SCALE,
            OVERWORLD_LARGEMAP_WIDTH,
            OVERWORLD_LARGEMAP_HEIGHT,
            baseFolder
        )
    }

    @JvmStatic
    fun sendOverworldToClient(client: EntityPlayerMP) {
        if (doneOverworldTexture) {
            try {
                val baseFolder: File = getBaseFolder()

                if (!baseFolder.exists()) {
                    GCLog.severe("Base folder missing: " + baseFolder.getAbsolutePath())
                    return
                }
                var file: File = File(baseFolder, "Overworld" + OVERWORLD_TEXTURE_WIDTH + ".bin")
                if (file.exists()) {
                    sendMapPacket(0, 0, client, FileUtils.readFileToByteArray(file))
                }
                file = File(baseFolder, "Overworld" + OVERWORLD_LARGEMAP_WIDTH + ".bin")
                if (file.exists()) {
                    sendMapPacket(LARGEMAP_MARKER, 0, client, FileUtils.readFileToByteArray(file))
                }
            } catch (ex: Exception) {
                System.err.println("Error sending overworld image to player.")
                ex.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun sendOrCreateMap(world: World, cx: Int, cz: Int, client: EntityPlayerMP) {
        if (world.getWorldType() === WorldType.FLAT || world.provider !is WorldProviderSurface) {
            doneOverworldTexture = true
            return
        }

        try {
            val baseFolder: File = getBaseFolder()

            if (!baseFolder.exists()) {
                GCLog.severe("Base folder missing: " + baseFolder.getAbsolutePath())
                return
            }
            val file: File = makeFileName(baseFolder, cx, cz)
            if (!file.exists()) {
                getBiomeMapForCoords(world, cx, cz, 1, SIZE_STD, SIZE_STD, baseFolder)
                return
            }
            sendMapPacket(cx, cz, client, FileUtils.readFileToByteArray(file))
        } catch (ex: Exception) {
            System.err.println("Error sending map image to player.")
            ex.printStackTrace()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun sendMapPacket(cx: Int, cz: Int, client: EntityPlayerMP, largeMap: ByteArray) {
        val compressed: ByteArray?
        if (cx == LARGEMAP_MARKER) {
            if (overworldImageCompressed == null) {
                overworldImageCompressed = zipCompress(largeMap)
            }
            compressed = overworldImageCompressed
        } else {
            compressed = zipCompress(largeMap)
        }
        sendMapPacketCompressed(cx, cz, client, compressed)
    }

    @JvmStatic
    fun sendMapPacketToAll(cx: Int, cz: Int, largeMap: ByteArray) {
        val compressed: ByteArray?
        if (cx == LARGEMAP_MARKER) {
            if (overworldImageCompressed == null) {
                overworldImageCompressed = zipCompress(largeMap)
            }
            compressed = overworldImageCompressed
        } else {
            compressed = zipCompress(largeMap)
        }
        sendMapPacketAllCompressed(cx, cz, compressed)
    }

    @JvmStatic
    @Throws(IOException::class)
    private fun sendMapPacketCompressed(cx: Int, cz: Int, client: EntityPlayerMP, map: ByteArray?) {
        if (cx == LARGEMAP_MARKER && map!!.size < 2080000) {
            val halfSize: Int = map.size / 2
            val largeMapPartA: ByteArray = map.copyOf(halfSize)
            val largeMapPartB: ByteArray = Arrays.copyOfRange(map, halfSize, map.size)
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE,
                    GCCoreUtil.getDimensionID(client.world),
                    arrayOf<Any>(cx, map.size, largeMapPartA)
                ), client
            )
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE,
                    GCCoreUtil.getDimensionID(client.world),
                    arrayOf<Any>(cx + 1, map.size, largeMapPartB)
                ), client
            )
        } else if (map!!.size < 1040000)  //That's about the limit on a Forge packet length
        {
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE,
                    GCCoreUtil.getDimensionID(client.world),
                    arrayOf<Any>(cx, cz, map)
                ), client
            )
        }
    }

    @JvmStatic
    private fun sendMapPacketAllCompressed(cx: Int, cz: Int, map: ByteArray?) {
        if (cx == LARGEMAP_MARKER && map!!.size < 2080000) {
            val halfSize: Int = map.size / 2
            val largeMapPartA: ByteArray = map.copyOf(halfSize)
            val largeMapPartB: ByteArray = Arrays.copyOfRange(map, halfSize, map.size)
            GCCoreUtil.sendToAllDimensions(
                PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE,
                arrayOf<Any>(cx, map.size, largeMapPartA)
            )
            GCCoreUtil.sendToAllDimensions(
                PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE,
                arrayOf<Any>(cx + 1, map.size, largeMapPartB)
            )
        } else if (map!!.size < 1040000)  //That's about the limit on a Forge packet length
        {
            GCCoreUtil.sendToAllDimensions(
                PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE,
                arrayOf<Any>(cx, cz, map)
            )
        }
    }

    /**
     * On a server, build any needed patchwork map files around co-ordinates (x, z)
     * The needed files may already have been generated by previous calls on the same server
     * Files are stored in the world save folder, subfolder galacticraft/overworldMap
     */
    @JvmStatic
    fun buildMaps(world: World, x: Int, z: Int): Boolean {
        if (world.getWorldType() === WorldType.FLAT || world.provider !is WorldProviderSurface) {
            return false
        }

        val baseFolder: File = getBaseFolder()

        if (!baseFolder.exists() && !baseFolder.mkdirs()) {
            GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath())
            return false
        }
        val cx: Int = convertMap(x)
        val cz: Int = convertMap(z)
        getBiomeMapForCoords(world, cx, cz, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx + SIZE_STD2, cz, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx, cz + SIZE_STD2, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx - SIZE_STD2, cz, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx, cz - SIZE_STD2, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx + SIZE_STD2, cz + SIZE_STD2, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx - SIZE_STD2, cz + SIZE_STD2, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx - SIZE_STD2, cz - SIZE_STD2, 1, SIZE_STD, SIZE_STD, baseFolder)
        getBiomeMapForCoords(world, cx + SIZE_STD2, cz - SIZE_STD2, 1, SIZE_STD, SIZE_STD, baseFolder)
        return true
    }

    @JvmStatic
    private fun getBaseFolder(): File {
        return File(DimensionManager.getCurrentSaveRootDirectory(), "galacticraft/overworldMap")
    }

    @JvmStatic
    private fun convertMap(x: Int): Int {
        var cx: Int = x + SIZE_STD
        if (cx < 0) {
            cx -= SIZE_STD2 - 1
        }
        cx /= SIZE_STD2
        return cx * SIZE_STD2
    }

    @JvmStatic
    fun getBiomeMapForCoords(
        world: World?,
        cx: Int,
        cz: Int,
        scale: Int,
        sizeX: Int,
        sizeZ: Int,
        baseFolder: File?
    ): Boolean {
        val outputFile: File
        if (sizeX != sizeZ) {
            outputFile = File(baseFolder, "Overworld" + sizeX + ".bin")
            if (sizeX == OVERWORLD_LARGEMAP_WIDTH) {
                val newGen: MapGen = MapGen((world)!!, sizeX, sizeZ, cx, cz, 1 shl scale, outputFile)
                if (newGen.mapNeedsCalculating) {
                    slowMap = newGen
                    calculatingMap.set(true)
                }
                return false
            }
        } else {
            outputFile = makeFileName(baseFolder, cx, cz)
        }

        val newGen: MapGen = MapGen((world)!!, sizeX, sizeZ, cx, cz, 1 shl scale, outputFile)
        if (newGen.mapNeedsCalculating) {
            if (calculatingMap.getAndSet(true)) {
                queuedMaps.add(newGen)
            } else {
                currentMap = newGen
            }
            return false
        }
        return true
    }

    @JvmStatic
    fun saveMapProgress() {
        if (slowMap != null) {
            slowMap!!.abort()
            try {
                Thread.sleep(90)
            } catch (e: InterruptedException) {
            }
            slowMap!!.writeOutputFile(false)
            slowMap = null
        }
    }

    /**
     * Poll any map threads to see if they need starting or if they're finished
     *
     * Multi-threaded version - runs each MapGen in its own thread, polls MapGen.finishedCalculatingMap to know when finished
     */
    @JvmStatic
    fun BiomeMapNextTick_MultiThreaded() {
        if (currentMap != null) {
            if (threadCurrentMap == null) {
                //Create the current map thread, pausing any slow map thread 
                if (slowMap != null) {
                    slowMap!!.pause()
                }
                //TODO = should it use a re-usable thread pool?
                threadCurrentMap = Thread(currentMap)
                threadCurrentMap!!.setName("Background world mapping")
                threadCurrentMap!!.setPriority(Thread.NORM_PRIORITY - 1)
                threadCurrentMap!!.start()
            } else if ((currentMap!!.finishedCalculating)!!.get()) {
                //Finished the current map
                threadCurrentMap = null
                currentMap!!.writeOutputFile(true)

                if (queuedMaps.size > 0) {
                    currentMap = queuedMaps.removeFirst()
                } else {
                    currentMap = null

                    if (slowMap == null) calculatingMap.set(false)
                    else {
                        if (slowMap != null) {
                            slowMap!!.resume()
                        }
                    }
                }
            }

            return
        }

        if (!queuedMaps.isEmpty()) {
            if (slowMap != null) {
                slowMap!!.pause()
            }

            currentMap = queuedMaps.removeFirst()
            return
        }

        if (slowMap != null) {
            if (threadSlowMap == null) {
                //Create the slow map thread 
                threadSlowMap = Thread(slowMap)
                threadSlowMap!!.setName("Background world mapping")
                threadSlowMap!!.setPriority(Thread.NORM_PRIORITY - 1)
                threadSlowMap!!.start()
            } else if ((slowMap!!.finishedCalculating)!!.get()) {
                //Finished the current map
                threadSlowMap = null
                slowMap!!.writeOutputFile(true)
                slowMap = null
                calculatingMap.set(false)
            }

            return
        }
    }

    //Single Threaded Version of the same code
    //(Currently unused)
    @JvmStatic
    fun BiomeMapNextTick_SingleThreaded() {
        val map: MapGen
        var doingSlow: Boolean = false
        if (currentMap != null) {
            map = currentMap!!
        } else if (slowMap != null) {
            map = slowMap!!
            doingSlow = true
        } else {
            return
        }

        //If single threade, allow GC background mapping around 9% of the server tick time if server running at full speed
        //(on a slow server, it will be proportionately lower %)
        val end: Long = System.nanoTime() + 4500000L
        while (System.nanoTime() < end) {
            if (map.BiomeMapOneTick()) {
                //Finished
                map.writeOutputFile(true)
                if (doingSlow) {
                    slowMap = null
                } else {
                    currentMap = null
                    if (queuedMaps.size > 0) {
                        currentMap = queuedMaps.removeFirst()
                    }
                }
                if (currentMap == null && slowMap == null) {
                    calculatingMap.set(false)
                }
                return
            }
        }
    }


    @JvmStatic
    fun backgroundMapping(currentThread: Thread?): Boolean {
        return currentThread === threadSlowMap || currentThread === threadCurrentMap
    }


    /**
     * Converts a 48px high image to a 12px high image with a palette chosen only from the colours in the paletteImage
     *
     * @param overworldImage Output image already created as a blank image, dimensions biomeMapSizeX x biomeMapSizeY
     * @param paletteImage   Palette image, dimensions must be a square with sides biomeMapSizeZ / 4
     */
    @JvmStatic
    fun convertTo12pxTexture(overworldImage: BufferedImage, paletteImage: BufferedImage): BufferedImage {
        val result: BufferedImage =
            BufferedImage(overworldImage.getWidth(), overworldImage.getHeight(), BufferedImage.TYPE_INT_RGB)
        val mapColPos: TreeMap<Int,Int> = TreeMap<Int,Int>()
        val mapColPosB: TreeMap<Int,Int> = TreeMap<Int,Int>()
        var count: Int = 0
        run {
            var x: Int = 0
            while (x < overworldImage.getWidth()) {
                var z: Int = 0
                while (z < overworldImage.getHeight()) {
                    var r: Int = 0
                    var g: Int = 0
                    var b: Int = 0
                    for (xx: Int in 0..3) {
                        for (zz: Int in 0..3) {
                            val col: Int = overworldImage.getRGB(xx + x, zz + z)
                            r += (col shr 16)
                            g += (col shr 8) and 255
                            b += col and 255
                        }
                    }
                    while (mapColPos.containsKey(g - b)) {
                        g++
                    }
                    mapColPos.put(g - b, count)
                    if (x < overworldImage.getHeight()) {
                        val col: Int = paletteImage.getRGB(x + 1, z + 1)
                        r = (col shr 16)
                        g = (col shr 8) and 255
                        b = col and 255
                        while (mapColPosB.containsKey(g - b)) {
                            g++
                        }
                        mapColPosB.put(g - b, col)
                    }
                    count++
                    z += 4
                }
                x += 4
            }
        }

        count = 0
        var newCol: Int = 0
        val it: MutableIterator<Int> = mapColPosB.keys.iterator()
        val itt: MutableIterator<Int> = mapColPos.keys.iterator()
        val modulus: Int = overworldImage.getHeight() / 4
        val mod2: Int = overworldImage.getWidth() / overworldImage.getHeight()
        for (x: Int in 0..<overworldImage.getWidth() / 4) {
            for (z: Int in 0..<modulus) {
                if (count % mod2 == 0) {
                    newCol = mapColPosB.get(it.next())!!
                }
                val position: Int = mapColPos.get(itt.next())!!
                val xx: Int = position / modulus
                val zz: Int = position % modulus
                for (xxx: Int in 0..3) {
                    for (zzz: Int in 0..3) {
                        result.setRGB(xx * 4 + xxx, zz * 4 + zzz, newCol)
                    }
                }
                count++
            }
        }

        return result
    }

    //Unused
    @JvmStatic
    @Throws(IOException::class)
    fun readImage(source: Any): BufferedImage? {
        val stream: ImageInputStream = ImageIO.createImageInputStream(source)
        val reader: ImageReader = ImageIO.getImageReaders(stream).next()
        reader.setInput(stream)
        val param: ImageReadParam = reader.getDefaultReadParam()

        var typeToUse: ImageTypeSpecifier? = null
        val i: MutableIterator<*> = reader.getImageTypes(0)
        while (i.hasNext()) {
            val type: ImageTypeSpecifier = i.next() as ImageTypeSpecifier
            if (type.getColorModel().getColorSpace().isCS_sRGB()) {
                typeToUse = type
            }
        }
        if (typeToUse != null) {
            param.setDestinationType(typeToUse)
        }

        val b: BufferedImage? = reader.read(0, param)
        reader.dispose()
        stream.close()
        return b
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun writeImgToFile(img: BufferedImage, name: String) {
        if (GalacticraftCore.enableJPEG) {
            val folder: File = File(FMLClientHandler.instance().getClient().gameDir, "assets/galacticraftMaps")

            try {
                val outputStreamA: ImageOutputStream = FileImageOutputStream(File(folder, name))
                (GalacticraftCore.jpgWriter)!!.setOutput(outputStreamA)
                (GalacticraftCore.jpgWriter)!!.write(null, IIOImage(img, null, null), GalacticraftCore.writeParam)
                outputStreamA.close()
            } catch (e: Exception) {
            }
        }
    }

    @JvmStatic
    fun zipCompress(data: ByteArray): ByteArray? {
        val deflater: Deflater = Deflater()
        deflater.setLevel(Deflater.BEST_SPEED)
        deflater.setInput(data)
        deflater.finish()
        val compressed: ByteArrayOutputStream = ByteArrayOutputStream(data.size * 2 / 3)
        val miniBuffer: ByteArray = ByteArray(4096)
        while (!deflater.finished()) {
            val count: Int = deflater.deflate(miniBuffer)
            compressed.write(miniBuffer, 0, count)
        }
        return compressed.toByteArray()
    }

    @JvmStatic
    @Throws(DataFormatException::class)
    private fun zipDeCompress(data: ByteArray): ByteArray {
        val inflater: Inflater = Inflater()
        inflater.setInput(data)
        val deCompressed: ByteArrayOutputStream = ByteArrayOutputStream(data.size * 2)
        val miniBuffer: ByteArray = ByteArray(4096)
        while (!inflater.finished()) {
            val count: Int = inflater.inflate(miniBuffer)
            deCompressed.write(miniBuffer, 0, count)
        }
        return deCompressed.toByteArray()
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    @Throws(IOException::class)
    fun receiveOverworldImageCompressed(cx: Int, cz: Int, raw: ByteArray) {
        var raw: ByteArray = raw
        if (cx == LARGEMAP_MARKER) {
            //Received large map part A
            if (overworldImageBytesPart == null) {
                overworldImageBytesPart = raw
                return
            } else {
                val overWorldImageComplete: ByteArray = raw.copyOf(cz)
                val offsetPartB: Int = cz / 2
                for (i: Int in offsetPartB..<cz) overWorldImageComplete[i] = overworldImageBytesPart!![i - offsetPartB]
                overworldImageBytesPart = null
                raw = overWorldImageComplete
            }
        } else if (cx == LARGEMAP_MARKER + 1) {
            //Received large map part B
            if (overworldImageBytesPart == null) {
                overworldImageBytesPart = raw
                return
            } else {
                val overWorldImageComplete: ByteArray = overworldImageBytesPart!!.copyOf(cz)
                val offsetPartB: Int = cz / 2
                for (i: Int in offsetPartB..<cz) overWorldImageComplete[i] = raw[i - offsetPartB]
                overworldImageBytesPart = null
                raw = overWorldImageComplete
            }
        }

        try {
            getOverworldImageFromRaw(cx, cz, zipDeCompress(raw))
        } catch (e: DataFormatException) {
            GCLog.debug(e.toString())
            GCLog.debug("Client received a corrupted map image data packet from server " + cx + "_" + cz)
        }
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    @Throws(IOException::class)
    fun getOverworldImageFromRaw(cx: Int, cz: Int, raw: ByteArray) {
        val folder: File? = getClientMapsFolder()

        if (raw.size == OVERWORLD_LARGEMAP_WIDTH * OVERWORLD_LARGEMAP_HEIGHT * 2) {
            if (folder != null) {
                val file0: File = File(folder, "overworldRaw.bin")

                if (!file0.exists() || (file0.canRead() && file0.canWrite())) {
                    FileUtils.writeByteArrayToFile(file0, raw)
                } else {
                    System.err.println("Cannot write to file %minecraft%/assets/galacticraftMaps/overworldRaw.bin")
                }
            } else {
                System.err.println("No folder for file %minecraft%/assets/galacticraftMaps/overworldRaw.bin")
            }

            //raw is a WIDTH_WORLD x HEIGHT_WORLD array of 2 byte entries: biome type followed by height
            //Here we will make a texture from that, but twice as large: 4 pixels for each data point, it just looks better that way when the texture is used
            val worldImageLarge: BufferedImage =
                BufferedImage(OVERWORLD_LARGEMAP_WIDTH * 2, OVERWORLD_LARGEMAP_HEIGHT * 2, BufferedImage.TYPE_INT_RGB)
            val cols: ArrayList<Int> = ArrayList<Int>()
            val lastcol: Int = -1
            val idx: Int = 0
            for (x: Int in 0..<OVERWORLD_LARGEMAP_WIDTH) {
                for (z: Int in 0..<OVERWORLD_LARGEMAP_HEIGHT) {
                    val arrayIndex: Int = (x * OVERWORLD_LARGEMAP_HEIGHT + z) * 2
                    var biome: Int = (raw[arrayIndex].toInt()) and 255
                    val height: Int = (raw[arrayIndex + 1].toInt()) and 255

                    if (height < OCEAN_HEIGHT && biome != 2 && biome != 10) {
                        //Includes ponds, lakes and rivers in other biomes
                        biome = 0
                    }
                    if (height < DEEP_OCEAN && biome == 0) {
                        biome = 24
                    }

                    worldImageLarge.setRGB(x * 2, z * 2, convertBiomeColour(biome, height))
                    worldImageLarge.setRGB(x * 2, z * 2 + 1, convertBiomeColour(biome, height))
                    worldImageLarge.setRGB(x * 2 + 1, z * 2, convertBiomeColour(biome, height))
                    worldImageLarge.setRGB(x * 2 + 1, z * 2 + 1, convertBiomeColour(biome, height))
                }
            }

            //overworldTextureLarge is currently unused in beta
//            if (ClientProxyCore.overworldTextureLarge == null)
//            {
//                ClientProxyCore.overworldTextureLarge = new DynamicTextureProper(WIDTH_WORLD * 2, HEIGHT_WORLD * 2);
//            }
//            ClientProxyCore.overworldTextureLarge.update(worldImageLarge);

            //Write it to a .jpg file on client for beta preview 
            if (GalacticraftCore.enableJPEG && folder != null) {
                val outputStream: ImageOutputStream = FileImageOutputStream(File(folder, "large.jpg"))
                (GalacticraftCore.jpgWriter)!!.setOutput(outputStream)
                (GalacticraftCore.jpgWriter)!!.write(
                    null,
                    IIOImage(worldImageLarge, null, null),
                    GalacticraftCore.writeParam
                )
                outputStream.close()
            }
        } else if (raw.size == OVERWORLD_TEXTURE_WIDTH * OVERWORLD_TEXTURE_HEIGHT * 2) {
            //raw is a WIDTH_STD x HEIGHT_STD array of 2 byte entries: biome type followed by height
            val worldImage: BufferedImage =
                BufferedImage(OVERWORLD_TEXTURE_WIDTH, OVERWORLD_TEXTURE_HEIGHT, BufferedImage.TYPE_INT_RGB)
            val cols: ArrayList<Int> = ArrayList<Int>()
            val lastcol: Int = -1
            val idx: Int = 0
            for (x: Int in 0..<OVERWORLD_TEXTURE_WIDTH) {
                for (z: Int in 0..<OVERWORLD_TEXTURE_HEIGHT) {
                    val arrayIndex: Int = (x * OVERWORLD_TEXTURE_HEIGHT + z) * 2
                    var biome: Int = (raw[arrayIndex].toInt()) and 255
                    val height: Int = (raw[arrayIndex + 1].toInt()) and 255

                    if (height < OCEAN_HEIGHT && biome != 2 && biome != 10) {
                        //Includes ponds, lakes and rivers in other biomes
                        biome = 0
                    }
                    if (height < DEEP_OCEAN && biome == 0) {
                        biome = 24
                    }

                    worldImage.setRGB(x, z, convertBiomeColour(biome, height))
                }
            }

            val rm: IResourceManager = Minecraft.getMinecraft().getResourceManager()
            var paletteImage: BufferedImage? = null
            try {
                val `in`: InputStream =
                    rm.getResource(ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png"))
                        .getInputStream()
                paletteImage = ImageIO.read(`in`)
                `in`.close()
                paletteImage.getHeight()
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }

            val result: BufferedImage = convertTo12pxTexture(worldImage, paletteImage)

            if (result != null) {
                if (ClientProxyCore.overworldTextureWide == null) {
                    ClientProxyCore.overworldTextureWide =
                        DynamicTextureProper(OVERWORLD_TEXTURE_WIDTH, OVERWORLD_TEXTURE_HEIGHT)
                }
                if (ClientProxyCore.overworldTextureClient == null) {
                    ClientProxyCore.overworldTextureClient =
                        DynamicTextureProper(OVERWORLD_TEXTURE_HEIGHT, OVERWORLD_TEXTURE_HEIGHT)
                }
                ((ClientProxyCore.overworldTextureWide)!!.update(result))!!
                ((ClientProxyCore.overworldTextureClient)!!.update(result))!!
                ClientProxyCore.overworldTexturesValid = true
            }
        } else if (folder != null) {
            val file0: File = makeFileName(folder, cx, cz)

            if (!file0.exists() || (file0.canRead() && file0.canWrite())) {
                FileUtils.writeByteArrayToFile(file0, raw)
            }
        } else {
            System.err.println("No folder %minecraft%/assets/galacticraftMaps for local map file.")
        }
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getMap(image: IntArray, world: World, pos: BlockPos): Boolean {
        val xCoord: Int = pos.getX()
        val zCoord: Int = pos.getZ()
        val cx: Int = convertMap(xCoord)
        val cz: Int = convertMap(zCoord)

        val baseFolder: File = File(FMLClientHandler.instance().getClient().gameDir, "assets/galacticraftMaps")
        if (!baseFolder.exists() && !baseFolder.mkdirs()) {
            GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath())
            return false
        }

        val dim: Int = GCCoreUtil.getDimensionID(world)
        var result: Boolean = true
        if (makeRGBimage(image, baseFolder, cx - SIZE_STD2, cz - SIZE_STD2, 0, 0, xCoord, zCoord, dim, result)) {
            result = false
        }
        if (makeRGBimage(image, baseFolder, cx - SIZE_STD2, cz, 0, SIZE_STD, xCoord, zCoord, dim, result)) {
            result = false
        }
        if (makeRGBimage(
                image,
                baseFolder,
                cx - SIZE_STD2,
                cz + SIZE_STD2,
                0,
                SIZE_STD2,
                xCoord,
                zCoord,
                dim,
                result
            )
        ) {
            result = false
        }
        if (makeRGBimage(image, baseFolder, cx, cz - SIZE_STD2, SIZE_STD, 0, xCoord, zCoord, dim, result)) {
            result = false
        }
        if (makeRGBimage(image, baseFolder, cx, cz, SIZE_STD, SIZE_STD, xCoord, zCoord, dim, result)) {
            result = false
        }
        if (makeRGBimage(image, baseFolder, cx, cz + SIZE_STD2, SIZE_STD, SIZE_STD2, xCoord, zCoord, dim, result)) {
            result = false
        }
        if (makeRGBimage(
                image,
                baseFolder,
                cx + SIZE_STD2,
                cz - SIZE_STD2,
                SIZE_STD2,
                0,
                xCoord,
                zCoord,
                dim,
                result
            )
        ) {
            result = false
        }
        if (makeRGBimage(image, baseFolder, cx + SIZE_STD2, cz, SIZE_STD2, SIZE_STD, xCoord, zCoord, dim, result)) {
            result = false
        }
        if (makeRGBimage(
                image,
                baseFolder,
                cx + SIZE_STD2,
                cz + SIZE_STD2,
                SIZE_STD2,
                SIZE_STD2,
                xCoord,
                zCoord,
                dim,
                result
            )
        ) {
            result = false
        }
        return result
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    private fun makeRGBimage(
        array: IntArray,
        baseFolder: File,
        cx: Int,
        cz: Int,
        offsetX: Int,
        offsetZ: Int,
        xCoord: Int,
        zCoord: Int,
        dim: Int,
        prevResult: Boolean
    ): Boolean {
        val filename: File = makeFileName(baseFolder, cx, cz)
        if (!filename.exists()) {
            if (clientRequests.contains(filename.getName())) {
                //GCLog.debug("Info: Server not yet ready to send map file " + baseFolder.getName() + "/" + filename.getName());
            } else {
                clientRequests.add(filename.getName())
                //GCLog.debug("Info: Client requested map file" + filename.getName());
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_REQUEST_MAP_IMAGE,
                        GCCoreUtil.getDimensionID(FMLClientHandler.instance().getClient().world),
                        arrayOf<Any>(dim, cx, cz)
                    )
                )
            }
            return true
        }

        if (!prevResult) {
            return true
        }

        val ox: Int = (convertMap(xCoord) - xCoord - SIZE_STD) / 2
        val oz: Int = (convertMap(zCoord) - zCoord - SIZE_STD) / 2

        var raw: ByteArray? = null
        try {
            raw = FileUtils.readFileToByteArray(filename)
        } catch (e: IOException) {
            GCLog.severe("Problem reading map file: " + baseFolder.getAbsolutePath() + filename.getName())
            return true
        }
        if (raw == null || raw.size != SIZE_STD * SIZE_STD * 2) {
            GCLog.debug("Warning: unexpected map size is " + raw.size + " for file " + filename.toString())
            return true
        }

        val xstart: Int = max(0, -offsetX - ox)
        val zstart: Int = max(0, -offsetZ - oz)
        for (x: Int in xstart..<SIZE_STD) {
            val imagex: Int = x + offsetX + ox
            if (imagex >= SIZE_STD2) {
                break
            }
            for (z: Int in zstart..<SIZE_STD) {
                val imageZ: Int = z + oz + offsetZ // + SIZE_STD - 1 - z;
                if (imageZ >= SIZE_STD2) {
                    break
                }

                val arrayIndex: Int = (x * SIZE_STD + z) * 2
                var biome: Int = (raw[arrayIndex].toInt()) and 255
                val height: Int = (raw[arrayIndex + 1].toInt()) and 255

                if (height < OCEAN_HEIGHT && biome != 2 && biome != 10) {
                    //Includes ponds, lakes and rivers in other biomes
                    biome = 0
                }
                if (height < DEEP_OCEAN && biome == 0) {
                    biome = 24
                }

                if (imagex < 0 || imageZ < 0) {
                    GCLog.debug("Outside image " + imagex + "," + imageZ + " - " + "x=" + x + " z=" + z + " offsetX=" + offsetX + " offsetZ = " + offsetZ + " ox=" + ox + " oz=" + oz)
                } else {
                    array[imagex + SIZE_STD2 * imageZ] = convertBiomeColour(biome, height) + -0x1000000
                }
            }
        }
        return false
    }


    @JvmStatic
    private fun makeFileName(folder: File?, cx: Int, cz: Int): File {
        return File(folder, "overworld" + cx / SIZE_STD2 + "_" + cz / SIZE_STD2 + ".bin")
    }

    @JvmStatic
    private fun getBiomeBaseColour(biomeId: Int): Int {
        val biomegenbase: Biome? = Biome.REGISTRY.getObjectById(biomeId)
        //    	return biomegenbase == null ? Biomes.OCEAN.color : biomegenbase.color; TODO Fix vanilla biome colors
        return 0
    }

    @JvmStatic
    fun convertBiomeColour(`in`: Int, height: Int): Int {
        var `in`: Int = `in`
        var rv: Int
        val s: Int = biomeColours.size
        if (`in` >= 128 && `in` < 128 + s) {
            `in` -= 128
        }
        if (`in` >= s) {
            rv = getBiomeBaseColour(`in`)
        } else {
            val bv: BlockVec3? = biomeColours.get(`in`)
            if (bv == null) {
                rv = getBiomeBaseColour(`in`)
            } else {
                if (bv.z > 0 && rand.nextInt(100) < bv.z) {
                    rv = bv.y
                } else {
                    rv = bv.x
                }
            }
        }
        if (rv == 0x9c2424 && rand.nextInt(2) == 0) {
            rv = 0xbfa384
        }
        if (height < OCEAN_HEIGHT) {
            return rv
        }
        if (height > 92 && (`in` == 3 || `in` == 20 || `in` == 31 || `in` == 33 || `in` == 34)) {
            if (rand.nextInt(8) > 98 - height) {
                rv = Material.SNOW.getMaterialMapColor().colorValue
            }
        }
        val factor: Float = (height - 68f) / 94f
        return ColorUtil.lighten(rv, factor)
    }

    @JvmStatic
    private fun setupColours() {
        //ocean = Ocean(0) colour(112) "Ocean"
        biomeColours.add(BlockVec3(Material.WATER.getMaterialMapColor().colorValue, 0, 0))
        //plains = Plains(1) colour(9286496) "Plains"
        biomeColours.add(BlockVec3(0x497436, 0, 0))
        //desert = Desert(2) colour(16421912) "Desert"
        biomeColours.add(BlockVec3(0xd4cd98, Material.CACTUS.getMaterialMapColor().colorValue, 3))
        //extremeHills = Hills(3, false) colour(6316128) "Extreme Hills"
        biomeColours.add(BlockVec3(0x4d654c, Material.ROCK.getMaterialMapColor().colorValue, 15))
        //forest = Forest(4, 0) colour(353825) "Forest"
        biomeColours.add(BlockVec3(0x3c7521, 0x295416, 45))
        //taiga = Taiga(5, 0) colour(747097) "Taiga"
        biomeColours.add(BlockVec3(0x627e61, 0x172a17, 18))
        //swampland = Swamp(6) colour(522674) "Swampland"
        biomeColours.add(BlockVec3(0x43541b, 0x111309, 25))
        //river = River(7) colour(255) "River"
        biomeColours.add(BlockVec3(0x497436, 0, 0))
        biomeColours.add(BlockVec3(0, 0, 0))
        biomeColours.add(BlockVec3(0, 0, 0))
        //frozenOcean = Ocean(10) colour(9474208) "FrozenOcean"
        biomeColours.add(BlockVec3(Material.ICE.getMaterialMapColor().colorValue, 0, 0))
        //frozenRiver = River(11) colour(10526975) "FrozenRiver"
        biomeColours.add(BlockVec3(Material.ICE.getMaterialMapColor().colorValue, 0, 0))
        //icePlains = Snow(12, false) colour(16777215) "Ice Plains"
        biomeColours.add(BlockVec3(Material.SNOW.getMaterialMapColor().colorValue, 0x497436, 3))
        //iceMountains = Snow(13, false) colour(10526880) "Ice Mountains"
        biomeColours.add(
            BlockVec3(
                Material.SNOW.getMaterialMapColor().colorValue,
                Material.ICE.getMaterialMapColor().colorValue,
                5
            )
        )
        //mushroomIsland = MushroomIsland(14) colour(16711935) "MushroomIsland"
        biomeColours.add(BlockVec3(0x63565f, 0x7c1414, 10))
        //mushroomIslandShore = MushroomIsland(15) colour(10486015) "MushroomIslandShore"
        biomeColours.add(BlockVec3(0x6a6066, 0, 0))
        //beach = Beach(16) colour(16440917) "Beach"
        biomeColours.add(BlockVec3(Material.SAND.getMaterialMapColor().colorValue, 0, 0))
        //desertHills = Desert(17) colour(13786898) "DesertHills"
        biomeColours.add(BlockVec3(0xd4cd98, 0, 0))
        //forestHills = Forest(18, 0) colour(2250012) "ForestHills"
        biomeColours.add(BlockVec3(0x3c7521, 0x295416, 35))
        //taigaHills = Taiga(19, 0) colour(1456435) "TaigaHills"
        biomeColours.add(BlockVec3(0x627e61, 0x172a17, 14))
        //extremeHillsEdge = Hills(20, true) colour(7501978) "Extreme Hills Edge"
        biomeColours.add(BlockVec3(0x4d654c, 0x497436, 50))
        //jungle = Jungle(21, false) colour(5470985) "Jungle"
        biomeColours.add(BlockVec3(0x176c03, 0x0f4502, 25))
        //jungleHills = Jungle(22, false) colour(2900485) "JungleHills"
        biomeColours.add(BlockVec3(0x176c03, 0x0f4502, 25))
        //jungleEdge = Jungle(23, true) colour(6458135) "JungleEdge"
        biomeColours.add(BlockVec3(0x176c03, 0x0f4502, 25))
        //deepOcean = Ocean(24) colour(48) "Deep Ocean"
        biomeColours.add(BlockVec3(0x2f2fd4, 0, 0))
        //stoneBeach = StoneBeach(25) colour(10658436) "Stone Beach"
        biomeColours.add(BlockVec3(Material.ROCK.getMaterialMapColor().colorValue, 0, 0))
        //coldBeach = Beach(26) colour(16445632) "Cold Beach"
        biomeColours.add(
            BlockVec3(
                Material.SAND.getMaterialMapColor().colorValue,
                Material.SNOW.getMaterialMapColor().colorValue,
                75
            )
        )
        //birchForest = Forest(27, 2)) colour(3175492) "Birch Forest"
        biomeColours.add(BlockVec3(0x516b36, 0x497436, 65))
        //birchForestHills = Forest(28, 2)) colour(2055986) "Birch Forest Hills"
        biomeColours.add(BlockVec3(0x516b36, 0x497436, 55))
        //roofedForest = Forest(29, 3) colour(4215066) "Roofed Forest"
        biomeColours.add(BlockVec3(0x9c2424, 0x1e2e18, 98))
        //coldTaiga = Taiga(30, 0) colour(3233098) "Cold Taiga"
        biomeColours.add(BlockVec3(Material.SNOW.getMaterialMapColor().colorValue, 0x172a17, 12))
        //coldTaigaHills = Taiga(31, 0) colour(2375478) "Cold Taiga Hills"
        biomeColours.add(BlockVec3(Material.SNOW.getMaterialMapColor().colorValue, 0x172a17, 12))
        //megaTaiga = Taiga(32, 1) colour(5858897) "Mega Taiga"
        biomeColours.add(BlockVec3(0x172a17, 0x6e4e35, 12))
        //megaTaigaHills = Taiga(33, 1) colour(4542270) "Mega Taiga Hills"
        biomeColours.add(BlockVec3(0x172a17, 0x6e4e35, 12))
        //extremeHillsPlus = Hills(34, true) colour(5271632) "Extreme Hills+"
        biomeColours.add(BlockVec3(0x7b7978, 0x497436, 10))
        //savanna = Savanna(35) colour(12431967) "Savanna"
        biomeColours.add(BlockVec3(0x565529, 0x262304, 20))
        //savannaPlateau = Savanna(36) colour(10984804) "Savanna Plateau"
        biomeColours.add(BlockVec3(0x565529, 0x262304, 14))
        //mesa = Mesa(37, false, false) colour(14238997) "Mesa"
        biomeColours.add(BlockVec3(0xa0521f, 0x712f23, 14))
        //mesaPlateau_F = Mesa(38, false, true) colour(11573093) "Mesa Plateau F"
        biomeColours.add(BlockVec3(0xa0521f, 0x712f23, 17))
        //mesaPlateau = Mesa(39, false, false) colour(13274213) "Mesa Plateau"
        biomeColours.add(BlockVec3(0xa0521f, 0x712f23, 20))
    }

    @JvmStatic
    fun makeVanillaMap(dim: Int, chunkXPos: Int, chunkZPos: Int, baseFolder: File?, image: BufferedImage) {
        val world: World? = WorldUtil.getWorldForDimensionServer(dim)
        for (x0: Int in -12..12) {
            for (z0: Int in -12..12) {
                val chunk: Chunk? = (world)!!.getChunk(chunkXPos + x0, chunkZPos + z0)

                if (chunk != null) {
                    for (z: Int in 0..15) {
                        for (x: Int in 0..15) {
                            var l4: Int = chunk.getHeightValue(x, z) + 1
                            var state: IBlockState = Blocks.AIR.getDefaultState()
                            var pos: BlockPos? = null

                            if (l4 > 1) {
                                do {
                                    --l4
                                    pos = BlockPos(x, l4, z)
                                    state = chunk.getBlockState(pos)
                                } while (state.getMapColor(world, pos) === MapColor.AIR && l4 > 0)
                            }

                            val col: Int = if (pos != null) state.getMapColor(world, pos).colorValue else 0
                            image.setRGB(x + (x0 + 12) * 16, z + (z0 + 12) * 16, col)
                        }
                    }
                }
            }
        }

        try {
            val outputFile: File = File(baseFolder, dim.toString() + "_" + chunkXPos + "_" + chunkZPos + ".jpg")

            if (!outputFile.exists() || (outputFile.canWrite() && outputFile.canRead())) {
                ImageIO.write(image, "jpg", outputFile)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getClientMapsFolder(): File? {
        val folder: File = File(FMLClientHandler.instance().getClient().gameDir, "assets/galacticraftMaps")
        try {
            if (folder.exists() || folder.mkdirs()) {
                return folder
            } else {
                System.err.println("Cannot create directory %minecraft%/assets/galacticraftMaps! : " + folder.toString())
            }
        } catch (e: Exception) {
            System.err.println(folder.toString())
            e.printStackTrace()
        }
        return null
    }
}
