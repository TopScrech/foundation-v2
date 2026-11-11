package micdoodle8.mods.galacticraft.planets.venus.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.items.ItemKey
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Rarity

open class ItemKeyVenus(name: String?) : ItemKey(
    FoundationItemProperties.named("galacticraftplanets", requireNotNull(name)).stacksTo(1).rarity(Rarity.RARE)
        .overrideDescription("item.key.t3.name"), 3
)
