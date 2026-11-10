package galaxyspace.systems.SolarSystem.planets.mars.entities

import micdoodle8.mods.galacticraft.core.network.PacketEntityUpdate.IEntityFullSync
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import galaxyspace.systems.SolarSystem.planets.mars.entities.EntityMarsRover
import kotlin.math.pow
import io.netty.buffer.ByteBuf
import java.io.IOException
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPad
import java.util.UUID
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity
import micdoodle8.mods.galacticraft.api.entity.IDockable
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.TransformerHooks
import micdoodle8.mods.galacticraft.core.entities.IControllableEntity
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.NetworkUtil
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.network.PacketEntityUpdate
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.model.ModelBase
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.MoverType
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

open class EntityMarsRover constructor(var1: World) : Entity(var1), ICameraZoomEntity, IInventory, IPacketReceiver,
    IDockable, IControllableEntity, PacketEntityUpdate.IEntityFullSync {
    @JvmField
    var buggyFuelTank: FluidTank = FluidTank(tankCapacity)

    @JvmField
    protected var ticks: Long = 0

    @JvmField
    var buggyType: Int = 0

    @JvmField
    var currentDamage: Int = 0

    @JvmField
    var timeSinceHit: Int = 0

    @JvmField
    var rockDirection: Int = 0

    @JvmField
    var speed: Double

    @JvmField
    var wheelRotationZ: Float = 0f

    @JvmField
    var wheelRotationX: Float = 0f

    @JvmField
    var maxSpeed: Float = 0.8f

    @JvmField
    var accel: Float = 0.4f

    @JvmField
    var turnFactor: Float = 3.0f

    @JvmField
    var texture: String? = null
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(60, ItemStack.EMPTY)

    @JvmField
    var boatX: Double = 0.0

    @JvmField
    var boatY: Double = 0.0

    @JvmField
    var boatZ: Double = 0.0

    @JvmField
    var boatYaw: Double = 0.0

    @JvmField
    var boatPitch: Double = 0.0

    @JvmField
    var boatPosRotationIncrements: Int = 0
    private var landingPad: IFuelDock? = null
    private var timeClimbing = 0
    private var shouldClimb = false


    init {
        this.setSize(1.9f, 1.6f)
        this.speed = 0.0
        this.preventEntitySpawning = true
        this.dataManager.register<Int>(CURRENT_DAMAGE, 0)
        this.dataManager.register<Int>(TIME_SINCE_HIT, 0)
        this.dataManager.register<Int>(ROCK_DIRECTION, 1)
        this.ignoreFrustumCheck = true
        this.isImmuneToFire = true

        if (var1 != null && var1.isRemote) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        }
    }

    constructor(var1: World, var2: Double, var4: Double, var6: Double, type: Int) : this(var1) {
        this.setPosition(var2, var4, var6)
        this.setBuggyType(type)
        this.stacks = NonNullList.withSize<ItemStack>(this.buggyType * 18, ItemStack.EMPTY)
    }

    open fun getScaledFuelLevel(i: Int): Int {
        val fuelLevel: Double =
            (if (this.buggyFuelTank.getFluid() == null) 0 else this.buggyFuelTank.getFluid()!!.amount).toDouble()

        return (fuelLevel * i / tankCapacity).toInt()
    }

    open fun getModel(): ModelBase? {
        return null
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(GCItems.buggy, 1, this.buggyType)
    }

    open fun getType(): Int {
        return this.buggyType
    }

    override fun entityInit() {
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun getMountedYOffset(): Double {
        return 1.0
    }

    override fun canBeCollidedWith(): Boolean {
        return !this.isDead
    }

    open fun setBuggyType(par1: Int) {
        this.buggyType = par1
    }

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            val offsetX: Double = cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D + 114.8) * -0.5
            val offsetZ: Double = sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D + 114.8) * -0.5
            passenger.setPosition(
                this.posX + offsetX,
                this.posY + 0.4f + passenger.getYOffset() + getMountedYOffset(),
                this.posZ + offsetZ
            )
        }
    }

    override fun setPositionRotationAndMotion(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        motX: Double,
        motY: Double,
        motZ: Double,
        onGround: Boolean
    ) {
        if (this.world.isRemote) {
            this.boatX = x
            this.boatY = y
            this.boatZ = z
            this.boatYaw = yaw.toDouble()
            this.boatPitch = pitch.toDouble()
            this.motionX = motX
            this.motionY = motY
            this.motionZ = motZ
            this.boatPosRotationIncrements = 5
        } else {
            this.setPosition(x, y, z)
            this.setRotation(yaw, pitch)
            this.motionX = motX
            this.motionY = motY
            this.motionZ = motZ
        }
    }

    override fun performHurtAnimation() {
        this.dataManager.set<Int>(ROCK_DIRECTION, -this.dataManager.get<Int>(ROCK_DIRECTION))
        this.dataManager.set<Int>(TIME_SINCE_HIT, 10)
        this.dataManager.set<Int>(CURRENT_DAMAGE, this.dataManager.get<Int>(CURRENT_DAMAGE) * 5)
    }

    override fun attackEntityFrom(var1: DamageSource, var2: Float): Boolean {
        if (this.isDead || var1 == DamageSource.CACTUS) {
            return true
        } else {
            val e: Entity? = var1.getTrueSource()
            val flag: Boolean = e is EntityPlayer && e.capabilities.isCreativeMode

            if (this.isEntityInvulnerable(var1) || (e is EntityLivingBase && e !is EntityPlayer)) {
                return false
            } else {
                this.dataManager.set<Int>(ROCK_DIRECTION, -this.dataManager.get<Int>(ROCK_DIRECTION))
                this.dataManager.set<Int>(TIME_SINCE_HIT, 10)
                this.dataManager.set<Int>(
                    CURRENT_DAMAGE,
                    (this.dataManager.get<Int>(CURRENT_DAMAGE) + var2 * 10).toInt()
                )
                this.markVelocityChanged()

                if (e is EntityPlayer && e.capabilities.isCreativeMode) {
                    this.dataManager.set<Int>(CURRENT_DAMAGE, 100)
                }

                if (flag || this.dataManager.get<Int>(CURRENT_DAMAGE) > 2) {
                    if (!this.getPassengers().isEmpty()) {
                        this.removePassengers()
                    }

                    if (flag) {
                        this.setDead()
                    } else {
                        this.setDead()
                        if (!this.world.isRemote) {
                            this.dropBuggyAsItem()
                        }
                    }
                    this.setDead()
                }

                return true
            }
        }
    }

    open fun dropBuggyAsItem() {
        val dropped: MutableList<ItemStack>? = this.getItemsDropped()

        if (dropped == null) {
            return
        }

        for (item: ItemStack in dropped) {
            val entityItem: EntityItem? = this.entityDropItem(item, 0f)

            if (item.hasTagCompound()) {
                entityItem!!.getItem().setTagCompound(item.getTagCompound()!!.copy())
            }
        }
    }

    open fun getItemsDropped(): MutableList<ItemStack> {
        val items: MutableList<ItemStack> = ArrayList<ItemStack>()

        val buggy: ItemStack = ItemStack(GCItems.buggy, 1, this.buggyType)
        buggy.setTagCompound(NBTTagCompound())
        buggy.getTagCompound()!!.setInteger("BuggyFuel", this.buggyFuelTank.getFluidAmount())
        items.add(buggy)

        for (item: ItemStack in this.stacks) {
            if (!item.isEmpty()) {
                items.add(item)
            }
        }

        return items
    }

    @SideOnly(Side.CLIENT)
    override fun setPositionAndRotationDirect(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        posRotationIncrements: Int,
        b: Boolean
    ) {
        if (!this.getPassengers().isEmpty()) {
            if (!this.getPassengers().contains(FMLClientHandler.instance().getClient().player)) {
                this.boatPosRotationIncrements = posRotationIncrements + 5
                this.boatX = x
                this.boatY = y
                this.boatZ = z
                this.boatYaw = yaw.toDouble()
                this.boatPitch = pitch.toDouble()
            }
        }
    }

    override fun onUpdate() {
        this.ticks++

        super.onUpdate()

        if (this.world.isRemote) {
            this.wheelRotationX += (sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 150.0f * (if (this.speed < 0) 1 else -1)).toFloat()
            this.wheelRotationX %= 360f
            this.wheelRotationZ = max(-30.0f, min(30.0f, this.wheelRotationZ * 0.9f))
        }

        if (this.world.isRemote && FMLClientHandler.instance()
                .getClient().player != this.world.getClosestPlayerToEntity(this, -1.0)
        ) {
            val x: Double
            val y: Double
            val var12: Double
            val z: Double
            if (this.boatPosRotationIncrements > 0) {
                x = this.posX + (this.boatX - this.posX) / this.boatPosRotationIncrements
                y = this.posY + (this.boatY - this.posY) / this.boatPosRotationIncrements
                z = this.posZ + (this.boatZ - this.posZ) / this.boatPosRotationIncrements
                var12 = MathHelper.wrapDegrees(this.boatYaw - this.rotationYaw)
                this.rotationYaw = (this.rotationYaw + var12 / this.boatPosRotationIncrements).toFloat()
                this.rotationPitch =
                    (this.rotationPitch + (this.boatPitch - this.rotationPitch) / this.boatPosRotationIncrements).toFloat()
                --this.boatPosRotationIncrements
                this.setPosition(x, y, z)
                this.setRotation(this.rotationYaw, this.rotationPitch)
            } else {
                x = this.posX + this.motionX
                y = this.posY + this.motionY
                z = this.posZ + this.motionZ
                if (!this.getPassengers().isEmpty()) {
                    this.setPosition(x, y, z)
                }

                if (this.onGround) {
                    this.motionX *= 0.5
                    this.motionY *= 0.5
                    this.motionZ *= 0.5
                }

                this.motionX *= 0.9900000095367432
                this.motionY *= 0.949999988079071
                this.motionZ *= 0.9900000095367432
            }
            return
        }

        if (this.dataManager.get<Int>(TIME_SINCE_HIT) > 0) {
            this.dataManager.set<Int>(TIME_SINCE_HIT, this.dataManager.get<Int>(TIME_SINCE_HIT) - 1)
        }

        if (this.dataManager.get<Int>(CURRENT_DAMAGE) > 0) {
            this.dataManager.set<Int>(CURRENT_DAMAGE, this.dataManager.get<Int>(CURRENT_DAMAGE) - 1)
        }

        if (!this.onGround) {
            this.motionY -= TransformerHooks.getGravityForEntity(this) * 0.5
        }

        if (this.inWater && this.speed > 0.2) {
            this.world.playSound(
                null,
                this.posX.toFloat().toDouble(),
                this.posY.toFloat().toDouble(),
                this.posZ.toFloat().toDouble(),
                SoundEvents.ENTITY_GENERIC_BURN,
                SoundCategory.NEUTRAL,
                0.5f,
                2.6f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8f
            )
        }

        this.speed *= 0.98

        if (this.speed > this.maxSpeed) {
            this.speed = this.maxSpeed.toDouble()
        }

        if (this.collidedHorizontally && this.shouldClimb) {
            this.speed *= 0.9
            this.motionY = 0.15 * ((-((this.timeClimbing) - 1).toDouble().pow(2.0)) / 250.0f) + 0.15f
            this.motionY = max(-0.15, this.motionY)
            this.shouldClimb = false
        }

        if ((this.motionX == 0.0 || this.motionZ == 0.0) && !this.onGround) {
            this.timeClimbing++
        } else {
            this.timeClimbing = 0
        }

        if (this.world.isRemote && this.buggyFuelTank.getFluid() != null && this.buggyFuelTank.getFluid()!!.amount > 0) {
            this.motionX = -(this.speed * cos((this.rotationYaw - 90f) / Constants.RADIANS_TO_DEGREES_D))
            this.motionZ = -(this.speed * sin((this.rotationYaw - 90f) / Constants.RADIANS_TO_DEGREES_D))
        }

        if (this.world.isRemote) {
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)
        }

        if (!this.world.isRemote && abs(this.motionX * this.motionZ) > 0.000001) {
            val d: Double = this.motionX * this.motionX + this.motionZ * this.motionZ

            if (d != 0.0 && this.ticks % (MathHelper.floor(2 / d) + 1) == 0L) {
                this.removeFuel(1)
            }
        }

        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.world.isRemote) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketEntityUpdate(this))
        } else if (this.ticks % 5 == 0L) {
            (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                PacketEntityUpdate(this),
                NetworkRegistry.TargetPoint(
                    GCCoreUtil.getDimensionID(this.world),
                    this.posX,
                    this.posY,
                    this.posZ,
                    50.0
                )
            )
            (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                PacketDynamic(this),
                NetworkRegistry.TargetPoint(
                    GCCoreUtil.getDimensionID(this.world),
                    this.posX,
                    this.posY,
                    this.posZ,
                    50.0
                )
            )
        }
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }
        (sendData)!!.add(this.buggyType)
        (sendData)!!.add(this.buggyFuelTank)
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        this.buggyType = (buffer)!!.readInt()

        try {
            this.buggyFuelTank = NetworkUtil.readFluidTank(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        this.buggyType = nbt.getInteger("buggyType")
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        if (nbt.hasKey("fuelTank")) {
            this.buggyFuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"))
        }
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        if (world.isRemote) return
        nbt.setInteger("buggyType", this.buggyType)
        val var2: NBTTagList = NBTTagList()

        if (this.buggyFuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", this.buggyFuelTank.writeToNBT(NBTTagCompound()))
        }

        ItemStackHelper.saveAllItems(nbt, stacks)
    }

    override fun getSizeInventory(): Int {
        return this.buggyType * 18
    }

    override fun getStackInSlot(index: Int): ItemStack {
        return this.stacks.get(index)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        return ItemStackHelper.getAndRemove(this.stacks, index)
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }

    override fun getName(): String {
        return "MarsRover"
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(var1: EntityPlayer): Boolean {
        return !this.isDead && var1.getDistanceSq(this) <= 64.0
    }

    override fun markDirty() {
    }

    //We don't use these because we use forge containers
    override fun openInventory(player: EntityPlayer) {
    }

    //We don't use these because we use forge containers
    override fun closeInventory(player: EntityPlayer) {
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {
    }

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (this.world.isRemote) {
            if (this.getPassengers().isEmpty()) {
                player.sendMessage(
                    TextComponentString(
                        GameSettings.getKeyDisplayString((KeyHandlerClient.leftKey)!!.getKeyCode()) + " / " + GameSettings.getKeyDisplayString(
                            (KeyHandlerClient.rightKey)!!.getKeyCode()
                        ) + "  - " + GCCoreUtil.translate("gui.buggy.turn.name")
                    )
                )
                player.sendMessage(
                    TextComponentString(
                        GameSettings.getKeyDisplayString((KeyHandlerClient.accelerateKey)!!.getKeyCode()) + "       - " + GCCoreUtil.translate(
                            "gui.buggy.accel.name"
                        )
                    )
                )
                player.sendMessage(
                    TextComponentString(
                        GameSettings.getKeyDisplayString((KeyHandlerClient.decelerateKey)!!.getKeyCode()) + "       - " + GCCoreUtil.translate(
                            "gui.buggy.decel.name"
                        )
                    )
                )
                player.sendMessage(
                    TextComponentString(
                        GameSettings.getKeyDisplayString(KeyHandlerClient.openFuelGui.getKeyCode()) + "       - " + GCCoreUtil.translate(
                            "gui.buggy.inv.name"
                        )
                    )
                )
            }

            return true
        } else {
            if (this.getPassengers().contains(player)) {
                this.removePassenger(player)

                return true
            } else {
                player.startRiding(this)
                return true
            }
        }
    }

    override fun pressKey(key: Int): Boolean {
        if (this.world.isRemote && (key == 6 || key == 8 || key == 9)) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_CONTROL_ENTITY,
                    GCCoreUtil.getDimensionID(this.world),
                    arrayOf<Any>(key)
                )
            )
            return true
        }

        when (key) {
            0 -> {
                this.speed += this.accel / 20.0
                this.stepHeight = 2.0f
                //this.shouldClimb = true;
                return true
            }

            1 -> {
                this.speed -= this.accel / 20.0
                this.stepHeight = 2.0f
                //this.shouldClimb = true;
                return true
            }

            2 -> {
                this.rotationYaw -= 0.5f * this.turnFactor
                this.wheelRotationZ = max(-30.0f, min(30.0f, this.wheelRotationZ + 0.5f))
                return true
            }

            3 -> {
                this.rotationYaw += 0.5f * this.turnFactor
                this.wheelRotationZ = max(-30.0f, min(30.0f, this.wheelRotationZ - 0.5f))
                return true
            }
        }

        return false
    }

    override fun isItemValidForSlot(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    override fun addFuel(liquid: FluidStack?, doDrain: Boolean): Int {
        if (this.landingPad != null) {
            return FluidUtil.fillWithGCFuel(this.buggyFuelTank, liquid, doDrain)
        }

        return 0
    }

    override fun removeFuel(amount: Int): FluidStack? {
        return this.buggyFuelTank.drain(amount, true)
    }

    override fun addCargo(stack: ItemStack?, doAdd: Boolean): ICargoEntity.EnumCargoLoadingState {
        if (this.buggyType == 0) {
            return ICargoEntity.EnumCargoLoadingState.NOINVENTORY
        }

        var count: Int = 0

        count = 0
        while (count < this.stacks.size) {
            val stackAt: ItemStack = this.stacks.get(count)

            if (stackAt != null && stackAt.getItem() === (stack)!!.getItem() && stackAt.getItemDamage() == (stack)!!.getItemDamage() && stackAt.getCount() < stackAt.getMaxStackSize()) {
                if (stackAt.getCount() + (stack)!!.getCount() <= stackAt.getMaxStackSize()) {
                    if (doAdd) {
                        stackAt.grow((stack)!!.getCount())
                        this.markDirty()
                    }

                    return ICargoEntity.EnumCargoLoadingState.SUCCESS
                } else {
                    //Part of the stack can fill this slot but there will be some left over
                    val origSize: Int = stackAt.getCount()
                    val surplus: Int = origSize + (stack)!!.getCount() - stackAt.getMaxStackSize()

                    if (doAdd) {
                        stackAt.setCount(stackAt.getMaxStackSize())
                        this.markDirty()
                    }

                    (stack)!!.setCount(surplus)
                    if (this.addCargo(stack, doAdd) == ICargoEntity.EnumCargoLoadingState.SUCCESS) {
                        return ICargoEntity.EnumCargoLoadingState.SUCCESS
                    }

                    stackAt.setCount(origSize)
                    return ICargoEntity.EnumCargoLoadingState.FULL
                }
            }
            count++
        }

        count = 0
        while (count < this.stacks.size) {
            val stackAt: ItemStack = this.stacks.get(count)

            if (!stackAt.isEmpty()) {
                if (doAdd) {
                    this.stacks.set(count, stack)
                    this.markDirty()
                }

                return ICargoEntity.EnumCargoLoadingState.SUCCESS
            }
            count++
        }

        return ICargoEntity.EnumCargoLoadingState.FULL
    }

    override fun removeCargo(doRemove: Boolean): ICargoEntity.RemovalResult {
        for (i: Int in this.stacks.indices) {
            val stackAt: ItemStack = this.getStackInSlot(i)

            if (stackAt != null) {
                val resultStack: ItemStack = stackAt.copy()
                resultStack.setCount(1)

                if (doRemove) {
                    stackAt.shrink(1)
                    this.markDirty()
                }

                return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.SUCCESS, resultStack)
            }
        }

        return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.EMPTY, ItemStack.EMPTY)
    }

    override fun setPad(pad: IFuelDock?) {
        this.landingPad = pad
    }

    override fun getLandingPad(): IFuelDock? {
        return this.landingPad
    }

    override fun onPadDestroyed() {
    }

    override fun isDockValid(dock: IFuelDock?): Boolean {
        return dock is TileEntityAdvLandingPad
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getOwnerUUID(): UUID? {
        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) !is EntityPlayer) {
            return null
        }

        return if (!this.getPassengers().isEmpty()) this.getPassengers().get(0).getPersistentID() else null
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun inFlight(): Boolean {
        return false
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun getRotateOffset(): Float {
        return 0f
    }

    companion object {
        private val CURRENT_DAMAGE: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntityMarsRover::class.java, DataSerializers.VARINT)
        private val TIME_SINCE_HIT: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntityMarsRover::class.java, DataSerializers.VARINT)
        private val ROCK_DIRECTION: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntityMarsRover::class.java, DataSerializers.VARINT)

        const val tankCapacity: Int = 3000
    }
}
