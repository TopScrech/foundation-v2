package micdoodle8.mods.galacticraft.planets.asteroids.dimension

import java.util.ArrayList
import java.util.HashSet
import java.util.TreeMap

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import micdoodle8.mods.galacticraft.planets.GCPlanetDimensions
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids
import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.DimensionType
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import kotlin.math.max

open class WorldProviderAsteroids : WorldProviderSpace(), ISolarLevel {
    //Used to list asteroid centres to external code that needs to know them
    private val asteroids = HashSet<AsteroidData>()
    private var dataNotLoaded = true
    private var datafile: AsteroidSaveData? = null
    private var solarMultiplier = -1.0

    //	@Override
    //	public void registerWorldChunkManager()
    //	{
    //		this.worldChunkMgr = new WorldChunkManagerAsteroids(this.world, 0F);
    //	}
    override fun getCelestialBody(): CelestialBody? {
        return AsteroidsModule.planetAsteroids
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
        return ChunkProviderAsteroids::class.java
    }

    override fun calculateCelestialAngle(par1: Long, par3: Float): Float {
        return 0.25f
    }

    @SideOnly(Side.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        return 1.0f
    }

    //	@Override
    //	public IChunkProvider createChunkGenerator()
    //	{
    //		return new ChunkProviderAsteroids(this.world, this.world.getSeed(), this.world.getWorldInfo().isMapFeaturesEnabled());
    //	}
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

    override fun getGravity(): Float {
        return 0.072f
    }

    override fun getMeteorFrequency(): Double {
        return 10.0
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.9
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier >= 3
    }

    override fun getFallDamageModifier(): Float {
        return 0.1f
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
        this.datafile = this.world.loadData(
            AsteroidSaveData::class.java,
            AsteroidSaveData.Companion.saveDataID
        ) as AsteroidSaveData?

        if (this.datafile == null) {
            this.datafile = AsteroidSaveData("")
            this.world.setData(AsteroidSaveData.Companion.saveDataID, this.datafile)
            this.writeToNBT(this.datafile!!.datacompound)
        } else {
            this.readFromNBT(this.datafile!!.datacompound)
        }

        this.dataNotLoaded = false
    }

    private fun readFromNBT(nbt: NBTTagCompound) {
        val coordList: NBTTagList = nbt.getTagList("coords", 10)
        if (coordList.tagCount() > 0) {
            for (j: Int in 0..<coordList.tagCount()) {
                val tag1: NBTTagCompound = coordList.getCompoundTagAt(j)

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
            coordList.appendTag(tag)
        }
        nbt.setTag("coords", coordList)
        this.datafile!!.markDirty()
    }

    private fun addToNBT(nbt: NBTTagCompound, coords: AsteroidData) {
        val coordList: NBTTagList = nbt.getTagList("coords", 10)
        val tag: NBTTagCompound = NBTTagCompound()
        coords.writeToNBT(tag)
        coordList.appendTag(tag)
        nbt.setTag("coords", coordList)
        this.datafile!!.markDirty()
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
        val offset: Int = EntityAstroMiner.Companion.MINE_LENGTH_AST / 2
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
        if (this.solarMultiplier < 0.0) {
            val s: Double = this.getSolarSize().toDouble()
            this.solarMultiplier = s * s * s * ConfigManagerCore.spaceStationEnergyScalar
        }
        return this.solarMultiplier
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
            tag.setInteger("x", this.centre!!.x)
            tag.setInteger("y", this.centre!!.y)
            tag.setInteger("z", this.centre!!.z)
            tag.setInteger("coreAndFlag", this.coreAndSpawnedFlag)
            tag.setInteger("sizeAndFlag", this.sizeAndLandedFlag)
            return tag
        }

        companion object {
            @JvmStatic
            fun readFromNBT(tag: NBTTagCompound): AsteroidData {
                val tempVector: BlockVec3 = BlockVec3()
                tempVector.x = tag.getInteger("x")
                tempVector.y = tag.getInteger("y")
                tempVector.z = tag.getInteger("z")

                val roid: AsteroidData = AsteroidData(tempVector)
                if (tag.hasKey("coreAndFlag")) {
                    roid.coreAndSpawnedFlag = tag.getInteger("coreAndFlag")
                }
                if (tag.hasKey("sizeAndFlag")) {
                    roid.sizeAndLandedFlag = tag.getInteger("sizeAndFlag")
                }

                return roid
            }
        }
    }

    override fun getDungeonSpacing(): Int {
        return 576
        //Used for generating Abandoned Base 
    }

    override fun getDimensionType(): DimensionType {
        return (GCPlanetDimensions.ASTEROIDS)!!
    }

    override fun getArrowGravity(): Float {
        return 0.002f
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasure.MOONCHEST
    }

    override fun hasSkyLight(): Boolean {
        return false
    }

    override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }
}
