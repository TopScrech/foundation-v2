package micdoodle8.mods.galacticraft.planets.venus.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.EnumParticleTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockScorchedRock constructor(assetName: String?) : Block(Material.ROCK), ISortableBlock {
    init {
        this.blockHardness = 0.9f
        this.blockResistance = 2.5f
        this.setTickRandomly(true)
        this.setTranslationKey(assetName)
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        worldIn.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL,
            pos.getX() + rand.nextDouble(),
            pos.getY() + 1.0,
            pos.getZ() + rand.nextDouble(),
            0.0,
            0.0,
            0.0
        )
    }
}
