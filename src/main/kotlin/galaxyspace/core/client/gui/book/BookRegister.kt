package galaxyspace.core.client.gui.book

import asmodeuscore.AsmodeusCore
import asmodeuscore.core.astronomy.gui.book.Page_WithCraftMatrix.Recipe_Type
import asmodeuscore.core.utils.BookUtils
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.GalaxySpace
import galaxyspace.core.GSBlocks
import galaxyspace.core.client.gui.book.pages.bodies.Page_BarnardsSystem
import galaxyspace.core.client.gui.book.pages.bodies.Page_ProximaSystem
import galaxyspace.core.client.gui.book.pages.bodies.Page_TauCetiSystem
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigCore
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.TauCetiSystem.core.configs.TCConfigCore
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.common.util.EnumHelper

object BookRegister {
    @JvmField
    var starTexture: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/book/star_bg.png")

    @JvmField
    var guiTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AsmodeusCore.ASSET_PREFIX, "textures/gui/gui.png")


    @JvmField
    var ASSEMBLER: Recipe_Type? = EnumHelper.addEnum<Recipe_Type>(
        Recipe_Type::class.java,
        "ASSEMBLER",
        arrayOf<Class<*>>(ItemStack::class.java),
        ItemStack(GSBlocks.ASSEMBLER)
    )

    @JvmField
    var ROCKET_ASSEMBLER: Recipe_Type? = EnumHelper.addEnum<Recipe_Type>(
        Recipe_Type::class.java,
        "ROCKET_ASSEMBLER",
        arrayOf<Class<*>>(ItemStack::class.java),
        ItemStack(GSBlocks.ROCKET_ASSEMBLER)
    )

    @JvmField
    var UNIVERSAL_RECYCLER: Recipe_Type? = EnumHelper.addEnum<Recipe_Type>(
        Recipe_Type::class.java,
        "UNIVERSAL_RECYCLER",
        arrayOf<Class<*>>(ItemStack::class.java),
        ItemStack(GSBlocks.UNIVERSAL_RECYCLER)
    )

    @JvmField
    var SEPARATOR: Recipe_Type? = EnumHelper.addEnum<Recipe_Type>(
        Recipe_Type::class.java,
        "SEPARATOR",
        arrayOf<Class<*>>(ItemStack::class.java),
        ItemStack(GSBlocks.LIQUID_SEPARATOR)
    )

    @JvmStatic
    fun registerCatergories() {
        BookUtils.addGuideBookCategory(Book_Cateroies.GENERAL.getName(), ResourceLocation.parse("textures/items/apple.png"))
        BookUtils.addGuideBookCategory(Book_Cateroies.BODIES.getName(), (SolarSystemBodies.planetNeptune)!!.getBodyIcon())
        BookUtils.addGuideBookCategory(
            Book_Cateroies.BLOCKS.getName(),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/blocks/overworld/hydroponic_base.png")
        )
        BookUtils.addGuideBookCategory(
            Book_Cateroies.ITEMS.getName(),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/items/basic/volcanic_stone.png")
        )
        BookUtils.addGuideBookCategory(
            Book_Cateroies.MECHANICS.getName(),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/items/basic/temp_shield_control.png")
        )

        if (ACConfigCore.enableACentauriSystems) BookUtils.addGuideBookPage(Page_ProximaSystem())
        if (BRConfigCore.enableBarnardsSystems) BookUtils.addGuideBookPage(Page_BarnardsSystem())
        if (TCConfigCore.enableTauCetiSystems) BookUtils.addGuideBookPage(Page_TauCetiSystem())
    }
}
