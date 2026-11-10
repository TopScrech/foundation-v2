package micdoodle8.mods.galacticraft.core.schematic

import micdoodle8.mods.galacticraft.api.recipe.SchematicPage
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiSchematicInput
import micdoodle8.mods.galacticraft.core.inventory.ContainerSchematic
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class SchematicAdd : SchematicPage() {
    override fun getPageID(): Int {
        return ConfigManagerCore.idSchematicAddSchematic
    }

    override fun getGuiID(): Int {
        return GuiIdsCore.NASA_WORKBENCH_NEW_SCHEMATIC
    }

    override fun getRequiredItem(): ItemStack? {
        return null //This null is OK, it's used only as a flag by SchematicRegistry calling code
    }

    @SideOnly(Side.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicInput((player)!!.inventory, (pos)!!)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerSchematic((player)!!.inventory, pos)
    }
}
