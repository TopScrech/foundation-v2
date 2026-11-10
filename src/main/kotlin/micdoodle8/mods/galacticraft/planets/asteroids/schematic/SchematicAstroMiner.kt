package micdoodle8.mods.galacticraft.planets.asteroids.schematic

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.GuiSchematicAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerSchematicAstroMiner
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class SchematicAstroMiner : ISchematicPage {
    override fun getPageID(): Int {
        return ConfigManagerAsteroids.idSchematicRocketT3 + 1
    }

    override fun getGuiID(): Int {
        return GuiIdsPlanets.NASA_WORKBENCH_ASTRO_MINER + Constants.MOD_ID_PLANETS.hashCode()
    }

    override fun getRequiredItem(): ItemStack {
        return ItemStack(MarsItems.schematic, 1, 2)
    }

    @SideOnly(Side.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicAstroMiner((player)!!.inventory, (pos)!!)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerSchematicAstroMiner((player)!!.inventory, pos)
    }

    override fun compareTo(o: ISchematicPage): Int {
        if (this.getPageID() > o.getPageID()) {
            return 1
        } else {
            return -1
        }
    }
}
