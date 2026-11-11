package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.inventory.armorItems
import foundation.inventory.mainItems
import micdoodle8.mods.miccore.Annotations.NetworkedField

import cofh.redstoneflux.api.IEnergyContainerItem
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityEnergyPad
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraftforge.fml.LogicalSide as Side

open class TileEntityEnergyPad constructor() : TileEntityElectricInventory("tile.energy_pad.name") {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = 1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var isCollide: Boolean = false

    init {
        this.storage.setCapacity(15000f)
        this.storage.setMaxExtract(0f)
        this.storage.setMaxReceive(500f)
        this.inventory = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
    }

    override fun update() {
        super.update()


        //Freezing
        if (this.world.rand.nextInt(4) == 0) this.world.notifyLightSet(this.getPos())

        if (canProcess() && !this.disabled) {
            if (this.hasEnoughEnergyToRun) {
                //this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90  : 75);
                //if (this.world.rand.nextInt(4) == 0) 
                //this.isCollide = false;
            } else this.isCollide = false
        }
    }

    /* Disable discharge machine */
    override fun slowDischarge() {
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val nbttagcompound: NBTTagCompound = NBTTagCompound()
        writeToNBT(nbttagcompound)
        return SPacketUpdateTileEntity(pos, 1, nbttagcompound)
    }

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        readFromNBT(packet.getNbtCompound())
    }

    open fun smeltItem(player: EntityPlayer) {
        if (this.hasEnoughEnergyToRun && this.storage.getEnergyStoredGC() > 200.0) {
            for (stack: ItemStack in player.inventory.armorItems) if (chargeItem(stack, 350.0)) {
                this.storage.extractEnergyGCnoMax(200f, false)
                break
            }
            for (stack: ItemStack in player.inventory.mainItems) if (chargeItem(stack, 350.0)) {
                this.storage.extractEnergyGCnoMax(200f, false)
                break
            }
        }
        isCollide = isCollideTile(player)
    }

    private fun isCollideTile(player: EntityPlayer?): Boolean {
        if (player != null && this.hasEnoughEnergyToRun) {
            //double dis = getPos().distanceSqToCenter(player.posX, player.posY, player.posZ);
            if (player.getPosition() == getPos()) {
                return true
            }
        }
        return false
    }

    private fun chargeItem(stack: ItemStack, count: Double): Boolean {
        if (stack.getItem() is IItemElectricBase) {
            val item: IItemElectricBase = stack.getItem() as IItemElectricBase
            if (item.getElectricityStored(stack) < item.getMaxElectricityStored(stack)) {
                item.recharge(stack, count.toFloat(), true)
                return true
            }
        }

        if (EnergyConfigHandler.isRFAPILoaded()) if (stack.getItem() is IEnergyContainerItem) {
            val item: IEnergyContainerItem = stack.getItem() as IEnergyContainerItem
            if (item.getEnergyStored(stack) < item.getMaxEnergyStored(stack)) {
                item.extractEnergy(stack, count.toInt(), false)
                return true
            }
        }

        return false
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        processTicks = par1NBTTagCompound.getIntOr("smeltingTicks", 0)
        this.isCollide = par1NBTTagCompound.getBooleanOr("isCollide", false)

        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.putInt("smeltingTicks", processTicks)
        par1NBTTagCompound.putBoolean("isCollide", this.isCollide)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())

        return par1NBTTagCompound
    }

    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        return true
    }

    override fun shouldUseEnergy(): Boolean {
        return canProcess()
    }

    override fun getFront(): EnumFacing {
        return EnumFacing.DOWN
    }

    override fun getElectricInputDirection(): EnumFacing {
        return EnumFacing.DOWN
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }
        return false
    }

    public override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf()
    }

    companion object {
        @JvmField
        var PROCESS_TIME_REQUIRED: Int = 130

        @JvmField
        @Annotations.NetworkedField(targetSide = Side.CLIENT)
        var processTicks: Int = 0

        @JvmStatic
        fun canProcess(): Boolean {
            return true
        }
    }
}
