package micdoodle8.mods.galacticraft.core.dimension

import foundation.block.getMetaFromState

import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedList

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockSpinThruster
import micdoodle8.mods.galacticraft.core.entities.ITumblable
import micdoodle8.mods.galacticraft.core.entities.player.FreefallHandler
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockDynamicLiquid
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityTNTPrimed
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.*
import kotlin.math.*

open class SpinManager
constructor(private val worldProvider: WorldProviderSpaceStation) {
    @JvmField
    var doSpinning: Boolean = true

    @JvmField
    var angularVelocityRadians: Float = 0f

    @JvmField
    var skyAngularVelocity: Float = this.angularVelocityRadians * Constants.RADIANS_TO_DEGREES

    @JvmField
    var angularVelocityTarget: Float = 0f

    @JvmField
    var angularVelocityAccel: Float = 0f

    @JvmField
    var spinCentreX: Double = 0.0

    @JvmField
    var spinCentreZ: Double = 0.0
    private var momentOfInertia = 0f
    private var massCentreX = 0f
    private var massCentreZ = 0f

    @JvmField
    var ssBoundsMaxX: Int = 0

    @JvmField
    var ssBoundsMinX: Int = 0

    @JvmField
    var ssBoundsMaxY: Int = 0

    @JvmField
    var ssBoundsMinY: Int = 0

    @JvmField
    var ssBoundsMaxZ: Int = 0

    @JvmField
    var ssBoundsMinZ: Int = 0

    private var savefile: OrbitSpinSaveData.Entry? = null

    private val thrustersPlus: LinkedList<BlockPos> = Lists.newLinkedList<BlockPos>()
    private val thrustersMinus: LinkedList<BlockPos> = Lists.newLinkedList<BlockPos>()
    private var oneSSBlock: BlockPos? = null

    //private HashSet<BlockPos> stationBlocks = new HashSet<>();
    private val checked = HashSet<BlockVec3?>()

    private val artificialG = 0f

    //Used to make continuous particles + thrust sounds at the spin thrusters in this dimension
    //If false, make particles + sounds occasionally in small bursts, just for fun (micro attitude changes)
    //see: BlockSpinThruster.randomDisplayTick()
    @JvmField
    var thrustersFiring: Boolean = false
    private var dataNotLoaded = true
    private val loadedEntities: MutableList<Entity?> = Lists.newLinkedList<Entity?>()

    private var clientSide = true

    /**
     * Called from WorldProviderOrbit when registering the worldObj
     */
    open fun registerServerSide() {
        if (!this.worldProvider.world.isRemote) {
            this.clientSide = false
        }
    }

    open fun getSpinRate(): Float {
        return this.skyAngularVelocity
    }

    /**
     * Sets the spin rate for the dimension in radians per tick For example,
     * 0.031415 would be 1/200 revolution per tick So that would be 1 revolution
     * every 10 seconds
     */
    open fun setSpinRate(angle: Float) {
        this.angularVelocityRadians = angle
        this.skyAngularVelocity = angle * 180f / 3.1415927f

        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
            this.updateSkyProviderSpinRate()
        }
    }

    @OnlyIn(Dist.CLIENT)
    private fun updateSkyProviderSpinRate() {
        this.worldProvider.setSpinDeltaPerTick(this.skyAngularVelocity)
    }

    open fun setSpinRate(angle: Float, firing: Boolean) {
        this.angularVelocityRadians = angle
        this.skyAngularVelocity = angle * 180f / 3.1415927f
        this.worldProvider.setSpinDeltaPerTick(this.skyAngularVelocity)
        this.thrustersFiring = firing
    }

    open fun setSpinCentre(x: Double, z: Double) {
        this.spinCentreX = x
        this.spinCentreZ = z
        if (this.clientSide) {
            if (ConfigManagerCore.enableDebug) {
                GCLog.info("Clientside update to spin centre: " + x + "," + z)
            }
        }
    }

    open fun setSpinBox(mx: Int, xx: Int, my: Int, yy: Int, mz: Int, zz: Int) {
        this.ssBoundsMinX = mx
        this.ssBoundsMaxX = xx
        this.ssBoundsMinY = my
        this.ssBoundsMaxY = yy
        this.ssBoundsMinZ = mz
        this.ssBoundsMaxZ = zz
    }

    open fun removeThruster(thruster: BlockPos?, positive: Boolean) {
        if (positive) {
            this.thrustersPlus.remove(thruster)
        } else {
            this.thrustersMinus.remove(thruster)
        }
    }

    /**
     * This will check all blocks which are in contact with each other to find
     * the shape of the spacestation. It also finds the centre of mass (to
     * rotate around) and the moment of inertia (how easy/hard this is to
     * rotate).
     *
     *
     * If placingThruster is true, it will return false if the thruster (at
     * baseBlock) is not in contact with the "existing" spacestation - so the
     * player cannot place thrusters on outlying disconnected blocks and expect
     * them to have an effect.
     *
     *
     * Note: this check will briefly load, server-side, all chunks which have
     * spacestation blocks in them or 1 block adjacent to those.
     *
     * @param baseBlock
     * @return
     */
    open fun refresh(baseBlock: BlockPos?, placingThruster: Boolean): Boolean {
        if (this.oneSSBlock == null || this.worldProvider.world.getBlockState(this.oneSSBlock).getBlock().isAir(
                this.worldProvider.world.getBlockState(this.oneSSBlock),
                this.worldProvider.world,
                this.oneSSBlock
            )
        ) {
            if (baseBlock != null) {
                this.oneSSBlock = baseBlock
            } else {
                this.oneSSBlock = BlockPos(0, 64, 0)
            }
        }

        // Find contiguous blocks using an algorithm like the oxygen sealer one
        var currentLayer: MutableList<BlockVec3> = LinkedList<BlockVec3>()
        var nextLayer: MutableList<BlockVec3> = LinkedList<BlockVec3>()
        val foundThrusters: MutableList<BlockPos> = LinkedList<BlockPos>()

        this.checked.clear()
        currentLayer.add(BlockVec3((this.oneSSBlock)!!))
        this.checked.add(BlockVec3((this.oneSSBlock)!!))
        val bsStart: IBlockState = this.worldProvider.world.getBlockState(this.oneSSBlock)
        var bStart: Block = bsStart.getBlock()
        if (bStart is BlockSpinThruster) {
            foundThrusters.add(this.oneSSBlock!!)
        }

        var thisssBoundsMaxX: Int = this.oneSSBlock!!.getX()
        var thisssBoundsMinX: Int = thisssBoundsMaxX
        var thisssBoundsMaxY: Int = this.oneSSBlock!!.getY()
        var thisssBoundsMinY: Int = thisssBoundsMaxY
        var thisssBoundsMaxZ: Int = this.oneSSBlock!!.getZ()
        var thisssBoundsMinZ: Int = thisssBoundsMaxZ
        var thismass: Float = 0.1f //Mass of a thruster
        var thismassCentreX: Float = 0.1f * thisssBoundsMaxX
        var thismassCentreY: Float = 0.1f * thisssBoundsMaxY
        var thismassCentreZ: Float = 0.1f * thisssBoundsMaxZ
        var thismoment: Float = 0f

        while (currentLayer.size > 0) {
            var bits: Int
            for (vec: BlockVec3 in currentLayer) {
                bits = vec.sideDoneBits
                if (vec.x < thisssBoundsMinX) {
                    thisssBoundsMinX = vec.x
                }
                if (vec.y < thisssBoundsMinY) {
                    thisssBoundsMinY = vec.y
                }
                if (vec.z < thisssBoundsMinZ) {
                    thisssBoundsMinZ = vec.z
                }
                if (vec.x > thisssBoundsMaxX) {
                    thisssBoundsMaxX = vec.x
                }
                if (vec.y > thisssBoundsMaxY) {
                    thisssBoundsMaxY = vec.y
                }
                if (vec.z > thisssBoundsMaxZ) {
                    thisssBoundsMaxZ = vec.z
                }

                for (side: Int in 0..5) {
                    if ((bits and (1 shl side)) == 1) {
                        continue
                    }
                    val sideVec: BlockVec3? = vec.newVecSide(side)

                    if (sideVec != null && !this.checked.contains(sideVec)) {
                        this.checked.add(sideVec)
                        val state: IBlockState? = sideVec.getBlockState(this.worldProvider.world)
                        if (state == null) {
                            continue
                        }
                        val b: Block = state.getBlock()
                        if (b != null && !b.isAir(
                                state,
                                this.worldProvider.world,
                                sideVec.toBlockPos()
                            ) && (b !is BlockDynamicLiquid)
                        ) {
                            nextLayer.add(sideVec)
                            if (bStart.isAir(bsStart, this.worldProvider.world, this.oneSSBlock)) {
                                this.oneSSBlock = sideVec.toBlockPos()
                                bStart = b
                            }
                            var m: Float = 1.0f
                            //Liquids have a mass of 1, stone, metal blocks etc will be heavier
                            if (b !is BlockLiquid) {
                                //For most blocks, hardness gives a good idea of mass
                                m = b.getBlockHardness(state, this.worldProvider.world, sideVec.toBlockPos())
                                if (m < 0.1f) {
                                    m = 0.1f
                                } else if (m > 30f) {
                                    m = 30f
                                }
                                //Wood items have a high hardness compared with their presumed mass
                                if (b.getMaterial(state) === Material.WOOD) {
                                    m /= 4f
                                }

                                //TODO: higher mass for future Galacticraft hi-density item like neutronium
                                //Maybe also check for things in other mods by name: lead, uranium blocks?
                            }
                            thismassCentreX += m * sideVec.x
                            thismassCentreY += m * sideVec.y
                            thismassCentreZ += m * sideVec.z
                            thismass += m
                            thismoment += m * (sideVec.x * sideVec.x + sideVec.z * sideVec.z)
                            if (b is BlockSpinThruster && !RedstoneUtil.isBlockReceivingRedstone(
                                    this.worldProvider.world,
                                    sideVec.toBlockPos()
                                )
                            ) {
                                foundThrusters.add(sideVec.toBlockPos())
                            }
                        }
                    }
                }
            }

            currentLayer = nextLayer
            nextLayer = LinkedList<BlockVec3>()
        }

        if (placingThruster && !this.checked.contains(BlockVec3((baseBlock)!!))) {
            if (foundThrusters.size > 0) {
                //The thruster was not placed on the existing contiguous space station: it must be.
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("Thruster placed on wrong part of space station: base at " + this.oneSSBlock + " - baseBlock was " + baseBlock + " - found " + foundThrusters.size)
                }
                return false
            }

            //No thruster on the original space station - so assume the player made new station and start check again
            //This offers players a reset option: just remove all thrusters from original station then starting adding to new one
            //(This first check prevents an infinite loop)
            if (this.oneSSBlock != baseBlock) {
                this.oneSSBlock = baseBlock
                val state: IBlockState = this.worldProvider.world.getBlockState(this.oneSSBlock)
                if (state.getMaterial() !== Material.AIR) {
                    return this.refresh(baseBlock, true)
                }
            }

            return false
        }

        // Update thruster lists based on what was found
        this.thrustersPlus.clear()
        this.thrustersMinus.clear()
        for (thruster: BlockPos in foundThrusters) {
            val state: IBlockState = this.worldProvider.world.getBlockState(thruster)
            val facing: Int = state.getBlock().getMetaFromState(state) and 8
            if (facing == 0) {
                this.thrustersPlus.add(thruster)
            } else {
                this.thrustersMinus.add(thruster)
            }
        }

        // Calculate centre of mass
        val mass: Float = thismass

        this.massCentreX = thismassCentreX / thismass + 0.5f
        val massCentreY: Float = thismassCentreY / thismass + 0.5f
        this.massCentreZ = thismassCentreZ / thismass + 0.5f

        //System.out.println("(X,Z) = "+this.massCentreX+","+this.massCentreZ);
        this.setSpinCentre(this.massCentreX.toDouble(), this.massCentreZ.toDouble())

        //The boundary is at the outer edges of the blocks
        this.ssBoundsMaxX = thisssBoundsMaxX + 1
        this.ssBoundsMinX = thisssBoundsMinX
        this.ssBoundsMaxY = thisssBoundsMaxY + 1
        this.ssBoundsMinY = thisssBoundsMinY
        this.ssBoundsMaxZ = thisssBoundsMaxZ + 1
        this.ssBoundsMinZ = thisssBoundsMinZ

        // Calculate momentOfInertia
        thismoment -= this.massCentreX * this.massCentreX * mass
        thismoment -= this.massCentreZ * this.massCentreZ * mass
        this.momentOfInertia = thismoment

        //TODO
        // TODO defy gravity
        // TODO break blocks which are outside SS (not in checked)
        // TODO prevent spin if there is a huge number of blocks outside SS
        GCLog.debug("MoI = " + this.momentOfInertia + " CoMx = " + this.massCentreX + " CoMz = " + this.massCentreZ)

        //Send packets to clients in this dimension
        var objList: MutableList<Any?> = ArrayList<Any?>()
        objList.add(this.spinCentreX)
        objList.add(this.spinCentreZ)
        (GalacticraftCore.packetPipeline)!!.sendToDimension(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_STATION_DATA,
                GCCoreUtil.getDimensionID(this.worldProvider),
                objList
            ), GCCoreUtil.getDimensionID(this.worldProvider)
        )

        objList = ArrayList<Any?>()
        objList.add(this.ssBoundsMinX)
        objList.add(this.ssBoundsMaxX)
        objList.add(this.ssBoundsMinY)
        objList.add(this.ssBoundsMaxY)
        objList.add(this.ssBoundsMinZ)
        objList.add(this.ssBoundsMaxZ)
        (GalacticraftCore.packetPipeline)!!.sendToDimension(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_STATION_BOX,
                GCCoreUtil.getDimensionID(this.worldProvider),
                objList
            ), GCCoreUtil.getDimensionID(this.worldProvider)
        )

        this.updateSpinSpeed()

        return true
    }

    open fun updateSpinSpeed() {
        if (this.momentOfInertia > 0f) {
            var netTorque: Float = 0f
            var countThrusters: Int = 0
            var countThrustersReverse: Int = 0

            for (thruster: BlockPos in this.thrustersPlus) {
                val xx: Float = thruster.getX() - this.massCentreX
                val zz: Float = thruster.getZ() - this.massCentreZ
                netTorque += MathHelper.sqrt(xx * xx + zz * zz)
                countThrusters++
            }
            for (thruster: BlockPos in this.thrustersMinus) {
                val xx: Float = thruster.getX() - this.massCentreX
                val zz: Float = thruster.getZ() - this.massCentreZ
                netTorque -= MathHelper.sqrt(xx * xx + zz * zz)
                countThrustersReverse++
            }

            if (countThrusters == countThrustersReverse) {
                this.angularVelocityAccel = 0.000004f
                this.angularVelocityTarget = 0f
            } else {
                countThrusters += countThrustersReverse
                if (countThrusters > 4) {
                    countThrusters = 4
                }

                val maxRx: Float = max(this.ssBoundsMaxX - this.massCentreX, this.massCentreX - this.ssBoundsMinX)
                val maxRz: Float = max(this.ssBoundsMaxZ - this.massCentreZ, this.massCentreZ - this.ssBoundsMinZ)
                val maxR: Float = max(maxRx, maxRz)
                this.angularVelocityTarget = MathHelper.sqrt(GFORCE / maxR) / 2
                //The divide by 2 is not scientific but is a Minecraft factor as everything happens more quickly
                val spinCap: Float = 0.00125f * countThrusters

                //TODO: increase this above 20F in release versions so everything happens more slowly
                this.angularVelocityAccel = netTorque / this.momentOfInertia / 20f
                if (this.angularVelocityAccel < 0) {
                    this.angularVelocityAccel = -this.angularVelocityAccel
                    this.angularVelocityTarget = -this.angularVelocityTarget
                    if (this.angularVelocityTarget < -spinCap) {
                        this.angularVelocityTarget = -spinCap
                    }
                } else  //Do not make it spin too fast or players might get dizzy
                //Also make it so players need minimum 4 thrusters for best spin
                    if (this.angularVelocityTarget > spinCap) {
                        this.angularVelocityTarget = spinCap
                    }

                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("MaxR = " + maxR + " Angular vel = " + this.angularVelocityTarget + " Angular accel = " + this.angularVelocityAccel)
                }
            }
        }

        if (!this.clientSide) {
            //Save the updated data for the world
            this.save()
        }
    }

    open fun updateSpin() {
        if (!this.clientSide) {
            if (this.dataNotLoaded) {
                this.savefile = OrbitSpinSaveData.initWorldData(this.worldProvider.world, GCCoreUtil.getDimensionID(this.worldProvider)) { writeToNBT(it) }
                this.readFromNBT((this.savefile!!.datacompound)!!)
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("Loading data from save: " + this.savefile!!.datacompound.getFloatOr("omegaSky", 0f))
                }
                this.dataNotLoaded = false
            }

            if (this.doSpinning) {
                var updateNeeded: Boolean = true
                if (this.angularVelocityTarget < this.angularVelocityRadians) {
                    var newAngle: Float = this.angularVelocityRadians - this.angularVelocityAccel
                    if (newAngle < this.angularVelocityTarget) {
                        newAngle = this.angularVelocityTarget
                    }
                    this.setSpinRate(newAngle)
                    this.thrustersFiring = true
                } else if (this.angularVelocityTarget > this.angularVelocityRadians) {
                    var newAngle: Float = this.angularVelocityRadians + this.angularVelocityAccel
                    if (newAngle > this.angularVelocityTarget) {
                        newAngle = this.angularVelocityTarget
                    }
                    this.setSpinRate(newAngle)
                    this.thrustersFiring = true
                } else if (this.thrustersFiring) {
                    this.thrustersFiring = false
                } else {
                    updateNeeded = false
                }

                if (updateNeeded) {
                    this.writeToNBT((this.savefile!!.datacompound)!!)
                    this.savefile!!.setDirty()
                    val objList: MutableList<Any?> = ArrayList<Any?>()
                    objList.add(this.angularVelocityRadians)
                    objList.add(this.thrustersFiring)
                    (GalacticraftCore.packetPipeline)!!.sendToDimension(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_UPDATE_STATION_SPIN,
                            GCCoreUtil.getDimensionID(this.worldProvider),
                            objList
                        ), GCCoreUtil.getDimensionID(this.worldProvider)
                    )
                }
            }

            //Update entity positions if in freefall
            this.loadedEntities.clear()
            this.loadedEntities.addAll(this.worldProvider.world.loadedEntityList)
            for (e: Entity? in this.loadedEntities) {
                //TODO: What about vehicles from GC (buggies) and other mods?
                if ((e is EntityItem || e is EntityLivingBase && e !is EntityPlayer || e is EntityTNTPrimed || e is EntityFallingBlock) && !e.onGround) {
                    val entityBoundingBox: AxisAlignedBB = e.getBoundingBox()
                    val outsideStation: Boolean =
                        entityBoundingBox.maxX < this.ssBoundsMinX || entityBoundingBox.minX > this.ssBoundsMaxX || entityBoundingBox.maxY < this.ssBoundsMinY || entityBoundingBox.minY > this.ssBoundsMaxY || entityBoundingBox.maxZ < this.ssBoundsMinZ || entityBoundingBox.minZ > this.ssBoundsMaxZ

                    if (outsideStation || FreefallHandler.testEntityFreefall(
                            this.worldProvider.world,
                            entityBoundingBox
                        )
                    ) {
                        if (this.doSpinning) {
                            this.moveRotatedEntity(e, this.spinCentreX, this.spinCentreZ, this.angularVelocityRadians)
                        }
                        FreefallHandler.tickFreefallEntity(e)
                        if (e is ITumblable) {
                            (e as ITumblable).setTumbling(3f)
                        }
                    } else {
                        if (e is ITumblable) {
                            (e as ITumblable).setTumbling(0f)
                        }
                    }
                }
            }
        }
    }

    private fun moveRotatedEntity(e: Entity, rotationCentreX: Double, rotationCentreZ: Double, deltaTheta: Float) {
        //Do the rotation
        if (deltaTheta != 0f) {
            var angle: Float
            val xx: Double = e.x - rotationCentreX
            val zz: Double = e.z - rotationCentreZ
            var arc: Double = sqrt(xx * xx + zz * zz)
            if (xx == 0.0) {
                angle = if (zz > 0) 3.1415926535f / 2 else -3.1415926535f / 2
            } else {
                angle = atan(zz / xx).toFloat()
            }
            if (xx < 0.0) {
                angle += 3.1415926535f
            }
            angle += deltaTheta / 3f
            arc = arc * deltaTheta
            val offsetX: Double = -arc * MathHelper.sin(angle)
            val offsetZ: Double = arc * MathHelper.cos(angle)
            e.posX += offsetX
            e.posZ += offsetZ
            e.lastTickPosX += offsetX
            e.lastTickPosZ += offsetZ

            //Rotated into an unloaded chunk (probably also drifted out to there): byebye
            if (!e.level().isBlockLoaded(BlockPos(MathHelper.floor(e.x), 64, MathHelper.floor(e.z)))) {
                e.setDead()
                return
            }

            e.setBoundingBox(e.getBoundingBox().offset(offsetX, 0.0, offsetZ))
            //TODO check for block collisions here - if so move the entity appropriately and apply fall damage
            //Moving the entity = slide along / down
            e.yRot += this.skyAngularVelocity
            while (e.yRot > 360f) {
                e.yRot -= 360f
            }

            //Rotate the motion vector (motionX, motionZ) by the angular rotation
            val velocity: Double = sqrt(e.deltaMovement.x * e.deltaMovement.x + e.deltaMovement.z * e.deltaMovement.z)
            if (e.deltaMovement.x == 0.0) {
                angle = if (e.deltaMovement.z > 0) 3.1415926535f / 2 else -3.1415926535f / 2
            } else {
                angle = atan(e.deltaMovement.z / e.deltaMovement.x).toFloat()
            }
            if (e.deltaMovement.x < 0.0) {
                angle += 3.1415926535f
            }
            angle += deltaTheta
            e.motionX = velocity * MathHelper.cos(angle)
            e.motionZ = velocity * MathHelper.sin(angle)
        }
    }

    @OnlyIn(Dist.CLIENT)
    open fun updatePlayerForSpin(p: EntityPlayerSP, partial: Float): Boolean {
        val angleDelta: Float = partial * this.angularVelocityRadians
        if (this.doSpinning && angleDelta != 0f) {
            //TODO maybe need to test to make sure xx and zz are not too large (outside sight range of SS)
            //TODO think about server + network load (loading/unloading chunks) when movement is rapid
            //Maybe reduce chunkloading radius?

            var doCentrifugal: Boolean = false
            var angle: Float
            val xx: Double = p.x - this.spinCentreX
            val zz: Double = p.z - this.spinCentreZ
            var arc: Double = sqrt(xx * xx + zz * zz)
            if (xx == 0.0) {
                angle = if (zz > 0) 3.1415926535f / 2 else -3.1415926535f / 2
            } else {
                angle = atan(zz / xx).toFloat()
            }
            if (xx < 0.0) {
                angle += 3.1415926535f
            }
            angle += angleDelta / 3f
            arc = arc * angleDelta
            var offsetX: Double = -arc * sin(angle.toDouble())
            var offsetZ: Double = arc * cos(angle.toDouble())

            //Check for block collisions here - if so move the player appropriately
            //First check that there are no existing collisions where the player is now (TODO: bounce the player away)
            if (p.level().getCollisionBoxes(p, p.getBoundingBox()).size == 0) {
                //Now check for collisions in the new direction and if there are some, try reducing the movement
                var collisions: Int = 0
                do {
                    val list: MutableList<AxisAlignedBB?> =
                        p.level().getCollisionBoxes(p, p.getBoundingBox().expand(offsetX, 0.0, offsetZ))
                    collisions = list.size
                    if (collisions > 0) {
                        if (!doCentrifugal) {
                            p.motionX += -offsetX
                            p.motionZ += -offsetZ
                        }
                        offsetX /= 2.0
                        offsetZ /= 2.0
                        if (offsetX < 0.01 && offsetX > -0.01) {
                            offsetX = 0.0
                        }
                        if (offsetZ < 0.01 && offsetZ > -0.01) {
                            offsetZ = 0.0
                        }
                        doCentrifugal = true
                    }
                } while (collisions > 0)

                p.posX += offsetX
                p.posZ += offsetZ
                p.setBoundingBox(p.getBoundingBox().offset(offsetX, 0.0, offsetZ))
            }

            p.yRot += this.skyAngularVelocity * partial
            p.yBodyRot += this.skyAngularVelocity * partial
            while (p.yRot > 360f) {
                p.yRot -= 360f
            }
            while (p.yRot < 0f) {
                p.yRot += 360f
            }

            return doCentrifugal
        }

        return false
    }

    @OnlyIn(Dist.CLIENT)
    open fun applyCentrifugalForce(p: EntityPlayerSP) {
        var quadrant: Int = 0
        val xd: Double = p.x - this.spinCentreX
        val zd: Double = p.z - this.spinCentreZ
        val accel: Double = sqrt(xd * xd + zd * zd) * this.angularVelocityRadians * this.angularVelocityRadians * 4.0

        if (xd < 0) {
            if (xd < -abs(zd)) {
                quadrant = 2
            } else {
                quadrant = if (zd < 0) 3 else 1
            }
        } else if (xd > abs(zd)) {
            quadrant = 0
        } else {
            quadrant = if (zd < 0) 3 else 1
        }

        when (quadrant) {
            0 -> p.motionX += accel
            1 -> p.motionZ += accel
            2 -> p.motionX -= accel
            3 -> p.motionZ -= accel
            else -> p.motionZ -= accel
        }
    }

    /**
     * Call this when player first login/transfer to this dimension
     *
     *
     * TODO how can this code be called by other mods / plugins with teleports
     * (e.g. Bukkit)? See WorldUtil.teleportEntity()
     *
     * @param player
     */
    open fun sendPackets(player: EntityPlayerMP?) {
        var objList: MutableList<Any?> = ArrayList<Any?>()
        objList.add(this.angularVelocityRadians)
        objList.add(this.thrustersFiring)
        if (player == null) {
            (GalacticraftCore.packetPipeline)!!.sendToDimension(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_STATION_SPIN,
                    GCCoreUtil.getDimensionID(this.worldProvider),
                    objList
                ), GCCoreUtil.getDimensionID(this.worldProvider)
            )
        } else {
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_STATION_SPIN,
                    GCCoreUtil.getDimensionID(player.level()),
                    objList
                ), player
            )
        }

        objList = ArrayList<Any?>()
        objList.add(this.spinCentreX)
        objList.add(this.spinCentreZ)
        if (player == null) {
            (GalacticraftCore.packetPipeline)!!.sendToDimension(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_STATION_DATA,
                    GCCoreUtil.getDimensionID(this.worldProvider),
                    objList
                ), GCCoreUtil.getDimensionID(this.worldProvider)
            )
        } else {
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_STATION_DATA,
                    GCCoreUtil.getDimensionID(player.level()),
                    objList
                ), player
            )
        }

        objList = ArrayList<Any?>()
        objList.add(this.ssBoundsMinX)
        objList.add(this.ssBoundsMaxX)
        objList.add(this.ssBoundsMinY)
        objList.add(this.ssBoundsMaxY)
        objList.add(this.ssBoundsMinZ)
        objList.add(this.ssBoundsMaxZ)
        if (player == null) {
            (GalacticraftCore.packetPipeline)!!.sendToDimension(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_STATION_BOX,
                    GCCoreUtil.getDimensionID(this.worldProvider),
                    objList
                ), GCCoreUtil.getDimensionID(this.worldProvider)
            )
        } else {
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_STATION_BOX,
                    GCCoreUtil.getDimensionID(player.level()),
                    objList
                ), player
            )
        }
    }

    private fun save() {
        if (this.savefile == null) {
            this.savefile = OrbitSpinSaveData.initWorldData(this.worldProvider.world, GCCoreUtil.getDimensionID(this.worldProvider)) { writeToNBT(it) }
            this.dataNotLoaded = false
        } else {
            this.writeToNBT((this.savefile!!.datacompound)!!)
            this.savefile!!.setDirty()
        }
    }

    open fun readFromNBT(nbt: NBTTagCompound) {
        this.doSpinning = true //nbt.getBooleanOr("doSpinning", false);
        this.angularVelocityRadians = nbt.getFloatOr("omegaRad", 0f)
        this.skyAngularVelocity = nbt.getFloatOr("omegaSky", 0f)
        this.angularVelocityTarget = nbt.getFloatOr("omegaTarget", 0f)
        this.angularVelocityAccel = nbt.getFloatOr("omegaAcc", 0f)

        val oneBlock = nbt.getTag("oneBlock") as? NBTTagCompound
        if (oneBlock != null) {
//            this.oneSSBlock = BlockVec3.readFromNBT(oneBlock);
            this.oneSSBlock = BlockPos(oneBlock.getIntOr("x", 0), oneBlock.getIntOr("y", 0), oneBlock.getIntOr("z", 0))
        } else {
            this.oneSSBlock = null
        }

        //A lot of the data can be refreshed by refresh
        this.refresh(this.oneSSBlock, false)

        this.sendPackets(null)
    }

    open fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        nbt.putBoolean("doSpinning", this.doSpinning)
        nbt.putFloat("omegaRad", this.angularVelocityRadians)
        nbt.putFloat("omegaSky", this.skyAngularVelocity)
        nbt.putFloat("omegaTarget", this.angularVelocityTarget)
        nbt.putFloat("omegaAcc", this.angularVelocityAccel)
        if (this.oneSSBlock != null) {
            val oneBlock: NBTTagCompound = NBTTagCompound()
            oneBlock.putInt("x", this.oneSSBlock!!.getX())
            oneBlock.putInt("y", this.oneSSBlock!!.getY())
            oneBlock.putInt("z", this.oneSSBlock!!.getZ())
            nbt.put("oneBlock", oneBlock)
        }
        return nbt
    }

    companion object {
        private val GFORCE = 9.81f / 400f //gravity in metres per tick squared
    }
}
