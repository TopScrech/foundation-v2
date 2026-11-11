package micdoodle8.mods.galacticraft.core.util

import foundation.inventory.offhandItems
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import java.io.IOException
import net.minecraft.launchwrapper.Launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.io.FileNotFoundException
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.inventory.ContainerBuggy
import micdoodle8.mods.galacticraft.core.inventory.ContainerParaChest
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import net.minecraft.world.level.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.resources.Language
import net.minecraft.world.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.Container as IInventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.text.translation.LanguageMap
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.registries.GameData
import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedList
import java.util.Locale
import java.util.Random
import java.util.zip.ZipFile

object GCCoreUtil {
    @JvmField
    var nextID: Int = 0
    private var deobfuscated = false
    private const val lastLang = ""

    @JvmField
    var langDisable: Boolean = false
    private var serverCached: MinecraftServer? = null

    init {
        try {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun isDeobfuscated(): Boolean {
        return deobfuscated
    }

    @JvmStatic
    fun openBuggyInv(player: EntityPlayerMP, buggyInv: IInventory, type: Int) {
        player.getNextWindowId()
        player.closeContainer()
        val id: Int = player.currentWindowId
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_OPEN_PARACHEST_GUI,
                getDimensionID(player.level()),
                arrayOf<Any>(id, 0, 0)
            ), player
        )
        player.openContainer = ContainerBuggy(player.inventory, buggyInv, type, player)
        player.openContainer.windowId = id
        player.openContainer.addListener(player)
    }

