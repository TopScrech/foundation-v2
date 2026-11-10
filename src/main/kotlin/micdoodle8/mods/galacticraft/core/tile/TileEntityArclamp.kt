package micdoodle8.mods.galacticraft.core.tile

import io.netty.buffer.ByteBuf
import java.util.concurrent.atomic.AtomicBoolean
import micdoodle8.mods.galacticraft.api.tile.ITileClientUpdates
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityCreature
import net.minecraft.entity.ai.RandomPositionGenerator
import net.minecraft.entity.monster.IMob
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.pathfinding.PathNavigate
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedList
import kotlin.math.abs

//ITileClientUpdates for changing in facing;  IPacketReceiver for initial transfer of NBT Data (airToRestore)
open class TileEntityArclamp : TileEntity(), ITickable, ITileClientUpdates, IPacketReceiver {
    private var ticks = 0
    private var sideRear = 0

    @JvmField
    var facing: Int = 0
    private val airToRestore = HashSet<BlockVec3>()
    private var buckets: Array<intBucket>? = null
    private var usingBuckets: AtomicBoolean? = null
    private var lightUpdateBlockList: IntArray? = null
    private var usingLightList: AtomicBoolean? = null
    private var isActive = false
    private var thisAABB: AxisAlignedBB? = null
    private var renderAABB: AxisAlignedBB? = null
    private var thisPos: Vec3d? = null
    private var facingSide = 0

    override fun update() {
        var initialLight: Boolean = false

        //        if (this.updateClientFlag)
//        {
//            this.updateAllInDimension();
//            this.updateClientFlag = false;
//        }
        if (RedstoneUtil.isBlockReceivingRedstone(this.world, this.getPos())) {
            if (this.isActive) {
                this.isActive = false
                this.revertAir()
                this.markDirty()
            }
        } else if (!this.isActive && this.pos.getX() >= -30000000 + 32 && this.pos.getZ() >= -30000000 + 32 && this.pos.getX() < 30000000 - 32 && this.pos.getZ() < 30000000 - 32) {
            this.isActive = true
            initialLight = true
        }

        if (this.isActive) {
            //Test for first tick after placement
            if (this.thisAABB == null) {
                initialLight = true
                val side: Int = this.getBlockMetadata()
                when (side) {
                    0 -> {
                        this.sideRear = side //Down
                        this.facingSide = this.facing + 2
                    }

                    1 -> {
                        this.sideRear = side //Up
                        this.facingSide = this.facing + 2
                    }

                    2 -> {
                        this.sideRear = side //North
                        this.facingSide = this.facing
                        if (this.facing > 1) {
                            this.facingSide = 7 - this.facing
                        }
                    }

                    3 -> {
                        this.sideRear = side //South
                        this.facingSide = this.facing
                        if (this.facing > 1) {
                            this.facingSide += 2
                        }
                    }

                    4 -> {
                        this.sideRear = side //West
                        this.facingSide = this.facing
                    }

                    5 -> {
                        this.sideRear = side //East
                        this.facingSide = this.facing
                        if (this.facing > 1) {
                            this.facingSide = 5 - this.facing
                        }
                    }

                    else -> {}
                }

                this.thisAABB = getAABBforSideAndFacing()
            }

            if (initialLight || this.ticks % 100 == 0) {
                this.lightArea()
            }

            if (!this.world.isRemote && this.world.rand.nextInt(20) == 0) {
                val moblist: MutableList<Entity?> =
                    this.world.getEntitiesInAABBexcluding(null, this.thisAABB, IMob.MOB_SELECTOR)

                if (!moblist.isEmpty()) {
                    val thisVec3: Vec3d = Vec3d(
                        this.getPos().getX().toDouble(),
                        this.getPos().getY().toDouble(),
                        this.getPos().getZ().toDouble()
                    )
                    for (entry: Entity? in moblist) {
                        if (entry !is EntityCreature) {
                            continue
                        }
                        val mob: EntityCreature = entry

                        //Check whether the mob can actually *see* the arclamp tile
                        //if (this.world.func_147447_a(thisPos, new Vec3(entry.posX, entry.posY, entry.posZ), true, true, false) != null) continue;
                        val nav: PathNavigate = mob.getNavigator()
                        if (nav == null) {
                            continue
                        }

                        val vecNewTarget: Vec3d? =
                            RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, 28, 11, this.thisPos)
                        if (vecNewTarget == null) {
                            continue
                        }

                        val distanceNew: Double = vecNewTarget.squareDistanceTo(thisVec3)
                        val distanceCurrent: Double = thisVec3.squareDistanceTo(Vec3d(mob.posX, mob.posY, mob.posZ))
                        if (distanceNew > distanceCurrent) {
                            var vecOldTarget: Vec3d? = null
                            if (nav.getPath() != null && !nav.getPath()!!.isFinished()) {
                                vecOldTarget = nav.getPath()!!.getPosition(mob)
                            }
                            if (vecOldTarget == null || distanceCurrent > vecOldTarget.squareDistanceTo(thisVec3)) {
                                nav.tryMoveToXYZ(vecNewTarget.x, vecNewTarget.y, vecNewTarget.z, 1.3)
                            }
                        }
                    }
                }
            }
        }

