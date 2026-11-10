package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemCanisterLiquidNitrogen constructor(assetName: String?) : ItemCanisterGeneric(assetName),
    ISortableItem {
    //    protected IIcon[] icons = new IIcon[7];
    init {
        this.setAllowedFluid("liquidnitrogen")
        //this.setTextureName(GalacticraftPlanets.TEXTURE_PREFIX + assetName);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < this.icons.length; i++)
        {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }*/
    public override fun getTranslationKey(itemStack: ItemStack): String {
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() == 0) {
            return "item.empty_gas_canister"
        }

        if (itemStack.getItemDamage() == 1) {
            return "item.canister.liquid_nitrogen.full"
        }

        return "item.canister.liquid_nitrogen.partial"
    }

    /*@Override
    public IIcon getIconFromDamage(int par1)
    {
        final int damage = 6 * par1 / this.getMaxDamage();

        if (this.icons.length > damage)
        {
            return this.icons[this.icons.length - damage - 1];
        }

        return super.getIconFromDamage(damage);
    }*/
    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() > 0) {
            tooltip.add(GCCoreUtil.translate("item.canister.liquid_nitrogen.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()))
        }
    }

    private fun canFreeze(b: Block): Block? {
        if (b === Blocks.WATER) {
            return Blocks.ICE
        }
        if (b === Blocks.LAVA) {
            return Blocks.OBSIDIAN
        }
        return null
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemStack: ItemStack = playerIn.getHeldItem(hand)

        val damage: Int = itemStack.getItemDamage() + 125
        if (damage > itemStack.getMaxDamage()) {
            return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
        }

        val movingobjectposition: RayTraceResult = this.rayTrace(worldIn, playerIn, true)

        if (movingobjectposition == null || movingobjectposition.typeOfHit == RayTraceResult.Type.MISS) {
            return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
        } else {
            if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
                val pos: BlockPos = movingobjectposition.getBlockPos()

                if (!worldIn.canMineBlockBody(playerIn, pos)) {
                    return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
                }

                if (!playerIn.canPlayerEdit(pos, movingobjectposition.sideHit, itemStack)) {
                    return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
                }

                //Material material = par2World.getBlock(i, j, k).getMaterial();
                val state: IBlockState = worldIn.getBlockState(pos)
                val b: Block = state.getBlock()
                val meta: Int = b.getMetaFromState(state)

                val result: Block? = this.canFreeze(b)
                if (result != null) {
                    this.setNewDamage(itemStack, damage)
                    worldIn.playSound(
                        null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        SoundEvents.ITEM_FLINTANDSTEEL_USE,
                        SoundCategory.NEUTRAL,
                        1.0f,
                        itemRand.nextFloat() * 0.4f + 0.8f
                    )
                    worldIn.setBlockState(pos, result.getDefaultState(), 3)
                    return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
                }
            }

            return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.CANISTER
    }
}
