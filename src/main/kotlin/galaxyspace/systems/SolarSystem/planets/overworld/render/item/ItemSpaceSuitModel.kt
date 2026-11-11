package galaxyspace.systems.SolarSystem.planets.overworld.render.item

import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel

import asmodeuscore.core.client.render.ModelOBJArmor
import org.lwjgl.opengl.GL11
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel
import galaxyspace.GalaxySpace
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import java.lang.Exception
import java.lang.RuntimeException
import java.util.function.Function

open class ItemSpaceSuitModel constructor(private val partType: Int) : ModelOBJArmor() {
    @Suppress("INAPPLICABLE_JVM_FIELD", "VIRTUAL_MEMBER_HIDDEN")
    @JvmField
    var color: FloatArray? = FloatArray(3)

    /**armorType: 0 - head, 1 - body and arms, 2 - legs, 3 - feet. */
    init {
        color!![0] = 1.0f
        color!![1] = 1.0f
        color!![2] = 1.0f
    }

    override fun pre() {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Minecraft.getInstance().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
    }

    override fun post() {
        GL11.glDisable(GL11.GL_BLEND)
    }

    override fun partHead(entity: Entity?) {
        if (partType == 3) {
            GL11.glTranslatef(0f, -1.9f, 0f)
            //GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            if (entity != null && entity is EntityArmorStand) {
                val i: Float = entity.yRot
                //GalaxySpace.debug(i + "");			
                if (i == 0f) GL11.glRotatef(i + 180, 0.0f, 1.0f, 0.0f)
                if (i == 90f || i == -90f) GL11.glRotatef(i, 0.0f, 1.0f, 0.0f)
                if (i == -45f || i == 135f) GL11.glRotatef(i - 90, 0.0f, 1.0f, 0.0f)
                if (i == 45f || i == -135f) GL11.glRotatef(i + 90, 0.0f, 1.0f, 0.0f)
                //else if(i < 45 && i > 0)
            } else GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
            GL11.glScalef(0.75f, 0.78f, 0.75f)
            GL11.glColor3f(color!![0], color!![1], color!![2])

            GL11.glCallList(helmetList)
        }
    }

    override fun partBody() {
        if (partType == 2 || partType == 5) {
            GL11.glTranslatef(0f, -1.75f, 0f)
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
            GL11.glScalef(0.75f, 0.75f, 0.75f)
            GL11.glColor3f(color!![0], color!![1], color!![2])
            GL11.glCallList(bodyList)
        }

        if (partType == 5 || partType == 6) {
            if (partType == 5) GL11.glTranslatef(0f, 1.9f, 0f)
            else GL11.glTranslatef(0f, -0.3f, -0.7f)

            GL11.glCallList(jetpackList)
        }
    }

    override fun partRightArm() {
        if (partType == 2 || partType == 5) {
            GL11.glTranslatef(0.375f, -1.6f, 0f)
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
            GL11.glScalef(0.75f, 0.75f, 0.75f)
            GL11.glColor3f(color!![0], color!![1], color!![2])
            GL11.glCallList(rightArmList)
        }
    }

    override fun partLeftArm() {
        if (partType == 2 || partType == 5) {
            GL11.glTranslatef(-0.375f, -1.6f, 0f)
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
            GL11.glScalef(0.75f, 0.75f, 0.75f)
            GL11.glColor3f(color!![0], color!![1], color!![2])
            GL11.glCallList(leftArmList)
        }
    }

    override fun partRightLeg() {
        if (partType == 1) {
            GL11.glTranslatef(0.125f, -0.75f, 0f)
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
            GL11.glScalef(0.75f, 0.75f, 0.75f)
            GL11.glColor3f(color!![0], color!![1], color!![2])
            GL11.glCallList(rightLegList)
        }
    }

    override fun partLeftLeg() {
        if (partType == 1) {
            GL11.glTranslatef(-0.125f, -0.75f, 0f)
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
            GL11.glScalef(0.75f, 0.75f, 0.75f)
            GL11.glColor3f(color!![0], color!![1], color!![2])
            GL11.glCallList(leftLegList)
        }
    }

    companion object {
        @JvmField
        var G: OBJModel.OBJBakedModel? = null

        @JvmField
        var Gru: OBJModel.OBJBakedModel? = null

        @JvmField
        var P2: OBJModel.OBJBakedModel? = null

        @JvmField
        var L2: OBJModel.OBJBakedModel? = null

        @JvmField
        var P3: OBJModel.OBJBakedModel? = null

        @JvmField
        var L3: OBJModel.OBJBakedModel? = null

        @JvmField
var wing1: OBJModel.OBJBakedModel? = null

        @JvmField
var wing2: OBJModel.OBJBakedModel? = null

        @JvmField
var corp: OBJModel.OBJBakedModel? = null

        @JvmField
var helmetList: Int

        @JvmField
var bodyList: Int

        @JvmField
var leftArmList: Int

        @JvmField
var rightArmList: Int

        @JvmField
var leftLegList: Int

        @JvmField
var rightLegList: Int

        @JvmField
var jetpackList: Int

        init {
            updateModels()

            val displayLists: Int = GLAllocation.generateDisplayLists(7)
            helmetList = displayLists
            bodyList = displayLists + 1
            leftArmList = displayLists + 2
            rightArmList = displayLists + 3
            leftLegList = displayLists + 4
            rightLegList = displayLists + 5
            jetpackList = displayLists + 6

            GL11.glPushMatrix()
            GL11.glNewList(helmetList, GL11.GL_COMPILE)
            ClientUtil.drawBakedModel((G)!!)
            GL11.glEndList()
            GL11.glPopMatrix()

            GL11.glPushMatrix()
            GL11.glNewList(bodyList, GL11.GL_COMPILE)
            ClientUtil.drawBakedModel((Gru)!!)
            GL11.glEndList()
            GL11.glPopMatrix()

            GL11.glPushMatrix()
            GL11.glNewList(leftArmList, GL11.GL_COMPILE)
            ClientUtil.drawBakedModel((L2)!!)
            GL11.glEndList()
            GL11.glPopMatrix()

            GL11.glPushMatrix()
            GL11.glNewList(rightArmList, GL11.GL_COMPILE)
            ClientUtil.drawBakedModel((P2)!!)
            GL11.glEndList()
            GL11.glPopMatrix()

            GL11.glPushMatrix()
            GL11.glNewList(leftLegList, GL11.GL_COMPILE)
            ClientUtil.drawBakedModel((L3)!!)
            GL11.glEndList()
            GL11.glPopMatrix()

            GL11.glPushMatrix()
            GL11.glNewList(rightLegList, GL11.GL_COMPILE)
            ClientUtil.drawBakedModel((P3)!!)
            GL11.glEndList()
            GL11.glPopMatrix()

            GL11.glPushMatrix()
            GL11.glNewList(jetpackList, GL11.GL_COMPILE)
            ClientUtil.drawBakedModel((corp)!!)
            ClientUtil.drawBakedModel((wing2)!!)
            GL11.glRotatef(10.0f, 0.0f, 0.0f, 1.0f)
            ClientUtil.drawBakedModel((wing1)!!)
            GL11.glEndList()
            GL11.glPopMatrix()
        }

        @JvmStatic
        private fun updateModels() {
            if (G == null) {
                try {
                    val model: IModel = OBJLoaderGC.instance.loadModel(
                        ResourceLocation(
                            GalaxySpace.ASSET_PREFIX,
                            "models/spacesuit.obj"
                        )
                    )
                    val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                        Function { location: ResourceLocation? ->
                            Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                        }

                    G = model.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("G"), false),
                        DefaultVertexFormats.ITEM,
                        spriteFunction
                    ) as OBJModel.OBJBakedModel
                    Gru = model.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("Gru"), false),
                        DefaultVertexFormats.ITEM,
                        spriteFunction
                    ) as OBJModel.OBJBakedModel
                    // arms
                    P2 = model.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("P2"), false),
                        DefaultVertexFormats.ITEM,
                        spriteFunction
                    ) as OBJModel.OBJBakedModel
                    L2 = model.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("L2"), false),
                        DefaultVertexFormats.ITEM,
                        spriteFunction
                    ) as OBJModel.OBJBakedModel
                    // legs
                    P3 = model.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("P3"), false),
                        DefaultVertexFormats.ITEM,
                        spriteFunction
                    ) as OBJModel.OBJBakedModel
                    L3 = model.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("L3"), false),
                        DefaultVertexFormats.ITEM,
                        spriteFunction
                    ) as OBJModel.OBJBakedModel

                    val jetpackmodel: IModel =
                        OBJLoaderGC.instance.loadModel(ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "models/jetpack.obj"))
                    val jetspriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                        Function { location: ResourceLocation? ->
                            Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                        }

                    wing1 = jetpackmodel.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("wing1"), false),
                        DefaultVertexFormats.ITEM,
                        jetspriteFunction
                    ) as OBJModel.OBJBakedModel
                    wing2 = jetpackmodel.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("wing2"), false),
                        DefaultVertexFormats.ITEM,
                        jetspriteFunction
                    ) as OBJModel.OBJBakedModel
                    corp = jetpackmodel.bake(
                        OBJModel.OBJState(ImmutableList.of<String?>("corp"), false),
                        DefaultVertexFormats.ITEM,
                        jetspriteFunction
                    ) as OBJModel.OBJBakedModel
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}
