package galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines

import galaxyspace.GalaxySpace
import asmodeuscore.api.item.IShiftDescription
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicFarm
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class BlockHydroponicFarm constructor() : BlockAdvancedTile(GCBlocks.machine), IShiftDescription,
    ISortableBlock {
    init {
        this.setTranslationKey("hydroponic_farm")
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
    }

    override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean {
        if (world.getBlockState(pos.below(3)).getBlock() === this) {
            return false
        }
        return true
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        playerIn: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        (playerIn)!!.openGui(GalaxySpace.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityHydroponicFarm()
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

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isBlockNormalCube(state: IBlockState): Boolean {
        return false
    }
}
