package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class LayerOxygenMask constructor(private val playerRenderer: RenderPlayer) :
    LayerRenderer<AbstractClientPlayer> {
    @JvmField
    var oxygenMask: ModelRenderer

    init {
        val scaleFactor: Float = 1.0f
        val modelPlayer: ModelPlayer = playerRenderer.getMainModel()

        this.oxygenMask = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.oxygenMask.addBox(-8.0f, -16.0f, -8.0f, 16, 16, 16, scaleFactor)
        this.oxygenMask.setRotationPoint(0.0f, 0.0f, 0.0f)
    }

    override fun doRenderLayer(
        player: AbstractClientPlayer,
        f5: Float,
        f6: Float,
        partialTicks: Float,
        f8: Float,
        f2: Float,
        f7: Float,
        scale: Float
    ) {
        if (!player.isInvisible()) {
            val gearData: PlayerGearData? = (GalacticraftCore.proxy)!!.getGearData(player)

            if (gearData != null) {
                val wearingMask: Boolean = gearData.getMask() != GCPlayerHandler.GEAR_NOT_PRESENT
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.oxygenMaskTexture)

                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedHeadwear, this.oxygenMask)
                this.oxygenMask.rotationPointY = this.playerRenderer.getMainModel().bipedHeadwear.rotationPointY * 8.0f

                GlStateManager.enableRescaleNormal()
                GlStateManager.pushMatrix()
                GlStateManager.scale(0.5f, 0.5f, 0.5f)

                if (wearingMask) {
                    GL11.glPushMatrix()
                    GL11.glScalef(1.05f, 1.05f, 1.05f)
                    this.oxygenMask.render(scale)
                    GL11.glScalef(1f, 1f, 1f)
                    GL11.glPopMatrix()
                }

                GlStateManager.popMatrix()
            }
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return true
    }
}
