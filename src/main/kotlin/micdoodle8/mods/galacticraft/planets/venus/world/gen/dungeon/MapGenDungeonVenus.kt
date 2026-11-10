package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import java.util.Random

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.MapGenStructure
import net.minecraft.world.gen.structure.MapGenStructureIO
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.StructureStart
import java.util.*

open class MapGenDungeonVenus constructor(private val configuration: DungeonConfigurationVenus?) :
    MapGenStructure() {
    override fun getStructureName(): String {
        return "GC_Dungeon_Venus"
    }

    override fun canSpawnStructureAtCoords(chunkX: Int, chunkZ: Int): Boolean {
        val dungeonPos: Long = MapGenDungeon.getDungeonPosForCoords(
            this.world,
            chunkX,
            chunkZ,
            (this.world.provider as IGalacticraftWorldProvider).getDungeonSpacing()
        )
        val i: Int = (dungeonPos shr 32).toInt()
        val j: Int = dungeonPos.toInt()
        return i == chunkX && j == chunkZ
    }

    override fun getStructureStart(chunkX: Int, chunkZ: Int): StructureStart {
        return Start(this.world, this.rand, chunkX, chunkZ, this.configuration)
    }

    override fun getNearestStructurePos(worldIn: World, pos: BlockPos, p_180706_3_: Boolean): BlockPos? {
        return null
    }

    open class Start : StructureStart {
        private var configuration: DungeonConfigurationVenus? = null

        constructor()

        constructor(
            worldIn: World?,
            rand: Random,
            chunkX: Int,
            chunkZ: Int,
            configuration: DungeonConfigurationVenus?
        ) : super(chunkX, chunkZ) {
            this.configuration = configuration
            val startPiece: DungeonStartVenus =
                DungeonStartVenus(worldIn, configuration, rand, (chunkX shl 4) + 2, (chunkZ shl 4) + 2)
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
                MapGenStructureIO.registerStructure(Start::class.java, "VenusDungeon")
                MapGenStructureIO.registerStructureComponent(DungeonStartVenus::class.java, "VenusDungeonStart")
                MapGenStructureIO.registerStructureComponent(CorridorVenus::class.java, "VenusDungeonCorridor")
                MapGenStructureIO.registerStructureComponent(RoomEmptyVenus::class.java, "VenusDungeonEmptyRoom")
                MapGenStructureIO.registerStructureComponent(RoomBossVenus::class.java, "VenusDungeonBossRoom")
                MapGenStructureIO.registerStructureComponent(RoomTreasureVenus::class.java, "VenusDungeonTreasureRoom")
                MapGenStructureIO.registerStructureComponent(RoomSpawnerVenus::class.java, "VenusDungeonSpawnerRoom")
                MapGenStructureIO.registerStructureComponent(RoomChestVenus::class.java, "VenusDungeonChestRoom")
            }

            initialized = true
        }
    }
}
