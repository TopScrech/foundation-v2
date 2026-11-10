package micdoodle8.mods.galacticraft.planets.mars.inventory

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

open class SlotSchematicTier2Rocket constructor(
    par2IInventory: IInventory,
    private val index: Int,
    par4: Int,
    par5: Int,
    private val pos: BlockPos,
    private val player: EntityPlayer?
) : Slot(par2IInventory, index, par4, par5) {
    override fun onSlotChanged() {
        if (this.player is EntityPlayerMP) {
            val dimID: Int = GCCoreUtil.getDimensionID(this.player.world)
            GCCoreUtil.sendToAllAround(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_SPAWN_SPARK_PARTICLES,
                    dimID,
                    arrayOf<Any?>(this.pos)
                ), this.player.world, dimID, this.pos, 20.0
            )
        }
    }

    override fun isItemValid(par1ItemStack: ItemStack): Boolean {
        if (par1ItemStack == null) return false

        val recipes: MutableList<INasaWorkbenchRecipe> = (GalacticraftRegistry.getRocketT2Recipes() as MutableList<INasaWorkbenchRecipe>)
        for (recipe: INasaWorkbenchRecipe in recipes) {
            if (ItemStack.areItemsEqual(par1ItemStack, (recipe.getRecipeInput())!!.get(this.index))) return true
        }
        return false
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    override fun getSlotStackLimit(): Int {
        return 1
    }
}
