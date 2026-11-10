package micdoodle8.mods.galacticraft.planets.mars.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.block.BlockDragonEgg
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockCreeperEgg constructor(assetName: String?) : BlockDragonEgg(), IShiftDescription, ISortableBlock {
    init {
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return DRAGON_EGG_AABB
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        return false
    }

    override fun onBlockClicked(worldIn: World, pos: BlockPos, playerIn: EntityPlayer) {
    }

    @SideOnly(Side.CLIENT)
    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack.EMPTY
    }

    override fun onBlockExploded(world: World, pos: BlockPos, explosion: Explosion) {
        if (!world.isRemote) {
            val creeper: EntityEvolvedCreeper = EntityEvolvedCreeper(world)
            creeper.setPosition(pos.getX() + 0.5, (pos.getY() + 3).toDouble(), pos.getZ() + 0.5)
            creeper.setChild(true)
            world.spawnEntity(creeper)
        }

        world.setBlockToAir(pos)
        this.onExplosionDestroy(world, pos, explosion)
    }

    override fun canDropFromExplosion(explose: Explosion): Boolean {
        return false
    }

    //Can only be harvested with a Sticky Desh Pickaxe
    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        val stack: ItemStack = player.inventory.getCurrentItem()
        if (stack.isEmpty()) {
            return player.canHarvestBlock(world.getBlockState(pos))
        }
        return stack.getItem() === MarsItems.deshPickSlime
    }

    override fun getPlayerRelativeBlockHardness(
        state: IBlockState,
        player: EntityPlayer,
        worldIn: World,
        pos: BlockPos
    ): Float {
        val stack: ItemStack = player.inventory.getCurrentItem()
        if (stack != null && stack.getItem() === MarsItems.deshPickSlime) {
            return 0.2f
        }
        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.EGG
    }

    companion object {
        @JvmField
        protected val DRAGON_EGG_AABB: AxisAlignedBB = AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375)
    }
}
