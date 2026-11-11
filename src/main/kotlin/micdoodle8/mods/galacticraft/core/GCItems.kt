package micdoodle8.mods.galacticraft.core

import foundation.item.legacyItemValue

import foundation.item.FoundationItemVariants

import net.minecraft.item.Item.ToolMaterial
import net.minecraft.item.ItemArmor.ArmorMaterial

import com.google.common.collect.Lists
import com.google.common.base.Function
import com.google.common.collect.Maps
import com.google.common.collect.Ordering
import mezz.jei.api.ingredients.IIngredientBlacklist
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemArmorGC
import micdoodle8.mods.galacticraft.core.items.ItemAxeGC
import micdoodle8.mods.galacticraft.core.items.ItemBase
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.items.ItemBattery
import micdoodle8.mods.galacticraft.core.items.ItemBatteryInfinite
import micdoodle8.mods.galacticraft.core.items.ItemBuggy
import micdoodle8.mods.galacticraft.core.items.ItemBuggyMaterial
import micdoodle8.mods.galacticraft.core.items.ItemCanister
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.items.ItemCanisterOxygenInfinite
import micdoodle8.mods.galacticraft.core.items.ItemCheese
import micdoodle8.mods.galacticraft.core.items.ItemEmergencyKit
import micdoodle8.mods.galacticraft.core.items.ItemFlag
import micdoodle8.mods.galacticraft.core.items.ItemFood
import micdoodle8.mods.galacticraft.core.items.ItemFuelCanister
import micdoodle8.mods.galacticraft.core.items.ItemHoeGC
import micdoodle8.mods.galacticraft.core.items.ItemIC2Compat
import micdoodle8.mods.galacticraft.core.items.ItemKey
import micdoodle8.mods.galacticraft.core.items.ItemMeteorChunk
import micdoodle8.mods.galacticraft.core.items.ItemMeteoricIron
import micdoodle8.mods.galacticraft.core.items.ItemMoon
import micdoodle8.mods.galacticraft.core.items.ItemOilCanister
import micdoodle8.mods.galacticraft.core.items.ItemOxygenGear
import micdoodle8.mods.galacticraft.core.items.ItemOxygenMask
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import micdoodle8.mods.galacticraft.core.items.ItemPickaxeGC
import micdoodle8.mods.galacticraft.core.items.ItemPreLaunchChecklist
import micdoodle8.mods.galacticraft.core.items.ItemRocketEngineGC
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import micdoodle8.mods.galacticraft.core.items.ItemSensorGlasses
import micdoodle8.mods.galacticraft.core.items.ItemSpadeGC
import micdoodle8.mods.galacticraft.core.items.ItemSwordGC
import micdoodle8.mods.galacticraft.core.items.ItemTier1Rocket
import micdoodle8.mods.galacticraft.core.items.ItemUniversalWrench
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.StackSorted
import micdoodle8.mods.galacticraft.core.wrappers.PartialCanister
import net.minecraft.world.level.block.Block
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import java.lang.RuntimeException
import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap
import java.util.LinkedList
import java.util.Locale

object GCItems {
    @JvmField
    var oxTankLight: Item? = null

    @JvmField
    var oxTankMedium: Item? = null

    @JvmField
    var oxTankHeavy: Item? = null

    @JvmField
    var oxMask: Item? = null

    @JvmField
    var rocketTier1: Item? = null

    @JvmField
    var sensorGlasses: Item? = null

    @JvmField
    var sensorLens: Item? = null

    @JvmField
    var steelPickaxe: Item? = null

    @JvmField
    var steelAxe: Item? = null

    @JvmField
    var steelHoe: Item? = null

    @JvmField
    var steelSpade: Item? = null

    @JvmField
    var steelSword: Item? = null

    @JvmField
    var steelHelmet: Item? = null

    @JvmField
    var steelChestplate: Item? = null

    @JvmField
    var steelLeggings: Item? = null

    @JvmField
    var steelBoots: Item? = null

