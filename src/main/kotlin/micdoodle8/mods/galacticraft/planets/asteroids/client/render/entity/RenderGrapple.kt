package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity

import com.google.common.base.Function
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelGrapple
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityGrapple
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

open class RenderGrapple constructor(manager: RenderManager) : Render<EntityGrapple>(manager) {
    private var grappleModel: ItemModelGrapple? = null

    private fun updateModel() {
        if (grappleModel == null) {
            val TEXTUREGETTER: Function<ResourceLocation?, TextureAtlasSprite?> = Function { input: ResourceLocation? ->
                Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(input.toString())
            }

            val modelResourceLocation: ModelResourceLocation =
                ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "grapple", "inventory")
            grappleModel =
                Minecraft.getInstance().getRenderItem().getItemModelMesher().getModelManager()
                    .getModel(modelResourceLocation) as ItemModelGrapple
        }
    }

    override fun doRender(grapple: EntityGrapple, x: Double, y: Double, z: Double, par8: Float, partialTicks: Float) {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPushMatrix()

        val vec3: Vec3d = Vec3d(0.0, -0.2, 0.0)
        val shootingEntity: EntityPlayer? = grapple.getShootingEntity()

        if (shootingEntity != null && grapple.getPullingEntity()) {
            val d3: Double =
                shootingEntity.prevPosX + (shootingEntity.x - shootingEntity.prevPosX) * partialTicks + vec3.x
            val d4: Double =
                shootingEntity.prevPosY + (shootingEntity.y - shootingEntity.prevPosY) * partialTicks + vec3.y
            val d5: Double =
                shootingEntity.prevPosZ + (shootingEntity.z - shootingEntity.prevPosZ) * partialTicks + vec3.z

            val tessellator: Tessellator = Tessellator.getInstance()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_LIGHTING)
            tessellator.getBuffer().begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
            val b2: Byte = 16

            val d14: Double = grapple.prevPosX + (grapple.posX - grapple.prevPosX) * partialTicks
            val d8: Double = grapple.prevPosY + (grapple.posY - grapple.prevPosY) * partialTicks + 0.25
            val d10: Double = grapple.prevPosZ + (grapple.posZ - grapple.prevPosZ) * partialTicks
            val d11: Double = (d3 - d14).toFloat().toDouble()
            val d12: Double = (d4 - d8).toFloat().toDouble()
            val d13: Double = (d5 - d10).toFloat().toDouble()
            tessellator.getBuffer().setTranslation(0.0, -0.2, 0.0)

            for (i: Int in 0..b2) {
                val f12: Float = i.toFloat() / b2.toFloat()
                tessellator.getBuffer().pos(x + d11 * f12, y + d12 * (f12 * f12 + f12) * 0.5 + 0.15, z + d13 * f12)
                    .color(203.0f / 255.0f, 203.0f / 255.0f, 192.0f / 255.0f, 1.0f).endVertex()
            }

            tessellator.draw()
            tessellator.getBuffer().setTranslation(0.0, 0.0, 0.0)
            GL11.glEnable(GL11.GL_LIGHTING)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
        }

        GL11.glTranslatef(x.toFloat(), y.toFloat(), z.toFloat())
        GL11.glRotatef(
            grapple.prevRotationYaw + (grapple.rotationYaw - grapple.prevRotationYaw) * partialTicks - 90.0f,
            0.0f,
            1.0f,
            0.0f
        )
        GL11.glRotatef(
            grapple.prevRotationPitch + (grapple.rotationPitch - grapple.prevRotationPitch) * partialTicks - 180,
            0.0f,
            0.0f,
            1.0f
        )
        GL11.glRotatef(
            grapple.prevRotationRoll + (grapple.rotationRoll - grapple.prevRotationRoll) * partialTicks,
            1.0f,
            0.0f,
            0.0f
        )

        updateModel()

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        ClientUtil.drawBakedModel((grappleModel)!!)

        //        this.bindEntityTexture(grapple);
//        ItemRendererGrappleHook.modelGrapple.renderAll(); TODO
        GL11.glPopMatrix()
    }

    //    protected ResourceLocation getEntityTexture(EntityGrapple grapple)
    //    {
    ////        return ItemRendererGrappleHook.grappleTexture;
    //    }
    override fun getEntityTexture(entity: EntityGrapple): ResourceLocation? {
        return ResourceLocation.parse("missing")
    }

    override fun shouldRender(
        entity: EntityGrapple,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        return entity.isInRangeToRender3d(camX, camY, camZ)
    }
}
