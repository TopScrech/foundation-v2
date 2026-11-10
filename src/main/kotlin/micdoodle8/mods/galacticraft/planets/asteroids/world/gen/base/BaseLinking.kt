package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
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
        this.boundingBox = StructureBoundingBox(
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
        structureBoundingBoxIn: StructureBoundingBox
    ): Boolean {
        for (i: Int in 0..<this.boundingBox.getXSize()) {
            for (j: Int in 0..<this.boundingBox.getYSize()) {
                for (k: Int in 0..<this.boundingBox.getZSize()) {
                    if (((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && (i == 0 || i == this.boundingBox.getXSize() - 1)) || j == 0 || j == this.boundingBox.getYSize() - 1 ||
                        ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && (k == 0 || k == this.boundingBox.getZSize() - 1))
                    ) {
                        this.setBlockState(worldIn, (this.configuration)!!.getWallBlock(), i, j, k, this.boundingBox)
                    } else {
                        if (j == this.boundingBox.getYSize() - 2) {
                            if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.Z && (k + 1) % 4 == 0 && (i == 1 || i == this.boundingBox.getXSize() - 2)
                            ) {
                                //TODO: windows or decor
//                                this.setBlockState(worldIn, GCBlocks.unlitTorch.getDefaultState().withProperty(BlockUnlitTorch.FACING, i == 1 ? EnumFacing.WEST.getOpposite() : EnumFacing.EAST.getOpposite()), i, j, k, this.boundingBox);
                                continue
                            } else if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.X && (i + 1) % 4 == 0 && (k == 1 || k == this.boundingBox.getZSize() - 2)
                            ) {
//                                this.setBlockState(worldIn, GCBlocks.unlitTorch.getDefaultState().withProperty(BlockUnlitTorch.FACING, k == 1 ? EnumFacing.NORTH.getOpposite() : EnumFacing.SOUTH.getOpposite()), i, j, k, this.boundingBox);
                                continue
                            }
                        }

                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, this.boundingBox)
                    }
                }
            }
        }

        return true
    }
}
