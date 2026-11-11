package galaxyspace.systems.ACentauriSystem.planets.proxima_b.dimension

import foundation.block.getStateFromMeta

import foundation.level.offset

import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we.Proxima_B_Forest
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we.Proxima_B_Plains

import galaxyspace.core.util.GSUtils
import galaxyspace.systems.ACentauriSystem.ACentauriSystemBodies
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.dimension.WorldProviderProxima_B_WE
import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.core.util.GSDimensions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Dandelions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Grass
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.api.dimension.IProviderFog
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.dimension.sky.SkyProviderProxima_B
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen
import galaxyspace.core.configs.GSConfigWorld
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.astronomy.dimension.world.worldengine.biome.WE_BaseBiome
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.features.WorldGenMushroom
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_BigJungle
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_BigJungle2
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we.Proxima_B_Mountains
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we.Proxima_B_Ice_Plains
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we.Proxima_B_Beach
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we.Proxima_B_Ocean
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.world.level.block.Block
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random
import kotlin.math.max

open class WorldProviderProxima_B_WE : WE_WorldProviderSpace(), IProviderFog {
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

    public override fun getMeteorFrequency(): Double {
        return 0.0
    }

    public override fun getSoundVolReductionAmount(): Float {
        return Float.MIN_VALUE
    }

