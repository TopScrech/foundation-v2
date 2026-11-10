package micdoodle8.mods.galacticraft.api.vector

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.crash.CrashReport
import net.minecraft.crash.CrashReportCategory
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ReportedException
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.floor
import kotlin.math.sqrt

/* BlockVec3 is similar to galacticraft.api.vector.Vector3?
 * 
 * But for speed it uses integer arithmetic not doubles, for block coordinates
 * This reduces unnecessary type conversion between integers and doubles and back again.
 * (Minecraft block coordinates are always integers, only entity coordinates are doubles.)
 * 
 */
open class BlockVec3Dim : Cloneable {
    @JvmField
    var x: Int

    @JvmField
    var y: Int

    @JvmField
    var z: Int

    @JvmField
    var dim: Int

    @JvmOverloads
    constructor(x: Int = 0, y: Int = 0, z: Int = 0, d: Int = 0) {
        this.x = x
        this.y = y
        this.z = z
        this.dim = d
    }

    constructor(par1: Entity) {
        this.x = floor(par1.posX).toInt()
        this.y = floor(par1.posY).toInt()
        this.z = floor(par1.posZ).toInt()
        this.dim = par1.dimension
    }

    constructor(par1: TileEntity) {
        this.x = par1.getPos().getX()
        this.y = par1.getPos().getY()
        this.z = par1.getPos().getZ()
        this.dim = par1.getWorld().provider.getDimension()
    }

    constructor(pos: BlockPos, dimensionId: Int) : this(pos.getX(), pos.getY(), pos.getZ(), dimensionId)

    /**
     * Makes a new copy of this Vector. Prevents variable referencing problems.
     */
    public override fun clone(): BlockVec3Dim {
        return BlockVec3Dim(this.x, this.y, this.z, this.dim)
    }

    /**
     * Get block ID at the BlockVec3Dim coordinates, with a forced chunk load if
     * the coordinates are unloaded.  Only works server-side.
     *
     * @return the block ID, or null if the y-coordinate is less than 0 or
     * greater than 256 or the x or z is outside the Minecraft worldmap.
     */
    open fun getBlockState(): IBlockState? {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null
        }

        val world: World? = getWorldForId(this.dim)
        if (world == null) return null

