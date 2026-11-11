package galaxyspace.systems.SolarSystem.moons.triton.dimenson

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import asmodeuscore.api.dimension.IProviderFreeze
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import galaxyspace.systems.SolarSystem.moons.triton.dimenson.sky.SkyProviderTriton
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import galaxyspace.systems.SolarSystem.moons.triton.world.gen.we.Triton_Plains
import galaxyspace.systems.SolarSystem.moons.triton.world.gen.we.Triton_Mountains
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class WorldProviderTriton_WE : WE_WorldProviderSpace(), IProviderFreeze {
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
        return 3.0
    }

    public override fun getSoundVolReductionAmount(): Float {
        return Float.MAX_VALUE
    }

    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.tritonNeptune
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((104 / 255.0f * f).toDouble(), (104 / 255.0f * f).toDouble(), (104 / 255.0f * f).toDouble())
    }

    override fun getSkyColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun isSkyColored(): Boolean {
        return false
    }

    override fun hasSunset(): Boolean {
        return false
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

        return var3 * var3 * 0.5f + 0.3f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSunBrightness(par1: Float): Float {
        val f1: Float = this.world.getCelestialAngle(1.0f)
        var f2: Float = 1.25f - (MathHelper.cos(f1 * 3.1415927f * 2.0f) * 2.0f + 0.2f)
        val f3: Float = this.world.getWorldTime().toFloat()
        if (f2 < 0.0f) {
            f2 = 0.0f
        }

        if (f2 > 1.0f) {
            f2 = 1.0f
        }

        f2 = 1.2f - f2
        return f2 * 0.35f
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderTriton())
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
        return (GSDimensions.TRITON)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()

        (cp as WE_ChunkProviderSpace).CRATER_PROB = 400

        WE_Biome.setBiomeMap(cp, 1.2, 4, 1800.0, 1.0)

        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = GSBlocks.TRITON_BLOCKS.getStateFromMeta(2)
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
        /*
		WE_RavineGen rg = new WE_RavineGen();
		rg.replaceBlocksList    .clear();
		rg.addReplacingBlock(terrainGenerator.worldStoneBlock);
		rg.addReplacingBlock(MarsBlocks.marsBlock.getStateFromMeta(6));
		rg.addReplacingBlock(MarsBlocks.marsBlock.getStateFromMeta(5));
		rg.addReplacingBlock(Blocks.PACKED_ICE.defaultBlockState());
		rg.lavaBlock = Blocks.AIR.defaultBlockState();
		rg.lavaMaxY = 0;
		cp.createChunkGen_List.add(rg);
		
		MapGenCavernMars cavernGenerator = new MapGenCavernMars();
		cp.worldGenerators.clear();
		cp.worldGenerators.add(cavernGenerator);
		*/
        cp.biomesList.clear()

        WE_Biome.addBiomeToGeneration(cp, Triton_Plains(0.0, -0.8, 1.4, 74, 16))
        WE_Biome.addBiomeToGeneration(cp, Triton_Plains(0.6, -0.3, 1.9, 95, 5))
        WE_Biome.addBiomeToGeneration(cp, Triton_Mountains(0.8, 0.0))
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return super.enableAdvancedThermalLevel()
    }

    override fun getThermalValueMod(): Float {
        return 0.001f
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
    }
}
