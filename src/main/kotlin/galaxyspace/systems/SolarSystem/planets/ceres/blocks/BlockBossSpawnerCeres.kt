package galaxyspace.systems.SolarSystem.planets.ceres.blocks

import galaxyspace.systems.SolarSystem.planets.ceres.tile.TileEntityDungeonSpawnerCeres
import micdoodle8.mods.galacticraft.core.blocks.BlockBossSpawner
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.level.Level as World

open class BlockBossSpawnerCeres constructor(assetName: String?) : BlockBossSpawner(assetName) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityDungeonSpawnerCeres()
    }
}
