package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.HashMap
import kotlin.math.floor
import kotlin.math.min

open class ItemCanisterLiquidOxygen constructor(assetName: String?) : ItemCanisterGeneric(assetName),
    IItemOxygenSupply, ISortableItem {
    init {
        this.setAllowedFluid("liquidoxygen")
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
        if (EMPTY - itemStack.getItemDamage() == 0) {
            return "item.empty_gas_canister"
        }

        if (itemStack.getItemDamage() == 1) {
            return "item.canister.lox.full"
        }

        return "item.canister.lox.partial"
    }

    /*@Override
    public IIcon getIconFromDamage(int par1)
    {
        final int damage = 6 * par1 / ItemCanisterGeneric.EMPTY;

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
        if (EMPTY - par1ItemStack.getItemDamage() > 0) {
            tooltip.add(GCCoreUtil.translate("item.canister.lox.name") + ": " + (EMPTY - par1ItemStack.getItemDamage()))
        }
    }

    override fun getContainerItem(itemstack: ItemStack): ItemStack {
        val saved: Int? = craftingvalues.get(itemstack)
        if (saved != null) {
            if (saved < EMPTY) {
                craftingvalues.remove(itemstack)
                itemstack.setItemDamage(saved)
                return itemstack.copy()
            }
            return ItemStack(this.getContainerItem(), 1, EMPTY)
        }
        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
            return itemstack.copy()
        }
        return super.getContainerItem(itemstack)
    }

    override fun discharge(itemStack: ItemStack?, amount: Int): Int {
        val damage: Int = (itemStack)!!.getItemDamage()
        val used: Int = min((amount * Constants.LOX_GAS_RATIO).toInt(), EMPTY - damage)
        this.setNewDamage((itemStack)!!, damage + used)
        return floor((used / Constants.LOX_GAS_RATIO).toDouble()).toInt()
    }

    override fun getOxygenStored(par1ItemStack: ItemStack?): Int {
        return EMPTY - (par1ItemStack)!!.getItemDamage()
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.CANISTER
    }

    companion object {
        //    protected IIcon[] icons = new IIcon[7];
        private val craftingvalues = HashMap<ItemStack?,Int>()

        @JvmStatic
        fun saveDamage(itemstack: ItemStack?, damage: Int) {
            craftingvalues.put(itemstack, damage)
        }
    }
}
