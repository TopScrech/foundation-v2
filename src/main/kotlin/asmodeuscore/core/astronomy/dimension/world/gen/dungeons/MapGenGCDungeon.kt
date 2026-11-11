package asmodeuscore.core.astronomy.dimension.world.gen.dungeons

import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.MapGenGCDungeon
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.Corridor
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonStart
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.RoomBoss
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.RoomChest
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.RoomEmpty
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.RoomSpawner
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.RoomTreasure
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.structure.MapGenStructure
import net.minecraft.world.gen.structure.MapGenStructureIO
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.StructureStart
import java.util.Random

open class MapGenGCDungeon constructor(private val configuration: DungeonConfiguration?) : MapGenStructure() {
    override fun getStructureName(): String {
        return "GS_Dungeon"
    }

    override fun canSpawnStructureAtCoords(chunkX: Int, chunkZ: Int): Boolean {
        val dungeonPos: Long = getDungeonPosForCoords(
            this.world,
            chunkX,
            chunkZ,
            (this.world.provider as IGalacticraftWorldProvider).getDungeonSpacing()
        )
        val i: Int = (dungeonPos shr 32).toInt()
        val j: Int = dungeonPos.toInt() //Java automatically gives the 32 least significant bits
        return i == chunkX && j == chunkZ
    }

    override fun getStructureStart(chunkX: Int, chunkZ: Int): StructureStart {
        return Start(this.world, this.rand, chunkX, chunkZ, this.configuration)
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
            val startPiece: DungeonStart =
                DungeonStart(worldIn, configuration, rand, (chunkX shl 4) + 2, (chunkZ shl 4) + 2)
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
                MapGenStructureIO.registerStructure(Start::class.java, "MoonDungeon")
                MapGenStructureIO.registerStructureComponent(DungeonStart::class.java, "MoonDungeonStart")
                MapGenStructureIO.registerStructureComponent(Corridor::class.java, "MoonDungeonCorridor")
                MapGenStructureIO.registerStructureComponent(RoomEmpty::class.java, "MoonDungeonEmptyRoom")
                MapGenStructureIO.registerStructureComponent(RoomBoss::class.java, "MoonDungeonBossRoom")
                MapGenStructureIO.registerStructureComponent(RoomTreasure::class.java, "MoonDungeonTreasureRoom")
                MapGenStructureIO.registerStructureComponent(RoomSpawner::class.java, "MoonDungeonSpawnerRoom")
                MapGenStructureIO.registerStructureComponent(RoomChest::class.java, "MoonDungeonChestRoom")
            }

            initialized = true
        }

        @JvmStatic
        fun getDungeonPosForCoords(world: World, chunkX: Int, chunkZ: Int, spacing: Int): Long {
            var chunkX: Int = chunkX
            var chunkZ: Int = chunkZ
            val numChunks: Int = spacing / 16
            if (chunkX < 0) {
                chunkX -= numChunks - 1
            }

            if (chunkZ < 0) {
                chunkZ -= numChunks - 1
            }

            var k: Int = chunkX / numChunks
            var l: Int = chunkZ / numChunks
            val seed: Long = k.toLong() * 341873128712L + l.toLong() * 132897987541L + world.getWorldInfo()
                .getSeed() + (10387340 + world.provider.getDimension()).toLong()
            val random: Random = Random()
            random.setSeed(seed)
            k = k * numChunks + random.nextInt(numChunks)
            l = l * numChunks + random.nextInt(numChunks)
            return ((k.toLong()) shl 32) + l
        }

        /**
         * This returns an angle between 0 and 360 degrees.  0 degrees means due North from the current (x, z) position
         * Only provides meaningful results in worlds with dungeon generation using this class!
         */
        @JvmStatic
        fun directionToNearestDungeon(world: World, xpos: Double, zpos: Double): Float {
            val spacing: Int = (world.provider as IGalacticraftWorldProvider).getDungeonSpacing()
            if (spacing == 0) return 0f
            val x: Int = MathHelper.floor(xpos)
            val z: Int = MathHelper.floor(zpos)
            val quadrantX: Int = x % spacing
            val quadrantZ: Int = z % spacing
            val searchOffsetX: Int = quadrantX / (spacing / 2) //0 or 1
            val searchOffsetZ: Int = quadrantZ / (spacing / 2) //0 or 1
            var nearestX: Double = 0.0
            var nearestZ: Double = 0.0
            var nearestDistance: Double = Double.MAX_VALUE
            for (cx: Int in searchOffsetX - 1..<searchOffsetX + 1) {
                for (cz: Int in searchOffsetZ - 1..<searchOffsetZ + 1) {
                    val dungeonPos: Long =
                        getDungeonPosForCoords(world, (x + cx * spacing) / 16, (z + cz * spacing) / 16, spacing)
                    val i: Int = 2 + (((dungeonPos shr 32).toInt()) shl 4)
                    val j: Int =
                        2 + ((dungeonPos.toInt()) shl 4) //Java automatically gives the 32 least significant bits
                    val oX: Double = i - xpos
                    val oZ: Double = j - zpos
                    val distanceSq: Double = oX * oX + oZ * oZ
                    if (distanceSq < nearestDistance) {
                        nearestDistance = distanceSq
                        nearestX = oX
                        nearestZ = oZ
                    }
                }
            }

            return GCCoreUtil.getAngleForRelativePosition(nearestX, nearestZ)
        }
    }
}
