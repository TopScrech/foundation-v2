package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.util.Random

open class RoomSpawnerVenus : RoomEmptyVenus {
    constructor()

    constructor(
        configuration: DungeonConfigurationVenus?,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        entranceDir: EnumFacing
    ) : super((configuration)!!, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir)

    override fun addComponentParts(worldIn: World, random: Random, chunkBox: StructureBoundingBox): Boolean {
        if (super.addComponentParts(worldIn, random, chunkBox)) {
            for (i: Int in 1..this.sizeX - 1) {
                for (j: Int in 1..this.sizeY - 1) {
                    for (k: Int in 1..this.sizeZ - 1) {
                        if (random.nextFloat() < 0.05f) {
                            this.setBlockState(worldIn, Blocks.WEB.getDefaultState(), i, j, k, chunkBox)
                        }
                    }
                }
            }

            this.placeMobSpawner(worldIn, random, chunkBox, 1, 0, 1)
            this.placeMobSpawner(worldIn, random, chunkBox, this.sizeX - 1, 0, this.sizeZ - 1)

            return true
        }

        return false
    }

    private fun placeMobSpawner(
        worldIn: World,
        random: Random,
        chunkBox: StructureBoundingBox,
        x: Int,
        y: Int,
        z: Int
    ) {
        this.setBlockState(worldIn, Blocks.MOB_SPAWNER.getDefaultState(), 1, 0, 1, boundingBox)
        val blockpos: BlockPos = BlockPos(this.getXWithOffset(1, 1), this.getYWithOffset(0), this.getZWithOffset(1, 1))
        val spawner: TileEntityMobSpawner? = worldIn.getTileEntity(blockpos) as TileEntityMobSpawner?

        if (spawner != null) {
            spawner.getSpawnerBaseLogic().setEntityId(getMob(random))
        }
    }

    companion object {
        @JvmStatic
        private fun getMob(rand: Random): ResourceLocation {
            when (rand.nextInt(4)) {
                0 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_spider")
                1 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_creeper")
                2 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_skeleton")
                3 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_zombie")
                else -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_zombie")
            }
        }
    }
}
