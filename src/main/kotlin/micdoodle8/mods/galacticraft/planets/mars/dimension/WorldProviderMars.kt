package micdoodle8.mods.galacticraft.planets.mars.dimension

import java.util.LinkedList

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.planets.GCPlanetDimensions
import micdoodle8.mods.galacticraft.planets.mars.MarsModule
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.world.gen.ChunkProviderMars
import micdoodle8.mods.galacticraft.planets.mars.world.gen.RoomTreasureMars
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

open class WorldProviderMars : WorldProviderSpace(), IGalacticraftWorldProvider, ISolarLevel {
    private var solarMultiplier = -1.0

    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((210f / 255f * f).toDouble(), (120f / 255f * f).toDouble(), (59f / 255f * f).toDouble())
    }

    override fun getSkyColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((154 / 255.0f * f).toDouble(), (114 / 255.0f * f).toDouble(), (66 / 255.0f * f).toDouble())
    }

    override fun hasSunset(): Boolean {
        return false
    }

    override fun getDayLength(): Long {
        return 24660L
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderMars::class.java
    }

    @SideOnly(Side.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val f1: Float = this.world.getCelestialAngle(par1)
        var f2: Float = 1.0f - (MathHelper.cos(f1 * Constants.twoPI) * 2.0f + 0.25f)

        if (f2 < 0.0f) {
            f2 = 0.0f
        }

        if (f2 > 1.0f) {
            f2 = 1.0f
        }

        return f2 * f2 * 0.75f
    }

    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getAverageGroundLevel(): Int {
        return 76
    }

    override fun canCoordinateBeSpawn(var1: Int, var2: Int): Boolean {
        return true
    }

    //Overriding so that beds do not explode on Mars
    public override fun canRespawnHere(): Boolean {
        if (EventHandlerGC.bedActivated) {
            EventHandlerGC.bedActivated = false
            return true
        }
        return false
    }

    override fun getGravity(): Float {
        return 0.058f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.9
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier >= 2
    }

    override fun getFallDamageModifier(): Float {
        return 0.38f
    }

    override fun getCelestialBody(): CelestialBody? {
        return MarsModule.planetMars
    }

    override fun getSolarEnergyMultiplier(): Double {
        if (this.solarMultiplier < 0.0) {
            val s: Double = this.getSolarSize().toDouble()
            this.solarMultiplier = s * s * s
        }
        return this.solarMultiplier
    }

    override fun getDungeonSpacing(): Int {
        return 704
    }

    override fun getDimensionType(): DimensionType {
        return (GCPlanetDimensions.MARS)!!
    }

    override fun getArrowGravity(): Float {
        return 0.015f
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasureMars.MARSCHEST
    }

    override fun getSurfaceBlocks(): MutableList<Block?> {
        val list: MutableList<Block?> = LinkedList<Block?>()
        list.add(MarsBlocks.marsBlock)
        return list
    }
}
