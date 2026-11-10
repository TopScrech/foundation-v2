package galaxyspace.systems.SolarSystem.moons.europa.dimension

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.SolarSystem.moons.europa.dimension.sky.SkyProviderEuropa
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_Ravine
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_Plains
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_HillPlains
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_Spikes
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class WorldProviderEuropa_WE : WE_WorldProviderSpace() {
    override fun getFuelUsageMultiplier(): Double {
        return 0.0
    }

    override fun getFallDamageModifier(): Float {
        return 0.5f
    }

    public override fun getMeteorFrequency(): Double {
        return 3.0
    }

    public override fun getSoundVolReductionAmount(): Float {
        return Float.MAX_VALUE
    }

    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.europaJupiter
    }

    override fun getDungeonSpacing(): Int {
        return 0
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return null
    }

    override fun hasSunset(): Boolean {
        return false
    }

    override fun getFogColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun getSkyColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun shouldForceRespawn(): Boolean {
        return !ConfigManagerCore.forceOverworldRespawn
    }

    @SideOnly(Side.CLIENT)
    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderEuropa())
        }

        return super.getSkyRenderer()
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator>? {
        return null
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
    }


    override fun isFreeze(): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val var2: Float = this.world.getCelestialAngle(par1)
        var var3: Float = 1.0f - (MathHelper.cos(var2 * Math.PI.toFloat() * 2.0f) * 2.0f + 0.25f)

        if (var3 < 0.0f) {
            var3 = 0.0f
        }

        if (var3 > 1.0f) {
            var3 = 1.0f
        }

        return var3 * var3 * 1.5f + 0.4f
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()
        (cp as WE_ChunkProviderSpace).worldGenerators.clear()
        cp.biomesList.clear()

        WE_Biome.setBiomeMap(cp, 1.0, 4, 1300.0, 6.0)

        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = GSBlocks.EUROPA_BLOCKS.getStateFromMeta(1)
        terrainGenerator.worldSeaGen = false
        terrainGenerator.worldSeaGenMaxY = 64
        cp.createChunkGen_List.add(terrainGenerator)

        if (GSConfigWorld.enableOresGeneration) {
            val standardOres: WE_OreGen = WE_OreGen()
            standardOres.add(GSBlocks.EUROPA_BLOCKS.getStateFromMeta(3), terrainGenerator.worldStoneBlock, 2, 1, 8, 4)
            standardOres.add(GSBlocks.EUROPA_BLOCKS.getStateFromMeta(4), terrainGenerator.worldStoneBlock, 6, 1, 25, 6)
            standardOres.add(GSBlocks.EUROPA_BLOCKS.getStateFromMeta(5), terrainGenerator.worldStoneBlock, 8, 1, 45, 8)
            cp.decorateChunkGen_List.add(standardOres)
        }

        WE_Biome.addBiomeToGeneration(cp, Europa_Ravine(terrainGenerator))
        WE_Biome.addBiomeToGeneration(cp, Europa_Plains(terrainGenerator, 0.35))
        WE_Biome.addBiomeToGeneration(cp, Europa_HillPlains(terrainGenerator, 1.0))
        WE_Biome.addBiomeToGeneration(cp, Europa_Spikes(terrainGenerator))
        WE_Biome.addBiomeToGeneration(cp, Europa_Plains(terrainGenerator, 5.0))
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.EUROPA)!!
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun getThermalValueMod(): Float {
        return 0.01f
    }
}
