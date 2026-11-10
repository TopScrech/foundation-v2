package micdoodle8.mods.galacticraft.api.vector

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
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.ChunkProviderServer
import kotlin.math.floor
import kotlin.math.sqrt

/* BlockVec3 is similar to galacticraft.api.vector.Vector3?
 * 
 * But for speed it uses integer arithmetic not doubles, for block coordinates
 * This reduces unnecessary type conversion between integers and doubles and back again.
 * (Minecraft block coordinates are always integers, only entity coordinates are doubles.)
 * 
 */
open class BlockVec3 : Cloneable {
    @JvmField
    var x: Int

    @JvmField
    var y: Int

    @JvmField
    var z: Int

    @JvmField
    var sideDoneBits: Int = 0

    constructor(pos: BlockPos) : this(pos.getX(), pos.getY(), pos.getZ())

    @JvmOverloads
    constructor(x: Int = 0, y: Int = 0, z: Int = 0) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(par1: Entity) {
        this.x = floor(par1.posX).toInt()
        this.y = floor(par1.posY).toInt()
        this.z = floor(par1.posZ).toInt()
    }

    constructor(par1: TileEntity) {
        this.x = par1.getPos().getX()
        this.y = par1.getPos().getY()
        this.z = par1.getPos().getZ()
    }

    /**
     * Makes a new copy of this Vector. Prevents variable referencing problems.
     */
    public override fun clone(): BlockVec3 {
        return BlockVec3(this.x, this.y, this.z)
    }

    open fun toBlockPos(): BlockPos {
        return BlockPos(this.x, this.y, this.z)
    }

    /**
     * Get block ID at the BlockVec3 coordinates, with a forced chunk load if
     * the coordinates are unloaded.
     *
     * @param world
     * @return the block ID, or null if the y-coordinate is less than 0 or
     * greater than 256 or the x or z is outside the Minecraft worldmap.
     */
    open fun getBlockState(world: World): IBlockState? {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null
        }

