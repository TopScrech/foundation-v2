package galaxyspace.core

import galaxyspace.core.prefab.items.ItemAxeGS
import galaxyspace.core.prefab.items.ItemFluidCanisterGS
import galaxyspace.core.prefab.items.ItemHoeGS
import galaxyspace.core.prefab.items.ItemPickaxeGS
import galaxyspace.core.prefab.items.ItemSpadeGS
import galaxyspace.core.prefab.items.ItemSwordGS
import galaxyspace.core.prefab.items.rockets.ItemFluidCargoRocket
import galaxyspace.core.prefab.items.rockets.ItemMultiSeatRocketTest
import galaxyspace.core.prefab.items.rockets.ItemTier4Rocket
import galaxyspace.core.prefab.items.rockets.ItemTier5Rocket
import galaxyspace.core.prefab.items.rockets.ItemTier6Rocket
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBattery
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemCompressedPlates
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemGSOxygenTank
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemHeavyDutyPlates
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemIngots
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemNuggets
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketModules
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketParts
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemSchematics
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemTierKeysChest
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemArmorGS
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemJetpack
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuitLight
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuitTier1
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemThermalPaddingBase
import net.minecraft.item.Item.ToolMaterial
import net.minecraft.item.ItemArmor.ArmorMaterial

import galaxyspace.GalaxySpace
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.prefab.items.*
import galaxyspace.core.prefab.items.rockets.*
import galaxyspace.systems.SolarSystem.planets.mars.items.ItemMarsRover
import galaxyspace.systems.SolarSystem.planets.overworld.items.*
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.*
import galaxyspace.systems.SolarSystem.planets.overworld.items.tools.*
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.PartialCanister
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.oredict.OreDictionary

object GSItems {
    @JvmField
    var SPACESUIT_LIGHT: ItemArmor.ArmorMaterial? = EnumHelper.addArmorMaterial(
        "SPACESUIT_LIGHT",
        "",
        15,
        intArrayOf(3, 6, 7, 3),
        0,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        0.0f
    )

    @JvmField
    var SPACESUIT_TIER_1: ItemArmor.ArmorMaterial? = EnumHelper.addArmorMaterial(
        "SPACESUIT",
        "",
        18,
        intArrayOf(6, 13, 11, 6),
        0,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        0.0f
    )

    @JvmField
    var COBALT: ItemArmor.ArmorMaterial? = EnumHelper.addArmorMaterial(
        "COBALT",
        "",
        13,
        intArrayOf(2, 5, 6, 2),
        9,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        0.0f
    )

    @JvmField
    var COBALT_TOOLS: Item.ToolMaterial? = EnumHelper.addToolMaterial("cobalt", 2, 250, 6.0f, 2.0f, 14)

    @JvmField
    var PLASMA_TOOLS: Item.ToolMaterial? = EnumHelper.addToolMaterial("plasma", 3, 1500, 8.0f, 8.0f, 0)


    @JvmField
    var BASIC: Item = ItemBasicGS()

    @JvmField
    var INGOTS: Item = ItemIngots()

    @JvmField
    var NUGGETS: Item = ItemNuggets()

    @JvmField
    var HDP: Item = ItemHeavyDutyPlates()

    @JvmField
    var COMPRESSED_PLATES: Item = ItemCompressedPlates()

    @JvmField
    var ROCKET_MODULES: Item = ItemRocketModules()

    @JvmField
    var ROCKET_PARTS: Item = ItemRocketParts()

    @JvmField
    var SCHEMATICS: Item = ItemSchematics()

    @JvmField
    var DUNGEON_KEYS: Item = ItemTierKeysChest()

    @JvmField
    var UPGRADES: Item = ItemUpgrades()

    @JvmField
    var OXYGENTANK_TIER_4: Item = ItemGSOxygenTank(3, "oxygen_tank_4", GSConfigCore.storageOxygenTankTier4, false)

    @JvmField
    var OXYGENTANK_TIER_5: Item = ItemGSOxygenTank(4, "oxygen_tank_5", GSConfigCore.storageOxygenTankTier5, false)

    @JvmField
    var OXYGENTANK_TIER_6: Item = ItemGSOxygenTank(5, "oxygen_tank_6", GSConfigCore.storageOxygenTankTier6, false)

