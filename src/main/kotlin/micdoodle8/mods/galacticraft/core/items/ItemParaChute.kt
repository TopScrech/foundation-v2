package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemParaChute constructor(assetName: String?) : Item(), ISortableItem, IClickableItem {
    //    protected IIcon[] icons;
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(1)
        this.setTranslationKey(assetName)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        int i = 0;
        this.icons = new IIcon[ItemParaChute.names.length];

        for (String name : ItemParaChute.names)
        {
            this.icons[i++] = iconRegister.registerIcon(this.getIconString() + "_" + name);
        }
    }*/
    public override fun getTranslationKey(itemStack: ItemStack): String {
        return this.getTranslationKey() + "_" + names[itemStack.getItemDamage()]
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GEAR
    }

    override fun onItemRightClick(worldIn: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        var itemStack: ItemStack = player.getHeldItem(hand)

        if (player is EntityPlayerMP) {
            if (itemStack.getItem() is IClickableItem) {
                itemStack = ((itemStack.getItem() as IClickableItem).onItemRightClick(itemStack, worldIn, player))!!
            }

            if (itemStack.isEmpty()) {
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
            }
        }
        return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
    }

    override fun onItemRightClick(itemStack: ItemStack?, worldIn: World?, player: EntityPlayer?): ItemStack? {
        var itemStack: ItemStack = (itemStack)!!
        val stats: GCPlayerStats = (GCPlayerStats.get((player)!!))!!
        val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(4)

        if (gear.isEmpty()) {
            (stats.getExtendedInventory())!!.setInventorySlotContents(4, itemStack.copy())
            itemStack = ItemStack.EMPTY
        }

        return itemStack
    }

    companion object {
        @JvmField
        val names: Array<String?> = arrayOf<String?>(
            "plain",  // 0
            "black",  // 1
            "blue",  // 2
            "lime",  // 3
            "brown",  // 4
            "darkblue",  // 5
            "darkgray",  // 6
            "darkgreen",  // 7
            "gray",  // 8
            "magenta",  // 9
            "orange",  // 10
            "pink",  // 11
            "purple",  // 12
            "red",  // 13
            "teal",  // 14
            "yellow"
        ) // 15

        /*@Override
    public IIcon getIconFromDamage(int damage)
    {
        if (this.icons.length > damage)
        {
            return this.icons[damage];
        }

        return super.getIconFromDamage(damage);
    }*/
        // @Override
        // @SideOnly(Side.CLIENT)
        // public int getIconFromDamage(int par1)
        // {
        // switch (par1)
        // {
        // case 0: // plain
        // return 49;
        // case 1: // black
        // return 34;
        // case 2: // blue
        // return 46;
        // case 3: // green
        // return 44;
        // case 4: // brown
        // return 37;
        // case 5: // dark blue
        // return 38;
        // case 6: // dark gray
        // return 42;
        // case 7: // dark green
        // return 36;
        // case 8: // gray
        // return 41;
        // case 9: // magenta
        // return 47;
        // case 10: // orange
        // return 48;
        // case 11: // pink
        // return 43;
        // case 12: // purple
        // return 39;
        // case 13: // red
        // return 35;
        // case 14: // teal
        // return 40;
        // case 15: // yellow
        // return 45;
        // }
        //
        // return 0;
        // }
        @JvmStatic
        fun getDyeEnumFromParachuteDamage(damage: Int): EnumDyeColor {
            when (damage) {
                1 -> return EnumDyeColor.BLACK
                13 -> return EnumDyeColor.RED
                7 -> return EnumDyeColor.GREEN
                4 -> return EnumDyeColor.BROWN
                5 -> return EnumDyeColor.BLUE
                12 -> return EnumDyeColor.PURPLE
                14 -> return EnumDyeColor.CYAN
                8 -> return EnumDyeColor.SILVER
                6 -> return EnumDyeColor.GRAY
                11 -> return EnumDyeColor.PINK
                3 -> return EnumDyeColor.LIME
                15 -> return EnumDyeColor.YELLOW
                2 -> return EnumDyeColor.LIGHT_BLUE
                9 -> return EnumDyeColor.MAGENTA
                10 -> return EnumDyeColor.ORANGE
                0 -> return EnumDyeColor.WHITE
            }

            return EnumDyeColor.WHITE
        }

        @JvmStatic
        fun getParachuteDamageValueFromDyeEnum(color: EnumDyeColor): Int {
            when (color) {
                EnumDyeColor.BLACK -> return 1
                EnumDyeColor.RED -> return 13
                EnumDyeColor.GREEN -> return 7
                EnumDyeColor.BROWN -> return 4
                EnumDyeColor.BLUE -> return 5
                EnumDyeColor.PURPLE -> return 12
                EnumDyeColor.CYAN -> return 14
                EnumDyeColor.SILVER -> return 8
                EnumDyeColor.GRAY -> return 6
                EnumDyeColor.PINK -> return 11
                EnumDyeColor.LIME -> return 3
                EnumDyeColor.YELLOW -> return 15
                EnumDyeColor.LIGHT_BLUE -> return 2
                EnumDyeColor.MAGENTA -> return 9
                EnumDyeColor.ORANGE -> return 10
                EnumDyeColor.WHITE -> return 0
            }

            return -1
        }

        @JvmStatic
        fun getParachuteDamageValueFromDye(meta: Int): Int {
            return getParachuteDamageValueFromDyeEnum(EnumDyeColor.byDyeDamage(meta))
        }
    }
}
