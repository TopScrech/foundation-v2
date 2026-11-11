package galaxyspace.systems.SolarSystem.planets.overworld.schematics

import galaxyspace.core.GSItems
import galaxyspace.core.configs.GSConfigSchematics
import galaxyspace.systems.SolarSystem.planets.overworld.gui.schematics.GuiSchematicBody
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.ContainerSchematicBody
import micdoodle8.mods.galacticraft.api.recipe.SchematicPage
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class SchematicBody : SchematicPage() {
    override fun getPageID(): Int {
        return GSConfigSchematics.idSchematicBody
    }

    override fun getGuiID(): Int {
        return this.getPageID()
    }

    override fun getRequiredItem(): ItemStack {
        return foundation.item.FoundationItemVariants.stack(requireNotNull(GSItems.SCHEMATICS), 1, 1)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicBody((player)!!.inventory, pos)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerSchematicBody((player)!!.inventory, pos)
    }
}
