package galaxyspace.core.client.render.entity.layers

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class LayerOxygenTank constructor() : LayerRenderer<AbstractClientPlayer> {
    @JvmField
    var OxygenTanks: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(2)
    private val playerRenderer: RenderPlayer

    init {
        this.playerRenderer = Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default")!!
        val scaleFactor: Float = 0.0f
        val modelPlayer: ModelPlayer = playerRenderer.getMainModel()

        this.OxygenTanks[0] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(16, 16)
        this.OxygenTanks[0]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.OxygenTanks[0]!!.setRotationPoint(2f, 2f, 3.8f)
        this.OxygenTanks[0]!!.mirror = true

        this.OxygenTanks[1] = ModelRenderer(modelPlayer, 0, 0).setTextureSize(16, 16)
        this.OxygenTanks[1]!!.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, scaleFactor)
        this.OxygenTanks[1]!!.setRotationPoint(-2f, 2f, 3.8f)
        this.OxygenTanks[1]!!.mirror = true
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
                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.OxygenTanks[0])
                ModelPlayer.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, this.OxygenTanks[1])

                if (Minecraft.getMinecraft().player.isSneaking()) {
                    this.OxygenTanks[0]!!.rotationPointY = 2.0f
                    this.OxygenTanks[1]!!.rotationPointY = 2.0f

                    this.OxygenTanks[0]!!.rotationPointZ = 1.6f
                    this.OxygenTanks[1]!!.rotationPointZ = 1.6f
                } else {
                    this.OxygenTanks[0]!!.rotationPointY = 0.5f
                    this.OxygenTanks[1]!!.rotationPointY = 0.5f

                    this.OxygenTanks[0]!!.rotationPointZ = 0.5f
                    this.OxygenTanks[1]!!.rotationPointZ = 0.5f
                }

                GlStateManager.enableRescaleNormal()
                if (gearData.getLeftTank() > 5) {
                    val tex: String? = getTankFromId(gearData.getLeftTank()).getTranslationKey().replace("item.", "")
                    FMLClientHandler.instance()
                        .getClient().renderEngine.bindTexture(ResourceLocation(GalaxySpace.ASSET_PREFIX + ":" + "textures/model/" + tex + ".png"))

                    GlStateManager.pushMatrix()
                    GlStateManager.translate(0.175f, 0.0f, 0.0f)
                    GlStateManager.translate(0.0f, 0.19f, 0.0f)
                    GlStateManager.translate(0.0f, 0.0f, 0.2f)
                    GlStateManager.scale(1.01f, 1.03f, 1.01f)
                    if (Minecraft.getMinecraft().player.isSneaking()) {
                        GlStateManager.rotate(29f, 1f, 0f, 0f)
                        GlStateManager.translate(0.0f, 0.031f, -0.076f)
                    }
                    this.OxygenTanks[0]!!.render(scale)

                    GlStateManager.popMatrix()
                }

                if (gearData.getRightTank() > 5) {
                    val tex: String? = getTankFromId(gearData.getRightTank()).getTranslationKey().replace("item.", "")
                    FMLClientHandler.instance()
                        .getClient().renderEngine.bindTexture(ResourceLocation(GalaxySpace.ASSET_PREFIX + ":" + "textures/model/" + tex + ".png"))


                    GlStateManager.pushMatrix()
                    GlStateManager.translate(-0.175f, 0.0f, 0.0f)
                    GlStateManager.translate(0.0f, 0.19f, 0.0f)
                    GlStateManager.translate(0.0f, 0.0f, 0.2f)
                    GlStateManager.scale(1.01f, 1.03f, 1.01f)
                    if (Minecraft.getMinecraft().player.isSneaking()) {
                        GlStateManager.rotate(29f, 1f, 0f, 0f)
                        GlStateManager.translate(0.0f, 0.031f, -0.076f)
                    }
                    this.OxygenTanks[1]!!.render(scale)

                    GlStateManager.color(1.0f, 1.0f, 1.0f)
                    GlStateManager.popMatrix()
                }
            }
        }
    }

    open fun getTankFromId(id: Int): ItemStack {
        when (id) {
            40 -> return ItemStack(GSItems.OXYGENTANK_TIER_4)
            41 -> return ItemStack(GSItems.OXYGENTANK_TIER_5)
            42 -> return ItemStack(GSItems.OXYGENTANK_TIER_6)
            43 -> return ItemStack(GSItems.OXYGENTANK_TIER_EPP)
            else -> return ItemStack(GCItems.oxTankHeavy)
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return true
    }
}
