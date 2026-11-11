package micdoodle8.mods.galacticraft.planets.venus.blocks

import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityTreasureChestVenus
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.level.Level as World

open class BlockTier3TreasureChest constructor(assetName: String?) : BlockTier1TreasureChest(assetName) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityTreasureChestVenus()
    }
}
