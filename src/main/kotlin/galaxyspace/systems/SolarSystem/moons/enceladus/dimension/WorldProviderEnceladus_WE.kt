package galaxyspace.systems.SolarSystem.moons.enceladus.dimension

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.SolarSystem.moons.enceladus.dimension.WorldProviderEnceladus_WE
import galaxyspace.core.configs.GSConfigWorld
import galaxyspace.core.GSBlocks
import galaxyspace.core.util.GSDimensions
import asmodeuscore.api.dimension.IProviderFreeze
import galaxyspace.systems.SolarSystem.moons.enceladus.dimension.sky.SkyProviderEnceladus
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.we.Enceladus_Plains
import galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.we.Enceladus_Mountains
import galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.we.Enceladus_Ravine
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class WorldProviderEnceladus_WE : WE_WorldProviderSpace(), IProviderFreeze {
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
        return SolarSystemBodies.Companion.enceladusSaturn
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
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

        return var3 * var3 * 0.5f + 0.3f
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderEnceladus())
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
        return (GSDimensions.ENCELADUS)!!
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()

        WE_Biome.setBiomeMap((cp)!!, 1.2, 4, 1200.0, 5.0)

        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(1)
        terrainGenerator.worldSeaGen = false
        terrainGenerator.worldSeaGenMaxY = 64
        (cp)!!.createChunkGen_List.add(terrainGenerator)


        //-// 
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        cg.lavaMaxY = 0
        (cp)!!.createChunkGen_List.add(cg)


        //-// 
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(GSBlocks.EUROPA_BLOCKS.getStateFromMeta(0))
        rg.addReplacingBlock(GSBlocks.EUROPA_BLOCKS.getStateFromMeta(1))
        rg.addReplacingBlock(Blocks.PACKED_ICE.getDefaultState())
        rg.lavaBlock = Blocks.AIR.getDefaultState()
        rg.lavaMaxY = 0
        (cp)!!.createChunkGen_List.add(rg)

        if (GSConfigWorld.enableOresGeneration) {
            val standardOres: WE_OreGen = WE_OreGen()
            //Coal
            standardOres.add(
                GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(2),
                terrainGenerator.worldStoneBlock,
                10,
                10,
                120,
                20
            )
            (cp)!!.decorateChunkGen_List.add(standardOres)
        }

        (cp as WE_ChunkProviderSpace).worldGenerators.clear()
        cp.biomesList.clear()

        WE_Biome.addBiomeToGeneration(cp, Enceladus_Plains())
        WE_Biome.addBiomeToGeneration(cp, Enceladus_Mountains())
        WE_Biome.addBiomeToGeneration(cp, Enceladus_Ravine())
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

    override fun getThermalValueMod(): Float {
        return 0.01f
    }
}
