package micdoodle8.mods.galacticraft.planets.venus

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.items.ItemBasicVenus
import micdoodle8.mods.galacticraft.planets.venus.items.ItemBatteryAtomic
import micdoodle8.mods.galacticraft.planets.venus.items.ItemKeyVenus
import micdoodle8.mods.galacticraft.planets.venus.items.ItemThermalPaddingTier2
import micdoodle8.mods.galacticraft.planets.venus.items.ItemVolcanicPickaxe
import net.minecraft.world.item.Item
import net.minecraftforge.common.util.EnumHelper

object VenusItems {
    @JvmField
    var thermalPaddingTier2: Item? = null

    @JvmField
    var basicItem: Item? = null

    @JvmField
    var volcanicPickaxe: Item? = null

    @JvmField
    var key: Item? = null

    @JvmField
    var bucketSulphuricAcid: Item? = null

    @JvmField
    var atomicBattery: Item? = null

    @JvmField
    var TOOL_VOLCANIC: Item.ToolMaterial? = EnumHelper.addToolMaterial("volcanic", 3, 1561, 32.0f, 1.5f, 10)

    @JvmStatic
    fun initItems() {
        thermalPaddingTier2 = ItemThermalPaddingTier2("thermal_padding_t2")
        basicItem = ItemBasicVenus("basic_item_venus")
        volcanicPickaxe = ItemVolcanicPickaxe("volcanic_pickaxe")
        key = ItemKeyVenus("key_t3")
        atomicBattery = ItemBatteryAtomic("atomic_battery")

        registerItems()
        registerHarvestLevels()
    }

    @JvmStatic
    fun registerHarvestLevels() {
    }

    @JvmStatic
    private fun registerItems() {
        VenusItems.registerItem(thermalPaddingTier2!!)
        VenusItems.registerItem(basicItem!!)
        VenusItems.registerItem(volcanicPickaxe!!)
        VenusItems.registerItem(key!!)
        VenusItems.registerItem(atomicBattery!!)
    }

    @JvmStatic
    fun registerItem(item: Item) {
        val name: String = item.getTranslationKey().substring(5)
        GCCoreUtil.registerGalacticraftItem(name, item)
        GalacticraftCore.itemListTrue.add(item)
        item.setRegistryName(name)
        (GalacticraftPlanets.proxy)!!.postRegisterItem(item)
    }
}
