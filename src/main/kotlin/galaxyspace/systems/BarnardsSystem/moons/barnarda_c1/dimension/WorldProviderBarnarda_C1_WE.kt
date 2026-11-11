package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension

import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaBlockIds

import foundation.block.getStateFromMeta

import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.BarnardsSystemBodies
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.WorldProviderBarnarda_C1_WE
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.core.util.GSDimensions
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks.EnumBlockBarnardaC1
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.api.dimension.IProviderFog
import asmodeuscore.api.dimension.IProviderWeather
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.sky.SkyProviderBarnarda_C1
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import galaxyspace.core.prefab.world.gen.we.WE_LakesGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen
import galaxyspace.core.configs.GSConfigWorld
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.world.gen.WorldGenIcyDripstone
import asmodeuscore.core.astronomy.dimension.world.worldengine.biome.WE_BaseBiome
import asmodeuscore.core.astronomy.WeatherData
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_BiomeProvider
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomBoss
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.Level as World
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random
import kotlin.math.max

open class WorldProviderBarnarda_C1_WE : WE_WorldProviderSpace(), IProviderFog, IProviderWeather {
    private val dungeonGeneratorMoon = MapGenDungeon(
        DungeonConfiguration(
            (GCBlocks.blockMoon)!!.defaultBlockState()
                .setValue(
                    BlockBasicMoon.BASIC_TYPE_MOON,
                    BlockBasicMoon.EnumBlockBasicMoon.MOON_DUNGEON_BRICK
                ), 25, 8, 16,
            5, 6, RoomBoss::class.java, RoomTreasure::class.java
        )
    )

    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getFallDamageModifier(): Float {
        return 0.16f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.8
    }

    public override fun getMeteorFrequency(): Double {
        return 0.9
    }

    public override fun getSoundVolReductionAmount(): Float {
        return Float.MIN_VALUE
    }

    override fun canSnowAt(pos: BlockPos, checkLight: Boolean): Boolean {
        return true
    }

    override fun getCelestialBody(): CelestialBody? {
        return BarnardsSystemBodies.Companion.Barnarda_C1
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return WE_BiomeProvider::class.java
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCloudHeight(): Float {
        return 180.0f
    }

    override fun canRespawnHere(): Boolean {
        return true
    }

    @OnlyIn(Dist.CLIENT)
    override fun calcSunriseSunsetColors(celestialAngle: Float, partialTicks: Float): FloatArray? {
        return super.calcSunriseSunsetColors(celestialAngle, partialTicks)
        /*  float f = 0.4F;
        float f1 = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) - 0.0F;
        float f2 = -0.0F;

        if (f1 >= -0.4F && f1 <= 0.4F)
        {
            float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
            f4 = f4 * f4;
            this.colorsSunriseSunset[0] = f3 * 0.3F + 0.7F;
            this.colorsSunriseSunset[1] = f3 * f3 * 0.7F + 0.2F;
            this.colorsSunriseSunset[2] = f3 * f3 * 0.0F + 0.2F;
            this.colorsSunriseSunset[3] = f4;
            return this.colorsSunriseSunset;
        }
        else
        {
            return null;
        }*/
    }

    /*
    @Override
    @OnlyIn(Dist.CLIENT)
    public Vector3 getFogColor() {

        float f = 1.0F - this.getStarBrightness(1.0F);
        return new Vector3(86 / 255.0F * f, 180 / 255.0F * f, 240 / 255.0F * f);
    }

    @Override
    public Vector3 getSkyColor() {
        float f = 0.6F - this.getStarBrightness(1.0F);
        return new Vector3(100 / 255.0F * f, 220 / 255.0F * f, 250 / 255.0F * f);
    }*/
    @OnlyIn(Dist.CLIENT)
    override fun getFogColor(): Vector3 {
        var f: Float = 1.0f - this.getStarBrightness(1.0f)
        val fsd: FrozenStormSaveData = FrozenStormSaveData.get(this.world, getCelestialBody()!!.getName())

        if (fsd.getStormStrength(1.0f) > 0.0f) f = max(0.4f, 1.0f - fsd.getStormStrength(1.0f))

        return Vector3((160 / 255.0f * f).toDouble(), (167 / 255.0f * f).toDouble(), (190 / 255.0f * f).toDouble())
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyColor(): Vector3 {
        var f: Float = 0.5f - this.getStarBrightness(1.0f)
        if (world.isRaining()) {
            f = 1.0f
            return Vector3((47 / 255.0f * f).toDouble(), (47 / 255.0f * f).toDouble(), (47 / 255.0f * f).toDouble())
        }
        return Vector3((161 / 255.0f * f).toDouble(), (146 / 255.0f * f).toDouble(), (175 / 255.0f * f).toDouble())
    }

    override fun isSkyColored(): Boolean {
        return true
    }

    override fun hasSunset(): Boolean {
        return true
    }

    override fun shouldForceRespawn(): Boolean {
        return !ConfigManagerCore.forceOverworldRespawn
    }

    @OnlyIn(Dist.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val f: Float = this.world.getCelestialAngle(par1)
        var f1: Float = 1.0f - (MathHelper.cos(f * (Math.PI.toFloat() * 2f)) * 2.0f + 0.25f)
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f)

        val fsd: FrozenStormSaveData = FrozenStormSaveData.get(this.world, getCelestialBody()!!.getName())
        if (fsd.isFrozenStorm()) f1 =
            (f1.toDouble() * (1.0 - (fsd.getStormStrength(par1) * 5.0f).toDouble() / 6.0)).toFloat()

        return f1 * f1 * 0.5f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSunBrightness(partialTicks: Float): Float {
        val f: Float = this.world.getCelestialAngle(partialTicks)
        var f1: Float = 1.0f - (MathHelper.cos(f * (Math.PI.toFloat() * 2f)) * 2.0f + 0.2f)
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f)
        f1 = 1.0f - f1
        f1 = (f1.toDouble() * (1.0 - (this.world.getRainStrength(partialTicks) * 5.0f).toDouble() / 16.0)).toFloat()
        f1 = (f1.toDouble() * (1.0 - (this.world.getThunderStrength(partialTicks) * 5.0f).toDouble() / 16.0)).toFloat()

        val fsd: FrozenStormSaveData = FrozenStormSaveData.get(this.world, getCelestialBody()!!.getName())
        if (fsd.isFrozenStorm()) f1 =
            (f1.toDouble() * (1.0 - (fsd.getStormStrength(partialTicks) * 5.0f).toDouble() / 6.0)).toFloat()

        //f2 = 1.2F - f2;
        return f1 * 0.8f + 0.2f
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderBarnarda_C1())
        }

