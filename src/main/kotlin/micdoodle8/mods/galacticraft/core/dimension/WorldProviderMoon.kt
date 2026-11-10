package micdoodle8.mods.galacticraft.core.dimension

import java.util.LinkedList

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderMoon
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

open class WorldProviderMoon : WorldProviderSpace(), IGalacticraftWorldProvider, ISolarLevel {
    override fun getDimensionType(): DimensionType {
        return (GCDimensions.MOON)!!
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
        return 192000L
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderMoon::class.java
    }

    @SideOnly(Side.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val var2: Float = this.world.getCelestialAngle(par1)
        var var3: Float = 1.0f - (MathHelper.cos(var2 * Constants.twoPI) * 2.0f + 0.25f)

        if (var3 < 0.0f) {
            var3 = 0.0f
        }

        if (var3 > 1.0f) {
            var3 = 1.0f
        }

        return var3 * var3 * 0.5f + 0.3f
    }

    override fun isSkyColored(): Boolean {
        return false
    }

    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getAverageGroundLevel(): Int {
        return 68
    }

    override fun canCoordinateBeSpawn(var1: Int, var2: Int): Boolean {
        return true
    }

    //Overriding  so that beds do not explode on Moon
    public override fun canRespawnHere(): Boolean {
        if (EventHandlerGC.bedActivated) {
            EventHandlerGC.bedActivated = false
            return true
        }
        return false
    }

    override fun getGravity(): Float {
        return 0.062f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.7
    }

    override fun getSolarEnergyMultiplier(): Double {
        return 1.4
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier > 0
    }

    override fun getFallDamageModifier(): Float {
        return 0.18f
    }

    override fun getCelestialBody(): CelestialBody? {
        return GalacticraftCore.moonMoon
    }

    override fun getDungeonSpacing(): Int {
        return 704
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasure.MOONCHEST
    }

    override fun getSurfaceBlocks(): MutableList<Block?> {
        val list: MutableList<Block?> = LinkedList<Block?>()
        list.add(GCBlocks.blockMoon)
        return list
    }
}
