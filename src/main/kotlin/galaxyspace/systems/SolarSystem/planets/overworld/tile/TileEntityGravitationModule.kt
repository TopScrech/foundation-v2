package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.inventory.getArmorItem
import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.api.tile.ITileEffects
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGravitationModule
import micdoodle8.mods.galacticraft.api.entity.IAntiGrav
import micdoodle8.mods.galacticraft.api.item.IArmorGravity
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.LogicalSide as Side
import java.util.HashSet

open class TileEntityGravitationModule constructor() :
    TileEntityElectricInventory("tile.gravitation_module.name"), ITileEffects {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = 1

    private var radius: Int

    private var strength: Float

    private var aabb: AxisAlignedBB? = null
    private var initialised = false

    @JvmField
    var shouldRenderEffects: Boolean = false

    init {
        this.initialised = true
        this.radius = 8
        this.strength = 1.0f
        this.storage.setCapacity(15000f)
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 else 45).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(1 + 4, ItemStack.EMPTY)
    }

    override fun validate() {
        super.validate()
        if (!this.world.isRemote) loadedTiles.add(BlockVec3Dim(this))
    }

    override fun onLoad() {
        if (!this.world.isRemote) loadedTiles.add(BlockVec3Dim(this))
    }

    override fun onChunkUnload() {
        if (!this.world.isRemote) loadedTiles.remove(BlockVec3Dim(this))

        super.onChunkUnload()
    }

    override fun invalidate() {
        if (!this.world.isRemote) {
            loadedTiles.remove(BlockVec3Dim(this))
        }

        super.invalidate()
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

    override fun update() {
        super.update()


        //Freezing
        if (this.world.rand.nextInt(4) == 0) this.world.notifyLightSet(this.getPos())

        if (canProcess() && !this.disabled) {
            if (this.hasEnoughEnergyToRun) {
                var energy_boost: Int = 0
                radius = 8


                //////////
                for (i: Int in 0..3) {
                    if (this.getInventory().get(1 + i).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 0))) radius += 2
                    if (this.getInventory().get(1 + i).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 3))) energy_boost++
                }

                this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 + 60 - (20 * energy_boost) else 75 + 55 - (15 * energy_boost)).toFloat())

                if (processTicks == 0) {
                    processTicks = this.processTimeRequired
                } else {
                    if (--processTicks <= 0) {
                        this.smeltItem()
                        processTicks = this.processTimeRequired
                        // this.world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 5, 2);
                    }
                }
            } else if (processTicks > 0 && processTicks < this.processTimeRequired) {
                // Apply a "cooling down" process if the electric furnace runs out of energy
                // while smelting
                if (this.world.rand.nextInt(4) == 0) {
                    processTicks++
                }
                check = false
            }

            /*
			 * else { if(this.getBlockMetadata() > 4)
			 * this.world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2); }
			 */
            // BlockGravitationModule.updateFurnaceBlockState(this.hasEnoughEnergyToRun,
            // this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        } else {
            // if(this.getBlockMetadata() > 4) this.world.setBlockMetadataWithNotify(xCoord,
            // yCoord, zCoord, 0, 2);

            processTicks = 0
        }
    }

    open fun smeltItem() {
        aabb = AxisAlignedBB(
            (this.getPos().getX() - getGravityRadius()).toDouble(), (this.getPos().getY() - 4).toDouble(),
            (this.getPos().getZ() - getGravityRadius()).toDouble(),

            (this.getPos().getX() + getGravityRadius()).toDouble(), (this.getPos().getY() + 16).toDouble(),
            (this.getPos().getZ() + getGravityRadius()).toDouble()
        )

        if (this.world.provider is IGalacticraftWorldProvider) {
            val g: Double =
                ((getGravityStrength() - (world.provider as IGalacticraftWorldProvider).getGravity()) / 0.08f).toDouble()

            //if(getGravityRadius() > 14) g /= 2;
            val list: MutableList<*> = world.getEntitiesWithinAABB<Entity?>(Entity::class.java, aabb)

            if (!world.isRemote) {
                for (e: Any? in list) {
                    if (e is IAntiGrav) continue
                    //Iterator iterator = list.iterator();
                    val entity: Entity = e as Entity
                    /*if(!(entity instanceof EntityPlayer))
					{
						entity.addVelocity(0.0D, g / 1000, 0.0D);
					}*/
                    entity.fallDistance -= (g * 10.0f).toFloat()

                    if (e is EntityLivingBase && e !is EntityPlayer) e.motionY -= (g / 200)

                    if (entity.fallDistance < 0) {
                        entity.fallDistance = 0.0f
                    }
                }
            } else {
                for (e: Any? in list) {
                    if (e is EntityLivingBase) {
                        val living: EntityLivingBase = e
                        if (e is EntityPlayer) {
                            val p: EntityPlayer = living as EntityPlayer
                            if (p.capabilities.isFlying) continue

                            if (!p.inventory.getArmorItem(0).isEmpty() && p.inventory.getArmorItem(0)
                                    .getItem() is IArmorGravity
                                && (p.inventory.getArmorItem(0).getItem() as IArmorGravity)
                                    .gravityOverrideIfLow(p) > 0
                            ) continue
                        }
                        living.motionY -= (g / 300)
                    }
                }
            }

            check = true
            if (this.shouldRenderEffects && world.isRemote) {
                for (yy: Int in -4..15) {
                    for (ix: Int in -getGravityRadius()..getGravityRadius() + 1) {
                        if (ix == -getGravityRadius() || ix == getGravityRadius() + 1 || yy == 15 || yy == -4) {
                            world.spawnParticle(
                                EnumParticleTypes.CRIT_MAGIC,
                                (this.getPos().getX() + ix + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos().getY() + yy + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos()
                                    .getZ() - getGravityRadius() + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                0.0,
                                0.0,
                                0.0
                            )
                            world.spawnParticle(
                                EnumParticleTypes.CRIT_MAGIC,
                                (this.getPos().getX() + ix + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos().getY() + yy + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos()
                                    .getZ() + getGravityRadius() + 1 + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                0.0,
                                0.0,
                                0.0
                            )
                        }
                    }
                    // -//
                    for (iz: Int in -getGravityRadius()..getGravityRadius() + 1) {
                        if (iz == -getGravityRadius() || iz == getGravityRadius() + 1 || yy == 15 || yy == -4) {
                            world.spawnParticle(
                                EnumParticleTypes.CRIT_MAGIC,
                                (this.getPos()
                                    .getX() - getGravityRadius() + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos().getY() + yy + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos().getZ() + iz + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                0.0,
                                0.0,
                                0.0
                            )
                            world.spawnParticle(
                                EnumParticleTypes.CRIT_MAGIC,
                                (this.getPos()
                                    .getX() + getGravityRadius() + 1 + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos().getY() + yy + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                (this.getPos().getZ() + iz + this.world.rand.nextFloat() - 0.5f).toDouble(),
                                0.0,
                                0.0,
                                0.0
                            )
                        }
                    }
                }
            }
        }
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        if (this.storage.getEnergyStoredGC() > STANDARD_CAPACITY) {
            this.initialised = true
        } else this.initialised = false
        processTicks = par1NBTTagCompound.getIntOr("smeltingTicks", 0)

        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())


        if (par1NBTTagCompound.containsTag("gravityradius")) {
            val grav: Int = par1NBTTagCompound.getIntOr("gravityradius", 0)
            this.setGravityRadius(if (grav == 0) 1 else grav)
        }

        if (par1NBTTagCompound.containsTag("gravitystrength")) {
            val strength: Float = par1NBTTagCompound.getFloatOr("gravitystrength", 0f)
            this.setGravityStrength(strength)
        }
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        if (this.tierGC == 1 && this.storage.getEnergyStoredGC() > STANDARD_CAPACITY) this.storage.setEnergyStored(
            STANDARD_CAPACITY
        )
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.putInt("smeltingTicks", processTicks)
        par1NBTTagCompound.putInt("gravityradius", if (radius > 16) 16 else radius)
        par1NBTTagCompound.putFloat("gravitystrength", this.strength)

        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())


        return par1NBTTagCompound
    }

    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        return true
    }

    override fun shouldUseEnergy(): Boolean {
        return canProcess()
    }

    override fun setEffectsVisible(shouldRender: Boolean) {
        this.shouldRenderEffects = shouldRender
    }

    override fun getEffectsVisible(): Boolean {
        return this.shouldRenderEffects
    }

    open fun setGravityRadius(radius: Int) {
        this.radius = radius
    }

    open fun getGravityRadius(): Int {
        return this.radius
    }

    open fun setGravityStrength(strength: Float) {
        this.strength = strength
    }

    open fun getGravityStrength(): Float {
        return this.strength
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

    open fun inGravityZone(world: World?, player: EntityLivingBase): Boolean {
        if (player.x > this.pos.getX() - getGravityRadius() && player.y > this.pos.getY() - 4 && player.z > this.pos.getZ() - getGravityRadius() && player.x < this.pos.getX() + getGravityRadius() && player.y < this.pos.getY() + 16 && player.z < this.pos.getZ() + getGravityRadius()) return true

        return false
    }

    public override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    companion object {
        @JvmField
        var PROCESS_TIME_REQUIRED: Int = 130

        @JvmField
        @Annotations.NetworkedField(targetSide = Side.CLIENT)
        var processTicks: Int = 0

        @JvmField
        var loadedTiles: HashSet<BlockVec3Dim?> = HashSet<BlockVec3Dim?>()

        @JvmField
        var check: Boolean = false

        @JvmStatic
        fun canProcess(): Boolean {
            return true
        }
    }
}
