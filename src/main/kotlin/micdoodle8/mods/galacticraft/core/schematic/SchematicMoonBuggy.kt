package micdoodle8.mods.galacticraft.core.schematic

import micdoodle8.mods.galacticraft.api.recipe.SchematicPage
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiSchematicBuggy
import micdoodle8.mods.galacticraft.core.inventory.ContainerBuggyBench
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class SchematicMoonBuggy : SchematicPage() {
    override fun getPageID(): Int {
        return ConfigManagerCore.idSchematicMoonBuggy
    }

    override fun getGuiID(): Int {
        return GuiIdsCore.NASA_WORKBENCH_BUGGY
    }

    override fun getRequiredItem(): ItemStack {
        return foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicBuggy((player)!!.inventory, (pos)!!)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerBuggyBench((player)!!.inventory, pos, player)
    }
}
