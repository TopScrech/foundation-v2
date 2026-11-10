package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity

import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.BlockBasicAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntitySmallAsteroid
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

open class RenderSmallAsteroid constructor(manager: RenderManager) : Render<EntitySmallAsteroid>(manager) {
    override fun doRender(
        asteroid: EntitySmallAsteroid,
        x: Double,
        y: Double,
        z: Double,
        f: Float,
        partialTickTime: Float
    ) {
        val dispatcher: BlockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher()
        GL11.glDisable(GL12.GL_RESCALE_NORMAL)

        GL11.glPushMatrix()
        GL11.glTranslatef(x.toFloat(), y.toFloat() + 0.5f, z.toFloat())
        GL11.glRotatef(asteroid.rotationPitch, 1f, 0f, 0f)
        GL11.glRotatef(asteroid.rotationYaw, 0f, 1f, 0f)

        this.bindEntityTexture(asteroid)
        dispatcher.renderBlockBrightness(
            (AsteroidBlocks.blockBasic)!!.getDefaultState()
                .withProperty(
                    BlockBasicAsteroids.BASIC_TYPE,
                    BlockBasicAsteroids.EnumBlockBasic.ASTEROID_0
                ), 1.0f
        )

        GL11.glPopMatrix()
    }

    override fun getEntityTexture(entity: EntitySmallAsteroid): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}
