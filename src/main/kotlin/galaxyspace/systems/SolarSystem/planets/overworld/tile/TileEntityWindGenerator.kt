package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.item.legacyItemValue

import foundation.persistence.containsTag
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass.GlassRotation.E


import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import galaxyspace.core.configs.GSConfigEnergy
import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockWindGenerator
import asmodeuscore.core.utils.Utils
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityInventorySource
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
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

open class TileEntityWindGenerator @JvmOverloads constructor(adv: Boolean = false) :
    TileEntityInventorySource(if (adv) "tile.adv_wind_generator.name" else "tile.wind_generator.name"),
    IMultiBlock, IDisableableMachine, IConnector {
    //@NetworkedField(targetSide = Side.CLIENT)
    //public int solarStrength = 0;
    @JvmField
    var angle: Float = 0f

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
    var generateWatts: Float = 0f
    private var renderAABB: AxisAlignedBB? = null

    private var advanced: Boolean


    init {
        this.storage.setMaxExtract(MAX_GENERATE_WATTS.toFloat())
        this.storage.setMaxReceive(MAX_GENERATE_WATTS.toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)
        this.advanced = adv
    }

    open fun isAdvanced(): Boolean {
        return this.advanced
    }

    override fun update() {
        this.receiveEnergyGC(null, this.generateWatts, false)

        super.update()


        if (!this.world.isRemote) {
            if (Utils.isDustStorm(getWorld()) && !this.advanced) return

            this.recharge(this.getInventory().get(0))

            if (this.disableCooldown > 0) {
                this.disableCooldown--
            }

            if (!this.getDisabled(0) && this.ticks % 20 == 0 && getFanType() >= 0) {
                //this.solarStrength = 0;

                if ((this.world.provider is IGalacticraftWorldProvider)) {
                    val distance: Double = 100.0
                    val sinA: Double = -sin((this.currentAngle - 77.5) * Math.PI / 180.0)
                    val cosA: Double = abs(cos((this.currentAngle - 77.5) * Math.PI / 180.0))

                    for (x: Int in -1..2) {
                        for (z: Int in -1..2) {
                            if (this.world.canBlockSeeSky(
                                    BlockPos(
                                        this.getPos().getX() + x,
                                        this.getPos().getY() + 2,
                                        this.getPos().getZ() + z
                                    )
                                )
                            ) {
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
                                    //this.solarStrength++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!this.world.isRemote) {
            if (this.getGenerate() > 0.0f && this.world.canBlockSeeSky(this.getPos().up())) {
                this.generateWatts = min(max(this.getGenerate(), 0f), MAX_GENERATE_WATTS.toFloat())

                if (this.getInventory().get(1).getItem() === GSItems.BASIC) {
                    if (!this.getInventory().get(1).hasTagCompound()) this.getInventory().get(1)
                        .setTagCompound(NBTTagCompound())

                    if (!(this.getInventory().get(1).getTagCompound())!!.containsTag("destroyedLvl")) (this.getInventory().get(1)
                        .getTagCompound())!!.putInt("destroyedLvl", 0)

                    if (ticks % 20 == 0) {
                        if ((this.getInventory().get(1).getTagCompound())!!
                                .getIntOr("destroyedLvl", 0) < ItemBasicGS.FANS_DURABILITY[getFanType()]
                        ) (this.getInventory().get(1).getTagCompound())!!.putInt(
                            "destroyedLvl",
                            (this.getInventory().get(1).getTagCompound())!!.getIntOr("destroyedLvl", 0) + 1
                        )
                        else {
                            this.getInventory().get(1).splitStack(1)
                            this.world.scheduleBlockUpdate(getPos(), getBlockType(), 0, 0)
                        }
                    }
                }
            } else {
                this.generateWatts = 0f
            }
        }

        this.produce()
    }

    open fun getGenerate(): Float {
        if (this.getDisabled(0)) {
            return 0f
        }

        if (getFanType() < 0) return 0f

        var watts: Float =
            MathHelper.floor((10.0f + 5.0f * (getFanType() + 1)) * this.getWindBoost()) * (GSConfigEnergy.coefficientWindTurbine)
        if (this.advanced) watts *= 2f
        return watts
    }

    open fun getWindBoost(): Float {
        if (this.world.provider is IGalacticraftWorldProvider && this.world.provider !is WorldProviderSpaceStation) if (this.world.canBlockSeeSky(
                this.getPos().up()
            )
        ) return (this.world.provider as IGalacticraftWorldProvider).getWindLevel() * Math.round(
            this.getPos().getY() / 60.0f
        )

        if (this.world.provider is WorldProviderSurface) return (if (this.world.isThundering()) 3.0f else 1.0f) * this.getPos()
            .getY() / 60.0f

        return 0.0f
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
                BlockMulti.EnumBlockMultiType.DISH_LARGE.getMeta()
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




        if (this.advanced) {
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), y, (placedPosition)!!.getZ()))
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), ++y, (placedPosition)!!.getZ()))
            for (yy: Int in 0..2) for (x: Int in -1..1) for (z: Int in -1..1) (positions)!!.add(
                BlockPos(
                    (placedPosition)!!.getX() + x,
                    y + yy + 1,
                    (placedPosition)!!.getZ() + z
                )
            )
        }

        if (!this.advanced) {
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), y, (placedPosition)!!.getZ()))
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), ++y, (placedPosition)!!.getZ()))
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), ++y, (placedPosition)!!.getZ()))

            when (this.getBlockMetadata()) {
                0 -> {
                    var x: Int = -1
                    while (x < 2) {
                        var z: Int = -1
                        while (z < 2) {
                            (positions)!!.add(BlockPos((placedPosition)!!.getX() - 1, y + x, (placedPosition)!!.getZ() + z))
                            z++
                        }
                        x++
                    }
                }

                1 -> {
                    var x: Int = -1
                    while (x < 2) {
                        var z: Int = -1
                        while (z < 2) {
                            (positions)!!.add(BlockPos((placedPosition)!!.getX() + z, y + x, (placedPosition)!!.getZ() - 1))
                            z++
                        }
                        x++
                    }
                }

                2 -> {
                    var x: Int = -1
                    while (x < 2) {
                        var z: Int = -1
                        while (z < 2) {
                            (positions)!!.add(BlockPos((placedPosition)!!.getX() + 1, y + x, (placedPosition)!!.getZ() + z))
                            z++
                        }
                        x++
                    }
                }

                3 -> {
                    var x: Int = -1
                    while (x < 2) {
                        var z: Int = -1
                        while (z < 2) {
                            (positions)!!.add(BlockPos((placedPosition)!!.getX() + z, y + x, (placedPosition)!!.getZ() + 1))
                            z++
                        }
                        x++
                    }
                }
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
                if ((type == getMultiType())) {
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
        this.advanced = nbt.getBooleanOr("advanced", false)

        this.inventory = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.getInventory())
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putFloat("maxEnergy", this.getMaxEnergyStoredGC())
        nbt.putFloat("currentAngle", this.currentAngle)
        nbt.putFloat("targetAngle", this.targetAngle)
        nbt.putInt("disabledCooldown", this.disableCooldown)
        nbt.putBoolean("disabled", this.getDisabled(0))
        nbt.putBoolean("advanced", this.advanced)

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
        if (state.getBlock() is BlockWindGenerator) {
            return state.getValue(BlockWindGenerator.FACING)
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
        return BlockMulti.EnumBlockMultiType.DISH_LARGE
    }

    open fun getFanType(): Int {
        if (this.getInventory().get(1).isEmpty()) return -1

        return this.getInventory().get(1).legacyItemValue - 33
    }

    companion object {
        const val MAX_GENERATE_WATTS: Int = 450
    }
}
