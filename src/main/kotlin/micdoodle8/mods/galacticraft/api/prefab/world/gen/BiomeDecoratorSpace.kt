package micdoodle8.mods.galacticraft.api.prefab.world.gen

import micdoodle8.mods.galacticraft.api.event.wgen.GCCoreEventPopulate
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.common.MinecraftForge
import java.lang.RuntimeException
import java.util.Random

/**
 * Do not include this prefab class in your released mod download.
 */
abstract class BiomeDecoratorSpace {
    @JvmField
    protected var rand: Random? = null

    @JvmField
    protected var posX: Int = 0

    @JvmField
    protected var posZ: Int = 0

    /**
     * Note: the passed X,Z co-ordinates are now block co-ordinates, not chunk co-ordinates
     * @param world
     * @param random
     * @param posX
     * @param posZ
     */
    fun decorate(world: World?, random: Random?, posX: Int, posZ: Int) {
        if (this.getCurrentWorld() != null) {
            throw RuntimeException("Already decorating!!")
        } else {
            this.setCurrentWorld(world)
            this.rand = random
            this.posX = posX
            this.posZ = posZ
            val pos: BlockPos = BlockPos(this.posX, 0, this.posZ)
            GCCoreEventPopulate.Pre(world, this.rand, pos).post()
            this.decorate()
            GCCoreEventPopulate.Post(world, this.rand, pos).post()
            this.setCurrentWorld(null)
            this.rand = null
        }
    }

    protected abstract fun setCurrentWorld(world: World?)

    protected abstract fun getCurrentWorld(): World?

    protected open fun generateOre(amountPerChunk: Int, worldGenerator: WorldGenerator, minY: Int, maxY: Int) {
        val currentWorld: World = checkNotNull(this.getCurrentWorld()) { "No world is being decorated" }
        for (var5: Int in 0..<amountPerChunk) {
            val var6: Int = this.posX + this.rand!!.nextInt(16)
            val var7: Int = this.rand!!.nextInt(maxY - minY) + minY
            val var8: Int = this.posZ + this.rand!!.nextInt(16)
            worldGenerator.generate(currentWorld, this.rand, BlockPos(var6, var7, var8))
        }
    }

    protected abstract fun decorate()
}
