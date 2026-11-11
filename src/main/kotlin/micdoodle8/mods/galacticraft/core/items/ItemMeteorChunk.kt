package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityMeteorChunk
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemMeteorChunk constructor(assetName: String?) : Item(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.maxStackSize = 16
        this.setCreativeTab(CreativeTabs.MATERIALS)
        this.setTranslationKey(assetName)
        //this.setTextureName("arrow");
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun onUpdate(itemstack: ItemStack, world: World, entity: Entity, par4: Int, par5: Boolean) {
        if (itemstack.legacyItemValue == 1 && !world.isClientSide) {
            if (itemstack.hasTagCompound()) {
                var meteorBurnTime: Float = itemstack.getTagCompound()!!.getFloat("MeteorBurnTimeF")

                if (meteorBurnTime >= 0.5f) {
                    meteorBurnTime -= 0.5f
                    itemstack.getTagCompound()!!.putFloat("MeteorBurnTimeF", meteorBurnTime)
                } else {
                    itemstack.setItemDamage(0)
                    itemstack.setTagCompound(null)
                }
            } else {
                itemstack.setTagCompound(NBTTagCompound())
                itemstack.getTagCompound()!!.putFloat("MeteorBurnTimeF", METEOR_BURN_TIME.toFloat())
            }
        }
    }

    override fun onCreated(itemstack: ItemStack, world: World, entityPlayer: EntityPlayer) {
        super.onCreated(itemstack, world, entityPlayer)

        if (itemstack.legacyItemValue == 1) {
            if (!itemstack.hasTagCompound()) {
                itemstack.setTagCompound(NBTTagCompound())
            }

            itemstack.getTagCompound()!!.putFloat("MeteorBurnTimeF", METEOR_BURN_TIME.toFloat())
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 0))
            list.add(ItemStack(this, 1, 1))
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(
        itemstack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (itemstack.legacyItemValue > 0) {
            var burnTime: Float = 0.0f

            if (itemstack.hasTagCompound()) {
                val meteorBurnTime: Float = itemstack.getTagCompound()!!.getFloat("MeteorBurnTimeF")
                burnTime = Math.round(meteorBurnTime / 10.0f) / 2.0f
            } else {
                burnTime = 45.0f
            }

            tooltip.add(GCCoreUtil.translate("item.hot_description.name") + " " + burnTime + GCCoreUtil.translate("gui.seconds"))
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack): String {
        return "item." + names[itemStack.legacyItemValue]
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemStack: ItemStack = player.getItemInHand(hand)

        if (!player.capabilities.isCreativeMode) {
            itemStack.shrink(1)
        }

        world.playSound(
            null,
            player.x,
            player.y,
            player.z,
            SoundEvents.ENTITY_ARROW_SHOOT,
            SoundCategory.NEUTRAL,
            1.0f,
            0.0001f / (itemRand.nextFloat() * 0.1f)
        )

        if (!world.isClientSide) {
            val meteor: EntityMeteorChunk = EntityMeteorChunk(world, player, 1.0f)

            if (itemStack.legacyItemValue > 0) {
                meteor.setFire(20)
                meteor.isHot = true
            }
            meteor.canBePickedUp = if (player.capabilities.isCreativeMode) 2 else 1
            world.addFreshEntity(meteor)
        }

        player.swingArm(hand)

        return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        val names: Array<String> = arrayOf<String>("meteor_chunk", "meteor_chunk_hot")

        @JvmField
        val METEOR_BURN_TIME: Int = 45 * 20
    }
}
