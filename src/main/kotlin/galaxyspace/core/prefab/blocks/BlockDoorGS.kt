package galaxyspace.core.prefab.blocks

import net.minecraft.block.BlockDoor
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import java.util.Random

open class BlockDoorGS constructor(name: String) : BlockDoor(Material.WOOD) {
    private var item: Item? = null

    init {
        this.setHardness(3.0f)
        this.setSoundType(SoundType.WOOD)
        this.setTranslationKey(name)
        this.setDefaultState(
            this.defaultBlockState().setValue(FACING, EnumFacing.NORTH)
                .setValue(OPEN, false)
                .setValue(HINGE, EnumHingePosition.LEFT)
                .setValue(POWERED, false)
                .setValue(HALF, EnumDoorHalf.LOWER)
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
