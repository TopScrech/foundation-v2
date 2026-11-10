package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.util.Random

open class RoomSpawner : RoomEmpty {
    constructor()

    constructor(
        configuration: DungeonConfiguration?,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        entranceDir: EnumFacing
    ) : super((configuration)!!, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir)

    override fun addComponentParts(worldIn: World, random: Random, boundingBox: StructureBoundingBox): Boolean {
        if (super.addComponentParts(worldIn, random, boundingBox)) {
            for (i: Int in 1..this.sizeX - 1) {
                for (j: Int in 1..this.sizeY - 1) {
                    for (k: Int in 1..this.sizeZ - 1) {
                        if (random.nextFloat() < 0.05f) {
                            this.setBlockState(worldIn, Blocks.WEB.getDefaultState(), i, j, k, boundingBox)
                        }
                    }
                }
            }

            this.setBlockState(worldIn, Blocks.MOB_SPAWNER.getDefaultState(), 1, 0, 1, boundingBox)
            this.setBlockState(
                worldIn,
                Blocks.MOB_SPAWNER.getDefaultState(),
                this.sizeX - 1,
                0,
                this.sizeZ - 1,
                boundingBox
            )

            var blockpos: BlockPos =
                BlockPos(this.getXWithOffset(1, 1), this.getYWithOffset(0), this.getZWithOffset(1, 1))
            var spawner: TileEntityMobSpawner? = worldIn.getTileEntity(blockpos) as TileEntityMobSpawner?

            if (spawner != null) {
                spawner.getSpawnerBaseLogic().setEntityId(getMob(random))
            }

            blockpos = BlockPos(
                this.getXWithOffset(this.sizeX - 1, this.sizeZ - 1),
                this.getYWithOffset(0),
                this.getZWithOffset(this.sizeX - 1, this.sizeZ - 1)
            )
            spawner = worldIn.getTileEntity(blockpos) as TileEntityMobSpawner?

            if (spawner != null) {
                spawner.getSpawnerBaseLogic().setEntityId(getMob(random))
            }

            return true
        }

        return false
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
