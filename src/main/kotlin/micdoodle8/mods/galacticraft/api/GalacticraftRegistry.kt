package micdoodle8.mods.galacticraft.api

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.client.IGameScreen
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.api.world.SpaceStationType
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.DimensionType
import net.minecraft.world.WorldProvider
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.fml.relauncher.FMLRelaunchLog
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.Level
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList

open class GalacticraftRegistry {
    open fun getTypeFromPlanetID(planetID: Int): SpaceStationType? {
        return spaceStations.get(planetID)
    }

    companion object {
        private val teleportTypeMap: MutableMap<Class<out WorldProvider>?, ITeleportType?> =
            HashMap<Class<out WorldProvider>?, ITeleportType?>()
        private val spaceStations: MutableList<SpaceStationType> = ArrayList<SpaceStationType>()
        private val rocketBenchT1Recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
        private val buggyBenchRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
        private val rocketBenchT2Recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
        private val cargoRocketRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
        private val rocketBenchT3Recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
        private val astroMinerRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
        private val rocketGuiMap: MutableMap<Class<out WorldProvider>?, ResourceLocation?> =
            HashMap<Class<out WorldProvider>?, ResourceLocation?>()
        private val dungeonLootMap: MutableMap<Int, MutableList<ItemStack?>?> =
            HashMap<Int, MutableList<ItemStack?>?>()
        private val dimensionTypeIDs: MutableList<Int> = ArrayList<Int>()
        private val gameScreens: MutableList<IGameScreen?> = ArrayList<IGameScreen?>()
        private var maxScreenTypes = 0
        private val gearMap: MutableMap<Int, MutableList<Any?>?> = HashMap<Int, MutableList<Any?>?>()
        private val gearSlotMap: MutableMap<Int, MutableList<EnumExtendedInventorySlot>> =
            HashMap<Int, MutableList<EnumExtendedInventorySlot>>()
        private val gratingRegister: Method? = null

        /**
         * Register a new Teleport type for the world provider passed
         *
         * @param clazz the world provider class that you wish to customize
         * teleportation for
         * @param type  an ITeleportType-implemented class that will be used for the
         * provided world type
         */
        @JvmStatic
        fun registerTeleportType(clazz: Class<out WorldProvider>?, type: ITeleportType?) {
            if (!teleportTypeMap.containsKey(clazz)) {
                teleportTypeMap.put(clazz, type)
            }
        }

        /**
         * Link a world provider to a gui texture. This texture will be shown on the
         * left-side of the screen while the player is in the rocket.
         *
         * @param clazz     The World Provider class
         * @param rocketGui Resource Location for the gui texture
         */
        @JvmStatic
        fun registerRocketGui(clazz: Class<out WorldProvider>?, rocketGui: ResourceLocation?) {
            if (!rocketGuiMap.containsKey(clazz)) {
                rocketGuiMap.put(clazz, rocketGui)
            }
        }

        /**
         * Add loot to the list of items that can possibly spawn in dungeon chests,
         * but it is guaranteed that one will always spawn
         *
         * @param tier Tier of dungeon chest to add loot to. For example Moon is 1
         * and Mars is 2
         * @param loot The itemstack to add to the possible list of items
         */
        @JvmStatic
        fun addDungeonLoot(tier: Int, loot: ItemStack?) {
            var dungeonStacks: MutableList<ItemStack?>? = null

            if (dungeonLootMap.containsKey(tier)) {
                dungeonStacks = dungeonLootMap.get(tier)
                dungeonStacks!!.add(loot)
            } else {
                dungeonStacks = ArrayList<ItemStack?>()
                dungeonStacks.add(loot)
            }

            dungeonLootMap.put(tier, dungeonStacks)
        }

        @JvmStatic
        fun addT1RocketRecipe(recipe: INasaWorkbenchRecipe?) {
            rocketBenchT1Recipes.add(recipe)
        }

        @JvmStatic
        fun addT2RocketRecipe(recipe: INasaWorkbenchRecipe?) {
            rocketBenchT2Recipes.add(recipe)
        }

        @JvmStatic
        fun addT3RocketRecipe(recipe: INasaWorkbenchRecipe?) {
            rocketBenchT3Recipes.add(recipe)
        }

        @JvmStatic
        fun addCargoRocketRecipe(recipe: INasaWorkbenchRecipe?) {
            cargoRocketRecipes.add(recipe)
        }

        @JvmStatic
        fun addMoonBuggyRecipe(recipe: INasaWorkbenchRecipe?) {
            buggyBenchRecipes.add(recipe)
        }

        @JvmStatic
        fun addAstroMinerRecipe(recipe: INasaWorkbenchRecipe?) {
            astroMinerRecipes.add(recipe)
        }

        @JvmStatic
        fun removeT1RocketRecipe(recipe: INasaWorkbenchRecipe?) {
            rocketBenchT1Recipes.remove(recipe)
        }

        @JvmStatic
        fun removeT2RocketRecipe(recipe: INasaWorkbenchRecipe?) {
            rocketBenchT2Recipes.remove(recipe)
        }

        @JvmStatic
        fun removeT3RocketRecipe(recipe: INasaWorkbenchRecipe?) {
            rocketBenchT3Recipes.remove(recipe)
        }

        @JvmStatic
        fun removeCargoRocketRecipe(recipe: INasaWorkbenchRecipe?) {
            cargoRocketRecipes.remove(recipe)
        }

        @JvmStatic
        fun removeMoonBuggyRecipe(recipe: INasaWorkbenchRecipe?) {
            buggyBenchRecipes.remove(recipe)
        }

        @JvmStatic
        fun removeAstroMinerRecipe(recipe: INasaWorkbenchRecipe?) {
            astroMinerRecipes.remove(recipe)
        }

        @JvmStatic
        fun removeAllT1RocketRecipes() {
            rocketBenchT1Recipes.clear()
        }

        @JvmStatic
        fun removeAllT2RocketRecipes() {
            rocketBenchT2Recipes.clear()
        }

        @JvmStatic
        fun removeAllT3RocketRecipes() {
            rocketBenchT3Recipes.clear()
        }

        @JvmStatic
        fun removeAllCargoRocketRecipes() {
            cargoRocketRecipes.clear()
        }

        @JvmStatic
        fun removeAllMoonBuggyRecipes() {
            buggyBenchRecipes.clear()
        }

        @JvmStatic
        fun removeAllAstroMinerRecipes() {
            astroMinerRecipes.clear()
        }

        @JvmStatic
        fun getTeleportTypeForDimension(clazz: Class<out WorldProvider>?): ITeleportType? {
            var clazz: Class<out WorldProvider>? = clazz
            if (!IGalacticraftWorldProvider::class.java.isAssignableFrom(clazz)) {
                clazz = WorldProviderSurface::class.java
            }
            return teleportTypeMap.get(clazz)
        }

        @JvmStatic
        fun registerSpaceStation(type: SpaceStationType) {
            for (type1: SpaceStationType in spaceStations) {
                if (type1.getWorldToOrbitID() == type.getWorldToOrbitID()) {
                    throw RuntimeException("Two space station types registered with the same home planet ID: " + type.getWorldToOrbitID())
                }
            }

            spaceStations.add(type)
        }

        @JvmStatic
        fun replaceSpaceStationRecipe(spaceStationID: Int, obj: HashMap<Any?,Int>) {
            for (type1: SpaceStationType in spaceStations) {
                if (type1.getSpaceStationID() == spaceStationID) {
                    type1.setRecipeForSpaceStation(SpaceStationRecipe(obj))
                }
            }
        }

        @JvmStatic
        fun getSpaceStationData(): MutableList<SpaceStationType> {
            return spaceStations
        }

        @JvmStatic
        fun getRocketT1Recipes(): MutableList<INasaWorkbenchRecipe?> {
            return rocketBenchT1Recipes
        }

        @JvmStatic
        fun getRocketT2Recipes(): MutableList<INasaWorkbenchRecipe?> {
            return rocketBenchT2Recipes
        }

        @JvmStatic
        fun getRocketT3Recipes(): MutableList<INasaWorkbenchRecipe?> {
            return rocketBenchT3Recipes
        }

        @JvmStatic
        fun getCargoRocketRecipes(): MutableList<INasaWorkbenchRecipe?> {
            return cargoRocketRecipes
        }

        @JvmStatic
        fun getBuggyBenchRecipes(): MutableList<INasaWorkbenchRecipe?> {
            return buggyBenchRecipes
        }

        @JvmStatic
        fun getAstroMinerRecipes(): MutableList<INasaWorkbenchRecipe?> {
            return astroMinerRecipes
        }

        @JvmStatic
        @SideOnly(Side.CLIENT)
        fun getResouceLocationForDimension(clazz: Class<out WorldProvider>?): ResourceLocation? {
            var clazz: Class<out WorldProvider>? = clazz
            if (!IGalacticraftWorldProvider::class.java.isAssignableFrom(clazz)) {
                clazz = WorldProviderSurface::class.java
            }
            return rocketGuiMap.get(clazz)
        }

        @JvmStatic
        fun getDungeonLoot(tier: Int): MutableList<ItemStack?> {
            return dungeonLootMap.get(tier)!!
        }

        /***
         * Register a Galacticraft dimension
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun registerDimension(
            name: String,
            suffix: String?,
            id: Int,
            provider: Class<out WorldProvider>,
            keepLoaded: Boolean
        ): DimensionType? {
            for (other: DimensionType in DimensionType.values()) {
                if (other.getId() == id) {
                    return null
                }
            }

            val type: DimensionType = DimensionType.register(name, suffix, id, provider, keepLoaded)
            dimensionTypeIDs.add(if (type == null) 0 else id)
            if (type == null) {
                FMLRelaunchLog.log(
                    "Galacticraft",
                    Level.ERROR,
                    "Problem registering dimension type " + id + ".  May be fixable by changing config."
                )
            }

            return type
        }

        @JvmStatic
        fun getDimensionTypeID(index: Int): Int {
            return dimensionTypeIDs.get(index)!!
        }

        @JvmStatic
        fun isDimensionTypeIDRegistered(typeId: Int): Boolean {
            return dimensionTypeIDs.contains(typeId)
        }

        /**
         * Register an IGameScreen so the Display Screen can access it
         *
         * @param screen  The IGameScreen to be registered
         * @return   The type ID assigned to this screen type
         */
        @JvmStatic
        fun registerScreen(screen: IGameScreen): Int {
            gameScreens.add(screen)
            maxScreenTypes++
            screen.setFrameSize(0.098f)
            return maxScreenTypes - 1
        }

        @JvmStatic
        fun registerScreensServer(maxTypes: Int) {
            maxScreenTypes = maxTypes
        }

        @JvmStatic
        fun getMaxScreenTypes(): Int {
            return maxScreenTypes
        }

        @JvmStatic
        fun getGameScreen(type: Int): IGameScreen? {
            return gameScreens.get(type)
        }

        /**
         * Adds a custom item for 'extended inventory' slots
         *
         * Gear IDs must be unique, and should be configurable for user convenience
         *
         * Please do not use values less than 100, to avoid conflicts with future Galacticraft core additions
         *
         * @param gearID Unique ID for this gear item, please use values greater than 100
         * @param type Slot this item can be placed in
         * @param item Item to register, not metadata-sensitive
         */
        @JvmStatic
        fun registerGear(gearID: Int, type: EnumExtendedInventorySlot?, item: Item?) {
            addGearObject(gearID, type, item)
        }

        /**
         * Adds a custom item for 'extended inventory' slots
         *
         * Gear IDs must be unique, and should be configurable for user convenience
         *
         * Please do not use values less than 100, to avoid conflicts with future Galacticraft core additions
         *
         * @param gearID Unique ID for this gear item, please use values greater than 100
         * @param type Slot this item can be placed in
         * @param itemStack ItemStack to register, metadata-sensitive
         */
        @JvmStatic
        fun registerGear(gearID: Int, type: EnumExtendedInventorySlot?, itemStack: ItemStack?) {
            addGearObject(gearID, type, itemStack)
        }

        @JvmStatic
        private fun addGearObject(gearID: Int, type: EnumExtendedInventorySlot?, obj: Any?) {
            if (gearMap.containsKey(gearID)) {
                if (!gearMap.get(gearID)!!.contains(obj)) {
                    gearMap.get(gearID)!!.add(obj)
                }
            } else {
                val gear: MutableList<Any?> = Lists.newArrayList<Any?>()
                gear.add(obj)
                gearMap.put(gearID, gear)
            }

            if (gearSlotMap.containsKey(gearID)) {
                if (!gearSlotMap.get(gearID)!!.contains(type)) {
                    gearSlotMap.get(gearID)!!.add((type)!!)
                }
            } else {
                val gearType: MutableList<EnumExtendedInventorySlot> = Lists.newArrayList<EnumExtendedInventorySlot>()
                gearType.add((type)!!)
                gearSlotMap.put(gearID, gearType)
            }
        }

        @JvmStatic
        fun listAllGearForSlot(slotType: EnumExtendedInventorySlot?): MutableList<ItemStack?> {
            val result: MutableList<ItemStack?> = LinkedList<ItemStack?>()
            for (entry in gearMap.entries) {
                val slotType1: MutableList<EnumExtendedInventorySlot> = Companion.getSlotType(entry.key!!)
                if (slotType1.contains(slotType)) {
                    val objectList: MutableList<Any?> = (entry.value)!!
                    for (o: Any? in objectList) {
                        if (o is ItemStack) {
                            result.add(o)
                        } else if (o is Item) {
                            result.add(ItemStack(o))
                        }
                    }
                }
            }
            return result
        }

        @JvmStatic
        fun findMatchingGearID(stack: ItemStack, slotType: EnumExtendedInventorySlot?): Int {
            for (entry in gearMap.entries) {
                val slotType1: MutableList<EnumExtendedInventorySlot> = Companion.getSlotType(entry.key!!)
                val objectList: MutableList<Any?> = (entry.value)!!

                if (!slotType1.contains(slotType)) {
                    continue
                }

                for (o: Any? in objectList) {
                    if (o is Item) {
                        if (stack.getItem() === o) {
                            return entry.key!!
                        }
                    } else if (o is ItemStack) {
                        if (stack.getItem() === o.getItem() && stack.getItemDamage() == o.getItemDamage()) {
                            return entry.key!!
                        }
                    }
                }
            }

            return -1
        }

        @JvmStatic
        fun getSlotType(gearID: Int): MutableList<EnumExtendedInventorySlot> {
            return gearSlotMap.get(gearID)!!
        }

        @JvmStatic
        @Deprecated("")
                /** Grating will now register fluids automatically if they extend BlockFluidBase
                 *
                 */
        fun registerGratingFluid(fluidBlock: Block?) {
        }
    }
}
