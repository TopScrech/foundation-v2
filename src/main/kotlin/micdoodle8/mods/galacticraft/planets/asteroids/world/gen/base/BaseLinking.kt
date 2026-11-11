package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.Random

open class BaseLinking : SizedPiece {
    constructor()

    constructor(
        configuration: BaseConfiguration?,
        rand: Random?,
        blockPosX: Int,
        blockPosY: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        direction: EnumFacing?
    ) : super(configuration, sizeX, sizeY, sizeZ, direction) {
        this.setCoordBaseMode(EnumFacing.SOUTH)
        this.boundingBox = BoundingBox(
            blockPosX,
            blockPosY,
            blockPosZ,
            blockPosX + sizeX,
            blockPosY + sizeY,
            blockPosZ + sizeZ
        )
    }

    override fun addComponentParts(
        worldIn: World,
        randomIn: Random,
        structureBoundingBoxIn: BoundingBox
    ): Boolean {
        for (i: Int in 0..<this.boundingBox.getXSpan()) {
            for (j: Int in 0..<this.boundingBox.getYSpan()) {
                for (k: Int in 0..<this.boundingBox.getZSpan()) {
                    if (((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && (i == 0 || i == this.boundingBox.getXSpan() - 1)) || j == 0 || j == this.boundingBox.getYSpan() - 1 ||
                        ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && (k == 0 || k == this.boundingBox.getZSpan() - 1))
                    ) {
                        this.setBlockState(worldIn, (this.configuration)!!.getWallBlock(), i, j, k, this.boundingBox)
                    } else {
                        if (j == this.boundingBox.getYSpan() - 2) {
                            if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.Z && (k + 1) % 4 == 0 && (i == 1 || i == this.boundingBox.getXSpan() - 2)
                            ) {
                                //TODO: windows or decor
//                                this.setBlockState(worldIn, GCBlocks.unlitTorch.defaultBlockState().setValue(BlockUnlitTorch.FACING, i == 1 ? EnumFacing.WEST.getOpposite() : EnumFacing.EAST.getOpposite()), i, j, k, this.boundingBox);
                                continue
                            } else if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.X && (i + 1) % 4 == 0 && (k == 1 || k == this.boundingBox.getZSpan() - 2)
                            ) {
//                                this.setBlockState(worldIn, GCBlocks.unlitTorch.defaultBlockState().setValue(BlockUnlitTorch.FACING, k == 1 ? EnumFacing.NORTH.getOpposite() : EnumFacing.SOUTH.getOpposite()), i, j, k, this.boundingBox);
                                continue
                            }
                        }

                        this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, this.boundingBox)
                    }
                }
            }
        }

        return true
    }
}
