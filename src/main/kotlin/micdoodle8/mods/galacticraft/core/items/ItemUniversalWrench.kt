package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemUniversalWrench constructor(assetName: String?) : Item(), ISortableItem {
    init {
        this.setTranslationKey(assetName)
        this.setMaxStackSize(1)
        this.setMaxDamage(256)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.Companion.galacticraftItem)!!
    }

    open fun wrenchUsed(entityPlayer: EntityPlayer, pos: BlockPos?) {
        val stack: ItemStack = entityPlayer.inventory.getCurrentItem()

        if (!stack.isEmpty()) {
            stack.damageItem(1, entityPlayer)

            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                stack.shrink(1)
            }

            if (stack.getCount() <= 0) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, ItemStack.EMPTY)
            }
        }
    }

    override fun onItemUse(
        playerIn: EntityPlayer,
        worldIn: World,
        pos: BlockPos,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): EnumActionResult {
        return EnumActionResult.PASS
    }

    override fun doesSneakBypassUse(
        stack: ItemStack,
        world: IBlockAccess,
        pos: BlockPos,
        player: EntityPlayer
    ): Boolean {
        return true
    }

    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        if (world.isRemote && player is EntityPlayerSP) {
            ClientProxyCore.Companion.playerClientHandler.onBuild(3, player)
        }
    }

    override fun onItemUseFirst(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        hand: EnumHand
    ): EnumActionResult {
        if (world.isRemote || player.isSneaking()) {
            return EnumActionResult.PASS
        }
        val state: IBlockState = world.getBlockState(pos)
        val blockID: Block = state.getBlock()

        if (blockID === Blocks.FURNACE || blockID === Blocks.LIT_FURNACE || blockID === Blocks.DISPENSER || blockID === Blocks.DROPPER) {
            val metadata: Int = blockID.getMetaFromState(state)

            world.setBlockState(pos, blockID.getStateFromMeta(EnumFacing.byIndex(metadata).rotateY().ordinal), 3)
            this.wrenchUsed(player, pos)

            return EnumActionResult.SUCCESS
        } else if (blockID === Blocks.HOPPER || blockID === Blocks.PISTON || blockID === Blocks.STICKY_PISTON) {
            val metadata: Int = blockID.getMetaFromState(state)
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
            if (blockID === Blocks.HOPPER && metaDir == 1) {
                metaDir = 2
            }

            world.setBlockState(pos, blockID.getStateFromMeta((metadata and 8) + metaDir), 3)
            this.wrenchUsed(player, pos)

            return EnumActionResult.SUCCESS
        }

        return EnumActionResult.PASS
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }
}
