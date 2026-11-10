package micdoodle8.mods.galacticraft.api.prefab.world.gen

import javax.annotation.Nullable

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import net.minecraft.block.BlockFlower
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeDecorator
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.feature.WorldGenAbstractTree
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.LinkedList
import java.util.Random

open class BiomeAdaptive constructor(private val index: Int, private var biomeTrue: Biome?) :
    BiomeGenBaseGC(BiomeProperties("Outer Space" + (if (index == 0) "" else " " + index)).setRainfall(0.0f)) {
    init {
        this.decorator = this.createBiomeDecorator()
        if (index == 0) {
            biomeDefault = this
        }
    }

    override fun registerTypes(b: Biome?) {
        if (this.biomeTrue is BiomeGenBaseGC) {
            (this.biomeTrue as BiomeGenBaseGC).registerTypes(this)
        } else {
            BiomeDictionary.addTypes(
                this,
                BiomeDictionary.Type.DRY,
                BiomeDictionary.Type.DEAD,
                BiomeDictionary.Type.SPARSE,
                BiomeDictionary.Type.SPOOKY
            )
        }
    }

    open fun isInstance(clazz: Class<*>): Boolean {
        return clazz.isInstance(this.biomeTrue)
    }

    /**
     * @param body
     * @return true if end of list
     */
    protected open fun setBodyInstance(body: CelestialBody): Boolean {
        this.biomeTrue = ((body)!!.biomesToAdapt)!![this.index]
        this.fillerBlock = this.biomeTrue!!.fillerBlock
        this.topBlock = this.biomeTrue!!.topBlock
        this.decorator = this.biomeTrue!!.decorator
        return this.index == (body.biomesToAdapt)!!.size - 1
    }

    // ===========================================================================
    // ===============Pass through all Biome methods to the true biome============
    // ===========================================================================
    override fun createBiomeDecorator(): BiomeDecorator {
        // Biome's constructor dispatches here before biomeTrue has been assigned
        // The initializer replaces this temporary decorator after construction
        return biomeTrue?.createBiomeDecorator() ?: BiomeDecorator()
    }

    override fun isMutation(): Boolean {
        return biomeTrue!!.isMutation()
    }

    override fun getRandomTreeFeature(rand: Random): WorldGenAbstractTree {
        return biomeTrue!!.getRandomTreeFeature(rand)
    }

    override fun getRandomWorldGenForGrass(rand: Random): WorldGenerator {
        return biomeTrue!!.getRandomWorldGenForGrass(rand)
    }

    override fun pickRandomFlower(rand: Random, pos: BlockPos): BlockFlower.EnumFlowerType {
        return biomeTrue!!.pickRandomFlower(rand, pos)
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyColorByTemp(currentTemperature: Float): Int {
        return biomeTrue!!.getSkyColorByTemp(currentTemperature)
    }

    override fun getSpawnableList(creatureType: EnumCreatureType): MutableList<SpawnListEntry?> {
        return biomeTrue!!.getSpawnableList(creatureType)
    }

    override fun getEnableSnow(): Boolean {
        return biomeTrue!!.getEnableSnow()
    }

    override fun canRain(): Boolean {
        return biomeTrue!!.canRain()
    }

    override fun isHighHumidity(): Boolean {
        return biomeTrue!!.isHighHumidity()
    }

    override fun getSpawningChance(): Float {
        return biomeTrue!!.getSpawningChance()
    }

    override fun getTemperature(pos: BlockPos): Float {
        return biomeTrue!!.getTemperature(pos)
    }

    override fun decorate(worldIn: World, rand: Random, pos: BlockPos) {
        if (worldIn.provider is IGalacticraftWorldProvider) {
            this.setBodyInstance(((worldIn.provider as IGalacticraftWorldProvider).getCelestialBody())!!)
        } else {
            Thread.dumpStack()
        }
        biomeTrue!!.decorate(worldIn, rand, pos)
    }

    override fun genTerrainBlocks(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        x: Int,
        z: Int,
        noiseVal: Double
    ) {
        if (worldIn.provider is IGalacticraftWorldProvider) {
            this.setBodyInstance(((worldIn.provider as IGalacticraftWorldProvider).getCelestialBody())!!)
        } else {
            Thread.dumpStack()
        }
        biomeTrue!!.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal)
    }

    @SideOnly(Side.CLIENT)
    override fun getGrassColorAtPos(pos: BlockPos): Int {
        return biomeTrue!!.getGrassColorAtPos(pos)
    }

    override fun generateBiomeTerrain(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        x: Int,
        z: Int,
        noiseVal: Double
    ) {
        if (worldIn.provider is IGalacticraftWorldProvider) {
            this.setBodyInstance(((worldIn.provider as IGalacticraftWorldProvider).getCelestialBody())!!)
        } else {
            Thread.dumpStack()
        }
        biomeTrue!!.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal)
    }

    @SideOnly(Side.CLIENT)
    override fun getFoliageColorAtPos(pos: BlockPos): Int {
        return biomeTrue!!.getFoliageColorAtPos(pos)
    }

    override fun getBiomeClass(): Class<out Biome> {
        return biomeTrue!!.getBiomeClass()
    }

    override fun getTempCategory(): TempCategory {
        return biomeTrue!!.getTempCategory()
    }

    override fun ignorePlayerSpawnSuitability(): Boolean {
        return biomeTrue!!.ignorePlayerSpawnSuitability()
    }

    override fun getBaseHeight(): Float {
        return if (biomeTrue == null) 0.0f else biomeTrue!!.getBaseHeight()
    }

    override fun getRainfall(): Float {
        return biomeTrue!!.getRainfall()
    }

    @SideOnly(Side.CLIENT)
    override fun getBiomeName(): String {
        return biomeTrue!!.getBiomeName()
    }

    override fun getHeightVariation(): Float {
        return biomeTrue!!.getHeightVariation()
    }

    override fun getDefaultTemperature(): Float {
        return biomeTrue!!.getDefaultTemperature()
    }

    @SideOnly(Side.CLIENT)
    override fun getWaterColor(): Int {
        return biomeTrue!!.getWaterColor()
    }

    override fun isSnowyBiome(): Boolean {
        return biomeTrue!!.isSnowyBiome()
    }

    override fun getModdedBiomeDecorator(original: BiomeDecorator): BiomeDecorator {
        return biomeTrue!!.getModdedBiomeDecorator(original)
    }

    override fun getWaterColorMultiplier(): Int {
        return biomeTrue!!.getWaterColorMultiplier()
    }

    override fun getModdedBiomeGrassColor(original: Int): Int {
        return biomeTrue!!.getModdedBiomeGrassColor(original)
    }

    override fun getModdedBiomeFoliageColor(original: Int): Int {
        return biomeTrue!!.getModdedBiomeFoliageColor(original)
    }

    override fun addDefaultFlowers() {
        if (biomeTrue != null) biomeTrue!!.addDefaultFlowers()
    }

    override fun addFlower(state: IBlockState, weight: Int) {
        biomeTrue!!.addFlower(state, weight)
    }

    override fun plantFlower(world: World, rand: Random, pos: BlockPos) {
        if (world.provider is IGalacticraftWorldProvider) {
            this.setBodyInstance(((world.provider as IGalacticraftWorldProvider).getCelestialBody())!!)
        } else {
            Thread.dumpStack()
        }
        biomeTrue!!.plantFlower(world, rand, pos)
    }

    companion object {
        @JvmField
        var biomeDefault: BiomeAdaptive? = null

        @JvmField
        var biomeList: MutableList<BiomeAdaptive> = LinkedList<BiomeAdaptive>()

        @JvmStatic
        fun register(index: Int, biome: BiomeGenBaseGC?): BiomeGenBaseGC {
            if (index >= biomeList.size) {
                val newAdaptive: BiomeAdaptive = BiomeAdaptive(index, biome)
                biomeList.add(newAdaptive)
                return newAdaptive
            }
            return biomeList.get(index)
        }

        @JvmStatic
        fun getDefaultBiomeFor(body: CelestialBody): BiomeGenBaseGC? {
            return ((body)!!.biomesToAdapt)!![0]
        }

        @JvmStatic
        fun setBody(body: CelestialBody) {
            biomeDefault!!.setBodyInstance(body)
        }

        /**
         * Be sure to call this from your BiomeProvider in:
         * <br></br>-- getBiome(BlockPos pos, Biome defaultBiome)
         * <br></br>-- getBiomesForGeneration(Biome[] biomes, int x, int z, int length, int width)
         * <br></br>-- getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
         * <br></br>-- getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
         * <br></br>also from your WorldProvider in:
         * <br></br>-- getBiomeProviderClass()
         *
         *
         * See BiomeProviderVenus for an example.
         */
        @JvmStatic
        fun setBodyMultiBiome(body: CelestialBody) {
            for (b: BiomeAdaptive in biomeList) {
                if (b.setBodyInstance(body)) break
            }
        }

        @JvmStatic
        fun getBiomesListFor(body: CelestialBody): MutableList<Biome?> {
            val result: MutableList<Biome?> = LinkedList<Biome?>()
            for (b: BiomeAdaptive? in biomeList) {
                if (result.size >= (body.biomesToAdapt)!!.size) {
                    break
                }

                result.add(b)
            }
            return result
        }
    }
}
