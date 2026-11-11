package micdoodle8.mods.galacticraft.planets.asteroids.items

import foundation.item.legacyItemValue

import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemCanisterLiquidNitrogen constructor(assetName: String?) : ItemCanisterGeneric(assetName),
    ISortableItem {
    //    protected IIcon[] icons = new IIcon[7];
    init {
        this.setAllowedFluid("liquidnitrogen")
        //this.setTextureName(GalacticraftPlanets.TEXTURE_PREFIX + assetName);
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < this.icons.length; i++)
        {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }*/
    public override fun getTranslationKey(itemStack: ItemStack): String {
        if (itemStack.getMaxDamage() - itemStack.legacyItemValue == 0) {
            return "item.empty_gas_canister"
        }

        if (itemStack.legacyItemValue == 1) {
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
    @OnlyIn(Dist.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.legacyItemValue > 0) {
            tooltip.add(GCCoreUtil.translate("item.canister.liquid_nitrogen.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.legacyItemValue))
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
        val itemStack: ItemStack = playerIn.getItemInHand(hand)

        val damage: Int = itemStack.legacyItemValue + 125
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
                    worldIn.setBlock(pos, result.defaultBlockState(), 3)
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
