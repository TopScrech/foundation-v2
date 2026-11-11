package micdoodle8.mods.galacticraft.planets.asteroids.tile

import micdoodle8.mods.galacticraft.api.power.EnergySource.EnergySourceAdjacent
import micdoodle8.mods.galacticraft.api.power.EnergySource.EnergySourceWireless
import micdoodle8.mods.miccore.Annotations.NetworkedField

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.power.EnergySource
import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC
import micdoodle8.mods.galacticraft.api.power.ILaserNode
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorage
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorageTile
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource
import micdoodle8.mods.galacticraft.core.tile.ReceiverMode
import micdoodle8.mods.miccore.Annotations
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.min

open class TileEntityBeamReceiver : TileEntityBeamOutput(), IEnergyHandlerGC, ILaserNode {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var facing: EnumFacing? = null
    private var preLoadFacing = -1
    private val maxRate = 1500f
    private val storage = EnergyStorage(
        10 * maxRate,
        maxRate
    ) //In broken circuits, Beam Receiver will accept energy for 0.5s (15000gJ max) then stop

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var modeReceive: Int = ReceiverMode.UNDEFINED.ordinal

    @JvmField
    var color: Vector3 = Vector3(0.0, 1.0, 0.0)

    override fun update() {
        super.update()

        if (this.preLoadFacing != -1) {
            this.setFacing(EnumFacing.from3DDataValue(this.preLoadFacing))
            this.preLoadFacing = -1
        }

        if (!this.world.isRemote) {
            if (this.getTarget() != null && this.modeReceive == ReceiverMode.EXTRACT.ordinal && this.facing != null) {
                val tile: TileEntity? = this.getAttachedTile()

                if (tile is TileBaseUniversalElectricalSource) {
                    //GC energy source
                    val electricalTile: TileBaseUniversalElectricalSource = tile

                    if (electricalTile.storage.getEnergyStoredGC() > 0) {
                        val source: EnergySource.EnergySourceAdjacent =
                            EnergySource.EnergySourceAdjacent(EnumFacing.from3DDataValue(this.facing!!.getIndex() xor 1))
                        val toSend: Float =
                            min(electricalTile.storage.getMaxExtract(), electricalTile.storage.getEnergyStoredGC())
                        val transmitted: Float = this.getTarget()!!.receiveEnergyGC(
                            EnergySource.EnergySourceWireless(Lists.newArrayList<ILaserNode?>(this as ILaserNode)),
                            toSend,
                            false
                        )
                        electricalTile.extractEnergyGC(source, transmitted, false)
                    }
                } else if (tile !is EnergyStorageTile && tile !is TileBaseConductor)  //Another mod's energy source
                //But don't use other mods methods to connect Beam Receivers to GC's own wires or machines
                {
                    val availableToSend: Float =
                        EnergyUtil.otherModsEnergyExtract(tile, this.facing, this.maxRate, true)
                    if (availableToSend > 0f) {
                        val transmitted: Float = this.getTarget()!!.receiveEnergyGC(
                            EnergySource.EnergySourceWireless(Lists.newArrayList<ILaserNode?>(this as ILaserNode)),
                            availableToSend,
                            false
                        )
                        EnergyUtil.otherModsEnergyExtract(tile, this.facing, transmitted, false)
                    }
                }
            } else if (this.modeReceive == ReceiverMode.RECEIVE.ordinal && this.storage.getEnergyStoredGC() > 0) {
                //One Beam Receiver might be powered by multiple transmitters - allow for 5 at maximum transfer rate
                val maxTransfer: Float = min(this.storage.getEnergyStoredGC(), maxRate * 5)

                if (maxTransfer < 0.01f)  //Stop updating this when de minimis energy remains
                {
                    this.storage.extractEnergyGCnoMax(maxTransfer, false)
                } else {
                    val tileAdj: TileEntity? = this.getAttachedTile()

                    if (tileAdj is TileBaseUniversalElectrical) {
                        val electricalTile: TileBaseUniversalElectrical = tileAdj
                        val source: EnergySource.EnergySourceAdjacent =
                            EnergySource.EnergySourceAdjacent(this.facing!!.getOpposite())
                        this.storage.extractEnergyGCnoMax(
                            electricalTile.receiveEnergyGC(source, maxTransfer, false),
                            false
                        )
                    } else if (tileAdj !is EnergyStorageTile && tileAdj !is TileBaseConductor)  //Dont use other mods methods to connect Beam Receivers to GC's own wires or machines
                    {
                        val otherModTransferred: Float =
                            EnergyUtil.otherModsEnergyTransfer(tileAdj, this.facing, maxTransfer, false)
                        if (otherModTransferred > 0f) {
                            this.storage.extractEnergyGCnoMax(otherModTransferred, false)
                        }
                    }
                }
            }
        }
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

    override fun getInputPoint(): Vector3 {
        val headVec: Vector3 =
            Vector3(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5)
        if (this.facing != null) {
            headVec.x += ((this.facing)!!.getStepX() * 0.1f)!!
            headVec.y += ((this.facing)!!.getStepY() * 0.1f)!!
            headVec.z += ((this.facing)!!.getStepZ() * 0.1f)!!
        }
        return headVec
    }

    override fun getOutputPoint(offset: Boolean): Vector3 {
        val headVec: Vector3 =
            Vector3(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5)
        if (this.facing != null) {
            headVec.x += ((this.facing)!!.getStepX() * 0.1f)!!
            headVec.y += ((this.facing)!!.getStepY() * 0.1f)!!
            headVec.z += ((this.facing)!!.getStepZ() * 0.1f)!!
        }
        return headVec
    }

    override fun getTile(): TileEntity {
        return this
    }

    open fun getAttachedTile(): TileEntity? {
        if (this.facing == null) {
            return null
        }

        val tile: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, (this.facing)!!)

        if (tile == null || tile.isRemoved()) {
            this.setFacing(null)
        }

        if (tile is IConductor) {
            this.setFacing(null /* TODO */)
            return null
        }

        if (tile is EnergyStorageTile) {
            val attachedStorage: EnergyStorage = tile.storage
            this.storage.setCapacity(attachedStorage.getCapacityGC() - attachedStorage.getEnergyStoredGC())
            this.storage.setMaxExtract(attachedStorage.getMaxExtract())
            this.storage.setMaxReceive(attachedStorage.getMaxReceive())
        }

        return tile
    }

