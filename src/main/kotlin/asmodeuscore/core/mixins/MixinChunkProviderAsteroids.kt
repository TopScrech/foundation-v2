package asmodeuscore.core.mixins

import asmodeuscore.core.configs.AsmodeusConfig
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.SpecialAsteroidBlock
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.SpecialAsteroidBlockHandler
import net.minecraft.world.level.block.Block
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.level.Level as World
import net.minecraftforge.registries.ForgeRegistries
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin

@Mixin(value = [ChunkProviderAsteroids::class], remap = false)
open class MixinChunkProviderAsteroids {
    @Final
    @Shadow
    private val coreHandler: SpecialAsteroidBlockHandler? = null

    @Inject(method = ["<init>"], at = [At("TAIL")])
    open fun init(par1World: World?, par2: Long, par4: Boolean, ci: CallbackInfo?) {
        val provider: ChunkProviderAsteroids = this as Any as ChunkProviderAsteroids

        val coreBlocksFromConfig: Array<String> = ((AsmodeusConfig.Companion.asteroidsCoreBlocks as Array<String>?))!!
        for (coreBlock: String in coreBlocksFromConfig) {
            val dataBlock: Array<String?> = coreBlock.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            //Example: modid:block:meta:probability:thickness
            val blockModId: String? = dataBlock[0]
            val blockName: String? = dataBlock[1]
            val blockMeta: Int = dataBlock[2]!!.toInt()
            val blockProbabiblity: Int = dataBlock[3]!!.toInt()
            val blockThickness: Float = dataBlock[4]!!.toFloat()

            val block: Block? = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(blockModId + ":" + blockName))

            coreHandler!!.addBlock(
                SpecialAsteroidBlock(
                    block,
                    blockMeta.toByte(),
                    blockProbabiblity,
                    blockThickness.toDouble()
                )
            )
        }
    }
}
