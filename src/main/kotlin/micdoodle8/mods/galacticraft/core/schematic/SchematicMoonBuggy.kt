package micdoodle8.mods.galacticraft.core.schematic

import micdoodle8.mods.galacticraft.api.recipe.SchematicPage
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiSchematicBuggy
import micdoodle8.mods.galacticraft.core.inventory.ContainerBuggyBench
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class SchematicMoonBuggy : SchematicPage() {
    override fun getPageID(): Int {
        return ConfigManagerCore.idSchematicMoonBuggy
    }

    override fun getGuiID(): Int {
        return GuiIdsCore.NASA_WORKBENCH_BUGGY
    }

    override fun getRequiredItem(): ItemStack {
        return ItemStack(GCItems.schematic, 1, 0)
    }

    @SideOnly(Side.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicBuggy((player)!!.inventory, (pos)!!)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerBuggyBench((player)!!.inventory, pos, player)
    }
}
