package micdoodle8.mods.galacticraft.core.entities

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.tile.TileEntityParaChest
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.MoverType
import net.minecraft.entity.item.EntityItem
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.util.ArrayList
import java.util.Collections
import java.util.Locale

open class EntityParachest constructor(world: World) : Entity(world), IPacketReceiver {
    @JvmField
    var cargo: NonNullList<ItemStack>? = null

    @JvmField
    var fuelLevel: Int = 0
    private var placedChest = false

    @JvmField
    var color: EnumDyeColor = EnumDyeColor.WHITE

    constructor(world: World, cargo: NonNullList<ItemStack>, fuelLevel: Int) : this(world) {
        this.cargo = NonNullList.withSize<ItemStack>(cargo.size, ItemStack.EMPTY)
        Collections.copy<ItemStack?>(this.cargo, cargo)
        this.placedChest = false
        this.fuelLevel = fuelLevel
    }

    init {
        this.setSize(1.0f, 1.0f)
    }

    override fun entityInit() {
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        var size: Int = 56
        if (nbt.hasKey("CargoLength")) {
            size = nbt.getInteger("CargoLength")
        }
        this.cargo = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)

        ItemStackHelper.loadAllItems(nbt, this.cargo)

        this.placedChest = nbt.getBoolean("placedChest")
        this.fuelLevel = nbt.getInteger("FuelLevel")

        if (nbt.hasKey("color")) {
            this.color = EnumDyeColor.byDyeDamage(nbt.getInteger("color"))
        }
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        if (world.isRemote) return
        nbt.setInteger("CargoLength", this.cargo!!.size)
        ItemStackHelper.saveAllItems(nbt, this.cargo)

        nbt.setBoolean("placedChest", this.placedChest)
        nbt.setInteger("FuelLevel", this.fuelLevel)
        nbt.setInteger("color", this.color.getDyeDamage())
    }

    override fun onUpdate() {
        if (!this.placedChest) {
            if (this.onGround && !this.world.isRemote) {
                for (i: Int in 0..99) {
                    val x: Int = MathHelper.floor(this.posX)
                    val y: Int = MathHelper.floor(this.posY)
                    val z: Int = MathHelper.floor(this.posZ)

                    val pos: BlockPos = BlockPos(x, y + i, z)
                    val state: IBlockState = this.world.getBlockState(pos)
                    val block: Block = state.getBlock()

                    if (block.getMaterial(state).isReplaceable()) {
                        if (this.placeChest(pos)) {
                            this.setDead()
                            return
                        } else if (this.cargo != null) {
                            for (stack: ItemStack in (this.cargo)!!) {
                                val e: EntityItem = EntityItem(this.world, this.posX, this.posY, this.posZ, stack)
                                this.world.spawnEntity(e)
                            }

                            return
                        }
                    }
                }

                if (this.cargo != null) {
                    for (stack: ItemStack in (this.cargo)!!) {
                        val e: EntityItem = EntityItem(this.world, this.posX, this.posY, this.posZ, stack)
                        this.world.spawnEntity(e)
                    }
                }
            } else {
                this.motionY = -0.35
            }

            this.move(MoverType.SELF, 0.0, this.motionY, 0.0)
        }

        if (!this.world.isRemote && this.ticksExisted % 5 == 0) {
            (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                PacketDynamic(this),
                NetworkRegistry.TargetPoint(
                    GCCoreUtil.getDimensionID(this.world),
                    this.posX,
                    this.posY,
                    this.posZ,
                    64.0
                )
            )
        }
    }

    private fun placeChest(pos: BlockPos): Boolean {
        this.world.setBlockState(pos, (GCBlocks.parachest)!!.getDefaultState(), 3)
        val te: TileEntity? = this.world.getTileEntity(pos)

        if (te is TileEntityParaChest && this.cargo != null) {
            val chest: TileEntityParaChest = te

            chest.stacks = NonNullList.withSize<ItemStack>(this.cargo!!.size + 1, ItemStack.EMPTY)
            chest.color = this.color

            Collections.copy<ItemStack?>(chest.stacks, this.cargo)

            //            System.arraycopy(this.cargo, 0, chest.stacks, 0, this.cargo.size());
            chest.fuelTank.fill(
                FluidRegistry.getFluidStack(
                    (GCFluids.fluidFuel)!!.getName().lowercase(Locale.getDefault()),
                    this.fuelLevel
                ), true
            )

            return true
        }

        this.placedChest = true

        return true
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        if (!this.world.isRemote) {
            (sendData)!!.add(this.color.getDyeDamage())
        }
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (this.world.isRemote) {
            this.color = EnumDyeColor.byDyeDamage((buffer)!!.readInt())
        }
    }
}
