package micdoodle8.mods.galacticraft.core.tile

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.blocks.BlockPlatform.EnumCorner

import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockPlatform
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import net.minecraft.block.BlockAir
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.LinkedList

open class TileEntityPlatform : TileEntity, ITickable {
    private var corner = 0
    private var detection: AxisAlignedBB? = null
    private var noCollide = false
    private var moving = false
    private var lightOn = false
    private var colorState = 0 //0 = green  1 = red
    private var colorTicks = 0
    private var renderAABB: AxisAlignedBB? = null
    private var lightA = 0
    private var lightB = 0
    private var deltaY = 0
    private var firstTickCheck = false

    constructor()

    constructor(meta: Int) {
        this.corner = if (meta > 4 || meta < 0) 0 else meta
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.corner = nbt.getIntOr("co", 0)
        if (this.corner != 0) {
            this.firstTickCheck = true
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("co", this.corner)
        return nbt
    }

    override fun update() {
        if (this.firstTickCheck && !this.world.isRemote) {
            this.firstTickCheck = !this.checkIntact()
        }

        if (this.corner == 0 && !this.world.isRemote) {
            val adjacentPlatforms: MutableList<TileEntityPlatform> = LinkedList<TileEntityPlatform>()
            val thisX: Int = this.getPos().getX()
            val thisY: Int = this.getPos().getY()
            val thisZ: Int = this.getPos().getZ()

            for (x: Int in -1..0) {
                for (z: Int in -1..0) {
                    val pos: BlockPos = BlockPos(x + thisX, thisY, z + thisZ)
                    val tile: TileEntity? =
                        if (this.world.isBlockLoaded(pos, false)) this.world.getBlockEntity(pos) else null

                    if (tile is TileEntityPlatform && !tile.isInvalid() && tile.corner == 0) {
                        val tileUp: TileEntity? = this.world.getBlockEntity(pos.above())
                        val tileDown: TileEntity? = this.world.getBlockEntity(pos.below())
                        if (tileUp !is TileEntityPlatform && tileDown !is TileEntityPlatform) {
                            adjacentPlatforms.add(tile)
                        }
                    }
                }
            }

            if (adjacentPlatforms.size == 4) {
                var index: Int = 1
                for (tile: TileEntityPlatform in adjacentPlatforms) {
                    tile.setWhole(index++)
                }
            }
        } else if (this.world.isRemote) {
            this.updateClient()
        }
    }

    @OnlyIn(Dist.CLIENT)
    private fun updateClient() {
        this.lightOn = false
        if (this.colorTicks > 0) {
            if (--this.colorTicks == 0) {
                this.colorState = 0
            }
        }

        val b: IBlockState = this.world.getBlockState(this.getPos())
        if (b.getBlock() === GCBlocks.platform && b.getValue(BlockPlatform.CORNER) == BlockPlatform.EnumCorner.NW) {
            //Scan area for player entities and light up
            if (this.detection == null) {
                this.detection = AxisAlignedBB(
                    this.getPos().getX() + 0.9,
                    this.getPos().getY() + 0.75,
                    this.getPos().getZ() + 0.9,
                    this.getPos().getX() + 1.1,
                    this.getPos().getY() + 1.85,
                    this.getPos().getZ() + 1.1
                )
            }
            val list: MutableList<Entity?> =
                this.world.getEntitiesWithinAABB<Entity?>(EntityPlayer::class.java, detection)

            if (list.size > 0) {
                // Light up the platform
                this.lightOn = true

                // If this player is within the box
                val p: EntityPlayerSP = FMLClientHandler.instance().getClientPlayerEntity()
                val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(p))!!
                if (list.contains(p) && !stats.getPlatformControlled() && p.getVehicle() == null) {
                    if (p.movementInput.sneak) {
                        val canDescend: Int = this.checkNextPlatform(-1)
                        if (canDescend == -1) {
                            this.colorState = 1
                            this.colorTicks = 16
                        } else if (canDescend > 0) {
                            val te: TileEntity? = this.world.getBlockEntity(this.pos.down(canDescend))
                            if (te is TileEntityPlatform) {
                                val tep: TileEntityPlatform = te
                                stats.startPlatformAscent(
                                    this,
                                    tep,
                                    this.pos.getY() - canDescend + (if (this.world.provider is IZeroGDimension) 0.97 else BlockPlatform.HEIGHT.toDouble())
                                )
                                this.startMove(tep)
                                tep.startMove(this)
                            }
                        }
                    } else if (p.movementInput.jump) {
                        val canAscend: Int = this.checkNextPlatform(1)
                        if (canAscend == -1) {
                            this.colorState = 1
                            this.colorTicks = 16
                        } else if (canAscend > 0) {
                            val te: TileEntity? = this.world.getBlockEntity(this.pos.up(canAscend))
                            if (te is TileEntityPlatform) {
                                p.motionY = 0.0
                                val tep: TileEntityPlatform = te
                                stats.startPlatformAscent(
                                    tep,
                                    this,
                                    this.pos.getY() + canAscend + BlockPlatform.HEIGHT + 0.01
                                )
                                this.startMove(tep)
                                tep.startMove(this)
                            }
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private fun startMove(te: TileEntityPlatform) {
        this.moving = true
        this.lightA = this.getBlendedLight()
        this.lightB = te.getBlendedLight()
        this.deltaY = te.getPos().getY() - this.getPos().getY()
    }

    /**
     * @return  0 for no platform, range for good platform, -1 for blocked platform
     */
    private fun checkNextPlatform(dir: Int): Int {
        val thisX: Int = this.getPos().getX()
        val thisY: Int = this.getPos().getY()
        val thisZ: Int = this.getPos().getZ()
        var maxY: Int = thisY + MAXRANGE * dir
        if (maxY > 255) maxY = 255
        if (maxY < 0) maxY = 0

        var y: Int = thisY + dir
        while (y != maxY) {
            var c1: Int = this.checkCorner(BlockPos(thisX, y, thisZ), BlockPlatform.EnumCorner.NW)
            if (c1 >= 2) return c1 - 3
            c1 += this.checkCorner(BlockPos(thisX + 1, y, thisZ), BlockPlatform.EnumCorner.NE) * 4
            if (c1 >= 8) return c1 - 3
            c1 += this.checkCorner(BlockPos(thisX, y, thisZ + 1), BlockPlatform.EnumCorner.SW) * 16
            if (c1 >= 32) return c1 - 3
            c1 += this.checkCorner(BlockPos(thisX + 1, y, thisZ + 1), BlockPlatform.EnumCorner.SE) * 64
            if (c1 >= 128) return c1 - 3
            // Good platform on all four corners
            if (c1 == 0) {
                y += dir
                continue
            }
            if (this.motionObstructed((thisY + 1).toDouble(), (y - thisY).toDouble())) {
                return -1
            }
            return (y - thisY) * dir
            y += dir
        }
        return 0
    }

    /**
     * @return  0 for air, 1 for good platform, 2 for blocked platform, 3 for block
     */
    private fun checkCorner(blockPos: BlockPos, corner: BlockPlatform.EnumCorner): Int {
        val b: IBlockState = this.world.getBlockState(blockPos)
        if (b.getBlock() is BlockAir) {
            return 0
        }
        if (b.getBlock() === GCBlocks.platform && b.getValue(BlockPlatform.CORNER) == corner) {
            return if (this.world.getBlockState(blockPos.above(1))
                    .causesSuffocation() || this.world.getBlockState(blockPos.above(2)).causesSuffocation()
            ) 2 else 1
        }
        if (b.causesSuffocation() || b.getBlock().isFullBlock(b)) {
            return 3
        }
        return 0
    }

    private fun setWhole(index: Int) {
        this.corner = index
        this.world.setBlockAndUpdate(this.getPos(), (GCBlocks.platform)!!.getStateFromMeta(index))
    }

    open fun onDestroy(callingBlock: TileEntity?) {
        if (this.corner > 0) {
            resetBlocks()
        }
        this.world.destroyBlock(this.pos, true)
    }

    private fun resetBlocks() {
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(this.pos, positions)

        for (pos: BlockPos in positions) {
            if (this.world.isBlockLoaded(pos, false) && this.world.getBlockState(pos)
                    .getBlock() === GCBlocks.platform
            ) {
                val tile: TileEntity? = this.world.getBlockEntity(pos)
                if (tile is TileEntityPlatform) {
                    tile.setWhole(0)
                }
            }
        }
    }

    open fun getPositions(blockPos: BlockPos, positions: MutableList<BlockPos>) {
        val x: Int = blockPos.getX()
        val y: Int = blockPos.getY()
        val z: Int = blockPos.getZ()
        when (this.corner) {
            0 -> {}
            1 -> {
                positions.add(BlockPos(x + 1, y, z))
                positions.add(BlockPos(x, y, z + 1))
                positions.add(BlockPos(x + 1, y, z + 1))
            }

            2 -> {
                positions.add(BlockPos(x + 1, y, z))
                positions.add(BlockPos(x, y, z - 1))
                positions.add(BlockPos(x + 1, y, z - 1))
            }

            3 -> {
                positions.add(BlockPos(x - 1, y, z))
                positions.add(BlockPos(x, y, z + 1))
                positions.add(BlockPos(x - 1, y, z + 1))
            }

            4 -> {
                positions.add(BlockPos(x - 1, y, z))
                positions.add(BlockPos(x, y, z - 1))
                positions.add(BlockPos(x - 1, y, z - 1))
            }
        }
    }

    private fun checkIntact(): Boolean {
        val bs: IBlockState = this.world.getBlockState(this.pos)
        if (bs.getBlock() !== GCBlocks.platform || (bs.getValue(BlockPlatform.CORNER) as BlockPlatform.EnumCorner).getMeta() != this.corner) {
            this.resetBlocks()
            return false
        }
        val x: Int = this.pos.getX()
        val y: Int = this.pos.getY()
        val z: Int = this.pos.getZ()
        var count: Int = 0
        when (this.corner) {
            0 -> count = 3
            1 -> {
                count += checkState(BlockPos(x + 1, y, z), 3)
                count += checkState(BlockPos(x, y, z + 1), 2)
                count += checkState(BlockPos(x + 1, y, z + 1), 4)
            }

            2 -> {
                count += checkState(BlockPos(x + 1, y, z), 4)
                count += checkState(BlockPos(x, y, z - 1), 1)
                count += checkState(BlockPos(x + 1, y, z - 1), 3)
            }

            3 -> {
                count += checkState(BlockPos(x - 1, y, z), 1)
                count += checkState(BlockPos(x, y, z + 1), 4)
                count += checkState(BlockPos(x - 1, y, z + 1), 2)
            }

            4 -> {
                count += checkState(BlockPos(x - 1, y, z), 2)
                count += checkState(BlockPos(x, y, z - 1), 3)
                count += checkState(BlockPos(x - 1, y, z - 1), 1)
            }
        }

        if (count >= 3) {
            return count == 3
        }

        this.resetBlocks()
        return true
    }

    private fun checkState(blockPos: BlockPos, meta: Int): Int {
        if (!this.world.isBlockLoaded(blockPos, false)) return 4

        val bs: IBlockState = this.world.getBlockState(blockPos)
        if (bs.getBlock() === GCBlocks.platform && (bs.getValue(BlockPlatform.CORNER) as BlockPlatform.EnumCorner).getMeta() == meta) {
            val tile: TileEntity? = this.world.getBlockEntity(blockPos)
            if (tile is TileEntityPlatform) {
                tile.corner = meta
                tile.firstTickCheck = false
                return 1
            }
        }

        return 0
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.getBlock() !== newState.getBlock()
    }

    open fun noCollide(): Boolean {
        return this.noCollide
    }

    open fun markNoCollide(y: Int, b: Boolean) {
        var y: Int = y
        var te: TileEntity?
        val x: Int = this.getPos().getX()
        val z: Int = this.getPos().getZ()
        y += this.getPos().getY()
        te = this.world.getBlockEntity(BlockPos(x, y, z))
        if (te is TileEntityPlatform) {
            te.noCollide = b
        }
        te = this.world.getBlockEntity(BlockPos(x + 1, y, z))
        if (te is TileEntityPlatform) {
            te.noCollide = b
        }
        te = this.world.getBlockEntity(BlockPos(x, y, z + 1))
        if (te is TileEntityPlatform) {
            te.noCollide = b
        }
        te = this.world.getBlockEntity(BlockPos(x + 1, y, z + 1))
        if (te is TileEntityPlatform) {
            te.noCollide = b
        }
    }

    open fun lightEnabled(): Boolean {
        return this.lightOn
    }

    open fun lightColor(): Int {
        return this.colorState
    }

    open fun isMoving(): Boolean {
        return this.moving
    }

    open fun stopMoving() {
        this.moving = false
    }

    @OnlyIn(Dist.CLIENT)
    open fun getYOffset(partialTicks: Float): Float {
        if (this.moving) {
            val p: EntityPlayerSP = FMLClientHandler.instance().getClientPlayerEntity()
            val playerY: Float = (p.lastTickPosY + (p.y - p.lastTickPosY) * partialTicks.toDouble()).toFloat()
            return (playerY - this.pos.getY() - BlockPlatform.HEIGHT)
        } else return 0.0f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(this.pos.add(-1, -18, -1), this.pos.add(1, 18, 1))
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    open fun getBlendedLight(): Int {
        var j: Int = 0
        var k: Int = 0
        var light: Int = this.world.getCombinedLight(this.getPos().up(), 0)
        j += light % 65536
        k += light / 65536
        light = this.world.getCombinedLight(this.getPos().add(1, 1, 0), 0)
        j += light % 65536
        k += light / 65536
        light = this.world.getCombinedLight(this.getPos().add(0, 1, 1), 0)
        j += light % 65536
        k += light / 65536
        light = this.world.getCombinedLight(this.getPos().add(1, 1, 1), 0)
        j += light % 65536
        k += light / 65536
        return j / 4 + k * 16384
    }

    @OnlyIn(Dist.CLIENT)
    open fun getMeanLightX(yOffset: Float): Float {
        val a: Float = (this.lightA % 65536).toFloat()
        val b: Float = (this.lightB % 65536).toFloat()
        val f: Float = yOffset / deltaY
        return (1 - f) * a + f * b
    }

    @OnlyIn(Dist.CLIENT)
    open fun getMeanLightZ(yOffset: Float): Float {
        val a: Float = (this.lightA / 65536).toFloat()
        val b: Float = (this.lightB / 65536).toFloat()
        val f: Float = yOffset / deltaY
        return (1 - f) * a + f * b
    }

    @OnlyIn(Dist.CLIENT)
    open fun motionObstructed(y: Double, velocityY: Double): Boolean {
        val p: EntityPlayerSP = FMLClientHandler.instance().getClientPlayerEntity()
        val x: Int = this.pos.getX() + 1
        val z: Int = this.pos.getZ() + 1
        val size: Double = 9 / 16.0
        val height: Double = p.height + velocityY
        val depth: Double = if (velocityY < 0.0) 0.179 else 0.0
        val bb: AxisAlignedBB = AxisAlignedBB(x - size, y - depth, z - size, x + size, y + height, z + size)
        BlockPlatform.ignoreCollisionTests = true
        val obstructed: Boolean = !this.world.getCollisionBoxes(p, bb).isEmpty()
        BlockPlatform.ignoreCollisionTests = false
        return obstructed
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_MEDIUM
    }

    companion object {
        private const val MAXRANGE = 16
    }
}
