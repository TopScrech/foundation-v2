package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.tile.TileEntityDish
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

open class TileEntityDishRenderer : TileEntitySpecialRenderer<TileEntityDish>() {
    private val renderEngine: TextureManager = FMLClientHandler.instance().getClient().renderEngine

    private fun updateModels() {
        if (modelDish == null) {
            try {
                modelDish = ClientUtil.modelFromOBJ(ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "teledish.obj"))
                modelFork = ClientUtil.modelFromOBJ(ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "telefork.obj"))
                modelSupport = ClientUtil.modelFromOBJ(ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "telesupport.obj"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun render(
        tile: TileEntityDish,
        par2: Double,
        par4: Double,
        par6: Double,
        partialTickTime: Float,
        par9: Int,
        alpha: Float
    ) {
        this.updateModels()
        val dish: TileEntityDish = tile
        val hour: Float = dish.rotation(partialTickTime) % 360f
        val declination: Float = dish.elevation(partialTickTime) % 360f

        val player: EntityPlayer? = FMLClientHandler.instance().getClient().player

        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glTranslatef(par2.toFloat(), par4.toFloat(), par6.toFloat())
        GL11.glTranslatef(0.5f, 1.0f, 0.5f)
        GL11.glScalef(1.6f, 1.25f, 1.6f)

        this.renderEngine.bindTexture(textureSupport)
        ClientUtil.drawBakedModel((modelSupport)!!)
        GL11.glScalef(1.25f, 1.6f, 1.25f)
        GL11.glTranslatef(0f, 2.88f * 0.15f / 1.6f, 0f)
        GL11.glScalef(0.85f, 0.85f, 0.85f)
        GL11.glRotatef(hour, 0f, -1f, 0f)
        this.renderEngine.bindTexture(textureFork)
        ClientUtil.drawBakedModel((modelFork)!!)

        //        float celestialAngle = (dish.getWorldObj().getCelestialAngle(1.0F) - 0.784690560F) * 360.0F;
//        float celestialAngle2 = dish.getWorldObj().getCelestialAngle(1.0F) * 360.0F;
        GL11.glTranslatef(0.0f, 2.3f, 0.0f)
        GL11.glRotatef(declination, 1.0f, 0.0f, 0.0f)
        GL11.glTranslatef(0.0f, -2.3f, 0.0f)

        this.renderEngine.bindTexture(textureDish)
        ClientUtil.drawBakedModel((modelDish)!!)

        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPopMatrix()
    }

    companion object {
        private val textureSupport = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/telesupport.png")
        private val textureFork = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/telefork.png")
        private val textureDish = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/teledish.png")
        private var modelSupport: IBakedModel? = null
        private var modelFork: IBakedModel? = null
        private var modelDish: IBakedModel? = null
    }
}