    override fun getCelestialBody(): CelestialBody? {
        return ACentauriSystemBodies.Companion.proxima_b
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    @OnlyIn(Dist.CLIENT)
    override fun calcSunriseSunsetColors(celestialAngle: Float, partialTicks: Float): FloatArray? {
        val f: Float = 0.4f
        val f1: Float = MathHelper.cos(celestialAngle * (Math.PI.toFloat() * 2f)) - 0.0f
        val f2: Float = -0.0f

        if (f1 >= -0.4f && f1 <= 0.4f) {
            val f3: Float = (f1 - -0.0f) / 0.4f * 0.5f + 0.5f
            var f4: Float = 1.0f - (1.0f - MathHelper.sin(f3 * Math.PI.toFloat())) * 0.99f
            f4 = f4 * f4
            this.colorsSunriseSunset[0] = f3 * 0.3f + 0.7f
            this.colorsSunriseSunset[1] = f3 * f3 * 0.7f + 0.2f
            this.colorsSunriseSunset[2] = f3 * f3 * 0.0f + 0.2f
            this.colorsSunriseSunset[3] = f4
            return this.colorsSunriseSunset
        } else {
            return null
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getFogColor(): Vector3 {
        var f: Float = 0.7f - this.getStarBrightness(1.0f)
        f = max(f, 0.0f)
        return Vector3((226 / 255.0f * f).toDouble(), (145 / 255.0f * f).toDouble(), (90 / 255.0f * f).toDouble())
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyColor(): Vector3 {
        var f: Float = 0.5f - this.getStarBrightness(1.0f)
        f = max(f, 0.0f)
        return Vector3((226 / 255.0f * f).toDouble(), (145 / 255.0f * f).toDouble(), (90 / 255.0f * f).toDouble())
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
        val var2: Float = this.world.getCelestialAngle(par1)
        var var3: Float = 1.0f - (MathHelper.cos(var2 * Math.PI.toFloat() * 2.0f) * 2.0f + 0.25f)

        if (var3 < 0.0f) {
            var3 = 0.0f
        }

        if (var3 > 1.0f) {
            var3 = 1.0f
        }

        return var3 * var3 * 0.1f + 0.1f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSunBrightness(par1: Float): Float {
        val f1: Float = this.world.getCelestialAngle(1.0f)
        var f2: Float = 1.0f - (MathHelper.cos(f1 * 3.1415927f * 2.0f) * 2.0f + 0.2f)
        f2 = MathHelper.clamp(f2, 0.0f, 1.0f)

        f2 = 1.2f - f2
        return f2 * 0.8f
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return super.getCloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCloudHeight(): Float {
        return 180.0f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderProxima_B())
        }

        return super.getSkyRenderer()
    }

    override fun getDungeonSpacing(): Int {
        return 0
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return null
    }

    public override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.PROXIMA_B)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        chunk = cp

        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()


        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(2)
        terrainGenerator.worldSeaGen = true
        terrainGenerator.worldSeaGenBlock = Blocks.WATER.defaultBlockState()
        terrainGenerator.worldSeaGenMaxY = 70
        (cp)!!.createChunkGen_List.add(terrainGenerator)


        //-// 
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        (cp)!!.createChunkGen_List.add(cg)


        //-// 
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        rg.lavaBlock = Blocks.LAVA.defaultBlockState()
        (cp)!!.createChunkGen_List.add(rg)

        (cp as WE_ChunkProviderSpace).worldGenerators.clear()
        cp.biomesList.clear()

        val standardOres: WE_OreGen = WE_OreGen()
        standardOres.add(
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(1),
            terrainGenerator.worldStoneBlock,
            14,
            30,
            100,
            150
        )
        if (GSConfigWorld.enableOresGeneration) {
            standardOres.add(
                ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(5),
                terrainGenerator.worldStoneBlock,
                6,
                10,
                80,
                8
            )
            standardOres.add(
                ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(6),
                terrainGenerator.worldStoneBlock,
                6,
                20,
                90,
                10
            )
            standardOres.add(
                ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(7),
                terrainGenerator.worldStoneBlock,
                6,
                20,
                90,
                15
            )
            standardOres.add(
                ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(8),
                terrainGenerator.worldStoneBlock,
                10,
                20,
                90,
                15
            )
            standardOres.add(
                ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(9),
                terrainGenerator.worldStoneBlock,
                4,
                5,
                20,
                5
            )
            standardOres.add(
                ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(10),
                terrainGenerator.worldStoneBlock,
                4,
                3,
                15,
                4
            )
        }
        cp.decorateChunkGen_List.add(standardOres)


        //WE_Biome.addBiomeToGeneration(cp, new Proxima_B_Plains());
        var layer: WE_BiomeLayer = WE_BiomeLayer()
        layer.add(
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(11),
            terrainGenerator.worldStoneBlock,
            -256,
            0,
            -4,
            -1,
            true
        )
        layer.add(Blocks.BEDROCK.defaultBlockState(), 0, 0, 1, 2, true)

        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(-0.4, 0.0, 1.4, 4, 78, 10, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                var randPosX: Int
                var randPosZ: Int
                var pos: BlockPos

                for (i: Int in 0..79) {
                    randPosX = x + (rand)!!.nextInt(16) + 8
                    randPosZ = z + (rand)!!.nextInt(16) + 8
                    pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                    if ((world)!!.getBlockState(pos.below()) === ACBlocks.PROXIMA_B_BLOCKS.defaultBlockState()
                            .setValue(
                                Proxima_B_Blocks.Companion.BASIC_TYPE,
                                EnumBlockProximaB.ROCKY_SURFACE
                            )
                    ) {
                        (world)!!.setBlockAndUpdate(
                            pos.below(),
                            ACBlocks.PROXIMA_B_BLOCKS.defaultBlockState()
                                .setValue(
                                    Proxima_B_Blocks.Companion.BASIC_TYPE,
                                    EnumBlockProximaB.SURFACE
                                )
                        )
                        (world)!!.setBlockAndUpdate(
                            pos,
                            ACBlocks.PROXIMA_B_DANDELIONS.defaultBlockState()
                                .setValue(
                                    Proxima_B_Dandelions.Companion.BASIC_TYPE,
                                    Proxima_B_Dandelions.EnumBlockDandelions.GRASS
                                )
                        )
                    }
                }

                if ((rand)!!.nextInt(2) == 0) {
                    randPosX = x + (rand)!!.nextInt(16) + 8
                    randPosZ = z + (rand)!!.nextInt(16) + 8
                    pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                    if ((world)!!.getBlockState(pos.below()) === ACBlocks.PROXIMA_B_BLOCKS.defaultBlockState()
                            .setValue(
                                Proxima_B_Blocks.Companion.BASIC_TYPE,
                                EnumBlockProximaB.ROCKY_SURFACE
                            )
                    ) {
                        WorldGenMushroom().generate((world)!!, (rand)!!, pos)
                    }
                }
            }
        })


        //WE_Biome.addBiomeToGeneration(cp, new Proxima_B_Forest(cp)); 
        layer = WE_BiomeLayer()
        layer.add(
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(14),
            terrainGenerator.worldStoneBlock,
            -256,
            0,
            -4,
            -1,
            true
        )
        layer.add(
            ACBlocks.PROXIMA_B_GRASS.defaultBlockState(),
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(14),
            -256,
            0,
            -256,
            0,
            false
        )
        layer.add(Blocks.BEDROCK.defaultBlockState(), 0, 0, 1, 2, true)

        WE_Biome.addBiomeToGeneration(
            cp, object : WE_BaseBiome(0.0, 0.8, 1.5, 4, 72, 15, layer) {
                override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                    var randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    var randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                    var cangen: Boolean = true

                    for (pos1: BlockPos in BlockPos.getAllInBox(
                        pos.offset(-3, -1, -3),
                        pos.offset(3, -1, 3)
                    )) if ((world)!!.isEmptyBlock(pos1)) cangen = false

                    if (!(world)!!.isAreaLoaded(
                            pos,
                            13,
                            false
                        )
                    ) if (cangen && (world)!!.getBlockState(pos.below()) === ACBlocks.PROXIMA_B_GRASS.defaultBlockState()
                            .setValue(
                                Proxima_B_Grass.Companion.BASIC_TYPE,
                                Proxima_B_Grass.EnumBlockGrass.GRASS
                            )
                    ) {
                        when ((rand)!!.nextInt(2)) {
                            0 -> WorldGenTree_BigJungle(
                                ACBlocks.PROXIMA_B_LOG_1.getStateFromMeta(0),
                                Blocks.AIR.defaultBlockState(),
                                (rand)!!.nextInt(3)
                            ).generate((world)!!, (rand)!!, pos)

                            1 -> WorldGenTree_BigJungle2(
                                ACBlocks.PROXIMA_B_LOG_1.getStateFromMeta(0),
                                Blocks.AIR.defaultBlockState(),
                                (rand)!!.nextInt(3)
                            ).generate((world)!!, (rand)!!, pos)
                        }
                    }

                    for (i: Int in 0..79) {
                        randPosX = x + (rand)!!.nextInt(16) + 8
                        randPosZ = z + (rand)!!.nextInt(16) + 8
                        pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                        if ((world)!!.getBlockState(pos.below()) === ACBlocks.PROXIMA_B_GRASS.defaultBlockState()
                                .setValue(
                                    Proxima_B_Grass.Companion.BASIC_TYPE,
                                    Proxima_B_Grass.EnumBlockGrass.GRASS
                                )
                        ) {
                            (world)!!.setBlockAndUpdate(
                                pos,
                                ACBlocks.PROXIMA_B_DANDELIONS.defaultBlockState()
                                    .setValue(
                                        Proxima_B_Dandelions.Companion.BASIC_TYPE,
                                        Proxima_B_Dandelions.EnumBlockDandelions.GRASS_2
                                    )
                            )
                        }
                    }

                    if ((rand)!!.nextInt(2) == 0) {
                        randPosX = x + (rand)!!.nextInt(16) + 8
                        randPosZ = z + (rand)!!.nextInt(16) + 8
                        pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                        if ((world)!!.getBlockState(pos.below()) === ACBlocks.PROXIMA_B_GRASS.defaultBlockState()
                                .setValue(
                                    Proxima_B_Grass.Companion.BASIC_TYPE,
                                    Proxima_B_Grass.EnumBlockGrass.GRASS
                                )
                        ) {
                            for (y: Int in 0..<6 + (rand)!!.nextInt(5)) {
                                val rand_pos: Int = (rand)!!.nextInt(100)
                                if (rand_pos > 20) pos = pos.east(1)
                                if (rand_pos > 50) pos = pos.east(-1).west(1)

                                (world)!!.setBlockAndUpdate(pos.above(y), ACBlocks.PROXIMA_B_LOG_1.getStateFromMeta(0))
                            }
                        }
                    }

                    for (i: Int in 0..5) {
                        randPosX = x + (rand)!!.nextInt(16) + 8
                        randPosZ = z + (rand)!!.nextInt(16) + 8
                        pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

                        if ((world)!!.getBlockState(pos.below()) === ACBlocks.PROXIMA_B_GRASS.defaultBlockState()
                                .setValue(
                                    Proxima_B_Grass.Companion.BASIC_TYPE,
                                    Proxima_B_Grass.EnumBlockGrass.GRASS
                                )
                        ) {
                            for (y: Int in 0..<6 + (rand)!!.nextInt(5)) {
                                if (y % 3 == 0) pos = pos.offset(EnumFacing.byHorizontalIndex((rand)!!.nextInt(4)))

                                (world)!!.setBlockAndUpdate(pos.above(y), ACBlocks.PROXIMA_B_LOG_1.getStateFromMeta(0))
                            }
                        }
                    }
                }
            }.setColors(
                GSUtils.getColor(150, 20, 20, 100),
                GSUtils.getColor(40, 20, 20, 100),
                GSUtils.getColor(150, 20, 20, 100)
            )
        )
        WE_Biome.addBiomeToGeneration(cp, Proxima_B_Mountains())
        WE_Biome.addBiomeToGeneration(cp, Proxima_B_Ice_Plains())
        WE_Biome.addBiomeToGeneration(cp, Proxima_B_Beach())
        WE_Biome.addBiomeToGeneration(cp, Proxima_B_Ocean())