        val chunkx: Int = this.x shr 4
        val chunkz: Int = this.z shr 4
        try {
            if (world.isRemote) {
                if (chunkCacheX_Client == chunkx && chunkCacheZ_Client == chunkz && chunkCacheDim_Client == world.provider.getDimension() && chunkCached_Client!!.isLoaded()) {
                    return chunkCached_Client!!.getBlockState(this.x and 15, this.y, this.z and 15)
                } else {
                    val chunk: Chunk = world.getChunk(chunkx, chunkz)
                    chunkCached_Client = chunk
                    chunkCacheDim_Client = world.provider.getDimension()
                    chunkCacheX_Client = chunkx
                    chunkCacheZ_Client = chunkz
                    return chunk.getBlockState(this.x and 15, this.y, this.z and 15)
                }
            } else {
                // In a typical inner loop, 80% of the time consecutive calls to
                // this will be within the same chunk
                if (chunkCacheX == chunkx && chunkCacheZ == chunkz && chunkCacheDim == world.provider.getDimension() && chunkCached!!.isLoaded()) {
                    return chunkCached!!.getBlockState(this.x and 15, this.y, this.z and 15)
                } else {
                    val chunk: Chunk = world.getChunk(chunkx, chunkz)
                    chunkCached = chunk
                    chunkCacheDim = world.provider.getDimension()
                    chunkCacheX = chunkx
                    chunkCacheZ = chunkz
                    return chunk.getBlockState(this.x and 15, this.y, this.z and 15)
                }
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
     * @param world
     * @return the block ID, or null if the y-coordinate is less than 0 or
     * greater than 256 or the x or z is outside the Minecraft worldmap.
     * Returns Blocks.BEDROCK if the coordinates being checked are in an
     * unloaded chunk
     */
    open fun getBlockState_noChunkLoad(world: World): IBlockState? {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null
        }

        val chunkx: Int = this.x shr 4
        val chunkz: Int = this.z shr 4
        try {
            if (world.getChunkProvider().getLoadedChunk(chunkx, chunkz) != null) {
                if (world.isRemote) {
                    if (chunkCacheX_Client == chunkx && chunkCacheZ_Client == chunkz && chunkCacheDim_Client == world.provider.getDimension() && chunkCached_Client!!.isLoaded()) {
                        return chunkCached_Client!!.getBlockState(this.x and 15, this.y, this.z and 15)
                    } else {
                        val chunk: Chunk = world.getChunk(chunkx, chunkz)
                        chunkCached_Client = chunk
                        chunkCacheDim_Client = world.provider.getDimension()
                        chunkCacheX_Client = chunkx
                        chunkCacheZ_Client = chunkz
                        return chunk.getBlockState(this.x and 15, this.y, this.z and 15)
                    }
                } else {
                    // In a typical inner loop, 80% of the time consecutive calls to
                    // this will be within the same chunk
                    if (chunkCacheX == chunkx && chunkCacheZ == chunkz && chunkCacheDim == world.provider.getDimension() && chunkCached!!.isLoaded()) {
                        return chunkCached!!.getBlockState(this.x and 15, this.y, this.z and 15)
                    } else {
                        val chunk: Chunk = world.getChunk(chunkx, chunkz)
                        chunkCached = chunk
                        chunkCacheDim = world.provider.getDimension()
                        chunkCacheX = chunkx
                        chunkCacheZ = chunkz
                        return chunk.getBlockState(this.x and 15, this.y, this.z and 15)
                    }
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

    open fun getBlockState(par1iBlockAccess: IBlockAccess): IBlockState {
        return par1iBlockAccess.getBlockState(BlockPos(this.x, this.y, this.z))
    }

    /**
     * Get block ID at the BlockVec3 coordinates without forcing a chunk load.
     * Only call this 'safe' version if x and z coordinates are within the
     * Minecraft world map (-30m to +30m)
     *
     * @param world
     * @return the block ID, or null if the y-coordinate is less than 0 or
     * greater than 256. Returns Blocks.BEDROCK if the coordinates being
     * checked are in an unloaded chunk
     */
    open fun getBlockStateSafe_noChunkLoad(world: World): IBlockState? {
        if (this.y < 0 || this.y >= 256) {
            return null
        }

        val chunkx: Int = this.x shr 4
        val chunkz: Int = this.z shr 4
        try {
            if (world.getChunkProvider().getLoadedChunk(chunkx, chunkz) != null) {
                if (world.isRemote) {
                    if (chunkCacheX_Client == chunkx && chunkCacheZ_Client == chunkz && chunkCacheDim_Client == world.provider.getDimension() && chunkCached_Client!!.isLoaded()) {
                        return chunkCached_Client!!.getBlockState(this.x and 15, this.y, this.z and 15)
                    } else {
                        val chunk: Chunk = world.getChunk(chunkx, chunkz)
                        chunkCached_Client = chunk
                        chunkCacheDim_Client = world.provider.getDimension()
                        chunkCacheX_Client = chunkx
                        chunkCacheZ_Client = chunkz
                        return chunk.getBlockState(this.x and 15, this.y, this.z and 15)
                    }
                } else {
                    // In a typical inner loop, 80% of the time consecutive calls to
                    // this will be within the same chunk
                    if (chunkCacheX == chunkx && chunkCacheZ == chunkz && chunkCacheDim == world.provider.getDimension() && chunkCached!!.isLoaded()) {
                        return chunkCached!!.getBlockState(this.x and 15, this.y, this.z and 15)
                    } else {
                        val chunk: Chunk = world.getChunk(chunkx, chunkz)
                        chunkCached = chunk
                        chunkCacheDim = world.provider.getDimension()
                        chunkCacheX = chunkx
                        chunkCacheZ = chunkz
                        return chunk.getBlockState(this.x and 15, this.y, this.z and 15)
                    }
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

    open fun translate(par1: BlockVec3): BlockVec3 {
        this.x += par1.x
        this.y += par1.y
        this.z += par1.z
        return this
    }

    open fun translate(par1x: Int, par1y: Int, par1z: Int): BlockVec3 {
        this.x += par1x
        this.y += par1y
        this.z += par1z
        return this
    }

    open fun subtract(par1: BlockVec3): BlockVec3 {
        this.x -= par1.x
        this.y -= par1.y
        this.z -= par1.z

        return this
    }

    open fun scale(par1: Int): BlockVec3 {
        this.x *= par1
        this.y *= par1
        this.z *= par1

        return this
    }

    @JvmOverloads
    open fun modifyPositionFromSide(side: EnumFacing, amount: Int = 1): BlockVec3 {
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

    open fun newVecSide(side: Int): BlockVec3 {
        val vec: BlockVec3 = BlockVec3(this.x, this.y, this.z)
        vec.sideDoneBits = (1 shl (side xor 1)) + (side shl 6)
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
        // Upgraded hashCode calculation from the one in VecDirPair to something
        // a bit stronger and faster
        return ((this.y * 379 + this.x) * 373 + this.z) * 7
    }

    override fun equals(o: Any?): Boolean {
        if (o is BlockVec3) {
            val vector: BlockVec3 = o
            return this.x == vector.x && this.y == vector.y && this.z == vector.z
        }

        return false
    }

    override fun toString(): String {
        return "[" + this.x + "," + this.y + "," + this.z + "]"
    }

    /**
     * This will load the chunk.
     */
    open fun getTileEntity(world: IBlockAccess): TileEntity? {
        return world.getTileEntity(BlockPos(this.x, this.y, this.z))
    }

    /**
     * No chunk load: returns null if chunk to side is unloaded
     */
    open fun getTileEntityOnSide(world: World, side: EnumFacing): TileEntity? {
        var x: Int = this.x
        var y: Int = this.y
        var z: Int = this.z
        when (side.ordinal) {
            0 -> y--
            1 -> y++
            2 -> z--
            3 -> z++
            4 -> x--
            5 -> x++
            else -> return null
        }
        val pos: BlockPos = BlockPos(x, y, z)
        return if (world.isBlockLoaded(pos, false)) world.getTileEntity(pos) else null
    }

    /**
     * No chunk load: returns null if chunk to side is unloaded
     */
    open fun getTileEntityOnSide(world: World, side: Int): TileEntity? {
        var x: Int = this.x
        var y: Int = this.y
        var z: Int = this.z
        when (side) {
            0 -> y--
            1 -> y++
            2 -> z--
            3 -> z++
            4 -> x--
            5 -> x++
            else -> return null
        }
        val pos: BlockPos = BlockPos(x, y, z)
        return if (world.isBlockLoaded(pos, false)) world.getTileEntity(pos) else null
    }

    /**
     * This will load the chunk to the side.
     */
    open fun blockOnSideHasSolidFace(world: World, side: Int): Boolean {
        var x: Int = this.x
        var y: Int = this.y
        var z: Int = this.z
        when (side) {
            0 -> y--
            1 -> y++
            2 -> z--
            3 -> z++
            4 -> x--
            5 -> x++
            else -> return false
        }
        val pos: BlockPos = BlockPos(x, y, z)
        return world.getBlockState(pos).getBlock()
            .isSideSolid(world.getBlockState(pos), world, pos, EnumFacing.byIndex(side xor 1))
    }

    /**
     * No chunk load: returns null if chunk is unloaded
     */
    open fun getBlockOnSide(world: World, side: Int): Block? {
        var x: Int = this.x
        var y: Int = this.y
        var z: Int = this.z
        when (side) {
            0 -> y--
            1 -> y++
            2 -> z--
            3 -> z++
            4 -> x--
            5 -> x++
            else -> return null
        }
        val pos: BlockPos = BlockPos(x, y, z)
        return if (world.isBlockLoaded(pos, false)) world.getBlockState(pos).getBlock() else null
    }

    open fun getBlockMetadata(world: IBlockAccess): Int {
        val state: IBlockState = world.getBlockState(BlockPos(x, y, z))
        return state.getBlock().getMetaFromState(state)
    }

    open fun distanceTo(vector: BlockVec3): Int {
        val var2: Int = vector.x - this.x
        val var4: Int = vector.y - this.y
        val var6: Int = vector.z - this.z
        return MathHelper.floor(sqrt((var2 * var2 + var4 * var4 + var6 * var6).toDouble()))
    }

    open fun distanceSquared(vector: BlockVec3): Int {
        val var2: Int = vector.x - this.x
        val var4: Int = vector.y - this.y
        val var6: Int = vector.z - this.z
        return var2 * var2 + var4 * var4 + var6 * var6
    }

    open fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        par1NBTTagCompound.setInteger("x", this.x)
        par1NBTTagCompound.setInteger("y", this.y)
        par1NBTTagCompound.setInteger("z", this.z)
        return par1NBTTagCompound
    }

    constructor(par1NBTTagCompound: NBTTagCompound) {
        this.x = par1NBTTagCompound.getInteger("x")
        this.y = par1NBTTagCompound.getInteger("y")
        this.z = par1NBTTagCompound.getInteger("z")
    }

    open fun writeToNBT(par1NBTTagCompound: NBTTagCompound, prefix: String?): NBTTagCompound {
        par1NBTTagCompound.setInteger(prefix + "_x", this.x)
        par1NBTTagCompound.setInteger(prefix + "_y", this.y)
        par1NBTTagCompound.setInteger(prefix + "_z", this.z)
        return par1NBTTagCompound
    }

    open fun getMagnitude(): Double {
        return sqrt(this.getMagnitudeSquared().toDouble())
    }

    open fun getMagnitudeSquared(): Int {
        return this.x * this.x + this.y * this.y + this.z * this.z
    }

    open fun setBlock(worldObj: World, block: IBlockState) {
        worldObj.setBlockState(BlockPos(x, y, z), block, 3)
    }

    open fun blockExists(world: World): Boolean {
        return world.isBlockLoaded(BlockPos(this.x, this.y, this.z), false)
    }

    open fun setSideDone(side: Int) {
        this.sideDoneBits = this.sideDoneBits or (1 shl side)
    }

    open fun getTileEntityForce(world: World): TileEntity? {
        val chunkx: Int = this.x shr 4
        val chunkz: Int = this.z shr 4

        if (world.getChunkProvider()
                .getLoadedChunk(chunkx, chunkz) != null
        ) return world.getTileEntity(this.toBlockPos())

        val chunk: Chunk? = (world.getChunkProvider() as ChunkProviderServer).loadChunk(chunkx, chunkz)
        return chunk!!.getTileEntity(
            BlockPos(this.x and 15, this.y, this.z and 15),
            Chunk.EnumCreateEntityType.IMMEDIATE
        )
    }

    open fun midPoint(): Vector3 {
        return Vector3(this.x + 0.5, this.y + 0.5, this.z + 0.5)
    }

    companion object {
        private var chunkCached: Chunk? = null

        @JvmField
        var chunkCacheDim: Int = Int.MAX_VALUE
        private var chunkCacheX = 1876000 // outside the world edge
        private var chunkCacheZ = 1876000 // outside the world edge
        private var chunkCached_Client: Chunk? = null

        @JvmField
        var chunkCacheDim_Client: Int = Int.MAX_VALUE
        private var chunkCacheX_Client = 1876000 // outside the world edge
        private var chunkCacheZ_Client = 1876000 // outside the world edge

        // INVALID_VECTOR is used in cases where a null vector cannot be used
        @JvmField
        val INVALID_VECTOR: BlockVec3 = BlockVec3(-1, -1, -1)

        @JvmStatic
        fun add(par1: BlockVec3, a: BlockVec3): BlockVec3 {
            return BlockVec3(par1.x + a.x, par1.y + a.y, par1.z + a.z)
        }

        @JvmStatic
        fun readFromNBT(nbtCompound: NBTTagCompound): BlockVec3 {
            val tempVector: BlockVec3 = BlockVec3()
            tempVector.x = nbtCompound.getInteger("x")
            tempVector.y = nbtCompound.getInteger("y")
            tempVector.z = nbtCompound.getInteger("z")
            return tempVector
        }

        @JvmStatic
        fun readFromNBT(par1NBTTagCompound: NBTTagCompound, prefix: String?): BlockVec3? {
            val readX: Int = par1NBTTagCompound.getInteger(prefix + "_x")
            if (readX == null) return null
            val readY: Int = par1NBTTagCompound.getInteger(prefix + "_y")
            if (readY == null) return null
            val readZ: Int = par1NBTTagCompound.getInteger(prefix + "_z")
            if (readZ == null) return null
            return BlockVec3(readX, readY, readZ)
        }
    }
}