    @JvmField
    var canister: Item? = null

    @JvmField
    var oxygenVent: Item? = null

    @JvmField
    var oxygenFan: Item? = null

    @JvmField
    var oxygenConcentrator: Item? = null

    @JvmField
    var rocketEngine: Item? = null

    @JvmField
    var heavyPlatingTier1: Item? = null

    @JvmField
    var partNoseCone: Item? = null

    @JvmField
    var partFins: Item? = null

    @JvmField
    var buggy: Item? = null

    @JvmField
    var flag: Item? = null

    @JvmField
    var oxygenGear: Item? = null

    @JvmField
    var parachute: Item? = null

    @JvmField
    var canvas: Item? = null

    @JvmField
    var flagPole: Item? = null

    @JvmField
    var oilCanister: Item? = null

    @JvmField
    var fuelCanister: Item? = null

    @JvmField
    var oxygenCanisterInfinite: Item? = null

    @JvmField
    var schematic: Item? = null

    @JvmField
    var key: Item? = null

    @JvmField
    var partBuggy: Item? = null

    @JvmField
    var basicItem: Item? = null

    @JvmField
    var foodItem: Item? = null

    @JvmField
    var battery: Item? = null

    @JvmField
    var infiniteBatery: Item? = null

    @JvmField
    var meteorChunk: Item? = null

    @JvmField
    var wrench: Item? = null

    @JvmField
    var cheeseCurd: Item? = null

    @JvmField
    var meteoricIronRaw: Item? = null

    @JvmField
    var itemBasicMoon: Item? = null

    @JvmField
    var bucketOil: Item? = null

    @JvmField
    var bucketFuel: Item? = null

    //	public static Item cheeseBlock;
    @JvmField
    var prelaunchChecklist: Item? = null

    @JvmField
    var dungeonFinder: Item? = null

    @JvmField
    var ic2compat: Item? = null

    @JvmField
    var emergencyKit: Item? = null

    @JvmField
    var ARMOR_SENSOR_GLASSES: ItemArmor.ArmorMaterial? = EnumHelper.addArmorMaterial(
        "SENSORGLASSES",
        "",
        200,
        intArrayOf(0, 0, 0, 0),
        0,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        0.0f
    )

    @JvmField
    var ARMOR_STEEL: ItemArmor.ArmorMaterial? =
        EnumHelper.addArmorMaterial("steel", "", 30, intArrayOf(3, 6, 8, 3), 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f)

    @JvmField
    var TOOL_STEEL: net.minecraft.world.item.ToolMaterial = foundation.item.FoundationSteelTools.MATERIAL

    @JvmField
    var hiddenItems: ArrayList<Item> = ArrayList<Item>()

    @JvmField
    var canisterTypes: LinkedList<ItemCanisterGeneric?> = LinkedList<ItemCanisterGeneric?>()

    @JvmField
    var sortMapItems: MutableMap<EnumSortCategoryItem, MutableList<StackSorted?>?> =
        Maps.newHashMap<EnumSortCategoryItem, MutableList<StackSorted?>?>()

    @JvmField
    var itemChanges: HashMap<ItemStack, ItemStack?> = HashMap<ItemStack, ItemStack?>(4, 1.0f)

