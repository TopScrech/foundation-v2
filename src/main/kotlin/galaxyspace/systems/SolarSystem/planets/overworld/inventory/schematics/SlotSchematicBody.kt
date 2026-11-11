package galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.SlotSchematicBody
import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos

open class SlotSchematicBody constructor(
    par2IInventory: IInventory,
    private val index: Int,
    par4: Int,
    par5: Int,
    private val pos: BlockPos,
    private val player: EntityPlayer?
) : Slot(par2IInventory, index, par4, par5) {
    override fun setChanged() {
        super.setChanged()
        if (this.player is EntityPlayerMP) {
            val dimID: Int = GCCoreUtil.getDimensionID(this.player.level())
            GCCoreUtil.sendToAllAround(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_SPAWN_SPARK_PARTICLES,
                    dimID,
                    arrayOf<Any?>(this.pos)
                ), this.player.level(), dimID, this.pos, 20.0
            )
        }
    }

    override fun mayPlace(par1ItemStack: ItemStack): Boolean {
        if (par1ItemStack == null) return false

        val recipes: MutableList<INasaWorkbenchRecipe> = (GSRecipeUtil.getBodyRecipes() as MutableList<INasaWorkbenchRecipe>)
        for (recipe: INasaWorkbenchRecipe in recipes) {
            if (NasaWorkbenchRecipe.matchesIngredient(par1ItemStack, (recipe.getRecipeInput())!!.get(this.index))) return true
        }
        return false
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    override fun getMaxStackSize(): Int {
        return 1
    }
}
