package micdoodle8.mods.galacticraft.planets.mars.blocks

import micdoodle8.mods.galacticraft.core.blocks.BlockBossSpawner
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityDungeonSpawnerMars
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.level.Level as World

open class BlockBossSpawnerMars constructor(assetName: String?) : BlockBossSpawner(assetName) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityDungeonSpawnerMars()
    }
}
