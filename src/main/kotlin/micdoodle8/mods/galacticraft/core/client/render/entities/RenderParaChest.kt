package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelParaChest
import micdoodle8.mods.galacticraft.core.entities.EntityParachest
import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
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
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/plain.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/black.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/blue.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/lime.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/brown.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/darkblue.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/darkgray.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/darkgreen.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/gray.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/magenta.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/orange.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/pink.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/purple.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/red.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/teal.png"),
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/yellow.png")
        )

        @JvmField
        val parachestTexture: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachest.png")
    }
}
