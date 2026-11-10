package galaxyspace.systems.SolarSystem.planets.overworld.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import galaxyspace.api.tile.ITileEffects
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityPanelController
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockSingleSolarPanel
import galaxyspace.core.configs.GSConfigEnergy
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockPanelController
import galaxyspace.GalaxySpace
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import galaxyspace.core.prefab.tile.TileEntityInventorySource
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.WorldProviderHell
import net.minecraftforge.fml.relauncher.Side
import java.util.HashMap
import kotlin.math.abs

open class TileEntityPanelController constructor() :
    TileEntityInventorySource("tile.panel_controller.name"), ITileEffects, IDisableableMachine, ISidedInventory,
    IConnector {
    private val panels: MutableMap<BlockPos?,Int> = HashMap<BlockPos?,Int>()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var heatGJperTick: Float = 0f

    private var visible = false

    init {
        this.storage.setCapacity(5000f)
        this.storage.setMaxExtract(2000f)
        this.inventory = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
    }


    override fun update() {
        if (this.heatGJperTick - MIN_GENERATE_GJ_PER_TICK > 0) {
            this.receiveEnergyGC(null, (this.heatGJperTick - MIN_GENERATE_GJ_PER_TICK), false)
        }

        super.update()
        panels.clear()
        val distance: Int = 21 // +1 for module
        val modules: Int = 1

        var xMin: Int = 0
        var xMax: Int = 0
        var zMin: Int = 0
        var zMax: Int = 0


        /*if(this.world.getBlockState(this.getPos().offset(getFront())).getBlock() instanceof BlockSingleSolarPanel)
		{*/
        when (this.getFront()) {
            EnumFacing.EAST -> {
                xMin = 1
                xMax = distance - 1
                zMin = -2
                zMax = 3

                var x: Int = 0
                x@ while (x <= distance) {
                    var z: Int = -2
                    while (z <= 2) {
                        if (x <= distance - 1) {
                            val pos: BlockPos =
                                BlockPos(this.getPos().getX() + x, this.getPos().getY(), this.getPos().getZ() + z)

                            if (this.world.getBlockState(pos).getBlock() is BlockSingleSolarPanel) {
                                var flag: Boolean = true
                                if (x > 1 && this.world.getBlockState(pos.west())
                                        .getBlock() !is BlockSingleSolarPanel
                                ) {
                                    flag = false
                                }

                                if (!flag) break@x

                                if (flag && this.world.canBlockSeeSky(pos.up())) {
                                    panels.put(
                                        pos,
                                        (world.getBlockState(pos).getBlock() as BlockSingleSolarPanel).getTier()
                                    )
                                }
                            }
                        }
                        z++
                    }
                    x++
                }
            }

            EnumFacing.WEST -> {
                xMin = -distance + 1
                xMax = 0
                zMin = -2
                zMax = 3

                var x: Int = xMax
                x@ while (x >= xMin) {
                    var z: Int = -2
                    while (z <= 2) {
                        val pos: BlockPos =
                            BlockPos(this.getPos().getX() + x, this.getPos().getY(), this.getPos().getZ() + z)

                        if (this.world.getBlockState(pos).getBlock() is BlockSingleSolarPanel) {
                            var flag: Boolean = true
                            if (x < -2 && this.world.getBlockState(pos.east()).getBlock() !is BlockSingleSolarPanel) {
                                flag = false
                            }

                            if (!flag) break@x

                            if (flag && this.world.canBlockSeeSky(pos.up())) {
                                panels.put(
                                    pos,
                                    (world.getBlockState(pos).getBlock() as BlockSingleSolarPanel).getTier()
                                )
                            }
                        }

                        z++
                    }
                    x--
                }
            }

            EnumFacing.NORTH -> {
                xMin = -2
                xMax = 3
                zMin = -distance + 1
                zMax = 0

                var x: Int = -2
                while (x <= 2) {
                    var z: Int = zMax
                    z@ while (z >= zMin) {
                        if (z >= zMin) {
                            val pos: BlockPos =
                                BlockPos(this.getPos().getX() + x, this.getPos().getY(), this.getPos().getZ() + z)

                            if (this.world.getBlockState(pos).getBlock() is BlockSingleSolarPanel) {
                                var flag: Boolean = true
                                if (z < -1 && this.world.getBlockState(pos.south())
                                        .getBlock() !is BlockSingleSolarPanel
                                ) {
                                    flag = false
                                }

                                if (!flag) break@z

                                if (flag && this.world.canBlockSeeSky(pos.up())) {
                                    panels.put(
                                        pos,
                                        (world.getBlockState(pos).getBlock() as BlockSingleSolarPanel).getTier()
                                    )
                                }
                            }
                        }
                        z--
                    }
                    x++
                }
            }

            EnumFacing.SOUTH -> {
                xMin = -2
                xMax = 3
                zMin = 1
                zMax = distance - 1

                var x: Int = -2
                while (x <= 2) {
                    var z: Int = zMin
                    x@ while (z <= zMax) {
                        val pos: BlockPos =
                            BlockPos(this.getPos().getX() + x, this.getPos().getY(), this.getPos().getZ() + z)

                        if (this.world.getBlockState(pos).getBlock() is BlockSingleSolarPanel) {
                            var flag: Boolean = true
                            if (z > 1 && this.world.getBlockState(pos.north()).getBlock() !is BlockSingleSolarPanel) {
                                flag = false
                            }

                            if (!flag) break@x

                            if (flag && this.world.canBlockSeeSky(pos.up())) {
                                panels.put(
                                    pos,
                                    (world.getBlockState(pos).getBlock() as BlockSingleSolarPanel).getTier()
                                )
                            }
                        }
                        z++
                    }
                    x++
                }
            }

            else -> {}
        }

        //}	
        if (world.isRemote && this.visible) {
            for (x: Int in xMin..xMax) for (z: Int in zMin..zMax) {
                if (x == xMin || x == xMax || z == zMin || z == zMax) world.spawnParticle(
                    EnumParticleTypes.REDSTONE, (this.getPos().getX() + x).toDouble(),
                    this.getPos().getY().toDouble(),
                    (this.getPos().getZ() + z).toDouble(), 0.0, 0.0,
                    0.0
                )
            }
        }


        if (!this.world.isRemote) {
            if (this.world.isDaytime() && (this.world.provider is IGalacticraftWorldProvider || !this.world.isRaining() && !this.world.isThundering())) {
                val angle: Float =
                    if (this.world.getCelestialAngle(1.0f) - 0.784690560f < 0) 1.0f - 0.784690560f else -0.784690560f
                var celestialAngle: Float = (this.world.getCelestialAngle(1.0f) + angle) * 360.0f

                celestialAngle %= 360f
                val difference: Float = (180.0f - abs(celestialAngle % 180 - celestialAngle)) / 180.0f


                var t2_count: Int = 0
                for (panel: MutableMap.MutableEntry<BlockPos?,Int> in panels.entries) {
                    //GalaxySpace.debug(panel.getKey() + " | " + ((BlockSingleSolarPanel)world.getBlockState(panel.getKey()).getBlock()).getTier() + "");
                    if (panel.value == 2) t2_count++
                }


                //GalaxySpace.debug(t2_count + "");
                this.heatGJperTick = (MathHelper.floor(
                    ((0.03f * difference * difference * (9 * (abs(difference) * (500.0f)) + (t2_count * 300))
                            * this.getSolarBoost())) * 0.25 * (panels.size * GSConfigEnergy.coefficientSolarPanel) / 5
                )).toFloat()


                //(panels.size() + 2 * GSConfigEnergy.coefficientSolarPanel) * getSolarBoost();
            } else {
                this.heatGJperTick = 0f
            }

            this.produce()
        }
    }

    open fun getSolarBoost(): Float {
        if (this.world.provider is WorldProviderHell) return 0.0f

        return (if (this.world.provider is ISolarLevel) (this.world.provider as ISolarLevel).getSolarEnergyMultiplier() else 1.0).toFloat()
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        val var2: NBTTagList = par1NBTTagCompound.getTagList("Items", 10)

        this.inventory = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())
        return par1NBTTagCompound
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }


    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return false
    }

    override fun getSlotsForFace(side: EnumFacing?): IntArray {
        return intArrayOf()
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, direction: EnumFacing?): Boolean {
        return this.isItemValidForSlot(slotID, itemstack)
    }

    override fun canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing?): Boolean {
        return false
    }

    override fun receiveElectricity(from: EnumFacing?, energy: Float, tier: Int, doReceive: Boolean): Float {
        return 0f
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
    }

    override fun getDisabled(index: Int): Boolean {
        return false
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricOutputDirection())
    }

    open fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockPanelController) {
            return state.getValue(BlockPanelController.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront().rotateY().rotateY()
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricOutputDirection()
        }
        return false
    }

    override fun setEffectsVisible(shouldRender: Boolean) {
        this.visible = shouldRender
    }

    override fun getEffectsVisible(): Boolean {
        return this.visible
    }

    companion object {
        const val MIN_GENERATE_GJ_PER_TICK: Float = 1f
    }
}