        return super.getSkyRenderer()
    }

    override fun getDungeonSpacing(): Int {
        return 704
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasure.MOONCHEST
    }

    public override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.BARNARDA_C1)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()

        WE_Biome.setBiomeMap((cp)!!, 1.2, 4, 1200.0, 1.0)

        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
            .setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.STONE
            )
        terrainGenerator.worldSeaGen = true
        terrainGenerator.worldSeaGenBlock = Blocks.ICE.defaultBlockState()
        terrainGenerator.worldSeaGenMaxY = 68
        (cp)!!.createChunkGen_List.add(terrainGenerator)

        //-//
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        cg.lavaMaxY = 0
        cg.range = 8
        (cp)!!.createChunkGen_List.add(cg)

        //-//
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        rg.lavaBlock = Blocks.LAVA.defaultBlockState()
        rg.lavaMaxY = 15
        rg.range = 32
        (cp)!!.createChunkGen_List.add(rg)

        var lakes: WE_LakesGen = WE_LakesGen()
        lakes.lakeBlock = Blocks.WATER.defaultBlockState()
        lakes.chunksForLake = 4
        lakes.underground = true
        lakes.maxY = 20
        (cp)!!.decorateChunkGen_List.add(lakes)

        lakes = WE_LakesGen()
        lakes.lakeBlock = Blocks.LAVA.defaultBlockState()
        lakes.chunksForLake = 14
        lakes.underground = true
        lakes.maxY = 40
        (cp)!!.decorateChunkGen_List.add(lakes)

        val standardOres: WE_OreGen = WE_OreGen()
        standardOres.add(
            BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                .setValue(
                    Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                    EnumBlockBarnardaC1.ICY_SUBSURFACE
                ),
            terrainGenerator.worldStoneBlock, 26, 5, 150, 20
        )

        standardOres.add(
            BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                .setValue(
                    Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                    EnumBlockBarnardaC1.SNOWY_SUBSURFACE
                ),
            terrainGenerator.worldStoneBlock, 15, 60, 150, 20
        )

        standardOres.add(
            Blocks.ICE.defaultBlockState(),
            terrainGenerator.worldStoneBlock, 8, 5, 150, 12
        )

        if (GSConfigWorld.enableOresGeneration) {
            standardOres.add(
                BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                    .setValue(
                        Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                        EnumBlockBarnardaC1.ONYX_ORE
                    ),
                terrainGenerator.worldStoneBlock, 4, 1, 30, 2
            )
            standardOres.add(
                BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                    .setValue(
                        Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                        EnumBlockBarnardaC1.IRON_ORE
                    ),
                terrainGenerator.worldStoneBlock, 5, 10, 60, 6
            )
            standardOres.add(
                BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                    .setValue(
                        Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                        EnumBlockBarnardaC1.COPPER_ORE
                    ),
                terrainGenerator.worldStoneBlock, 6, 30, 120, 10
            )
            standardOres.add(
                BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                    .setValue(
                        Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                        EnumBlockBarnardaC1.TIN_ORE
                    ),
                terrainGenerator.worldStoneBlock, 6, 30, 120, 8
            )
        }

        (cp)!!.decorateChunkGen_List.add(standardOres)

        (cp as WE_ChunkProviderSpace).worldGenerators.clear()
        cp.biomesList.clear()

        val layer: WE_BiomeLayer = WE_BiomeLayer()
        layer.add(Blocks.BEDROCK.defaultBlockState(), 0, 0, 1, 2, true)
        layer.add(
            BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState().setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.SNOWY_SUBSURFACE
            ), terrainGenerator.worldStoneBlock, -256, 0, -10, -4, true
        )
        layer.add(
            BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState().setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.SNOWY_SURFACE
            ),
            BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState().setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.SNOWY_SUBSURFACE
            ),
            -256,
            0,
            -2,
            -1,
            false
        )

        val layerIce: WE_BiomeLayer = WE_BiomeLayer()
        layerIce.add(Blocks.BEDROCK.defaultBlockState(), 0, 0, 1, 2, true)
        layerIce.add(
            BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState().setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.ICY_SUBSURFACE
            ), terrainGenerator.worldStoneBlock, -256, 0, -10, -4, true
        )
        layerIce.add(
            Blocks.PACKED_ICE.defaultBlockState(),
            BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState().setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.ICY_SUBSURFACE
            ),
            -256,
            0,
            -2,
            -1,
            false
        )

        val icyDripstone: WorldGenIcyDripstone = WorldGenIcyDripstone()


        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(-0.5, 0.0, 1.4, 4, 60, 80, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..19) {
                    icyDripstone.generate((world)!!, (rand)!!, BlockPos(x, 0, z))
                }
            }
        })

        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(0.0, 0.3, 1.5, 4, 70, 80, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..149) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                    if ((world)!!.isEmptyBlock(pos) && (world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                            .setValue(
                                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                                EnumBlockBarnardaC1.SNOWY_SURFACE
                            )
                    ) {
                        (world)!!.setBlockAndUpdate(pos, LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.SNOW_LAYER, (rand)!!.nextInt(3)))
                    }
                }

                for (i: Int in 0..19) {
                    icyDripstone.generate((world)!!, (rand)!!, BlockPos(x, 0, z))
                }
            }
        })
        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(0.3, 0.5, 2.2, 4, 90, 140, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..79) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                    if ((world)!!.isEmptyBlock(pos) && (world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                            .setValue(
                                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                                EnumBlockBarnardaC1.SNOWY_SURFACE
                            )
                    ) {
                        (world)!!.setBlockAndUpdate(pos, LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.SNOW_LAYER, (rand)!!.nextInt(4)))
                    }
                }

                for (i: Int in 0..19) {
                    icyDripstone.generate((world)!!, (rand)!!, BlockPos(x, 0, z))
                }
            }
        })
        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(0.5, 0.9, 2.8, 4, 120, 180, layerIce) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..59) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                    if ((world)!!.isEmptyBlock(pos) && (world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
                            .setValue(
                                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                                EnumBlockBarnardaC1.SNOWY_SURFACE
                            )
                    ) {
                        (world)!!.setBlockAndUpdate(pos, LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.SNOW_LAYER, (rand)!!.nextInt(4)))
                    }
                }

                for (i: Int in 0..19) {
                    icyDripstone.generate((world)!!, (rand)!!, BlockPos(x, 0, z))
                }
            }
        })
        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(0.9, 1.8, 3.8, 4, 150, 140, layerIce) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..19) {
                    icyDripstone.generate((world)!!, (rand)!!, BlockPos(x, 0, z))
                }
            }
        })
        /*WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-0.5D, 0.5D, 2.0F, 6, 150, 3, layer));
        WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-1.4D, 1.0D, 1.5F, 4, 90, 10, layer));
        WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-1.0D, 1.5D, 1.5F, 4, 90, 20, layer));
        WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-2.0D, 2.0D, 1.5F, 4, 40, 3, layer));*/
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun getThermalValueMod(): Float {
        return 0.4f
    }

    override fun onPopulate(cX: Int, cZ: Int) {
        dungeonGeneratorMoon.generateStructure(this.world, this.world.rand, ChunkPos(cX, cZ))
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
        dungeonGeneratorMoon.generate(this.world, cX, cZ, primer)
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
        dungeonGeneratorMoon.generate(this.world, x, z, null)
    }

    override fun getFogDensity(x: Int, y: Int, z: Int): Float {
        val fsd: FrozenStormSaveData = FrozenStormSaveData.get(this.world, getCelestialBody()!!.getName())
        if (fsd.getStormStrength(1.0f) > 0.0f) return max(0.3f, 1.0f - fsd.getStormStrength(1.0f))

        return 0.93f
    }

    override fun getWeather(): WeatherData {
        return IProviderWeather.frozen_storm
    }
}
