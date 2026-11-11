package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

@OnlyIn(Dist.CLIENT)
open class TileEntityScreenRenderer : TileEntitySpecialRenderer<TileEntityScreen>() {
    private val renderEngine: TextureManager = FMLClientHandler.instance().getClient().renderEngine
    private val yPlane = 0.91f

    @JvmField
    var frame: Float = 0.098f

    override fun render(
        screen: TileEntityScreen,
        par2: Double,
        par4: Double,
        par6: Double,
        partialTickTime: Float,
        par9: Int,
        alpha: Float
    ) {
        GL11.glPushMatrix()
        // Texture file
        this.renderEngine.bindTexture(blockTexture)
        GL11.glTranslatef(par2.toFloat(), par4.toFloat(), par6.toFloat())

        var meta: Int = screen.getBlockMetadata()
        val screenData: Boolean = (meta >= 8)
        meta = meta and 7

        when (meta) {
            0 -> {
                GL11.glRotatef(180f, 1f, 0f, 0f)
                GL11.glTranslatef(0f, -1.0f, -1.0f)
            }

            1 -> {}
            2 -> {
                GL11.glTranslatef(0.0f, 0.0f, -0.87f)
                GL11.glRotatef(90f, 1.0f, 0f, 0f)
                GL11.glTranslatef(0.0f, 0.0f, -1.0f)
            }

            3 -> {
                GL11.glTranslatef(0.0f, 0.0f, 0.87f)
                GL11.glRotatef(90f, -1.0f, 0f, 0f)
                GL11.glTranslatef(1.0f, -1.0f, 1.0f)
                GL11.glRotatef(180f, 0f, -1.0f, 0f)
            }

            4 -> {
                GL11.glTranslatef(-0.87f, 0.0f, 0.0f)
                GL11.glRotatef(90f, 0f, 0f, -1.0f)
                GL11.glTranslatef(-1.0f, 0.0f, 1.0f)
                GL11.glRotatef(90f, 0f, 1.0f, 0f)
            }

            5 -> {
                GL11.glTranslatef(0.87f, 0.0f, 0.0f)
                GL11.glRotatef(90f, 0f, 0f, 1.0f)
                GL11.glTranslatef(1.0f, -1.0f, 0.0f)
                GL11.glRotatef(90f, 0f, -1.0f, 0f)
            }

            else -> {}
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        GL11.glTranslatef(-screen.screenOffsetx.toFloat(), this.yPlane, -screen.screenOffsetz.toFloat())
        GL11.glRotatef(90f, 1f, 0f, 0f)
        var cornerblock: Boolean = false
        if (screen.connectionsLeft == 0 || screen.connectionsRight == 0) {
            cornerblock = (screen.connectionsUp == 0 || screen.connectionsDown == 0)
        }
        val totalLR: Int = screen.connectionsLeft + screen.connectionsRight
        val totalUD: Int = screen.connectionsUp + screen.connectionsDown
        if (totalLR > 1 && totalUD > 1 && !cornerblock) {
            //centre block
            if (screen.connectionsLeft == screen.connectionsRight - (totalLR or 1)) {
                if (screen.connectionsUp == screen.connectionsDown - (totalUD or 1)) {
                    cornerblock = true
                }
            }
        }
        GL11.glRotatef(180f, 0f, 1f, 0f)
        GL11.glTranslatef(-(screen.screen)!!.getScaleX(), 0.0f, 0.0f)
        (screen.screen)!!.drawScreen(screen.imageType, partialTickTime + screen.getWorld().getWorldTime(), cornerblock)

        GL11.glPopMatrix()
    }

    companion object {
        @JvmField
        val blockTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/blocks/screen_side.png")
        private val colorBuffer: FloatBuffer = GLAllocation.createDirectFloatBuffer(16)
    }
}