        this.ticks++
    }

    private fun getAABBforSideAndFacing(): AxisAlignedBB {
        val x: Int = this.pos.getX()
        val y: Int = this.pos.getY()
        val z: Int = this.pos.getZ()
        val rangeForSide: IntArray? = IntArray(6)
        for (i: Int in 0..5) {
            rangeForSide!![i] = if (i == this.sideRear) 2 else if (i == (this.facingSide xor 1)) 4 else 25
        }
        return AxisAlignedBB(
            (x - rangeForSide!![4]).toDouble(),
            (y - rangeForSide[0]).toDouble(),
            (z - rangeForSide[2]).toDouble(),
            (x + rangeForSide[5]).toDouble(),
            (y + rangeForSide[1]).toDouble(),
            (z + rangeForSide[3]).toDouble()
        )
    }

    override fun onLoad() {
        this.thisPos = Vec3d(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5)
        this.ticks = 0
        this.thisAABB = null
        if (this.world.isRemote) {
            this.buckets = bucketsClient
            this.usingBuckets = usingBucketsClient
            if (lightUpdateBlockListClient == null) {
                lightUpdateBlockListClient = IntArray(SIZELIST)
            }
            this.lightUpdateBlockList = lightUpdateBlockListClient
            this.usingLightList = usingLightListClient
            this.clientOnLoad()
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        } else {
            this.buckets = bucketsServer
            this.usingBuckets = usingBucketsServer
            if (lightUpdateBlockListServer == null) {
                lightUpdateBlockListServer = IntArray(SIZELIST)
            }
            this.lightUpdateBlockList = lightUpdateBlockListServer
            this.usingLightList = usingLightListServer
            this.isActive =
                this.pos.getX() >= -30000000 + 32 && this.pos.getZ() >= -30000000 + 32 && this.pos.getX() < 30000000 - 32 && this.pos.getZ() < 30000000 - 32
        }
    }

    override fun invalidate() {
        this.revertAir()
        this.isActive = false
        super.invalidate()
    }

    open fun lightArea() {
        if (this.usingBuckets!!.getAndSet(true) || this.usingLightList!!.getAndSet(true)) {
            return
        }
        //        long time1 = System.nanoTime();
        var index: Int = 0
        val air: Block? = Blocks.AIR
        val breatheableAirID: Block? = GCBlocks.breatheableAir
        val brightAir: IBlockState = (GCBlocks.brightAir)!!.getDefaultState()
        val brightBreatheableAir: IBlockState = (GCBlocks.brightBreatheableAir)!!.getDefaultState()
        var dirty: Boolean = false
        checkedClear()
        val airToRevert: HashSet<BlockVec3?> = HashSet<BlockVec3?>()
        airToRevert.addAll(airToRestore)
        val airNew: LinkedList<BlockVec3?> = LinkedList<BlockVec3?>()
        var currentLayer: LinkedList<BlockVec3> = LinkedList<BlockVec3>()
        var nextLayer: LinkedList<BlockVec3> = LinkedList<BlockVec3>()
        val thisvec: BlockVec3 = BlockVec3(this)
        currentLayer.add(thisvec)
        val world: World = this.world
        val sideskip1: Int = this.sideRear
        val sideskip2: Int = this.facingSide xor 1
        var side: Int
        var bits: Int
        for (i: Int in 0..5) {
            if (i != sideskip1 && i != sideskip2 && i != (sideskip1 xor 1) && i != (sideskip2 xor 1)) {
                val onEitherSide: BlockVec3 = thisvec.newVecSide(i)
                val state: IBlockState? = onEitherSide.getBlockStateSafe_noChunkLoad(world)
                if (state != null && state.getBlock().getLightOpacity(state) < 15) {
                    currentLayer.add(onEitherSide)
                }
            }
        }
        var inFront: BlockVec3 = BlockVec3(this)
        for (i: Int in 0..3) {
            inFront = inFront.newVecSide(this.facingSide)
            var state: IBlockState? = inFront.getBlockStateSafe_noChunkLoad(world)
            if (state != null && state.getBlock().getLightOpacity(state) == 15) {
                break
            }
            inFront = inFront.newVecSide(sideskip1 xor 1)
            state = inFront.getBlockStateSafe_noChunkLoad(world)
            if (state != null && state.getBlock().getLightOpacity(state) < 15) {
                currentLayer.add(inFront)
            } else {
                break
            }
        }

        inFront = BlockVec3(this).newVecSide(this.facingSide)

        for (count: Int in 0..<LIGHTRANGE) {
            for (vec: BlockVec3 in currentLayer) {
                //Shape the arc lamp lighted area to more of a cone in front of it
                if (count > 1) {
                    var offset: Int = 0
                    when (this.facingSide) {
                        0 -> offset = inFront.y - vec.y
                        1 -> offset = vec.y - inFront.y
                        2 -> offset = inFront.z - vec.z
                        3 -> offset = vec.z - inFront.z
                        4 -> offset = inFront.x - vec.x
                        5 -> offset = vec.x - inFront.x
                    }
                    var offset2: Int = 0
                    when (this.sideRear xor 1) {
                        0 -> offset2 = inFront.y - vec.y
                        1 -> offset2 = vec.y - inFront.y
                        2 -> offset2 = inFront.z - vec.z
                        3 -> offset2 = vec.z - inFront.z
                        4 -> offset2 = inFront.x - vec.x
                        5 -> offset2 = vec.x - inFront.x
                    }
                    if (offset2 - 2 > offset) offset = offset2 - 2
                    if (abs(vec.x - inFront.x) > offset + 2) continue
                    if (abs(vec.y - inFront.y) > offset + 2) continue
                    if (abs(vec.z - inFront.z) > offset + 2) continue
                }


                //Now process each layer outwards from the source, finding new blocks to light (similar to ThreadFindSeal)
                //This is high performance code using our own custom HashSet (that's intBucket)
                side = 0
                bits = vec.sideDoneBits
                var doShine: Boolean = false
                do {
                    //Skip the side which this was entered from
                    //and never go 'backwards'
                    if ((bits and (1 shl side)) == 0) {
                        val sideVec: BlockVec3 = vec.newVecSide(side)
                        var toAdd: Boolean = false
                        if (!checkedContains(vec, side)) {
                            checkedAdd(sideVec)
                            toAdd = true
                        }

                        val bs: IBlockState? = sideVec.getBlockStateSafe_noChunkLoad(world)
                        if (bs == null) {
                            side++
                            continue
                        }
                        val b: Block = bs.getBlock()
                        if (b is BlockAir) {
                            if (toAdd && side != sideskip1 && side != sideskip2) {
                                nextLayer.add(sideVec)
                            }
                        } else {
                            doShine = true
                            //Glass blocks go through to the next layer as well
                            if (side != sideskip1 && side != sideskip2) {
                                if (toAdd && b != null && b.getLightOpacity(bs, world, sideVec.toBlockPos()) == 0) {
                                    nextLayer.add(sideVec)
                                }
                            }
                        }
                    }
                    side++
                } while (side < 6)

                if (doShine) {
                    airNew.add(vec)
                    val id: Block = vec.getBlockStateSafe_noChunkLoad(world)!!.getBlock()
                    if (Blocks.AIR === id) {
                        this.brightenAir(world, vec, brightAir)
                        index = this.checkLightPartA(EnumSkyBlock.BLOCK, vec.toBlockPos(), index)
                        dirty = true
                    } else if (id === breatheableAirID) {
                        this.brightenAir(world, vec, brightBreatheableAir)
                        index = this.checkLightPartA(EnumSkyBlock.BLOCK, vec.toBlockPos(), index)
                        dirty = true
                    }
                }
            }
            if (nextLayer.size == 0) {
                break
            }
            currentLayer = nextLayer
            nextLayer = LinkedList<BlockVec3>()
        }

        if (dirty) {
            this.markDirty()
            this.checkLightPartB(EnumSkyBlock.BLOCK, index)
        }


        //Look for any holdover bright blocks which are no longer lit (e.g. because the Arc Lamp became blocked in a tunnel)
        airToRevert.removeAll(airNew)
        index = 0
        dirty = false
        for (obj: Any? in airToRevert) {
            val vec: BlockVec3 = obj as BlockVec3
            this.setDarkerAir(vec)
            index = this.checkLightPartA(EnumSkyBlock.BLOCK, vec.toBlockPos(), index)
            this.airToRestore.remove(vec)
            dirty = true
        }

        if (dirty) {
            this.markDirty()
            this.checkLightPartB(EnumSkyBlock.BLOCK, index)
        }

        //        long time3 = System.nanoTime();
//        float total = (time3 - time1) / 1000000.0F;
//        GCLog.info("   Total Time taken: " + String.format("%.2f", total) + "ms");
        this.usingBuckets!!.set(false)
        this.usingLightList!!.set(false)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.facing = nbt.getInteger("Facing")
        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            this.airToRestore.clear()
            val airBlocks: NBTTagList = nbt.getTagList("AirBlocks", 10)
            if (airBlocks.tagCount() > 0) {
                for (j: Int in airBlocks.tagCount() - 1 downTo 0) {
                    val tag1: NBTTagCompound = airBlocks.getCompoundTagAt(j)
                    if (tag1 != null) {
                        this.airToRestore.add(BlockVec3.readFromNBT(tag1))
                    }
                }
            }
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        nbt.setInteger("Facing", this.facing)

        val airBlocks: NBTTagList = NBTTagList()

        for (vec: BlockVec3 in this.airToRestore) {
            val tag: NBTTagCompound = NBTTagCompound()
            vec.writeToNBT(tag)
            airBlocks.appendTag(tag)
        }
        nbt.setTag("AirBlocks", airBlocks)
        return nbt
    }

    open fun facingChanged() {
        this.facing -= 2
        if (this.facing < 0) {
            this.facing = 1 - this.facing
            //facing sequence: 0 - 3 - 1 - 2
        }

        this.updateAllInDimension()
        this.thisAABB = null
        this.revertAir()
        this.markDirty()
        this.ticks = 91
    }

    private fun brightenAir(world: World?, vec: BlockVec3, newState: IBlockState) {
        val blockpos: BlockPos = vec.toBlockPos()
        val chunk: Chunk = this.world.getChunk(blockpos)
        val oldState: IBlockState? = chunk.setBlockState(blockpos, newState)
        if (this.world.isRemote && oldState != null) this.world.markAndNotifyBlock(
            blockpos,
            chunk,
            oldState,
            newState,
            2
        )
        //No block update on server - not necessary for changing air to air (also must not trigger a sealer edge check!)
        this.airToRestore.add(vec)
    }

    private fun setDarkerAir(vec: BlockVec3) {
        val blockpos: BlockPos = vec.toBlockPos()
        val b: Block = this.world.getBlockState(blockpos).getBlock()
        val newState: IBlockState
        if (b === GCBlocks.brightAir) {
            newState = Blocks.AIR.getDefaultState()
        } else if (b === GCBlocks.brightBreatheableAir) {
            newState = (GCBlocks.breatheableAir)!!.getDefaultState()
        } else {
            return
        }


//      Roughly similar to:  this.worldObj.setBlockState(pos, newState, (this.worldObj.isRemote) ? 2 : 0);
        val chunk: Chunk = this.world.getChunk(blockpos)
        val oldState: IBlockState? = chunk.setBlockState(blockpos, newState)
        if (this.world.isRemote && oldState != null) this.world.markAndNotifyBlock(
            blockpos,
            chunk,
            oldState,
            newState,
            2
        )
        //No block update on server - not necessary for changing air to air (also must not trigger a sealer edge check!)
    }

    private fun revertAir() {
        if (this.airToRestore.isEmpty()) return

        if (this.usingLightList == null) {
            this.usingLightList = if (this.world.isRemote) usingLightListClient else usingLightListServer
        }

        var index: Int = 0
        for (vec: BlockVec3 in this.airToRestore) {
            this.setDarkerAir(vec)
        }
        if (!this.usingLightList!!.getAndSet(true)) {
            for (vec: BlockVec3 in this.airToRestore) {
                index = this.checkLightPartA(EnumSkyBlock.BLOCK, vec.toBlockPos(), index)
            }
            this.checkLightPartB(EnumSkyBlock.BLOCK, index)
            this.usingLightList!!.set(false)
        }
        this.airToRestore.clear()
        this.checkLightFor(EnumSkyBlock.BLOCK, this.pos)
    }

    open fun checkLightFor(lightType: EnumSkyBlock, bp: BlockPos): Boolean {
        if (!this.world.isAreaLoaded(bp, 17, false)) {
            return false
        }

        val world: World = this.world
        var blockpos: BlockPos
        var i: Int = 0
        var index: Int = 0
        var savedLight: Int = world.getLightFor(lightType, bp)
        var rawLight: Int = this.getRawLight(bp, lightType)
        val testx: Int = bp.getX()
        val testy: Int = bp.getY()
        val testz: Int = bp.getZ()
        val x: Int = testx - 32
        val y: Int = testy - 32
        val z: Int = testz - 32
        var value: Int
        var opacity: Int
        var arraylight: Int
        var xx: Int
        var yy: Int
        var zz: Int
        var range: Int
        val result: LinkedList<BlockPos> = LinkedList<BlockPos>()

        if (rawLight > savedLight)  //Light switched on
        {
            lightUpdateBlockList!![index++] = 133152 //32, 32, 32 = the 0 position
        } else if (rawLight < savedLight)  //Light switched off ?
        {
            lightUpdateBlockList!![index++] = 133152 or (savedLight shl 18)

            while (i < index)  //This becomes CRAZY LARGE
            {
                value = lightUpdateBlockList!![i++]
                xx = (value and 63) + x
                yy = (value shr 6 and 63) + y
                zz = (value shr 12 and 63) + z
                arraylight = value shr 18 and 15

                if (arraylight > 0) {
                    blockpos = BlockPos(xx, yy, zz)
                    if (world.getLightFor(
                            lightType,
                            blockpos
                        ) == arraylight
                    )  //Only gonna happen once (definitely will happen the first iteration)
                    {
                        this.setLightFor_preChecked(lightType, blockpos, 0) //= flagdone

                        range = MathHelper.abs(xx - testx) + MathHelper.abs(yy - testy) + MathHelper.abs(zz - testz)
                        if (range < 17) {
                            GCCoreUtil.getPositionsAdjoining(xx, yy, zz, (result as MutableList<BlockPos?>))
                            for (vec: BlockPos in result) {
                                savedLight = world.getLightFor(lightType, vec)
                                if (savedLight == 0) continue  //eliminate backtracking

                                val bs: IBlockState = world.getBlockState(vec)
                                opacity = bs.getBlock().getLightOpacity(bs, world, vec)
                                if (opacity <= 0) opacity = 1
                                //Tack positions onto the list as long as it looks like lit from here. i.e. saved light is adjacent light - opacity!
                                //There will be some errors due to coincidence / equality of light levels from 2 sources
                                if (savedLight == arraylight - opacity && index < SIZELIST) {
                                    lightUpdateBlockList!![index++] =
                                        vec.getX() - x + (((savedLight shl 6) + vec.getZ() - z shl 6) + vec.getY() - y shl 6)
                                }
                            }
                        }
                    }
                }
            }

            i = 0
        }

        while (i < index) {
            value = lightUpdateBlockList!![i++]
            xx = (value and 63) + x
            yy = (value shr 6 and 63) + y
            zz = (value shr 12 and 63) + z
            blockpos = BlockPos(xx, yy, zz)
            savedLight = world.getLightFor(lightType, blockpos)
            rawLight = this.getRawLight(blockpos, lightType)

            if (rawLight != savedLight) {
                this.setLightFor_preChecked(lightType, blockpos, rawLight) //<-------the light setting
                if (world.isRemote) world.notifyLightSet(blockpos)

                if (rawLight > savedLight) {
                    range = MathHelper.abs(xx - testx) + MathHelper.abs(yy - testy) + MathHelper.abs(zz - testz)
                    if (range < 17 && index < SIZELIST - 6) {
                        GCCoreUtil.getPositionsAdjoining(xx, yy, zz, (result as MutableList<BlockPos?>))
                        for (vec: BlockPos in result) {
                            if (world.getLightFor(
                                    lightType,
                                    vec
                                ) < rawLight - 1
                            )  //-1 here because opacity can't be less than 1 in getRawLight
                            {
                                //Tack even more positions on to the end of the list - this propagates each time we find a light source block... including going back over old positions
                                lightUpdateBlockList!![index++] =
                                    vec.getX() - x + ((vec.getZ() - z shl 6) + vec.getY() - y shl 6)
                            }
                        }
                    }
                }
            } else if (world.isRemote && savedLight != ((value shr 21) and 15)) {
                world.notifyLightSet(blockpos)
            }
        }

        return true
    }

    open fun checkLightPartA(lightType: EnumSkyBlock, bp: BlockPos, indexIn: Int): Int {
        if (indexIn >= SIZELIST) {
            return indexIn
        }

        val world: World = this.world
        var blockpos: BlockPos?
        var i: Int = indexIn
        val iNextStart: Int = 0
        var index: Int = indexIn
        var savedLight: Int = world.getLightFor(lightType, bp)
        val rawLight: Int = this.getRawLight(bp, lightType)
        val x: Int = this.pos.getX() - 64
        val y: Int = this.pos.getY() - 64
        val z: Int = this.pos.getZ() - 64
        val testx: Int = bp.getX()
        val testy: Int = bp.getY()
        val testz: Int = bp.getZ()
        var value: Int
        var opacity: Int
        var arraylight: Int
        var xx: Int
        var yy: Int
        var zz: Int
        var range: Int
        val neighbours: LinkedList<BlockPos> = LinkedList<BlockPos>()

        if (rawLight > savedLight)  //Light switched on
        {
            lightUpdateBlockList!![index++] = ((testz - z shl 7) + testy - y shl 7) + testx - x
        } else if (rawLight < savedLight)  //Light switched off:  savedLight cannot be 0 here
        {
            lightUpdateBlockList!![index++] = ((testz - z shl 7) + testy - y shl 7) + testx - x or (savedLight shl 21)
            this.setLightFor_preChecked(lightType, bp, 0)

            while (i < index) {
                value = lightUpdateBlockList!![i++]
                xx = (value and 127) + x
                yy = (value shr 7 and 127) + y
                zz = (value shr 14 and 127) + z
                range = MathHelper.abs(xx - testx) + MathHelper.abs(yy - testy) + MathHelper.abs(zz - testz)
                if (range < 17) {
                    arraylight = value shr 21 and 15
                    GCCoreUtil.getPositionsAdjoiningLoaded(xx, yy, zz, (neighbours as MutableList<BlockPos?>), world)
                    for (vec: BlockPos in neighbours) {
                        savedLight = world.getLightFor(lightType, vec)
                        if (savedLight == 0) continue  //eliminate backtracking

                        val bs: IBlockState = world.getBlockState(vec)
                        opacity = bs.getBlock().getLightOpacity(bs, world, vec)
                        if (opacity <= 0) opacity = 1
                        //Tack positions onto the list as long as it looks like lit from here. i.e. saved light is adjacent light - opacity!
                        //There will be some errors due to coincidence / equality of light levels from 2 sources
                        if (savedLight == arraylight - opacity && index < SIZELIST) {
                            lightUpdateBlockList!![index++] =
                                vec.getX() - x + (((savedLight shl 7) + vec.getZ() - z shl 7) + vec.getY() - y shl 7)
                            this.setLightFor_preChecked(lightType, vec, 0) //darken everything ready for re-light
                        }
                    }
                }
            }

            i = indexIn
        }
        return index
    }

    open fun checkLightPartB(lightType: EnumSkyBlock, index: Int): Boolean {
        var index: Int = index
        val world: World = this.world
        var blockpos: BlockPos
        var i: Int = 0
        var savedLight: Int
        var rawLight: Int
        val testx: Int = this.pos.getX()
        val testy: Int = this.pos.getY()
        val testz: Int = this.pos.getZ()
        val x: Int = testx - 64
        val y: Int = testy - 64
        val z: Int = testz - 64
        var value: Int
        var xx: Int
        var yy: Int
        var zz: Int
        var range: Int
        val neighbours: LinkedList<BlockPos> = LinkedList<BlockPos>()

        while (i < index) {
            value = lightUpdateBlockList!![i++]
            xx = (value and 127) + x
            yy = (value shr 7 and 127) + y
            zz = (value shr 14 and 127) + z
            blockpos = BlockPos(xx, yy, zz)
            savedLight = world.getLightFor(lightType, blockpos)
            rawLight = this.getRawLight(blockpos, lightType)

            if (rawLight != savedLight) {
                this.setLightFor_preChecked(lightType, blockpos, rawLight) //<-------the light setting
                if (world.isRemote) world.notifyLightSet(blockpos)

                if (rawLight > savedLight) {
                    range = MathHelper.abs(xx - testx) + MathHelper.abs(yy - testy) + MathHelper.abs(zz - testz)
                    if (range < 34 && index < SIZELIST - 6) {
                        GCCoreUtil.getPositionsAdjoiningLoaded(xx, yy, zz, (neighbours as MutableList<BlockPos?>), world)
                        for (vec: BlockPos in neighbours) {
                            if (world.getLightFor(
                                    lightType,
                                    vec
                                ) < rawLight - 1
                            )  //-1 here because opacity can't be less than 1 in getRawLight
                            {
                                //Tack even more positions on to the end of the list - this propagates each time we find a light source block... including going back over old positions
                                lightUpdateBlockList!![index++] =
                                    vec.getX() - x + ((vec.getZ() - z shl 7) + vec.getY() - y shl 7)
                            }
                        }
                    }
                }
            } else if (world.isRemote && savedLight != ((value shr 21) and 15)) {
                world.notifyLightSet(blockpos)
            }
        }
        return true
    }

    /**
     * From vanilla.  This is buggy, gets confused if two low opacity blocks adjacent (e.g. redstone wire, stairs)
     * if those blocks are receiving similar light levels from another source
     */
    private fun getRawLight(pos: BlockPos, lightType: EnumSkyBlock): Int {
        val bs: IBlockState = this.world.getBlockState(pos)
        val block: Block = bs.getBlock()
        val blockLight: Int = block.getLightValue(bs, this.world, pos)
        var light: Int = if (lightType == EnumSkyBlock.SKY) 0 else blockLight

        if (light < 14) {
            var opacity: Int = block.getLightOpacity(bs, this.world, pos)
            if (opacity < 1) {
                opacity = 1
            } else if (opacity >= 15) {
                if (blockLight > 0) {
                    opacity = 1
                } else {
                    return 0
                }
            }

            for (blockpos in GCCoreUtil.getPositionsAdjoining(pos)) {
                val neighbourLight: Int = this.world.getLightFor(
                    lightType,
                    blockpos
                ) - opacity //Easily picks up neighbour lighting if opacity is low...
                if (neighbourLight > light) {
                    if (neighbourLight >= 14) {
                        return neighbourLight
                    }
                    light = neighbourLight
                }
            }
        }

        return light
    }

    private fun setLightFor_preChecked(type: EnumSkyBlock, pos: BlockPos, lightValue: Int) {
        this.world.getChunk(pos.getX() shr 4, pos.getZ() shr 4).setLightFor(type, pos, lightValue)
    }

    open fun getEnabled(): Boolean {
        return !RedstoneUtil.isBlockReceivingRedstone(this.world, this.getPos())
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }


    private fun checkedAdd(vec: BlockVec3) {
        val dx: Int = this.pos.getX() - vec.x
        val dz: Int = this.pos.getZ() - vec.z
        if (dx < -8191 || dx > 8192) return
        if (dz < -8191 || dz > 8192) return
        val bucket: intBucket? = buckets!![((dx and 15) shl 4) + (dz and 15)]
        bucket!!.add(vec.y + ((dx and 0x3FF0) + ((dz and 0x3FF0) shl 10) shl 4))
    }

    /**
     * Currently unused - the sided implementation is used instead
     */
    private fun checkedContains(vec: BlockVec3): Boolean {
        val dx: Int = this.pos.getX() - vec.x
        val dz: Int = this.pos.getZ() - vec.z
        if (dx < -8191 || dx > 8192) return true
        if (dz < -8191 || dz > 8192) return true
        val bucket: intBucket? = buckets!![((dx and 15) shl 4) + (dz and 15)]
        return bucket!!.contains(vec.y + ((dx and 0x3FF0) + ((dz and 0x3FF0) shl 10) shl 4))
    }

    private fun checkedContains(vec: BlockVec3, side: Int): Boolean {
        var y: Int = vec.y
        var dx: Int = this.pos.getX() - vec.x
        var dz: Int = this.pos.getZ() - vec.z
        when (side) {
            0 -> {
                y--
                if (y < 0) return true
            }

            1 -> {
                y++
                if (y > 255) return true
            }

            2 -> dz++
            3 -> dz--
            4 -> dx++
            5 -> dx--
        }
        if (dx < -8191 || dx > 8192) return true
        if (dz < -8191 || dz > 8192) return true
        val bucket: intBucket? = buckets!![((dx and 15) shl 4) + (dz and 15)]
        return bucket!!.contains(y + ((dx and 0x3FF0) + ((dz and 0x3FF0) shl 10) shl 4))
    }

    private fun checkedClear() {
        for (i: Int in 0..255) {
            this.buckets!![i].clear()
        }
    }

    open class intBucket {
        private var maxSize = 12 //default size
        private var size = 0
        private var table = IntArray(maxSize)

        open fun add(i: Int) {
            if (this.contains(i)) return

            if (size >= maxSize) {
                val newTable: IntArray = IntArray(maxSize + maxSize)
                System.arraycopy(table, 0, newTable, 0, maxSize)
                table = newTable
                maxSize += maxSize
            }
            table[size] = i
            size++
        }

        open fun contains(test: Int): Boolean {
            for (i: Int in size - 1 downTo 0) {
                if (table[i] == test) return true
            }
            return false
        }

        open fun clear() {
            size = 0
        }

        open fun size(): Int {
            return size
        }

        open fun contents(): IntArray {
            return table
        }
    }

    override fun buildDataPacket(data: IntArray?) {
        (data)!![0] = this.facing
    }

    override fun updateClient(data: MutableList<Any?>?) {
        this.facing = ((data)!!.get(1) as kotlin.Int?)!!
        this.revertAir()
        this.thisAABB = null
        this.ticks = 86
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(1, 1, 1))
        }
        return this.renderAABB!!
    }

    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        for (vec: BlockVec3? in this.airToRestore) {
            (sendData)!!.add(vec)
        }
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        while ((buffer)!!.readableBytes() >= 12) {
            val x: Int = (buffer)!!.readInt()
            val y: Int = (buffer)!!.readInt()
            val z: Int = (buffer)!!.readInt()
            this.airToRestore.add(BlockVec3(x, y, z))
        }
    }

    companion object {
        private const val LIGHTRANGE = 14
        private val bucketsServer: Array<intBucket>
        private val bucketsClient: Array<intBucket>
        private val usingBucketsServer = AtomicBoolean()
        private val usingBucketsClient = AtomicBoolean()
        private const val SIZELIST = 65536
        private var lightUpdateBlockListServer: IntArray? = null
        private var lightUpdateBlockListClient: IntArray? = null
        private val usingLightListServer = AtomicBoolean()
        private val usingLightListClient = AtomicBoolean()

        init {
            bucketsServer = (arrayOfNulls<intBucket>(256) as Array<TileEntityArclamp.intBucket>)
            bucketsClient = (arrayOfNulls<intBucket>(256) as Array<TileEntityArclamp.intBucket>)
            checkedInit(bucketsServer)
            checkedInit(bucketsClient)
        }

        @JvmStatic
        private fun checkedInit(buckets: Array<intBucket>) {
            for (i: Int in 0..255) {
                buckets[i] = intBucket()
            }
        }
    }
}
