package micdoodle8.mods.galacticraft.planets.mars.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.items.ItemKey
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Rarity

open class ItemKeyMars : ItemKey(
    FoundationItemProperties.named("galacticraftplanets", "key").stacksTo(1).rarity(Rarity.RARE)
        .overrideDescription("item.key.t2.name"), 2
)
