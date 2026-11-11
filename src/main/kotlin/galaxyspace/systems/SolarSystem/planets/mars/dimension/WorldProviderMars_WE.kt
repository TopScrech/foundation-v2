package galaxyspace.systems.SolarSystem.planets.mars.dimension

import foundation.block.getStateFromMeta

import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.astronomy.dimension.world.worldengine.biome.WE_BaseBiome
import asmodeuscore.api.dimension.IProviderFog
import asmodeuscore.api.dimension.IProviderWeather
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import galaxyspace.systems.SolarSystem.planets.mars.dimension.WorldProviderMars_WE
import galaxyspace.systems.SolarSystem.planets.mars.dimension.sky.WeatherProviderMars
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import galaxyspace.systems.SolarSystem.planets.mars.world.gen.we.Mars_Plains
import galaxyspace.systems.SolarSystem.planets.mars.world.gen.we.Mars_High_Plains
import galaxyspace.systems.SolarSystem.planets.mars.world.gen.we.Mars_Mountains
import asmodeuscore.core.astronomy.WeatherData
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import galaxyspace.systems.SolarSystem.planets.mars.dimension.sky.SkyProviderMars
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import micdoodle8.mods.galacticraft.planets.GCPlanetDimensions
import micdoodle8.mods.galacticraft.planets.mars.MarsModule
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockBasicMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.world.gen.BiomeDecoratorMars
import micdoodle8.mods.galacticraft.planets.mars.world.gen.BiomeProviderMars
import micdoodle8.mods.galacticraft.planets.mars.world.gen.MapGenCavernMars
import micdoodle8.mods.galacticraft.planets.mars.world.gen.MapGenDungeonMars
import micdoodle8.mods.galacticraft.planets.mars.world.gen.RoomBossMars
import micdoodle8.mods.galacticraft.planets.mars.world.gen.RoomTreasureMars
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.max

open class WorldProviderMars_WE : WE_WorldProviderSpace(), IProviderFog, IProviderWeather {
    private val dungeonGenerator: MapGenDungeon = MapGenDungeonMars(
        DungeonConfiguration(
            (MarsBlocks.marsBlock)!!.defaultBlockState()
                .setValue(
                    BlockBasicMars.BASIC_TYPE,
                    BlockBasicMars.EnumBlockBasic.DUNGEON_BRICK
                ), 30, 8, 16, 7, 7, RoomBossMars::class.java, RoomTreasureMars::class.java
        )
    )
    private val colorsSunriseSunset = FloatArray(4)

    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getFallDamageModifier(): Float {
        return 0.16f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.8
    }

    public override fun getSoundVolReductionAmount(): Float {
        return Float.MAX_VALUE
    }

    override fun getCelestialBody(): CelestialBody? {
        return MarsModule.planetMars
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderMars::class.java
    }

    override fun getFogColor(): Vector3 {
        var f: Float = 1.0f - this.getStarBrightness(1.0f)
        val msd: DustStormSaveData = DustStormSaveData.get(this.world, DATA)
        if (msd.getStormStrength(1.0f) > 0.0f) f = max(0.4f, 1.0f - msd.getStormStrength(1.0f))

        return Vector3((210f / 255f * f).toDouble(), (170f / 255f * f).toDouble(), (129f / 255f * f).toDouble())
    }

