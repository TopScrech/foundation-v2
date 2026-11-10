package micdoodle8.mods.galacticraft.api.world

import micdoodle8.mods.galacticraft.api.entity.IRocketType

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation

interface IGalacticraftWorldProvider {
    /**
     * gets additional gravity to add to players in this dimension. Typical
     * values range from 0.040 to 0.065
     *
     * @return additional gravity for this provider
     */
    fun getGravity(): Float


    /**
     * Gets custom arrow gravity, overriding the vanilla gravity for arrows
     *
     * @return added y-motion per tick for arrows
     */
    fun getArrowGravity(): Float

    /**
     * Determines the rate to spawn meteors in this planet. Lower means MORE
     * meteors.
     *
     *
     * Typical value would be about 7. Return 0 for no meteors.
     *
     * @return
     */
    fun getMeteorFrequency(): Double

    /**
     * Depending on gravity, different fuel depletion rates will occur in
     * spacecraft (less force required to lift)
     *
     * @return multiplier of fuel usage, relative to the earth. Lower gravity =
     * Lower fuel usage (multiplier less than zero)
     */
    fun getFuelUsageMultiplier(): Double

    /**
     * Whether or not the spaceship tier from [IRocketType] can enter this
     * dimension
     *
     * @param tier The tier of the spaceship entering this dimension
     * @return Whether or not the spaceship with given tier can enter this
     * dimension
     */
    fun canSpaceshipTierPass(tier: Int): Boolean

    /**
     * Fall damage will be multiplied by this number while on the planet/moon.
     *
     * @return Fall damage multiplier, returning 1 will be equal to earth.
     */
    fun getFallDamageModifier(): Float

    /**
     *
     * @return true if this world has no type of atmosphere at all - e.g. the Moon
     */
    fun hasNoAtmosphere(): Boolean

    /**
     * Changes volume of sounds on this planet. You should be using higher
     * values for thin atmospheres and high values for dense atmospheres
     *
     * @return Sound reduction divisor. Value of 10 will make sounds ten times
     * more quiet. Value of 0.1 will make sounds 10 times louder. Be
     * careful with the values you choose!
     */
    fun getSoundVolReductionAmount(): Float

    /**
     * Whether or not the atmosphere of this dimension is valid for players to
     * breathe
     *
     * @return True if players can breathe here, False if not.
     */
    fun hasBreathableAtmosphere(): Boolean

    /**
     * If false (the default) then Nether Portals will have no function on this world.
     * Nether Portals can still be constructed, if the player can make fire, they just
     * won't do anything.
     *
     * @return True if Nether Portals should work like on the Overworld.
     */
    fun netherPortalsOperational(): Boolean

    /**
     * Whether or not the atmosphere of this dimension contains the specified gas
     *
     * @return True if the gas is present in the atmosphere, False if not.
     */
    fun isGasPresent(gas: EnumAtmosphericGas?): Boolean

    /**
     * This value will affect player's thermal level, damaging them if it
     * reaches too high or too low.
     *
     * @return Positive integer for hot celestial bodies, negative for cold.
     * Zero for neutral
     */
    fun getThermalLevelModifier(): Float

    /**
     * Amount of wind on this world. Used for flag waving.
     *
     *
     * Earth has a value of 1.0F, Luna (Moon) has a value of 0.0F.
     *
     * @return Flag movement magnitude. Relative to earth's value of 1.0F
     */
    fun getWindLevel(): Float

    /**
     * Factor by which the sun is to be drawn smaller (<1.0) or larger (>1.0) than
     * the sun on the Overworld
     *
     * @return  factor
     */
    fun getSolarSize(): Float

    /**
     * The celestial body object for this dimension
     *
     * @return The Moon or Planet object for this dimension
     * @see micdoodle8.mods.galacticraft.api.galaxies.Planet
     *
     * @see micdoodle8.mods.galacticraft.api.galaxies.Moon
     */
    fun getCelestialBody(): CelestialBody?

    /**
     * Whether rain and snow should be disabled on this planet
     *
     * @return true if precipitation should be disabled. False otherwise.
     */
    fun shouldDisablePrecipitation(): Boolean

    /**
     * Whether or not player's armor should be corroded in this world
     *
     * @return true if armor should be corroded, false if not
     */
    fun shouldCorrodeArmor(): Boolean

    /**
     * The size (in blocks) of the average spacing between dungeons
     * For example, on the Moon it's 704 blocks, meaning one dungeon in each (704 x 704) square in the (x, z) plane
     *
     *
     * If your world has no dungeons you can safely return 0 here.
     */
    fun getDungeonSpacing(): Int

    /**
     * The ChestGenHooks identifier of the dungeon chests to generate in this world
     */
    fun getDungeonChestType(): ResourceLocation?

    fun getSurfaceBlocks(): MutableList<Block?>?
}
