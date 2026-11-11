package micdoodle8.mods.galacticraft.core.energy.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField
import java.util.EnumSet

import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraftforge.fml.LogicalSide as Side
import java.util.*
import kotlin.math.floor


abstract class TileBaseElectricBlock : TileBaseUniversalElectrical(), IDisableableMachine, IConnector {
    //	public int energyPerTick = 200;
    //	private final float ueMaxEnergy;
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disableCooldown: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var hasEnoughEnergyToRun: Boolean = false

    @JvmField
    var noRedstoneControl: Boolean = false

    open fun shouldPullEnergy(): Boolean {
        return this.shouldUseEnergy() || this.getEnergyStoredGC(null) < this.getMaxEnergyStoredGC()
    }

    abstract fun shouldUseEnergy(): Boolean

    abstract fun getElectricInputDirection(): EnumFacing?

    abstract fun getBatteryInSlot(): ItemStack?

    //	public TileBaseElectricBlock()
    //	{
    //		this.storage.setMaxReceive(ueWattsPerTick);
    //		this.storage.setMaxExtract(0);
    //		this.storage.setCapacity(maxEnergy);
    ////		this.ueMaxEnergy = maxEnergy;
    ////		this.ueWattsPerTick = ueWattsPerTick;
    //
    //		/*
    //		 * if (PowerFramework.currentFramework != null) { this.bcPowerProvider =
    //		 * new GCCoreLinkedPowerProvider(this);
    //		 * this.bcPowerProvider.configure(20, 1, 10, 10, 1000); }
    //		 */
    //	}
    //	@Override
    //	public float getMaxEnergyStored()
    //	{
    //		return this.ueMaxEnergy;
    //	}
    fun getScaledElecticalLevel(i: Int): Int {
        return floor((this.getEnergyStoredGC(null) * i / this.getMaxEnergyStoredGC(null)).toDouble()).toInt()
        //- this.ueWattsPerTick;
    }

    //	@Override
    //	public float getRequest(EnumFacing direction)
    //	{
    //		if (this.shouldPullEnergy())
    //		{
    //			return this.ueWattsPerTick * 2;
    //		}
    //		else
    //		{
    //			return 0;
    //		}
    //	}
    //
    //	@Override
    //	public float getProvide(EnumFacing direction)
    //	{
    //		return 0;
    //	}
    override fun update() {
        if (!this.world.isRemote) {
            if (this.shouldPullEnergy() && this.getEnergyStoredGC(null) < this.getMaxEnergyStoredGC(null) && this.getBatteryInSlot() != null && this.getElectricInputDirection() != null) {
                this.discharge(this.getBatteryInSlot())
            }

            if (this.getEnergyStoredGC(null) > this.storage.getMaxExtract() && (this.noRedstoneControl || !RedstoneUtil.isBlockReceivingRedstone(
                    this.world,
                    this.getPos()
                ))
            ) {
                this.hasEnoughEnergyToRun = true
                if (this.shouldUseEnergy()) {
                    this.storage.extractEnergyGC(this.storage.getMaxExtract(), false)
                } else {
                    this.slowDischarge()
                }
            } else {
                this.hasEnoughEnergyToRun = false
                this.slowDischarge()
            }
        }

        super.update()

        if (!this.world.isRemote) {
            if (this.disableCooldown > 0) {
                this.disableCooldown--
            }
        }
    }

    open fun slowDischarge() {
        if (this.ticks % 10 == 0) {
            this.storage.extractEnergyGC(5f, false)
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        nbt.putBoolean("isDisabled", this.getDisabled(0))
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.setDisabled(0, nbt.getBooleanOr("isDisabled", false))
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown == 0) {
            this.disabled = disabled
            this.disableCooldown = 10
        }
    }

    abstract fun getFront(): EnumFacing?

    override fun getDisabled(index: Int): Boolean {
        return this.disabled
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        if (this.getElectricInputDirection() == null) {
            return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
        }

        return EnumSet.of<EnumFacing?>(this.getElectricInputDirection())
    }

    open fun isUsableByPlayer(entityplayer: EntityPlayer): Boolean {
        return this.getWorld().getTileEntity(this.getPos()) === this && entityplayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null || type != NetworkType.POWER) {
            return false
        }

        return direction == this.getElectricInputDirection()
    }

    fun getGUIstatus(): String {
        if (!this.noRedstoneControl && RedstoneUtil.isBlockReceivingRedstone(this.world, this.getPos())) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.off.name")
        }

        if (this.getEnergyStoredGC() == 0f) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.missingpower.name")
        }

        if (this.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.ready.name")
        }

        if (this.getEnergyStoredGC() < this.storage.getMaxExtract()) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.missingpower.name")
        }

        return EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.status.active.name")
    }

    /**
     * @param missingInput = dynamically: null if all inputs are present, or a string if an input (e.g. oxygen, fuel) is missing
     * @param activeString = the specific 'Running' / 'Processing' etc string for this machine
     * @return
     */
    fun getGUIstatus(missingInput: String?, activeString: String?, shorten: Boolean): String {
        if (!this.noRedstoneControl && RedstoneUtil.isBlockReceivingRedstone(this.world, this.getPos())) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.off.name")
        }

        if (this.getEnergyStoredGC() == 0f) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate(if (shorten) "gui.status.missingpower.short.name" else "gui.status.missingpower.name")
        }

        if (missingInput != null) {
            return missingInput
        }

        if (this.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.ready.name")
        }

        if (this.getEnergyStoredGC() < this.storage.getMaxExtract()) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate(if (shorten) "gui.status.missingpower.short.name" else "gui.status.missingpower.name")
        }

        if (activeString != null) {
            return activeString
        }

        return EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.unknown.name")
    }
}
