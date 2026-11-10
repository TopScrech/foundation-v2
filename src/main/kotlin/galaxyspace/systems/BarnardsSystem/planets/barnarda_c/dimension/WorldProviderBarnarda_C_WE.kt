package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension

import galaxyspace.systems.BarnardsSystem.BarnardsSystemBodies
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension.WorldProviderBarnarda_C_WE
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.api.dimension.IProviderWeather
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen
import galaxyspace.core.configs.GSConfigWorld
import asmodeuscore.core.astronomy.WeatherData
import asmodeuscore.api.dimension.IProviderFreeze
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension.sky.CloudProviderBarnarda_C
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension.sky.SkyProviderBarnarda_C
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_DeepOcean
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Ocean
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Beach
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Plains
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Forest
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_River
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Swampland
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Jungle
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Mountains
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_SnowPlains
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Dunes
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.max

open class WorldProviderBarnarda_C_WE : WE_WorldProviderSpace(), IProviderFreeze, IProviderWeather {
    private val colorsSunriseSunset = FloatArray(4)
    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getFallDamageModifier(): Float {
        return 1.0f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 1.0
    }

    public override fun getMeteorFrequency(): Double {
        return 0.0
    }

    public override fun getSoundVolReductionAmount(): Float {
        return Float.MIN_VALUE
    }

    override fun getCelestialBody(): CelestialBody? {
        return BarnardsSystemBodies.Companion.Barnarda_C
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProviderSpace::class.java
    }

    @SideOnly(Side.CLIENT)
    override fun getCloudHeight(): Float {
        return 180.0f
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

    /*
    @Override
    @SideOnly(Side.CLIENT)
    public Vector3 getFogColor() {
    	
    	float f = 1.0F - this.getStarBrightness(1.0F);
        return new Vector3(86 / 255.0F * f, 180 / 255.0F * f, 240 / 255.0F * f);
    }

    @Override
    public Vector3 getSkyColor() {
    	float f = 0.6F - this.getStarBrightness(1.0F);
        return new Vector3(100 / 255.0F * f, 220 / 255.0F * f, 250 / 255.0F * f);
    }*/
    @SideOnly(Side.CLIENT)
    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)

