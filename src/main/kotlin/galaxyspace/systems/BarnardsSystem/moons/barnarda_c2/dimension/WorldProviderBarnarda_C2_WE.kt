package galaxyspace.systems.BarnardsSystem.moons.barnarda_c2.dimension

import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.BarnardsSystemBodies
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c2.dimension.WorldProviderBarnarda_C2_WE
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import asmodeuscore.core.astronomy.dimension.world.worldengine.biome.WE_BaseBiome
import asmodeuscore.api.dimension.IProviderFreeze
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_BiomeProvider
import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderAsteroids
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class WorldProviderBarnarda_C2_WE : WE_WorldProviderSpace(), IProviderFreeze {
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
        return BarnardsSystemBodies.Companion.Barnarda_C2
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return WE_BiomeProvider::class.java
    }

    @SideOnly(Side.CLIENT)
    override fun getCloudHeight(): Float {
        return 180.0f
    }

    override fun canRespawnHere(): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((160 / 255.0f * f).toDouble(), (167 / 255.0f * f).toDouble(), (190 / 255.0f * f).toDouble())
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyColor(): Vector3 {
        return Vector3(0.0)
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

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderAsteroids())
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
        return (GSDimensions.BARNARDA_C2)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()


        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = Blocks.STONE.getDefaultState()
        terrainGenerator.worldSeaGen = true
        terrainGenerator.worldSeaGenBlock = Blocks.LAVA.getDefaultState()
        terrainGenerator.worldSeaGenMaxY = 64
        (cp)!!.createChunkGen_List.add(terrainGenerator)


        //-// 
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        cg.lavaMaxY = 15
        cg.range = 64
        (cp)!!.createChunkGen_List.add(cg)


        //-// 
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        rg.lavaBlock = Blocks.LAVA.getDefaultState()
        rg.lavaMaxY = 15
        rg.range = 32
        (cp)!!.createChunkGen_List.add(rg)

        (cp as WE_ChunkProviderSpace).worldGenerators.clear()
        cp.biomesList.clear()

        val layer: WE_BiomeLayer = WE_BiomeLayer()
        layer.add(Blocks.BEDROCK.getDefaultState(), 0, 0, 1, 2, true)
        layer.add(Blocks.OBSIDIAN.getDefaultState(), terrainGenerator.worldStoneBlock, -256, 0, -4, -10, true)
        layer.add(Blocks.COAL_BLOCK.getDefaultState(), Blocks.OBSIDIAN.getDefaultState(), -256, 0, -2, -1, false)


        WE_Biome.addBiomeToGeneration(cp, WE_BaseBiome(0.0, 2.0, 1.8, 4, 64, 10, layer))
        WE_Biome.addBiomeToGeneration(cp, WE_BaseBiome(2.0, 4.0, 2.4, 4, 90, 30, layer))

        WE_Biome.setBiomeMap(cp, 1.4, 4, (cp.biomesList.size * 400).toDouble(), 1.0)
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun getThermalValueMod(): Float {
        return 1.0f
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
    }
}
