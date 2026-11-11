package micdoodle8.mods.galacticraft.planets.asteroids.tile

import micdoodle8.mods.galacticraft.api.power.EnergySource.EnergySourceWireless

import micdoodle8.mods.galacticraft.api.power.EnergySource
import micdoodle8.mods.galacticraft.api.power.ILaserNode
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorage
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.cos
import kotlin.math.sin

open class TileEntityBeamReflector : TileEntityBeamOutput(), ILaserNode {
    @JvmField
    var color: Vector3 = Vector3(0.0, 1.0, 0.0)
    private val storage = EnergyStorage(10f, 1f)

    //    @Override
    //    public void update()
    //    {
    //        super.update();
    //    }
    override fun getInputPoint(): Vector3 {
        val distance: Float = 0.15f
        val deviation: Vector3 = Vector3(
            sin(Math.toRadians((this.yaw - 180).toDouble())) * distance,
            0.0,
            cos(Math.toRadians((this.yaw - 180).toDouble())) * distance
        )
        val headVec: Vector3 =
            Vector3(this.getPos().getX() + 0.5, this.getPos().getY() + 1.13228 / 2.0, this.getPos().getZ() + 0.5)
        headVec.translate(deviation.clone().invert())
        return headVec
    }

    override fun getOutputPoint(offset: Boolean): Vector3 {
        return Vector3(this.getPos().getX() + 0.5, this.getPos().getY() + 1.13228 / 2.0, this.getPos().getZ() + 0.5)
    }

    override fun getPacketRange(): Double {
        return 24.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    override fun getColor(): Vector3 {
        return this.color
    }

    override fun canConnectTo(laserNode: ILaserNode?): Boolean {
        return this.color == (laserNode)!!.getColor()
    }

    override fun receiveEnergyGC(from: EnergySource?, amount: Float, simulate: Boolean): Float {
        if (this.getTarget() != null) {
            if (from is EnergySource.EnergySourceWireless) {
                if ((from.nodes)!!.contains(this.getTarget())) {
                    return 0f
                }

                (from.nodes)!!.add(this)

                return ((this.getTarget())!!.receiveEnergyGC(from, amount, simulate))!!
            } else {
                return 0f
            }
        }

        return this.storage.receiveEnergyGC(amount, simulate)
    }

    override fun extractEnergyGC(from: EnergySource?, amount: Float, simulate: Boolean): Float {
        return 0f
    }

    override fun nodeAvailable(from: EnergySource?): Boolean {
        return from is EnergySource.EnergySourceWireless
    }

    override fun getEnergyStoredGC(from: EnergySource?): Float {
        return this.storage.getEnergyStoredGC()
    }

    override fun getMaxEnergyStoredGC(from: EnergySource?): Float {
        return this.storage.getCapacityGC()
    }

    override fun setTarget(target: ILaserNode?) {
        super.setTarget(target)
    }

    private var renderAABB: AxisAlignedBB? = null

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(1, 2, 1))
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_SHORT
    }
}
