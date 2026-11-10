package galaxyspace.core.util

import galaxyspace.GalaxySpace
import galaxyspace.core.GSFluids
import galaxyspace.core.util.GSUtils
import galaxyspace.core.GSItems
import java.io.BufferedReader
import java.io.IOException
import java.io.FileWriter
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.fluid.OxygenPressureProtocol
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.block.Block
import net.minecraft.block.BlockCommandBlock
import net.minecraft.block.BlockEnchantmentTable
import net.minecraft.block.BlockFarmland
import net.minecraft.block.BlockGlass
import net.minecraft.block.BlockGravel
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockPistonBase
import net.minecraft.block.BlockSlab
import net.minecraft.block.BlockSponge
import net.minecraft.block.BlockStainedGlass
import net.minecraft.block.BlockStructure
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemBucket
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.File
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.HashSet
import java.util.Locale
import kotlin.math.cos

object GSUtils {
    @JvmField
    var changelog: MutableList<String?> = ArrayList<String?>()
    private var checked: HashSet<BlockPos?>? = null

    @JvmField
    var missingIcon: TextureAtlasSprite? = null
    private var texMap: TextureMap? = null

    @JvmStatic
    fun initFluidTextures(map: TextureMap) {
        missingIcon = map.getMissingSprite()

        texMap = map
    }

    @JvmStatic
    fun getBlockTexture(block: Block?): TextureAtlasSprite? {
        return getBlockTexture(block, "")
    }

    @JvmStatic
    fun getBlockTexture(block: Block?, addpath: String?): TextureAtlasSprite? {
        if (block == null) {
            return missingIcon
        }

        val spriteLocation: ResourceLocation?
        spriteLocation = block.getRegistryName()

        val sprite: TextureAtlasSprite =
            texMap!!.getAtlasSprite(spriteLocation!!.getNamespace() + ":blocks/" + spriteLocation.getPath() + addpath)

        return if (sprite != null) sprite else missingIcon
    }

    @JvmStatic
    fun getItemTexture(item: Item?, addpath: String?): TextureAtlasSprite? {
        if (item == null) {
            return missingIcon
        }

        val spriteLocation: ResourceLocation?
        spriteLocation = item.getRegistryName()

        val sprite: TextureAtlasSprite =
            texMap!!.getAtlasSprite(spriteLocation!!.getNamespace() + ":items/" + spriteLocation.getPath() + addpath)

        return if (sprite != null) sprite else missingIcon
    }

    @JvmStatic
    fun getFluidTexture(fluid: Fluid?, type: FluidType?): TextureAtlasSprite? {
        if (fluid == null || type == null) {
            return missingIcon
        }

        val spriteLocation: ResourceLocation
        if (type == FluidType.STILL) {
            spriteLocation = fluid.getStill()
        } else {
            spriteLocation = fluid.getFlowing()
        }

        val sprite: TextureAtlasSprite? = texMap!!.getTextureExtry(spriteLocation.toString())

        return if (sprite != null) sprite else missingIcon
    }

