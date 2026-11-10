package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.structure.MapGenStructure
import net.minecraft.world.gen.structure.MapGenStructureIO
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.StructureStart
import java.util.Random

open class MapGenAbandonedBase constructor() : MapGenStructure() {
    override fun getNearestStructurePos(worldIn: World, pos: BlockPos, p_180706_3_: Boolean): BlockPos? {
        return null
    }

    override fun getStructureName(): String {
        return "GC_AbandonedBase"
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

    override fun generate(worldIn: World, x: Int, z: Int, chunkPrimerIn: ChunkPrimer?) {
        val i: Int = this.range
        this.world = worldIn
        this.rand.setSeed(worldIn.getSeed())
        val j: Long = this.rand.nextLong()
        val k: Long = this.rand.nextLong()

        for (l: Int in x - i..x + i) {
            for (i1: Int in z - i..z + i) {
                val j1: Long = l.toLong() * j
                val k1: Long = i1.toLong() * k
                this.rand.setSeed(j1 xor k1 xor worldIn.getSeed())
                this.recursiveGenerate(worldIn, l, i1, x, z, chunkPrimerIn)
            }
        }
    }

    open fun reset() {
        this.structureMap.clear()
    }

    override fun getStructureStart(chunkX: Int, chunkZ: Int): StructureStart {
        val asteroid: BlockVec3? = (this.world.provider as WorldProviderAsteroids).getClosestAsteroidXZ(
            (chunkX shl 4) + 8,
            0,
            (chunkZ shl 4) + 8,
            false
        )
        if (asteroid == null) {
            return Start(
                this.world,
                this.rand,
                (chunkX shl 4) + 8,
                (chunkZ shl 4) + 8,
                15,
                BaseConfiguration(148, this.rand)
            )
        }
        return Start(
            this.world,
            this.rand,
            asteroid.x,
            asteroid.z,
            asteroid.sideDoneBits - 5,
            BaseConfiguration(asteroid.y - 10, this.rand)
        )
    }

    open class Start : StructureStart {
        private var configuration: BaseConfiguration? = null

        constructor()

        constructor(
            worldIn: World?,
            rand: Random,
            posX: Int,
            posZ: Int,
            size: Int,
            configuration: BaseConfiguration
        ) : super(posX shr 4, posZ shr 4) {
            var size: Int = size
            this.configuration = configuration
            if (size < 1) size = 1
            size = size * MathHelper.sqrt(size.toFloat()).toInt() / 4
            if (configuration.isHangarDeck()) size -= 6
            var xoffset: Int = 0
            var zoffset: Int = 0
            val direction: EnumFacing = EnumFacing.Plane.HORIZONTAL.random(rand)
            when (direction) {
                EnumFacing.NORTH -> zoffset = -size
                EnumFacing.SOUTH -> zoffset = size
                EnumFacing.WEST -> xoffset = -size
                EnumFacing.EAST -> xoffset = size
            
else -> {}
}
            val startPiece: BaseStart = BaseStart(configuration, rand, posX + xoffset, posZ + zoffset, direction)
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
                MapGenStructureIO.registerStructure(Start::class.java, "AbandonedBase")
                MapGenStructureIO.registerStructureComponent(BaseStart::class.java, "AbandonedBaseStart")
                MapGenStructureIO.registerStructureComponent(BaseRoom::class.java, "AbandonedBaseRoom")
                MapGenStructureIO.registerStructureComponent(BaseDeck::class.java, "AbandonedBaseDeck")
                MapGenStructureIO.registerStructureComponent(BasePlate::class.java, "AbandonedBasePlate")
                MapGenStructureIO.registerStructureComponent(BaseHangar::class.java, "AbandonedBaseHangar")

                //Currently in fact unused, but just in case...
                MapGenStructureIO.registerStructureComponent(BaseLinking::class.java, "AbandonedBaseCorridor")
            }

            initialized = true
        }
    }
}
