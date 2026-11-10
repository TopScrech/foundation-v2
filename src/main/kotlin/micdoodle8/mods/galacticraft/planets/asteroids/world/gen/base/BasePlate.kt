package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
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
            StructureBoundingBox(blockPosX, yPos, blockPosZ, blockPosX + this.sizeX, yPos, blockPosZ + this.sizeZ)
    }

    override fun addComponentParts(worldIn: World, random: Random, boundingBox: StructureBoundingBox): Boolean {
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
