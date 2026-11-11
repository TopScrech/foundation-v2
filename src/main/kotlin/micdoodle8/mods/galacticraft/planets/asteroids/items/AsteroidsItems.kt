package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.PartialCanister
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.oredict.OreDictionary

object AsteroidsItems {
    @JvmField
    var grapple: Item? = null

    @JvmField
    var tier3Rocket: Item? = null

    @JvmField
    var astroMiner: Item? = null

    @JvmField
    var thermalPadding: Item? = null

    @JvmField
    var basicItem: Item? = null

    @JvmField
    var methaneCanister: Item? = null

    @JvmField
    var canisterLOX: Item? = null

    @JvmField
    var canisterLN2: Item? = null

    //public static Item canisterLAr;
    @JvmField
    var atmosphericValve: Item? = null

    @JvmField
    var heavyNoseCone: ItemHeavyNoseCone? = null

    @JvmField
    var orionDrive: Item? = null

    @JvmField
    var titaniumHelmet: Item? = null

    @JvmField
    var titaniumChestplate: Item? = null

    @JvmField
    var titaniumLeggings: Item? = null

    @JvmField
    var titaniumBoots: Item? = null

    @JvmField
    var titaniumAxe: Item? = null

    @JvmField
    var titaniumPickaxe: Item? = null

    @JvmField
    var titaniumSpade: Item? = null

    @JvmField
    var titaniumHoe: Item? = null

    @JvmField
    var titaniumSword: Item? = null

    @JvmField
    var strangeSeed: Item? = null

    @JvmField
    var TOOL_TITANIUM: net.minecraft.world.item.ToolMaterial = foundation.item.FoundationTitaniumTools.BASE_MATERIAL

    @JvmField
    var ARMOR_TITANIUM: ItemArmor.ArmorMaterial? = EnumHelper.addArmorMaterial(
        "titanium",
        "",
        26,
        intArrayOf(5, 7, 10, 5),
        20,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        1.0f
    )

    @JvmStatic
    fun initItems() {
        grapple = ItemGrappleHook("grapple")
        tier3Rocket = ItemTier3Rocket("rocket_t3")
        astroMiner = ItemAstroMiner("astro_miner")
        thermalPadding = ItemThermalPadding("thermal_padding")
        basicItem = ItemBasicAsteroids("item_basic_asteroids")
        methaneCanister = ItemCanisterMethane("methane_canister_partial")
        canisterLOX = ItemCanisterLiquidOxygen("canister_partial_lox")
        canisterLN2 = ItemCanisterLiquidNitrogen("canister_partial_ln2")
        //AsteroidsItems.canisterLAr = new ItemCanisterLiquidArgon("canisterPartialLAr");
        atmosphericValve = ItemAtmosphericValve("atmospheric_valve")
        heavyNoseCone = ItemHeavyNoseCone("heavy_nose_cone")
        orionDrive = ItemOrionDrive("orion_drive")
        titaniumHelmet = ItemArmorAsteroids(EntityEquipmentSlot.HEAD, "helmet")
        titaniumChestplate = ItemArmorAsteroids(EntityEquipmentSlot.CHEST, "chestplate")
        titaniumLeggings = ItemArmorAsteroids(EntityEquipmentSlot.LEGS, "leggings")
        titaniumBoots = ItemArmorAsteroids(EntityEquipmentSlot.FEET, "boots")
        titaniumAxe = ItemAxeAsteroids("titanium_axe")
        titaniumPickaxe = ItemPickaxeAsteroids("titanium_pickaxe")
        titaniumSpade = ItemSpadeAsteroids("titanium_shovel")
        titaniumHoe = ItemHoeAsteroids("titanium_hoe")
        titaniumSword = ItemSwordAsteroids("titanium_sword")
        strangeSeed = ItemStrangeSeed("strange_seed")

        registerItems()


        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                methaneCanister,
                Constants.MOD_ID_PLANETS,
                "methane_canister_partial",
                7
            )
        )
        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                canisterLOX,
                Constants.MOD_ID_PLANETS,
                "canister_partial_lox",
                7
            )
        )
        (GalacticraftCore.proxy)!!.registerCanister(
            PartialCanister(
                canisterLN2,
                Constants.MOD_ID_PLANETS,
                "canister_partial_ln2",
                7
            )
        )
    }

    @JvmStatic
    fun oreDictRegistrations() {
        OreDictionary.registerOre("compressedTitanium", ItemStack(basicItem, 1, 6))
        OreDictionary.registerOre("ingotTitanium", ItemStack(basicItem, 1, 0))
        OreDictionary.registerOre("shardTitanium", ItemStack(basicItem, 1, 4))
        OreDictionary.registerOre("shardIron", ItemStack(basicItem, 1, 3))
        OreDictionary.registerOre("dustTitanium", ItemStack(basicItem, 1, 9))
    }

    @JvmStatic
    private fun registerItems() {
        AsteroidsItems.registerItem(grapple!!)
        AsteroidsItems.registerItem(tier3Rocket!!)
        AsteroidsItems.registerItem(astroMiner!!)
        AsteroidsItems.registerItem(thermalPadding!!)
        AsteroidsItems.registerItem(basicItem!!)
        AsteroidsItems.registerItem(methaneCanister!!)
        AsteroidsItems.registerItem(canisterLOX!!)
        AsteroidsItems.registerItem(canisterLN2!!)
        //registerItem(AsteroidsItems.canisterLAr);
        AsteroidsItems.registerItem(atmosphericValve!!)
        AsteroidsItems.registerItem(heavyNoseCone!!)
        AsteroidsItems.registerItem(orionDrive!!)
        AsteroidsItems.registerItem(titaniumHelmet!!)
        AsteroidsItems.registerItem(titaniumChestplate!!)
        AsteroidsItems.registerItem(titaniumLeggings!!)
        AsteroidsItems.registerItem(titaniumBoots!!)
        AsteroidsItems.registerItem(titaniumAxe!!)
        AsteroidsItems.registerItem(titaniumPickaxe!!)
        AsteroidsItems.registerItem(titaniumSpade!!)
        AsteroidsItems.registerItem(titaniumHoe!!)
        AsteroidsItems.registerItem(titaniumSword!!)
        AsteroidsItems.registerItem(strangeSeed!!)

        ARMOR_TITANIUM!!.setRepairItem(ItemStack(basicItem, 1, 0))

        GCItems.canisterTypes.add(canisterLOX as ItemCanisterGeneric?)
        GCItems.canisterTypes.add(methaneCanister as ItemCanisterGeneric?)
        GCItems.canisterTypes.add(canisterLN2 as ItemCanisterGeneric?)
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
