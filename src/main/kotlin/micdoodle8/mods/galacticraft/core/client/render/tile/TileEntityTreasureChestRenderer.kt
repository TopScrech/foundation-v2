package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.block.ModelTreasureChest
import micdoodle8.mods.galacticraft.core.client.model.block.ModelTreasureChestLarge
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

@SideOnly(Side.CLIENT)
open class TileEntityTreasureChestRenderer : TileEntitySpecialRenderer<TileEntityTreasureChest>() {
    private val chestModel = ModelTreasureChest()

    /**
     * Renders the TileEntity for the chest at a position.
     */
    override fun render(
        chest: TileEntityTreasureChest,
        par2: Double,
        par4: Double,
        par6: Double,
        partialTickTime: Float,
        par9: Int,
        alpha: Float
    ) {
        val var9: Int

        if (!chest.hasWorld()) {
            var9 = 0
        } else {
            var9 = chest.getBlockMetadata()
        }

        var var14: ModelTreasureChest? = null
        val var14b: ModelTreasureChestLarge? = null

        var14 = this.chestModel
        this.bindTexture(treasureChestTexture)

        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glTranslatef(par2.toFloat(), par4.toFloat() + 1.0f, par6.toFloat() + 1.0f)
        GL11.glScalef(1.0f, -1.0f, -1.0f)
        GL11.glTranslatef(0.5f, 0.5f, 0.5f)
        var var11: Short = 0

        if (var9 == 2) {
            var11 = 180
        }

        if (var9 == 3) {
            var11 = 0
        }

        if (var9 == 4) {
            var11 = 90
        }

        if (var9 == 5) {
            var11 = -90
        }

        GL11.glRotatef(var11.toFloat(), 0.0f, 1.0f, 0.0f)
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f)
        var var12: Float = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTickTime

        var var13: Float

        var12 = 1.0f - var12
        var12 = 1.0f - var12 * var12 * var12

        if (var14 != null) {
            var14.chestLid.rotateAngleX = -(var12 * Math.PI.toFloat() / 4.0f)
            var14.renderAll(!chest.locked)
        }


        //Note: currently var14b is always null - no large Treasure Chest model
//        if (var14b != null)
//        {
//            var14b.chestLid.rotateAngleX = -(var12 * (float) Math.PI / 4.0F);
//            var14b.renderAll(!chest.locked);
//        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPopMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    companion object {
        private val treasureChestTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/treasure.png")
    }
}
