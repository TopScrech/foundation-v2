package micdoodle8.mods.galacticraft.planets.asteroids.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.power.ILaserNode
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.fml.relauncher.Side
import java.util.LinkedList
import kotlin.math.atan2

abstract class TileEntityBeamOutput : TileEntityAdvanced(), ILaserNode {
    @JvmField
    var nodeList: LinkedList<ILaserNode> = LinkedList<ILaserNode>()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var targetVec: BlockPos = BlockPos(-1, -1, -1)

    @JvmField
    var pitch: Float = 0f

    @JvmField
    var yaw: Float = 0f
    private var preLoadTarget: BlockPos? = null
    private var lastTargetVec = BlockPos(-1, -1, -1)

    override fun update() {
        if (this.preLoadTarget != null) {
            val tileAtTarget: TileEntity? = this.world.getTileEntity(this.preLoadTarget)

            if (tileAtTarget != null && tileAtTarget is ILaserNode) {
                this.setTarget(tileAtTarget as ILaserNode)
                this.preLoadTarget = null
            }
        }

        super.update()

        if (this.targetVec != this.lastTargetVec) {
            this.markDirty()
        }

        this.lastTargetVec = this.targetVec

        if (this.world.isRemote) {
            this.updateOrientation()
        } else if (this.targetVec.getX() == -1 && this.targetVec.getY() == -1 && this.targetVec.getZ() == -1) {
            this.initiateReflector()
        }
    }

    override fun invalidate() {
        super.invalidate()
        this.invalidateReflector()
    }

    override fun onLoad() {
    }

    override fun onChunkUnload() {
        this.invalidateReflector()
    }

    fun invalidateReflector() {
        for (node: ILaserNode in this.nodeList) {
            node.removeNode(this)
        }

        this.nodeList.clear()
    }

    fun initiateReflector() {
        this.nodeList.clear()

        val chunkXMin: Int = this.getPos().getX() - 15 shr 4
        val chunkZMin: Int = this.getPos().getZ() - 15 shr 4
        val chunkXMax: Int = this.getPos().getX() + 15 shr 4
        val chunkZMax: Int = this.getPos().getZ() + 15 shr 4

        for (cX: Int in chunkXMin..chunkXMax) {
            for (cZ: Int in chunkZMin..chunkZMax) {
                if (this.world.getChunkProvider().getLoadedChunk(cX, cZ) != null) {
                    val chunk: Chunk = this.world.getChunk(cX, cZ)

                    for (obj: Any? in chunk.getTileEntityMap().values) {
                        if (obj !== this && obj is ILaserNode) {
                            val deltaPos: BlockVec3 = BlockVec3(this).subtract(BlockVec3((obj.getTile())!!))

                            if (deltaPos.x < 16 && deltaPos.y < 16 && deltaPos.z < 16) {
                                val laserNode: ILaserNode = obj

                                if (this.canConnectTo(laserNode) && laserNode.canConnectTo(this)) {
                                    this.addNode(laserNode)
                                    laserNode.addNode(this)
                                }
                            }
                        }
                    }
                }
            }
        }

        this.setTarget(this.nodeList.peekFirst())
    }

    override fun addNode(node: ILaserNode?) {
        var index: Int = -1

        for (i: Int in this.nodeList.indices) {
            if (BlockVec3((this.nodeList.get(i).getTile())!!) == BlockVec3(((node)!!.getTile())!!)) {
                index = i
                break
            }
        }

        if (index != -1) {
            this.nodeList.set(index, (node)!!)
            return
        }

        if (this.nodeList.isEmpty()) {
            this.nodeList.add((node)!!)
        } else {
            var nodeCompare: Int = this.nodeList.get(0).compareTo(node, BlockVec3(this))

            if (nodeCompare <= 0) {
                this.nodeList.addFirst(node)
                return
            }

            nodeCompare = this.nodeList.get(this.nodeList.size - 1).compareTo(node, BlockVec3(this))

            if (nodeCompare >= 0) {
                this.nodeList.addLast(node)
                return
            }

            index = 1

            while (index < this.nodeList.size) {
                index++
            }

            this.nodeList.add(index, (node)!!)
        }
    }

