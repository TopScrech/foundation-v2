package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC
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
import kotlin.math.pow

@SideOnly(Side.CLIENT)
open class LayerOxygenGear constructor(private val playerRenderer: RenderPlayer) :
    LayerRenderer<AbstractClientPlayer> {
    @JvmField
    var tubes: Array<Array<ModelRenderer?>?> = Array<Array<ModelRenderer?>?>(2) { arrayOfNulls<ModelRenderer>(7) }

    init {
        val scaleFactor: Float = 0.0f
        val modelPlayer: ModelPlayer = playerRenderer.getMainModel()

        this.tubes[0]!![0] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[0]!![0]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[0]!![0]!!.setRotationPoint(2f, 3f, 5.8f)
        this.tubes[0]!![0]!!.setTextureSize(128, 64)
        this.tubes[0]!![0]!!.mirror = true
        this.tubes[0]!![1] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[0]!![1]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[0]!![1]!!.setRotationPoint(2f, 2f, 6.8f)
        this.tubes[0]!![1]!!.setTextureSize(128, 64)
        this.tubes[0]!![1]!!.mirror = true
        this.tubes[0]!![2] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[0]!![2]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[0]!![2]!!.setRotationPoint(2f, 1f, 6.8f)
        this.tubes[0]!![2]!!.setTextureSize(128, 64)
        this.tubes[0]!![2]!!.mirror = true
        this.tubes[0]!![3] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[0]!![3]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[0]!![3]!!.setRotationPoint(2f, 0f, 6.8f)
        this.tubes[0]!![3]!!.setTextureSize(128, 64)
        this.tubes[0]!![3]!!.mirror = true
        this.tubes[0]!![4] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[0]!![4]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[0]!![4]!!.setRotationPoint(2f, -1f, 6.8f)
        this.tubes[0]!![4]!!.setTextureSize(128, 64)
        this.tubes[0]!![4]!!.mirror = true
        this.tubes[0]!![5] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[0]!![5]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[0]!![5]!!.setRotationPoint(2f, -2f, 5.8f)
        this.tubes[0]!![5]!!.setTextureSize(128, 64)
        this.tubes[0]!![5]!!.mirror = true
        this.tubes[0]!![6] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[0]!![6]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[0]!![6]!!.setRotationPoint(2f, -3f, 4.8f)
        this.tubes[0]!![6]!!.setTextureSize(128, 64)
        this.tubes[0]!![6]!!.mirror = true

        this.tubes[1]!![0] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[1]!![0]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[1]!![0]!!.setRotationPoint(-2f, 3f, 5.8f)
        this.tubes[1]!![0]!!.setTextureSize(128, 64)
        this.tubes[1]!![0]!!.mirror = true
        this.tubes[1]!![1] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[1]!![1]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[1]!![1]!!.setRotationPoint(-2f, 2f, 6.8f)
        this.tubes[1]!![1]!!.setTextureSize(128, 64)
        this.tubes[1]!![1]!!.mirror = true
        this.tubes[1]!![2] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[1]!![2]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[1]!![2]!!.setRotationPoint(-2f, 1f, 6.8f)
        this.tubes[1]!![2]!!.setTextureSize(128, 64)
        this.tubes[1]!![2]!!.mirror = true
        this.tubes[1]!![3] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[1]!![3]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[1]!![3]!!.setRotationPoint(-2f, 0f, 6.8f)
        this.tubes[1]!![3]!!.setTextureSize(128, 64)
        this.tubes[1]!![3]!!.mirror = true
        this.tubes[1]!![4] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[1]!![4]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[1]!![4]!!.setRotationPoint(-2f, -1f, 6.8f)
        this.tubes[1]!![4]!!.setTextureSize(128, 64)
        this.tubes[1]!![4]!!.mirror = true
        this.tubes[1]!![5] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[1]!![5]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[1]!![5]!!.setRotationPoint(-2f, -2f, 5.8f)
        this.tubes[1]!![5]!!.setTextureSize(128, 64)
        this.tubes[1]!![5]!!.mirror = true
        this.tubes[1]!![6] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(128, 64)
        this.tubes[1]!![6]!!.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, scaleFactor)
        this.tubes[1]!![6]!!.setRotationPoint(-2f, -3f, 4.8f)
        this.tubes[1]!![6]!!.setTextureSize(128, 64)
        this.tubes[1]!![6]!!.mirror = true
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
                val wearingGear: Boolean = gearData.getGear() == Constants.GEAR_ID_OXYGEN_GEAR
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.playerTexture)

                if (wearingGear && !playerRenderer.getMainModel().isSneak) {
                    for (i: Int in 0..6) {
                        for (k: Int in 0..1) {
                            ModelPlayer.copyModelAngles(
                                this.playerRenderer.getMainModel().bipedBody,
                                this.tubes[k]!![i]
                            )

                            GlStateManager.enableRescaleNormal()
                            GlStateManager.pushMatrix()
                            GlStateManager.translate(0.175f * (k * 2 - 1).toFloat(), 0.0f, 0.0f)
                            GlStateManager.translate(0.0f, -0.0325f * (i * 2 - 1).toFloat(), 0.0f)
                            GlStateManager.translate(
                                0.0f,
                                0.0f,
                                -0.0325f * ((i * 2 - 1).toDouble().pow(2.0) * 0.05).toFloat()
                            )
                            GlStateManager.translate(0.0f, 0.2f, 0.0f)
                            GlStateManager.translate(0.0f, 0.0f, 0.2f)

                            this.tubes[k]!![i]!!.render(scale)

                            GlStateManager.popMatrix()
                        }
                    }
                }
            }
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f)
    }

    override fun shouldCombineTextures(): Boolean {
        return true
    }
}