    @JvmStatic
    fun displayGauge(xPos: Int, yPos: Int, scale: Int, fluid: FluidStack?, side: Int /*0-left, 1-right*/) {
        var scale: Int = scale
        if (fluid == null) {
            return
        }

        val guiWidth: Int = 0 //(width - xSize) / 2;
        val guiHeight: Int = 0 //(height - ySize) / 2;

        var start: Int = 0

        while (true) {
            val renderRemaining: Int

            if (scale > 16) {
                renderRemaining = 16
                scale -= 16
            } else {
                renderRemaining = scale
                scale = 0
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            drawTexturedModalRect(
                guiWidth + xPos,
                guiHeight + yPos + 58 - renderRemaining - start,
                getFluidTexture(fluid.getFluid(), FluidType.STILL),
                16,
                16 - (16 - renderRemaining)
            )
            start += 16

            if (renderRemaining == 0 || scale == 0) {
                break
            }
        }

        //mc.renderEngine.bindTexture(this.guiTexture);
        //drawTexturedModalRect(guiWidth + xPos, guiHeight + yPos, 176, side == 0 ? 0 : 54, 16, 54);
    }

    @JvmStatic
    fun drawTexturedModalRect(
        xCoord: Int,
        yCoord: Int,
        textureSprite: TextureAtlasSprite?,
        widthIn: Int,
        heightIn: Int
    ) {
        val zLevel: Double = 0.0
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos((xCoord + 0).toDouble(), (yCoord + heightIn).toDouble(), zLevel)
            .tex(textureSprite!!.getMinU().toDouble(), textureSprite.getMaxV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + widthIn).toDouble(), (yCoord + heightIn).toDouble(), zLevel)
            .tex(textureSprite.getMaxU().toDouble(), textureSprite.getMaxV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + widthIn).toDouble(), (yCoord + 0).toDouble(), zLevel)
            .tex(textureSprite.getMaxU().toDouble(), textureSprite.getMinV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + 0).toDouble(), (yCoord + 0).toDouble(), zLevel)
            .tex(textureSprite.getMinU().toDouble(), textureSprite.getMinV().toDouble()).endVertex()
        tessellator.draw()
    }

    @JvmStatic
    fun testFuel(name: String): Boolean {
        if (name.contains("helium")) return true
        if (name.contains("fuel")) return true

        return false
    }

    @JvmStatic
    fun fillWithGCFuel(tank: FluidTank, liquid: FluidStack?, doFill: Boolean, rocket: EntityTieredRocket): Int {
        if (liquid != null && testFuel(FluidRegistry.getFluidName(liquid))) {
            val liquidInTank: FluidStack? = tank.getFluid()



            if (liquid.isFluidEqual(
                    FluidStack(
                        if (rocket.getRocketTier() > 10) GSFluids.HeliumHydrogen else GCFluids.fluidFuel,
                        1
                    )
                )
            ) {
                // If the tank is empty, fill it with the current type of GC fuel
                if (liquidInTank == null) {
                    return tank.fill(FluidStack(liquid.getFluid(), liquid.amount), doFill)
                }

                // If the tank already contains something, fill it with more of the same
                if (liquidInTank.amount < tank.getCapacity()) {
                    return tank.fill(FluidStack(liquidInTank, liquid.amount), doFill)
                }
            }
        }

        return 0
    }

    @JvmStatic
    fun getColor(r: Int, g: Int, b: Int, a: Int): Int {
        val color: Int = a shl 24 or (r shl 16) or (g shl 8) or b
        return color - 16777216
    }

    @JvmStatic
    fun getDimColor(daycolor: Vector3, nightcolor: Vector3, starbrightness: Float): Vector3 {
        val night: Float = starbrightness
        val day: Float = 1.0f - starbrightness

        val dayColR: Float = daycolor.floatX() / 255.0f
        val dayColG: Float = daycolor.floatY() / 255.0f
        val dayColB: Float = daycolor.floatZ() / 255.0f
        val nightColR: Float = nightcolor.floatX() / 255.0f
        val nightColG: Float = nightcolor.floatY() / 255.0f
        val nightColB: Float = nightcolor.floatZ() / 255.0f
        return Vector3(
            (dayColR * day + nightColR * night).toDouble(),
            (dayColG * day + nightColG * night).toDouble(),
            (dayColB * day + nightColB * night).toDouble()
        )
    }

    @JvmStatic
    fun calculateCelestialAngle(worldtime: Long, ticks: Float, daylenght: Float): Float {
        val j: Int = (worldtime % daylenght).toInt()
        var f1: Float = (j.toFloat() + ticks) / daylenght - 0.25f

        if (f1 < 0.0f) {
            ++f1
        }

        if (f1 > 1.0f) {
            --f1
        }

        val f2: Float = f1
        f1 = 1.0f - ((cos(f1.toDouble() * Math.PI) + 1.0) / 2.0).toFloat()
        f1 = f2 + (f1 - f2) / 3.0f
        return f1
    }

    @JvmStatic
    fun checkFluidTankTransfer(stacks: NonNullList<ItemStack>, slot: Int, tank: FluidTank) {
        if (FluidUtil.isValidContainer(stacks.get(slot))) {
            val liquid: FluidStack? = tank.getFluid()

            if (liquid != null) {
                if (liquid.isFluidEqual(FluidStack(FluidRegistry.LAVA, 1)) && stacks.get(slot)
                        .getItem() is ItemBucket
                ) FluidUtil.tryFillContainer(tank, liquid, stacks, slot, Items.BUCKET)
                else if (liquid.isFluidEqual(FluidStack(FluidRegistry.WATER, 1)) && stacks.get(slot)
                        .getItem() is ItemBucket
                ) FluidUtil.tryFillContainer(tank, liquid, stacks, slot, Items.BUCKET)
                else if (liquid.getFluid() === GSFluids.LiquidEthaneMethane) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    GSItems.EM_CANISTER
                )
                else if (liquid.getFluid().getName().contains("methane")) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    (AsteroidsItems.methaneCanister)!!
                )
                else if (liquid.getFluid() === GSFluids.Helium3) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    GSItems.HELIUM_CANISTER
                )
                else if (liquid.getFluid() === GSFluids.HeliumHydrogen) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    GSItems.HELIUM_HYDROGEN_CANISTER
                )
                else if (liquid.getFluid().getName().contains("hydrogen")) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    GSItems.HYDROGEN_CANISTER
                )
                else if (liquid.getFluid().getName().contains("ethane")) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    GSItems.ETHANE_CANISTER
                )
                else if (liquid.getFluid().getName().contains("oxygen")) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    (AsteroidsItems.canisterLOX)!!
                )
                else if (liquid.getFluid().getName().contains("oil")) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    (GCItems.oilCanister)!!
                )
                else if (liquid.getFluid().getName().contains("fuel")) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    (GCItems.fuelCanister)!!
                )
                else if (liquid.getFluid().getName().contains("nirtogen")) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    (AsteroidsItems.canisterLN2)!!
                )
                else if (liquid.isFluidEqual(FluidStack(FluidRegistry.WATER, 1)) && stacks.get(slot)
                        .getItem() !is ItemBucket
                ) {
                } else if (liquid.isFluidEqual(FluidStack(FluidRegistry.LAVA, 1)) && stacks.get(slot)
                        .getItem() !is ItemBucket
                ) {
                } else if (liquid.getFluid() === GSFluids.NatureGas) FluidUtil.tryFillContainer(
                    tank,
                    liquid,
                    stacks,
                    slot,
                    GSItems.NATURE_GAS_CANISTER
                )
                else FluidUtil.tryFillContainer(tank, liquid, stacks, slot, Items.BUCKET)
            }
        }
    }

    @JvmStatic
    fun getThermalControl(world: World, pos: BlockPos): Boolean {
        val block: Block = world.getBlockState(pos).getBlock()
        checked = HashSet<BlockPos?>()
        return testContactWithBreathableAir(world, block, pos, 0) == 1
    }

    @JvmStatic
    @Synchronized
    private fun testContactWithBreathableAir(
        world: World, block: Block?, pos: BlockPos,
        limitCount: Int
    ): Int {
        checked!!.add(pos)
        if (block === GCBlocks.breatheableAir || block === GCBlocks.brightBreatheableAir) {
            return (block)!!.getMetaFromState(world.getBlockState(pos))
        }

        val state: IBlockState = world.getBlockState(pos)
        if (block == null || block.getMaterial(state) === Material.AIR) {
            return -1
        }

        // Test for non-sided permeable or solid blocks first
        var permeableFlag: Boolean = false
        if (block !is BlockLeaves) {
            if (block.isOpaqueCube(state)) {
                if (block is BlockGravel || block.getMaterial(state) === Material.CLOTH || block is BlockSponge) {
                    permeableFlag = true
                } else {
                    return -1
                }
            } else if (block is BlockGlass || block is BlockStainedGlass) {
                return -1
            } else if (block is BlockLiquid) {
                return -1
            } else if (OxygenPressureProtocol.nonPermeableBlocks.containsKey(block)) {
                val metaList: ArrayList<Int> = OxygenPressureProtocol.nonPermeableBlocks.get(block)!!
                if (metaList.contains(-1) || metaList.contains(state.getBlock().getMetaFromState(state))) {
                    return -1
                }
            }
        } else {
            permeableFlag = true
        }

        // Testing a non-air, permeable block (for example a torch or a ladder)
        if (limitCount < 5) {
            for (side: EnumFacing in EnumFacing.VALUES) {
                if (permeableFlag || canBlockPassAirOnSide(world, block, pos, side)) {
                    val sidevec: BlockPos = pos.add(side.getXOffset(), side.getYOffset(), side.getZOffset())
                    if (!checked!!.contains(sidevec)) {
                        val newblock: Block = world.getBlockState(sidevec).getBlock()
                        val adjResult: Int = testContactWithBreathableAir(
                            world, newblock, sidevec,
                            limitCount + 1
                        )
                        if (adjResult >= 0) {
                            return adjResult
                        }
                    }
                }
            }
        }

        return -1
    }

    @JvmStatic
    private fun canBlockPassAirOnSide(world: World, block: Block, vec: BlockPos, side: EnumFacing): Boolean {
        if (block is IPartialSealableBlock) {
            return !(block as IPartialSealableBlock).isSealed(world, vec, side)
        }

        //Half slab seals on the top side or the bottom side according to its metadata
        if (block is BlockSlab) {
            val state: IBlockState = world.getBlockState(vec)
            val meta: Int = state.getBlock().getMetaFromState(state)
            return !(side == EnumFacing.DOWN && (meta and 8) == 8 || side == EnumFacing.UP && (meta and 8) == 0)
        }

        //Farmland etc only seals on the solid underside
        if (block is BlockFarmland || block is BlockEnchantmentTable || block is BlockLiquid) {
            return side != EnumFacing.UP
        }

        if (block is BlockPistonBase) {
            val state: IBlockState = world.getBlockState(vec)
            if (state.getValue(BlockPistonBase.EXTENDED) as Boolean) {
                val meta0: Int = state.getBlock().getMetaFromState(state)
                val facing: EnumFacing? = BlockPistonBase.getFacing(meta0)
                return side != facing
            }
            return false
        }

        return !block.isSideSolid(world.getBlockState(vec), world, vec, EnumFacing.byIndex(side.getIndex() xor 1))
    }

    @JvmStatic
    fun start() {
        val mc: Minecraft = Minecraft.getMinecraft()

        val scaledresolution: ScaledResolution = ClientUtil.getScaledRes(mc, mc.displayWidth, mc.displayHeight)

        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()

        try {
            val url: URL = URL(
                "https://raw.githubusercontent.com/BlesseNtumble/GalaxySpace/1.12.2/1.12.2/changelog.txt"
            )
            val http: HttpURLConnection = url.openConnection() as HttpURLConnection
            http.addRequestProperty("User-Agent", "Mozilla/4.76")
            val `in`: BufferedReader = BufferedReader(InputStreamReader(http.getInputStream()))
            var str: String?

            var i: Int = 0
            while ((`in`.readLine().also { str = it }) != null) {
                if (mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("ru_")) {
                    i++
                    var str2: String = String(str!!.toByteArray(), charset("UTF-8"))
                    if (str2.startsWith("r.")) {
                        str2 = str2.replace("r.", "")
                        changelog.add(str2)
                        if (str2.length > 70) changelog.add("")
                        //font.drawString(str2, x, y + 11 * i + 10,
                        //ColorUtil.to32BitColor(255, 150, 200, 255), true);
                    }
                } else { //if (this.mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("en_")) {
                    i++
                    var str2: String = String(str!!.toByteArray(), charset("UTF-8"))
                    if (str2.startsWith("e.")) {
                        str2 = (str)!!.replace("e.", "")
                        changelog.add(str2)
                        if (str2.length > 70) changelog.add("")
                        //font.drawString(str2, x, y + 12 * i - 185,
                        //ColorUtil.to32BitColor(255, 150, 200, 255), true);
                    }
                }
            }
            //count = i;
            `in`.close()
        } catch (e: Exception) {
        }
    }

    @JvmField
    var path: String = ""

    init {
        path = resolvePath()
    }

    @JvmStatic
    private fun resolvePath(): String {
        if (!GalaxySpace.debug || !GCCoreUtil.isDeobfuscated()) return ""


        /*String path = GSUtils.class.getResource("/").getPath();
		path = path.replace("bin", "src");
		path += "resources/assets/" + GalaxySpace.ASSET_PREFIX + "/";*/
        val path: String = ""
        return path
    }

    @JvmStatic
    fun addBlockJsonFiles(block: Block, addPath: String?) {
        try {
            val blockStates: File = File(
                path + "/blockstates/",
                block.getTranslationKey().lowercase(Locale.getDefault()).substring(5) + ".json"
            )
            val modelBlock: File = File(
                path + "/models/block/",
                block.getTranslationKey().lowercase(Locale.getDefault()).substring(5) + ".json"
            )
            val modelItemBlock: File = File(
                path + "/models/item/",
                block.getTranslationKey().lowercase(Locale.getDefault()).substring(5) + ".json"
            )

            if (!blockStates.exists()) if (blockStates.createNewFile()) {
                blockstateJson(block, blockStates)
            } else if (blockStates.exists()) {
                blockstateJson(block, blockStates)
            }

            if (!modelBlock.exists()) if (modelBlock.createNewFile()) {
                modelBlockJson(block, modelBlock, addPath)
            } else if (modelBlock.exists()) {
                modelBlockJson(block, modelBlock, addPath)
            }

            if (!modelItemBlock.exists()) if (modelItemBlock.createNewFile()) {
                modelItemBlockJson(block, modelItemBlock, addPath)
            } else if (modelItemBlock.exists()) {
                modelItemBlockJson(block, modelItemBlock, addPath)
            }
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun blockstateJson(block: Block, file: File) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write(
                ("{"
                        + "\n \"variants\": {"
                        + "\n \"normal\": { "
                        + "\"model\" : "
                        + "\"" + GalaxySpace.MODID + ":" + block.getTranslationKey().lowercase(Locale.getDefault())
                    .substring(5) + "\""
                        + "}"
                        + "}"
                        + "}")
            )
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun modelBlockJson(block: Block, file: File, addPath: String?) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write(
                ("{"
                        + " \"parent\": \"block/cube_all\", "
                        + " \"textures\": { "
                        + "\"all\" : "
                        + "\"" + GalaxySpace.MODID + ":blocks/" + addPath + block.getTranslationKey()
                    .lowercase(Locale.getDefault()).substring(5) + "\""
                        + "}"
                        + "}")
            )
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun modelItemBlockJson(block: Block, file: File, addPath: String?) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write(
                ("{\n"
                        + "	\"parent\": \"" + GalaxySpace.MODID + ":block/" + block.getTranslationKey()
                    .lowercase(Locale.getDefault()).substring(5) + "\" \n"
                        + "}")
            )
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    /*
	private static void modelItemBlockJson(Block block, File file, String addPath){
		try{
		FileWriter writer = new FileWriter(file);
		writer.write(
		"{"
		+ " \"parent\": \"block/cube_all\", "
		+     " \"textures\": { "
		+         "\"all\" : "
		+          "\"" + GalaxySpace.MODID + ":blocks/" + addPath + block.getUnlocalizedName().toLowerCase().substring(5) + "\""
		+       "}"
		+   "}"
		);
		writer.close();
		} catch(IOException ex){
			System.out.println(ex);
		}
	}
	*/
    // ***********ITEMS************
    @JvmStatic
    fun addItemJsonFiles(item: Item) {
        addItemJsonFiles(item, "", item.getTranslationKey().lowercase(Locale.getDefault()).substring(5))
    }

    @JvmStatic
    fun addItemJsonFiles(item: Item, folder: String?) {
        addItemJsonFiles(item, folder, item.getTranslationKey().lowercase(Locale.getDefault()).substring(5))
    }

    @JvmStatic
    fun addItemJsonFiles(item: Item, folder: String?, name: String?) {
        try {
            val pathFolder: File = File(path + "/models/item/" + folder)
            if (!pathFolder.exists()) pathFolder.mkdirs()

            val modelItemFile: File = File(pathFolder, name + ".json")

            if (!modelItemFile.exists()) if (modelItemFile.createNewFile()) {
                modelItemJson(item, modelItemFile, folder)
            } else if (modelItemFile.exists()) {
                modelItemJson(item, modelItemFile, folder)
            }
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun modelItemJson(item: Item, file: File, folder: String?) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write(
                ("{"
                        + " \"parent\": \"item/generated\", "
                        + " \"textures\": { "
                        + "\"layer0\" : "
                        + "\"" + GalaxySpace.MODID + ":items/" + folder + item.getTranslationKey()
                    .lowercase(Locale.getDefault()).substring(5) + "\""
                        + "}"
                        + "}")
            )
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    // ***********BLOCKS META************
    @JvmStatic
    fun addBlockMetadataJsonFiles(block: Block, variants: Array<String?>, property: String?, addPath: String?) {
        try {
            val blockstateItemBlockMeta: File = File(
                path + "/blockstates/",
                block.getTranslationKey().lowercase(Locale.getDefault()).substring(5) + ".json"
            )
            for (i: Int in variants.indices) {
                val modelBlockMeta: File = File(
                    path + "/models/block/" + addPath,
                    variants[i] + ".json"
                )
                val modelItemBlockMeta: File = File(
                    path + "/models/item/" + addPath,
                    variants[i] + ".json"
                )

                if (!modelBlockMeta.exists()) if (modelBlockMeta.createNewFile()) {
                    modelBlockMetaJson(block, variants, modelBlockMeta, i, addPath)
                } else if (modelBlockMeta.exists()) {
                    modelBlockMetaJson(block, variants, modelBlockMeta, i, addPath)
                }
                if (!modelItemBlockMeta.exists()) if (modelItemBlockMeta.createNewFile()) {
                    modelItemBlockMetaJson(block, variants, modelItemBlockMeta, i, addPath)
                } else if (modelItemBlockMeta.exists()) {
                    modelItemBlockMetaJson(block, variants, modelItemBlockMeta, i, addPath)
                }
            }
            if (!blockstateItemBlockMeta.exists()) if (blockstateItemBlockMeta.createNewFile()) {
                blockstateItemBlockMetaJson(block, blockstateItemBlockMeta, variants, property)
            } else if (blockstateItemBlockMeta.exists()) {
                blockstateItemBlockMetaJson(block, blockstateItemBlockMeta, variants, property)
            }
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun blockstateItemBlockMetaJson(block: Block, file: File, variants: Array<String?>, property: String?) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write("{ \n	\"variants\": { \n")

            for (i: Int in variants.indices) {
                val string: String =
                    ("\"" + property + "=" + variants[i] + "\"" + ": { \"model\": " + "\"" + GalaxySpace.MODID
                            + ":" + variants[i] + "\"}")
                if (variants[i] !== variants[0]) {
                    writer.write(", \n" + "		" + string)
                } else {
                    writer.write("		" + string)
                }
            }
            writer.write("\n	} \n}")
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun modelBlockMetaJson(block: Block, variants: Array<String?>, file: File, i: Int, addPath: String?) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write(
                ("{ \n"
                        + "	\"parent\": \"block/cube_all\", \n"
                        + " 	\"textures\": { "
                        + "\"all\" : "
                        + "\"" + GalaxySpace.MODID + ":blocks/" + addPath + variants[i] + "\""
                        + "}"
                        + "\n}")
            )
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun modelItemBlockMetaJson(block: Block, variants: Array<String?>, file: File, i: Int, addPath: String?) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write(
                ("{\n"
                        + "	\"parent\": \"" + GalaxySpace.MODID + ":block/" + variants[i] + "\" \n"
                        + "}")
            )
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    /*
	private static void modelItemBlockMetaJson(Block block, String[] variants, File file, int i, String addPath) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(
			"{"
			+ " \"parent\": \"block/cube_all\", "
			+     " \"textures\": { "
			+         "\"all\" : "
			+          "\"" + GalaxySpace.MODID + ":blocks/" + addPath + variants[i] + "\""
			+       "}"
			+   "}"
			);
			writer.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}*/
    // ***********ITEMS META************
    @JvmStatic
    fun addItemMetadataJsonFiles(item: Item?, variants: Array<String?>, folder: String?) {
        try {
            for (i: Int in variants.indices) {
                val folders: File = File(path + "/models/item/" + folder)
                if (!folders.exists()) folders.mkdirs()

                val modelVariantsItemMeta: File = File(path + "/models/item/" + folder, variants[i] + ".json")


                if (!modelVariantsItemMeta.exists()) if (modelVariantsItemMeta.createNewFile()) {
                    modelVariantsItemMetaJson(item, variants, modelVariantsItemMeta, i, folder)
                } else if (modelVariantsItemMeta.exists()) {
                    modelVariantsItemMetaJson(item, variants, modelVariantsItemMeta, i, folder)
                }
            }
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    private fun modelVariantsItemMetaJson(item: Item?, variants: Array<String?>, file: File, i: Int, folder: String?) {
        try {
            val writer: FileWriter = FileWriter(file)
            writer.write(
                ("{"
                        + " \"parent\": \"item/generated\", "
                        + " \"textures\": { "
                        + "\"layer0\" : "
                        + "\"" + GalaxySpace.MODID + ":items/" + folder + variants[i] + "\""
                        + "}"
                        + "}")
            )
            writer.close()
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @JvmStatic
    fun renderDebugGui(gui: GuiContainer, width: Int, height: Int) {
        val enable: Boolean = GalaxySpace.debug

        if (enable) for (i: Int in gui.inventorySlots.inventorySlots.indices) {
            val x: Int = gui.inventorySlots.getSlot(i).xPos
            val y: Int = gui.inventorySlots.getSlot(i).yPos
            gui.mc.fontRenderer.drawString(
                EnumColor.WHITE.toString() + "" + gui.inventorySlots.getSlot(i).getSlotIndex(),
                width + x + 5,
                height + y + 5,
                4210752
            )
        }
    }

    @JvmStatic
    fun findMatchingNASARecipe(recipes: MutableList<INasaWorkbenchRecipe>, craftMatrix: IInventory?): ItemStack {
        for (recipe: INasaWorkbenchRecipe in recipes) {
            if (recipe.matches(craftMatrix)) {
                return recipe.getRecipeOutput()
            }
        }

        return ItemStack.EMPTY
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun renderItemIntoSlot(re: RenderItem, stack: ItemStack, x: Int, y: Int, color: Vec3d) {
        GlStateManager.pushMatrix()
        renderItemModelIntoGUI(
            re,
            stack,
            x,
            y,
            re.getItemModelWithOverrides(stack, null as World?, null as EntityLivingBase?),
            color
        )
        drawRect(x + 1, y + 1, x + 15, y + 15, -0x33747475, 100.5)
        GlStateManager.popMatrix()
    }

    @JvmStatic
    private fun renderItemModelIntoGUI(
        re: RenderItem,
        stack: ItemStack,
        x: Int,
        y: Int,
        bakedmodel: IBakedModel,
        color: Vec3d
    ) {
        var bakedmodel: IBakedModel = bakedmodel
        GlStateManager.pushMatrix()
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            .setBlurMipmap(false, false)
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableAlpha()
        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        setupGuiTransform(x, y, bakedmodel.isGui3d())
        bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false)
        renderItem(re, stack, bakedmodel, color)
        GlStateManager.disableAlpha()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableLighting()
        GlStateManager.popMatrix()
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            .restoreLastBlurMipmap()
    }

    @JvmStatic
    private fun setupGuiTransform(xPosition: Int, yPosition: Int, isGui3d: Boolean) {
        GlStateManager.translate(xPosition.toFloat(), yPosition.toFloat(), 100.0f + 0)
        GlStateManager.translate(8.0f, 8.0f, 0.0f)
        GlStateManager.scale(1.0f, -1.0f, 1.0f)
        GlStateManager.scale(16.0f, 16.0f, 16.0f)

        if (isGui3d) {
            GlStateManager.enableLighting()
        } else {
            GlStateManager.disableLighting()
        }
    }

    @JvmStatic
    private fun renderItem(re: RenderItem, stack: ItemStack, model: IBakedModel, color: Vec3d) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(-0.5f, -0.5f, -0.5f)

            if (model.isBuiltInRenderer()) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                GlStateManager.enableRescaleNormal()
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack)
            } else {
                renderModel(re, model, stack, color)

                if (stack.hasEffect()) {
                    renderEffect(re, model)
                }
            }

            GlStateManager.popMatrix()
        }
    }

    @JvmStatic
    private fun renderEffect(re: RenderItem, model: IBakedModel) {
        val RES_ITEM_GLINT: ResourceLocation = ResourceLocation("textures/misc/enchanted_item_glint.png")
        GlStateManager.depthMask(false)
        GlStateManager.depthFunc(514)
        GlStateManager.disableLighting()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE)
        Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT)
        GlStateManager.matrixMode(5890)
        GlStateManager.pushMatrix()
        GlStateManager.scale(8.0f, 8.0f, 8.0f)
        val f: Float = (Minecraft.getSystemTime() % 3000L).toFloat() / 3000.0f / 8.0f
        GlStateManager.translate(f, 0.0f, 0.0f)
        GlStateManager.rotate(-50.0f, 0.0f, 0.0f, 1.0f)
        renderModel(re, model, -8372020)
        GlStateManager.popMatrix()
        GlStateManager.pushMatrix()
        GlStateManager.scale(8.0f, 8.0f, 8.0f)
        val f1: Float = (Minecraft.getSystemTime() % 4873L).toFloat() / 4873.0f / 8.0f
        GlStateManager.translate(-f1, 0.0f, 0.0f)
        GlStateManager.rotate(10.0f, 0.0f, 0.0f, 1.0f)
        renderModel(re, model, -8372020)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableLighting()
        GlStateManager.depthFunc(515)
        GlStateManager.depthMask(true)
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
    }

    @JvmStatic
    private fun renderModel(re: RenderItem, model: IBakedModel, stack: ItemStack, color: Vec3d) {
        renderModel(
            re,
            model,
            getColor((255 * color.x).toInt(), (255 * color.y).toInt(), (255 * color.z).toInt(), 255),
            stack
        )
    }

    @JvmStatic
    private fun renderModel(re: RenderItem, model: IBakedModel, color: Int) {
        renderModel(re, model, color, ItemStack.EMPTY)
    }

    @JvmStatic
    private fun renderModel(re: RenderItem, model: IBakedModel, color: Int, stack: ItemStack) {
        if (ForgeModContainer.allowEmissiveItems) {
            ForgeHooksClient.renderLitItem(re, model, color, stack)
            return
        }
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM)

        for (enumfacing: EnumFacing? in EnumFacing.entries) {
            re.renderQuads(bufferbuilder, model.getQuads(null as IBlockState?, enumfacing, 0L), color, stack)
        }

        re.renderQuads(bufferbuilder, model.getQuads(null as IBlockState?, null as EnumFacing?, 0L), color, stack)
        tessellator.draw()
    }

    @JvmStatic
    fun drawRect(left: Int, top: Int, right: Int, bottom: Int, color: Int, zLevel: Double) {
        var left: Int = left
        var top: Int = top
        var right: Int = right
        var bottom: Int = bottom
        if (left < right) {
            val i: Int = left
            left = right
            right = i
        }

        if (top < bottom) {
            val j: Int = top
            top = bottom
            bottom = j
        }

        val f3: Float = (color shr 24 and 255).toFloat() / 255.0f
        val f: Float = (color shr 16 and 255).toFloat() / 255.0f
        val f1: Float = (color shr 8 and 255).toFloat() / 255.0f
        val f2: Float = (color and 255).toFloat() / 255.0f
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.color(f, f1, f2, f3)
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION)
        bufferbuilder.pos(left.toDouble(), bottom.toDouble(), zLevel).endVertex()
        bufferbuilder.pos(right.toDouble(), bottom.toDouble(), zLevel).endVertex()
        bufferbuilder.pos(right.toDouble(), top.toDouble(), zLevel).endVertex()
        bufferbuilder.pos(left.toDouble(), top.toDouble(), zLevel).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun tryHarvestBlock(
        stack: ItemStack,
        worldIn: World,
        pos: BlockPos,
        entityLiving: EntityLivingBase,
        breakRadius: Int
    ) {
        val player: EntityPlayer? = entityLiving as EntityPlayer?
        var facing: EnumFacing = entityLiving.getHorizontalFacing()

        if (entityLiving.rotationPitch < -45.0f) {
            facing = EnumFacing.UP
        } else if (entityLiving.rotationPitch > 45.0f) {
            facing = EnumFacing.DOWN
        }

        val yAxis: Boolean = facing.getAxis() == EnumFacing.Axis.Y
        val xAxis: Boolean = facing.getAxis() == EnumFacing.Axis.X

        for (i: Int in -breakRadius..breakRadius) {
            var j: Int = -breakRadius
            while (j <= breakRadius && !stack.isEmpty()) {
                if (i == 0 && j == 0) {
                    ++j
                    continue
                }

                val pos1: BlockPos
                if (yAxis) {
                    pos1 = pos.add(i, 0, j)
                } else if (xAxis) {
                    pos1 = pos.add(0, i, j)
                } else {
                    pos1 = pos.add(i, j, 0)
                }

                //:Replicate logic of PlayerInteractionManager.tryHarvestBlock(pos1)
                val state1: IBlockState = worldIn.getBlockState(pos1)
                val f: Float = state1.getBlockHardness(worldIn, pos1)
                if (f >= 0f) {
                    val event: BlockEvent.BreakEvent = BlockEvent.BreakEvent(worldIn, pos1, state1, player)
                    MinecraftForge.EVENT_BUS.post(event)
                    if (!event.isCanceled()) {
                        val block: Block = state1.getBlock()
                        if ((block is BlockCommandBlock || block is BlockStructure) && !player!!.canUseCommandBlock()) {
                            worldIn.notifyBlockUpdate(pos1, state1, state1, 3)
                            ++j
                            continue
                        }
                        if (worldIn.isAirBlock(pos1)) {
                            ++j
                            continue
                        }

                        val tileentity: TileEntity? = worldIn.getTileEntity(pos1)
                        if (tileentity != null) {
                            val pkt: Packet<*>? = tileentity.getUpdatePacket()
                            if (pkt != null) {
                                (player as EntityPlayerMP).connection.sendPacket(pkt)
                            }
                        }

                        val canHarvest: Boolean = block.canHarvestBlock(worldIn, pos1, player)
                        if (canHarvest) {
                            val destroyed: Boolean = block.removedByPlayer(state1, worldIn, pos1, player, canHarvest)
                            if (destroyed) {
                                block.onPlayerDestroy(worldIn, pos1, state1)
                                block.harvestBlock(worldIn, player, pos1, state1, tileentity, stack)
                                //stack.damageItem(1, player);
                            }
                        }
                    }
                }
                ++j
            }
        }
    }

    @JvmStatic
    fun getRay(world: World, player: EntityPlayer, useLiquids: Boolean): RayTraceResult? {
        val f: Float = player.rotationPitch
        val f1: Float = player.rotationYaw
        val d0: Double = player.posX
        val d1: Double = player.posY + player.getEyeHeight().toDouble()
        val d2: Double = player.posZ
        val vec3d: Vec3d = Vec3d(d0, d1, d2)
        val f2: Float = MathHelper.cos(-f1 * 0.017453292f - Math.PI.toFloat())
        val f3: Float = MathHelper.sin(-f1 * 0.017453292f - Math.PI.toFloat())
        val f4: Float = -MathHelper.cos(-f * 0.017453292f)
        val f5: Float = MathHelper.sin(-f * 0.017453292f)
        val f6: Float = f3 * f4
        val f7: Float = f2 * f4
        val d3: Double = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 10
        val vec3d1: Vec3d = vec3d.add(f6.toDouble() * d3, f5.toDouble() * d3, f7.toDouble() * d3)
        return world.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false)
    }

    enum class Module_Type {
        TOOLS,
        SPACESUIT,
        ROCKET,
        OXYGEN_TANK,
        ALL
    }

    enum class FluidType {
        STILL,
        FLOWING
    }
}
