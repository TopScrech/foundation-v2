package galaxyspace.systems.SolarSystem.moons.io.blocks

import galaxyspace.systems.SolarSystem.moons.io.tile.TileEntityDungeonSpawnerIo
import micdoodle8.mods.galacticraft.core.blocks.BlockBossSpawner
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

open class BlockBossSpawnerIo constructor(assetName: String?) : BlockBossSpawner(assetName) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityDungeonSpawnerIo()
    }
}
