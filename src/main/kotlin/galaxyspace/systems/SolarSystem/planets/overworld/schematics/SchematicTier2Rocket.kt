package galaxyspace.systems.SolarSystem.planets.overworld.schematics

import galaxyspace.systems.SolarSystem.planets.overworld.gui.schematics.GuiSchematicTier2Rocket
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.ContainerSchematicTier2Rocket
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class SchematicTier2Rocket : ISchematicPage {
    override fun getPageID(): Int {
        return ConfigManagerMars.idSchematicRocketT2
    }

    override fun getGuiID(): Int {
        return GuiIdsPlanets.NASA_WORKBENCH_TIER_2_ROCKET + Constants.MOD_ID_PLANETS.hashCode()
    }

    override fun getRequiredItem(): ItemStack {
        return foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicTier2Rocket((player)!!.inventory, pos)
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
