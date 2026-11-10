package micdoodle8.mods.galacticraft.core.dimension

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderOrbit
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/***
 * Properties of a WorldProviderSpaceStation
 * 1.  Spinnable with Spin Thrusters (if you don't want spin, create your own SpinManager subclass which does nothing)
 * (note: your SkyProvider needs to rotate according to setSpinDeltaPerTick()
 * 2.  Oregen from other mods is inhibited in this dimension
 * 3.  AstroMiner placement is inhibited in this dimension
 * 4.  The player on arrival into this dimension (after rocket flight) will be in 1st person view
 *
 */
abstract class WorldProviderSpaceStation : WorldProviderSpace() {
    private val spinManager = SpinManager(this)

    fun getSpinManager(): SpinManager {
        return spinManager
    }

    override fun setDimension(var1: Int) {
        super.setDimension(var1)
    }

    /**
     * Called only once from WorldProvider.registerWorld()
     * so this provides a handy initialisation method
     */
    override fun init() {
        super.init()
        this.getSpinManager().registerServerSide()
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderOrbit::class.java
    }

    override fun updateWeather() {
        super.updateWeather()
        spinManager.updateSpin()
    }

    @SideOnly(Side.CLIENT)
    abstract fun setSpinDeltaPerTick(angle: Float)

    @SideOnly(Side.CLIENT)
    abstract fun getSkyRotation(): Float

    @SideOnly(Side.CLIENT)
    abstract fun createSkyProvider()
}
