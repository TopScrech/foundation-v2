package micdoodle8.mods.galacticraft.core.energy.tile

import java.util.EnumSet

import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.capabilities.Capability
import java.util.*
import kotlin.math.min

open class TileBaseUniversalElectricalSource : TileBaseUniversalElectrical() {
    /*
     * The main function to output energy each tick from a source.
     *
     * The source will attempt to produce into its outputDirections whatever energy
     * it has available, and will reduce its stored energy by the amount which is in fact used.
     *
     * Max output = this.storage.maxExtract.
     *
     * @return The amount of energy that was used.
     */
    open fun produce(): Float {
        this.storage.maxExtractRemaining = this.storage.maxExtract
        val produced: Float = this.extractEnergyGC(null, this.produce(false), false)
        this.storage.maxExtractRemaining -= produced
        if (this.storage.maxExtractRemaining < 0) {
            this.storage.maxExtractRemaining = 0f
        }
        return produced
    }

    /*
     * Function to produce energy each tick into the outputs of a source.
     * If simulate is true, no energy is in fact transferred.
     *
     * Note: even if simulate is false this does NOT reduce the source's own
     * energy storage by the amount produced, that needs to be done elsewhere
     * See this.produce() for an example.
     */
    open fun produce(simulate: Boolean): Float {
        var amountProduced: Float = 0f

        if (!this.world.isRemote) {
            val outputDirections: EnumSet<EnumFacing?> = this.getElectricalOutputDirections()

            //            outputDirections.remove(EnumFacing.UNKNOWN);
            val thisVec: BlockVec3 = BlockVec3(this)
            for (directionEntry in outputDirections) {
                val direction = directionEntry!!
                val tileAdj: TileEntity? = thisVec.getTileEntityOnSide(this.world, direction)

                if (tileAdj != null) {
                    val toSend: Float = this.extractEnergyGC(
                        null,
                        min(
                            this.getEnergyStoredGC() - amountProduced,
                            this.getEnergyStoredGC() / outputDirections.size
                        ),
                        true
                    )
                    if (toSend <= 0) {
                        continue
                    }

                    if (tileAdj is TileBaseConductor && tileAdj.canConnect(
                            direction.getOpposite(),
                            NetworkType.POWER
                        )
                    ) {
                        val network: IElectricityNetwork? = (tileAdj as IConductor).getNetwork()
                        if (network != null) {
                            amountProduced += (toSend - network.produce(toSend, !simulate, this.tierGC, this))
                        }
                    } else if (tileAdj is TileBaseUniversalElectrical) {
                        amountProduced += tileAdj.receiveElectricity(
                            direction.getOpposite(),
                            toSend,
                            this.tierGC,
                            !simulate
                        )
                    } else {
                        amountProduced += EnergyUtil.otherModsEnergyTransfer(
                            tileAdj,
                            direction.getOpposite(),
                            toSend,
                            simulate
                        )
                    }
                }
            }
        }

        return amountProduced
    }

    /**
     * Recharges electric item.
     */
    open fun recharge(itemStack: ItemStack?) {
        if (itemStack != null && itemStack.getCount() == 1) {
            val item: Item = itemStack.getItem()
            val maxExtractSave: Float = this.storage.getMaxExtract()
            if (this.tierGC > 1) {
                this.storage.setMaxExtract(maxExtractSave * 2.5f)
            }
            val energyToCharge: Float = this.storage.extractEnergyGC(this.storage.getMaxExtract(), true)

            if (item is IItemElectric) {
                this.storage.extractEnergyGC(ElectricItemHelper.chargeItem(itemStack, energyToCharge), false)
            }

            //            else if (EnergyConfigHandler.isRFAPILoaded() && item instanceof IEnergyContainerItem)
//            {
//                this.storage.extractEnergyGC(((IEnergyContainerItem)item).receiveEnergy(itemStack, (int) (energyToCharge * EnergyConfigHandler.TO_RF_RATIO), false) / EnergyConfigHandler.TO_RF_RATIO, false);
//            }

            //			else if (GCCoreCompatibilityManager.isTELoaded() && itemStack.getItem() instanceof IEnergyContainerItem)
            //			{
            //				int accepted = ((IEnergyContainerItem) itemStack.getItem()).receiveEnergy(itemStack, (int) Math.floor(this.getProvide(EnumFacing.UNKNOWN) * EnergyConfigHandler.TO_TE_RATIO), false);
            //				this.provideElectricity(accepted * EnergyConfigHandler.TE_RATIO, true);
            //			}
            if (this.tierGC > 1) {
                this.storage.setMaxExtract(maxExtractSave)
            }
        }
    }

    override fun hasCapability(cap: Capability<*>, side: EnumFacing?): Boolean {
        return super.hasCapability(cap, side)
    }

    override fun <T> getCapability(cap: Capability<T?>, side: EnumFacing?): T? {
        return super.getCapability<T?>(cap, side)
    }

    override fun getProvide(direction: EnumFacing?): Float {
        if (this.getElectricalOutputDirections().contains(direction)) {
            return this.storage.extractEnergyGC(Float.MAX_VALUE, true)
        }

        return 0f
    }

    open fun getElectricOutputDirection(): EnumFacing? {
        return null
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyProvider", modID = "")
    open fun extractEnergy(from: EnumFacing?, maxExtract: Int, simulate: Boolean): Int {
        if (EnergyConfigHandler.disableRFOutput) {
            return 0
        }

        if (!this.getElectricalOutputDirections().contains(from)) {
            return 0
        }

        return MathHelper.floor(
            this.storage.extractEnergyGC(
                maxExtract / EnergyConfigHandler.TO_RF_RATIO,
                !simulate
            ) * EnergyConfigHandler.TO_RF_RATIO
        )
    }

    //ForgeEnergy
    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        if (!canExtract()) return 0

        return MathHelper.floor(
            this.storage.extractEnergyGC(
                maxExtract / EnergyConfigHandler.TO_RF_RATIO,
                !simulate
            ) * EnergyConfigHandler.TO_RF_RATIO
        )
    }

    //ForgeEnergy
    override fun canExtract(): Boolean {
        return !EnergyConfigHandler.disableFEOutput
    }
}
