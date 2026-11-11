package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.Random

open class BasePlate : SizedPiece {
    constructor()

    constructor(
        configuration: BaseConfiguration?,
        blockPosX: Int,
        yPos: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeZ: Int,
        dir: EnumFacing?
    ) : super(configuration, sizeX, 1, sizeZ, dir) {
        this.setCoordBaseMode(dir)
        this.boundingBox =
            BoundingBox(blockPosX, yPos, blockPosZ, blockPosX + this.sizeX, yPos, blockPosZ + this.sizeZ)
    }

    override fun addComponentParts(worldIn: World, random: Random, boundingBox: BoundingBox): Boolean {
        val blockWall: IBlockState = ((this.configuration)!!.getWallBlock())!!
        val axisEW: Boolean = (getDirection())!!.getAxis() == EnumFacing.Axis.X
        val maxX: Int = if (axisEW) this.sizeZ else this.sizeX
        val maxZ: Int = if (axisEW) this.sizeX else this.sizeZ
        for (xx: Int in 0..maxX) {
            for (zz: Int in 0..maxZ) {
                this.setBlockState(worldIn, blockWall, xx, 0, zz, boundingBox)
            }
        }

        return true
    }
}
