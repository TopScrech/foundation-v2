package micdoodle8.mods.galacticraft.api.recipe

import micdoodle8.mods.galacticraft.api.recipe.SchematicEvent.FlipPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicEvent.Unlock

import org.apache.logging.log4j.LogManager
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList

object SchematicRegistry {
    private val logger = LogManager.getLogger("Galacticraft")
    @JvmField
    var schematicRecipes: ArrayList<ISchematicPage> = ArrayList<ISchematicPage>()

    @JvmField
    var schematicItems: ArrayList<ItemStack?> = ArrayList<ItemStack?>()

    @JvmField
    var textures: ArrayList<ResourceLocation?> = ArrayList<ResourceLocation?>()

    /**
     * Register a new schematic page
     */
    @JvmStatic
    fun registerSchematicRecipe(page: ISchematicPage) {
        if (!schematicRecipes.contains(page)) {
            schematicRecipes.add(page)
        }
    }

    /**
     * Finds the recipe for the given itemstack
     *
     * @param stack the itemstack to be searched with
     * @return the recipe that requires the provided itemstack
     */
    @JvmStatic
    fun getMatchingRecipeForItemStack(stack: ItemStack?): ISchematicPage? {
        for (schematic: ISchematicPage in schematicRecipes) {
            val requiredItem: ItemStack? = schematic.getRequiredItem()

            if (requiredItem != null && stack != null && !requiredItem.isEmpty && !stack.isEmpty && foundation.item.FoundationItemVariants.sameVariant(requiredItem, stack)) {
                return schematic
            }
        }

        return null
    }

    /**
     * Finds the recipe for the given recipe ID
     *
     * @param id the ID to be searched with
     * @return the recipe that has and ID equal to the one provided
     */
    @JvmStatic
    fun getMatchingRecipeForID(id: Int): ISchematicPage? {
        for (schematic: ISchematicPage in schematicRecipes) {
            if (schematic.getPageID() == id) {
                return schematic
            }
        }

        return null
    }

    /**
     * Called when a player unlocks a page. Used internally.
     *
     * @param player the player that unlocked the schematic
     * @param page   the schematic page to be unlocked
     */
    @JvmStatic
    fun addUnlockedPage(player: EntityPlayerMP?, page: ISchematicPage?) {
        // Used internally to add page to player's list of unlocked schematics.
        // No need to subscribe to this event
        if (page != null) SchematicEvent.Unlock(player, page).post()
    }

    /**
     * Called when a player unlocks a page. Used internally.
     *
     * @param player the player that unlocked the schematic
     * @param stack  the itemstack the player has provided
     * @return the schematic page that was unlocked
     */
    @JvmStatic
    fun unlockNewPage(player: EntityPlayerMP?, stack: ItemStack?): ISchematicPage? {
        if (stack != null) {
            val schematic: ISchematicPage? = getMatchingRecipeForItemStack(stack)

            if (schematic != null) {
                addUnlockedPage(player, schematic)

                return schematic
            }
        }

        return null
    }

    /**
     * Finds the correct schematic when player presses NEXT
     *
     * @param currentIndex the current index of unlocked schematics the player is viewing
     * @return the schematic page that will be shown when the player clicks NEXT
     */
    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun flipToNextPage(cs: GuiScreen?, currentIndex: Int) {
        Minecraft.getInstance().gui.setScreen(null)

        // Used internally inside Galacticraft to flip to the next page. No need
        // to subscribe to this event.
        SchematicEvent.FlipPage(cs, null, currentIndex, 1).post()
    }

    /**
     * Finds the correct schematic when player presses BACK
     *
     * @param currentIndex the current index of unlocked schematics the player is viewing
     * @return the schematic page that will be shown when the player clicks BACK
     */
    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun flipToLastPage(cs: GuiScreen?, currentIndex: Int) {
        Minecraft.getInstance().gui.setScreen(null)

        // Used internally inside Galacticraft to flip to the last page. No need
        // to subscribe to this event.
        SchematicEvent.FlipPage(cs, null, currentIndex, -1).post()
    }

    @JvmStatic
    fun registerTexture(loc: ResourceLocation?) {
        textures.add(loc)
    }

    @JvmStatic
    fun getSchematicTexture(index: Int): ResourceLocation? {
        if (index in textures.indices) return textures[index]

        logger.debug("couldn't find render texture for " + index)
        return textures.firstOrNull()
    }

    @JvmStatic
    fun registerSchematicItem(item: ItemStack?): Int {
        val index: Int = schematicItems.size
        schematicItems.add(item)
        return index
    }

    @JvmStatic
    fun getSchematicItem(index: Int): ItemStack {
        if (index in schematicItems.indices) return schematicItems[index]?.copy() ?: ItemStack.EMPTY

        logger.debug("couldn't find schematic item for " + index)
        return schematicItems.firstOrNull()?.copy() ?: ItemStack.EMPTY
    }
}
