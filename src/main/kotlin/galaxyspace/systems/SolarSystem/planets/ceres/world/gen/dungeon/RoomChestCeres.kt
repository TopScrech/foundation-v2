package galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon

import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomEmptyCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomChestCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomTreasureCeres
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.util.Random

open class RoomChestCeres : RoomEmptyCeres {
    constructor()

    constructor(
        configuration: DungeonConfiguration,
        rand: Random,
        blockPosX: Int,
        blockPosZ: Int,
        entranceDir: EnumFacing
    ) : this(
        configuration,
        rand,
        blockPosX,
        blockPosZ,
        rand.nextInt(4) + 8,
        configuration.getRoomHeight(),
        rand.nextInt(4) + 8,
        entranceDir
    )

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

    override fun addComponentParts(worldIn: World, rand: Random, boundingBox: StructureBoundingBox): Boolean {
        if (super.addComponentParts(worldIn, rand, boundingBox)) {
            val chestX: Int = this.sizeX / 2
            val chestY: Int = 2
            val chestZ: Int = this.sizeZ / 2


            for (x: Int in -1..1) for (z: Int in -1..1) {
                this.setBlockState(
                    worldIn,
                    Blocks.PACKED_ICE.getDefaultState(),
                    chestX + x,
                    chestY - 1,
                    chestZ + z,
                    boundingBox
                )

                for (y: Int in 0..1) {
                    if (x % 2 != 0 && z % 2 != 0) this.setBlockState(
                        worldIn,
                        Blocks.PACKED_ICE.getDefaultState(),
                        chestX + x,
                        chestY + y,
                        chestZ + z,
                        boundingBox
                    )
                    else this.setBlockState(
                        worldIn,
                        Blocks.ICE.getDefaultState(),
                        chestX + x,
                        chestY + y,
                        chestZ + z,
                        boundingBox
                    )
                }

                if (x != 0 || z != 0) for (y: Int in 2..<(this.configuration)!!.getRoomHeight() - 2) this.setBlockState(
                    worldIn,
                    Blocks.PACKED_ICE.getDefaultState(),
                    chestX + x,
                    chestY + y,
                    chestZ + z,
                    boundingBox
                )
            }


            this.setBlockState(
                worldIn, Blocks.CHEST.getDefaultState().withProperty(
                    BlockTier1TreasureChest.FACING,
                    (this.getDirection())!!.getOpposite()
                ), chestX, chestY, chestZ, boundingBox
            )

            val blockpos: BlockPos = BlockPos(
                this.getXWithOffset(chestX, chestZ), this.getYWithOffset(chestY),
                this.getZWithOffset(chestX, chestZ)
            )
            val chest: TileEntityChest? = worldIn.getTileEntity(blockpos) as TileEntityChest?

            if (chest != null) {
                var chesttype: ResourceLocation = RoomTreasureCeres.Companion.CERESCHEST
                if (worldIn.provider is IGalacticraftWorldProvider) {
                    chesttype = ((worldIn.provider as IGalacticraftWorldProvider).getDungeonChestType())!!
                }
                chest.setLootTable(chesttype, rand.nextLong())
            }

            this.setBlockState(worldIn, Blocks.MOB_SPAWNER.getDefaultState(), chestX, chestY + 1, chestZ, boundingBox)
            val spawner: TileEntityMobSpawner? = worldIn.getTileEntity(blockpos.up()) as TileEntityMobSpawner?

            if (spawner != null) {
                spawner.getSpawnerBaseLogic().setEntityId(getMob(rand))
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
