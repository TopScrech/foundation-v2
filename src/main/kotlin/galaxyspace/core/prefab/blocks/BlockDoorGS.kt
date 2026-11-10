package galaxyspace.core.prefab.blocks

import net.minecraft.block.BlockDoor
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import java.util.Random

open class BlockDoorGS constructor(name: String) : BlockDoor(Material.WOOD) {
    private var item: Item? = null

    init {
        this.setHardness(3.0f)
        this.setSoundType(SoundType.WOOD)
        this.setTranslationKey(name)
        this.setDefaultState(
            this.getDefaultState().withProperty(FACING, EnumFacing.NORTH)
                .withProperty(OPEN, false)
                .withProperty(HINGE, EnumHingePosition.LEFT)
                .withProperty(POWERED, false)
                .withProperty(HALF, EnumDoorHalf.LOWER)
        )
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(item)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        if (state.getValue(HALF) != EnumDoorHalf.UPPER) {
            return item!!
        }
        return Items.AIR
    }

    open fun setDoorItem(item: Item) {
        this.item = item
    }
}
