package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed.EnumEnclosedBlockType

import appeng.api.AEApi
import appeng.api.util.AEColor
import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.reflect.Method

open class ItemBlockEnclosed constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        var name: String

        try {
            name = BlockEnclosed.EnumEnclosedBlockType.byMetadata(par1ItemStack.getItemDamage()).getName()
            name = name.substring(9, name.length) // Remove "enclosed_"
        } catch (e: Exception) {
            name = "null"
        }

        return this.getBlock().getTranslationKey() + "." + name
    }

    override fun onItemUse(
        playerIn: EntityPlayer,
        worldIn: World,
        pos: BlockPos,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): EnumActionResult {
        var pos: BlockPos = pos
        val itemstack: ItemStack = playerIn.getHeldItem(hand)
        val metadata: Int = this.getMetadata(itemstack.getItemDamage())
        if (metadata == BlockEnclosed.EnumEnclosedBlockType.ME_CABLE.getMeta() && CompatibilityManager.isAppEngLoaded()) {
            val iblockstate: IBlockState = worldIn.getBlockState(pos)
            val block: Block = iblockstate.getBlock()
            val origPos: BlockPos = pos

            if (!block.isReplaceable(worldIn, pos)) {
                pos = pos.offset(side)
            }

            if (itemstack.getCount() == 0) {
                return EnumActionResult.FAIL
            } else if (!playerIn.canPlayerEdit(pos, side, itemstack)) {
                return EnumActionResult.FAIL
            } else if (worldIn.mayPlace(this.block, pos, false, side, null)) {
                val i: Int = this.getMetadata(itemstack.getMetadata())
                val iblockstate1: IBlockState =
                    this.block.getStateForPlacement(worldIn, pos, side, hitX, hitY, hitZ, i, playerIn)

                if (placeBlockAt(itemstack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, iblockstate1)) {
                    val soundType: SoundType = this.getBlock().getSoundType(iblockstate, worldIn, pos, playerIn)
                    worldIn.playSound(
                        playerIn,
                        (pos.getX() + 0.5f).toDouble(),
                        (pos.getY() + 0.5f).toDouble(),
                        (pos.getZ() + 0.5f).toDouble(),
                        soundType.getPlaceSound(),
                        SoundCategory.BLOCKS,
                        (soundType.getVolume() + 1.0f) / 2.0f,
                        soundType.getPitch() * 0.8f
                    )
                    itemstack.shrink(1)

                    val itemME: ItemStack =
                        AEApi.instance().definitions().parts().cableGlass().stack(AEColor.TRANSPARENT, 1)
                    itemME.setCount(2) //Fool AppEng into not destroying anything in the player inventory
                    AEApi.instance().partHelper().placeBus(itemME, origPos, side, playerIn, hand, worldIn)
                    //Emulate appeng.parts.PartPlacement.place( is, pos, side, player, w, PartPlacement.PlaceType.INTERACT_SECOND_PASS, 0 );
                    try {
                        val clazzpp: Class<*> = Class.forName("appeng.parts.PartPlacement")
                        val enumPlaceType: Class<*> = Class.forName("appeng.parts.PartPlacement\$PlaceType")
                        val methPl: Method = clazzpp.getMethod(
                            "place",
                            ItemStack::class.java,
                            BlockPos::class.java,
                            EnumFacing::class.java,
                            EntityPlayer::class.java,
                            EnumHand::class.java,
                            World::class.java,
                            enumPlaceType,
                            Int::class.javaPrimitiveType
                        )
                        methPl.invoke(
                            null,
                            itemME,
                            origPos,
                            side,
                            playerIn,
                            hand,
                            worldIn,
                            enumPlaceType.getEnumConstants()[2],
                            0
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return EnumActionResult.SUCCESS
            } else {
                return EnumActionResult.FAIL
            }
        } else {
            return super.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
