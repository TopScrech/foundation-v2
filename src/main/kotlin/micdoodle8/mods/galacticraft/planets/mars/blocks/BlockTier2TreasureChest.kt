package micdoodle8.mods.galacticraft.planets.mars.blocks

import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTreasureChestMars
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.level.Level as World

open class BlockTier2TreasureChest constructor(assetName: String?) : BlockTier1TreasureChest(assetName) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityTreasureChestMars()
    }
}
