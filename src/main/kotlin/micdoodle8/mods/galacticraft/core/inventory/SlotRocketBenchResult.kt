package micdoodle8.mods.galacticraft.core.inventory

import foundation.inventory.FoundationCraftingConsumption
import net.minecraft.util.Prediction
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.ForgeHooks

open class SlotRocketBenchResult(
    private val thePlayer: Player,
    private val craftMatrix: Container,
    result: Container,
    slot: Int,
    x: Int,
    y: Int
) : Slot(result, slot, x, y) {
    override fun mayPlace(stack: ItemStack): Boolean = false

    override fun onTake(player: Player, stack: ItemStack) {
        val previousPlayer = ForgeHooks.getCraftingPlayer()
        ForgeHooks.setCraftingPlayer(player)
        try {
            FoundationCraftingConsumption.consume(
                craftMatrix,
                insert = { thePlayer.inventory.add(it) },
                drop = { thePlayer.drop(it, false, Prediction.PREDICTED) }
            )
        } finally {
            ForgeHooks.setCraftingPlayer(previousPlayer)
        }
        super.onTake(player, stack)
    }
}
