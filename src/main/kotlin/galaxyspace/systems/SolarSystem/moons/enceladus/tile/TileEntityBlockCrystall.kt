package galaxyspace.systems.SolarSystem.moons.enceladus.tile

import galaxyspace.systems.SolarSystem.moons.enceladus.models.ModelCrystal
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.resources.Identifier as ResourceLocation

open class TileEntityBlockCrystall constructor() : TileEntitySpecialRenderer<TileEntity>() {
    private val model: ModelCrystal

    init {
        this.model = ModelCrystal()
    }

    override fun render(
        te: TileEntity,
        x: Double,
        y: Double,
        z: Double,
        partialTickTime: Float,
        par9: Int,
        alpha: Float
    ) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220f, 0f)

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f)
        GL11.glTranslatef(x.toFloat() + 0.5f, y.toFloat() + 1.8f, z.toFloat() + 0.5f)
        val textures: ResourceLocation =
            (ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/model/enceladus_crystal.png"))
        Minecraft.getInstance().renderEngine.bindTexture(textures)
        GL11.glScalef(1.2f, 1.2f, 1.2f)

        if (te.getLevel().getBlockState(te.getBlockPos().up()).isNormalCube()) {
            GL11.glTranslatef(0.0f, -2.15f, 0.0f)
            GL11.glRotatef(0f, 0.0f, 0.0f, 1.0f)
        } else GL11.glRotatef(180f, 0.0f, 0.0f, 1.0f)

        GL11.glRotatef(90f * te.getBlockMetadata(), 0.0f, 1.0f, 0.0f)
        this.model.render((null as Entity?)!!, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }
}
