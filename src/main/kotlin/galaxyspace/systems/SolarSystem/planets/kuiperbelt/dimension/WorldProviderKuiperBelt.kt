package galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension

import foundation.persistence.containsTag
import foundation.persistence.listOfType

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.core.util.GSDimensions
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.ChunkProviderKuiperBelt
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.WorldProviderKuiperBelt
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.KuiperBeltSaveData
import asmodeuscore.core.astronomy.dimension.world.gen.WorldProviderAdvancedSpace
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.world.gen.BiomeProviderKuiperBelt
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.sky.SkyProviderKuiperBelt
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import net.minecraft.world.level.block.Block
import net.minecraft.world.entity.Entity
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.HashSet
import java.util.TreeMap
import kotlin.math.max

open class WorldProviderKuiperBelt : WorldProviderAdvancedSpace(), ISolarLevel, IZeroGDimension {
    //Used to list asteroid centres to external code that needs to know them
    private val asteroids = HashSet<AsteroidData>()
    private var dataNotLoaded = true
    private var datafile: KuiperBeltSaveData? = null
    private val solarMultiplier = -1.0

    //	@Override
    //	public void registerWorldChunkManager()
    //	{
    //		this.worldChunkMgr = new WorldChunkManagerAsteroids(this.world, 0F);
    //	}
    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.planetKuiperBelt
    }

    override fun getFogColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun getSkyColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun hasSunset(): Boolean {
        return false
    }

    override fun getDayLength(): Long {
        return 0
    }

    override fun isDaytime(): Boolean {
        return true
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderKuiperBelt::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderKuiperBelt::class.java
    }

    override fun calculateCelestialAngle(par1: Long, par3: Float): Float {
        return 0.22f
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

    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getAverageGroundLevel(): Int {
        return 96
    }

    override fun canCoordinateBeSpawn(var1: Int, var2: Int): Boolean {
        return true
    }

    //Overriding so that beds do not explode on Asteroids
    public override fun canRespawnHere(): Boolean {
        if (EventHandlerGC.bedActivated) {
            EventHandlerGC.bedActivated = false
            return true
        }
        return false
    }

    override fun getMeteorFrequency(): Double {
        return 10.0
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.9
    }

    override fun getFallDamageModifier(): Float {
        return 0.1f
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderKuiperBelt())
        }

        return super.getSkyRenderer()
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
        return f2 * 1.0f
    }

    open fun addAsteroid(x: Int, y: Int, z: Int, size: Int, core: Int) {
        val coords: AsteroidData = AsteroidData(x, y, z, size, core)
        if (!this.asteroids.contains(coords)) {
            if (this.dataNotLoaded) {
                this.loadAsteroidSavedData()
            }
            if (!this.asteroids.contains(coords)) {
                this.addToNBT(this.datafile!!.datacompound, coords)
                this.asteroids.add(coords)
            }
        }
    }

    open fun removeAsteroid(x: Int, y: Int, z: Int) {
        val coords: AsteroidData = AsteroidData(x, y, z)
        if (this.asteroids.contains(coords)) {
            this.asteroids.remove(coords)

            if (this.dataNotLoaded) {
                this.loadAsteroidSavedData()
            }
            this.writeToNBT(this.datafile!!.datacompound)
        }
    }

    private fun loadAsteroidSavedData() {
        this.datafile = KuiperBeltSaveData.get(this.world)
        this.readFromNBT(this.datafile!!.datacompound)

        this.dataNotLoaded = false
    }

    private fun readFromNBT(nbt: NBTTagCompound) {
        val coordList: NBTTagList = nbt.listOfType("coords", 10)
        if (coordList.size > 0) {
            for (j: Int in 0..<coordList.size) {
                val tag1: NBTTagCompound = coordList.getCompoundOrEmpty(j)

                if (tag1 != null) {
                    this.asteroids.add(AsteroidData.Companion.readFromNBT(tag1))
                }
            }
        }
    }

    private fun writeToNBT(nbt: NBTTagCompound) {
        val coordList: NBTTagList = NBTTagList()
        for (coords: AsteroidData in this.asteroids) {
            val tag: NBTTagCompound = NBTTagCompound()
            coords.writeToNBT(tag)
            coordList.add(tag)
        }
        nbt.put("coords", coordList)
        this.datafile!!.setDirty()
    }

    private fun addToNBT(nbt: NBTTagCompound, coords: AsteroidData) {
        val coordList: NBTTagList = nbt.listOfType("coords", 10)
        val tag: NBTTagCompound = NBTTagCompound()
        coords.writeToNBT(tag)
        coordList.add(tag)
        nbt.put("coords", coordList)
        this.datafile!!.setDirty()
    }


    open fun checkHasAsteroids(): Boolean {
        if (this.dataNotLoaded) {
            this.loadAsteroidSavedData()
        }

        if (this.asteroids.size == 0) {
            return false
        }

        return true
    }

    open fun getClosestAsteroidXZ(x: Int, y: Int, z: Int, mark: Boolean): BlockVec3? {
        if (!this.checkHasAsteroids()) {
            return null
        }

        var result: BlockVec3? = null
        var resultRoid: AsteroidData? = null
        var lowestDistance: Int = Int.MAX_VALUE

        for (test: AsteroidData in this.asteroids) {
            if (mark && (test.sizeAndLandedFlag and 128) > 0) {
                continue
            }

            val dx: Int = x - test.centre!!.x
            val dz: Int = z - test.centre!!.z
            val a: Int = dx * dx + dz * dz
            if (a < lowestDistance) {
                lowestDistance = a
                result = test.centre
                resultRoid = test
            }
        }

        if (result == null) {
            return null
        }

        if (mark) {
            resultRoid!!.sizeAndLandedFlag = resultRoid.sizeAndLandedFlag or 128
            this.writeToNBT(this.datafile!!.datacompound)
        }
        result = result.clone()
        result.sideDoneBits = resultRoid!!.sizeAndLandedFlag and 127
        return result
    }

    open fun getClosestAsteroidsXZ(x: Int, y: Int, z: Int, facing: Int, count: Int): ArrayList<BlockVec3?>? {
        if (!this.checkHasAsteroids()) {
            return null
        }

        val targets: TreeMap<Int, BlockVec3> = TreeMap<Int, BlockVec3>()

        for (roid: AsteroidData in this.asteroids) {
            val test: BlockVec3? = roid.centre
            when (facing) {
                2 -> if (z - 16 < test!!.z) {
                    continue
                }

                3 -> if (z + 16 > test!!.z) {
                    continue
                }

                4 -> if (x - 16 < test!!.x) {
                    continue
                }

                5 -> if (x + 16 > test!!.x) {
                    continue
                }
            }
            val dx: Int = x - test!!.x
            val dz: Int = z - test.z
            val a: Int = dx * dx + dz * dz
            if (a < 262144) {
                targets.put(a, test)
            }
        }

        val max: Int = max(count, targets.size)
        if (max <= 0) {
            return null
        }

        val returnValues: ArrayList<BlockVec3?> = ArrayList<BlockVec3?>()
        var i: Int = 0
        val offset: Int = EntityAstroMiner.MINE_LENGTH_AST / 2
        for (target: BlockVec3 in targets.values) {
            val coords: BlockVec3 = target.clone()
            GCLog.debug("Found nearby asteroid at " + target.toString())
            when (facing) {
                2 -> coords.z += offset
                3 -> coords.z -= offset
                4 -> coords.x += offset
                5 -> coords.x -= offset
            }
            returnValues.add(coords)
            if (++i >= count) {
                break
            }
        }

        return returnValues
    }

    override fun getActualHeight(): Int {
        return 256
    }

    override fun init() {
        super.init()
        this.nether = true
    }

    override fun getSolarEnergyMultiplier(): Double {
        /*if (this.solarMultiplier < 0D)
        {
            double s = this.getSolarSize();
            this.solarMultiplier = s * s * s * ConfigManagerCore.spaceStationEnergyScalar;
        }
        return this.solarMultiplier;*/
        return super.getSolarEnergyMultiplier()
    }

    private open class AsteroidData {
        @JvmField
        var centre: BlockVec3?

        @JvmField
        var sizeAndLandedFlag: Int = 15

        @JvmField
        protected var coreAndSpawnedFlag: Int = -2

        constructor(x: Int, y: Int, z: Int) {
            this.centre = BlockVec3(x, y, z)
        }

        constructor(x: Int, y: Int, z: Int, size: Int, core: Int) {
            this.centre = BlockVec3(x, y, z)
            this.sizeAndLandedFlag = size
            this.coreAndSpawnedFlag = core
        }

        constructor(bv: BlockVec3?) {
            this.centre = bv
        }

        override fun hashCode(): Int {
            if (this.centre != null) {
                return this.centre.hashCode()
            } else {
                return 0
            }
        }

        override fun equals(o: Any?): Boolean {
            if (o is AsteroidData) {
                val vector: BlockVec3? = o.centre
                return this.centre!!.x == vector!!.x && this.centre!!.y == vector.y && this.centre!!.z == vector.z
            }

            if (o is BlockVec3) {
                val vector: BlockVec3 = o
                return this.centre!!.x == vector.x && this.centre!!.y == vector.y && this.centre!!.z == vector.z
            }

            return false
        }

        open fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
            tag.putInt("x", this.centre!!.x)
            tag.putInt("y", this.centre!!.y)
            tag.putInt("z", this.centre!!.z)
            tag.putInt("coreAndFlag", this.coreAndSpawnedFlag)
            tag.putInt("sizeAndFlag", this.sizeAndLandedFlag)
            return tag
        }

        companion object {
            @JvmStatic
            fun readFromNBT(tag: NBTTagCompound): AsteroidData {
                val tempVector: BlockVec3 = BlockVec3()
                tempVector.x = tag.getIntOr("x", 0)
                tempVector.y = tag.getIntOr("y", 0)
                tempVector.z = tag.getIntOr("z", 0)

                val roid: AsteroidData = AsteroidData(tempVector)
                if (tag.containsTag("coreAndFlag")) {
                    roid.coreAndSpawnedFlag = tag.getIntOr("coreAndFlag", 0)
                }
                if (tag.containsTag("sizeAndFlag")) {
                    roid.sizeAndLandedFlag = tag.getIntOr("sizeAndFlag", 0)
                }

                return roid
            }
        }
    }

    override fun getDungeonSpacing(): Int {
        return 0
        //Used for generating Abandoned Base 
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.KUIPER_BELT)!!
    }

    override fun getArrowGravity(): Float {
        return 0.002f
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return null
    }

    override fun hasSkyLight(): Boolean {
        return false
    }

    override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun inFreefall(entity: Entity?): Boolean {
        return true
    }

    override fun setInFreefall(entity: Entity?) {
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun getThermalValueMod(): Float {
        return 0.01f
    }
}
