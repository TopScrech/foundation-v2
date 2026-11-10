package micdoodle8.mods.galacticraft.planets.mars.client.render.entity

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.client.model.ModelCreeperBoss
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.pow

open class RenderCreeperBoss constructor(renderManager: RenderManager) :
    RenderLiving<EntityCreeperBoss>(renderManager, ModelCreeperBoss(), 1.0f) {
    private val creeperModel: ModelBase = ModelCreeperBoss(2.0f)

    override fun getEntityTexture(entity: EntityCreeperBoss): ResourceLocation? {
        return creeperTexture
    }

    //    protected int func_27006_a(EntityCreeperBoss par1EntityCreeper, int par2, float par3)
    //    {
    //        if (par1EntityCreeper.headsRemaining == 1)
    //        {
    //            if (par2 == 1)
    //            {
    //                final float var4 = par1EntityCreeper.ticksExisted + par3;
    //                this.bindTexture(RenderCreeperBoss.powerTexture);
    //                GL11.glMatrixMode(GL11.GL_TEXTURE);
    //                GL11.glLoadIdentity();
    //                final float var5 = var4 * 0.01F;
    //                final float var6 = var4 * 0.01F;
    //                GL11.glTranslatef(var5, var6, 0.0F);
    //                this.setRenderPassModel(this.creeperModel);
    //                GL11.glMatrixMode(GL11.GL_MODELVIEW);
    //                GL11.glEnable(GL11.GL_BLEND);
    //                final float var7 = 0.5F;
    //                GL11.glColor4f(var7, var7, var7, 1.0F);
    //                GL11.glDisable(GL11.GL_LIGHTING);
    //                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
    //                return 1;
    //            }
    //
    //            if (par2 == 2)
    //            {
    //                GL11.glMatrixMode(GL11.GL_TEXTURE);
    //                GL11.glLoadIdentity();
    //                GL11.glMatrixMode(GL11.GL_MODELVIEW);
    //                GL11.glEnable(GL11.GL_LIGHTING);
    //                GL11.glDisable(GL11.GL_BLEND);
    //            }
    //        }
    //
    //        return -1;
    //    }
    override fun preRenderCallback(par1EntityLiving: EntityCreeperBoss, par2: Float) {
        GL11.glScalef(4.0f, 4.0f, 4.0f)
        GL11.glRotatef(
            (par1EntityLiving.deathTicks.toDouble().pow(2.0) / 5.0f + (par1EntityLiving.deathTicks.toDouble()
                .pow(2.0) / 5.0f - (par1EntityLiving.deathTicks - 1).toDouble().pow(2.0) / 5.0f) * par2).toFloat(),
            0.0f,
            1.0f,
            0.0f
        )
    }

    override fun getColorMultiplier(par1EntityLivingBase: EntityCreeperBoss, par2: Float, par3: Float): Int {
        return super.getColorMultiplier(par1EntityLivingBase, par2, par3)
    } //    @Override
    //    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    //    {
    //        return this.func_27006_a((EntityCreeperBoss) par1EntityLivingBase, par2, par3);
    //    }
    //
    //    @Override
    //    protected int inheritRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    //    {
    //        return -1;
    //    }

    companion object {
        private val creeperTexture = ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/model/creeper.png")
        private val powerTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/power.png")
    }
}
