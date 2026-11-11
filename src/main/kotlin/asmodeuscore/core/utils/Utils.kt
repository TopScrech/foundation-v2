package asmodeuscore.core.utils

import foundation.inventory.getArmorItem
import foundation.block.getStateFromMeta

import net.minecraft.world.biome.Biome.BiomeProperties

import asmodeuscore.api.dimension.IProviderWeather
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import asmodeuscore.api.item.IItemPressurized
import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.fml.LogicalSide as Side
import kotlin.math.cos

object Utils {
    @JvmField
    var WE_Biome: net.minecraft.resources.ResourceKey<Biome> = foundation.biome.FoundationBiomeKeys.WORLD_ENGINE

    @JvmField
    var WE_Biome_2: net.minecraft.resources.ResourceKey<Biome> = foundation.biome.FoundationBiomeKeys.WORLD_ENGINE_2


    @JvmField
    var missingIcon: TextureAtlasSprite? = null
    private var texMap: TextureMap? = null

    @JvmStatic
    fun initFluidTextures(map: TextureMap) {
        missingIcon = map.getMissingSprite()

        texMap = map
    }

    @JvmStatic
    fun inWater(player: EntityPlayer): Boolean {
        val pos: BlockPos = player.getPosition()

        return player.level().getBlockState(pos.above()).getMaterial() === Material.WATER
    }


    @JvmStatic
    fun getBlockTexture(block: Block?): TextureAtlasSprite {
        return getBlockTexture(block, "")
    }

    @JvmStatic
    fun getBlockTexture(block: Block?, addpath: String?): TextureAtlasSprite {
        if (block == null) {
            return missingIcon!!
        }

        val spriteLocation: ResourceLocation?
        spriteLocation = block.getRegistryName()

        val sprite: TextureAtlasSprite =
            texMap!!.getAtlasSprite(spriteLocation!!.getNamespace() + ":blocks/" + spriteLocation.getPath() + addpath)

        return (if (sprite != null) sprite else asmodeuscore.core.utils.Utils.missingIcon)!!
    }

    @JvmStatic
    fun getFluidTexture(fluid: Fluid?, type: FluidType?): TextureAtlasSprite {
        if (fluid == null || type == null) {
            return missingIcon!!
        }

        val spriteLocation: ResourceLocation
        if (type == FluidType.STILL) {
            spriteLocation = fluid.getStill()
        } else {
            spriteLocation = fluid.getFlowing()
        }

        val sprite: TextureAtlasSprite? = texMap!!.getTextureExtry(spriteLocation.toString())

        return (if (sprite != null) sprite else asmodeuscore.core.utils.Utils.missingIcon)!!
    }

    @JvmStatic
    fun drawFluid(xPos: Int, yPos: Int, scale: Int, fluid: FluidStack?) {
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

            Minecraft.getInstance().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
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
    }

    @JvmStatic
    fun drawTexturedModalRect(
        xCoord: Int,
        yCoord: Int,
        textureSprite: TextureAtlasSprite,
        widthIn: Int,
        heightIn: Int
    ) {
        val zLevel: Double = 0.0
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos((xCoord + 0).toDouble(), (yCoord + heightIn).toDouble(), zLevel)
            .tex(textureSprite.getMinU().toDouble(), textureSprite.getMaxV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + widthIn).toDouble(), (yCoord + heightIn).toDouble(), zLevel)
            .tex(textureSprite.getMaxU().toDouble(), textureSprite.getMaxV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + widthIn).toDouble(), (yCoord + 0).toDouble(), zLevel)
            .tex(textureSprite.getMaxU().toDouble(), textureSprite.getMinV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + 0).toDouble(), (yCoord + 0).toDouble(), zLevel)
            .tex(textureSprite.getMinU().toDouble(), textureSprite.getMinV().toDouble()).endVertex()
        tessellator.draw()
    }

    @JvmStatic
    fun drawTexturedModalRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        u: Float,
        v: Float,
        uWidth: Float,
        vHeight: Float,
        invertX: Boolean,
        invertY: Boolean,
        texSizeX: Float,
        texSizeY: Float
    ) {
        val zLevel: Float = 0.0f
        GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        val texModX: Float = 1f / texSizeX
        val texModY: Float = 1f / texSizeY
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        val height0: Float = if (invertY) 0f else vHeight
        val height1: Float = if (invertY) vHeight else 0f
        val width0: Float = if (invertX) uWidth else 0f
        val width1: Float = if (invertX) 0f else uWidth
        worldRenderer.pos(x.toDouble(), (y + height).toDouble(), zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        worldRenderer.pos((x + width).toDouble(), (y + height).toDouble(), zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        worldRenderer.pos((x + width).toDouble(), y.toDouble(), zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        worldRenderer.pos(x.toDouble(), y.toDouble(), zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        tessellator.draw()
    }

    @JvmStatic
    fun getIntColor(r: Int, g: Int, b: Int): Int {
        return getIntColor(r, g, b, 255)
    }

    @JvmStatic
    fun getIntColorWHC(r: Int, g: Int, b: Int, a: Int): Int {
        return getIntColor(r, g, b, a) + 16777216
    }

    @JvmStatic
    fun getIntColor(r: Int, g: Int, b: Int, a: Int): Int {
        val A: Int = a * 256 * 256 * 256
        val R: Int = r * 256 * 256
        val G: Int = g * 256
        val B: Int = b
        val color: Int = A + R + G + B

        return color - 16777216
    }

    @JvmStatic
    fun calculateCelestialAngle(worldtime: Long, ticks: Float, daylenght: Float): Float {
        var daylenght: Float = daylenght
        if (daylenght == 0f) daylenght = Long.MAX_VALUE.toFloat()

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
    fun interpolateRotation(a1: Float, a2: Float, p: Float): Float {
        var angle: Float = a2 - a1
        angle = if (angle < -180f) 360f.let { angle += it; angle } else angle
        return a1 + (p * ((if (angle >= 180f) 360f.let { angle -= it; angle } else angle).also { angle = it }))
    }

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
    fun getBlock(par1: String): IBlockState {
        val meta: Array<String?> = par1.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val blocks: Block? = ForgeRegistries.BLOCKS.getValue(ResourceLocation(meta[0] + ":" + meta[1]))
        if (meta.size > 2) {
            return blocks!!.getStateFromMeta(meta[2]!!.toInt())
        }
        return blocks!!.defaultBlockState()
    }

    @JvmStatic
    fun localeBoolean(bol: Boolean): String? {
        return if (bol) GCCoreUtil.translate("gui.message.yes") else GCCoreUtil.translate("gui.message.no")
    }

    @JvmStatic
    fun getAtmoArmor(player: EntityPlayer): Boolean {
        for (i: Int in 0..2) if (player.inventory.getArmorItem(i).isEmpty() || player.inventory.getArmorItem(i)
                .getItem() !is IItemPressurized
        ) return false
        return true
    }

    @JvmStatic
    fun isDustStorm(world: World): Boolean {
        if (!world.isClientSide && world.getGameTime() % 20 == 0L && world.provider is IProviderWeather
            && world.provider is IGalacticraftWorldProvider
        ) {
            val gcprovider: IGalacticraftWorldProvider = world.provider as IGalacticraftWorldProvider
            val name: String? = (gcprovider.getCelestialBody())!!.getName()
            val msd: DustStormSaveData = DustStormSaveData.get(world, name)

            return msd.isDustStorm()
        }
        return false
    }


    enum class FluidType {
        STILL,
        FLOWING
    }
}
