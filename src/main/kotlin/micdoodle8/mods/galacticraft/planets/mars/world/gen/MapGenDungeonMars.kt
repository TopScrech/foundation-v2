package micdoodle8.mods.galacticraft.planets.mars.world.gen

import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import net.minecraft.world.gen.structure.MapGenStructureIO

open class MapGenDungeonMars constructor(configuration: DungeonConfiguration?) : MapGenDungeon(configuration) {
    companion object {
        private var initialized = false

        init {
            try {
                initiateStructures()
            } catch (e: Throwable) {
            }
        }

        @JvmStatic
        @Throws(Throwable::class)
        fun initiateStructures() {
            if (!initialized) {
                MapGenStructureIO.registerStructureComponent(RoomBossMars::class.java, "MarsDungeonBossRoom")
                MapGenStructureIO.registerStructureComponent(RoomTreasureMars::class.java, "MarsDungeonTreasureRoom")
            }

            initialized = true
        }
    }
}
