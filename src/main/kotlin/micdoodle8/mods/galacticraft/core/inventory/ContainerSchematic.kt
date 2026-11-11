package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.recipe.ISchematicItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class ContainerSchematic constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) : Container() {
    @JvmField
    var craftMatrix: InventorySchematic = InventorySchematic(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val world: World

    init {
        this.world = par1InventoryPlayer.player.world
        this.addSlot(SlotSpecific(this.craftMatrix, 0, 80, 1, ISchematicItem::class.java))
        var var6: Int
        var var7: Int

        // Player inv:
        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 9) {
                this.addSlot(
                    Slot(
                        par1InventoryPlayer,
                        var7 + var6 * 9 + 9,
                        8 + var7 * 18,
                        111 + var6 * 18 - 59 + 16
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 169 - 59 + 16))
            ++var6
        }

        this.slotsChanged(this.craftMatrix)
    }

    override fun removed(par1EntityPlayer: EntityPlayer) {
        super.removed(par1EntityPlayer)

        if (!this.world.isClientSide) {
            for (var2: Int in 0..0) {
                val var3: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!var3.isEmpty()) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f)
                }
            }
        }
    }

    override fun stillValid(entityplayer: EntityPlayer): Boolean {
        return true
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par2: Int): ItemStack {
        var var3: ItemStack = ItemStack.EMPTY
        val var4: Slot? = this.inventorySlots.get(par2)

        if (var4 != null && var4.hasItem()) {
            val var5: ItemStack = var4.getItem()
            var3 = var5.copy()

            if (par2 < 1) {
                if (!this.moveItemStackTo(var5, 1, this.inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(var5, 0, 1, false)) {
                return ItemStack.EMPTY
            }

            if (var5.getCount() == 0) {
                var4.set(ItemStack.EMPTY)
            } else {
                var4.setChanged()
            }
        }

        return var3
    }
}
