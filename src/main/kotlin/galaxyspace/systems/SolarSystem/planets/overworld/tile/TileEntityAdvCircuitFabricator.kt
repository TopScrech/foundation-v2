package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.item.legacyItemValue

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvCircuitFabricator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockAdvCircuitFabricator
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList

open class TileEntityAdvCircuitFabricator constructor() :
    TileEntityElectricInventory("tile.adv_circuit_fabricator.name"), ISidedInventory {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0
    private var producingStack: ItemStack = ItemStack.EMPTY
    private var machineTickCounter: Long = 0


    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 40 else 20).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(7 + 4, ItemStack.EMPTY)
    }

    override fun update() {
        super.update()

        this.updateInput()

        if (!this.world.isRemote) {
            var updateInv: Boolean = false

            if (this.hasEnoughEnergyToRun) {
                if (this.canCompress()) {
                    //////////
                    var boost_speed: Int = 0
                    var energy_boost: Int = 0

                    for (i: Int in 0..3) {
                        if (this.getInventory().get(getInventory().size - i - 1)
                                .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 2))
                        ) boost_speed++
                        if (this.getInventory().get(getInventory().size - i - 1)
                                .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 3))
                        ) energy_boost++
                    }

                    this.processTicks += 2 + boost_speed
                    this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 + (40 * boost_speed) - (20 * energy_boost) else 75 + (35 * boost_speed) - (15 * energy_boost)).toFloat())

                    /////////////

                    // ++this.processTicks;
                    if (this.processTicks >= this.getProcessTimeRequired()) {
                        this.world.playSound(
                            null,
                            this.getPos(),
                            SoundEvents.BLOCK_ANVIL_LAND,
                            SoundCategory.BLOCKS,
                            0.3f,
                            this.world.rand.nextFloat() * 0.1f + 0.9f
                        )
                        this.processTicks = 0
                        this.compressItems()
                        updateInv = true
                    }
                } else {
                    this.processTicks = 0
                }
            } else {
                this.processTicks = 0
            }

            if (updateInv) {
                this.setChanged()
            }
        }

        this.machineTickCounter++
    }

    open fun getProcessTimeRequired(): Int {
        return PROCESS_TIME_REQUIRED * 2 / (1 + this.poweredByTierGC)
    }

    open fun updateInput() {
        this.producingStack = (CircuitFabricatorRecipes.getOutputForInput(this.getInventory().subList(1, 6)))!!
    }

    private fun canCompress(): Boolean {
        if (this.producingStack.isEmpty()) {
            return false
        }
        if (this.getInventory().get(6).isEmpty()) {
            return true
        }
        if (!this.getInventory().get(6).isEmpty() && !this.getInventory().get(6).isItemEqual(this.producingStack)) {
            return false
        }
        val result: Int = if (this.getInventory().get(6).isEmpty()) 0 else this.getInventory().get(6)
            .getCount() + this.producingStack.getCount()
        return result <= this.getMaxStackSize() && result <= this.producingStack.getMaxStackSize()
    }

    open fun compressItems() {
        if (this.canCompress()) {
            val resultItemStack: ItemStack = this.producingStack.copy()
            if (this.world.provider is IZeroGDimension) {
                if (resultItemStack.getItem() === GCItems.basicItem) {
                    if (resultItemStack.legacyItemValue == ItemBasic.WAFER_BASIC) {
                        resultItemStack.setCount(5)
                    } else if (resultItemStack.legacyItemValue == 12)  //Solar panels
                    {
                        resultItemStack.setCount(15)
                    } else {
                        resultItemStack.setCount(resultItemStack.getCount() * 2)
                    }
                }
            }

            if (this.getInventory().get(6).isEmpty()) {
                this.getInventory().set(6, resultItemStack)
            } else if (this.getInventory().get(6).isItemEqual(resultItemStack)) {
                if (this.getInventory().get(6).getCount() + resultItemStack.getCount() > 64) {
                    resultItemStack.setCount(this.getInventory().get(6).getCount() + resultItemStack.getCount() - 64)
                    GCCoreUtil.spawnItem(this.world, this.getPos(), resultItemStack)
                    this.getInventory().get(6).setCount(64)
                } else {
                    this.getInventory().get(6).grow(resultItemStack.getCount())
                }
            }
        }

        for (i: Int in 1..5) {
            this.removeItem(i, 1)
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.processTicks = nbt.getIntOr("smeltingTicks", 0)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("smeltingTicks", this.processTicks)

        return nbt
    }


    override fun hasCustomName(): Boolean {
        return false
    }

    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        if (slotID == 0) {
            return itemStack != null && ItemElectricBase.isElectricItem(itemStack.getItem())
        }

        if (slotID > 5) {
            return false
        }

        val list: ArrayList<ItemStack?> = CircuitFabricatorRecipes.slotValidItems.get(slotID - 1)

        for (testEntry in list) {
            val test = testEntry!!
            if (test.isItemEqual(itemStack)) {
                return true
            }
        }

        return false
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (side == EnumFacing.DOWN) {
            return intArrayOf(6)
        }

        //Offer whichever silicon slot has less silicon
        val siliconFlag: Boolean =
            !this.getInventory().get(2).isEmpty() && (this.getInventory().get(3).isEmpty() || this.getInventory().get(3)
                .getCount() < this.getInventory().get(2).getCount())
        return if (siliconFlag) intArrayOf(0, 1, 3, 4, 5) else intArrayOf(0, 1, 2, 4, 5)
    }

    override fun canPlaceItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return slotID < 6 && this.canPlaceItem(slotID, par2ItemStack)
    }

    override fun canTakeItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID == 6
    }

    override fun shouldUseEnergy(): Boolean {
        return this.processTicks > 0
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockAdvCircuitFabricator) {
            return (state.getValue(BlockAdvCircuitFabricator.FACING))
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    companion object {
        const val PROCESS_TIME_REQUIRED: Int = 300
    }
}
