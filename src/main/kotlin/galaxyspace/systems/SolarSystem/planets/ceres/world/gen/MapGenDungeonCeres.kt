package galaxyspace.systems.SolarSystem.planets.ceres.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.MapGenGCDungeon
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.MapGenDungeonCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.CorridorCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomEmptyCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomSpawnerCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomChestCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomBossCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomTreasureCeres
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import net.minecraft.world.gen.structure.MapGenStructureIO

open class MapGenDungeonCeres constructor(configuration: DungeonConfiguration?) : MapGenGCDungeon(configuration) {
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
                MapGenStructureIO.registerStructureComponent(CorridorCeres::class.java, "CeresDungeonCorridor")
                MapGenStructureIO.registerStructureComponent(RoomEmptyCeres::class.java, "CeresDungeonEmptyRoom")
                MapGenStructureIO.registerStructureComponent(RoomSpawnerCeres::class.java, "CeresDungeonSpawnerRoom")
                MapGenStructureIO.registerStructureComponent(RoomChestCeres::class.java, "CeresDungeonChestRoom")
                MapGenStructureIO.registerStructureComponent(RoomBossCeres::class.java, "CeresDungeonBossRoom")
                MapGenStructureIO.registerStructureComponent(RoomTreasureCeres::class.java, "CeresDungeonTreasureRoom")
            }

            initialized = true
        }
    }
}
