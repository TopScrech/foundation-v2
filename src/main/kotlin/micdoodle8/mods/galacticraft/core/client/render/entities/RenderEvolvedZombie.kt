package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedZombie
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelZombie
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor
import net.minecraft.client.renderer.entity.layers.LayerHeldItem
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class RenderEvolvedZombie constructor(manager: RenderManager) :
    RenderBiped<EntityEvolvedZombie>(manager, ModelEvolvedZombie(true), 0.5f) {
    private val model: ModelBase = ModelEvolvedZombie(0.2f, false, true)
    private var texSwitch = false

    init {
        val layerrenderer: LayerRenderer<*>? = this.layerRenderers.get(0) as LayerRenderer<*>?
        this.addLayer<EntityLivingBase?, LayerHeldItem?>(LayerHeldItem(this))
        val layerbipedarmor: LayerBipedArmor = object : LayerBipedArmor(this) {
            override fun initArmor() {
                this.modelLeggings = ModelZombie(0.5f, true)
                this.modelArmor = ModelZombie(1.0f, true)
            }
        }
        this.addLayer<EntityLivingBase?, LayerBipedArmor?>(layerbipedarmor)
    }

    override fun getEntityTexture(par1Entity: EntityEvolvedZombie): ResourceLocation {
        return if (texSwitch) OverlaySensorGlasses.altTexture else zombieTexture
    }

    override fun preRenderCallback(zombie: EntityEvolvedZombie, par2: Float) {
        GL11.glScalef(1.2f, 1.2f, 1.2f)
        if (texSwitch) {
            OverlaySensorGlasses.preRenderMobs()
        }
    }

    override fun doRender(
        entity: EntityEvolvedZombie,
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

    override fun applyRotations(zombie: EntityEvolvedZombie, pitch: Float, yaw: Float, partialTicks: Float) {
        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        GL11.glTranslatef(0f, -zombie.height * 0.55f, 0f)
        GL11.glRotatef(zombie.getTumbleAngle(partialTicks), zombie.getTumbleAxisX(), 0f, zombie.getTumbleAxisZ())
        GL11.glTranslatef(0f, zombie.height * 0.55f, 0f)
        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        super.applyRotations(zombie, pitch, yaw, partialTicks)
    }

    companion object {
        private val zombieTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/zombie.png")
        private val powerTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/power.png")
    }
}
