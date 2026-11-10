package galaxyspace.systems.SolarSystem.planets.overworld.schematics

import galaxyspace.core.GSItems
import galaxyspace.core.configs.GSConfigSchematics
import galaxyspace.systems.SolarSystem.planets.overworld.gui.schematics.GuiSchematicCone
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.ContainerSchematicCone
import micdoodle8.mods.galacticraft.api.recipe.SchematicPage
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class SchematicCone : SchematicPage() {
    override fun getPageID(): Int {
        return GSConfigSchematics.idSchematicCone
    }

    override fun getGuiID(): Int {
        return this.getPageID() + Constants.MOD_ID_PLANETS.hashCode()
    }

    override fun getRequiredItem(): ItemStack {
        return ItemStack(GSItems.SCHEMATICS, 1, 0)
    }

    @SideOnly(Side.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicCone((player)!!.inventory, pos)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerSchematicCone((player)!!.inventory, pos)
    }
}
