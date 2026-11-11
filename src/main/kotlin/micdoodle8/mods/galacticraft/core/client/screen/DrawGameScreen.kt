package micdoodle8.mods.galacticraft.core.client.screen

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.client.IScreenManager
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.MapUtil
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.WorldProvider
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

open class DrawGameScreen constructor(
    private val scaleX: Float,
    private val scaleZ: Float,
    @JvmField var driver: TileEntity?
) : IScreenManager {
    private val renderEngine: TextureManager? = FMLClientHandler.instance().getClient().renderEngine
    private var tickDrawn = -1f

    @JvmField
    var initialise: Boolean = true

    @JvmField
    var initialiseLast: Boolean = false
    private var readyToInitialise = false
    private var tileCount = 0
    private var callCount = 0
    private var tickMapDone = -1

    @JvmField
    var telemetryLastClass: Class<*>? = null

    @JvmField
    var telemetryLastName: String? = null

    @JvmField
    var telemetryLastEntity: Entity? = null

    @JvmField
    var telemetryLastRender: Render<*>? = null

    @JvmField
    var localMap: IntArray? = null

    @JvmField
    var mapDone: Boolean = false

    @JvmField
    var mapFirstTick: Boolean = false

    init {
        this.mapFirstTick = true
    }

    open fun check(scaleXparam: Float, scaleZparam: Float): Boolean {
        if (this.mapDone) {
            return this.scaleX == scaleXparam && this.scaleZ == scaleZparam
        }

        return false
    }

    private fun makeMap() {
        if (this.mapDone || reusableMap == null || GCCoreUtil.getDimensionID(this.driver!!.getLevel()) != 0) {
            return
        }
        this.localMap = IntArray(MapUtil.SIZE_STD2 * MapUtil.SIZE_STD2)
        val result: Boolean = MapUtil.getMap((this.localMap)!!, this.driver!!.getLevel(), this.driver!!.getBlockPos())
        if (result) {
            TextureUtil.uploadTexture(
                reusableMap!!.getGlTextureId(),
                this.localMap,
                MapUtil.SIZE_STD2,
                MapUtil.SIZE_STD2
            )
            mapDone = true
        }
    }

    open fun drawScreen(type: Int, ticks: Float, cornerBlock: Boolean) {
        if (type >= GalacticraftRegistry.getMaxScreenTypes()) {
            println("Wrong gamescreen type detected - this is a bug." + type)
            return
        }

        if (cornerBlock) {
            if ((this.mapFirstTick || (ticks.toInt()) % 99 == 0) && !mapDone) {
                if (this.tickMapDone != ticks.toInt()) {
                    this.tickMapDone = ticks.toInt()
                    this.makeMap()
                    this.mapFirstTick = false
                }
            }
            this.doDraw(type, ticks)
            this.initialise = true
            this.initialiseLast = false
            return
        }

        //Performance code: if type > 1 then we only want
        //to draw the screen once per tick, for multi-screens

        //Spend the first tick just initialising the counter
        if (initialise) {
            if (!initialiseLast) {
                tickDrawn = ticks
                readyToInitialise = false
                initialiseLast = true
                return
            }

            if (!readyToInitialise) {
                if (ticks == tickDrawn) {
                    return
                }
            }

            if (!readyToInitialise) {
                readyToInitialise = true
                tickDrawn = ticks
                tileCount = 1
                return
            } else if (ticks == tickDrawn) {
                tileCount++
                return
            } else {
                //Start normal operations
                initialise = false
                initialiseLast = false
                readyToInitialise = false
            }
        }

        if (++callCount < tileCount) {
            //Normal situation, everything OK
            if (callCount == 1 || tickDrawn == ticks) {
                tickDrawn = ticks
                return
            } else  //The callCount last tick was less than the tileCount, reinitialise
            {
                initialise = true
                //but draw this tick [probably a tileEntity moved out of the frustum]
            }
        }

        if (callCount == tileCount) {
            callCount = 0
            //Again if this is not the tickDrawn then something is wrong, reinitialise
            if (tileCount > 1 && ticks != tickDrawn) {
                initialise = true
            }
        }

        tickDrawn = ticks

        this.doDraw(type, ticks)
    }

    private fun doDraw(type: Int, ticks: Float) {
        val lightMapSaveX: Float = OpenGlHelper.lastBrightnessX
        val lightMapSaveY: Float = OpenGlHelper.lastBrightnessY
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)

        if (type > 0) {
            GL11.glDisable(GL11.GL_LIGHTING)
        }

        (GalacticraftRegistry.getGameScreen(type))!!.render(type, ticks, scaleX, scaleZ, this)

        if (type > 0) {
            GL11.glEnable(GL11.GL_LIGHTING)
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY)
    }

    override fun getWorldProvider(): WorldProvider? {
        if (this.driver != null) {
            return driver!!.getLevel().provider
        }

        return null
    }

    open fun getScaleZ(): Float {
        return scaleZ
    }

    open fun getScaleX(): Float {
        return scaleX
    }

    companion object {
        private val colorBuffer: FloatBuffer = GLAllocation.createDirectFloatBuffer(16)
        private const val texCount = 1

        @JvmField
        var reusableMap: DynamicTexture? = null //This will be set up in MapUtil.resetClientBody()
    }
}
