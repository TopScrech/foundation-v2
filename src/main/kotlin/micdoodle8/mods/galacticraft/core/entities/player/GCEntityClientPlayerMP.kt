package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.event.ZeroGravityEvent
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.client.EventHandlerClient
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.MoverType
import net.minecraft.init.MobEffects
import net.minecraft.init.SoundEvents
import net.minecraft.stats.RecipeBook
import net.minecraft.stats.StatisticsManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.EnumDifficulty
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.atan

open class GCEntityClientPlayerMP constructor(
    mcIn: Minecraft,
    worldIn: World,
    netHandler: NetHandlerPlayClient,
    statFileWriter: StatisticsManager,
    book: RecipeBook
) : EntityPlayerSP(mcIn, worldIn, netHandler, statFileWriter, book) {
    private var lastIsFlying = false
    private var sneakLast = false
    private var lastLandingTicks = 0
    private var checkedCape = false
    private var galacticraftCape: ResourceLocation? = null

    override fun wakeUpPlayer(immediately: Boolean, updateWorldFlag: Boolean, setSpawn: Boolean) {
        if (!ClientProxyCore.playerClientHandler.wakeUpPlayer(this, immediately, updateWorldFlag, setSpawn)) {
            super.wakeUpPlayer(immediately, updateWorldFlag, setSpawn)
        }
    }

    override fun isEntityInsideOpaqueBlock(): Boolean {
        return ClientProxyCore.playerClientHandler.isEntityInsideOpaqueBlock(this, super.isEntityInsideOpaqueBlock())
    }

    override fun isServerWorld(): Boolean {
        return true
    }


    override fun onLivingUpdate() {
        ClientProxyCore.playerClientHandler.onLivingUpdatePre(this)
        try {
            if (this.world.provider is IZeroGDimension) {
                //  from: EntityPlayerSP
                if (this.sprintingTicksLeft > 0) {
                    --this.sprintingTicksLeft

                    if (this.sprintingTicksLeft == 0) {
                        this.setSprinting(false)
                    }
                }

                if (this.sprintToggleTimer > 0) {
                    --this.sprintToggleTimer
                }

                this.prevTimeInPortal = this.timeInPortal

                if (this.inPortal) {
                    if (this.mc.currentScreen != null && !this.mc.currentScreen!!.doesGuiPauseGame()) {
                        this.mc.displayGuiScreen(null as GuiScreen?)
                    }

                    if (this.timeInPortal == 0.0f) {
                        this.mc.getSoundHandler().playSound(
                            PositionedSoundRecord.getMasterRecord(
                                SoundEvents.BLOCK_PORTAL_TRIGGER,
                                this.rand.nextFloat() * 0.4f + 0.8f
                            )
                        )
                    }

                    this.timeInPortal += 0.0125f

                    if (this.timeInPortal >= 1.0f) {
                        this.timeInPortal = 1.0f
                    }

                    this.inPortal = false
                } else if (this.isPotionActive(MobEffects.NAUSEA) && this.getActivePotionEffect(MobEffects.NAUSEA)!!
                        .getDuration() > 60
                ) {
                    this.timeInPortal += 0.006666667f

                    if (this.timeInPortal > 1.0f) {
                        this.timeInPortal = 1.0f
                    }
                } else {
                    if (this.timeInPortal > 0.0f) {
                        this.timeInPortal -= 0.05f
                    }

                    if (this.timeInPortal < 0.0f) {
                        this.timeInPortal = 0.0f
                    }
                }

                if (this.timeUntilPortal > 0) {
                    --this.timeUntilPortal
                }

                val flag1: Boolean = this.movementInput.sneak
                val sprintlevel: Float = 0.8f
                val flag2: Boolean = this.movementInput.moveForward >= sprintlevel
                this.movementInput.updatePlayerMoveState()

                if (this.isHandActive() && !this.isRiding()) {
                    this.movementInput.moveStrafe *= 0.2f
                    this.movementInput.moveForward *= 0.2f
                    this.sprintToggleTimer = 0
                }

                //CUSTOM-------------------
                val stats: GCPlayerStatsClient = (GCPlayerStatsClient.Companion.get(this))!!
                if (stats.getLandingTicks() > 0) {
                    this.movementInput.moveStrafe *= 0.5f
                    this.movementInput.moveForward *= 0.5f
                }

                //-----------END CUSTOM
                this.pushOutOfBlocks(
                    this.posX - this.width.toDouble() * 0.35,
                    this.getEntityBoundingBox().minY + 0.5,
                    this.posZ + this.width.toDouble() * 0.35
                )
                this.pushOutOfBlocks(
                    this.posX - this.width.toDouble() * 0.35,
                    this.getEntityBoundingBox().minY + 0.5,
                    this.posZ - this.width.toDouble() * 0.35
                )
                this.pushOutOfBlocks(
                    this.posX + this.width.toDouble() * 0.35,
                    this.getEntityBoundingBox().minY + 0.5,
                    this.posZ - this.width.toDouble() * 0.35
                )
                this.pushOutOfBlocks(
                    this.posX + this.width.toDouble() * 0.35,
                    this.getEntityBoundingBox().minY + 0.5,
                    this.posZ + this.width.toDouble() * 0.35
                )
                val flag4: Boolean =
                    this.getFoodStats().getFoodLevel().toFloat() > 6.0f || this.capabilities.allowFlying

                if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= 0.8f && !this.isSprinting() && flag4 && !this.isHandActive() && !this.isPotionActive(
                        MobEffects.BLINDNESS
                    )
                ) {
                    if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                        this.sprintToggleTimer = 7
                    } else {
                        this.setSprinting(true)
                    }
                }

                if (!this.isSprinting() && this.movementInput.moveForward >= sprintlevel && flag4 && !this.isHandActive() && !this.isPotionActive(
                        MobEffects.BLINDNESS
                    ) && this.mc.gameSettings.keyBindSprint.isKeyDown()
                ) {
                    this.setSprinting(true)
                }

                if (this.isSprinting() && (this.movementInput.moveForward < sprintlevel || this.collidedHorizontally || !flag4)) {
                    this.setSprinting(false)
                }

                //Omit flying toggles - flying will be controlled by freefall status
                if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
                    if (this.movementInput.sneak) {
                        this.motionY -= (this.capabilities.getFlySpeed() * 3.0f).toDouble()
                    }

                    if (this.movementInput.jump) {
                        this.motionY += (this.capabilities.getFlySpeed() * 3.0f).toDouble()
                    }
                }

                //Omit horse jumping - no horse jumping in space!

                // -from: EntityPlayer

                //Omit fly toggle timer
                if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.world.getGameRules()
                        .getBoolean("naturalRegeneration")
                ) {
                    if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
                        this.heal(1.0f)
                    }

                    if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
                        this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 1)
                    }
                }

                this.inventory.decrementAnimations()
                this.prevCameraYaw = this.cameraYaw

                //  from: EntityLivingBase
                if (this.newPosRotationIncrements > 0) {
                    val d0: Double =
                        this.posX + (this.interpTargetX - this.posX) / this.newPosRotationIncrements.toDouble()
                    val d1: Double =
                        this.posY + (this.interpTargetY - this.posY) / this.newPosRotationIncrements.toDouble()
                    val d2: Double =
                        this.posZ + (this.interpTargetZ - this.posZ) / this.newPosRotationIncrements.toDouble()
                    val d3: Double = MathHelper.wrapDegrees(this.interpTargetYaw - this.rotationYaw.toDouble())
                    this.rotationYaw =
                        (this.rotationYaw.toDouble() + d3 / this.newPosRotationIncrements.toDouble()).toFloat()
                    this.rotationPitch =
                        (this.rotationPitch.toDouble() + (this.interpTargetPitch - this.rotationPitch.toDouble()) / this.newPosRotationIncrements.toDouble()).toFloat()
                    --this.newPosRotationIncrements
                    this.setPosition(d0, d1, d2)
                    this.setRotation(this.rotationYaw, this.rotationPitch)
                } else if (!this.isServerWorld()) {
                    this.motionX *= 0.98
                    this.motionY *= 0.98
                    this.motionZ *= 0.98
                }

                if (abs(this.motionX) < 0.005) {
                    this.motionX = 0.0
                }

                if (abs(this.motionY) < 0.005) {
                    this.motionY = 0.0
                }

                if (abs(this.motionZ) < 0.005) {
                    this.motionZ = 0.0
                }

                this.world.profiler.startSection("ai")

                if (this.isMovementBlocked()) {
                    this.isJumping = false
                    this.moveStrafing = 0.0f
                    this.moveForward = 0.0f
                    this.randomYawVelocity = 0.0f
                } else this.updateEntityActionState()

                this.world.profiler.endSection()
                this.world.profiler.startSection("travel")
                this.moveStrafing *= 0.98f
                this.moveForward *= 0.98f
                this.randomYawVelocity *= 0.9f

                // CUSTOM--------------
                val aABB: AxisAlignedBB = this.getEntityBoundingBox()
                if ((aABB.minY % 1.0) == 0.5) this.setEntityBoundingBox(aABB.offset(0.0, 0.00001, 0.0))

                //-----------END CUSTOM

                //NOTE: No Elytra movement from this.updateElytra() in a zero G dimension
                this.travel(this.moveStrafing, this.moveVertical, this.moveForward)
                this.world.profiler.endSection()
                this.world.profiler.startSection("push")

                if (!this.world.isRemote) {
                    this.collideWithNearbyEntities()
                }

                this.world.profiler.endSection()

                // -from: EntityPlayer

                //Omit IAttributeInstance - seems relevant only on server

                //Omit        this.jumpMovementFactor = this.speedInAir;
                //(no bounding in space)
                var f: Float = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
                var f1: Float = (atan(-this.motionY * 0.20000000298023224) * 15.0).toFloat()

                if (f > 0.1f) {
                    f = 0.1f
                }

                if (!this.onGround || this.getHealth() <= 0.0f) {
                    f = 0.0f
                }

                if (this.onGround || this.getHealth() <= 0.0f) {
                    f1 = 0.0f
                }

                this.cameraYaw += (f - this.cameraYaw) * 0.4f
                this.cameraPitch += (f1 - this.cameraPitch) * 0.8f

                if (this.getHealth() > 0.0f && !this.isSpectator()) {
                    var axisalignedbb: AxisAlignedBB? = null

                    if (this.getRidingEntity() != null && !this.getRidingEntity()!!.isDead) {
                        axisalignedbb =
                            this.getEntityBoundingBox().union(this.getRidingEntity()!!.getEntityBoundingBox())
                                .grow(1.0, 0.0, 1.0)
                    } else {
                        axisalignedbb = this.getEntityBoundingBox().grow(1.0, 0.5, 1.0)
                    }

                    val list: MutableList<Entity?> =
                        this.world.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb)

                    for (i: Int in list.indices) {
                        val entity: Entity = list.get(i) as Entity

                        if (!entity.isDead) {
                            entity.onCollideWithPlayer(this)
                        }
                    }
                }

                //  from: EntityPlayerSP
                //(modified CUSTOM)
                if (this.lastIsFlying != this.capabilities.isFlying) {
                    this.lastIsFlying = this.capabilities.isFlying
                    this.sendPlayerAbilities()
                }
            } else {
                super.onLivingUpdate()
            }
        } catch (e: RuntimeException) {
            FMLLog.severe("A mod has crashed while Minecraft was doing a normal player tick update.  See details below.  GCEntityClientPlayerMP is in this because that is the player class name when Galacticraft is installed.  This is =*NOT*= a bug in Galacticraft, please report it to the mod indicated by the first lines of the crash report.")
            throw (e)
        }
        ClientProxyCore.playerClientHandler.onLivingUpdatePost(this)
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
        super.move(type, x, y, z)
        ClientProxyCore.playerClientHandler.move(this, type, x, y, z)
    }

    override fun onUpdate() {
        ClientProxyCore.playerClientHandler.onUpdate(this)
        super.onUpdate()
    }

    override fun isSneaking(): Boolean {
        if (this.world.provider is IZeroGDimension) {
            val zeroGEvent: ZeroGravityEvent = ZeroGravityEvent.SneakOverride(this)
            MinecraftForge.EVENT_BUS.post(zeroGEvent)
            if (zeroGEvent.isCanceled()) {
                return super.isSneaking()
            }

            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.Companion.get(this))!!
            if (stats.getLandingTicks() > 0) {
                if (this.lastLandingTicks == 0) this.lastLandingTicks = stats.getLandingTicks()

                this.sneakLast = stats.getLandingTicks() < this.lastLandingTicks
                return sneakLast
            } else this.lastLandingTicks = 0
            if ((stats.getFreefallHandler())!!.pjumpticks > 0) {
                this.sneakLast = true
                return true
            }
            if (EventHandlerClient.sneakRenderOverride) {
                if (this.movementInput != null && this.movementInput.sneak != this.sneakLast) {
                    return false
                }
                //                if (stats.freefallHandler.testFreefall(this)) return false;
                if (stats.isInFreefall() || (stats.getFreefallHandler())!!.onWall) {
                    this.sneakLast = false
                    return false
                }
            }
            this.sneakLast = if (this.movementInput == null) false else this.movementInput.sneak
        } else {
            this.sneakLast = false
            if (EventHandlerClient.sneakRenderOverride && this.onGround && this.inventory.getCurrentItem() != null && this.inventory.getCurrentItem()
                    .getItem() is IHoldableItem && (this.getRidingEntity() !is ICameraZoomEntity)
            ) {
                val holdableItem: IHoldableItem = this.inventory.getCurrentItem().getItem() as IHoldableItem

                if (holdableItem.shouldCrouch(this)) {
                    return true
                }
            }
        }
        return super.isSneaking()
    }

    override fun getEyeHeight(): Float {
        val f: Float = eyeHeight

        if (this.isPlayerSleeping()) {
            return 0.2f
        }

        var ySize: Float = 0.0f
        if (this.world.provider is IZeroGDimension) {
            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.Companion.get(this))!!
            if (stats.getLandingTicks() > 0) {
                ySize = ((stats)!!.getLandingYOffset())!![stats.getLandingTicks()]
                if (this.movementInput.sneak && ySize < 0.08f) {
                    ySize = 0.08f
                }
            } else if ((stats.getFreefallHandler())!!.pjumpticks > 0) {
                ySize = 0.01f * (stats.getFreefallHandler())!!.pjumpticks
            } else if (this.isSneaking() && !stats.isInFreefall() && !(stats.getFreefallHandler())!!.onWall) {
                ySize = 0.08f
            }
        } else if (this.isSneaking() && this.movementInput != null && this.movementInput.sneak) {
            ySize = 0.08f
        }

        return f - ySize
    }

    @SideOnly(Side.CLIENT)
    override fun getBedOrientationInDegrees(): Float {
        return ClientProxyCore.playerClientHandler.getBedOrientationInDegrees(this, super.getBedOrientationInDegrees())
    }

    //
    //    @Override
    //    @SideOnly(Side.CLIENT)
    //    public void setVelocity(double xx, double yy, double zz)
    //    {
    //    	if (this.world.provider instanceof WorldProviderOrbit)
    //    	{
    //    		((WorldProviderOrbit)this.world.provider).setVelocityClient(this, xx, yy, zz);
    //    	}
    //    	super.setVelocity(xx, yy, zz);
    //    }
    //
    /*@Override
    public void setInPortal()
    {
    	if (!(this.world.provider instanceof IGalacticraftWorldProvider))
    	{
    		super.setInPortal();
    	}
    } TODO Fix disable of portal */
    override fun getLocationCape(): ResourceLocation? {
        if (this.getRidingEntity() is EntitySpaceshipBase) {
            // Don't draw any cape if riding a rocket (the cape renders outside the rocket model!)
            return null
        }

        val vanillaCape: ResourceLocation? = super.getLocationCape()

        if (!this.checkedCape) {
            val networkplayerinfo: NetworkPlayerInfo? = this.getPlayerInfo()
            this.galacticraftCape = ClientProxyCore.capeMap.get(networkplayerinfo!!.getGameProfile().getName())
            this.checkedCape = true
        }

        if ((ConfigManagerCore.overrideCapes || vanillaCape == null) && galacticraftCape != null) {
            return galacticraftCape
        }

        return vanillaCape
    }

    @SideOnly(Side.CLIENT)
    override fun getBrightnessForRender(): Int {
        var height: Double = this.posY + this.getEyeHeight().toDouble()
        if (height > 255.0) height = 255.0
        val blockpos: BlockPos = BlockPos(this.posX, height, this.posZ)
        return if (this.world.isBlockLoaded(blockpos)) this.world.getCombinedLight(blockpos, 0) else 0
    }
}
