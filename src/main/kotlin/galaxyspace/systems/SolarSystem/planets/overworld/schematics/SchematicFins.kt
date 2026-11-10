package galaxyspace.systems.SolarSystem.planets.overworld.schematics

import galaxyspace.core.GSItems
import galaxyspace.core.configs.GSConfigSchematics
import galaxyspace.systems.SolarSystem.planets.overworld.gui.schematics.GuiSchematicFins
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.ContainerSchematicFins
import micdoodle8.mods.galacticraft.api.recipe.SchematicPage
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class SchematicFins : SchematicPage() {
    override fun getPageID(): Int {
        return GSConfigSchematics.idSchematicFins
    }

    override fun getGuiID(): Int {
        return this.getPageID()
    }

    override fun getRequiredItem(): ItemStack {
        return ItemStack(GSItems.SCHEMATICS, 1, 4)
    }

    @SideOnly(Side.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicFins((player)!!.inventory, pos)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerSchematicFins((player)!!.inventory, pos)
    }
}