    @JvmField
    var OXYGENTANK_TIER_EPP: Item =
        ItemGSOxygenTank(-1, "oxygen_tank_epp_1", GSConfigCore.storageOxygenTankTierEPP, true)

    @JvmField
    var ROCKET_TIER_4: Item = ItemTier4Rocket("rocket_tier_4")

    @JvmField
    var ROCKET_TIER_5: Item = ItemTier5Rocket("rocket_tier_5")

    @JvmField
    var ROCKET_TIER_6: Item = ItemTier6Rocket("rocket_tier_6")

    @JvmField
    var ROCKET_MULTI_SEAT: Item = ItemMultiSeatRocketTest("rocket_multi_seat")

    @JvmField
    var ROCKET_FLUID_CRAGO: Item = ItemFluidCargoRocket("rocket_fluid_cargo")

    @JvmField
    var MARS_ROVER: Item = ItemMarsRover("mars_rover")

    @JvmField
    var ADVANCED_BATTERY: Item = ItemBattery("advanced_battery", 60000f)

    @JvmField
    var MODERN_BATTERY: Item = ItemBattery("modern_battery", 120000f)

    @JvmField
    var EXTRA_BATTERY: Item = ItemBattery("extra_battery", 180000f)

    @JvmField
    var ULTIMATE_BATTERY: Item = ItemBattery("ultimate_battery", 300000f)

    @JvmField
    var HYDROGEN_CANISTER: Item = ItemFluidCanisterGS("hydrogen_canister", FluidRegistry.getFluid("hydrogen"))

    @JvmField
    var ETHANE_CANISTER: Item = ItemFluidCanisterGS("ethane_canister", FluidRegistry.getFluid("ethane"))

    @JvmField
    var EM_CANISTER: Item = ItemFluidCanisterGS("em_canister", (GSFluids.LiquidEthaneMethane)!!)

    @JvmField
    var HELIUM_CANISTER: Item = ItemFluidCanisterGS("helium_canister", (GSFluids.Helium3)!!)

    @JvmField
    var HELIUM_HYDROGEN_CANISTER: Item = ItemFluidCanisterGS("hh_canister", (GSFluids.HeliumHydrogen)!!)

    @JvmField
    var NATURE_GAS_CANISTER: Item = ItemFluidCanisterGS("naturegas_canister", (GSFluids.NatureGas)!!)

    //public static Item PLASTIC_OIL_CANISTER = new ItemFluidCanisterGS("plastic_oil_canister", FluidRegistry.getFluid("oil"), 10001);
    //ARMOR	
    @JvmField
    var SPACE_SUIT_HELMET: Item = ItemSpaceSuitTier1(SPACESUIT_TIER_1, EntityEquipmentSlot.HEAD)

    @JvmField
    var SPACE_SUIT_BODY: Item = ItemSpaceSuitTier1(SPACESUIT_TIER_1, EntityEquipmentSlot.CHEST)

    @JvmField
    var SPACE_SUIT_LEGGINS: Item = ItemSpaceSuitTier1(SPACESUIT_TIER_1, EntityEquipmentSlot.LEGS)

    @JvmField
    var SPACE_SUIT_BOOTS: Item = ItemSpaceSuitTier1(SPACESUIT_TIER_1, EntityEquipmentSlot.FEET)

    @JvmField
    var SPACE_SUIT_LIGHT_HELMET: Item = ItemSpaceSuitLight((SPACESUIT_LIGHT)!!, EntityEquipmentSlot.HEAD)

    @JvmField
    var SPACE_SUIT_LIGHT_BODY: Item = ItemSpaceSuitLight((SPACESUIT_LIGHT)!!, EntityEquipmentSlot.CHEST)

    @JvmField
    var SPACE_SUIT_LIGHT_LEGGINS: Item = ItemSpaceSuitLight((SPACESUIT_LIGHT)!!, EntityEquipmentSlot.LEGS)

    @JvmField
    var SPACE_SUIT_LIGHT_BOOTS: Item = ItemSpaceSuitLight((SPACESUIT_LIGHT)!!, EntityEquipmentSlot.FEET)

