package micdoodle8.mods.galacticraft.api

import java.lang.Exception
import java.lang.reflect.Field

object GalacticraftConfigAccess {
    private var quickMode: Field? = null
    private var hardMode: Field? = null
    private var adventureMode: Field? = null
    private var adventureRecipes: Field? = null
    private var adventureMobDropsAndSpawning: Field? = null
    private var adventureSpawnHandling: Field? = null
    private var adventureAsteroidPopulation: Field? = null

    @JvmStatic
    fun getQuickMode(): Boolean {
        if (quickMode == null) setup()

        try {
            return quickMode!!.getBoolean(null)
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    fun getHardMode(): Boolean {
        if (quickMode == null) setup()

        try {
            return hardMode!!.getBoolean(null)
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    fun getChallengeMode(): Boolean {
        if (quickMode == null) setup()

        try {
            return adventureMode!!.getBoolean(null)
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    fun getChallengeRecipes(): Boolean {
        if (quickMode == null) setup()

        try {
            return adventureRecipes!!.getBoolean(null)
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    fun getChallengeMobDropsAndSpawning(): Boolean {
        if (quickMode == null) setup()

        try {
            return adventureMobDropsAndSpawning!!.getBoolean(null)
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    fun getChallengeSpawnHandling(): Boolean {
        if (quickMode == null) setup()

        try {
            return adventureSpawnHandling!!.getBoolean(null)
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    fun getChallengeAsteroidPopulation(): Boolean {
        if (quickMode == null) setup()

        try {
            return adventureAsteroidPopulation!!.getBoolean(null)
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    private fun setup() {
        try {
            val GCConfig: Class<*> = Class.forName("micdoodle8.mods.galacticraft.core.util.ConfigManagerCore")
            quickMode = GCConfig.getField("quickMode")
            hardMode = GCConfig.getField("hardMode")
            adventureMode = GCConfig.getField("challengeMode")
            adventureRecipes = GCConfig.getField("challengeRecipes")
            adventureMobDropsAndSpawning = GCConfig.getField("challengeMobDropsAndSpawning")
            adventureSpawnHandling = GCConfig.getField("challengeSpawnHandling")
            adventureAsteroidPopulation = GCConfig.getField("challengeAsteroidPopulation")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
