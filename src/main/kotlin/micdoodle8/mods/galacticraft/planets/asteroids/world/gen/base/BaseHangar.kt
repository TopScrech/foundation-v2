package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.Random

open class BaseHangar : SizedPiece {
    constructor()

    constructor(
        configuration: BaseConfiguration,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        direction: EnumFacing
    ) : super(configuration, HANGARWIDTH, HANGARHEIGHT, HANGARLENGTH, direction) {
        if (direction.getAxis() == EnumFacing.Axis.X) {
            val w: Int = this.sizeX
            this.sizeX = this.sizeZ
            this.sizeZ = w
            this.setCoordBaseMode(direction.getOpposite()) //Maybe a bug in vanilla here?
        } else this.setCoordBaseMode(direction)
        val yPos: Int = configuration.getYPosition()
        this.boundingBox = BoundingBox(
            blockPosX,
            yPos,
            blockPosZ,
            blockPosX + this.sizeX,
            yPos + this.sizeY,
            blockPosZ + this.sizeZ
        )
        //TODO check save nbt
    }

    override fun addComponentParts(
        worldIn: World,
        randomIn: Random,
        structureBoundingBoxIn: BoundingBox
    ): Boolean {
        val blockAir: IBlockState = Blocks.AIR.defaultBlockState()
        val blockPlain: IBlockState = (GCBlocks.basicBlock)!!.getStateFromMeta(4)
        val blockPattern: IBlockState = (GCBlocks.basicBlock)!!.getStateFromMeta(3)
        val blockGrid: IBlockState = (AsteroidBlocks.blockWalkway)!!.defaultBlockState()
        val blockSlab: IBlockState = (GCBlocks.slabGCHalf)!!.getStateFromMeta(1)
        val upsideSlab: IBlockState = (GCBlocks.slabGCHalf)!!.getStateFromMeta(9)
        val blockWall: IBlockState = (GCBlocks.wallGC)!!.getStateFromMeta(1)
        val decoWall: IBlockState = (GCBlocks.wallGC)!!.getStateFromMeta(0)
        val moonWall: IBlockState = (GCBlocks.wallGC)!!.getStateFromMeta(2)
        val blockBars: IBlockState = Blocks.IRON_BARS.defaultBlockState()
        val blockDesh: IBlockState = (MarsBlocks.marsBlock)!!.getStateFromMeta(8)
        val blockRedstone: IBlockState = (GCBlocks.concealedRedstone)!!.defaultBlockState()
        val blockStair: Block = (GCBlocks.tinStairs2)!!
        val arcLamp: Block = (GCBlocks.brightLamp)!!
        var stairmeta: Int = 1
        var stairmetaB: Int = 2
        var lampmeta: Int = 3
        var lampmeta2: Int = 5
        if (direction == EnumFacing.SOUTH) {
            stairmeta = stairmeta xor 1
            stairmetaB = stairmetaB xor 1
            lampmeta = lampmeta xor 1
            lampmeta2 = lampmeta2 xor 1
        } else if (direction == EnumFacing.EAST) {
            stairmeta = stairmeta xor 2
            stairmetaB = stairmetaB xor 3
            lampmeta = 4
            lampmeta2 = 3
        } else if (direction == EnumFacing.WEST) {
            stairmeta = stairmeta xor 3
            stairmetaB = stairmetaB xor 2
            lampmeta = 5
            lampmeta2 = 2
        }


        val maxX: Int = HANGARWIDTH
        val maxZ: Int = HANGARLENGTH
        val maxY: Int = this.sizeY
        val midPoint: Int = HANGARLENGTH - 22


        //AIR
        for (zz: Int in HANGARLENGTH downTo 0) {
            for (xx: Int in 0..maxX) {
                for (y: Int in 0..maxY) {
                    if (y <= 1 && (xx == 0 || xx == maxX)) continue
                    if (y == maxY && (xx < 4 || xx > maxX - 4)) continue
                    if (y == maxY - 1 && (xx < 2 || xx > maxX - 2)) continue
                    this.setBlockState(worldIn, blockAir, xx, y, zz, structureBoundingBoxIn)
                }
            }
        }


        //endwall
        for (y: Int in 7..8) {
            this.setBlockState(worldIn, blockWall, 1, y, HANGARLENGTH, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockWall, 2, y, HANGARLENGTH, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockWall, 3, y, HANGARLENGTH, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockWall, 23, y, HANGARLENGTH, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockWall, 24, y, HANGARLENGTH, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockWall, 25, y, HANGARLENGTH, structureBoundingBoxIn)

            for (x: Int in 5..21) {
                this.setBlockState(
                    worldIn,
                    (this.configuration)!!.getWallBlock(),
                    x,
                    y,
                    HANGARLENGTH,
                    structureBoundingBoxIn
                )
                if (y == 7 && (x < 9 || x > 17)) this.setBlockState(
                    worldIn,
                    (this.configuration)!!.getWallBlock(),
                    x,
                    maxY,
                    HANGARLENGTH,
                    structureBoundingBoxIn
                )
            }
        }

        for (y: Int in 7..maxY) {
            this.setBlockState(worldIn, blockPlain, 4, y, HANGARLENGTH, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockPlain, 22, y, HANGARLENGTH, structureBoundingBoxIn)
        }
        for (y: Int in 9..maxY) {
            this.setBlockState(worldIn, (this.configuration)!!.getWallBlock(), 6, y, HANGARLENGTH, structureBoundingBoxIn)
            this.setBlockState(worldIn, (this.configuration)!!.getWallBlock(), 20, y, HANGARLENGTH, structureBoundingBoxIn)
        }

        for (xx: Int in 0..3) this.setBlockState(worldIn, moonWall, xx, 1, HANGARLENGTH, structureBoundingBoxIn)
        for (xx: Int in maxX - 3..maxX) this.setBlockState(
            worldIn,
            moonWall,
            xx,
            1,
            HANGARLENGTH,
            structureBoundingBoxIn
        )

        this.setBlockState(worldIn, blockDesh, 9, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockRedstone, 11, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockPattern, 13, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockRedstone, 15, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockDesh, 17, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, upsideSlab, 10, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, upsideSlab, 12, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, upsideSlab, 14, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, upsideSlab, 16, maxY, HANGARLENGTH - 1, structureBoundingBoxIn)


        //Ceiling struts
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB + 4),
            4,
            maxY - 1,
            HANGARLENGTH - 3,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB + 4),
            4,
            maxY - 2,
            HANGARLENGTH - 2,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB + 4),
            4,
            maxY - 3,
            HANGARLENGTH - 1,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB + 4),
            22,
            maxY - 1,
            HANGARLENGTH - 3,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB + 4),
            22,
            maxY - 2,
            HANGARLENGTH - 2,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB + 4),
            22,
            maxY - 3,
            HANGARLENGTH - 1,
            structureBoundingBoxIn
        )

        //        this.setBlockState(worldIn, arcLamp.getStateFromMeta(lampmeta), 5, maxY - 3, HANGARLENGTH, structureBoundingBoxIn);
