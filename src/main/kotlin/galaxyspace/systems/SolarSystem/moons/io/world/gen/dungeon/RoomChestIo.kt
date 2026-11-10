package galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon

import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomEmptyIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomChestIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomTreasureIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.block.BlockChest
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.util.Random

open class RoomChestIo : RoomEmptyIo {
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
        rand.nextInt(4) + 14,
        configuration.getRoomHeight(),
        rand.nextInt(4) + 14,
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
            val chestY: Int = 1
            val chestZ: Int = this.sizeZ / 2


            for (x: Int in -2..2) for (z: Int in -2..2) {
                //if((x == -3 || x == 2) && (z == -3 || z == 2))
                //this.setBlockState(worldIn, Blocks.PACKED_ICE.getDefaultState(), chestX + x, 1, chestZ + z, boundingBox);

                /*for(int y = 0; y < 2; y++) {
						if(x % 2 != 0 && z % 2 != 0) 
							this.setBlockState(worldIn, Blocks.PACKED_ICE.getDefaultState(), chestX + x, chestY + y, chestZ + z, boundingBox);
						else 
							this.setBlockState(worldIn, Blocks.ICE.getDefaultState(), chestX + x, chestY + y, chestZ + z, boundingBox);
					}*/

                //if(x != 0 || z != 0)
                //for(int y = 2; y < this.configuration.getRoomHeight() - 2; y++)
                //this.setBlockState(worldIn, Blocks.PACKED_ICE.getDefaultState(), chestX + x, chestY + y, chestZ + z, boundingBox);
            }


            this.setBlockState(
                worldIn, Blocks.CHEST.getDefaultState().withProperty(
                    BlockChest.FACING,
                    (this.getDirection())!!.getOpposite()
                ), chestX, chestY, chestZ, boundingBox
            )

            val blockpos: BlockPos = BlockPos(
                this.getXWithOffset(chestX, chestZ), this.getYWithOffset(chestY),
                this.getZWithOffset(chestX, chestZ)
            )
            val chest: TileEntityChest? = worldIn.getTileEntity(blockpos) as TileEntityChest?

            if (chest != null) {
                var chesttype: ResourceLocation = RoomTreasureIo.Companion.IOCHEST
                if (worldIn.provider is IGalacticraftWorldProvider) {
                    chesttype = ((worldIn.provider as IGalacticraftWorldProvider).getDungeonChestType())!!
                }
                chest.setLootTable(chesttype, rand.nextLong())
            }

            /*
			this.setBlockState(worldIn, Blocks.MOB_SPAWNER.getDefaultState(), chestX, chestY - 1, chestZ, boundingBox);
			TileEntityMobSpawner spawner = (TileEntityMobSpawner) worldIn.getTileEntity(new BlockPos(chestX, chestY - 1, chestZ));
			  
			if (spawner != null)
            {
                spawner.getSpawnerBaseLogic().setEntityId(getMob(rand));
            }*/
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
