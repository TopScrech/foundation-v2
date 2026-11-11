package micdoodle8.mods.galacticraft.planets.asteroids.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationTitaniumTools
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

open class ItemSwordAsteroids(properties: Item.Properties) : Item(properties), ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftplanets", requireNotNull(assetName))
            .sword(FoundationTitaniumTools.BASE_MATERIAL, 3f, -2.4f).rarity(Rarity.RARE)
            .overrideDescription("item.$assetName.name")
    )

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.TOOLS
    // Legacy public accessor differed from the effective attack modifier used by the engine
    fun getAttackDamage(): Float = 6f

    override fun mineBlock(stack: ItemStack, level: Level, state: BlockState, pos: BlockPos, owner: LivingEntity): Boolean {
        val damage = blockDamage(state.getDestroySpeed(level, pos))
        if (!level.isClientSide && damage > 0) stack.hurtAndBreak(damage, owner, EquipmentSlot.MAINHAND)
        return true
    }

    companion object {
        @JvmStatic fun blockDamage(hardness: Float): Int = when {
            hardness <= 0f || hardness.isNaN() -> 0
            hardness > 0.2001f -> 2
            else -> 1
        }
    }
}