    @JvmField
    var COBALT_HELMET: Item = ItemArmorGS("cobalt_helmet", (COBALT)!!, EntityEquipmentSlot.HEAD)

    @JvmField
    var COBALT_CHEST: Item = ItemArmorGS("cobalt_chest", (COBALT)!!, EntityEquipmentSlot.CHEST)

    @JvmField
    var COBALT_LEGS: Item = ItemArmorGS("cobalt_legs", (COBALT)!!, EntityEquipmentSlot.LEGS)

    @JvmField
    var COBALT_BOOTS: Item = ItemArmorGS("cobalt_boots", (COBALT)!!, EntityEquipmentSlot.FEET)

    @JvmField
    var THERMAL_PADDING_3: Item = ItemThermalPaddingBase(3, false)

    @JvmField
    var THERMAL_PADDING_4: Item = ItemThermalPaddingBase(4, false)

    //TOOLS
    @JvmField
    var COBALT_SWORD: Item = ItemSwordGS("cobalt_sword", (COBALT_TOOLS)!!)

    @JvmField
    var COBALT_AXE: Item = ItemAxeGS("cobalt_axe", (COBALT_TOOLS)!!)

    @JvmField
    var COBALT_SPADE: Item = ItemSpadeGS("cobalt_spade", (COBALT_TOOLS)!!)

    @JvmField
    var COBALT_HOE: Item = ItemHoeGS("cobalt_hoe", (COBALT_TOOLS)!!)

    @JvmField
    var COBALT_PICKAXE: Item = ItemPickaxeGS("cobalt_pickaxe", (COBALT_TOOLS)!!, false)

    @JvmField
    var PLASMA_SWORD: Item = ItemPlasmaSword()

    @JvmField
    var PLASMA_AXE: Item = ItemPlasmaAxe()

    @JvmField
    var PLASMA_PICKAXE: Item = ItemPlasmaPickaxe()

    @JvmField
    var JETPACK: Item = ItemJetpack(SPACESUIT_TIER_1, EntityEquipmentSlot.CHEST)

    @JvmField
    var MATTER_MANIPULATOR: Item = ItemMatterManipulator()

    @JvmField
    var GEOLOGICAL_SCANNER: Item = ItemGeologicalScanner()

    @JvmField
    var GAS_EXTRACTOR: Item = ItemGasExtractor()

