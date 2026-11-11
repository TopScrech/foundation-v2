package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import java.util.Random

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import net.minecraft.world.level.block.Blocks
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.*

open class RoomChestVenus : RoomEmptyVenus {
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

    override fun addComponentParts(worldIn: World, rand: Random, boundingBox: BoundingBox): Boolean {
        if (super.addComponentParts(worldIn, rand, boundingBox)) {
            val chestX: Int = this.sizeX / 2
            val chestY: Int = 1
            val chestZ: Int = this.sizeZ / 2
            this.setBlockState(
                worldIn,
                Blocks.CHEST.defaultBlockState().setValue(
                    BlockTier1TreasureChest.FACING,
                    (this.getDirection())!!.getOpposite()
                ),
                chestX,
                chestY,
                chestZ,
                boundingBox
            )

            val blockpos: BlockPos = BlockPos(
                this.getXWithOffset(chestX, chestZ),
                this.getYWithOffset(chestY),
                this.getZWithOffset(chestX, chestZ)
            )
            val chest: TileEntityChest? = worldIn.getBlockEntity(blockpos) as TileEntityChest?

            if (chest != null) {
                var chesttype: ResourceLocation? = RoomTreasureVenus.VENUSCHEST
                if (worldIn.provider is IGalacticraftWorldProvider) {
                    chesttype = (worldIn.provider as IGalacticraftWorldProvider).getDungeonChestType()
                }
                chest.setLootTable(RoomTreasureVenus.VENUSCHEST, rand.nextLong())
            }

            return true
        }

        return false
    }
}
