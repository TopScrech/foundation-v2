package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner

interface IBoss {
    fun onBossSpawned(spawner: TileEntityDungeonSpawner<*>?)
}
