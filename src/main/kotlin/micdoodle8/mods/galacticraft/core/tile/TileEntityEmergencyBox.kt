package micdoodle8.mods.galacticraft.core.tile

import foundation.block.getStateFromMeta

import foundation.persistence.listOfType

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ItemEmergencyKit
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.entity.EntityCreature
import net.minecraft.entity.ai.RandomPositionGenerator
import net.minecraft.entity.monster.IMob
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.pathfinding.PathNavigate
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.items.ItemHandlerHelper
import java.lang.Exception
import java.util.ArrayList
import java.util.HashSet

open class TileEntityEmergencyBox : TileEntity(), ITickable, IPacketReceiver {
    @JvmField
    var angleA: Float = 0f

    @JvmField
    var angleB: Float = 0f

    @JvmField
    var angleC: Float = 0f

    @JvmField
    var angleD: Float = 0f

    @JvmField
    var lastAngleA: Float = 0f

    @JvmField
    var lastAngleB: Float = 0f

    @JvmField
    var lastAngleC: Float = 0f

    @JvmField
    var lastAngleD: Float = 0f

    private var openN = false
    private var openW = false
    private var openS = false
    private var openE = false
    private var cooldown = 0

    private val airToRestore = HashSet<BlockVec3>()
    private var activated = false
    private var vec3Centre: Vec3d? = null
    private var thisVec3: Vec3d? = null
    private var mobsAABB: AxisAlignedBB? = null

    override fun update() {
        if (!this.activated) {
            this.activated = true
            this.setLightBlocks()
            this.thisVec3 =
                Vec3d(this.getPos().getX().toDouble(), this.getPos().getY().toDouble(), this.getPos().getZ().toDouble())
            this.vec3Centre = Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5)
            this.mobsAABB = AxisAlignedBB(
                (this.pos.getX() - 14).toDouble(),
                (this.pos.getY() - 7).toDouble(),
                (this.pos.getZ() - 14).toDouble(),
                (this.pos.getX() + 14).toDouble(),
                (this.pos.getY() + 7).toDouble(),
                (this.pos.getZ() + 14).toDouble()
            )
        }

        if (this.world.isRemote) {
            if (this.openN && this.angleA < 90f) {
                this.lastAngleA = this.angleA
                this.angleA += SPEED
                if (this.angleA > 90f) this.angleA = 90f
            }
            if (this.openW && this.angleB < 90f) {
                this.lastAngleB = this.angleB
                this.angleB += SPEED
                if (this.angleB > 90f) this.angleB = 90f
            }
            if (this.openS && this.angleC < 90f) {
                this.lastAngleC = this.angleC
                this.angleC += SPEED
                if (this.angleC > 90f) this.angleC = 90f
            }
            if (this.openE && this.angleD < 90f) {
                this.lastAngleD = this.angleD
                this.angleD += SPEED
                if (this.angleD > 90f) this.angleD = 90f
            }

            if (!this.openN && this.angleA > 0f) {
                this.lastAngleA = this.angleA
                this.angleA -= SPEED
                if (this.angleA < 0f) this.angleA = 0f
            }
            if (!this.openW && this.angleB > 0f) {
                this.lastAngleB = this.angleB
                this.angleB -= SPEED
                if (this.angleB < 0f) this.angleB = 0f
            }
            if (!this.openS && this.angleC > 0f) {
                this.lastAngleC = this.angleC
                this.angleC -= SPEED
                if (this.angleC < 0f) this.angleC = 0f
            }
            if (!this.openE && this.angleD > 0f) {
                this.lastAngleD = this.angleD
                this.angleD -= SPEED
                if (this.angleD < 0f) this.angleD = 0f
            }
        } else {
            if (this.cooldown > 0) {
                this.cooldown--
            }

            var updateRequired: Boolean = false
            if (this.openN) {
                var clash: Boolean = false
                val testPos: BlockPos = this.pos.north(1)
                val bs: IBlockState = this.world.getBlockState(testPos)
                if (bs.getBlock() !is BlockAir) {
                    if (bs.isFullBlock()) {
                        clash = true
                    } else {
                        val check: AxisAlignedBB = AxisAlignedBB(0.125, 0.125, 11 / 16.0, 0.875, 0.875, 1.0)
                        val neighbour: AxisAlignedBB? = bs.getCollisionBoundingBox(this.world, testPos)
                        if (neighbour != null) {
                            clash = check.intersects(neighbour)
                        }
                    }
                }
                if (clash) {
                    this.openN = false
                    updateRequired = true
                }
            }
            if (this.openS) {
                var clash: Boolean = false
                val testPos: BlockPos = this.pos.south(1)
                val bs: IBlockState = this.world.getBlockState(testPos)
                if (bs.getBlock() !is BlockAir) {
                    if (bs.isFullBlock()) {
                        clash = true
                    } else {
                        val check: AxisAlignedBB = AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 5 / 16.0)
                        val neighbour: AxisAlignedBB? = bs.getCollisionBoundingBox(this.world, testPos)
                        if (neighbour != null) {
                            clash = check.intersects(neighbour)
                        }
                    }
                }
                if (clash) {
                    this.openS = false
                    updateRequired = true
                }
            }
            if (this.openW) {
                var clash: Boolean = false
                val testPos: BlockPos = this.pos.west(1)
                val bs: IBlockState = this.world.getBlockState(testPos)
                if (bs.getBlock() !is BlockAir) {
                    if (bs.isFullBlock()) {
                        clash = true
                    } else {
                        val check: AxisAlignedBB = AxisAlignedBB(11 / 16.0, 0.125, 0.125, 1.0, 0.875, 0.875)
                        val neighbour: AxisAlignedBB? = bs.getCollisionBoundingBox(this.world, testPos)
                        if (neighbour != null) {
                            clash = check.intersects(neighbour)
                        }
                    }
                }
                if (clash) {
                    this.openW = false
                    updateRequired = true
                }
            }
            if (this.openE) {
                var clash: Boolean = false
                val testPos: BlockPos = this.pos.east(1)
                val bs: IBlockState = this.world.getBlockState(testPos)
                if (bs.getBlock() !is BlockAir) {
                    if (bs.isFullBlock()) {
                        clash = true
                    } else {
                        val check: AxisAlignedBB = AxisAlignedBB(0.0, 0.125, 0.125, 5 / 16.0, 0.875, 0.875)
                        val neighbour: AxisAlignedBB? = bs.getCollisionBoundingBox(this.world, testPos)
                        if (neighbour != null) {
                            clash = check.intersects(neighbour)
                        }
                    }
                }
                if (clash) {
                    this.openE = false
                    updateRequired = true
                }
            }

