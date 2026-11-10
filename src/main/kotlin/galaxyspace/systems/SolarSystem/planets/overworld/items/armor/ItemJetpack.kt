package galaxyspace.systems.SolarSystem.planets.overworld.items.armor

import galaxyspace.api.item.IJetpackArmor
import galaxyspace.core.prefab.items.ItemElectricArmor
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.common.ISpecialArmor
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemJetpack constructor(materialIn: ArmorMaterial?, equipmentSlotIn: EntityEquipmentSlot?) :
    ItemElectricArmor((materialIn)!!, (equipmentSlotIn)!!), ISpecialArmor, IJetpackArmor {
    init {
        this.setTranslationKey("jetpack")
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSArmorTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, this.getMaxDamage()))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getArmorModel(
        entityLiving: EntityLivingBase,
        itemStack: ItemStack,
        armorSlot: EntityEquipmentSlot,
        model: ModelBiped
    ): ModelBiped? {
        // ModelBiped model = new ModelJetPack();

        var armormodel: ModelBiped? = ItemSpaceSuitModel(6)
        if (itemStack.getItem() is ItemJetpack) {
            armormodel = ItemSpaceSuit.Companion.fillingArmorModel(armormodel, entityLiving)
        }
        return armormodel
    }

    public override fun getMaxElectricityStored(theItem: ItemStack?): Float {
        return 100000f
    }

    override fun consumeFuel(stack: ItemStack?, fuel: Int) {
        this.discharge(stack, 5f, true)
    }

    override fun decrementFuel(stack: ItemStack?) {
        if ((stack)!!.getItem() is IItemElectricBase) ((stack)!!.getItem() as IItemElectricBase).discharge(stack, 1f, true)
    }

    override fun getFuel(stack: ItemStack?): Int {
        return (stack)!!.getTagCompound()!!.getInteger(IJetpackArmor.TAG_FUEL)
    }

    override fun canFly(stack: ItemStack?, player: EntityPlayer?): Boolean {
        if ((stack)!!.hasTagCompound()) {
            if ((stack)!!.getItemDamage() < (stack)!!.getMaxDamage()) {
                return true
            }
        }

        return false
    }

    override fun isActivated(stack: ItemStack?): Boolean {
        return (stack)!!.getTagCompound()!!.getBoolean(IJetpackArmor.TAG_ACT)
    }

    override fun switchState(stack: ItemStack?, state: Boolean) {
        if ((stack)!!.hasTagCompound()) (stack)!!.getTagCompound()!!.setBoolean(IJetpackArmor.TAG_ACT, state)
        else {
            (stack)!!.setTagCompound(NBTTagCompound())
        }
    }

    override fun getFireStreams(stack: ItemStack?): Int {
        return 2
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        var color: String = ""
        val joules: Float = this.getElectricityStored(stack)

        if (joules <= this.getMaxElectricityStored(stack) / 3) {
            color = "\u00a74"
        } else if (joules > this.getMaxElectricityStored(stack) * 2 / 3) {
            color = "\u00a72"
        } else {
            color = "\u00a76"
        }

        list.add("")
        list.add(
            (color + EnergyDisplayHelper.getEnergyDisplayS(joules) + "/"
                    + EnergyDisplayHelper.getEnergyDisplayS(this.getMaxElectricityStored(stack)))
        )
    }

    override fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        return 0x2ed8db
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return true
    }

    override fun isBookEnchantable(stack: ItemStack, book: ItemStack): Boolean {
        return false
    }

    override fun getProperties(
        player: EntityLivingBase?,
        armor: ItemStack,
        source: DamageSource?,
        damage: Double,
        slot: Int
    ): ISpecialArmor.ArmorProperties {
        if ((source)!!.isUnblockable()) return ISpecialArmor.ArmorProperties(0, 0.0, 0)

        return ISpecialArmor.ArmorProperties(2, 0.15, Int.MAX_VALUE)
    }

    override fun getArmorDisplay(player: EntityPlayer?, armor: ItemStack, slot: Int): Int {
        return this.damageReduceAmount
    }

    override fun damageArmor(
        entity: EntityLivingBase?,
        stack: ItemStack,
        source: DamageSource?,
        damage: Int,
        slot: Int
    ) {
        this.discharge(stack, 5f, true)
    }
}
