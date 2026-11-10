package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.api.event.ZeroGravityEvent
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.TransformerHooks
import micdoodle8.mods.galacticraft.core.dimension.SpinManager
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityFlying
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.MoverType
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.WorldProvider
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class FreefallHandler
constructor(private val stats: GCPlayerStatsClient) {
    private var pPrevMotionX = 0.0

    @JvmField
    var pPrevMotionY: Double = 0.0
    private var pPrevMotionZ = 0.0
    private var jetpackBoost = 0f
    private var pPrevdY = 0.0

    @JvmField
    var sneakLast: Boolean = false

    @JvmField
    var onWall: Boolean = false

    @JvmField
    var pjumpticks: Int = 0

    open fun testFreefall(player: EntityPlayer): Boolean {
        val zeroGEvent: ZeroGravityEvent = ZeroGravityEvent.InFreefall(player)
        MinecraftForge.EVENT_BUS.post(zeroGEvent)
        if (zeroGEvent.isCanceled()) {
            return false
        }


        //Test whether feet are on a block, also stops the login glitch
        val playerFeetOnY: Int = (player.getEntityBoundingBox().minY - 0.01).toInt()
        val xx: Int = MathHelper.floor(player.posX)
        val zz: Int = MathHelper.floor(player.posZ)
        val pos: BlockPos = BlockPos(xx, playerFeetOnY, zz)
        val state: IBlockState = player.world.getBlockState(pos)
        val b: Block = state.getBlock()
        if (b.getMaterial(state) !== Material.AIR && b !is BlockLiquid) {
            val blockYmax: Double
            if (b === GCBlocks.platform) blockYmax = playerFeetOnY + 1.0
            else blockYmax = playerFeetOnY + b.getBoundingBox(state, player.world, pos).maxY
            if (player.getEntityBoundingBox().minY - blockYmax < 0.01 && player.getEntityBoundingBox().minY - blockYmax > -0.5) {
                player.onGround = true
                if (player.getEntityBoundingBox().minY - blockYmax > 0.0) {
                    player.posY -= player.getEntityBoundingBox().minY - blockYmax
                    player.setEntityBoundingBox(
                        player.getEntityBoundingBox().offset(0.0, blockYmax - player.getEntityBoundingBox().minY, 0.0)
                    )
                } else if (b.canCollideCheck(player.world.getBlockState(BlockPos(xx, playerFeetOnY, zz)), false)) {
                    val offsetPos: BlockPos = BlockPos(xx, playerFeetOnY, zz)
                    val collisionBox: AxisAlignedBB? =
                        b.getCollisionBoundingBox(player.world.getBlockState(offsetPos), player.world, offsetPos)
                    if (collisionBox != null && collisionBox.intersects(player.getEntityBoundingBox())) {
                        player.posY -= player.getEntityBoundingBox().minY - blockYmax
                        player.setEntityBoundingBox(
                            player.getEntityBoundingBox()
                                .offset(0.0, blockYmax - player.getEntityBoundingBox().minY, 0.0)
                        )
                    }
                }
                return false
            }
        }
        return true
    }

    @SideOnly(Side.CLIENT)
    private fun testFreefall(p: EntityPlayerSP, flag: Boolean): Boolean {
        val world: World = p.world
        val worldProvider: WorldProvider? = world.provider
        if (worldProvider !is IZeroGDimension) {
            return false
        }
        val zeroGEvent: ZeroGravityEvent = ZeroGravityEvent.InFreefall(p)
        MinecraftForge.EVENT_BUS.post(zeroGEvent)
        if (zeroGEvent.isCanceled()) {
            return false
        }
        if (this.pjumpticks > 0 || (stats.isSsOnGroundLast() && p.movementInput.jump)) {
            return false
        }

        if (p.getRidingEntity() != null) {
            val e: Entity? = p.getRidingEntity()
            if (e is EntitySpaceshipBase) {
                return e.getLaunched()
            }
            if (e is EntityLanderBase) {
                return false
            }
            //TODO: should check whether lander has landed (whatever that means)
            //TODO: could check other ridden entities - every entity should have its own freefall check :(
        }

        //This is an "on the ground" check
        if (!flag) {
            return false
        } else {
            val rY: Float = p.rotationYaw % 360f
            var zreach: Double = 0.0
            var xreach: Double = 0.0
            if (rY < 80f || rY > 280f) {
                zreach = 0.2
            }
            if (rY < 170f && rY > 10f) {
                xreach = 0.2
            }
            if (rY < 260f && rY > 100f) {
                zreach = -0.2
            }
            if (rY < 350f && rY > 190f) {
                xreach = -0.2
            }
            val playerReach: AxisAlignedBB = p.getEntityBoundingBox().expand(xreach, 0.0, zreach)

            val checkBlockWithinReach: Boolean
            if (worldProvider is WorldProviderSpaceStation) {
                val spinManager: SpinManager = (worldProvider as WorldProviderSpaceStation).getSpinManager()
                checkBlockWithinReach =
                    playerReach.maxX >= spinManager.ssBoundsMinX && playerReach.minX <= spinManager.ssBoundsMaxX && playerReach.maxY >= spinManager.ssBoundsMinY && playerReach.minY <= spinManager.ssBoundsMaxY && playerReach.maxZ >= spinManager.ssBoundsMinZ && playerReach.minZ <= spinManager.ssBoundsMaxZ
                //Player is somewhere within the space station boundaries
            } else {
                checkBlockWithinReach = true
            }

            if (checkBlockWithinReach) {
                //Check if the player's bounding box is in the same block coordinates as any non-vacuum block (including torches etc)
                //If so, it's assumed the player has something close enough to grab onto, so is not in freefall
                //Note: breatheable air here means the player is definitely not in freefall
                val xm: Int = MathHelper.floor(playerReach.minX)
                val xx: Int = MathHelper.floor(playerReach.maxX)
                val ym: Int = MathHelper.floor(playerReach.minY)
                val yy: Int = MathHelper.floor(playerReach.maxY)
                val zm: Int = MathHelper.floor(playerReach.minZ)
                val zz: Int = MathHelper.floor(playerReach.maxZ)
                for (x: Int in xm..xx) {
                    for (y: Int in ym..yy) {
                        for (z: Int in zm..zz) {
                            //Blocks.AIR is hard vacuum - we want to check for that, here
                            val b: Block = world.getBlockState(BlockPos(x, y, z)).getBlock()
                            if (Blocks.AIR !== b && GCBlocks.brightAir !== b) {
                                this.onWall = true
                                return false
                            }
                        }
                    }
                }
            }
        }

        /*
        if (freefall)
		{
			//If that check didn't produce a result, see if the player is inside the walls
			//TODO: could apply special weightless movement here like Coriolis force - the player is inside the walls,  not touching them, and in a vacuum
			int quadrant = 0;
			double xd = p.posX - this.spinCentreX;
			double zd = p.posZ - this.spinCentreZ;
			if (xd<0)
			{
				if (xd<-Math.abs(zd))
				{
					quadrant = 2;
				} else
					quadrant = (zd<0) ? 3 : 1;
			} else
				if (xd>Math.abs(zd))
				{
					quadrant = 0;
				} else
					quadrant = (zd<0) ? 3 : 1;

			int ymin = MathHelper.floor(p.boundingBox.minY)-1;
			int ymax = MathHelper.floor(p.boundingBox.maxY);
			int xmin, xmax, zmin, zmax;

			switch (quadrant)
			{
			case 0:
				xmin = MathHelper.floor(p.boundingBox.maxX);
				xmax = this.ssBoundsMaxX - 1;
				zmin = MathHelper.floor(p.boundingBox.minZ)-1;
				zmax = MathHelper.floor(p.boundingBox.maxZ)+1;
				break;
			case 1:
				xmin = MathHelper.floor(p.boundingBox.minX)-1;
				xmax = MathHelper.floor(p.boundingBox.maxX)+1;
				zmin = MathHelper.floor(p.boundingBox.maxZ);
				zmax = this.ssBoundsMaxZ - 1;
				break;
			case 2:
				zmin = MathHelper.floor(p.boundingBox.minZ)-1;
				zmax = MathHelper.floor(p.boundingBox.maxZ)+1;
				xmin = this.ssBoundsMinX;
				xmax = MathHelper.floor(p.boundingBox.minX);
				break;
			case 3:
			default:
				xmin = MathHelper.floor(p.boundingBox.minX)-1;
				xmax = MathHelper.floor(p.boundingBox.maxX)+1;
				zmin = this.ssBoundsMinZ;
				zmax = MathHelper.floor(p.boundingBox.minZ);
				break;
			}

			//This block search could cost a lot of CPU (but client side) - maybe optimise later
			BLOCKCHECK0:
			for(int x = xmin; x <= xmax; x++)
				for (int z = zmin; z <= zmax; z++)
					for (int y = ymin; y <= ymax; y++)
						if (Blocks.AIR != this.worldProvider.world.getBlock(x, y, z))
						{
							freefall = false;
							break BLOCKCHECK0;
						}
		}*/
        this.onWall = false
        return true
    }

    @SideOnly(Side.CLIENT)
    open fun setupFreefallPre(p: EntityPlayerSP) {
        val dY: Double = p.motionY - pPrevMotionY
        jetpackBoost = 0f
        pPrevdY = dY
        pPrevMotionX = p.motionX
        pPrevMotionY = p.motionY
        pPrevMotionZ = p.motionZ
    }

    @SideOnly(Side.CLIENT)
    open fun freefallMotion(p: EntityPlayerSP) {
        var jetpackUsed: Boolean = false
        val dX: Double = p.motionX - pPrevMotionX
        val dY: Double = p.motionY - pPrevMotionY
        val dZ: Double = p.motionZ - pPrevMotionZ

        val posOffsetX: Double = -p.motionX
        var posOffsetY: Double = -p.motionY
        if (posOffsetY == -TransformerHooks.getGravityForEntity(p)) posOffsetY = 0.0
        val posOffsetZ: Double = -p.motionZ

        //if (p.capabilities.isFlying)
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.Companion.get(p))!!
        /**Undo whatever vanilla tried to do to our y motion */
        if (dY < 0.0 && p.motionY != 0.0) {
            p.motionY = pPrevMotionY
        } else if (dY > 0.01 && stats.isInFreefallLast()) {
            //Impulse upwards - it's probably a jetpack from another mod
            if (dX < 0.01 && dZ < 0.01) {
                val pitch: Float = p.rotationPitch / Constants.RADIANS_TO_DEGREES
                jetpackBoost = dY.toFloat() * MathHelper.cos(pitch) * 0.1f
                val factor: Float = 1 + MathHelper.sin(pitch) / 5
                p.motionY -= dY * factor
                jetpackUsed = true
            } else {
                p.motionY -= dY / 2
            }
        }

        p.motionX -= dX
        //        p.motionY -= dY;    //Enabling this will disable jetpacks
        p.motionZ -= dZ

        if (p.movementInput.moveForward != 0f) {
            p.motionX -= (p.movementInput.moveForward * MathHelper.sin(p.rotationYaw / Constants.RADIANS_TO_DEGREES) / (if (ConfigManagerCore.hardMode) 600f else 200f)).toDouble()
            p.motionZ += (p.movementInput.moveForward * MathHelper.cos(p.rotationYaw / Constants.RADIANS_TO_DEGREES) / (if (ConfigManagerCore.hardMode) 600f else 200f)).toDouble()
        }

        if (jetpackBoost != 0f) {
            p.motionX -= (jetpackBoost * MathHelper.sin(p.rotationYaw / Constants.RADIANS_TO_DEGREES)).toDouble()
            p.motionZ += (jetpackBoost * MathHelper.cos(p.rotationYaw / Constants.RADIANS_TO_DEGREES)).toDouble()
        }

        if (p.movementInput.sneak) {
            if (!sneakLast) {
//              posOffsetY += 0.0268;
                sneakLast = true
            }
            p.motionY -= if (ConfigManagerCore.hardMode) 0.002 else 0.0032
        } else if (sneakLast) {
            sneakLast = false
            //          posOffsetY -= 0.0268;
        }

        if (!jetpackUsed && p.movementInput.jump) {
            p.motionY += if (ConfigManagerCore.hardMode) 0.002 else 0.0032
        }

        val speedLimit: Float = if (ConfigManagerCore.hardMode) 0.9f else 0.7f

        if (p.motionX > speedLimit) {
            p.motionX = speedLimit.toDouble()
        }
        if (p.motionX < -speedLimit) {
            p.motionX = -speedLimit.toDouble()
        }
        if (p.motionY > speedLimit) {
            p.motionY = speedLimit.toDouble()
        }
        if (p.motionY < -speedLimit) {
            p.motionY = -speedLimit.toDouble()
        }
        if (p.motionZ > speedLimit) {
            p.motionZ = speedLimit.toDouble()
        }
        if (p.motionZ < -speedLimit) {
            p.motionZ = -speedLimit.toDouble()
        }
        pPrevMotionX = p.motionX
        pPrevMotionY = p.motionY
        pPrevMotionZ = p.motionZ
        p.move(MoverType.SELF, p.motionX + posOffsetX, p.motionY + posOffsetY, p.motionZ + posOffsetZ)
    }


    /*              double dyaw = p.rotationYaw - p.prevRotationYaw;
    p.rotationYaw -= dyaw * 0.8D;
    double dyawh = p.rotationYawHead - p.prevRotationYawHead;
    p.rotationYawHead -= dyawh * 0.8D;
    while (p.rotationYaw > 360F)
    {
        p.rotationYaw -= 360F;
    }
    while (p.rotationYaw < 0F)
    {
        p.rotationYaw += 360F;
    }
    while (p.rotationYawHead > 360F)
    {
        p.rotationYawHead -= 360F;
    }
    while (p.rotationYawHead < 0F)
    {
        p.rotationYawHead += 360F;
    }
*/
    open fun updateFreefall(p: EntityPlayer) {
        pPrevMotionX = p.motionX
        pPrevMotionY = p.motionY
        pPrevMotionZ = p.motionZ
    }

    @SideOnly(Side.CLIENT)
    open fun preVanillaMotion(p: EntityPlayerSP) {
        this.setupFreefallPre(p)
        stats.setSsOnGroundLast(p.onGround)
    }

    @SideOnly(Side.CLIENT)
    open fun postVanillaMotion(p: EntityPlayerSP) {
        val world: World = p.world
        val worldProvider: WorldProvider? = world.provider
        if (worldProvider !is IZeroGDimension) {
            return
        }
        val zeroGEvent: ZeroGravityEvent = ZeroGravityEvent.Motion(p)
        MinecraftForge.EVENT_BUS.post(zeroGEvent)
        if (zeroGEvent.isCanceled()) {
            return
        }

        var freefall: Boolean = stats.isInFreefall()
        freefall = this.testFreefall(p, freefall)
        stats.setInFreefall(freefall)
        stats.setInFreefallFirstCheck(true)

        var spinManager: SpinManager? = null
        if (worldProvider is WorldProviderSpaceStation && !stats.getPlatformControlled()) {
            spinManager = (worldProvider as WorldProviderSpaceStation).getSpinManager()
        }
        var doCentrifugal: Boolean = spinManager != null

        if (freefall) {
            this.pjumpticks = 0
            //Reverse effects of deceleration
            p.motionX /= 0.91
            p.motionZ /= 0.91
            p.motionY /= 0.9800000190734863

            if (spinManager != null) {
                doCentrifugal = spinManager.updatePlayerForSpin(p, 1f)
            }

            //Do freefall motion
            if (!p.capabilities.isCreativeMode) {
                this.freefallMotion(p)
            } else {
                p.capabilities.isFlying = true
                //Half the normal acceleration in Creative mode
                val dx: Double = p.motionX - this.pPrevMotionX
                val dy: Double = p.motionY - this.pPrevMotionY
                val dz: Double = p.motionZ - this.pPrevMotionZ
                p.motionX -= dx / 2
                p.motionY -= dy / 2
                p.motionZ -= dz / 2

                if (p.motionX > 1.2f) {
                    p.motionX = 1.2
                }
                if (p.motionX < -1.2f) {
                    p.motionX = -1.2
                }
                if (p.motionY > 0.7f) {
                    p.motionY = 0.7
                }
                if (p.motionY < -0.7f) {
                    p.motionY = -0.7
                }
                if (p.motionZ > 1.2f) {
                    p.motionZ = 1.2
                }
                if (p.motionZ < -1.2f) {
                    p.motionZ = -1.2
                }
            }
            //TODO: Think about endless drift?
            //Player may run out of oxygen - that will kill the player eventually if can't get back to SS
            //Could auto-kill + respawn the player if floats too far away (config option whether to lose items or not)
            //But we want players to be able to enjoy the view of the spinning space station from the outside
            //Arm and leg movements could start tumbling the player?
        } else  //Not freefall - within arm's length of something or jumping
        {
            val dy: Double = p.motionY - this.pPrevMotionY
            //if (p.motionY < 0 && this.pPrevMotionY >= 0) p.posY -= p.motionY;
            //if (p.motionY != 0) p.motionY = this.pPrevMotionY;
            if (p.movementInput.jump) {
                if ((p.onGround || stats.isSsOnGroundLast()) && !p.capabilities.isCreativeMode) {
                    if (this.pjumpticks < 25) this.pjumpticks++
                    p.motionY -= dy
                } else {
                    p.motionY += 0.015
                    if (this.pjumpticks == 0) {
                        p.motionY -= dy
                    }
                }
            } else if (this.pjumpticks > 0) {
                p.motionY += 0.0145 * this.pjumpticks
                this.pjumpticks = 0
            } else if (p.movementInput.sneak) {
                if (!p.onGround) {
                    p.motionY -= 0.015
                }
                this.pjumpticks = 0
            }
        }

        //Artificial gravity
        if (doCentrifugal && !p.onGround) {
            spinManager!!.applyCentrifugalForce(p)
        }

        this.pPrevMotionX = p.motionX
        this.pPrevMotionY = p.motionY
        this.pPrevMotionZ = p.motionZ
    }

    companion object {
        /**
         * Used for non-player entities in ZeroGDimensions
         */
        @JvmStatic
        fun testEntityFreefall(worldObj: World, entityBoundingBox: AxisAlignedBB): Boolean {
            //Check if the entity's bounding box is in the same block coordinates as any non-vacuum block (including torches etc)
            //If so, it's assumed the entity has something close enough to catch onto, so is not in freefall
            //Note: breatheable air here means the entity is definitely not in freefall
            val xmx: Int = MathHelper.floor(entityBoundingBox.maxX + 0.2)
            var ym: Int = MathHelper.floor(entityBoundingBox.minY - 0.1)
            var yy: Int = MathHelper.floor(entityBoundingBox.maxY + 0.1)
            val zm: Int = MathHelper.floor(entityBoundingBox.minZ - 0.2)
            val zz: Int = MathHelper.floor(entityBoundingBox.maxZ + 0.2)
            if (ym < 0) ym = 0
            if (yy > 255) yy = 255

            for (x: Int in MathHelper.floor(entityBoundingBox.minX - 0.2)..xmx) {
                for (z: Int in zm..zz) {
                    if (!worldObj.isBlockLoaded(BlockPos(x, 0, z), false)) continue

                    for (y: Int in ym..yy) {
                        if (Blocks.AIR !== worldObj.getBlockState(BlockPos(x, y, z)).getBlock()) {
                            return false
                        }
                    }
                }
            }

            return true
        }

        /**
         * Call this on every freefalling non-player entity in a dimension
         * either at the end of the world tick (ideal) or else during the
         * start of the next world tick (e.g. during updateWeather())
         *
         * May require iteration through the world's loadedEntityList
         * See SpinManager.updateSpin() for an example
         * @param e
         */
        @JvmStatic
        fun tickFreefallEntity(e: Entity) {
            if (e.world.provider is IZeroGDimension) (e.world.provider as IZeroGDimension).setInFreefall(e)


            //Undo deceleration applied at the end of the previous tick
            var warnLog: Boolean = false
            if (e is EntityLivingBase) {
                val zeroGEvent: ZeroGravityEvent = ZeroGravityEvent.InFreefall(e)
                MinecraftForge.EVENT_BUS.post(zeroGEvent)
                if (!zeroGEvent.isCanceled()) {
                    e.motionX /= 0.91 //0.91F;
                    e.motionZ /= 0.91 //0.91F;
                    e.motionY /= if (e is EntityFlying) 0.91 else 0.9800000190734863

                    if (e.motionX > 10.0) {
                        warnLog = true
                        e.motionX = 10.0
                    } else if (e.motionX < -10.0) {
                        warnLog = true
                        e.motionX = -10.0
                    }
                    if (e.motionY > 10.0) {
                        warnLog = true
                        e.motionY = 10.0
                    } else if (e.motionY < -10.0) {
                        warnLog = true
                        e.motionY = -10.0
                    }
                    if (e.motionZ > 10.0) {
                        warnLog = true
                        e.motionZ = 10.0
                    } else if (e.motionZ < -10.0) {
                        warnLog = true
                        e.motionZ = -10.0
                    }
                }
            } else if (e is EntityFallingBlock) {
                e.motionY /= 0.9800000190734863
                //e.motionY += 0.03999999910593033D;
                //e.posY += 0.03999999910593033D;
                //e.lastTickPosY += 0.03999999910593033D;
                if (e.motionY > 10.0) {
                    warnLog = true
                    e.motionY = 10.0
                } else if (e.motionY < -10.0) {
                    warnLog = true
                    e.motionY = -10.0
                }
            } else {
                e.motionX /= 0.9800000190734863
                e.motionY /= 0.9800000190734863
                e.motionZ /= 0.9800000190734863

                if (e.motionX > 10.0) {
                    warnLog = true
                    e.motionX = 10.0
                } else if (e.motionX < -10.0) {
                    warnLog = true
                    e.motionX = -10.0
                }
                if (e.motionY > 10.0) {
                    warnLog = true
                    e.motionY = 10.0
                } else if (e.motionY < -10.0) {
                    warnLog = true
                    e.motionY = -10.0
                }
                if (e.motionZ > 10.0) {
                    warnLog = true
                    e.motionZ = 10.0
                } else if (e.motionZ < -10.0) {
                    warnLog = true
                    e.motionZ = -10.0
                }
            }

            if (warnLog) GCLog.debug(e.getName() + " moving too fast")
        }
    }
}
