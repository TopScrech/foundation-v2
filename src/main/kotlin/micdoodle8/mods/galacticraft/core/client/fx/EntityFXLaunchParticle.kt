package micdoodle8.mods.galacticraft.core.client.fx

import java.util.LinkedList

import micdoodle8.mods.galacticraft.core.blocks.BlockGrating
import net.minecraft.block.BlockAir
import net.minecraft.block.state.IBlockState
import net.minecraft.client.particle.Particle
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

@SideOnly(Side.CLIENT)
abstract class EntityFXLaunchParticle(
    worldIn: World,
    xCoordIn: Double,
    yCoordIn: Double,
    zCoordIn: Double,
    xSpeedIn: Double,
    ySpeedIn: Double,
    zSpeedIn: Double
) : Particle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn) {
    override fun move(x: Double, y: Double, z: Double) {
        var x: Double = x
        var y: Double = y
        var z: Double = z
        val d0: Double = y
        val origX: Double = x
        val origZ: Double = z

        val list: MutableList<AxisAlignedBB> = this.getCollidingBoundingBoxes(this.getBoundingBox().expand(x, y, z))
        var axisalignedbb: AxisAlignedBB = this.getBoundingBox()

        for (blocker: AxisAlignedBB in list) {
            y = blocker.calculateYOffset(axisalignedbb, y)
        }
        axisalignedbb = axisalignedbb.offset(0.0, y, 0.0)
        if (y != d0) {
            this.motionY += (y - d0) * 1.15
            this.motionX *= 0.7
            this.motionZ *= 0.7
        }
        this.motionY -= 0.005

        for (blocker: AxisAlignedBB in list) {
            x = blocker.calculateXOffset(axisalignedbb, x)
        }
        axisalignedbb = axisalignedbb.offset(x, 0.0, 0.0)

        for (blocker: AxisAlignedBB in list) {
            z = blocker.calculateZOffset(axisalignedbb, z)
        }
        axisalignedbb = axisalignedbb.offset(0.0, 0.0, z)

        this.setBoundingBox(axisalignedbb)
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0
        this.posY = axisalignedbb.minY
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0
        this.onGround = false
    }

    /**
     * Simplified for performance: no colliding boxes for entities (most/all of the entities will be other launch particles)
     */
    fun getCollidingBoundingBoxes(bb: AxisAlignedBB): MutableList<AxisAlignedBB> {
        val list: MutableList<AxisAlignedBB> = LinkedList<AxisAlignedBB>()
        val w: World = this.world
        val xs: Int = MathHelper.floor(bb.minX) - 1
        val xe: Int = MathHelper.ceil(bb.maxX)
        val ys: Int = MathHelper.floor(bb.minY) - 1
        val ye: Int = MathHelper.ceil(bb.maxY)
        val zs: Int = MathHelper.floor(bb.minZ) - 1
        val ze: Int = MathHelper.ceil(bb.maxZ)
        val mutablePos: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()
        var iblockstate1: IBlockState
        var xends: Boolean
        var xzmiddle: Boolean

        for (x: Int in xs..xe) {
            xends = (x == xs || x == xe)
            for (z: Int in zs..ze) {
                if (xends) {
                    if (z == zs || z == ze) continue

                    xzmiddle = false
                } else {
                    xzmiddle = z > zs && z < ze
                }

                if (w.isBlockLoaded(mutablePos.setPos(x, 64, z))) {
                    for (y: Int in ys..ye) {
                        if (y != ys && y != ye || xzmiddle) {
                            mutablePos.setPos(x, y, z)
                            iblockstate1 = w.getBlockState(mutablePos)
                            if (iblockstate1.getBlock() !is BlockAir && iblockstate1.getBlock() !is BlockGrating) iblockstate1.addCollisionBoxToList(
                                w,
                                mutablePos,
                                bb,
                                list,
                                null,
                                false
                            )
                        }
                    }
                }
            }
        }

        return list
    }
}
