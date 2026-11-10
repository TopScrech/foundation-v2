package galaxyspace.core.client.render.tile

import micdoodle8.mods.galacticraft.core.client.model.block.ModelTreasureChest
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

@SideOnly(Side.CLIENT)
open class TileEntityTreasureChestRenderer constructor(private val treasureChestTexture: ResourceLocation) :
    TileEntitySpecialRenderer<TileEntityTreasureChest>() {
    private val chestModel = ModelTreasureChest()

    override fun render(
        chest: TileEntityTreasureChest,
        x: Double,
        y: Double,
        z: Double,
        par7: Float,
        par8: Int,
        alpha: Float
    ) {
        val var9: Int

        if (!chest.hasWorld()) {
            var9 = 0
        } else {
            var9 = chest.getBlockMetadata()
        }

        this.bindTexture(treasureChestTexture)

        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glTranslatef(x.toFloat(), y.toFloat() + 1.0f, z.toFloat() + 1.0f)
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
        var var12: Float = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * par8

        var12 = 1.0f - var12
        var12 = 1.0f - var12 * var12 * var12

        this.chestModel.chestLid.rotateAngleX = -(var12 * Math.PI.toFloat() / 4.0f)
        this.chestModel.renderAll(!chest.locked)

        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPopMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }
}