    @JvmStatic
    fun initItems() {
        oxTankLight = ItemOxygenTank(1, "oxygen_tank_light_full")
        oxTankMedium = ItemOxygenTank(2, "oxygen_tank_med_full")
        oxTankHeavy = ItemOxygenTank(3, "oxygen_tank_heavy_full")
        oxMask = ItemOxygenMask("oxygen_mask")
        rocketTier1 = ItemTier1Rocket("rocket_t1")
        sensorGlasses = ItemSensorGlasses("sensor_glasses")
        steelPickaxe = ItemPickaxeGC("steel_pickaxe")
        steelAxe = ItemAxeGC("steel_axe")
        steelHoe = ItemHoeGC("steel_hoe")
        steelSpade = ItemSpadeGC("steel_shovel")
        steelSword = ItemSwordGC("steel_sword")
        steelHelmet = ItemArmorGC(EntityEquipmentSlot.HEAD, "helmet")
        steelChestplate = ItemArmorGC(EntityEquipmentSlot.CHEST, "chestplate")
        steelLeggings = ItemArmorGC(EntityEquipmentSlot.LEGS, "leggings")
        steelBoots = ItemArmorGC(EntityEquipmentSlot.FEET, "boots")
        canister = ItemCanister("canister")
        oxygenVent = ItemBase("air_vent")
        oxygenFan = ItemBase("air_fan")
        oxygenConcentrator = ItemBase("oxygen_concentrator")
        heavyPlatingTier1 = ItemBase("heavy_plating").setSmeltingXP(1f)
        rocketEngine = ItemRocketEngineGC("engine")
        partFins = ItemBase("rocket_fins")
        partNoseCone = ItemBase("nose_cone")
        sensorLens = ItemBase("sensor_lens")
        buggy = ItemBuggy("buggy")
        flag = ItemFlag("flag")
        oxygenGear = ItemOxygenGear("oxygen_gear")
        parachute = ItemParaChute("parachute")
        canvas = ItemBase("canvas")
        oilCanister = ItemOilCanister("oil_canister_partial")
        fuelCanister = ItemFuelCanister("fuel_canister_partial")
        oxygenCanisterInfinite = ItemCanisterOxygenInfinite("infinite_oxygen")
        flagPole = ItemBase("steel_pole")
        schematic = ItemSchematic("schematic")
        key = ItemKey("key")
        partBuggy = ItemBuggyMaterial("buggymat")
        basicItem = ItemBasic("basic_item")
        foodItem = ItemFood("food")
        battery = ItemBattery("battery")
        infiniteBatery = ItemBatteryInfinite("infinite_battery")
        meteorChunk = ItemMeteorChunk("meteor_chunk")
        wrench = ItemUniversalWrench("standard_wrench")
        cheeseCurd = ItemCheese(1, 0.1f, false)
        //		GCItems.cheeseBlock = new ItemBlockCheese(GCBlocks.cheeseBlock, "cheeseBlock");
        meteoricIronRaw = ItemMeteoricIron("meteoric_iron_raw")
        itemBasicMoon = ItemMoon("item_basic_moon")
        prelaunchChecklist = ItemPreLaunchChecklist("prelaunch_checklist")
        dungeonFinder = ItemBase("dungeonfinder")
        ic2compat = ItemIC2Compat("ic2compat")
        emergencyKit = ItemEmergencyKit("emergency_kit")


        registerItems()
        itemChanges.put(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 15), ItemStack(foodItem, 1, 0))
        itemChanges.put(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 16), ItemStack(foodItem, 1, 1))
        itemChanges.put(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 17), ItemStack(foodItem, 1, 2))
        itemChanges.put(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 18), ItemStack(foodItem, 1, 3))

        (GalacticraftCore.Companion.proxy)!!.registerCanister(
            PartialCanister(
                oilCanister,
                Constants.MOD_ID_CORE,
                "oil_canister_partial",
                7
            )
        )
        (GalacticraftCore.Companion.proxy)!!.registerCanister(
            PartialCanister(
                fuelCanister,
                Constants.MOD_ID_CORE,
                "fuel_canister_partial",
                7
            )
        )
    }

    @JvmStatic
    fun oreDictRegistrations() {
        for (i: Int in ItemBasic.names.indices) {
            if ((ItemBasic.names[i])!!.contains("ingot") || (ItemBasic.names[i])!!.contains("compressed") || (ItemBasic.names[i])!!.contains(
                    "wafer"
                )
            ) {
                var name: String = (ItemBasic.names[i])!!
                while (name.contains("_")) {
                    val loc: Int = name.indexOf("_")
                    name = name.substring(0, loc) + name.substring(loc + 1, loc + 2)
                        .uppercase(Locale.getDefault()) + name.substring(loc + 2, name.length)
                }
                OreDictionary.registerOre(name, FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, i))
            }
        }

        OreDictionary.registerOre("foodCheese", ItemStack(cheeseCurd, 1, 0))
        OreDictionary.registerOre("compressedMeteoricIron", FoundationItemVariants.stack(requireNotNull(itemBasicMoon), 1, 1))
        OreDictionary.registerOre("ingotMeteoricIron", FoundationItemVariants.stack(requireNotNull(itemBasicMoon), 1, 0))
        if (CompatibilityManager.useAluDust()) {
            OreDictionary.registerOre("dustAluminum", ItemStack(ic2compat, 1, 0))
            OreDictionary.registerOre("dustAluminium", ItemStack(ic2compat, 1, 0))
        }

        (GalacticraftCore.Companion.proxy)!!.registerCanister(
            PartialCanister(
                oilCanister,
                Constants.MOD_ID_CORE,
                "oil_canister_partial",
                7
            )
        )
        (GalacticraftCore.Companion.proxy)!!.registerCanister(
            PartialCanister(
                fuelCanister,
                Constants.MOD_ID_CORE,
                "fuel_canister_partial",
                7
            )
        )
        OreDictionary.registerOre(ConfigManagerCore.otherModsSilicon, FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 2))
    }


    /**
     * Do not call this until after mod loading is complete
     * because JEI doesn't have an internal item blacklist
     * until it services an FMLLoadCompleteEvent.
     * (Seriously?!)
     */
    @JvmStatic
    fun hideItemsJEI(jeiHidden: IIngredientBlacklist?) {
        if (jeiHidden != null) {
            for (item: ItemStack in itemChanges.keys) {
                jeiHidden.addIngredientToBlacklist<ItemStack?>(item.copy())
            }

            for (item: Item in hiddenItems) {
                jeiHidden.addIngredientToBlacklist<ItemStack?>(ItemStack(item, 1, 0))
            }

            for (block in GCBlocks.hiddenBlocks) {
                jeiHidden.addIngredientToBlacklist<ItemStack?>(ItemStack(block, 1, 0))
                if (block === GCBlocks.slabGCDouble) {
                    for (j: Int in 1..<(if (GalacticraftCore.Companion.isPlanetsLoaded) 7 else 4)) jeiHidden.addIngredientToBlacklist<ItemStack?>(
                        ItemStack(block, 1, j)
                    )
                }
            }
        }
    }

    @JvmStatic
    fun finalizeSort() {
        val itemOrderListItems: MutableList<StackSorted?> = Lists.newArrayList<StackSorted?>()
        for (type: EnumSortCategoryItem in EnumSortCategoryItem.entries) {
            val stackSorteds: MutableList<*>? = sortMapItems.get(type)
            if (stackSorteds != null) {
                itemOrderListItems.addAll((stackSorteds as Collection<StackSorted?>))
            } else {
                println("ERROR: null sort stack: " + type.toString())
            }
        }

        val tabSorterItems: Comparator<ItemStack>? = Ordering.explicit<StackSorted?>(itemOrderListItems)
            .onResultOf<ItemStack?>(Function { input: ItemStack? ->
                StackSorted(
                    input!!.getItem(),
                    input.legacyItemValue
                )
            })

        (GalacticraftCore.Companion.galacticraftItemsTab)!!.setTabSorter(tabSorterItems)
    }

    @JvmStatic
    fun registerSorted(item: Item) {
        if (item is ISortableItem) {
            val sortableItem: ISortableItem = item as ISortableItem
            val items: NonNullList<ItemStack> = NonNullList.create<ItemStack>()
            item.getSubItems(GalacticraftCore.Companion.galacticraftItemsTab, items)
            for (stack: ItemStack in items) {
                val categoryItem: EnumSortCategoryItem? = sortableItem.getCategory(stack.legacyItemValue)
                if (!sortMapItems.containsKey(categoryItem)) {
                    sortMapItems.put((categoryItem)!!, ArrayList<StackSorted?>())
                }
                sortMapItems.get(categoryItem)!!.add(StackSorted(stack.getItem(), stack.legacyItemValue))
            }
        } else if (item.getCreativeTab() === GalacticraftCore.Companion.galacticraftItemsTab) {
            throw RuntimeException(item.javaClass.toString() + " must inherit " + ISortableItem::class.java.getSimpleName() + "!")
        }
    }

    @JvmStatic
    fun registerItems() {
        GCItems.registerItem(rocketTier1!!)
        GCItems.registerItem(oxMask!!)
        GCItems.registerItem(oxygenGear!!)
        GCItems.registerItem(oxTankLight!!)
        GCItems.registerItem(oxTankMedium!!)
        GCItems.registerItem(oxTankHeavy!!)
        GCItems.registerItem(oxygenCanisterInfinite!!)
        GCItems.registerItem(sensorLens!!)
        GCItems.registerItem(sensorGlasses!!)
        GCItems.registerItem(wrench!!)
        GCItems.registerItem(steelPickaxe!!)
        GCItems.registerItem(steelAxe!!)
        GCItems.registerItem(steelHoe!!)
        GCItems.registerItem(steelSpade!!)
        GCItems.registerItem(steelSword!!)
        GCItems.registerItem(steelHelmet!!)
        GCItems.registerItem(steelChestplate!!)
        GCItems.registerItem(steelLeggings!!)
        GCItems.registerItem(steelBoots!!)
        GCItems.registerItem(canister!!)
        GCItems.registerItem(oxygenVent!!)
        GCItems.registerItem(oxygenFan!!)
        GCItems.registerItem(oxygenConcentrator!!)
        GCItems.registerItem(rocketEngine!!)
        GCItems.registerItem(heavyPlatingTier1!!)
        GCItems.registerItem(partNoseCone!!)
        GCItems.registerItem(partFins!!)
        GCItems.registerItem(flagPole!!)
        GCItems.registerItem(canvas!!)
        GCItems.registerItem(oilCanister!!)
        GCItems.registerItem(fuelCanister!!)
        GCItems.registerItem(schematic!!)
        GCItems.registerItem(key!!)
        GCItems.registerItem(partBuggy!!)
        GCItems.registerItem(buggy!!)
        GCItems.registerItem(basicItem!!)
        GCItems.registerItem(foodItem!!)
        GCItems.registerItem(battery!!)
        GCItems.registerItem(infiniteBatery!!)
        GCItems.registerItem(meteorChunk!!)
        GCItems.registerItem(cheeseCurd!!)
        GCItems.registerItem(meteoricIronRaw!!)
        GCItems.registerItem(itemBasicMoon!!)
        //		GCItems.registerItem(GCItems.cheeseBlock);
        GCItems.registerItem(flag!!)
        GCItems.registerItem(parachute!!)
        GCItems.registerItem(prelaunchChecklist!!)
        GCItems.registerItem(dungeonFinder!!)
        GCItems.registerItem(emergencyKit!!)

        canisterTypes.add(fuelCanister as ItemCanisterGeneric?)
        canisterTypes.add(oilCanister as ItemCanisterGeneric?)

        if (CompatibilityManager.useAluDust()) GCItems.registerItem(ic2compat!!)
    }

    @JvmStatic
    fun registerItem(item: Item) {
        val name: String = item.getTranslationKey().substring(5)
        if (item.getRegistryName() == null) {
            item.setRegistryName(name)
        }
        GCCoreUtil.registerGalacticraftItem(name, item)
        GalacticraftCore.Companion.itemListTrue.add(item)
        (GalacticraftCore.Companion.proxy)!!.postRegisterItem(item)
    }

    @JvmStatic
    fun registerItems(registry: IForgeRegistry<Item?>) {
        for (item in GalacticraftCore.Companion.itemList) {
            registry.register((item)!!.getItem())
        }
    }
}
