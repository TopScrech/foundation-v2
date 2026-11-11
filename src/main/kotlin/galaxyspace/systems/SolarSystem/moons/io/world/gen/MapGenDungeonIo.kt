package galaxyspace.systems.SolarSystem.moons.io.world.gen

import galaxyspace.systems.SolarSystem.moons.io.world.gen.MapGenDungeonIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.CorridorIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomEmptyIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomSpawnerIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomChestIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomBossIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomTreasureIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.MapGenGCDungeon
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DungeonStartIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.structure.MapGenStructureIO
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.StructureStart
import java.util.Random

open class MapGenDungeonIo constructor(private val configuration: DungeonConfiguration?) :
    MapGenGCDungeon(configuration) {
    override fun getStructureStart(chunkX: Int, chunkZ: Int): StructureStart {
        return Start(this.world, this.rand, chunkX, chunkZ, this.configuration)
    }

    override fun getStructureName(): String {
        return "GS_Dungeon_Io"
    }

    override fun getNearestStructurePos(worldIn: World, pos: BlockPos, p_180706_3_: Boolean): BlockPos? {
        return null
    }

    open class Start : StructureStart {
        private var configuration: DungeonConfiguration? = null

        constructor()

        constructor(
            worldIn: World?,
            rand: Random,
            chunkX: Int,
            chunkZ: Int,
            configuration: DungeonConfiguration?
        ) : super(chunkX, chunkZ) {
            this.configuration = configuration
            val startPiece: DungeonStartIo =
                DungeonStartIo(worldIn, configuration, rand, (chunkX shl 4) + 2, (chunkZ shl 4) + 2)
            startPiece.buildComponent(startPiece, this.components, rand)
            val list: MutableList<StructureComponent> = (startPiece.attachedComponents as MutableList<StructureComponent>)

            while (!list.isEmpty()) {
                val i: Int = rand.nextInt(list.size)
                val structurecomponent: StructureComponent = list.removeAt(i)
                structurecomponent.buildComponent(startPiece, this.components, rand)
            }

            this.updateBoundingBox()
        }
    }

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
                MapGenStructureIO.registerStructure(Start::class.java, "IoDungeon")
                MapGenStructureIO.registerStructureComponent(DungeonStartIo::class.java, "IoDungeonStart")
                MapGenStructureIO.registerStructureComponent(CorridorIo::class.java, "IoDungeonCorridor")
                MapGenStructureIO.registerStructureComponent(RoomEmptyIo::class.java, "IoDungeonEmptyRoom")
                MapGenStructureIO.registerStructureComponent(RoomSpawnerIo::class.java, "IoDungeonSpawnerRoom")
                MapGenStructureIO.registerStructureComponent(RoomChestIo::class.java, "IoDungeonChestRoom")
                MapGenStructureIO.registerStructureComponent(RoomBossIo::class.java, "IoDungeonBossRoom")
                MapGenStructureIO.registerStructureComponent(RoomTreasureIo::class.java, "IoDungeonTreasureRoom")
            }

            initialized = true
        }
    }
}
