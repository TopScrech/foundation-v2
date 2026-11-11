package galaxyspace.systems.SolarSystem.planets.overworld.tile
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass.GlassRotation.E


import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import galaxyspace.core.configs.GSConfigEnergy
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindSolarPanel
import asmodeuscore.api.dimension.IAdvancedSpace
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockSolarWindPanel
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityInventorySource
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class TileEntityWindSolarPanel constructor() : TileEntityInventorySource("tile.solarwind_panel.name"),
    IMultiBlock, IDisableableMachine, IConnector {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var solarWindStrength: Int = 0

    @JvmField
    var angle: Double = 0.0

    @JvmField
    var targetAngle: Float = 0f

    @JvmField
    var currentAngle: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disableCooldown: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var generateWatts: Int = 0

    private var initialised = false
    private var initialisedMulti = false
    private var renderAABB: AxisAlignedBB? = null

    init {
        this.storage.setMaxExtract(MAX_GENERATE_WATTS.toFloat())
        this.storage.setMaxReceive(MAX_GENERATE_WATTS.toFloat())
        this.storage.setCapacity(40000f)
        this.inventory = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

        this.initialised = true
        this.tierGC = 1
    }

    override fun update() {
        if (!this.initialised) {
            val metadata: Int = this.getBlockMetadata()
            this.storage.setCapacity(40000f)

            this.initialised = true
        }

        if (!this.initialisedMulti) {
            this.initialisedMulti = this.initialiseMultiTiles(this.getPos(), this.world)
        }

        if (!this.world.isRemote) this.receiveEnergyGC(null, this.generateWatts.toFloat(), false)

        super.update()

        if (!this.world.isRemote) {
            this.recharge(this.getInventory().get(0))


            //this.storage.setCapacity(this.containingItems[1] != null ? 60000 : 40000);
            if (this.disableCooldown > 0) {
                this.disableCooldown--
            }

            if (!this.getDisabled(0) && this.ticks % 20 == 0) {
                this.solarWindStrength = 0

                if (this.world.isDaytime() && (this.world.provider is IGalacticraftWorldProvider || !this.world.isRaining() && !this.world.isThundering())) {
                    val distance: Double = 100.0
                    val sinA: Double = -sin((this.currentAngle - 77.5) * Math.PI / 180.0)
                    val cosA: Double = abs(cos((this.currentAngle - 77.5) * Math.PI / 180.0))

                    for (x: Int in -1..1) {
                        for (z: Int in -1..1) {
                            var valid: Boolean = true

                            val blockVec: BlockVec3 = BlockVec3(this).translate(x, 3, z)
                            var d: Double = 0.0
                            while (d < distance) {
                                val blockAt: BlockVec3 =
                                    blockVec.clone().translate((d * sinA).toInt(), (d * cosA).toInt(), 0)
                                val state: IBlockState? = blockAt.getBlockState(this.world)

                                if (state == null) {
                                    break
                                }

                                if (state.getBlock().isOpaqueCube(state)) {
                                    valid = false
                                    break
                                }
                                d++
                            }

                            if (valid) {
                                this.solarWindStrength++
                            }
                        }
                    }
                }
            }
        }

        val angle: Float = if (this.world.getCelestialAngle(1.0f) - 0.7845194f < 0) 1.0f - 0.7845194f else -0.7845194f
        var celestialAngle: Float = (this.world.getCelestialAngle(1.0f) + angle) * 360.0f


        celestialAngle %= 360f

        /* if (this.tierGC == 1)
        {
            if (!this.worldObj.isDaytime() || this.worldObj.isRaining() || this.worldObj.isThundering())
            {
                this.targetAngle = 77.5F + 180.0F;
            }
            else
            {
                this.targetAngle = 77.5F;
            }
        }
        else
        {*/
        if (celestialAngle > 30 && celestialAngle < 150 && !this.world.isRaining()) {
            val difference: Float = this.targetAngle - celestialAngle

            this.targetAngle -= difference / 20.0f
        } else if (!this.world.isDaytime() || this.world.isRaining() || this.world.isThundering()) {
            this.targetAngle = 77.5f + 100.0f
        } else if (celestialAngle < 50) {
            this.targetAngle = 50f
        } else if (celestialAngle > 150) {
            this.targetAngle = 150f
        }

        //}
        val difference: Float = this.targetAngle - this.currentAngle

        this.currentAngle += difference / 20.0f

        if (!this.world.isRemote) {
            if (this.getGenerate() > 0.0f) {
                this.generateWatts = min(max(this.getGenerate(), 0), MAX_GENERATE_WATTS)
            } else {
                this.generateWatts = 0
            }
        }

        this.produce()
    }

    protected open fun initialiseMultiTiles(pos: BlockPos, world: World): Boolean {
        //Client can create its own fake blocks and tiles - no need for networking in 1.8+
        if (world.isClientSide) this.onCreate(world, pos)

        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(pos, (positions as MutableList<BlockPos?>?))
        var result: Boolean = true
        for (vecToAdd: BlockPos in positions) {
            val tile: TileEntity? = world.getBlockEntity(vecToAdd)
            if (tile is TileEntityMulti) {
                tile.mainBlockPosition = pos
            } else {
                result = false
            }
        }
        return result
    }

    open fun getGenerate(): Int {
        if (this.getDisabled(0)) {
            return 0
        }

        val angle: Float =
            if (this.world.getCelestialAngle(1.0f) - 0.784690560f < 0) 1.0f - 0.784690560f else -0.784690560f
        var celestialAngle: Float = (this.world.getCelestialAngle(1.0f) + angle) * 360.0f

        celestialAngle %= 360f

        val difference: Float = (180.0f - abs(this.currentAngle % 180 - celestialAngle)) / 180.0f
        return MathHelper.floor((0.01f * difference * difference * (this.solarWindStrength * (abs(difference) * 500.0f)) * this.getSolarBoost() * 2) * GSConfigEnergy.coefficientSolarPanel)
    }

    open fun getSolarBoost(): Float {
        if (this.world.provider is WorldProviderSurface) return 0.007f
        return (if (this.world.provider is IAdvancedSpace) (this.world.provider as IAdvancedSpace).getSolarWindMultiplier() else 1.0).toFloat()
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        return false
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        if (positions.size > 0) {
            (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock(
                (world)!!,
                positions.get(0),
                placedPosition,
                BlockMulti.EnumBlockMultiType.SOLAR_PANEL_0.getMeta()
            )
            positions.removeAt(0)
        }
        (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock((world)!!, positions, placedPosition, this.getMultiType())
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val buildHeight: Int = this.world.getHeight() - 1
        var y: Int = (placedPosition)!!.getY() + 1
        if (y > buildHeight) {
            return
        }
        (positions)!!.add(BlockPos((placedPosition)!!.getX(), y, (placedPosition)!!.getZ()))

        y++
        if (y > buildHeight) {
            return
        }
        for (x: Int in -1..1) {
            for (z: Int in -1..1) {
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === GCBlocks.fakeBlock) {
                val type: BlockMulti.EnumBlockMultiType =
                    stateAt.getValue(BlockMulti.MULTI_TYPE) as BlockMulti.EnumBlockMultiType
                if ((type == BlockMulti.EnumBlockMultiType.SOLAR_PANEL_0 || type == BlockMulti.EnumBlockMultiType.SOLAR_PANEL_1)) {
                    if (this.world.isRemote && this.world.rand.nextDouble() < 0.1) {
                        FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(
                            pos,
                            (GCBlocks.solarPanel)!!.defaultBlockState()
                        )
                    }

                    this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                }
            }
        }

        this.world.destroyBlock(getPos(), true)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.storage.setCapacity(nbt.getFloatOr("maxEnergy", 0f))
        this.currentAngle = nbt.getFloatOr("currentAngle", 0f)
        this.targetAngle = nbt.getFloatOr("targetAngle", 0f)
        this.setDisabled(0, nbt.getBooleanOr("disabled", false))
        this.disableCooldown = nbt.getIntOr("disabledCooldown", 0)

        this.inventory = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.getInventory())

        this.initialised = false
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putFloat("maxEnergy", this.getMaxEnergyStoredGC())
        nbt.putFloat("currentAngle", this.currentAngle)
        nbt.putFloat("targetAngle", this.targetAngle)
        nbt.putInt("disabledCooldown", this.disableCooldown)
        nbt.putBoolean("disabled", this.getDisabled(0))

        ItemStackHelper.saveAllItems(nbt, this.getInventory())

        return nbt
    }


    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getFront())
    }


    open fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockSolarWindPanel) {
            return state.getValue(BlockSolarWindPanel.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront()
    }


    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(
                (getPos().getX() - 1).toDouble(),
                getPos().getY().toDouble(),
                (getPos().getZ() - 1).toDouble(),
                (getPos().getX() + 2).toDouble(),
                (getPos().getY() + 4).toDouble(),
                (getPos().getZ() + 2).toDouble()
            )
        }
        return this.renderAABB!!
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown == 0) {
            this.disabled = disabled
            this.disableCooldown = 20
        }
    }

    override fun getDisabled(index: Int): Boolean {
        return this.disabled
    }

    open fun getScaledElecticalLevel(i: Int): Int {
        return floor((this.getEnergyStoredGC() * i / this.getMaxEnergyStoredGC()).toDouble()).toInt()
    }

    public override fun getMaxStackSize(): Int {
        return 1
    }

    public override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    public override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    public override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    public override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 0
    }

    public override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem())
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null || type != NetworkType.POWER) {
            return false
        }

        return direction == this.getElectricOutputDirection()
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return BlockMulti.EnumBlockMultiType.SOLAR_PANEL_0
    }

    companion object {
        const val MAX_GENERATE_WATTS: Int = 450
    }
}