            if (updateRequired) {
                this.updateClients()
            }

            if (this.world.rand.nextInt(15) == 0) {
                this.scareMobs()
            }
        }
    }

    private fun scareMobs() {
        val moblist: MutableList<Entity?> = this.world.getEntitiesInAABBexcluding(null, mobsAABB, IMob.MOB_SELECTOR)
        if (!moblist.isEmpty()) {
            for (entry: Entity? in moblist) {
                if (!(entry is EntityCreature && entry is IEntityBreathable)) {
                    continue
                }
                val mob: EntityCreature = entry as EntityCreature
                val nav: PathNavigate = mob.getNavigator()
                if (nav == null) {
                    continue
                }

                var vecNewTarget: Vec3d? = RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, 12, 5, vec3Centre)
                if (vecNewTarget == null) {
                    vecNewTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, 14, 7, vec3Centre)
                    if (vecNewTarget == null) continue
                }
                val distanceNew: Double = vecNewTarget.squareDistanceTo(thisVec3)
                val distanceCurrent: Double = thisVec3!!.squareDistanceTo(Vec3d(mob.posX, mob.posY, mob.posZ))
                if (distanceNew > distanceCurrent) {
                    var vecOldTarget: Vec3d? = null
                    if (nav.getPath() != null && !nav.getPath()!!.isFinished()) {
                        vecOldTarget = nav.getPath()!!.getPosition(mob)
                    }
                    if (vecOldTarget == null || distanceCurrent > vecOldTarget.squareDistanceTo(thisVec3)) {
                        nav.tryMoveToXYZ(vecNewTarget.x, vecNewTarget.y, vecNewTarget.z, 1.1)
                    }
                }
            }
        }
    }

    open fun getAngleA(f: Float): Float {
        var result: Float = this.angleA + (this.angleA - this.lastAngleA) * f
        if (result > 90f) result = 90f
        if (result < 0f) result = 0f
        return result
    }

    open fun getAngleB(f: Float): Float {
        var result: Float = this.angleB + (this.angleB - this.lastAngleB) * f
        if (result > 90f) result = 90f
        if (result < 0f) result = 0f
        return result
    }

    open fun getAngleC(f: Float): Float {
        var result: Float = this.angleC + (this.angleC - this.lastAngleC) * f
        if (result > 90f) result = 90f
        if (result < 0f) result = 0f
        return result
    }

    open fun getAngleD(f: Float): Float {
        var result: Float = this.angleD + (this.angleD - this.lastAngleD) * f
        if (result > 90f) result = 90f
        if (result < 0f) result = 0f
        return result
    }

    open fun click(player: EntityPlayer, side: EnumFacing, kitted: Boolean) {
        do {
when (side) {
            EnumFacing.NORTH -> {
                if (this.openN && this.cooldown == 0) {
                    if (kitted) {
                        ItemHandlerHelper.giveItemToPlayer(player, ItemStack(GCItems.emergencyKit), 0)
                        this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.defaultBlockState(), 3)
                        break
                    } else {
                        val stack: ItemStack = player.inventory.getSelectedItem()
                        if (!stack.isEmpty() && stack.getItem() is ItemEmergencyKit) {
                            player.inventory.setItem(player.inventory.selectedSlot, ItemStack.EMPTY)
                            this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.getStateFromMeta(1), 3)
                            this.openW = false
                            this.openS = false
                            this.openE = false
                        }
                    }
                }
                this.cooldown = 12
                this.openN = !this.openN
                this.updateClients()
            }

            EnumFacing.WEST -> {
                if (this.openW && this.cooldown == 0) {
                    if (kitted) {
                        ItemHandlerHelper.giveItemToPlayer(player, ItemStack(GCItems.emergencyKit), 0)
                        this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.defaultBlockState(), 3)
                        break
                    } else {
                        val stack: ItemStack = player.inventory.getSelectedItem()
                        if (!stack.isEmpty() && stack.getItem() is ItemEmergencyKit) {
                            player.inventory.setItem(player.inventory.selectedSlot, ItemStack.EMPTY)
                            this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.getStateFromMeta(1), 3)
                            this.openN = false
                            this.openS = false
                            this.openE = false
                        }
                    }
                }
                this.cooldown = 12
                this.openW = !this.openW
                this.updateClients()
            }

            EnumFacing.SOUTH -> {
                if (this.openS && this.cooldown == 0) {
                    if (kitted) {
                        ItemHandlerHelper.giveItemToPlayer(player, ItemStack(GCItems.emergencyKit), 0)
                        this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.defaultBlockState(), 3)
                        break
                    } else {
                        val stack: ItemStack = player.inventory.getSelectedItem()
                        if (!stack.isEmpty() && stack.getItem() is ItemEmergencyKit) {
                            player.inventory.setItem(player.inventory.selectedSlot, ItemStack.EMPTY)
                            this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.getStateFromMeta(1), 3)
                            this.openN = false
                            this.openW = false
                            this.openE = false
                        }
                    }
                }
                this.cooldown = 12
                this.openS = !this.openS
                this.updateClients()
            }

            EnumFacing.EAST -> {
                if (this.openE && this.cooldown == 0) {
                    if (kitted) {
                        ItemHandlerHelper.giveItemToPlayer(player, ItemStack(GCItems.emergencyKit), 0)
                        this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.defaultBlockState(), 3)
                        break
                    } else {
                        val stack: ItemStack = player.inventory.getSelectedItem()
                        if (!stack.isEmpty() && stack.getItem() is ItemEmergencyKit) {
                            player.inventory.setItem(player.inventory.selectedSlot, ItemStack.EMPTY)
                            this.world.setBlock(this.pos, (GCBlocks.emergencyBox)!!.getStateFromMeta(1), 3)
                            this.openN = false
                            this.openW = false
                            this.openS = false
                        }
                    }
                }
                this.cooldown = 12
                this.openE = !this.openE
                this.updateClients()
            }

            else -> {}
        }
} while (false)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val data: Int = nbt.getIntOr("open", 0)
        this.openN = (data and 1) == 1
        this.openW = (data and 2) == 2
        this.openS = (data and 4) == 4
        this.openE = (data and 8) == 8
        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            this.airToRestore.clear()
            val airBlocks: NBTTagList = nbt.listOfType("air", 10)
            if (airBlocks.size > 0) {
                for (j: Int in airBlocks.size - 1 downTo 0) {
                    val tag1: NBTTagCompound = airBlocks.getCompoundOrEmpty(j)
                    if (tag1 != null) {
                        this.airToRestore.add(BlockVec3.readFromNBT(tag1))
                    }
                }
            }
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        val data: Int =
            (if (this.openN) 1 else 0) + (if (this.openW) 2 else 0) + (if (this.openS) 4 else 0) + (if (this.openE) 8 else 0)
        nbt.putInt("open", data)

        val airBlocks: NBTTagList = NBTTagList()
        for (vec: BlockVec3 in this.airToRestore) {
            val tag: NBTTagCompound = NBTTagCompound()
            vec.writeToNBT(tag)
            airBlocks.add(tag)
        }
        nbt.put("air", airBlocks)
        return nbt
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }

        val data: Int =
            (if (this.openN) 1 else 0) + (if (this.openW) 2 else 0) + (if (this.openS) 4 else 0) + (if (this.openE) 8 else 0)
        (sendData)!!.add(data.toByte())
        for (vec: BlockVec3? in this.airToRestore) {
            (sendData)!!.add(vec)
        }
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (this.world.isRemote) {
            try {
                val data: Int = (buffer)!!.readByte().toInt()
                this.openN = (data and 1) == 1
                this.openW = (data and 2) == 2
                this.openS = (data and 4) == 4
                this.openE = (data and 8) == 8
                while ((buffer)!!.readableBytes() >= 12) {
                    val x: Int = (buffer)!!.readInt()
                    val y: Int = (buffer)!!.readInt()
                    val z: Int = (buffer)!!.readInt()
                    this.airToRestore.add(BlockVec3(x, y, z))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onLoad() {
        if (this.world.isRemote) {
            //Request any networked information from server on first client update
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        }
    }

    private fun updateClients() {
        (GalacticraftCore.packetPipeline)!!.sendToAllAround(
            PacketDynamic(this),
            NetworkRegistry.TargetPoint(
                GCCoreUtil.getDimensionID(this.world),
                getPos().getX().toDouble(),
                getPos().getY().toDouble(),
                getPos().getZ().toDouble(),
                128.0
            )
        )
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.getBlock() !== newState.getBlock()
    }

    private fun brightenAir(blockpos: BlockPos, newState: IBlockState) {
        val chunk: Chunk = this.world.getChunk(blockpos)
        val oldState: IBlockState? = chunk.setBlockState(blockpos, newState)
        if (this.world.isRemote && oldState != null) {
            this.world.markAndNotifyBlock(blockpos, chunk, oldState, newState, 2)
        }
        //No block update on server - not necessary for changing air to air (also must not trigger a sealer edge check!)
        this.airToRestore.add(BlockVec3(blockpos))
        this.world.checkLightFor(EnumSkyBlock.BLOCK, blockpos)
        this.markDirty()
    }

    private fun setDarkerAir(vec: BlockVec3) {
        val blockpos: BlockPos = vec.toBlockPos()
        val b: Block = this.world.getBlockState(blockpos).getBlock()
        val newState: IBlockState
        if (b === GCBlocks.brightAir) {
            newState = Blocks.AIR.defaultBlockState()
        } else if (b === GCBlocks.brightBreatheableAir) {
            newState = (GCBlocks.breatheableAir)!!.defaultBlockState()
        } else {
            return
        }

        val chunk: Chunk = this.world.getChunk(blockpos)
        val oldState: IBlockState? = chunk.setBlockState(blockpos, newState)
        if (this.world.isRemote && oldState != null) this.world.markAndNotifyBlock(
            blockpos,
            chunk,
            oldState,
            newState,
            2
        )
        this.world.checkLightFor(EnumSkyBlock.BLOCK, blockpos)
    }

    private fun revertAir() {
        if (this.airToRestore.isEmpty()) return

        val index: Int = 0
        for (vec: BlockVec3 in this.airToRestore) {
            this.setDarkerAir(vec)
        }
        this.airToRestore.clear()
        this.markDirty()
    }

    override fun invalidate() {
        this.revertAir()
        super.invalidate()
    }

    private fun setLightBlocks() {
        this.setLightBlock(BlockPos(this.pos.getX() - 6, this.pos.getY(), this.pos.getZ()))
        this.setLightBlock(BlockPos(this.pos.getX() + 6, this.pos.getY(), this.pos.getZ()))
        this.setLightBlock(BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() - 6))
        this.setLightBlock(BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() + 6))
        this.setLightBlock(BlockPos(this.pos.getX() - 5, this.pos.getY(), this.pos.getZ() - 5))
        this.setLightBlock(BlockPos(this.pos.getX() - 5, this.pos.getY(), this.pos.getZ() + 5))
        this.setLightBlock(BlockPos(this.pos.getX() + 5, this.pos.getY(), this.pos.getZ() + 5))
        this.setLightBlock(BlockPos(this.pos.getX() + 5, this.pos.getY(), this.pos.getZ() - 5))
    }

    private fun setLightBlock(blockPos: BlockPos): Boolean {
        var blockPos: BlockPos = blockPos
        var bs: IBlockState = this.world.getBlockState(blockPos)
        if (bs.getBlock() === Blocks.AIR) {
            this.brightenAir(blockPos, (GCBlocks.brightAir)!!.defaultBlockState())
            return true
        } else if (bs.getBlock() === GCBlocks.breatheableAir) {
            this.brightenAir(blockPos, (GCBlocks.brightBreatheableAir)!!.defaultBlockState())
            return true
        }

        blockPos = blockPos.above(1)
        bs = this.world.getBlockState(blockPos)
        if (bs.getBlock() === Blocks.AIR) {
            this.brightenAir(blockPos, (GCBlocks.brightAir)!!.defaultBlockState())
            return true
        } else if (bs.getBlock() === GCBlocks.breatheableAir) {
            this.brightenAir(blockPos, (GCBlocks.brightBreatheableAir)!!.defaultBlockState())
            return true
        }

        return false
    }

    companion object {
        private const val SPEED = 4f
    }
}
