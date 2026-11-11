package micdoodle8.mods.galacticraft.core.schematic

import micdoodle8.mods.galacticraft.api.recipe.SchematicPage
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiSchematicTier1Rocket
import micdoodle8.mods.galacticraft.core.inventory.ContainerSchematicTier1Rocket
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class SchematicRocketT1 : SchematicPage() {
    override fun getPageID(): Int {
        return ConfigManagerCore.idSchematicRocketT1
    }

    override fun getGuiID(): Int {
        return GuiIdsCore.NASA_WORKBENCH_ROCKET
    }

    override fun getRequiredItem(): ItemStack? {
        return null //This null is OK, it's used only as a flag by the calling code in SchematicRegistry
    }

    @OnlyIn(Dist.CLIENT)
    override fun getResultScreen(player: EntityPlayer?, pos: BlockPos?): GuiScreen {
        return GuiSchematicTier1Rocket((player)!!.inventory, (pos)!!)
    }

    override fun getResultContainer(player: EntityPlayer?, pos: BlockPos?): Container {
        return ContainerSchematicTier1Rocket((player)!!.inventory, pos)
    }
}