        return Vector3((140 / 255.0f * f).toDouble(), (167 / 255.0f * f).toDouble(), (207 / 255.0f * f).toDouble())
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyColor(): Vector3 {
        var f: Float = 0.5f - this.getStarBrightness(1.0f)
        f = max(f, 0.0f)
        if (world.isRaining()) {
            f = 1.0f
            return Vector3((47 / 255.0f * f).toDouble(), (47 / 255.0f * f).toDouble(), (47 / 255.0f * f).toDouble())
        }
        return Vector3((61 / 255.0f * f).toDouble(), (86 / 255.0f * f).toDouble(), (175 / 255.0f * f).toDouble())
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

        if (world.isRaining()) {
            return f1 * f1 * 0.2f
        }

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
        if (super.getCloudRenderer() == null) this.setCloudRenderer(CloudProviderBarnarda_C())

        return super.getCloudRenderer()
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderBarnarda_C())
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
        return (GSDimensions.BARNARDA_C)!!
    }

    override fun canRespawnHere(): Boolean {
        EventHandlerGC.bedActivated = false
        return true
    }

    override fun isSurfaceWorld(): Boolean {
        if (this.world == null) return false

        if (!this.world.isRemote) return false

        return true //this.world.isRemote;
    }

    override fun canSleepAt(player: EntityPlayer, pos: BlockPos): WorldSleepResult {
        return if (this.canRespawnHere()) WorldSleepResult.ALLOW else WorldSleepResult.BED_EXPLODES
    }

    override fun genSettings(cp: WE_ChunkProvider?) {
        chunk = cp

        (cp)!!.createChunkGen_List.clear()
        (cp)!!.createChunkGen_InXZ_List.clear()
        (cp)!!.createChunkGen_InXYZ_List.clear()
        (cp)!!.decorateChunkGen_List.clear()
        (cp as WE_ChunkProviderSpace).worldGenerators.clear()


        val terrainGenerator: WE_TerrainGenerator = WE_TerrainGenerator()
        terrainGenerator.worldStoneBlock = BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(1)
        terrainGenerator.worldSeaGen = true
        terrainGenerator.worldSeaGenBlock = Blocks.WATER.getDefaultState()
        terrainGenerator.worldSeaGenMaxY = 64
        cp.createChunkGen_List.add(terrainGenerator)


        //-// 
        val cg: WE_CaveGen = WE_CaveGen()
        cg.replaceBlocksList.clear()
        cg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        cg.lavaMaxY = 8
        cp.createChunkGen_List.add(cg)


        //-// 
        val rg: WE_RavineGen = WE_RavineGen()
        rg.replaceBlocksList.clear()
        rg.addReplacingBlock(terrainGenerator.worldStoneBlock)
        rg.lavaBlock = Blocks.LAVA.getDefaultState()
        rg.lavaMaxY = 15
        cp.createChunkGen_List.add(rg)

        val standardOres: WE_OreGen = WE_OreGen()
        if (GSConfigWorld.enableOresGeneration) {
            //Coal
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(0),
                terrainGenerator.worldStoneBlock,
                15,
                5,
                150,
                20
            )
            //Iron
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(1),
                terrainGenerator.worldStoneBlock,
                6,
                5,
                70,
                15
            )
            //Gold
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(2),
                terrainGenerator.worldStoneBlock,
                6,
                5,
                45,
                10
            )
            //Redstone
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(3),
                terrainGenerator.worldStoneBlock,
                6,
                5,
                15,
                20
            )
            //Lapis
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(4),
                terrainGenerator.worldStoneBlock,
                4,
                5,
                35,
                4
            )
            //Diamond
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(5),
                terrainGenerator.worldStoneBlock,
                5,
                5,
                12,
                8
            )
            //Silicon
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(6),
                terrainGenerator.worldStoneBlock,
                6,
                10,
                40,
                8
            )
            //Copper
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(7),
                terrainGenerator.worldStoneBlock,
                8,
                20,
                150,
                20
            )
            //Tin
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(8),
                terrainGenerator.worldStoneBlock,
                8,
                15,
                64,
                15
            )
            //Aluminum
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(9),
                terrainGenerator.worldStoneBlock,
                5,
                5,
                30,
                15
            )
            //Quartz
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(10),
                terrainGenerator.worldStoneBlock,
                4,
                5,
                15,
                8
            )
            //Cobaltum
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(11),
                terrainGenerator.worldStoneBlock,
                4,
                5,
                25,
                20
            )
            //Nickel
            standardOres.add(
                BRBlocks.BARNARDA_C_ORES.getStateFromMeta(12),
                terrainGenerator.worldStoneBlock,
                4,
                5,
                15,
                15
            )
        }
        //Dirt
        standardOres.add(
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0),
            terrainGenerator.worldStoneBlock,
            30,
            5,
            150,
            30
        )
        //Gravel
        standardOres.add(Blocks.GRAVEL.getDefaultState(), terrainGenerator.worldStoneBlock, 15, 5, 150, 20)
        //Granite
        standardOres.add(Blocks.STONE.getStateFromMeta(1), terrainGenerator.worldStoneBlock, 15, 5, 150, 20)
        //Diorite
        standardOres.add(Blocks.STONE.getStateFromMeta(3), terrainGenerator.worldStoneBlock, 15, 5, 150, 20)
        //Andesite
        standardOres.add(Blocks.STONE.getStateFromMeta(5), terrainGenerator.worldStoneBlock, 15, 5, 150, 20)

        cp.decorateChunkGen_List.add(standardOres)

        cp.biomesList.clear()

        //WE_Biome.addPrelinNoise(cp, new PerlinNoise(cp.worldObj.getSeed(), 1.0D, 1, 100D, .5D, 0));
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_DeepOcean(-4.0, 4.0))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Ocean(-3.8, 3.8, false))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Beach(-3.5, 3.5, 1))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Plains(-3.4, 3.4))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Forest(-2.9, 2.9))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_River(-2.5, 2.5))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Swampland(-2.4, 2.4))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Jungle(-1.9, 1.9))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Mountains(-1.4, 1.4, 100, 2.8, 4))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Mountains(-1.0, 1.0, 180, 3.0, 4))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_SnowPlains(-0.8, 0.8, 160))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Mountains(-0.6, 0.6, 100, 2.4, 4))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Dunes(-0.3, 0.3))
        WE_Biome.addBiomeToGeneration(cp, Barnarda_C_Ocean(-0.0, 0.0, true))

        WE_Biome.setBiomeMap(cp, 1.8, 4, cp.biomesList.size * 450.0, 2.4)
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun getThermalValueMod(): Float {
        return 0.05f
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int) {
    }

    override fun isColorWorld(): Boolean {
        return true
    }


    public override fun getWindLevel(): Float {
        val windLevel: Float = this.getCelestialBody()!!.atmosphere.windLevel()
        return if (this.world.isThundering()) windLevel * 2 else windLevel
    }

    override fun getWeather(): WeatherData? {
        return IProviderWeather.lightning_storm.withFrequence((if (this.world.thunderingStrength > 0) 10 else 0).toDouble())
    }

    companion object {
        @JvmField
        var chunk: WE_ChunkProvider? = null
    }
}
