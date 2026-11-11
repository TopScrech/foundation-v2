package micdoodle8.mods.galacticraft.core.client.screen

import micdoodle8.mods.galacticraft.api.client.IGameScreen
import micdoodle8.mods.galacticraft.api.client.IScreenManager
import micdoodle8.mods.galacticraft.api.entity.ITelemetry
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderLivingBase
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.*
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.DyeColor as EnumDyeColor
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.lang.reflect.Method
import java.nio.DoubleBuffer
import kotlin.Array
import kotlin.DoubleArray
import kotlin.Exception
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.arrayOf
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

open class GameScreenText constructor() : IGameScreen {
    private var frameA = 0f
    private var frameBx = 0f
    private var frameBy = 0f
    private var yPos = 0
    private var planes: DoubleBuffer? = null
    private var renderModelMethod: Method? = null
    private var renderLayersMethod: Method? = null

    init {
        if (GCCoreUtil.getEffectiveSide().isClient()) {
            planes = BufferUtils.createDoubleBuffer(4 * java.lang.Double.SIZE)
            try {
                val clazz: Class<*> = RenderLivingBase::class.java
                var count: Int = 0
                for (m: Method in clazz.getDeclaredMethods()) {
                    val s: String = m.getName()
                    if (s == if (GCCoreUtil.isDeobfuscated()) "renderModel" else "func_77036_a") {
                        m.setAccessible(true)
                        this.renderModelMethod = m
                        if (count == 1) break
                        count = 1
                    } else if (s == if (GCCoreUtil.isDeobfuscated()) "renderLayers" else "func_177093_a") {
                        m.setAccessible(true)
                        this.renderLayersMethod = m
                        if (count == 1) break
                        count = 1
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun setFrameSize(frameSize: Float) {
        this.frameA = frameSize
    }

    @OnlyIn(Dist.CLIENT)
    override fun render(type: Int, ticks: Float, sizeX: Float, sizeY: Float, scr: IScreenManager?) {
        val screen: DrawGameScreen = scr as DrawGameScreen

        frameBx = sizeX - frameA
        frameBy = sizeY - frameA
        drawBlackBackground(0.0f)
        planeEquation(frameA, frameA, 0f, frameA, frameBy, 0f, frameA, frameBy, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE0)
        planeEquation(frameBx, frameBy, 0f, frameBx, frameA, 0f, frameBx, frameA, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE1, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE1)
        planeEquation(frameA, frameBy, 0f, frameBx, frameBy, 0f, frameBx, frameBy, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE2, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE2)
        planeEquation(frameBx, frameA, 0f, frameA, frameA, 0f, frameA, frameA, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE3, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE3)
        yPos = 0

        val telemeter: TileEntityTelemetry? = TileEntityTelemetry.getNearest(screen.driver)
        //Make the text to draw.  To look good it's important the width and height
        //of the whole text box are correctly set here.
        var strName: String = ""
        val str: Array<String?> = arrayOf<String?>(GCCoreUtil.translate("gui.display.nolink"), "", "", "", "")
        var renderEntity: Render<*>? = null
        var entity: Entity? = null
        var Xmargin: Float = 0f

        if (telemeter != null && telemeter.clientData.size >= 3) {
            if (telemeter.clientClass != null) {
                if (telemeter.clientClass == screen.telemetryLastClass && (telemeter.clientClass != EntityPlayerMP::class.java || telemeter.clientName == screen.telemetryLastName)) {
                    //Used cached data from last time if possible
                    entity = screen.telemetryLastEntity
                    renderEntity = screen.telemetryLastRender
                    strName = (screen.telemetryLastName)!!
                } else {
                    //Create an entity to render, based on class, and get its name
                    entity = null

                    if (telemeter.clientClass == EntityPlayerMP::class.java) {
                        strName = (telemeter.clientName)!!
                        entity = EntityOtherPlayerMP((screen.driver)!!.getLevel(), telemeter.clientGameProfile)
                        renderEntity = Minecraft.getInstance().getRenderManager()
                            .getEntityRenderObject<Entity>(entity) as Render<*>?
                    } else {
                        try {
                            entity = (telemeter.clientClass)!!.getConstructor(World::class.java)
                                .newInstance((screen.driver)!!.getLevel()) as Entity?
                        } catch (ex: Exception) {
                        }
                        if (entity != null) {
                            strName = entity.getName()
                        }
                        renderEntity = Minecraft.getInstance().getRenderManager().entityRenderMap.get(
                            telemeter.clientClass
                        )
                    }
                }

                //Setup special visual types from data sent by Telemetry
                if (entity is EntityHorse) {
//                    ((EntityHorse) entity).setType(HorseType.values()[telemeter.clientData[3]]);
                    entity.setHorseVariant(telemeter.clientData[4])
                }
                if (entity is EntityVillager) {
                    entity.setProfession(telemeter.clientData[3])
                    entity.setGrowingAge(telemeter.clientData[4])
                } else if (entity is EntityWolf) {
                    entity.setCollarColor(EnumDyeColor.byDyeDamage(telemeter.clientData[3]))
                    entity.setBegging(telemeter.clientData[4] == 1)
                } else if (entity is EntitySheep) {
                    entity.setFleeceColor(EnumDyeColor.byDyeDamage(telemeter.clientData[3]))
                    entity.setSheared(telemeter.clientData[4] == 1)
                } else if (entity is EntityOcelot) {
                    entity.setTameSkin(telemeter.clientData[3])
                } else if (entity is EntitySkeleton) {
//                    ((EntitySkeleton) entity).setSkeletonType(SkeletonType.values()[telemeter.clientData[3]]);
                } else if (entity is EntityZombie) {
//                    ((EntityZombie) entity).setVillager(telemeter.clientData[3] == 1); TODO Fix for MC 1.10
                    entity.setChild(telemeter.clientData[4] == 1)
                }
            }

            if (entity is ITelemetry) {
                (entity as ITelemetry).receiveData(telemeter.clientData, str)
            } else if (entity is EntityLivingBase) {
                //Living entity:
                //  data0 = time to show red damage
                //  data1 = health in half-hearts
                //  data2 = pulse
                //  data3 = hunger (for player); horsetype (for horse)
                //  data4 = oxygen (for player); horsevariant (for horse)
                str[0] = if (telemeter.clientData[0] > 0) GCCoreUtil.translate("gui.player.ouch") else ""
                if (telemeter.clientData[1] >= 0) {
                    str[1] = GCCoreUtil.translate("gui.player.health") + ": " + telemeter.clientData[1] + "%"
                } else {
                    str[1] = ""
                }
                str[2] = "" + telemeter.clientData[2] + " " + GCCoreUtil.translate("gui.player.bpm")
                if (telemeter.clientData[3] > -1) {
                    str[3] = GCCoreUtil.translate("gui.player.food") + ": " + telemeter.clientData[3] + "%"
                }
                if (telemeter.clientData[4] > -1) {
                    var oxygen: Int = telemeter.clientData[4]
                    oxygen = (oxygen % 4096) + (oxygen / 4096)
                    if (oxygen == 180 || oxygen == 90) {
                        str[4] = GCCoreUtil.translate("gui.oxygen_storage.desc.1") + ": OK"
                    } else {
                        str[4] =
                            GCCoreUtil.translate("gui.oxygen_storage.desc.1") + ": " + this.makeOxygenString(oxygen) + GCCoreUtil.translate(
                                "gui.seconds"
                            )
                    }
                }
            } else  //Generic - could be boats or minecarts etc - just show the speed
            //TODO  can add more here, e.g. position data?
                if (telemeter.clientData[2] >= 0) {
                    str[2] = makeSpeedString(telemeter.clientData[2])
                }
        } else {
            //Default - draw a simple time display just to show the Display Screen is working
            val w1: World = (screen.driver)!!.getLevel()
            val time1: Int = if (w1 != null) ((w1.getWorldTime() + 6000L) % 24000L).toInt() else 0
            str[2] = makeTimeString(time1 * 360)
        }

        val textWidthPixels: Int = 155
        var textHeightPixels: Int = 60 //1 lines
        if (str[3]!!.isEmpty()) {
            textHeightPixels -= 10
        }
        if (str[4]!!.isEmpty()) {
            textHeightPixels -= 10
        }

        //First pass - approximate border size
        var borders: Float = frameA * 2 + 0.05f * min(sizeX, sizeY)
        var scaleXTest: Float = (sizeX - borders) / textWidthPixels
        var scaleYTest: Float = (sizeY - borders) / textHeightPixels
        var scale: Float = sizeX
        if (scaleYTest < scaleXTest) {
            scale = sizeY
        }
        //Second pass - the border size may be more accurate now
        borders = frameA * 2 + 0.05f * scale
        scaleXTest = (sizeX - borders) / textWidthPixels
        scaleYTest = (sizeY - borders) / textHeightPixels
        scale = sizeX
        var scaleText: Float = scaleXTest
        if (scaleYTest < scaleXTest) {
            scale = sizeY
            scaleText = scaleYTest
        }

        //Centre the text in the display
        val border: Float = frameA + 0.025f * scale
        if (entity != null && renderEntity != null) {
            Xmargin = (sizeX - borders) / 2
        }
        val Xoffset: Float = (sizeX - borders - textWidthPixels * scaleText) / 2 + Xmargin
        val Yoffset: Float = (sizeY - borders - textHeightPixels * scaleText) / 2 + scaleText
        GL11.glTranslatef(border + Xoffset, border + Yoffset, 0.0f)
        GL11.glScalef(scaleText, scaleText, 1.0f)

        //Actually draw the text
        val whiteColour: Int = ColorUtil.to32BitColor(255, 240, 216, 255)
        drawText(strName, whiteColour)
        drawText(str[0]!!, whiteColour)
        drawText(str[1]!!, whiteColour)
        drawText(str[2]!!, whiteColour)
        drawText(str[3]!!, whiteColour)
        drawText(str[4]!!, whiteColour)

        //If there is an entity to render, draw it on the left of the text
        if (renderEntity != null && entity != null) {
            GL11.glTranslatef(
                -Xmargin / 2 / scaleText,
                textHeightPixels / 2 + (-Yoffset + (sizeY - borders) / 2) / scaleText,
                -0.0005f
            )
            val scalefactor: Float = 38f / max(entity.height, entity.width).toDouble().pow(0.65).toFloat()
            GL11.glScalef(scalefactor, scalefactor, 0.0015f)
            GL11.glRotatef(180f, 0f, 0f, 1f)
            GL11.glRotatef(180f, 0f, 1f, 0f)
            if (entity is ITelemetry) {
                (entity as ITelemetry).adjustDisplay(telemeter!!.clientData)
            }
            RenderPlayerGC.flagThermalOverride = true
            if (entity is EntityLivingBase && renderEntity is RenderLivingBase<*> && renderModelMethod != null) {
                this.renderLiving(entity, renderEntity, ticks % 1f)
            } else {
                (renderEntity as Render<Entity>).doRender(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f)
            }
            RenderPlayerGC.flagThermalOverride = false
            //            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
//            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        //TODO  Cross-dimensional tracking (i.e. old entity setDead, new entity created)
        //TODO  Deal with text off screen (including where localizations longer than English)
        screen.telemetryLastClass = if (telemeter == null) null else telemeter.clientClass
        screen.telemetryLastEntity = entity
        screen.telemetryLastRender = renderEntity
        screen.telemetryLastName = strName
        GL11.glDisable(GL11.GL_CLIP_PLANE3)
        GL11.glDisable(GL11.GL_CLIP_PLANE2)
        GL11.glDisable(GL11.GL_CLIP_PLANE1)
        GL11.glDisable(GL11.GL_CLIP_PLANE0)
    }

    // This is a simplified version of doRender() in RenderLivingEntity
    // No lighting adjustment, no sitting, no name text, no sneaking and no Forge events
    private fun renderLiving(entity: EntityLivingBase, render: RenderLivingBase<*>, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        render.mainModel.isChild = entity.isChild()

        try {
            val f: Float = 0f
            val f1: Float = 0f
            val f2: Float = f1 - f
            val f7: Float = entity.xRotO + (entity.xRot - entity.xRotO) * partialTicks
            val f8: Float = 0f
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
            GlStateManager.enableRescaleNormal()
            GlStateManager.scale(-1.0f, -1.0f, 1.0f)
            val f4: Float = 0.0625f
            GlStateManager.translate(0.0f, -1.5078125f, 0.0f)
            var f5: Float =
                entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks
            var f6: Float = entity.limbSwing - entity.limbSwingAmount * (1.0f - partialTicks)

            if (entity.isChild()) {
                f6 *= 3.0f
            }

            if (f5 > 1.0f) {
                f5 = 1.0f
            }

            GlStateManager.enableAlpha()
            render.mainModel.setLivingAnimations(entity, f6, f5, partialTicks)
            render.mainModel.setRotationAngles(f6, f5, f8, f2, f7, 0.0625f, entity)

            renderModelMethod!!.invoke(render, entity, f6, f5, f8, f2, f7, 0.0625f)
            GlStateManager.depthMask(true)
            renderLayersMethod!!.invoke(render, entity, f6, f5, partialTicks, f8, f2, f7, 0.0625f)

            GlStateManager.disableRescaleNormal()
        } catch (exception: Exception) {
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.enableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }

    private fun makeTimeString(l: Int): String {
        val hrs: Int = l / 360000
        val mins: Int = l / 6000 - hrs * 60
        val secs: Int = l / 100 - hrs * 3600 - mins * 60
        val hrsStr: String = if (hrs > 9) "" + hrs else "0" + hrs
        val minsStr: String = if (mins > 9) "" + mins else "0" + mins
        val secsStr: String = if (secs > 9) "" + secs else "0" + secs
        return hrsStr + ":" + minsStr + ":" + secsStr
    }

    private fun makeHealthString(hearts2: Int): String {
        val sp1: Int = hearts2 / 2
        val sp2: Int = (hearts2 % 2) * 5
        val spstr1: String = "" + sp1
        val spstr2: String = "" + sp2
        return spstr1 + "." + spstr2 + " hearts"
    }

    private fun makeOxygenString(oxygen: Int): String {
        //Server takes 1 air away every 9 ticks (OxygenUtil.getDrainSpacing)
        val sp1: Int = oxygen * 9 / 20
        val sp2: Int = ((oxygen * 9) % 20) / 2
        val spstr1: String = "" + sp1
        val spstr2: String = "" + sp2
        return spstr1 + "." + spstr2
    }

    private fun drawText(str: String, colour: Int) {
        Minecraft.getInstance().fontRenderer.drawString(str, 0f, yPos.toFloat(), colour, false)
        yPos += 10
    }

    private fun drawBlackBackground(greyLevel: Float) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GL11.glColor4f(greyLevel, greyLevel, greyLevel, 1.0f)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(frameA.toDouble(), frameBy.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameBy.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameA.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameA.toDouble(), frameA.toDouble(), 0.005).endVertex()
        tess.draw()

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
    }

    private fun planeEquation(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
        x3: Float,
        y3: Float,
        z3: Float
    ) {
        val result: DoubleArray = DoubleArray(4)
        result[0] = (y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2)).toDouble()
        result[1] = (z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2)).toDouble()
        result[2] = (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)).toDouble()
        result[3] = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1)).toDouble()
        planes!!.put(result, 0, 4)
        planes!!.position(0)
    }

    companion object {
        @JvmStatic
        fun makeSpeedString(speed100: Int): String {
            val sp1: Int = speed100 / 100
            val sp2: Int = (speed100 % 100)
            val spstr1: String = GCCoreUtil.translate("gui.rocket.speed") + ": " + sp1
            val spstr2: String = (if (sp2 > 9) "" else "0") + sp2
            return spstr1 + "." + spstr2 + " " + GCCoreUtil.translate("gui.lander.velocityu")
        }
    }
}
