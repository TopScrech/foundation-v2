package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.gen.structure.MapGenStructure
import net.minecraft.world.gen.structure.MapGenStructureIO
import net.minecraft.world.gen.structure.StructureStart
import java.util.Arrays
import java.util.Random

open class MapGenVillageMoon constructor() : MapGenStructure() {
    private val terrainType = 0

    override fun canSpawnStructureAtCoords(i: Int, j: Int): Boolean {
        var i: Int = i
        var j: Int = j
        val numChunks: Byte = 32
        val offsetChunks: Byte = 8
        val oldi: Int = i
        val oldj: Int = j

        if (i < 0) {
            i -= numChunks - 1
        }

        if (j < 0) {
            j -= numChunks - 1
        }

        var randX: Int = i / numChunks
        var randZ: Int = j / numChunks
        val var7: Random = this.world.setRandomSeed(i, j, 10387312)
        randX *= numChunks.toInt()
        randZ *= numChunks.toInt()
        randX += var7.nextInt(numChunks - offsetChunks)
        randZ += var7.nextInt(numChunks - offsetChunks)

        return oldi == randX && oldj == randZ
    }

    override fun getNearestStructurePos(worldIn: World, pos: BlockPos, p_180706_3_: Boolean): BlockPos? {
        this.world = worldIn
        return findNearestStructurePosBySpacing(worldIn, this, pos, 32, 8, 10387312, false, 100, p_180706_3_)
    }

    override fun getStructureStart(par1: Int, par2: Int): StructureStart {
        GCLog.debug("Generating Moon Village at x" + par1 * 16 + " z" + par2 * 16)
        return StructureVillageStartMoon(this.world, this.rand, par1, par2, this.terrainType)
    }

    override fun getStructureName(): String {
        return "MoonVillage"
    }

    companion object {
        @JvmField
        var villageSpawnBiomes: MutableList<Biome?> =
            Arrays.asList<Biome?>(*arrayOf<Biome?>(BiomeAdaptive.biomeDefault))
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
                MapGenStructureIO.registerStructure(StructureVillageStartMoon::class.java, "MoonVillage")
                MapGenStructureIO.registerStructureComponent(StructureComponentVillageField::class.java, "MoonField1")
                MapGenStructureIO.registerStructureComponent(StructureComponentVillageField2::class.java, "MoonField2")
                MapGenStructureIO.registerStructureComponent(StructureComponentVillageHouse::class.java, "MoonHouse")
                MapGenStructureIO.registerStructureComponent(
                    StructureComponentVillageRoadPiece::class.java,
                    "MoonRoadPiece"
                )
                MapGenStructureIO.registerStructureComponent(StructureComponentVillagePathGen::class.java, "MoonPath")
                MapGenStructureIO.registerStructureComponent(StructureComponentVillageTorch::class.java, "MoonTorch")
                MapGenStructureIO.registerStructureComponent(
                    StructureComponentVillageStartPiece::class.java,
                    "MoonWell"
                )
                MapGenStructureIO.registerStructureComponent(
                    StructureComponentVillageWoodHut::class.java,
                    "MoonWoodHut"
                )
            }

            initialized = true
        }
    }
}
