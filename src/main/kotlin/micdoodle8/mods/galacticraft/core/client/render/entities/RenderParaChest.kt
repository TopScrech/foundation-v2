package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelParaChest
import micdoodle8.mods.galacticraft.core.entities.EntityParachest
import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class RenderParaChest constructor(manager: RenderManager) : Render<EntityParachest>(manager) {
    private val chestModel: ModelParaChest

    init {
        this.shadowSize = 1f
        this.chestModel = ModelParaChest()
    }

    override fun getEntityTexture(entity: EntityParachest): ResourceLocation? {
        return textures[ItemParaChute.getParachuteDamageValueFromDyeEnum(entity.color)]
    }

    override fun doRender(
        entity: EntityParachest,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        GL11.glPushMatrix()
        GL11.glTranslatef(x.toFloat() - 0.5f, y.toFloat(), z.toFloat())

        this.bindEntityTexture(entity)

        if (!entity.isDead) {
            this.chestModel.renderAll()
        }

        GL11.glPopMatrix()
    }

    companion object {
        private val textures = arrayOf<ResourceLocation?>(
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/plain.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/black.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/blue.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/lime.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/brown.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/darkblue.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/darkgray.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/darkgreen.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/gray.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/magenta.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/orange.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/pink.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/purple.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/red.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/teal.png"),
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/yellow.png")
        )

        @JvmField
        val parachestTexture: ResourceLocation =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachest.png")
    }
}