        val chunkx: Int = this.x shr 4
        val chunkz: Int = this.z shr 4
        try {
            // In a typical inner loop, 80% of the time consecutive calls to
            // this will be within the same chunk
            if (chunkCacheX == chunkx && chunkCacheZ == chunkz && chunkCacheDim == world.provider.getDimension() && chunkCached!!.isLoaded()) {
                return chunkCached!!.getBlockState(this.x and 15, this.y, this.z and 15)
            } else {
                var chunk: Chunk? = null
                chunk = world.getChunk(chunkx, chunkz)
                chunkCached = chunk
                chunkCacheDim = world.provider.getDimension()
                chunkCacheX = chunkx
                chunkCacheZ = chunkz
                return chunk!!.getBlockState(this.x and 15, this.y, this.z and 15)
            }
        } catch (throwable: Throwable) {
            val crashreport: CrashReport =
                CrashReport.makeCrashReport(throwable, "Oxygen Sealer thread: Exception getting block type in world")
            val crashreportcategory: CrashReportCategory = crashreport.makeCategory("Requested block coordinates")
            crashreportcategory.addCrashSection(
                "Location",
                CrashReportCategory.getCoordinateInfo(BlockPos(this.x, this.y, this.z))
            )
            throw ReportedException(crashreport)
        }
    }

    /**
     * Get block ID at the BlockVec3 coordinates without forcing a chunk load.
     *
     * @return the block ID, or null if the y-coordinate is less than 0 or
     * greater than 256 or the x or z is outside the Minecraft worldmap.
     * Returns Blocks.BEDROCK if the coordinates being checked are in an
     * unloaded chunk
     */
    open fun getBlockState_noChunkLoad(): IBlockState? {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null
        }

        val world: World? = getWorldForId(this.dim)
        if (world == null) return null

        val chunkx: Int = this.x shr 4
        val chunkz: Int = this.z shr 4
        try {
            if (world.getChunkProvider().getLoadedChunk(chunkx, chunkz) != null) {
                // In a typical inner loop, 80% of the time consecutive calls to
                // this will be within the same chunk
                if (chunkCacheX == chunkx && chunkCacheZ == chunkz && chunkCacheDim == world.provider.getDimension() && chunkCached!!.isLoaded()) {
                    return chunkCached!!.getBlockState(this.x and 15, this.y, this.z and 15)
                } else {
                    var chunk: Chunk? = null
                    chunk = world.getChunk(chunkx, chunkz)
                    chunkCached = chunk
                    chunkCacheDim = world.provider.getDimension()
                    chunkCacheX = chunkx
                    chunkCacheZ = chunkz
                    return chunk!!.getBlockState(this.x and 15, this.y, this.z and 15)
                }
            }
            //Chunk doesn't exist - meaning, it is not loaded
            return Blocks.BEDROCK.getDefaultState()
        } catch (throwable: Throwable) {
            val crashreport: CrashReport =
                CrashReport.makeCrashReport(throwable, "Oxygen Sealer thread: Exception getting block type in world")
            val crashreportcategory: CrashReportCategory = crashreport.makeCategory("Requested block coordinates")
            crashreportcategory.addCrashSection(
                "Location",
                CrashReportCategory.getCoordinateInfo(BlockPos(this.x, this.y, this.z))
            )
            throw ReportedException(crashreport)
        }
    }

    open fun getBlock(): Block? {
        val world: World? = this.getWorldForId(this.dim)
        if (world == null) return null
        return world.getBlockState(BlockPos(this.x, this.y, this.z)).getBlock()
    }

    @JvmOverloads
    open fun modifyPositionFromSide(side: EnumFacing, amount: Int = 1): BlockVec3Dim {
        when (side.ordinal) {
            0 -> this.y -= amount
            1 -> this.y += amount
            2 -> this.z -= amount
            3 -> this.z += amount
            4 -> this.x -= amount
            5 -> this.x += amount
        }
        return this
    }

    open fun newVecSide(side: Int): BlockVec3Dim {
        val vec: BlockVec3Dim = BlockVec3Dim(this.x, this.y, this.z, this.dim)
        when (side) {
            0 -> {
                vec.y--
                return vec
            }

            1 -> {
                vec.y++
                return vec
            }

            2 -> {
                vec.z--
                return vec
            }

            3 -> {
                vec.z++
                return vec
            }

            4 -> {
                vec.x--
                return vec
            }

            5 -> {
                vec.x++
                return vec
            }
        }
        return vec
    }

    override fun hashCode(): Int {
        return (((this.z * 431 + this.x) * 379 + this.y) * 373 + this.dim) * 7
    }

    override fun equals(o: Any?): Boolean {
        if (o is BlockVec3Dim) {
            val vector: BlockVec3Dim = o
            return this.x == vector.x && this.y == vector.y && this.z == vector.z && this.dim == vector.dim
        }

        return false
    }

    override fun toString(): String {
        return "BlockVec3 " + this.dim + ":[" + this.x + "," + this.y + "," + this.z + "]"
    }

    /**
     * This will load the chunk - use getTileEntityNoLoad() if just updating things if present.
     */
    open fun getTileEntity(): TileEntity? {
        val world: World? = this.getWorldForId(this.dim)
        if (world == null) return null
        return world.getTileEntity(BlockPos(this.x, this.y, this.z))
    }

    open fun getTileEntityNoLoad(): TileEntity? {
        val world: World? = getWorldForId(this.dim)
        if (world == null) return null
        val pos: BlockPos = BlockPos(this.x, this.y, this.z)
        if (world.isBlockLoaded(pos, false)) return world.getTileEntity(pos)
        return null
    }

    open fun getBlockMetadata(): IBlockState? {
        val world: World? = this.getWorldForId(this.dim)
        if (world == null) return null
        return world.getBlockState(BlockPos(this.x, this.y, this.z))
    }

    open fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        par1NBTTagCompound.setInteger("x", this.x)
        par1NBTTagCompound.setInteger("y", this.y)
        par1NBTTagCompound.setInteger("z", this.z)
        par1NBTTagCompound.setInteger("dim", this.dim)
        return par1NBTTagCompound
    }

    constructor(par1NBTTagCompound: NBTTagCompound) {
        this.x = par1NBTTagCompound.getInteger("x")
        this.y = par1NBTTagCompound.getInteger("y")
        this.z = par1NBTTagCompound.getInteger("z")
        this.dim = par1NBTTagCompound.getInteger("dim")
    }

    open fun getMagnitude(): Double {
        return sqrt(this.getMagnitudeSquared().toDouble())
    }

    open fun getMagnitudeSquared(): Int {
        return this.x * this.x + this.y * this.y + this.z * this.z
    }

    open fun setBlock(block: IBlockState) {
        val world: World? = this.getWorldForId(this.dim)
        if (world == null) return
        world.setBlockState(BlockPos(this.x, this.y, this.z), block, 3)
    }

    open fun blockExists(): Boolean {
        val world: World? = this.getWorldForId(this.dim)
        if (world == null) return false
        return world.isBlockLoaded(BlockPos(this.x, this.y, this.z))
    }

    /**
     * It is up to the calling method to check that the dimension matches
     *
     * @param vector
     * @return
     */
    open fun distanceSquared(vector: BlockVec3): Int {
        val var2: Int = vector.x - this.x
        val var4: Int = vector.y - this.y
        val var6: Int = vector.z - this.z
        return var2 * var2 + var4 * var4 + var6 * var6
    }

    open fun toBlockPos(): BlockPos {
        return BlockPos(x, y, z)
    }

    private fun getWorldForId(dimensionID: Int): World? {
        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            return WorldUtil.getWorldForDimensionServer(dimensionID)
        } else {
            return getWorldForIdClient(dimensionID)
        }
    }

    @SideOnly(Side.CLIENT)
    private fun getWorldForIdClient(dimensionID: Int): World? {
        val world: World? = FMLClientHandler.instance().getClient().world

        if (world != null && world.provider.getDimension() == dimensionID) {
            return world
        }

        return null
    }

    companion object {
        private var chunkCached: Chunk? = null

        @JvmField
        var chunkCacheDim: Int = Int.MAX_VALUE
        private var chunkCacheX = 1876000 // outside the world edge
        private var chunkCacheZ = 1876000 // outside the world edge

        // INVALID_VECTOR is used in cases where a null vector cannot be used
        @JvmField
        val INVALID_VECTOR: BlockVec3Dim = BlockVec3Dim(-1, -1, -1, -2)

        @JvmStatic
        fun readFromNBT(nbtCompound: NBTTagCompound): BlockVec3Dim {
            val tempVector: BlockVec3Dim = BlockVec3Dim()
            tempVector.x = nbtCompound.getInteger("x")
            tempVector.y = nbtCompound.getInteger("y")
            tempVector.z = nbtCompound.getInteger("z")
            tempVector.dim = nbtCompound.getInteger("dim")
            return tempVector
        }
    }
}
