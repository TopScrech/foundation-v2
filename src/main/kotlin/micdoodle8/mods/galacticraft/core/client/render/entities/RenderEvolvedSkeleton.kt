package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import net.minecraft.client.model.ModelSkeleton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor
import net.minecraft.client.renderer.entity.layers.LayerHeldItem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class RenderEvolvedSkeleton constructor(manager: RenderManager) :
    RenderBiped<EntityEvolvedSkeleton>(manager, ModelEvolvedSkeleton(), 0.6f) {
    private val model = ModelEvolvedSkeleton(0.2f)
    private var texSwitch = false

    init {
        this.addLayer<EntityLivingBase?, LayerHeldItem?>(LayerHeldItem(this))
        this.addLayer(object : LayerBipedArmor(this) {
            override fun initArmor() {
                this.modelLeggings = ModelSkeleton(0.5f, true)
                this.modelArmor = ModelSkeleton(1.0f, true)
            }
        })
    }

    override fun getEntityTexture(par1Entity: EntityEvolvedSkeleton): ResourceLocation {
        return if (texSwitch) OverlaySensorGlasses.altTexture else skeletonTexture
    }

    override fun preRenderCallback(par1EntityLiving: EntityEvolvedSkeleton, par2: Float) {
        GL11.glScalef(1.2f, 1.2f, 1.2f)
        if (texSwitch) {
            OverlaySensorGlasses.preRenderMobs()
        }
    }

    override fun doRender(
        entity: EntityEvolvedSkeleton,
        par2: Double,
        par4: Double,
        par6: Double,
        par8: Float,
        par9: Float
    ) {
        super.doRender(entity, par2, par4, par6, par8, par9)
        if (OverlaySensorGlasses.overrideMobTexture()) {
            texSwitch = true
            super.doRender(entity, par2, par4, par6, par8, par9)
            texSwitch = false
        }
    }


    override fun applyRotations(skellie: EntityEvolvedSkeleton, pitch: Float, yaw: Float, partialTicks: Float) {
        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        GL11.glTranslatef(0f, -skellie.height * 0.55f, 0f)
        GL11.glRotatef(skellie.getTumbleAngle(partialTicks), skellie.getTumbleAxisX(), 0f, skellie.getTumbleAxisZ())
        GL11.glTranslatef(0f, skellie.height * 0.55f, 0f)
        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        super.applyRotations(skellie, pitch, yaw, partialTicks)
    }

    companion object {
        private val skeletonTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/skeleton.png")
        private val powerTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/power.png")
    }
}