    override fun getSkyColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)

        return Vector3((184 / 255.0f * f).toDouble(), (134 / 255.0f * f).toDouble(), (86 / 255.0f * f).toDouble())
    }

    override fun isSkyColored(): Boolean {
        return false
    }

    override fun hasSunset(): Boolean {
        return true
    }

    override fun shouldForceRespawn(): Boolean {
        return !ConfigManagerCore.forceOverworldRespawn
    }

    @OnlyIn(Dist.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val var2: Float = this.world.getCelestialAngle(par1)
        var var3: Float = 1.0f - (MathHelper.cos(var2 * Math.PI.toFloat() * 2.0f) * 2.0f + 0.25f)
        var3 = MathHelper.clamp(var3, 0.0f, 1.0f)
        return var3 * var3 * 0.5f + 0.1f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSunBrightness(partialTicks: Float): Float {
        val f: Float = this.world.getCelestialAngle(partialTicks)
        var f1: Float = 1.0f - (MathHelper.cos(f * (Math.PI.toFloat() * 2f)) * 2.0f + 0.2f)
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f)
        f1 = 1.0f - f1
        f1 = (f1.toDouble() * (1.0 - (this.world.getRainStrength(partialTicks) * 5.0f).toDouble() / 16.0)).toFloat()
        f1 = (f1.toDouble() * (1.0 - (this.world.getThunderStrength(partialTicks) * 5.0f).toDouble() / 16.0)).toFloat()

        val msd: DustStormSaveData = DustStormSaveData.get(this.world, DATA)
        if (msd.isDustStorm()) f1 =
            (f1.toDouble() * (1.0 - (msd.getStormStrength(partialTicks) * 5.0f).toDouble() / 6.0)).toFloat()
        return f1 * 0.8f + 0.2f
    }

    @OnlyIn(Dist.CLIENT)
    override fun calcSunriseSunsetColors(celestialAngle: Float, partialTicks: Float): FloatArray? {
        val f1: Float = MathHelper.cos(celestialAngle * (Math.PI.toFloat() * 2f)) - 0.0f

        //GalaxySpace.instance.debug(f1);
        if (f1 >= 0.05f && f1 <= 0.4f) {
            val f3: Float = (f1 - -0.0f) / 0.4f * 0.5f + 0.6f
            var f4: Float = 1.0f - (1.0f - MathHelper.sin(f3 * Math.PI.toFloat())) * 0.99f
            f4 = f4 * f4
            this.colorsSunriseSunset[0] = f3 * 0.1f + 0.35f
            this.colorsSunriseSunset[1] = f3 * f3 * 0.1f + 0.2f
            this.colorsSunriseSunset[2] = f3 * f3 * 0.7f + 1.0f
            this.colorsSunriseSunset[3] = f4

            return this.colorsSunriseSunset
        } else {
            return null
        }
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderMars())
        }

        return super.getSkyRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getWeatherRenderer(): IRenderHandler? {
        return WeatherProviderMars()
    }

    override fun getDungeonSpacing(): Int {
        return 704
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasureMars.MARSCHEST
    }

    public override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun getDimensionType(): DimensionType {
        return (GCPlanetDimensions.MARS)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()

        (cp as WE_ChunkProviderSpace).CRATER_PROB = 2000

        WE_Biome.setBiomeMap(cp, 1.5, 4, 2000.0, 1.0)

        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = (MarsBlocks.marsBlock)!!.getStateFromMeta(9)
        terrainGenerator.worldSeaGen = false
        terrainGenerator.worldSeaGenBlock = Blocks.WATER.defaultBlockState()
        terrainGenerator.worldSeaGenMaxY = 64
        cp.createChunkGen_List.add(terrainGenerator)


        //-// 
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        cg.lavaMaxY = 0
        cp.createChunkGen_List.add(cg)


        //-// 
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        rg.addReplacingBlock((MarsBlocks.marsBlock)!!.getStateFromMeta(6))
        rg.addReplacingBlock((MarsBlocks.marsBlock)!!.getStateFromMeta(5))
        rg.addReplacingBlock(Blocks.PACKED_ICE.defaultBlockState())
        rg.lavaBlock = Blocks.AIR.defaultBlockState()
        rg.lavaMaxY = 0

        //cp.createChunkGen_List.add(rg);
        val cavernGenerator: MapGenCavernMars = MapGenCavernMars()
        cp.worldGenerators.clear()
        cp.worldGenerators.add(cavernGenerator)

        cp.biomesList.clear()


        //WE_Biome.addBiomeToGeneration(cp, new Mars_Triangle_Mountains(-1.0D, 0.0D));
        //WE_Biome.addBiomeToGeneration(cp, new Mars_Ravine(-1.0D, 0.0D));
        val layer: WE_BiomeLayer = WE_BiomeLayer()
        layer.add(
            (MarsBlocks.marsBlock)!!.getStateFromMeta(6),
            (MarsBlocks.marsBlock)!!.getStateFromMeta(9),
            -256,
            0,
            -4,
            -6,
            true
        )
        layer.add(
            (MarsBlocks.marsBlock)!!.getStateFromMeta(5),
            (MarsBlocks.marsBlock)!!.getStateFromMeta(6),
            -256,
            0,
            -2,
            -1,
            true
        )
        layer.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)

        val mount_layer: WE_BiomeLayer = WE_BiomeLayer()
        mount_layer.add(
            (MarsBlocks.marsBlock)!!.getStateFromMeta(6),
            (MarsBlocks.marsBlock)!!.getStateFromMeta(9),
            -256,
            0,
            -4,
            -6,
            true
        )
        mount_layer.add(
            (MarsBlocks.marsBlock)!!.getStateFromMeta(5),
            (MarsBlocks.marsBlock)!!.getStateFromMeta(6),
            -256,
            0,
            -2,
            -1,
            true
        )
        mount_layer.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)

        WE_Biome.addBiomeToGeneration(cp, Mars_Plains(-0.0, 0.0).setAbsoluteValue())
        WE_Biome.addBiomeToGeneration(cp, Mars_High_Plains(-0.4, 0.4).setAbsoluteValue())
        WE_Biome.addBiomeToGeneration(cp, Mars_Mountains(-1.2, 1.2).setAbsoluteValue())
        WE_Biome.addBiomeToGeneration(cp, Mars_High_Plains(-2.0, 2.0).setAbsoluteValue())
        WE_Biome.addBiomeToGeneration(cp, WE_BaseBiome(-2.8, 2.8, 2.4, 4, 180, 30, layer).setAbsoluteValue())
        /*WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(0D, 0D, 2.8F, 3, 95, 25, layer));
		WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-0.4D, 0.4D, 11.4F, 3, 155, 5, mount_layer));
		WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-1.4D, 1.1D, 2.8F, 3, 95, 15, layer));*/
        //WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-1.8D, 1.8D, 2.8F, 2, 90, 5, layer));
        //WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-2.6D, 2.6D, 1.2F, 3, 45, 10, mount_layer).setSize(128.0D, 1.5D));
        /*WE_BiomeRavineGen biome_ravine = new WE_BiomeRavineGen();
		biome_ravine.addReplacingBlock(terrainGenerator.worldStoneBlock);
		biome_ravine.addReplacingBlock(MarsBlocks.marsBlock.getStateFromMeta(6));
		biome_ravine.addReplacingBlock(MarsBlocks.marsBlock.getStateFromMeta(5));
		biome_ravine.addReplacingBlock(Blocks.PACKED_ICE.defaultBlockState());
		biome_ravine.lavaBlock = Blocks.AIR.defaultBlockState();
		biome_ravine.lavaMaxY = 0;
		WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-1.8D, 1.8D, 2.8F, 2, 90, 5, layer).addChunkGen(biome_ravine));
		 */
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return super.enableAdvancedThermalLevel()
    }

    override fun getThermalValueMod(): Float {
        return 0.65f
    }

    override fun onPopulate(cX: Int, cZ: Int) {
        dungeonGenerator.generateStructure(this.world, this.world.rand, ChunkPos(cX, cZ))
        BiomeDecoratorMars().decorate(this.world, this.world.rand, cX * 16, cZ * 16)
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
        dungeonGenerator.generate(this.world, cX, cZ, primer)
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
        dungeonGenerator.generate(this.world, x, z, null)
    }

    override fun getFogDensity(x: Int, y: Int, z: Int): Float {
        val msd: DustStormSaveData = DustStormSaveData.get(world, DATA)

        if (msd.getStormStrength(1.0f) > 0.0f) return max(0.3f, 1.0f - msd.getStormStrength(1.0f))


        return 1.0f
    }

    override fun getWeather(): WeatherData {
        return IProviderWeather.dust_storm
    }

    companion object {
        const val DATA: String = "mars"
    }
}
