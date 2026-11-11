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
open class LayerOxygenParachute constructor(playerRendererIn: RenderPlayer) :
    LayerRenderer<AbstractClientPlayer> {
    private val playerRenderer: RenderPlayer?

    @JvmField
    var parachute: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(3)

    @JvmField
    var parachuteStrings: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(4)

    init {
        this.playerRenderer = playerRendererIn
        val scaleFactor: Float = 0.0f
        val modelPlayer: ModelPlayer = playerRendererIn.getMainModel()

        this.parachute[0] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(256, 256)
        this.parachute[0]!!.addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, scaleFactor)
        this.parachute[0]!!.setRotationPoint(15.0f, 4.0f, 0.0f)
        this.parachute[1] = ModelRenderer(modelPlayer, 0, 42).setTextureSize(256, 256)
        this.parachute[1]!!.addBox(-20.0f, -45.0f, -20.0f, 40, 2, 40, scaleFactor)
        this.parachute[1]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachute[2] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(256, 256)
        this.parachute[2]!!.addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, scaleFactor)
        this.parachute[2]!!.setRotationPoint(11f, -11f, 0.0f)

        this.parachuteStrings[0] = ModelRenderer(modelPlayer, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[0]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, scaleFactor)
        this.parachuteStrings[0]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachuteStrings[1] = ModelRenderer(modelPlayer, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[1]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, scaleFactor)
        this.parachuteStrings[1]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachuteStrings[2] = ModelRenderer(modelPlayer, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[2]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, scaleFactor)
        this.parachuteStrings[2]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachuteStrings[3] = ModelRenderer(modelPlayer, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[3]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, scaleFactor)
        this.parachuteStrings[3]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
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
                val usingParachute: Boolean = gearData.getParachute() != null

                if (usingParachute) {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.playerTexture)

                    this.parachute[0]!!.rotateAngleZ = (30f / Constants.RADIANS_TO_DEGREES)
                    this.parachute[2]!!.rotateAngleZ = -(30f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[0]!!.rotateAngleZ = (155f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[0]!!.rotateAngleX = (23f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[0]!!.setRotationPoint(-9.0f, -7.0f, 2.0f)
                    this.parachuteStrings[1]!!.rotateAngleZ = (155f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[1]!!.rotateAngleX = -(23f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[1]!!.setRotationPoint(-9.0f, -7.0f, 2.0f)
                    this.parachuteStrings[2]!!.rotateAngleZ = -(155f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[2]!!.rotateAngleX = (23f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[2]!!.setRotationPoint(9.0f, -7.0f, 2.0f)
                    this.parachuteStrings[3]!!.rotateAngleZ = -(155f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[3]!!.rotateAngleX = -(23f / Constants.RADIANS_TO_DEGREES)
                    this.parachuteStrings[3]!!.setRotationPoint(9.0f, -7.0f, 2.0f)

                    GlStateManager.pushMatrix()

                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(gearData.getParachute())

                    this.parachute[0]!!.render(scale)
                    this.parachute[1]!!.render(scale)
                    this.parachute[2]!!.render(scale)

                    this.parachuteStrings[0]!!.render(scale)
                    this.parachuteStrings[1]!!.render(scale)
                    this.parachuteStrings[2]!!.render(scale)
                    this.parachuteStrings[3]!!.render(scale)

                    GlStateManager.popMatrix()
                }
            }
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return true
    }
}
