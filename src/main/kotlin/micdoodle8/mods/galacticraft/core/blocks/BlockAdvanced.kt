package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.ItemUniversalWrench
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * An advanced block class that is to be extended for wrenching capabilities.
 */
abstract class BlockAdvanced(material: Material) : Block(material) {
    init {
        this.setHardness(0.6f)
        this.setResistance(2.5f)
        //A default blast resistance for GC machines and tiles, similar to a bookshelf
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
        if (hand != EnumHand.MAIN_HAND) {
            return false
        }

        val heldItem: ItemStack = playerIn.getHeldItem(hand)

        if (this.useWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
            return true
        }

        if (playerIn.isSneaking()) {
            if (this.onSneakMachineActivated(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
                return true
            }
        }

        return this.onMachineActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
    }

    protected fun useWrench(
        worldIn: World?,
        pos: BlockPos?,
        playerIn: EntityPlayer,
        hand: EnumHand,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        /**
         * Check if the player is holding a wrench or an electric item. If so,
         * call the wrench event.
         */
        if (this.isUsableWrench(playerIn, heldItem, pos)) {
            this.damageWrench(playerIn, heldItem, pos)

            if (playerIn.isSneaking()) {
                if (this.onSneakUseWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
                    playerIn.swingArm(hand)
                    return true
                }
            }

            if (this.onUseWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
                playerIn.swingArm(hand)
                return true
            }
        }

        return false
    }

    fun isUsableWrench(entityPlayer: EntityPlayer?, itemStack: ItemStack?, pos: BlockPos?): Boolean {
        return entityPlayer != null && itemStack != null && itemStack.getItem() === GCItems.wrench
    }

    fun damageWrench(entityPlayer: EntityPlayer, itemStack: ItemStack?, pos: BlockPos?): Boolean {
        if (this.isUsableWrench(entityPlayer, itemStack, pos)) {
            (GCItems.wrench as ItemUniversalWrench).wrenchUsed(entityPlayer, pos)
            return true
        }

        return false
    }

    /**
     * Called when the machine is right clicked by the player
     *
     * @return True if something happens
     */
    open fun onMachineActivated(
        worldIn: World?,
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
        return false
    }

    /**
     * Called when the machine is being wrenched by a player while sneaking.
     *
     * @return True if something happens
     */
    open fun onSneakMachineActivated(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        return false
    }

    /**
     * Called when a player uses a wrench on the machine
     *
     * @return True if some happens
     */
    open fun onUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        return false
    }

    /**
     * Called when a player uses a wrench on the machine while sneaking. Only
     * works with the UE wrench.
     *
     * @return True if some happens
     */
    open fun onSneakUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        return this.onUseWrench(world, pos, entityPlayer, hand, heldItem, side, hitX, hitY, hitZ)
    }

    fun rotate6Ways(world: World, pos: BlockPos) {
        val metadata: Int = this.getMetaFromState(world.getBlockState(pos))
        var metaDir: Int = ((metadata and 7) + 1) % 6
        //DOWN->UP->NORTH->*EAST*->*SOUTH*->WEST
        //0->1 1->2 2->5 3->4 4->0 5->3 
        if (metaDir == 3)  //after north
        {
            metaDir = 5
        } else if (metaDir == 0) {
            metaDir = 3
        } else if (metaDir == 5) {
            metaDir = 0
        }

        world.setBlockState(pos, this.getStateFromMeta(metaDir), 3)
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return this.isNormalCube(base_state, world, pos)
    }
}
