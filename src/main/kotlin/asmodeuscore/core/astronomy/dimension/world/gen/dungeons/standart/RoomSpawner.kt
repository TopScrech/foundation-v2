package asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.world.level.block.Blocks
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
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

    override fun addComponentParts(worldIn: World, random: Random, boundingBox: BoundingBox): Boolean {
        if (super.addComponentParts(worldIn, random, boundingBox)) {
            for (i: Int in 1..this.sizeX - 1) {
                for (j: Int in 1..this.sizeY - 1) {
                    for (k: Int in 1..this.sizeZ - 1) {
                        if (random.nextFloat() < 0.05f) {
                            this.setBlockState(worldIn, Blocks.WEB.defaultBlockState(), i, j, k, boundingBox)
                        }
                    }
                }
            }

            this.setBlockState(worldIn, Blocks.MOB_SPAWNER.defaultBlockState(), 1, 0, 1, boundingBox)
            this.setBlockState(
                worldIn,
                Blocks.MOB_SPAWNER.defaultBlockState(),
                this.sizeX - 1,
                0,
                this.sizeZ - 1,
                boundingBox
            )

            var blockpos: BlockPos =
                BlockPos(this.getXWithOffset(1, 1), this.getYWithOffset(0), this.getZWithOffset(1, 1))
            var spawner: TileEntityMobSpawner? = worldIn.getBlockEntity(blockpos) as TileEntityMobSpawner?

            if (spawner != null) {
                spawner.getSpawnerBaseLogic().setEntityId(getMob(random))
            }

            blockpos = BlockPos(
                this.getXWithOffset(this.sizeX - 1, this.sizeZ - 1),
                this.getYWithOffset(0),
                this.getZWithOffset(this.sizeX - 1, this.sizeZ - 1)
            )
            spawner = worldIn.getBlockEntity(blockpos) as TileEntityMobSpawner?

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
                0 -> return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_CORE, "evolved_spider")
                1 -> return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_CORE, "evolved_creeper")
                2 -> return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_CORE, "evolved_skeleton")
                3 -> return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_CORE, "evolved_zombie")
                else -> return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_CORE, "evolved_zombie")
            }
        }
    }
}
