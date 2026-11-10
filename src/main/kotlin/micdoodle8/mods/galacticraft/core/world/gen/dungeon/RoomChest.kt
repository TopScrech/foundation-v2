package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.util.Random

open class RoomChest : RoomEmpty {
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

    override fun addComponentParts(worldIn: World, rand: Random, boundingBox: StructureBoundingBox): Boolean {
        if (super.addComponentParts(worldIn, rand, boundingBox)) {
            val chestX: Int = this.sizeX / 2
            val chestY: Int = 1
            val chestZ: Int = this.sizeZ / 2
            this.setBlockState(
                worldIn,
                Blocks.CHEST.getDefaultState().withProperty(
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
            val chest: TileEntityChest? = worldIn.getTileEntity(blockpos) as TileEntityChest?

            if (chest != null) {
                var chesttype: ResourceLocation = RoomTreasure.Companion.MOONCHEST
                if (worldIn.provider is IGalacticraftWorldProvider) {
                    chesttype = ((worldIn.provider as IGalacticraftWorldProvider).getDungeonChestType())!!
                }
                chest.setLootTable(chesttype, rand.nextLong())
            }

            return true
        }

        return false
    }
}
