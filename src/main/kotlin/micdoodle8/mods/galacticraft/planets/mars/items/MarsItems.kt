package micdoodle8.mods.galacticraft.planets.mars.items

import net.minecraft.item.Item.ToolMaterial
import net.minecraft.item.ItemArmor.ArmorMaterial

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.item.ItemArmor
import net.minecraftforge.common.util.EnumHelper

object MarsItems {
    @JvmField
    var marsItemBasic: Item? = null

    @JvmField
    var deshPickaxe: Item? = null

    @JvmField
    var deshPickSlime: Item? = null

    @JvmField
    var deshAxe: Item? = null

    @JvmField
    var deshHoe: Item? = null

    @JvmField
    var deshSpade: Item? = null

    @JvmField
    var deshSword: Item? = null

    @JvmField
    var deshHelmet: Item? = null

    @JvmField
    var deshChestplate: Item? = null

    @JvmField
    var deshLeggings: Item? = null

    @JvmField
    var deshBoots: Item? = null

    @JvmField
    var rocketMars: Item? = null

    @JvmField
    var key: Item? = null

    @JvmField
    var schematic: Item? = null

    @JvmField
    var carbonFragments: Item? = null

    @JvmField
    var bucketSludge: Item? = null

    @JvmField
    var ARMORDESH: ItemArmor.ArmorMaterial? =
        EnumHelper.addArmorMaterial("DESH", "", 42, intArrayOf(4, 7, 9, 4), 12, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0f)

    @JvmField
    var TOOLDESH: Item.ToolMaterial? = EnumHelper.addToolMaterial("DESH", 3, 1024, 5.0f, 2.5f, 10)

    @JvmStatic
    fun initItems() {
        marsItemBasic = ItemBasicMars("item_basic_mars")
        deshPickaxe = ItemPickaxeMars((TOOLDESH)!!).setTranslationKey("desh_pick")
        deshPickSlime = ItemPickaxeStickyMars((TOOLDESH)!!).setTranslationKey("desh_pick_slime")
        deshAxe = ItemAxeMars((TOOLDESH)!!).setTranslationKey("desh_axe")
        deshHoe = ItemHoeMars((TOOLDESH)!!).setTranslationKey("desh_hoe")
        deshSpade = ItemSpadeMars((TOOLDESH)!!).setTranslationKey("desh_spade")
        deshSword = ItemSwordMars((TOOLDESH)!!).setTranslationKey("desh_sword")
        deshHelmet = ItemArmorMars((ARMORDESH)!!, 7, EntityEquipmentSlot.HEAD).setTranslationKey("desh_helmet")
        deshChestplate = ItemArmorMars((ARMORDESH)!!, 7, EntityEquipmentSlot.CHEST).setTranslationKey("desh_chestplate")
        deshLeggings = ItemArmorMars((ARMORDESH)!!, 7, EntityEquipmentSlot.LEGS).setTranslationKey("desh_leggings")
        deshBoots = ItemArmorMars((ARMORDESH)!!, 7, EntityEquipmentSlot.FEET).setTranslationKey("desh_boots")
        rocketMars = ItemTier2Rocket().setTranslationKey("rocket_t2")
        key = ItemKeyMars()
        schematic = ItemSchematicTier2()
        carbonFragments = ItemCarbonFragments().setTranslationKey("carbon_fragments")

        registerItems()
        registerHarvestLevels()
    }

    @JvmStatic
    fun registerHarvestLevels() {
        deshPickaxe!!.setHarvestLevel("pickaxe", 4)
        deshPickSlime!!.setHarvestLevel("pickaxe", 4)
        deshAxe!!.setHarvestLevel("axe", 4)
        deshSpade!!.setHarvestLevel("shovel", 4)
    }

    @JvmStatic
    private fun registerItems() {
        MarsItems.registerItem(carbonFragments!!)
        MarsItems.registerItem(marsItemBasic!!)
        MarsItems.registerItem(deshPickaxe!!)
        MarsItems.registerItem(deshPickSlime!!)
        MarsItems.registerItem(deshAxe!!)
        MarsItems.registerItem(deshHoe!!)
        MarsItems.registerItem(deshSpade!!)
        MarsItems.registerItem(deshSword!!)
        MarsItems.registerItem(deshHelmet!!)
        MarsItems.registerItem(deshChestplate!!)
        MarsItems.registerItem(deshLeggings!!)
        MarsItems.registerItem(deshBoots!!)
        MarsItems.registerItem(rocketMars!!)
        MarsItems.registerItem(key!!)
        MarsItems.registerItem(schematic!!)
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
