package galaxyspace.systems.SolarSystem.planets.overworld.items


import galaxyspace.api.item.IModificationItem
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.EPP
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import net.minecraft.world.item.ItemStack

open class ItemGSOxygenTank constructor(tier: Int, assetName: String, damage: Int, epp: Boolean) :
    ItemOxygenTank(
        foundation.item.FoundationItemProperties.named("galaxyspace", assetName).stacksTo(1)
            .rarity(net.minecraft.world.item.Rarity.RARE)
            .overrideDescription("item.$assetName.name"),
        damage
    ), IModificationItem {
    private var epp = false

    init {
        this.epp = epp
    }

    override fun appendHoverText(
        stack: ItemStack, context: net.minecraft.world.item.Item.TooltipContext,
        display: net.minecraft.world.item.component.TooltipDisplay,
        builder: java.util.function.Consumer<net.minecraft.network.chat.Component>, flag: net.minecraft.world.item.TooltipFlag
    ) {
        if (epp || stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA)?.copyTag()?.contains("epp") == true) {
            for (key in listOf("gui.tank.passive_oxygen_regen.desc1", "gui.tank.passive_oxygen_regen.desc2")) {
                builder.accept(net.minecraft.network.chat.Component.translatable(key).withStyle(net.minecraft.ChatFormatting.DARK_GREEN))
            }
        }
        super.appendHoverText(stack, context, display, builder, flag)
    }

    override fun getType(stack: ItemStack?): Module_Type? {
        if ((stack)!!.getItem() !== GSItems.OXYGENTANK_TIER_EPP) return Module_Type.OXYGEN_TANK

        return null
    }

    override fun getAvailableModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(EPP()) as Array<ItemModule?>?))!!
    }

    override fun getModificationCount(stack: ItemStack?): Int {
        return 1
    }
}
