package galaxyspace.core.prefab.entities

import net.minecraft.network.chat.Component

import net.minecraft.world.ContainerHelper
import foundation.persistence.containsTag

import galaxyspace.core.GSItems
import galaxyspace.core.GSFluids
import io.netty.buffer.ByteBuf
import galaxyspace.core.prefab.entities.EntityCustomCargoRocket
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.entity.IWorldTransferCallback
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult

import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class EntityCustomCargoRocket : EntityAutoRocket, IRocketType, IWorldTransferCallback {
    @JvmField
    var rocketType: IRocketType.EnumRocketType? = null

    @JvmField
    var rumble: Float = 0f

    @JvmField
    var fluidTank: FluidTank = FluidTank(10000)

    @JvmField
    var fluidName: String? = null

    constructor(par1World: World?) : super(par1World) {
        this.setSize(0.98f, 2f)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?
    ) : super(par1World, par2, par4, par6) {
        this.rocketType = rocketType
        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        this.setSize(0.98f, 2f)
    }

    override fun entityInit() {
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in (this.stacks)!!) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getItem(index: Int): ItemStack {
        if (this.stacks == null) {
            return ItemStack.EMPTY
        }

        return (this.stacks)!!.get(index)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        return ContainerHelper.takeItem(this.stacks, index)
    }

    override fun setItem(index: Int, stack: ItemStack) {
        (this.stacks)!!.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun getMaxStackSize(): Int {
        return 64
    }


    override fun setChanged() {
    }

    override fun stillValid(entityplayer: EntityPlayer): Boolean {
        return !this.isDead && entityplayer.getDistanceSq(this) <= 64.0
    }




    override fun canPlaceItem(i: Int, itemstack: ItemStack): Boolean {
        return false
    }




    override fun clearContent() {
        for (slot in 0 until containerSize) setItem(slot, ItemStack.EMPTY)
    }

    override fun getFuelTankCapacity(): Int {
        return 2000
    }

    open fun getCargoFilledAmount(): Float {
        var weight: Float = 1f

        for (stack: ItemStack? in (this.stacks)!!) {
            if (stack != null && !stack.isEmpty()) {
                weight += 0.1.toFloat()
            }
        }

        return weight
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(GSItems.ROCKET_FLUID_CRAGO, 1, this.rocketType!!.getIndex())
    }

    override fun onUpdate() {
        if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal && this.hasValidFuel()) {
            var motionScalar: Double = (this.timeSinceLaunch / 250).toDouble()

            motionScalar = min(motionScalar, 1.0)

            val modifier: Double = this.getCargoFilledAmount().toDouble()
            motionScalar *= 5.0 / modifier

            if (this.launchPhase != EnumLaunchPhase.LANDING.ordinal) {
                if (motionScalar != 0.0) {
                    this.motionY = -motionScalar * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
                }
            }

            var multiplier: Double = 1.0

            if (this.world.provider is IGalacticraftWorldProvider) {
                multiplier = (this.world.provider as IGalacticraftWorldProvider).getFuelUsageMultiplier()

                if (multiplier <= 0) {
                    multiplier = 1.0
                }
            }

            if (this.timeSinceLaunch % MathHelper.floor(3 * (1 / multiplier)) == 0f) {
                this.removeFuel(1)
                if (!this.hasValidFuel()) {
                    this.stopRocketSound()
                }
            }
        } else if (!this.hasValidFuel() && this.getLaunched()) {
            if (abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 10 != 0.0) {
                this.motionY -= abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 20
            }
        }

        super.onUpdate()

        if (this.rumble > 0) {
            this.rumble--
        }

        if (this.rumble < 0) {
            this.rumble++
        }

        if (this.launchPhase >= EnumLaunchPhase.IGNITED.ordinal) {
            this.performHurtAnimation()

            this.rumble = this.rand.nextInt(3).toFloat() - 3
        }

        val i: Int

        if (this.timeUntilLaunch >= 100) {
            i = abs(this.timeUntilLaunch / 100)
        } else {
            i = 1
        }

        if ((this.getLaunched() || this.launchPhase == EnumLaunchPhase.IGNITED.ordinal && this.rand.nextInt(i) == 0) && !ConfigManagerCore.disableSpaceshipParticles && this.hasValidFuel()) {
            if (this.world.isRemote) {
                this.spawnParticles(this.getLaunched())
            }
        }


        if (fluidTank.getFluid() == null) {
            fluidTank.setFluid(FluidStack(GSFluids.Helium3, 1535))
        }
    }

    override fun shouldMoveClientSide(): Boolean {
        return true
    }

    protected open fun spawnParticles(launched: Boolean) {
        val sinPitch: Double = sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
        var x1: Double = 2 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
        var z1: Double = 2 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
        var y1: Double = 2 * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)

        if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.targetVec != null) {
            var modifier: Double = this.posY - (this.targetVec)!!.getY()
            modifier = max(modifier, 1.0)
            x1 *= modifier / 60.0
            y1 *= modifier / 60.0
            z1 *= modifier / 60.0
        }

        val y: Double = this.prevPosY + (this.posY - this.prevPosY) - 0.4

        if (!this.isDead) {
            val riddenByEntity: EntityLivingBase? = if (this.getPassengers().isEmpty() || this.getPassengers()
                    .get(0) !is EntityLivingBase
            ) null else this.getPassengers().get(0) as EntityLivingBase?
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(
                    this.posX + 0.2 - this.rand.nextDouble() / 10 + x1,
                    y,
                    this.posZ + 0.2 - this.rand.nextDouble() / 10 + z1
                ),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(
                    this.posX - 0.2 + this.rand.nextDouble() / 10 + x1,
                    y,
                    this.posZ + 0.2 - this.rand.nextDouble() / 10 + z1
                ),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(
                    this.posX - 0.2 + this.rand.nextDouble() / 10 + x1,
                    y,
                    this.posZ - 0.2 + this.rand.nextDouble() / 10 + z1
                ),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(
                    this.posX + 0.2 - this.rand.nextDouble() / 10 + x1,
                    y,
                    this.posZ - 0.2 + this.rand.nextDouble() / 10 + z1
                ),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(this.posX + x1, y, this.posZ + z1),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(this.posX + 0.2 + x1, y, this.posZ + z1),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(this.posX - 0.2 + x1, y, this.posZ + z1),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(this.posX + x1, y, this.posZ + 0.2 + z1),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle",
                Vector3(this.posX + x1, y, this.posZ - 0.2 + z1),
                Vector3(x1, y1, z1),
                arrayOf<Any?>(riddenByEntity)
            )
        }
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        this.rocketType = (IRocketType.EnumRocketType.entries)!![(buffer)!!.readInt()]
        super.decodePacketdata(buffer)
        this.posX = (buffer)!!.readDouble() / 8000.0
        this.posY = (buffer)!!.readDouble() / 8000.0
        this.posZ = (buffer)!!.readDouble() / 8000.0

        val fluid: FluidStack = FluidStack(GSFluids.Helium3, (buffer)!!.readInt())
        this.fluidTank.setFluid(fluid)
    }

    override fun getNetworkedData(list: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }
        (list)!!.add(if (this.rocketType != null) this.rocketType!!.getIndex() else 0)
        super.getNetworkedData(list)
        (list)!!.add(this.posX * 8000.0)
        (list)!!.add(this.posY * 8000.0)
        (list)!!.add(this.posZ * 8000.0)

        (list)!!.add(this.fluidTank.getFluidAmount())
    }

    override fun onReachAtmosphere() {
        if (this.world.isRemote) {
            //stop the sounds on the client - but do not reset, the rocket may start again
            this.stopRocketSound()
            return
        }

        GCLog.debug("[Serverside] Cargo rocket reached space, heading to " + this.destinationFrequency)
        this.setTarget(true, this.destinationFrequency)

        if (this.targetVec != null) {
            GCLog.debug("Destination location = " + this.targetVec.toString())
            if (this.targetDimension != GCCoreUtil.getDimensionID(this.world)) {
                GCLog.debug("Destination is in different dimension: " + this.targetDimension)
                val targetDim: WorldProvider? = WorldUtil.getProviderForDimensionServer(this.targetDimension)
                if (targetDim != null && targetDim.world is WorldServer) {
                    GCLog.debug("Loaded destination dimension " + this.targetDimension)
                    this.setPosition(
                        ((this.targetVec)!!.getX() + 0.5f).toDouble(),
                        ((this.targetVec)!!.getY() + 800).toDouble(),
                        ((this.targetVec)!!.getZ() + 0.5f).toDouble()
                    )
                    val e: Entity? = WorldUtil.transferEntityToDimension(
                        this,
                        this.targetDimension,
                        (targetDim.world as WorldServer?)!!,
                        false,
                        null
                    )

                    if (e is EntityCustomCargoRocket) {
                        GCLog.debug("Cargo rocket arrived at destination dimension, going into landing mode.")
                        e.setPosition(
                            ((this.targetVec)!!.getX() + 0.5f).toDouble(),
                            ((this.targetVec)!!.getY() + 800).toDouble(),
                            ((this.targetVec)!!.getZ() + 0.5f).toDouble()
                        )
                        e.setLaunchPhase(EnumLaunchPhase.LANDING)
                        //No setDead() following successful transferEntityToDimension() - see javadoc on that
                    } else {
                        GCLog.info("Error: failed to recreate the cargo rocket in landing mode on target planet.")
                        e!!.setDead()
                        this.setDead()
                    }
                    return
                }
                GCLog.info("Error: the server failed to load the dimension the cargo rocket is supposed to land in. Destroying rocket!")
                this.setDead()
                return
            } else {
                GCLog.debug("Cargo rocket going into landing mode in same destination.")
                this.setPosition(
                    ((this.targetVec)!!.getX() + 0.5f).toDouble(),
                    ((this.targetVec)!!.getY() + 800).toDouble(),
                    ((this.targetVec)!!.getZ() + 0.5f).toDouble()
                )
                this.setLaunchPhase(EnumLaunchPhase.LANDING)
                return
            }
        } else {
            GCLog.info("Error: the cargo rocket failed to find a valid landing spot when it reached space.")
            this.setDead()
        }
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (!this.world.isRemote && player is EntityPlayerMP) {
            player.sendSystemMessage(
                Component.literal(
                    this.fluidTank.getFluid()!!.getLocalizedName() + ": " + this.fluidTank.getFluidAmount()
                )
            )
            //MarsUtil.openCargoRocketInventory((EntityPlayerMP) player, this);
        }

        return false
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        if (world.isRemote) return
        nbt.putInt("Type", this.rocketType!!.getIndex())

        if (this.fluidTank.getFluid() != null) {
            nbt.put("fluidTank", this.fluidTank.writeToNBT(NBTTagCompound()))
        }
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        this.rocketType = IRocketType.EnumRocketType.entries[nbt.getIntOr("Type", 0)]

        if (nbt.containsTag("fluidTank")) {
            this.fluidTank.readFromNBT(nbt.getCompoundOrEmpty("fluidTank"))
        }
    }

    override fun getType(): IRocketType.EnumRocketType? {
        return this.rocketType
    }

    override fun getContainerSize(): Int {
        if (this.rocketType == null) {
            return 0
        }
        return 0 //this.rocketType.getInventorySpace();
    }

    override fun onWorldTransferred(world: World?) {
        if (this.targetVec != null) {
            this.setPosition(
                ((this.targetVec)!!.getX() + 0.5f).toDouble(),
                ((this.targetVec)!!.getY() + 800).toDouble(),
                ((this.targetVec)!!.getZ() + 0.5f).toDouble()
            )
            this.setLaunchPhase(EnumLaunchPhase.LANDING)
        } else {
            this.setDead()
        }
    }

    override fun getRocketTier(): Int {
        return Int.MAX_VALUE
    }

    override fun getPreLaunchWait(): Int {
        return 20
    }

    override fun getItemsDropped(droppedItemList: MutableList<ItemStack?>?): MutableList<ItemStack>? {
        super.getItemsDropped(droppedItemList)
        val rocket: ItemStack = ItemStack(MarsItems.rocketMars, 1, this.rocketType!!.getIndex())
        rocket.setTagCompound(NBTTagCompound())
        rocket.getTagCompound()!!.putInt("RocketFuel", this.fuelTank.getFluidAmount())
        (droppedItemList)!!.add(rocket)
        return ((droppedItemList as MutableList<ItemStack>?))!!
    }

    override fun isPlayerRocket(): Boolean {
        return false
    }

    override fun getOnPadYOffset(): Double {
        return -0.05
    }

    override fun getRenderOffsetY(): Float {
        return -0.1f
    }
}
