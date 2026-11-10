package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityMeteorChunk
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
        if (itemstack.getItemDamage() == 1 && !world.isRemote) {
            if (itemstack.hasTagCompound()) {
                var meteorBurnTime: Float = itemstack.getTagCompound()!!.getFloat("MeteorBurnTimeF")

                if (meteorBurnTime >= 0.5f) {
                    meteorBurnTime -= 0.5f
                    itemstack.getTagCompound()!!.setFloat("MeteorBurnTimeF", meteorBurnTime)
                } else {
                    itemstack.setItemDamage(0)
                    itemstack.setTagCompound(null)
                }
            } else {
                itemstack.setTagCompound(NBTTagCompound())
                itemstack.getTagCompound()!!.setFloat("MeteorBurnTimeF", METEOR_BURN_TIME.toFloat())
            }
        }
    }

    override fun onCreated(itemstack: ItemStack, world: World, entityPlayer: EntityPlayer) {
        super.onCreated(itemstack, world, entityPlayer)

        if (itemstack.getItemDamage() == 1) {
            if (!itemstack.hasTagCompound()) {
                itemstack.setTagCompound(NBTTagCompound())
            }

            itemstack.getTagCompound()!!.setFloat("MeteorBurnTimeF", METEOR_BURN_TIME.toFloat())
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 0))
            list.add(ItemStack(this, 1, 1))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        itemstack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (itemstack.getItemDamage() > 0) {
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

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack): String {
        return "item." + names[itemStack.getItemDamage()]
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemStack: ItemStack = player.getHeldItem(hand)

        if (!player.capabilities.isCreativeMode) {
            itemStack.shrink(1)
        }

        world.playSound(
            null,
            player.posX,
            player.posY,
            player.posZ,
            SoundEvents.ENTITY_ARROW_SHOOT,
            SoundCategory.NEUTRAL,
            1.0f,
            0.0001f / (itemRand.nextFloat() * 0.1f)
        )

        if (!world.isRemote) {
            val meteor: EntityMeteorChunk = EntityMeteorChunk(world, player, 1.0f)

            if (itemStack.getItemDamage() > 0) {
                meteor.setFire(20)
                meteor.isHot = true
            }
            meteor.canBePickedUp = if (player.capabilities.isCreativeMode) 2 else 1
            world.spawnEntity(meteor)
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
