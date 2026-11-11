package micdoodle8.mods.galacticraft.planets.venus.blocks

import micdoodle8.mods.galacticraft.core.blocks.BlockBossSpawner
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityDungeonSpawnerVenus
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.level.Level as World

open class BlockBossSpawnerVenus constructor(assetName: String?) : BlockBossSpawner(assetName) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityDungeonSpawnerVenus()
    }
}