    @JvmStatic
    fun openParachestInv(player: EntityPlayerMP, landerInv: EntityLanderBase) {
        player.getNextWindowId()
        player.closeContainer()
        val windowId: Int = player.currentWindowId
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_OPEN_PARACHEST_GUI,
                getDimensionID(player.level()),
                arrayOf<Any>(windowId, 1, landerInv.getEntityId())
            ), player
        )
        player.openContainer = ContainerParaChest(player.inventory, landerInv, player)
        player.openContainer.windowId = windowId
        player.openContainer.addListener(player)
    }

    @JvmStatic
    fun nextInternalID(): Int {
        nextID++
        return nextID - 1
    }

    @JvmStatic
    fun registerGalacticraftCreature(clazz: Class<out Entity>?, name: String, back: Int, fore: Int) {
        registerGalacticraftNonMobEntity(clazz, name, 80, 3, true)
        val nextEggID: Int = getNextValidID()
        if (nextEggID < 65536) {
            val resourcelocation: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_CORE, name)
            //            name = Constants.MOD_ID_CORE + "." + name;
            EntityList.ENTITY_EGGS.put(resourcelocation, EntityList.EntityEggInfo(resourcelocation, back, fore))
        }
    }

    @JvmStatic
    fun getNextValidID(): Int {
        var eggID: Int = 255

        //Non-global entity IDs - for egg ID purposes - can be greater than 255
        //The spawn egg will have this metadata.  Metadata up to 65535 is acceptable (see potions).
        do {
            eggID++
        } while (GameData.getEntityRegistry().getValue(eggID) != null)

        return eggID
    }

    @JvmStatic
    fun registerGalacticraftNonMobEntity(
        var0: Class<out Entity>?,
        var1: String,
        trackingDistance: Int,
        updateFreq: Int,
        sendVel: Boolean
    ) {
        val registryName: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_CORE, var1)
        EntityRegistry.registerModEntity(
            registryName,
            var0,
            var1,
            nextInternalID(),
            GalacticraftCore.instance,
            trackingDistance,
            updateFreq,
            sendVel
        )
    }

    @JvmStatic
    fun registerGalacticraftItem(key: String?, item: Item) {
        registerGalacticraftItem(key, ItemStack(item))
    }

    @JvmStatic
    fun registerGalacticraftItem(key: String?, item: Item, metadata: Int) {
        registerGalacticraftItem(key, ItemStack(item, 1, metadata))
    }

    @JvmStatic
    fun registerGalacticraftItem(key: String?, stack: ItemStack?) {
        GalacticraftCore.itemList.add(stack)
    }

    @JvmStatic
    fun registerGalacticraftBlock(key: String?, block: Block?) {
        GalacticraftCore.blocksList.add(block)
    }

    @JvmStatic
    fun translate(key: String): String {
        val result: String = I18n.translateToLocal(key)
        val comment: Int = result.indexOf('#')
        val ret: String = if (comment > 0) result.substring(0, comment).trim { it <= ' ' } else result
        for (i: Int in 0..<key.length) {
            val c: Char = key.get(i)
            if (Character.isUpperCase(c)) {
                System.err.println(ret)
            }
        }
        return ret
    }

    @JvmStatic
    fun translateWithSplit(key: String): MutableList<String?> {
        var translated: String = translate(key)
        val comment: Int = translated.indexOf('#')
        translated = if (comment > 0) translated.substring(0, comment).trim { it <= ' ' } else translated
        return Arrays.asList<String?>(*translated.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    @JvmStatic
    fun translateWithFormat(key: String, vararg values: Any?): String {
        val result: String = I18n.translateToLocalFormatted(key, *values)
        val comment: Int = result.indexOf('#')
        val ret: String = if (comment > 0) result.substring(0, comment).trim { it <= ' ' } else result
        for (i: Int in 0..<key.length) {
            val c: Char = key.get(i)
            if (Character.isUpperCase(c)) {
                System.err.println(ret)
            }
        }
        return ret
    }

    @JvmStatic
    fun drawStringRightAligned(string: String, x: Int, y: Int, color: Int, fontRendererObj: FontRenderer) {
        fontRendererObj.drawString(string, x - fontRendererObj.getStringWidth(string), y, color)
    }

    @JvmStatic
    fun drawStringCentered(string: String, x: Int, y: Int, color: Int, fontRendererObj: FontRenderer) {
        fontRendererObj.drawString(string, x - fontRendererObj.getStringWidth(string) / 2, y, color)
    }

    @JvmStatic
    fun lowerCaseNoun(string: String): String? {
        val l: Language = Minecraft.getInstance().getLanguageManager().getCurrentLanguage()
        if (l.getLanguageCode() == "de_DE") {
            return string
        }
        return translate(string).lowercase(Locale.getDefault())
    }

    @JvmStatic
    @Throws(IOException::class)
    fun supplementEntityKeys(inputstream: InputStream?, assetprefix: String): InputStream? {
        val langLines: ArrayList<String?> = ArrayList<String?>()
        val br: BufferedReader = BufferedReader(InputStreamReader(inputstream, StandardCharsets.UTF_8))
        var line: String?
        val supplemented: String = "entity." + assetprefix.lowercase(Locale.getDefault()) + "."


        //TODO:  We could also load en_US here and have any language keys not in the other lang set to the en_US value
        while ((br.readLine().also { line = it }) != null) {
            line = line!!.trim { it <= ' ' }
            if (!((line)!!.isEmpty())!!) {
                langLines.add(line)
                if (((line)!!.substring(0, 7) == "entity.")!!) {
                    langLines.add(supplemented + ((line)!!.substring(7))!!)
                }
            }
        }

        val outputstream: ByteArrayOutputStream = ByteArrayOutputStream()
        val writer: BufferedWriter = BufferedWriter(OutputStreamWriter(outputstream, Charsets.UTF_8.newEncoder()))
        for (outLine: String? in langLines) writer.write(outLine + "\n")
        writer.close()

        return ByteArrayInputStream(outputstream.toByteArray())
    }

    @JvmStatic
    fun loadLanguage(langIdentifier: String, assetPrefix: String, source: File) {
        if (lastLang != langIdentifier) {
            langDisable = false
        }
        if (langDisable) return
        val langFile: String = "assets/" + assetPrefix + "/lang/" + langIdentifier.substring(0, 3)
            .lowercase(Locale.getDefault()) + langIdentifier.substring(3).uppercase(Locale.getDefault()) + ".lang"
        var stream: InputStream? = null
        var zip: ZipFile? = null
        try {
            if (source.isDirectory() && (Launch.blackboard.get("fml.deobfuscatedEnvironment") as Boolean?)!!) {
                stream = FileInputStream(File(source.toURI().resolve(langFile).getPath()))
            } else {
                zip = ZipFile(source)
                val entry: ZipEntry = zip.getEntry(langFile)
                if (entry == null) throw FileNotFoundException()
                stream = zip.getInputStream(entry)
            }
            LanguageMap.inject(supplementEntityKeys(stream, assetPrefix))
        } catch (fnf: FileNotFoundException) {
            langDisable = true
        } catch (ignore: Exception) {
        } finally {
            if (stream != null) IOUtils.closeQuietly(stream)
            try {
                if (zip != null) zip.close()
            } catch (ignore: IOException) {
            }
        }
    }

    @JvmStatic
    fun getDimensionID(world: World?): Int {
        return world!!.provider.getDimension()
    }

    @JvmStatic
    fun getDimensionID(provider: WorldProvider): Int {
        return provider.getDimension()
    }

    @JvmStatic
    fun getDimensionID(tileEntity: TileEntity): Int {
        return tileEntity.getLevel().provider.getDimension()
    }

    @JvmStatic
    fun getWorldServerList(): Array<WorldServer>? {
        val server: MinecraftServer? = getServer()
        if (server != null) {
            return server.worlds
        }
        return (arrayOfNulls<WorldServer>(0) as Array<WorldServer>?)
    }

    @JvmStatic
    fun getWorldServerList(world: World?): Array<WorldServer>? {
        if (world is WorldServer) {
            return world.getMinecraftServer()!!.worlds
        }
        return getWorldServerList()
    }

    @JvmStatic
    fun sendToAllDimensions(packetType: PacketSimple.EnumSimplePacket?, data: Array<out Any?>?) {
        for (world: WorldServer in getWorldServerList()!!) {
            val id: Int = getDimensionID(world)
            (GalacticraftCore.packetPipeline)!!.sendToDimension(PacketSimple((packetType)!!, id, (data)!!), id)
        }
    }

    @JvmStatic
    fun sendToAllAround(packet: PacketSimple?, world: World, dimID: Int, pos: BlockPos, radius: Double) {
        val x: Double = pos.getX() + 0.5
        val y: Double = pos.getY() + 0.5
        val z: Double = pos.getZ() + 0.5
        val r2: Double = radius * radius
        for (playerMP: EntityPlayer in world.players()) {
            if (playerMP.dimension == dimID) {
                val dx: Double = x - playerMP.x
                val dy: Double = y - playerMP.y
                val dz: Double = z - playerMP.z

                if (dx * dx + dy * dy + dz * dz < r2) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(packet, playerMP as EntityPlayerMP)
                }
            }
        }
    }

    /**
     * Call this to obtain a seeded random which will be the SAME on
     * client and server.  This means EntityItems won't jump position, for example.
     */
    @JvmStatic
    fun getRandom(pos: BlockPos): Random {
        val blockSeed: Long = (((pos.getY() shl 28) + pos.getX() + 30000000 shl 28) + pos.getZ() + 30000000).toLong()
        return Random(blockSeed)
    }

    /**
     * Returns the angle of the compass (0 - 360 degrees) needed to reach the given position offset
     */
    @JvmStatic
    fun getAngleForRelativePosition(nearestX: Double, nearestZ: Double): Float {
        return (MathHelper.atan2(nearestX, -nearestZ).toFloat() * Constants.RADIANS_TO_DEGREES + 360f) % 360f
    }

    /**
     * Custom getEffectiveSide method, covering more cases than FMLCommonHandler
     */
    @JvmStatic
    fun getEffectiveSide(): Side {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER || Thread.currentThread().getName()
                .startsWith("Netty Epoll Server IO")
        ) {
            return Side.SERVER
        }

        return Side.CLIENT
    }

    @JvmStatic
    fun getServer(): MinecraftServer? {
        val server: MinecraftServer? = FMLCommonHandler.instance().getMinecraftServerInstance()
        if (server == null) {
            // This may be called from a different thread e.g. MapUtil
            // If on a different thread, FMLCommonHandler.instance().getMinecraftServerInstance() can return null on LAN servers
            // (I think because the FMLCommonHandler wrongly picks the client proxy if it's not in the Integrated Server thread)
            return serverCached
        }
        return server
    }

    @JvmStatic
    fun getMatchingItemEitherHand(player: EntityPlayer, item: Item?): ItemStack? {
        var stack: ItemStack = player.inventory.getItem(player.inventory.selectedSlot)
        if (stack != null && stack.getItem() === item) {
            return stack
        }
        stack = player.inventory.offhandItems.get(0)
        if (stack != null && stack.getItem() === item) {
            return stack
        }
        return null
    }

    //For performance
    @JvmStatic
    fun getPositionsAdjoining(pos: BlockPos): MutableList<BlockPos?> {
        val result: LinkedList<BlockPos?> = LinkedList<BlockPos?>()
        val x: Int = pos.getX()
        val y: Int = pos.getY()
        val z: Int = pos.getZ()
        if (y > 0) result.add(BlockPos(x, y - 1, z))
        if (y < 255) result.add(BlockPos(x, y + 1, z))
        result.add(BlockPos(x, y, z - 1))
        result.add(BlockPos(x, y, z + 1))
        result.add(BlockPos(x - 1, y, z))
        result.add(BlockPos(x + 1, y, z))
        return result
    }

    //For performance
    @JvmStatic
    fun getPositionsAdjoining(x: Int, y: Int, z: Int, result: MutableList<BlockPos?>) {
        result.clear()
        if (y > 0) result.add(BlockPos(x, y - 1, z))
        if (y < 255) result.add(BlockPos(x, y + 1, z))
        result.add(BlockPos(x, y, z - 1))
        result.add(BlockPos(x, y, z + 1))
        result.add(BlockPos(x - 1, y, z))
        result.add(BlockPos(x + 1, y, z))
    }

    @JvmStatic
    fun getPositionsAdjoiningLoaded(x: Int, y: Int, z: Int, result: MutableList<BlockPos?>, world: World) {
        result.clear()
        if (y > 0) result.add(BlockPos(x, y - 1, z))
        if (y < 255) result.add(BlockPos(x, y + 1, z))
        var pos: BlockPos = BlockPos(x, y, z - 1)
        if ((z and 15) > 0 || world.isBlockLoaded(pos, false)) result.add(pos)
        pos = BlockPos(x, y, z + 1)
        if ((z and 15) < 15 || world.isBlockLoaded(pos, false)) result.add(pos)
        pos = BlockPos(x - 1, y, z)
        if ((x and 15) > 0 || world.isBlockLoaded(pos, false)) result.add(pos)
        pos = BlockPos(x + 1, y, z)
        if ((x and 15) < 15 || world.isBlockLoaded(pos, false)) result.add(pos)
    }

    //For performance
    @JvmStatic
    fun getPositionsAdjoining(pos: BlockPos, result: MutableList<BlockPos?>) {
        result.clear()
        val x: Int = pos.getX()
        val y: Int = pos.getY()
        val z: Int = pos.getZ()
        if (y > 0) result.add(BlockPos(x, y - 1, z))
        if (y < 255) result.add(BlockPos(x, y + 1, z))
        result.add(BlockPos(x, y, z - 1))
        result.add(BlockPos(x, y, z + 1))
        result.add(BlockPos(x - 1, y, z))
        result.add(BlockPos(x + 1, y, z))
    }

    @JvmStatic
    fun spawnItem(world: World, pos: BlockPos, stack: ItemStack) {
        val `var`: Float = 0.7f
        while (!stack.isEmpty()) {
            val dx: Double = world.random.nextFloat() * `var` + (1.0f - `var`) * 0.5
            val dy: Double = world.random.nextFloat() * `var` + (1.0f - `var`) * 0.5
            val dz: Double = world.random.nextFloat() * `var` + (1.0f - `var`) * 0.5
            val entityitem: EntityItem = EntityItem(
                world,
                pos.getX() + dx,
                pos.getY() + dy,
                pos.getZ() + dz,
                stack.splitStack(world.random.nextInt(21) + 10)
            )

            entityitem.setPickupDelay(10)

            world.addFreshEntity(entityitem)
        }
    }

    @JvmStatic
    fun notifyStarted(server: MinecraftServer?) {
        serverCached = server
    }
}