    override fun removeNode(node: ILaserNode?) {
        var index: Int = -1

        for (i: Int in this.nodeList.indices) {
            if (BlockVec3((this.nodeList.get(i).getTile())!!) == BlockVec3(((node)!!.getTile())!!)) {
                index = i
                break
            }
        }

        if (BlockVec3(((node)!!.getTile())!!) == BlockVec3(this.targetVec)) {
            if (index == 0) {
                if (this.nodeList.size > 1) {
                    this.setTarget(this.nodeList.get(index + 1))
                } else {
                    this.setTarget(null)
                }
            } else if (index > 0) {
                this.setTarget(this.nodeList.get(index - 1))
            } else {
                this.setTarget(null)
            }
        }

        if (index != -1) {
            this.nodeList.removeAt(index)
        }
    }

    fun updateOrientation() {
        if (this.getTarget() != null) {
            val direction: Vector3 =
                Vector3.subtract((this.getOutputPoint(false))!!, (this.getTarget()!!.getInputPoint())!!).normalize()
            this.pitch = -Vector3.getAngle(Vector3(-direction.x, -direction.y, -direction.z), Vector3(0.0, 1.0, 0.0))
                .toFloat() * Constants.RADIANS_TO_DEGREES + 90
            this.yaw = -(atan2(direction.z, direction.x) * Constants.RADIANS_TO_DEGREES).toFloat() + 90
        }
    }

    override fun getTile(): TileEntity {
        return this
    }

    override fun compareTo(otherNode: ILaserNode?, origin: BlockVec3?): Int {
        val thisDistance: Int = BlockVec3(this).subtract((origin)!!).getMagnitudeSquared()
        val otherDistance: Int = BlockVec3(((otherNode)!!.getTile())!!).subtract((origin)!!).getMagnitudeSquared()

        if (thisDistance < otherDistance) {
            return 1
        } else if (thisDistance > otherDistance) {
            return -1
        }

        return 0
    }

    fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (this.nodeList.size > 1) {
            var index: Int = -1

            if (this.getTarget() != null) {
                for (i: Int in this.nodeList.indices) {
                    if (BlockVec3((this.nodeList.get(i).getTile())!!) == BlockVec3((this.getTarget()!!.getTile())!!)) {
                        index = i
                        break
                    }
                }
            }

            if (index == -1) {
                // This shouldn't happen, but just in case...
                this.initiateReflector()
            } else {
                index++
                index %= this.nodeList.size
                this.setTarget(this.nodeList.get(index))
                return true
            }
        }

        return false
    }

    override fun getTarget(): ILaserNode? {
        if (this.targetVec.getX() != -1 || this.targetVec.getY() != -1 || this.targetVec.getZ() != -1) {
            val tileAtTarget: TileEntity? = this.world.getTileEntity(this.targetVec)

            if (tileAtTarget != null && tileAtTarget is ILaserNode) {
                return tileAtTarget as ILaserNode
            }

            return null
        }

        return null
    }

    open fun setTarget(target: ILaserNode?) {
        if (target != null) {
            this.targetVec = (target.getTile())!!.getPos()
        } else {
            this.targetVec = BlockPos(-1, -1, -1)
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        if (nbt.getBoolean("HasTarget")) {
            this.preLoadTarget =
                BlockPos(nbt.getInteger("TargetX"), nbt.getInteger("TargetY"), nbt.getInteger("TargetZ"))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        nbt.setBoolean("HasTarget", this.getTarget() != null)

        if (this.getTarget() != null) {
            nbt.setInteger("TargetX", (this.getTarget()!!.getTile())!!.getPos().getX())
            nbt.setInteger("TargetY", (this.getTarget()!!.getTile())!!.getPos().getY())
            nbt.setInteger("TargetZ", (this.getTarget()!!.getTile())!!.getPos().getZ())
        }

        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }
}
