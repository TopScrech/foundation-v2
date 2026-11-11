package galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines

import asmodeuscore.api.item.IShiftDescription
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockSingleSolarPanel
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World

open class BlockSingleSolarPanel constructor(name: String, tier: Int) : Block(Material.IRON), IShiftDescription,
    ISortableBlock {
    private val tier: Int

    init {
        this.setTranslationKey(name)
        this.setHardness(2.0f)
        this.setSoundType(SoundType.METAL)
        this.tier = tier
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB_BOTTOM
    }

    override fun getLightOpacity(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        return 1
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isTopSolid(state: IBlockState): Boolean {
        return true
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".desc")
    }

    override fun getDescription(meta: Int): String? {
        return null
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    open fun getTier(): Int {
        return this.tier
    }

    companion object {
        @JvmField
        protected val AABB_BOTTOM: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0)
    }
}
