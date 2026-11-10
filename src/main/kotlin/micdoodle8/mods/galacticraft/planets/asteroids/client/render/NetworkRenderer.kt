package micdoodle8.mods.galacticraft.planets.asteroids.client.render

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamOutput
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11

object NetworkRenderer {
    @JvmStatic
    fun renderNetworks(world: World, partialTicks: Float) {
        val nodes: MutableList<TileEntityBeamOutput> = ArrayList<TileEntityBeamOutput>()

        for (o: Any? in ArrayList<TileEntity?>(world.loadedTileEntityList)) {
            if (o is TileEntityBeamOutput) {
                nodes.add(o)
            }
        }

        if (nodes.isEmpty()) {
            return
        }

        val tess: Tessellator = Tessellator.getInstance()
        val player: EntityPlayerSP = FMLClientHandler.instance().getClient().player
        val interpPosX: Double = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
        val interpPosY: Double = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
        val interpPosZ: Double = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks

        GL11.glDisable(GL11.GL_TEXTURE_2D)

        for (tileEntity: TileEntityBeamOutput in nodes) {
            if (tileEntity.getTarget() == null) {
                continue
            }

            GL11.glPushMatrix()

            val outputPoint: Vector3 = (tileEntity.getOutputPoint(true))!!
            val targetInputPoint: Vector3 = ((tileEntity.getTarget())!!.getInputPoint())!!

            val direction: Vector3 = Vector3.subtract(outputPoint, targetInputPoint)
            val directionLength: Float = direction.getMagnitude().toFloat()

            val posX: Float = (tileEntity.getPos().getX() - interpPosX).toFloat()
            val posY: Float = (tileEntity.getPos().getY() - interpPosY).toFloat()
            val posZ: Float = (tileEntity.getPos().getZ() - interpPosZ).toFloat()
            GL11.glTranslatef(posX, posY, posZ)

            GL11.glTranslatef(
                outputPoint.floatX() - tileEntity.getPos().getX(),
                outputPoint.floatY() - tileEntity.getPos().getY(),
                outputPoint.floatZ() - tileEntity.getPos().getZ()
            )
            GL11.glRotatef(tileEntity.yaw + 180, 0f, 1f, 0f)
            GL11.glRotatef(-tileEntity.pitch, 1f, 0f, 0f)
            GL11.glRotatef((tileEntity.ticks * 10).toFloat(), 0f, 0f, 1f)

            tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

            for (dir: EnumFacing in EnumFacing.VALUES) {
                tess.getBuffer()
                    .pos((dir.getXOffset() / 40.0f).toDouble(), (dir.getYOffset() / 40.0f).toDouble(), (dir.getZOffset() / 40.0f).toDouble())
                    .color(
                        (tileEntity.getColor())!!.floatX(),
                        (tileEntity.getColor())!!.floatY(),
                        (tileEntity.getColor())!!.floatZ(),
                        1.0f
                    ).endVertex()
                tess.getBuffer().pos(
                    (dir.getXOffset() / 40.0f).toDouble(),
                    (dir.getYOffset() / 40.0f).toDouble(),
                    (directionLength + dir.getZOffset() / 40.0f).toDouble()
                ).color(
                    (tileEntity.getColor())!!.floatX(),
                    (tileEntity.getColor())!!.floatY(),
                    (tileEntity.getColor())!!.floatZ(),
                    1.0f
                ).endVertex()
            }

            tess.draw()

            GL11.glPopMatrix()
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D)

        GL11.glColor4f(1f, 1f, 1f, 1f)
    }
}