    override fun receiveEnergyGC(from: EnergySource?, amount: Float, simulate: Boolean): Float {
        if (this.modeReceive != ReceiverMode.RECEIVE.ordinal) {
            return 0f
        }

        this.getAttachedTile()

        if (this.facing == null) {
            return 0f
        }

        //		if (received < amount)
        //		{
        //			if (tile instanceof EnergyStorageTile)
        //			{
        //				received += ((EnergyStorageTile) tile).storage.receiveEnergyGC(amount - received, simulate);
        //			}
        //		}
        return this.storage.receiveEnergyGC(amount, simulate)
    }

    override fun extractEnergyGC(from: EnergySource?, amount: Float, simulate: Boolean): Float {
        if (this.modeReceive != ReceiverMode.EXTRACT.ordinal) {
            return 0f
        }

        val tile: TileEntity? = this.getAttachedTile()

        if (this.facing == null) {
            return 0f
        }

        var extracted: Float = this.storage.extractEnergyGC(amount, simulate)

        if (extracted < amount) {
            if (tile is EnergyStorageTile) {
                extracted += tile.storage.extractEnergyGC(amount - extracted, simulate)
            }
        }

        return extracted
    }

    override fun getEnergyStoredGC(from: EnergySource?): Float {
        val tile: TileEntity? = this.getAttachedTile()

        if (this.facing == null) {
            return 0f
        }

        return this.storage.getEnergyStoredGC()
    }

    override fun getMaxEnergyStoredGC(from: EnergySource?): Float {
        val tile: TileEntity? = this.getAttachedTile()

        if (this.facing == null) {
            return 0f
        }

        return this.storage.getCapacityGC()
    }

    override fun nodeAvailable(from: EnergySource?): Boolean {
        val tile: TileEntity? = this.getAttachedTile()

        return this.facing != null
    }

    open fun setFacing(newDirection: EnumFacing?) {
        if (newDirection != this.facing) {
            if (newDirection == null) {
                this.modeReceive = ReceiverMode.UNDEFINED.ordinal
            } else {
                val tile: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, newDirection)

                if (tile == null) {
                    this.modeReceive = ReceiverMode.UNDEFINED.ordinal
                } else if (tile is EnergyStorageTile) {
                    val mode: ReceiverMode? = tile.getModeFromDirection(newDirection.getOpposite())

                    if (mode != null) {
                        this.modeReceive = mode.ordinal
                    } else {
                        this.modeReceive = ReceiverMode.UNDEFINED.ordinal
                    }
                } else if (EnergyUtil.otherModCanReceive(tile, newDirection.getOpposite())) {
                    this.modeReceive = ReceiverMode.RECEIVE.ordinal
                } else if (EnergyUtil.otherModCanProduce(tile, newDirection.getOpposite())) {
                    this.modeReceive = ReceiverMode.EXTRACT.ordinal
                }
            }
        }

        this.facing = newDirection
    }

    override fun canConnectTo(laserNode: ILaserNode?): Boolean {
        if (this.modeReceive != ReceiverMode.UNDEFINED.ordinal && this.color == (laserNode)!!.getColor()) {
            if (laserNode is TileEntityBeamReceiver) {
                return laserNode.modeReceive != this.modeReceive
            }

            return true
        }

        return false
    }

    override fun getColor(): Vector3 {
        return Vector3(0.0, 1.0, 0.0)
    }

    override fun getTarget(): ILaserNode? {
        if (this.modeReceive == ReceiverMode.EXTRACT.ordinal) {
            return super.getTarget()
        }

        return null
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.preLoadFacing = nbt.getIntOr("FacingSide", 0)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("FacingSide", if (this.facing == null) this.preLoadFacing else this.facing!!.ordinal)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
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
