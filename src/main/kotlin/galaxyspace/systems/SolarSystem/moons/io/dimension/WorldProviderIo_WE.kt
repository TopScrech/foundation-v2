package galaxyspace.systems.SolarSystem.moons.io.dimension

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomTreasureIo
import asmodeuscore.api.dimension.IProviderFreeze
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.SolarSystem.moons.io.world.gen.BiomeProviderIo
import galaxyspace.systems.SolarSystem.moons.io.dimension.sky.SkyProviderIo
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import galaxyspace.systems.SolarSystem.moons.io.dimension.WorldProviderIo_WE
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import galaxyspace.systems.SolarSystem.moons.io.world.gen.we.Io_Plains
import galaxyspace.systems.SolarSystem.moons.io.world.gen.we.Io_Mountains
import galaxyspace.systems.SolarSystem.moons.io.world.gen.we.Io_Volcano
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
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class WorldProviderIo_WE : WE_WorldProviderSpace(), IProviderFreeze {
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
        return SolarSystemBodies.ioJupiter
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderIo::class.java
    }

    override fun getFogColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
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

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderIo())
        }

        return super.getSkyRenderer()
    }

    override fun getDungeonSpacing(): Int {
        return 0
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasureIo.Companion.IOCHEST
    }

    public override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.IO)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        chunk = cp

        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()

        WE_Biome.setBiomeMap((cp)!!, 1.2, 6, 850.0, 0.275)

        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = GSBlocks.IO_BLOCKS.getStateFromMeta(1)
        terrainGenerator.worldSeaGen = false
        (cp)!!.createChunkGen_List.add(terrainGenerator)


        //-// 
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        cg.lavaBlock = Blocks.LAVA.defaultBlockState()
        (cp)!!.createChunkGen_List.add(cg)


        //-// 
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        rg.lavaBlock = Blocks.LAVA.defaultBlockState()
        (cp)!!.createChunkGen_List.add(rg)

        (cp as WE_ChunkProviderSpace).worldGenerators.clear()
        cp.biomesList.clear()

        WE_Biome.addBiomeToGeneration(cp, Io_Plains())
        WE_Biome.addBiomeToGeneration(cp, Io_Mountains(0, 1.4, 1.9, 120, 35))
        WE_Biome.addBiomeToGeneration(cp, Io_Mountains(1, 1.9, 2.4, 125, 35))
        WE_Biome.addBiomeToGeneration(cp, Io_Volcano())
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return false
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
