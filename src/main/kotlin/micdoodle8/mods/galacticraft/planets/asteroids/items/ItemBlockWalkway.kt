package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.BlockWalkway
import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockWalkway constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        itemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (itemStack.getItemDamage() == BlockWalkway.EnumWalkwayType.WALKWAY_WIRE.getMeta()) {
            tooltip.add(EnumColor.AQUA.toString() + GCCoreUtil.translate("tile.aluminum_wire.alu_wire.name"))
        } else if (itemStack.getItemDamage() == BlockWalkway.EnumWalkwayType.WALKWAY_PIPE.getMeta()) {
            tooltip.add(EnumColor.AQUA.toString() + GCCoreUtil.translate((GCBlocks.oxygenPipe)!!.getTranslationKey() + ".name"))
        }

        super.addInformation(itemStack, worldIn, tooltip, flagIn)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    public override fun getTranslationKey(itemstack: ItemStack): String {
        val name: String =
            micdoodle8.mods.galacticraft.planets.asteroids.blocks.BlockWalkway.EnumWalkwayType.entries.toTypedArray()[itemstack.getItemDamage()].getName()
        return this.getBlock().getTranslationKey() + "." + name
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