//        this.setBlockState(worldIn, arcLamp.getStateFromMeta(lampmeta), 5, maxY - 6, HANGARLENGTH, structureBoundingBoxIn);
//        this.setBlockState(worldIn, arcLamp.getStateFromMeta(lampmeta), 21, maxY - 3, HANGARLENGTH, structureBoundingBoxIn);
//        this.setBlockState(worldIn, arcLamp.getStateFromMeta(lampmeta), 21, maxY - 6, HANGARLENGTH, structureBoundingBoxIn);

        //FIRST SECTION
        for (zz: Int in HANGARLENGTH downTo HANGARLENGTH - 5 + 1) {
            var xx: Int = 0
            while (xx <= maxX) {
                this.setBlockState(worldIn, moonWall, xx, 1, zz, structureBoundingBoxIn)
                for (y: Int in 2..6) {
                    this.setBlockState(worldIn, blockAir, xx, y, zz, structureBoundingBoxIn)
                }
                for (y: Int in 9..<maxY - 1) {
                    this.setBlockState(worldIn, blockAir, xx, y, zz, structureBoundingBoxIn)
                }
                this.setBlockState(worldIn, blockWall, xx, 7, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockWall, xx, 8, zz, structureBoundingBoxIn)
                xx += maxX
            }

            if (zz % 3 == 1) {
                this.setBlockState(worldIn, blockPlain, 4, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, 22, maxY, zz, structureBoundingBoxIn)
            } else {
                this.setBlockState(worldIn, blockPattern, 4, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPattern, 22, maxY, zz, structureBoundingBoxIn)
            }


            //Top middle
            if (zz < HANGARLENGTH - 1) {
                this.setBlockState(worldIn, blockGrid, 9, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockRedstone, 11, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockGrid, 13, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockRedstone, 15, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockGrid, 17, maxY, zz, structureBoundingBoxIn)
            }


            //Floor
            this.setBlockState(worldIn, blockPattern, 7, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockPattern, 9, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockPattern, 19, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockPattern, 17, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockGrid, 8, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockGrid, 18, 0, zz, structureBoundingBoxIn)

            if (zz > HANGARLENGTH - 5) {
                this.setBlockState(worldIn, blockPlain, 10, 0, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, 16, 0, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, 11, 0, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, 15, 0, zz, structureBoundingBoxIn)
            }

            if (zz > HANGARLENGTH - 4) {
                this.setBlockState(worldIn, blockPlain, 12, 0, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, 14, 0, zz, structureBoundingBoxIn)
            }
            if (zz > HANGARLENGTH - 3) this.setBlockState(worldIn, blockPlain, 13, 0, zz, structureBoundingBoxIn)
        }

        //Floor end
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB),
            10,
            0,
            HANGARLENGTH - 6,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB),
            16,
            0,
            HANGARLENGTH - 6,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB),
            11,
            0,
            HANGARLENGTH - 5,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB),
            15,
            0,
            HANGARLENGTH - 5,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB),
            12,
            0,
            HANGARLENGTH - 4,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB),
            14,
            0,
            HANGARLENGTH - 4,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            blockStair.getStateFromMeta(stairmetaB),
            13,
            0,
            HANGARLENGTH - 3,
            structureBoundingBoxIn
        )
        this.setBlockState(worldIn, blockPlain, 10, 0, HANGARLENGTH - 5, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockPlain, 16, 0, HANGARLENGTH - 5, structureBoundingBoxIn)

        //Join first and second sections
        this.setBlockState(worldIn, moonWall, 0, 1, HANGARLENGTH - 5, structureBoundingBoxIn)
        this.setBlockState(worldIn, moonWall, maxX, 1, HANGARLENGTH - 5, structureBoundingBoxIn)

        this.setBlockState(
            worldIn,
            arcLamp.getStateFromMeta(lampmeta2 xor 1),
            12,
            maxY,
            HANGARLENGTH - 3,
            structureBoundingBoxIn
        )
        this.setBlockState(
            worldIn,
            arcLamp.getStateFromMeta(lampmeta2),
            14,
            maxY,
            HANGARLENGTH - 3,
            structureBoundingBoxIn
        )

        //SECOND SECTION
        for (zz: Int in HANGARLENGTH - 5 downTo midPoint + 1) {
            //Top sides
            if (zz % 3 == 1) {
                this.setBlockState(worldIn, blockSlab, 3, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, 4, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, 22, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockSlab, 23, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(
                    worldIn,
                    blockStair.getStateFromMeta(stairmeta + 4),
                    2,
                    maxY - 1,
                    zz,
                    structureBoundingBoxIn
                )
                this.setBlockState(
                    worldIn,
                    blockStair.getStateFromMeta(stairmeta + 4),
                    1,
                    maxY - 2,
                    zz,
                    structureBoundingBoxIn
                )
                this.setBlockState(
                    worldIn,
                    blockStair.getStateFromMeta((stairmeta xor 1) + 4),
                    24,
                    maxY - 1,
                    zz,
                    structureBoundingBoxIn
                )
                this.setBlockState(
                    worldIn,
                    blockStair.getStateFromMeta((stairmeta xor 1) + 4),
                    25,
                    maxY - 2,
                    zz,
                    structureBoundingBoxIn
                )
                if (zz != HANGARLENGTH - 11) {
                    this.setBlockState(
                        worldIn,
                        blockStair.getStateFromMeta(stairmeta),
                        1,
                        1,
                        zz,
                        structureBoundingBoxIn
                    )
                    this.setBlockState(
                        worldIn,
                        blockStair.getStateFromMeta(stairmeta xor 1),
                        25,
                        1,
                        zz,
                        structureBoundingBoxIn
                    )
                    floorStrut(worldIn, blockWall, decoWall, zz, structureBoundingBoxIn)
                }
            } else {
                this.setBlockState(worldIn, blockPattern, 22, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPattern, 4, maxY, zz, structureBoundingBoxIn)
            }

            //Top middle
            if (zz == midPoint + 1) {
                this.setBlockState(worldIn, blockDesh, 9, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPattern, 11, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockDesh, 13, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPattern, 15, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockDesh, 17, maxY, zz, structureBoundingBoxIn)
            } else {
                this.setBlockState(worldIn, blockGrid, 9, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockGrid, 13, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockGrid, 17, maxY, zz, structureBoundingBoxIn)
                if (zz == HANGARLENGTH - 12) {
                    val repeater: IBlockState =
                        (GCBlocks.concealedRepeater_Powered)!!.getStateFromMeta(if ((direction)!!.getAxis() == EnumFacing.Axis.Z) 0 else 2) //Rotation will be taken care of by getRotation() but seems to be bugged in vanilla
                    this.setBlockState(worldIn, repeater, 11, maxY, zz, structureBoundingBoxIn)
                    this.setBlockState(worldIn, repeater, 15, maxY, zz, structureBoundingBoxIn)
                } else {
                    this.setBlockState(worldIn, blockRedstone, 11, maxY, zz, structureBoundingBoxIn)
                    this.setBlockState(worldIn, blockRedstone, 15, maxY, zz, structureBoundingBoxIn)
                }
            }
            if ((zz - midPoint - 1) % 6 == 0) {
                this.setBlockState(worldIn, arcLamp.getStateFromMeta(lampmeta2), 10, maxY, zz, structureBoundingBoxIn)
                this.setBlockState(
                    worldIn,
                    arcLamp.getStateFromMeta(lampmeta2 xor 1),
                    16,
                    maxY,
                    zz,
                    structureBoundingBoxIn
                )
            }

            //Walls
            var xx: Int = 0
            while (xx <= maxX) {
                this.setBlockState(worldIn, blockPlain, xx, maxY - 2, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, xx, maxY - 4, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, xx, 2, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockPlain, xx, 4, zz, structureBoundingBoxIn)
                if (zz % 3 == 1) {
                    this.setBlockState(worldIn, blockPattern, xx, maxY - 3, zz, structureBoundingBoxIn)
                    this.setBlockState(worldIn, blockPlain, xx, 3, zz, structureBoundingBoxIn)

                    this.setBlockState(worldIn, blockWall, xx, 5, zz, structureBoundingBoxIn)
                    this.setBlockState(worldIn, blockWall, xx, 6, zz, structureBoundingBoxIn)
                    this.setBlockState(worldIn, blockWall, xx, 9, zz, structureBoundingBoxIn)
                    this.setBlockState(worldIn, blockWall, xx, 10, zz, structureBoundingBoxIn)
                }
                this.setBlockState(worldIn, blockWall, xx, 7, zz, structureBoundingBoxIn)
                this.setBlockState(worldIn, blockWall, xx, 8, zz, structureBoundingBoxIn)
                xx += maxX
            }


            //Floor
            val blockBeam: IBlockState = if (zz % 2 == 0) blockPattern else blockPlain
            this.setBlockState(worldIn, blockBeam, 7, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockBeam, 9, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockBeam, 19, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockBeam, 17, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockGrid, 8, 0, zz, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockGrid, 18, 0, zz, structureBoundingBoxIn)
        }

        //THIRD SECTION
        val zz: Int = midPoint

        //Top sides
        extrudeTrioOff(worldIn, randomIn, 2, blockPlain, blockPattern, 4, maxY, zz, structureBoundingBoxIn)
        extrudeTrioOff(worldIn, randomIn, 2, blockPlain, blockPattern, 22, maxY, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 2, blockGrid, 23, maxY, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 2, blockGrid, 3, maxY, zz, structureBoundingBoxIn)

        //Walls
        run {
            var xx: Int = 0
            while (xx <= maxX) {
                extrudeTrio(worldIn, randomIn, 1, blockPlain, blockPattern, xx, maxY - 2, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 1, blockPlain, blockPattern, xx, maxY - 4, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 1, blockPlain, blockPattern, xx, 2, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 1, blockPlain, blockPattern, xx, 4, zz, structureBoundingBoxIn)

                extrudeTrio(worldIn, randomIn, 0, blockBars, blockAir, xx, 5, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 0, blockBars, blockAir, xx, 6, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 0, blockBars, blockAir, xx, 7, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 0, blockBars, blockAir, xx, 8, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 0, blockBars, blockAir, xx, 9, zz, structureBoundingBoxIn)
                extrudeTrio(worldIn, randomIn, 0, blockBars, blockAir, xx, 10, zz, structureBoundingBoxIn)
                extrude(worldIn, randomIn, 2, blockBars, xx, maxY - 3, zz, structureBoundingBoxIn)
                extrude(worldIn, randomIn, 2, blockBars, xx, 3, zz, structureBoundingBoxIn)
                xx += maxX
            }
        }

        //Floor
        extrude(worldIn, randomIn, 2, blockGrid, 1, 1, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 2, blockGrid, 2, 1, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 2, blockGrid, 24, 1, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 2, blockGrid, 25, 1, zz, structureBoundingBoxIn)
        extrudeDuo(worldIn, randomIn, 3, blockPlain, blockPattern, 7, 0, zz, structureBoundingBoxIn)
        extrudeDuo(worldIn, randomIn, 3, blockPlain, blockPattern, 9, 0, zz, structureBoundingBoxIn)
        extrudeDuo(worldIn, randomIn, 3, blockPlain, blockPattern, 19, 0, zz, structureBoundingBoxIn)
        extrudeDuo(worldIn, randomIn, 3, blockPlain, blockPattern, 17, 0, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 3, blockGrid, 8, 0, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 3, blockGrid, 18, 0, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 3, blockGrid, 5, 0, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 3, blockGrid, 21, 0, zz, structureBoundingBoxIn)

        this.setBlockState(worldIn, moonWall, 10, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, moonWall, 11, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, moonWall, 12, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, moonWall, 13, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, moonWall, 14, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, moonWall, 15, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, moonWall, 16, 0, zz, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 2, blockGrid, 10, 0, zz - 1, structureBoundingBoxIn)
        extrude(worldIn, randomIn, 2, blockGrid, 16, 0, zz - 1, structureBoundingBoxIn)


        //WHERE SECOND AND THIRD SECTION MEET
        //Extend the mid-side walls a little way past the midPoint
        var xx: Int = 0
        while (xx <= maxX) {
            this.setBlockState(worldIn, blockWall, xx, 7, midPoint, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockWall, xx, 8, midPoint, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockBars, xx, 3, midPoint + 1, structureBoundingBoxIn)
            this.setBlockState(worldIn, blockBars, xx, maxY - 3, midPoint + 1, structureBoundingBoxIn)
            xx += maxX
        }
        //Extend the walkways a little way also
        this.setBlockState(worldIn, blockGrid, 1, 1, midPoint + 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockGrid, 2, 1, midPoint + 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockGrid, 5, 0, midPoint + 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockGrid, 21, 0, midPoint + 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockGrid, 24, 1, midPoint + 1, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockGrid, 25, 1, midPoint + 1, structureBoundingBoxIn)

        return true
    }

    private fun floorStrut(
        worldIn: World,
        blockWall: IBlockState,
        decoWall: IBlockState,
        zz: Int,
        structureBoundingBoxIn: BoundingBox
    ) {
        this.setBlockState(worldIn, blockWall, 1, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, decoWall, 2, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockWall, 3, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, decoWall, 4, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockWall, 5, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, decoWall, 6, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, decoWall, 20, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockWall, 21, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, decoWall, 22, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockWall, 23, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, decoWall, 24, 0, zz, structureBoundingBoxIn)
        this.setBlockState(worldIn, blockWall, 25, 0, zz, structureBoundingBoxIn)
    }

    private fun extrude(
        worldIn: World,
        rand: Random,
        solid: Int,
        blockstateIn: IBlockState,
        x: Int,
        y: Int,
        z: Int,
        boundingBoxIn: BoundingBox
    ) {
        for (zz: Int in z downTo rand.nextInt(4 * (4 - solid))) this.setBlockState(
            worldIn,
            blockstateIn,
            x,
            y,
            zz,
            boundingBoxIn
        )
    }

    private fun extrudeDuo(
        worldIn: World,
        rand: Random,
        solid: Int,
        blockA: IBlockState,
        blockB: IBlockState,
        x: Int,
        y: Int,
        z: Int,
        boundingBoxIn: BoundingBox
    ) {
        for (zz: Int in z downTo rand.nextInt(4 * (4 - solid))) this.setBlockState(
            worldIn,
            if (zz % 2 == 0) blockA else blockB,
            x,
            y,
            zz,
            boundingBoxIn
        )
    }

    private fun extrudeTrio(
        worldIn: World,
        rand: Random,
        solid: Int,
        blockA: IBlockState,
        blockB: IBlockState,
        x: Int,
        y: Int,
        z: Int,
        boundingBoxIn: BoundingBox
    ) {
        for (zz: Int in z downTo rand.nextInt(4 * (4 - solid))) this.setBlockState(
            worldIn,
            if (zz % 3 == 0) blockA else blockB,
            x,
            y,
            zz,
            boundingBoxIn
        )
    }

    private fun extrudeTrioOff(
        worldIn: World,
        rand: Random,
        solid: Int,
        blockA: IBlockState,
        blockB: IBlockState,
        x: Int,
        y: Int,
        z: Int,
        boundingBoxIn: BoundingBox
    ) {
        for (zz: Int in z downTo rand.nextInt(4 * (4 - solid))) this.setBlockState(
            worldIn,
            if (zz % 3 == 1) blockA else blockB,
            x,
            y,
            zz,
            boundingBoxIn
        )
    }

    companion object {
        const val HANGARWIDTH: Int = 26

        const val HANGARLENGTH: Int = 42

        const val HANGARHEIGHT: Int = 15
    }
}
