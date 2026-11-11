package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class LayerOxygenTanks constructor(private val playerRenderer: RenderPlayer) :
    LayerRenderer<AbstractClientPlayer> {
    @JvmField
    var greenOxygenTanks: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(2)

    @JvmField
    var orangeOxygenTanks: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(2)

    @JvmField
    var redOxygenTanks: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(2)

    init {
        val scaleFactor: Float = 0.0f
        val modelPlayer: ModelPlayer = playerRenderer.getMainModel()

        this.greenOxygenTanks[0] = ModelRenderer(modelPlayer, 4, 0).setTextureSize(128, 64)
        this.greenOxygenTanks[0]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.greenOxygenTanks[0]!!.setRotationPoint(2f, 2f, 3.8f)
        this.greenOxygenTanks[0]!!.mirror = true
        this.greenOxygenTanks[1] = ModelRenderer(modelPlayer, 4, 0).setTextureSize(128, 64)
        this.greenOxygenTanks[1]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.greenOxygenTanks[1]!!.setRotationPoint(-2f, 2f, 3.8f)
        this.greenOxygenTanks[1]!!.mirror = true

        this.orangeOxygenTanks[0] = ModelRenderer(modelPlayer, 16, 0).setTextureSize(128, 64)
        this.orangeOxygenTanks[0]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.orangeOxygenTanks[0]!!.setRotationPoint(2f, 2f, 3.8f)
        this.orangeOxygenTanks[0]!!.mirror = true
        this.orangeOxygenTanks[1] = ModelRenderer(modelPlayer, 16, 0).setTextureSize(128, 64)
        this.orangeOxygenTanks[1]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.orangeOxygenTanks[1]!!.setRotationPoint(-2f, 2f, 3.8f)
        this.orangeOxygenTanks[1]!!.mirror = true

        this.redOxygenTanks[0] = ModelRenderer(modelPlayer, 28, 0).setTextureSize(128, 64)
        this.redOxygenTanks[0]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.redOxygenTanks[0]!!.setRotationPoint(2f, 2f, 3.8f)
        this.redOxygenTanks[0]!!.mirror = true
        this.redOxygenTanks[1] = ModelRenderer(modelPlayer, 28, 0).setTextureSize(128, 64)
        this.redOxygenTanks[1]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.redOxygenTanks[1]!!.setRotationPoint(-2f, 2f, 3.8f)
        this.redOxygenTanks[1]!!.mirror = true
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
                val wearingLeftTankGreen: Boolean = gearData.getLeftTank() == Constants.GEAR_ID_OXYGEN_TANK_LIGHT
                val wearingLeftTankOrange: Boolean = gearData.getLeftTank() == Constants.GEAR_ID_OXYGEN_TANK_MEDIUM
                val wearingLeftTankRed: Boolean =
                    gearData.getLeftTank() == Constants.GEAR_ID_OXYGEN_TANK_HEAVY || gearData.getLeftTank() == Constants.GEAR_ID_OXYGEN_TANK_INFINITE
                val wearingRightTankGreen: Boolean = gearData.getRightTank() == Constants.GEAR_ID_OXYGEN_TANK_LIGHT
                val wearingRightTankOrange: Boolean = gearData.getRightTank() == Constants.GEAR_ID_OXYGEN_TANK_MEDIUM
                val wearingRightTankRed: Boolean =
                    gearData.getRightTank() == Constants.GEAR_ID_OXYGEN_TANK_HEAVY || gearData.getRightTank() == Constants.GEAR_ID_OXYGEN_TANK_INFINITE

                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.playerTexture)

                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.greenOxygenTanks[0])
                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.greenOxygenTanks[1])
                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.orangeOxygenTanks[0])
                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.orangeOxygenTanks[1])
                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.redOxygenTanks[0])
                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.redOxygenTanks[1])

                if (playerRenderer.getMainModel().isSneak) {
                    this.greenOxygenTanks[0]!!.rotationPointY = 2.0f
                    this.greenOxygenTanks[1]!!.rotationPointY = 2.0f
                    this.greenOxygenTanks[0]!!.rotationPointZ = 1.6f
                    this.greenOxygenTanks[1]!!.rotationPointZ = 1.6f

                    this.orangeOxygenTanks[0]!!.rotationPointY = 2.0f
                    this.orangeOxygenTanks[1]!!.rotationPointY = 2.0f
                    this.orangeOxygenTanks[0]!!.rotationPointZ = 1.6f
                    this.orangeOxygenTanks[1]!!.rotationPointZ = 1.6f

                    this.redOxygenTanks[0]!!.rotationPointY = 2.0f
                    this.redOxygenTanks[1]!!.rotationPointY = 2.0f
                    this.redOxygenTanks[0]!!.rotationPointZ = 1.6f
                    this.redOxygenTanks[1]!!.rotationPointZ = 1.6f
                } else {
                    this.greenOxygenTanks[0]!!.rotationPointY = 0.5f
                    this.greenOxygenTanks[1]!!.rotationPointY = 0.5f
                    this.greenOxygenTanks[0]!!.rotationPointZ = 0.5f
                    this.greenOxygenTanks[1]!!.rotationPointZ = 0.5f

                    this.orangeOxygenTanks[0]!!.rotationPointY = 0.5f
                    this.orangeOxygenTanks[1]!!.rotationPointY = 0.5f
                    this.orangeOxygenTanks[0]!!.rotationPointZ = 0.5f
                    this.orangeOxygenTanks[1]!!.rotationPointZ = 0.5f

                    this.redOxygenTanks[0]!!.rotationPointY = 0.5f
                    this.redOxygenTanks[1]!!.rotationPointY = 0.5f
                    this.redOxygenTanks[0]!!.rotationPointZ = 0.5f
                    this.redOxygenTanks[1]!!.rotationPointZ = 0.5f
                }

                for (i: Int in 0..1) {
                    GlStateManager.enableRescaleNormal()
                    GlStateManager.pushMatrix()
                    GlStateManager.translate(0.175f, 0.0f, 0.0f)
                    GlStateManager.translate(0.0f, 0.2f, 0.0f)
                    GlStateManager.translate(0.0f, 0.0f, 0.2f)

                    if (wearingLeftTankRed) {
                        this.redOxygenTanks[0]!!.render(scale)
                    }

                    if (wearingLeftTankOrange) {
                        this.orangeOxygenTanks[0]!!.render(scale)
                    }

                    if (wearingLeftTankGreen) {
                        this.greenOxygenTanks[0]!!.render(scale)
                    }

                    GlStateManager.popMatrix()

                    GlStateManager.pushMatrix()
                    GlStateManager.translate(-0.175f, 0.0f, 0.0f)
                    GlStateManager.translate(0.0f, 0.2f, 0.0f)
                    GlStateManager.translate(0.0f, 0.0f, 0.2f)

                    if (wearingRightTankRed) {
                        this.redOxygenTanks[1]!!.render(scale)
                    }

                    if (wearingRightTankOrange) {
                        this.orangeOxygenTanks[1]!!.render(scale)
                    }

                    if (wearingRightTankGreen) {
                        this.greenOxygenTanks[1]!!.render(scale)
                    }
                    GlStateManager.color(1.0f, 1.0f, 1.0f)
                    GlStateManager.popMatrix()
                }
            }
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return true
    }
}
