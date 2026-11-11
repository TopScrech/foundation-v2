package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedCreeper
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerEvolvedCreeperCharge
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class RenderEvolvedCreeper constructor(manager: RenderManager) :
    RenderLiving<EntityEvolvedCreeper>(manager, ModelEvolvedCreeper(), 0.5f) {
    private var texSwitch = false

    init {
        this.addLayer<EntityEvolvedCreeper?, LayerEvolvedCreeperCharge?>(LayerEvolvedCreeperCharge(this))
    }

    override fun preRenderCallback(entity: EntityEvolvedCreeper, partialTicks: Float) {
        var f: Float = entity.getCreeperFlashIntensity(partialTicks)
        val f1: Float = 1.0f + MathHelper.sin(f * 100.0f) * f * 0.01f
        f = MathHelper.clamp(f, 0.0f, 1.0f)
        f = f * f
        f = f * f
        val f2: Float = (1.0f + f * 0.4f) * f1
        val f3: Float = (1.0f + f * 0.1f) / f1
        GlStateManager.scale(0.2f + f2, 0.2f + f3, 0.2f + f2)

        if (this.texSwitch) {
            GlStateManager.translate(0.0f, -0.03f, 0.0f)
            OverlaySensorGlasses.preRenderMobs()
        }
        GlStateManager.translate(0.0f, 0.125f, 0.0f)
    }

    override fun getColorMultiplier(entity: EntityEvolvedCreeper, lightBrightness: Float, partialTicks: Float): Int {
        val f: Float = entity.getCreeperFlashIntensity(partialTicks)

        if ((f * 10.0f).toInt() % 2 == 0) {
            return 0
        } else {
            var i: Int = (f * 0.2f * 255.0f).toInt()
            i = MathHelper.clamp(i, 0, 255)
            return i shl 24 or 16777215
        }
    }

    override fun getEntityTexture(entity: EntityEvolvedCreeper): ResourceLocation? {
        return if (this.texSwitch) OverlaySensorGlasses.altTexture else creeperTexture
    }

    override fun doRender(
        entity: EntityEvolvedCreeper,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
        if (OverlaySensorGlasses.overrideMobTexture()) {
            texSwitch = true
            super.doRender(entity, x, y, z, entityYaw, partialTicks)
            texSwitch = false
        }
    }

    companion object {
        private val creeperTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/creeper.png")
    }
}
