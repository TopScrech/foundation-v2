package asmodeuscore.core.utils.worldengine

import asmodeuscore.core.utils.worldengine.WE_WorldProvider
import net.minecraft.world.WorldProvider
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator

abstract class WE_WorldProvider : WorldProvider() {
    @JvmField
    var rainfall: Float = 0.1f

    @JvmField
    var chunk_provider: WE_ChunkProvider? = null

    public override fun init() {
        println("////////////////////////////////////-")
        println("//#===============================//=* Version: 1.12.2-1.0.0")
        println("//#=-------| WorldEngine |-------=//=* By Vamig Aliev (vk.com/win_vista).")
        println("//#===============================//=* Port by BlesseNtumble.")
        println("////////////////////////////////////-")
    }

    override fun createChunkGenerator(): IChunkGenerator {
        println("WorldEngine: -Starting WorldEngine...")
        this.chunk_provider = WE_ChunkProvider(this)
        println("WorldEngine: -WorldEngine started successfully!")
        return this.chunk_provider!!
    }

    abstract fun genSettings(var1: WE_ChunkProvider?)

    abstract fun onChunkProvider(var1: Int, var2: Int, var3: ChunkPrimer?)

    abstract fun onPopulate(var1: Int, var2: Int)

    abstract fun recreateStructures(var1: Chunk?, var2: Int, var3: Int)

    open fun isColorWorld(): Boolean {
        return false
    }
}
