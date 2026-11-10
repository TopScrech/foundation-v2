package galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Blocks.EnumBlockTauCetiF
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Blocks
import galaxyspace.systems.TauCetiSystem.core.TCBlocks
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.api.dimension.IProviderFog
import asmodeuscore.api.dimension.IProviderWeather
import galaxyspace.systems.TauCetiSystem.TauCetiSystemBodies
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.sky.CloudProviderTauCeti_F
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.sky.SkyProviderTauCeti_F
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.astronomy.dimension.world.worldengine.biome.WE_BaseBiome
import galaxyspace.core.prefab.world.gen.WorldGenCircleBlock
import asmodeuscore.core.astronomy.WeatherData
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class WorldProviderTauCeti_F_WE : WE_WorldProviderSpace(), IProviderFog, IProviderWeather {
    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getFallDamageModifier(): Float {
        return 1.0f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 1.1
    }

    public override fun getMeteorFrequency(): Double {
        return 10.0
    }

    public override fun getSoundVolReductionAmount(): Float {
        return Float.MAX_VALUE
    }

    override fun canSnowAt(pos: BlockPos, checkLight: Boolean): Boolean {
        return false
    }

    override fun getCelestialBody(): CelestialBody? {
        return TauCetiSystemBodies.TauCeti_F
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    @SideOnly(Side.CLIENT)
    override fun getCloudHeight(): Float {
        return 180.0f
    }

    override fun canRespawnHere(): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
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

    @SideOnly(Side.CLIENT)
    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((140 / 255.0f * f).toDouble(), (167 / 255.0f * f).toDouble(), (207 / 255.0f * f).toDouble())
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyColor(): Vector3 {
        var f: Float = 0.8f - this.getStarBrightness(1.0f)
        if (world.isRaining()) {
            f = 1.0f
            return Vector3((47 / 255.0f * f).toDouble(), (47 / 255.0f * f).toDouble(), (47 / 255.0f * f).toDouble())
        }
        return Vector3((101 / 255.0f * f).toDouble(), (146 / 255.0f * f).toDouble(), (205 / 255.0f * f).toDouble())
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

    @SideOnly(Side.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val f: Float = this.world.getCelestialAngle(par1)
        var f1: Float = 1.0f - (MathHelper.cos(f * (Math.PI.toFloat() * 2f)) * 2.0f + 0.25f)
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f)
        return f1 * f1 * 0.5f
    }

    @SideOnly(Side.CLIENT)
    override fun getSunBrightness(par1: Float): Float {
        val f1: Float = this.world.getCelestialAngle(1.0f)
        var f2: Float = 1.0f - (MathHelper.cos(f1 * 3.1415927f * 2.0f) * 2.0f + 0.2f)
        f2 = MathHelper.clamp(f2, 0.0f, 1.0f)

        f2 = 1.2f - f2
        return f2 * 0.8f
    }

    @SideOnly(Side.CLIENT)
    override fun getCloudRenderer(): IRenderHandler? {
        if (super.getCloudRenderer() == null) this.setCloudRenderer(CloudProviderTauCeti_F())

        return super.getCloudRenderer()
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderTauCeti_F())
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
        return (GSDimensions.TAU_CETI_F)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()

        WE_Biome.setBiomeMap((cp)!!, 1.8, 4, 2500.0, 2.0)
        WE_Biome.addPrelinNoise(
            (cp)!!,
            PerlinNoise(((cp)!!.world)!!.getSeed(), 1.5, (cp)!!.biomemapNumberOfOctaves, 400.0, (cp)!!.biomemapScaleY, 0)
        )

        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = TCBlocks.TAUCETI_F_BLOCKS.getStateFromMeta(0)
        terrainGenerator.worldSeaGen = true
        terrainGenerator.worldSeaGenBlock = Blocks.WATER.getDefaultState()
        terrainGenerator.worldSeaGenMaxY = 100
        (cp)!!.createChunkGen_List.add(terrainGenerator)


        //-// 
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        cg.lavaMaxY = 0
        cg.range = 32
        cg.d6_range = 4.5
        cg.frequency = 5
        cg.maxY = 70
        cg.caveBlock = terrainGenerator.worldSeaGenBlock
        cg.ignoreLiquid = true
        (cp)!!.createChunkGen_List.add(cg)


        //-// 
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        rg.lavaBlock = Blocks.LAVA.getDefaultState()
        rg.lavaMaxY = 0
        rg.range = 32
        rg.caveBlock = terrainGenerator.worldSeaGenBlock
        rg.ignoreLiquid = true
        (cp)!!.createChunkGen_List.add(rg)

        (cp as WE_ChunkProviderSpace).worldGenerators.clear()
        cp.biomesList.clear()

        val layer: WE_BiomeLayer = WE_BiomeLayer()
        layer.add(
            TCBlocks.TAUCETI_F_BLOCKS.getStateFromMeta(2),
            terrainGenerator.worldStoneBlock,
            -256,
            0,
            -4,
            -1,
            true
        )
        layer.add(Blocks.BEDROCK.getDefaultState(), 0, 0, 1, 2, true)


        //layer.add(Blocks.PACKED_ICE.getDefaultState(), terrainGenerator.worldStoneBlock, -256, 0,   -4, -10,  true);
        //layer.add(Blocks.SNOW.getDefaultState(), Blocks.PACKED_ICE.getDefaultState(), -256, 0,   -2, -1,  false);
        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(0.0, 1.4, 4, 90, 35, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..79) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    for (y: Int in terrainGenerator.worldSeaGenMaxY downTo 1) {
                        val pos: BlockPos = BlockPos(randPosX, y, randPosZ)
                        if (y < 91 && (world)!!.getBlockState(pos).getMaterial() === Material.WATER && (world)!!.getBlockState(
                                pos.down()
                            ) === TCBlocks.TAUCETI_F_BLOCKS.getDefaultState()
                                .withProperty(
                                    TauCeti_F_Blocks.Companion.BASIC_TYPE,
                                    EnumBlockTauCetiF.STONE
                                )
                        ) (world)!!.setBlockState(pos, TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(0))
                    }
                }

                for (i: Int in 0..1) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    for (y: Int in terrainGenerator.worldSeaGenMaxY downTo 1) {
                        val pos: BlockPos = BlockPos(randPosX, y, randPosZ)
                        if (y < 90 && (world)!!.getBlockState(pos).getMaterial() === Material.WATER && (world)!!.getBlockState(
                                pos.down()
                            ) === TCBlocks.TAUCETI_F_BLOCKS.getDefaultState()
                                .withProperty(
                                    TauCeti_F_Blocks.Companion.BASIC_TYPE,
                                    EnumBlockTauCetiF.STONE
                                )
                        ) {
                            (world)!!.setBlockState(pos, TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(4))
                            (world)!!.setBlockState(pos.up(), TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(5))
                        }
                    }
                }
            }
        }.setColors(0x00FF00, 0x11FF66, 0x00FF00))
        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(0.8, 2.4, 4, 80, 35, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..9) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    for (y: Int in terrainGenerator.worldSeaGenMaxY downTo 1) {
                        val pos: BlockPos = BlockPos(randPosX, y, randPosZ)
                        if ((world)!!.getBlockState(pos)
                                .getMaterial() === Material.WATER && (world)!!.getBlockState(pos.down()) === TCBlocks.TAUCETI_F_BLOCKS.getDefaultState()
                                .withProperty(
                                    TauCeti_F_Blocks.Companion.BASIC_TYPE,
                                    EnumBlockTauCetiF.STONE
                                )
                        ) {
                            val size: Int = (rand)!!.nextInt(15) + 1
                            (world)!!.setBlockState(pos, TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(1))


                            for (l: Int in 1..<size - 1) if (l < size - 1 && (world)!!.getBlockState(pos.up(l + 1))
                                    .getMaterial() === Material.WATER
                            ) (world)!!.setBlockState(pos.up(l), TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(2))

                            if ((world)!!.getBlockState(pos.up(size)).getMaterial() === Material.WATER) (world)!!.setBlockState(
                                pos.up(size - 1),
                                TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(3)
                            )


                            break
                        }
                    }
                }
            }
        }.setColors(0x00FF00, 0x11FF66, 0x00FF00))
        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(2.6, 2.6, 4, 60, 10, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {}
        }.setColors(0x00FF00, 0x11FF66, 0x00FF00))
        WE_Biome.addBiomeToGeneration(cp, object : WE_BaseBiome(4.0, 1.4, 4, 40, 15, layer) {
            override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
                for (i: Int in 0..0) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    for (y: Int in terrainGenerator.worldSeaGenMaxY downTo 1) {
                        val pos: BlockPos = BlockPos(randPosX, y, randPosZ)
                        if ((world)!!.getBlockState(pos)
                                .getMaterial() === Material.WATER && (world)!!.getBlockState(pos.down()) === TCBlocks.TAUCETI_F_BLOCKS.getDefaultState()
                                .withProperty(
                                    TauCeti_F_Blocks.Companion.BASIC_TYPE,
                                    EnumBlockTauCetiF.STONE
                                )
                        ) WorldGenCircleBlock(
                            Blocks.DIRT.getDefaultState(),
                            8,
                            TCBlocks.TAUCETI_F_BLOCKS.getStateFromMeta(2)
                        ).generate((world)!!, (rand)!!, pos)
                    }
                }

                for (i: Int in 0..1) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    for (y: Int in terrainGenerator.worldSeaGenMaxY downTo 1) {
                        val pos: BlockPos = BlockPos(randPosX, y, randPosZ)
                        if (y > 35 && (world)!!.getBlockState(pos).getMaterial() === Material.WATER && (world)!!.getBlockState(
                                pos.down()
                            ) === TCBlocks.TAUCETI_F_BLOCKS.getDefaultState()
                                .withProperty(
                                    TauCeti_F_Blocks.Companion.BASIC_TYPE,
                                    EnumBlockTauCetiF.STONE
                                )
                        ) {
                            val size: Int = (rand)!!.nextInt(15) + 1
                            (world)!!.setBlockState(pos, TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(1))


                            for (l: Int in 1..<size - 1) if (l < size - 1 && (world)!!.getBlockState(pos.up(l + 1))
                                    .getMaterial() === Material.WATER
                            ) (world)!!.setBlockState(pos.up(l), TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(2))

                            if ((world)!!.getBlockState(pos.up(size)).getMaterial() === Material.WATER) (world)!!.setBlockState(
                                pos.up(size - 1),
                                TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(3)
                            )


                            break
                        }
                    }
                }

                for (i: Int in 0..49) {
                    val randPosX: Int = x + (rand)!!.nextInt(16) + 8
                    val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
                    for (y: Int in terrainGenerator.worldSeaGenMaxY downTo 1) {
                        val pos: BlockPos = BlockPos(randPosX, y, randPosZ)
                        if (y > 35 && (world)!!.getBlockState(pos).getMaterial() === Material.WATER && (world)!!.getBlockState(
                                pos.down()
                            ) === TCBlocks.TAUCETI_F_BLOCKS.getDefaultState()
                                .withProperty(
                                    TauCeti_F_Blocks.Companion.BASIC_TYPE,
                                    EnumBlockTauCetiF.STONE
                                )
                        ) (world)!!.setBlockState(pos, TCBlocks.TAUCETI_F_WATERGRASS.getStateFromMeta(6))
                    }
                }
            }
        }.setColors(0x00FF00, 0x11FF66, 0x00FF00))


        //WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(1.0D, 1.6F, 4, 40, 15, layer).setPrelinNoiseID(1).setColors(0x00FF00, 0xFF00FF, 0x00FF00));	

        //WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-0.3D, 0.3D, 1.9F, 4, 90, 3, layer).setColors(0x00FF00, 0xFF0000, 0x00FF00));	
        //WE_Biome.addBiomeToGeneration(cp, new WE_BaseBiome(-0.9D, 0.9D, 1.5F, 4, 135, 15, layer).setColors(0x00FF00, 0xFF0000, 0x00FF00));	
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun getThermalValueMod(): Float {
        return 0.4f
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
    }

    override fun getThermalLevelModifier(player: EntityPlayer?): Float {
        //if(player.posY < 80) 
        //return 1.0F;

        return this.getThermalLevelModifier()
    }

    override fun getSolarSize(): Float {
        return 1.5f / this.getCelestialBody()!!.getRelativeDistanceFromCenter().unScaledDistance
    }

    override fun getFogDensity(x: Int, y: Int, z: Int): Float {
        if (this.world.isRaining()) return 0.92f
        return 0.95f
    }

    override fun isColorWorld(): Boolean {
        return true
    }

    override fun getWeather(): WeatherData? {
        return IProviderWeather.lightning_storm.withFrequence((if (this.world.thunderingStrength > 0) 20 else if (this.world.isRaining()) 10 else 0).toDouble())
    }
}
