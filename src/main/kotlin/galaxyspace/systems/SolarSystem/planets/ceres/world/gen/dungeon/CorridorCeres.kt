package galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.CorridorCeres
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.Corridor
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.Random

open class CorridorCeres : Corridor {
    constructor()

    constructor(
        configuration: DungeonConfiguration,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        direction: EnumFacing?
    ) : super(configuration, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, direction) {
        this.setCoordBaseMode(EnumFacing.SOUTH)
        this.boundingBox = BoundingBox(
            blockPosX,
            configuration.getYPosition(),
            blockPosZ,
            blockPosX + sizeX,
            configuration.getYPosition() + sizeY,
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
                        if (j == (this.configuration)!!.getHallwayHeight()) this.setBlockState(
                            worldIn,
                            GSBlocks.CERES_BLOCKS.defaultBlockState().setValue(
                                CeresBlocks.BASIC_TYPE,
                                EnumCeresBlocks.CERES_DUNGEON_TOP
                            ),
                            i,
                            j,
                            k,
                            boundingBox
                        )
                        else if (j == 0) this.setBlockState(
                            worldIn,
                            GSBlocks.CERES_BLOCKS.defaultBlockState().setValue(
                                CeresBlocks.BASIC_TYPE,
                                EnumCeresBlocks.CERES_DUNGEON_FLOOR
                            ),
                            i,
                            j,
                            k,
                            boundingBox
                        )
                        else this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, boundingBox)
                    } else {
                        if (j == this.boundingBox.getYSpan() - 4) {
                            if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.Z && (k + 1) % 4 == 0 && (i == 1 || i == this.boundingBox.getXSpan() - 2)
                            ) {
                                this.setBlockState(
                                    worldIn,
                                    (GCBlocks.unlitTorch)!!.defaultBlockState().setValue(
                                        BlockUnlitTorch.FACING,
                                        if (i == 1) EnumFacing.WEST.getOpposite() else EnumFacing.EAST.getOpposite()
                                    ),
                                    i,
                                    j,
                                    k,
                                    this.boundingBox
                                )
                                continue
                            } else if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.X && (i + 1) % 4 == 0 && (k == 1 || k == this.boundingBox.getZSpan() - 2)
                            ) {
                                this.setBlockState(
                                    worldIn,
                                    (GCBlocks.unlitTorch)!!.defaultBlockState().setValue(
                                        BlockUnlitTorch.FACING,
                                        if (k == 1) EnumFacing.NORTH.getOpposite() else EnumFacing.SOUTH.getOpposite()
                                    ),
                                    i,
                                    j,
                                    k,
                                    this.boundingBox
                                )
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
