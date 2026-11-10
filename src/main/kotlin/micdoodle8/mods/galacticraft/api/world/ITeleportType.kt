package micdoodle8.mods.galacticraft.api.world

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import java.util.Random

/**
 * Implement into WorldProvider for customizing spawning players and other
 * entities into space dimension
 *
 *
 * You can also create a separate class, implement it there, then register it in @GalacticraftRegistry
 */
interface ITeleportType {
    /**
     * This method is used to determine if a player will open parachute upon
     * entering the dimension
     *
     * @return whether player will set parachute open upon entering this
     * dimension
     */
    fun useParachute(): Boolean

    /**
     * Gets the player spawn location when entering this dimension
     *
     * @param world  The world to be spawned into
     * @param player The player to be teleported
     * @return a vector3 object containing the coordinates to be spawned into
     * the world with
     */
    fun getPlayerSpawnLocation(world: WorldServer?, player: EntityPlayerMP?): Vector3?

    /**
     * Gets the entity (non-player) spawn location when entering this dimension
     *
     * @param world  The world to be spawned into
     * @param entity the non-player entity to be teleported
     * @return a vector3 object containing the coordinates to be spawned into
     * the world with
     */
    fun getEntitySpawnLocation(world: WorldServer?, entity: Entity?): Vector3?

    /**
     * Gets the parachest spawn location when entering this dimension. Return
     * null for no parachest spawn
     *
     * @param world The world to be spawned into
     * @param player The player being teleported
     * @param rand The random instance
     * @return a vector3 object containing the coordinates to be spawned into
     * the world with. Return null for no spawn
     */
    fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3?

    /**
     * Called when player is transferred to a space dimension
     *
     * @param newWorld         The world object of the entered world
     * @param player           The player that has transferred dimensions
     * @param ridingAutoRocket If the player is riding an auto rocket. Do not spawn in
     * landers if so.
     */
    fun onSpaceDimensionChanged(newWorld: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean)


    /**
     * Used by Asteroids Survival game mode to set up the initial lander inventory
     *
     * @param player
     */
    fun setupAdventureSpawn(player: EntityPlayerMP?)
}
