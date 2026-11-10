package micdoodle8.mods.galacticraft.planets.mars.schematic

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiSchematicTier2Rocket
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerSchematicTier2Rocket
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class SchematicTier2Rocket : ISchematicPage {
    override fun getPageID(): Int {
        return ConfigManagerMars.idSchematicRocketT2
    }

    override fun getGuiID(): Int {
        return GuiIdsPlanets.NASA_WORKBENCH_TIER_2_ROCKET + Constants.MOD_ID_PLANETS.hashCode()
    }

    override fun getRequiredItem(): ItemStack {
        return ItemStack(GCItems.schematic, 1, 1)
    }

    @SideOnly(Side.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicTier2Rocket((player)!!.inventory, (pos)!!)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerSchematicTier2Rocket((player)!!.inventory, pos)
    }

    override fun compareTo(o: ISchematicPage): Int {
        if (this.getPageID() > o.getPageID()) {
            return 1
        } else {
            return -1
        }
    }
}
