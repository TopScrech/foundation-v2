package micdoodle8.mods.galacticraft.core.items

import com.mojang.blaze3d.platform.InputConstants
import foundation.item.FoundationItemVariants
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.util.Optional
import java.util.function.Consumer

abstract class ItemDesc(properties: Item.Properties) : Item(properties), IShiftDescription {
    @OnlyIn(Dist.CLIENT)
    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val metadata = FoundationItemVariants.metadata(stack)
        if (!showDescription(metadata)) return
        val client = Minecraft.getInstance()
        if (InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)) {
            val description = Component.literal(getShiftDescription(metadata).orEmpty())
            for (line in client.font.splitter.splitLines(description, 150, Style.EMPTY)) {
                val component = Component.empty()
                line.visit(FormattedText.StyledContentConsumer<Unit> { style, text ->
                    component.append(Component.literal(text).setStyle(style))
                    Optional.empty()
                }, Style.EMPTY)
                builder.accept(component)
            }
        } else {
            builder.accept(Component.translatable("item_desc.shift.name", client.options.keyShift.translatedKeyMessage))
        }
    }
}
