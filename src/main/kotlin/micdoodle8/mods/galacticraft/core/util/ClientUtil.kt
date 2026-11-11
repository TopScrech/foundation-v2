package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import com.google.common.base.Function
import org.lwjgl.Sys
import java.io.IOException
import com.google.common.collect.ImmutableList
import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.world.level.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.item.Item
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.lang.RuntimeException

@OnlyIn(Dist.CLIENT)
object ClientUtil {
    @JvmStatic
    fun getClientTimeTotal(): Long = Sys.getTime() * 1000L / Sys.getTimerResolution()

    @JvmStatic
    fun getMilliseconds(): Long {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution()
    }

    @JvmStatic
    fun addVariant(modID: String, name: String, vararg variants: String?) {
//        Item itemBlockVariants = GameRegistry.findItem(modID, name);
        val itemBlockVariants: Item? = Item.REGISTRY.getObject(ResourceLocation.fromNamespaceAndPath(modID, name))
        val variants0: Array<ResourceLocation?> = arrayOfNulls<ResourceLocation>(variants.size)
        for (i: Int in variants.indices) {
            variants0[i] = ResourceLocation(modID + ":" + variants[i])
        }
        ModelBakery.registerItemVariants(itemBlockVariants, *variants0)
    }

    @JvmStatic
    fun registerBlockJson(texturePrefix: String?, block: Block) {
        registerBlockJson(texturePrefix, block, 0, block.getTranslationKey().substring(5))
    }

    @JvmStatic
    fun registerBlockJson(texturePrefix: String?, block: Block, meta: Int, name: String?) {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(texturePrefix + name, "inventory"));
        Minecraft.getInstance().getRenderItem().getItemModelMesher()
            .register(Item.getItemFromBlock(block), meta, ModelResourceLocation(texturePrefix + name, "inventory"))
    }

    @JvmStatic
    fun registerItemJson(texturePrefix: String?, item: Item) {
        registerItemJson(texturePrefix, item, 0, item.getTranslationKey().substring(5))
    }

    @JvmStatic
    fun registerItemJson(texturePrefix: String?, item: Item, meta: Int, name: String?) {
//        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(texturePrefix + name, "inventory"));
        Minecraft.getInstance().getRenderItem().getItemModelMesher()
            .register(item, meta, ModelResourceLocation(texturePrefix + name, "inventory"))
    }

    @JvmStatic
    fun getScaledRes(minecraft: Minecraft, width: Int, height: Int): ScaledResolution {
        return ScaledResolution(minecraft)
        //        return VersionUtil.getScaledRes(minecraft, width, height);
    }

    @JvmStatic
    fun updateFlagData(playerName: String?, sendPacket: Boolean): FlagData? {
        val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(playerName)

        if (race != null) {
            return race.getFlagData()
        } else if (!ClientProxyCore.flagRequestsSent.contains(playerName) && sendPacket) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_REQUEST_FLAG_DATA,
                    GCCoreUtil.getDimensionID(Minecraft.getInstance().level),
                    arrayOf<Any?>(playerName)
                )
            )
            ClientProxyCore.flagRequestsSent.add(playerName)
        }

        return FlagData.DEFAULT
    }

    @JvmStatic
    fun updateTeamColor(playerName: String?, sendPacket: Boolean): Vector3? {
        val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(playerName)

        if (race != null) {
            return race.getTeamColor()
        } else if (!ClientProxyCore.flagRequestsSent.contains(playerName) && sendPacket) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_REQUEST_FLAG_DATA,
                    GCCoreUtil.getDimensionID(Minecraft.getInstance().level),
                    arrayOf<Any?>(playerName)
                )
            )
            ClientProxyCore.flagRequestsSent.add(playerName)
        }

        return Vector3(1.0, 1.0, 1.0)
    }

    @JvmStatic
    fun replaceModel(
        modid: String,
        event: ModelBakeEvent,
        resLoc: String?,
        objLoc: String,
        visibleGroups: MutableList<String?>,
        clazz: Class<out ModelTransformWrapper>?,
        parentState: IModelState,
        vararg variants: String
    ) {
        var variants: Array<out String> = variants
        val model: OBJModel
        try {
            model = OBJLoaderGC.instance.loadModel(ResourceLocation.fromNamespaceAndPath(modid, objLoc)) as OBJModel
        } catch (e: Exception) {
            throw RuntimeException(e)
        }


        val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> = Function { location: ResourceLocation? ->
            Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
        }
        val newModelBase: IBakedModel =
            model.bake(OBJModel.OBJState(visibleGroups, false, parentState), DefaultVertexFormats.ITEM, spriteFunction)
        var newModelAlt: IBakedModel? = null
        if (variants.size == 0) {
            variants = arrayOf<String>("inventory")
        } else if (variants.size > 1 || variants[0] != "inventory") {
            newModelAlt = model.bake(
                OBJModel.OBJState(visibleGroups, false, TRSRTransformation.identity()),
                DefaultVertexFormats.ITEM,
                spriteFunction
            )
        }

        for (variant: String in variants) {
            val modelResourceLocation: ModelResourceLocation = ModelResourceLocation(modid + ":" + resLoc, variant)
            val `object`: IBakedModel? = event.getModelRegistry().getObject(modelResourceLocation)
            if (`object` != null) {
                var newModel: IBakedModel? = if (variant == "inventory") newModelBase else newModelAlt
                if (clazz != null) {
                    try {
                        newModel = clazz.getConstructor(IBakedModel::class.java).newInstance(newModel)
                    } catch (e: Exception) {
                        GCLog.severe("ItemModel constructor problem for " + modelResourceLocation)
                        e.printStackTrace()
                    }
                }
                event.getModelRegistry().putObject(modelResourceLocation, newModel)
            }
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun modelFromOBJ(loc: ResourceLocation): IBakedModel {
        return modelFromOBJ(loc, ImmutableList.of<String?>("main"))
    }

    @JvmStatic
    @Throws(IOException::class)
    fun modelFromOBJ(loc: ResourceLocation, visibleGroups: MutableList<String?>): IBakedModel {
        return modelFromOBJ(loc, visibleGroups, TRSRTransformation.identity())
    }

    @JvmStatic
    @Throws(IOException::class)
    fun modelFromOBJ(
        loc: ResourceLocation,
        visibleGroups: MutableList<String?>,
        parentState: IModelState
    ): IBakedModel {
        val model: IModel = OBJLoaderGC.instance.loadModel(loc)
        val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> = Function { location: ResourceLocation? ->
            Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
        }
        return model.bake(
            OBJModel.OBJState(visibleGroups, false, parentState),
            DefaultVertexFormats.ITEM,
            spriteFunction
        )
    }

    @JvmStatic
    fun drawBakedModel(model: IBakedModel) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldrenderer: BufferBuilder = tessellator.getBuffer()
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)

        for (bakedquad: BakedQuad in model.getQuads(null, null, 0)) {
            worldrenderer.addVertexData(bakedquad.getVertexData())
        }

        tessellator.draw()
    }

    @JvmStatic
    fun drawBakedModelColored(model: IBakedModel, color: Int) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldrenderer: BufferBuilder = tessellator.getBuffer()
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)

        for (bakedquad: BakedQuad in model.getQuads(null, null, 0)) {
            val data: IntArray = bakedquad.getVertexData()
            data[3] = color
            data[10] = color
            data[17] = color
            data[24] = color
            worldrenderer.addVertexData(data)
        }

        tessellator.draw()
    }
}