        WE_Biome.setBiomeMap(cp, 1.0, 4, cp.biomesList.size * 450.0, 2.0)
    }

    override fun getSolarSize(): Float {
        return 0.3f / this.getCelestialBody()!!.getRelativeDistanceFromCenter().unScaledDistance
    }

    public override fun getMoonPhase(worldTime: Long): Int {
        return (worldTime / this.getDayLength() % 8L + 8L).toInt() % 8
    }

    @OnlyIn(Dist.CLIENT)
    override fun getLightmapColors(
        partialTicks: Float,
        sunBrightness: Float,
        skyLight: Float,
        blockLight: Float,
        colors: FloatArray
    ) {
        val player: EntityPlayer? = FMLClientHandler.instance().getClientPlayerEntity()

        if (player != null) {
            val phase: Int = this.getMoonPhase(this.getWorldTime())

            if (sunBrightness > 0.4f && phase != 0 && phase != 6 && !this.world.isRaining()) {
                //colors[0] = colors[0] + skyLight + 0.8F;				

                colors[1] = colors[1] - skyLight / 1.6f
                colors[2] = colors[2] - skyLight / 1.6f
            }
        }
    }

    override fun getFogColor(arg0: Int, arg1: Int, arg2: Int): Int {
        val A: Int = 10 * 256 * 256 * 256
        val R: Int = 66 * 256 * 256
        val G: Int = 70 * 256
        val B: Int = 80
        val color: Int = A + R + G + B

        return GSUtils.getColor(66, 70, 80, 10)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getFogDensity(arg0: Int, arg1: Int, arg2: Int): Float {
        when (this.getMoonPhase(this.getWorldTime())) {
            0 -> return 0.8f
            1 -> return 0.95f
            3 -> return 0.85f

            else -> return 1.0f
        }
    }

    public override fun getSolarEnergyMultiplier(): Double {
        var solarMultiplier: Double = -1.0
        if (solarMultiplier < 0.0) {
            val s: Double =
                (if (this.getCelestialBody() is IChildBody) this.getSolarSizeForMoon() else this.getSolarSize()).toDouble()
            solarMultiplier = s * s * s * ConfigManagerCore.spaceStationEnergyScalar
        }

        if (this.world == null) return solarMultiplier

        when (this.getMoonPhase(this.getWorldTime())) {
            0 -> return solarMultiplier * 0.35f
            1 -> return solarMultiplier * 0.95f

            3 -> return solarMultiplier * 0.95f

            5 -> return solarMultiplier * 0.5f
            6 -> return solarMultiplier * 0.2f
            else -> return solarMultiplier
        }
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
    }

    companion object {
        @JvmField
        var chunk: WE_ChunkProvider? = null
    }
}
