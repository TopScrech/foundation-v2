package micdoodle8.mods.galacticraft.api.entity

import javax.annotation.Nonnull

import net.minecraft.item.ItemStack

/**
 * Implement into entities that can be loaded with cargo
 */
interface ICargoEntity {
    enum class EnumCargoLoadingState {
        FULL,
        EMPTY,
        NOTARGET,
        NOINVENTORY,
        SUCCESS
    }

    class RemovalResult
    constructor(
        @JvmField val resultState: EnumCargoLoadingState?,
        @field:Nonnull @JvmField @param:Nonnull val resultStack: ItemStack
    )

    fun addCargo(stack: ItemStack?, doAdd: Boolean): EnumCargoLoadingState?

    fun removeCargo(doRemove: Boolean): RemovalResult?
}
