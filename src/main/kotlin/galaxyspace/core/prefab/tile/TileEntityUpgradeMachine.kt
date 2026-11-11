package galaxyspace.core.prefab.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.LogicalSide as Side

abstract class TileEntityUpgradeMachine(tileName: String?) : TileEntityElectricInventory(tileName) {
    private var boost_speed = 0
    private var energy_boost = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            if (this.hasEnoughEnergyToRun) {
                if (canProcess()) {
                    energy_boost = 0
                    boost_speed = energy_boost

                    if (upgradeSlots() != null) {
                        for (i: Int in 0..upgradeSlots()!!.size - 1) {
                            if (this.getInventory().get(upgradeSlots()!![i])
                                    .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 2))
                            ) boost_speed++
                            if (this.getInventory().get(upgradeSlots()!![i])
                                    .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 3))
                            ) energy_boost++
                        }

                        this.processTicks += 1 * (1 + this.getBoost())
                        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 + (60 * this.getBoost()) - (20 * getEnergy()) else 75 + (55 * this.getBoost()) - (15 * this.getEnergy())).toFloat())
                    }
                }
            }
        }
    }

    public override fun getMaxStackSize(): Int {
        return 64
    }

    public override fun getItem(var1: Int): ItemStack {
        return this.getInventory().get(var1)
    }

    override fun shouldUseEnergy(): Boolean {
        return canProcess()
    }

    override fun getBatteryInSlot(): ItemStack? {
        return this.getItem(0)
    }

    fun getBoost(): Int {
        return boost_speed
    }

    fun getEnergy(): Int {
        return energy_boost
    }

    abstract fun canProcess(): Boolean

    abstract fun upgradeSlots(): IntArray?
}