    @JvmStatic
    fun initialize() {
        registerItem(BASIC)
        registerItem(INGOTS)
        registerItem(NUGGETS)
        registerItem(HDP)
        registerItem(COMPRESSED_PLATES)
        registerItem(ROCKET_MODULES)
        registerItem(ROCKET_PARTS)
        registerItem(SCHEMATICS)
        registerItem(DUNGEON_KEYS)
        registerItem(UPGRADES)

        registerItem(ADVANCED_BATTERY)
        registerItem(MODERN_BATTERY)
        registerItem(EXTRA_BATTERY)
        registerItem(ULTIMATE_BATTERY)

        registerItem(HYDROGEN_CANISTER)
        registerItem(ETHANE_CANISTER)
        registerItem(EM_CANISTER)
        registerItem(HELIUM_CANISTER)
        registerItem(HELIUM_HYDROGEN_CANISTER)
        registerItem(NATURE_GAS_CANISTER)

        registerItem(OXYGENTANK_TIER_4)
        registerItem(OXYGENTANK_TIER_5)
        registerItem(OXYGENTANK_TIER_6)
        registerItem(OXYGENTANK_TIER_EPP)

        registerItem(ROCKET_TIER_4)
        registerItem(ROCKET_TIER_5)
        registerItem(ROCKET_TIER_6)
        //registerItem(ROCKET_MULTI_SEAT);
        //registerItem(ROCKET_FLUID_CRAGO);
        registerItem(MARS_ROVER)

        registerItem(SPACE_SUIT_HELMET)
        registerItem(SPACE_SUIT_BODY)
        registerItem(SPACE_SUIT_LEGGINS)
        registerItem(SPACE_SUIT_BOOTS)

        registerItem(SPACE_SUIT_LIGHT_HELMET)
        registerItem(SPACE_SUIT_LIGHT_BODY)
        registerItem(SPACE_SUIT_LIGHT_LEGGINS)
        registerItem(SPACE_SUIT_LIGHT_BOOTS)

        registerItem(COBALT_HELMET)
        registerItem(COBALT_CHEST)
        registerItem(COBALT_LEGS)
        registerItem(COBALT_BOOTS)

        registerItem(THERMAL_PADDING_3)
        registerItem(THERMAL_PADDING_4)

        registerItem(COBALT_SWORD)
        registerItem(COBALT_AXE)
        registerItem(COBALT_PICKAXE)
        registerItem(COBALT_SPADE)
        registerItem(COBALT_HOE)

        registerItem(PLASMA_SWORD)
        registerItem(PLASMA_AXE)
        registerItem(PLASMA_PICKAXE)

        registerItem(JETPACK)
        //registerItem(MATTER_MANIPULATOR);
        registerItem(GEOLOGICAL_SCANNER)
        registerItem(GAS_EXTRACTOR)

        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                HYDROGEN_CANISTER,
                GalaxySpace.Companion.MODID,
                "hydrogen_canister",
                6
            )
        )
        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                ETHANE_CANISTER,
                GalaxySpace.Companion.MODID,
                "ethane_canister",
                6
            )
        )
        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                EM_CANISTER,
                GalaxySpace.Companion.MODID,
                "em_canister",
                6
            )
        )
        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                HELIUM_CANISTER,
                GalaxySpace.Companion.MODID,
                "helium_canister",
                6
            )
        )
        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                HELIUM_HYDROGEN_CANISTER,
                GalaxySpace.Companion.MODID,
                "hh_canister",
                6
            )
        )
        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                NATURE_GAS_CANISTER,
                GalaxySpace.Companion.MODID,
                "naturegas_canister",
                6
            )
        )
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("ingotCobalt", ItemStack(INGOTS, 1, 0))
        OreDictionary.registerOre("ingotMagnesium", ItemStack(INGOTS, 1, 1))
        OreDictionary.registerOre("ingotNickel", ItemStack(INGOTS, 1, 2))
        OreDictionary.registerOre("dustSulfur", ItemStack(BASIC, 1, 7))
        OreDictionary.registerOre("waferModern", ItemStack(BASIC, 1, 5))

        if (GSConfigCore.enablePlateOreDict) {
            OreDictionary.registerOre("plateCopper", ItemStack(GCItems.basicItem, 1, 6))
            OreDictionary.registerOre("plateTin", ItemStack(GCItems.basicItem, 1, 7))
            OreDictionary.registerOre("plateAluminum", ItemStack(GCItems.basicItem, 1, 8))
            OreDictionary.registerOre("plateSteel", ItemStack(GCItems.basicItem, 1, 9))
            OreDictionary.registerOre("plateBronze", ItemStack(GCItems.basicItem, 1, 10))
            OreDictionary.registerOre("plateIron", ItemStack(GCItems.basicItem, 1, 11))
            OreDictionary.registerOre("plateDesh", ItemStack(MarsItems.marsItemBasic, 1, 5))
            OreDictionary.registerOre("plateTitanium", ItemStack(AsteroidsItems.basicItem, 1, 6))
        }

        OreDictionary.registerOre("plateCoal", ItemStack(COMPRESSED_PLATES, 1, 0))
        OreDictionary.registerOre("plateCobalt", ItemStack(COMPRESSED_PLATES, 1, 1))
        OreDictionary.registerOre("plateMagnesium", ItemStack(COMPRESSED_PLATES, 1, 2))
        OreDictionary.registerOre("plateNickel", ItemStack(COMPRESSED_PLATES, 1, 3))

        OreDictionary.registerOre("ingotLead", ItemStack(VenusItems.basicItem, 1, 1))
    }

    @JvmStatic
    fun registerItem(item: Item) {
        val name: String = item.getTranslationKey().substring(5)
        if (item.getRegistryName() == null) {
            item.setRegistryName(name)
        }
        GCCoreUtil.registerGalacticraftItem(name, item)
        GalacticraftCore.itemListTrue.add(item)
        (GalacticraftCore.proxy)!!.postRegisterItem(item)
    }
}
