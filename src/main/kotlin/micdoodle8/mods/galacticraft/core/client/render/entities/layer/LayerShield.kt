package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import org.lwjgl.opengl.GL11
import kotlin.math.sin

open class LayerShield constructor(private val renderer: RenderPlayer) : LayerRenderer<EntityLivingBase> {
    private var shieldModel: ModelBiped? = null

    init {
        this.initModel()
    }

    override fun shouldCombineTextures(): Boolean {
        return false
    }

    override fun doRenderLayer(
        entitylivingbaseIn: EntityLivingBase,
        f2: Float,
        f3: Float,
        partialTicks: Float,
        f5: Float,
        f6: Float,
        f7: Float,
        scale: Float
    ) {
        if (!entitylivingbaseIn.isInvisible()) {
            val gearData: PlayerGearData? = (GalacticraftCore.proxy)!!.getGearData(entitylivingbaseIn as EntityPlayer)

            if (gearData != null) {
                if (gearData.getShieldController() != GCPlayerHandler.GEAR_NOT_PRESENT) {
                    this.shieldModel!!.setVisible(false)
                    this.shieldModel!!.bipedRightLeg.showModel = true
                    this.shieldModel!!.bipedLeftLeg.showModel = true
                    this.shieldModel!!.bipedBody.showModel = true
                    this.shieldModel!!.bipedRightArm.showModel = true
                    this.shieldModel!!.bipedLeftArm.showModel = true
                    this.shieldModel!!.bipedHead.showModel = true
                    this.shieldModel!!.bipedHeadwear.showModel = true
                    this.shieldModel!!.setModelAttributes(this.renderer.getMainModel())
                    this.shieldModel!!.setLivingAnimations(entitylivingbaseIn, f2, f3, partialTicks)
                    this.renderer.bindTexture(RenderPlayerGC.thermalPaddingTexture0)
                    GlStateManager.matrixMode(5890)
                    GlStateManager.loadIdentity()
                    val f: Float =
                        sin(((entitylivingbaseIn.tickCount + partialTicks) / 20.0f).toDouble()).toFloat() * 5.0f
                    GlStateManager.translate(0.0f, f * 0.01f, 0.0f)
                    GlStateManager.matrixMode(5888)

                    GL11.glDisable(GL11.GL_LIGHTING)
                    Minecraft.getInstance().renderEngine.bindTexture(RenderPlayerGC.heatShieldTexture)
                    GL11.glEnable(GL11.GL_BLEND)
                    val sTime: Float =
                        ((1.0f - sin(((entitylivingbaseIn.tickCount + partialTicks) / 10.0f).toDouble())) * 0.1f + 0.0f).toFloat()

                    val r: Float = 0.9f * sTime
                    val g: Float = 0.2f * sTime
                    val b: Float = 0.9f * sTime

                    GlStateManager.depthMask(false)
                    GL11.glColor4f(r, g, b, 0.2f)
                    GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
                    this.shieldModel!!.render(entitylivingbaseIn, f2, f3, f5, f6, f7, scale)
                    GlStateManager.matrixMode(5890)
                    GlStateManager.loadIdentity()
                    GlStateManager.matrixMode(5888)
                    GL11.glColor4f(1f, 1f, 1f, 1f)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_ALPHA_TEST)
                    GL11.glEnable(GL11.GL_LIGHTING)
                    GlStateManager.depthMask(true)
                }
            }
        }
    }

    private fun initModel() {
        this.shieldModel = ModelPlayerGC(1.5f, false)
    }
}
