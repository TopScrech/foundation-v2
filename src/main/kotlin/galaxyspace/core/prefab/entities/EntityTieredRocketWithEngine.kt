package galaxyspace.core.prefab.entities

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.entities.EntityTieredRocketWithEngine
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import galaxyspace.core.util.GSUtils
import asmodeuscore.api.entity.ICustomEngine
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.NonNullList
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.LogicalSide as Side
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

abstract class EntityTieredRocketWithEngine : EntityTieredRocket, ICustomEngine {
    private var engine_type = Engine_Type.FUEL_ENGINE
    private val tier: Int

    constructor(par1World: World?, tier: Int) : super(par1World) {
        this.setSize(2.2f, 11f)
        this.tier = tier
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        tier: Int,
        rocketType: IRocketType.EnumRocketType?
    ) : super(par1World, par2, par4, par6) {
        this.rocketType = rocketType
        this.tier = tier
        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
    }

    override fun entityInit() {
        super.entityInit()
    }

    override fun onUpdate() {
        super.onUpdate()

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

        if (FMLCommonHandler.instance()
                .getEffectiveSide() == Side.CLIENT && this.hasValidFuel() && this.rand.nextInt(4) == 0 && !this.getLaunched() && this.getEngine() == Engine_Type.FUEL_ENGINE
        ) {
            //GalacticraftCore.proxy.spawnParticle("whiteSmokeLargeLaunched", new Vector3(this.posX + 0.4 - this.rand.nextDouble(), this.posY + 10, this.posZ + 0.4 - this.rand.nextDouble()), new Vector3(0.5D, -1.5D, 0.5D), new Object[] { });
            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX + 0.4 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ + rand.nextDouble()
                ),
                Vector3(0.05 + 0.06, -0.5, 0.0 - 0.03),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )

            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX - 0.8 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ + rand.nextDouble()
                ),
                Vector3(0.05 - 0.25, -0.5, 0.0 - 0.03),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )

            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX - 0.2 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ + 0.4 + rand.nextDouble()
                ),
                Vector3(0.0, -0.5, 0.1 + 0.06),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )

            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX - 0.2 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ - 0.4 + rand.nextDouble()
                ),
                Vector3(0.0, -0.5, -0.1 - 0.06),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )
        }


        // if(!world.isRemote)
        //System.out.println(this.autoLaunchSetting);
        if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal && this.hasValidFuel()) {
            if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal) {
                var d: Double = (this.timeSinceLaunch / 150).toDouble()

                var mod_engine: Float = 1.0f

                when (engine_type) {
                    Engine_Type.FUEL_ENGINE -> {}
                    Engine_Type.ION_ENGINE -> mod_engine = 2.0f
                    Engine_Type.SUBLIGHT_ENGINE -> mod_engine = 5.0f
                    else -> {}
                }


                // if(this.engine_type == Engine_Type.SUBLIGHT_ENGINE) mod_engine = 5;
                if (this.world.provider is IGalacticraftWorldProvider && (this.world.provider as IGalacticraftWorldProvider).hasNoAtmosphere()) {
                    d = min(d * 1.2 * mod_engine, (2 * mod_engine).toDouble())
                } else {
                    d = min(d * mod_engine, 1.4 * mod_engine)
                }

                if (d != 0.0) {
                    this.motionY =
                        -d * 2.5 * cos(((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES).toDouble())
                }
            } else {
                this.motionY -= 0.008
            }

            var multiplier: Double = 1.0

            if (this.world.provider is IGalacticraftWorldProvider) {
                multiplier = (this.world.provider as IGalacticraftWorldProvider).getFuelUsageMultiplier()

                if (multiplier <= 0) {
                    multiplier = 1.0
                }
            }

            if (this.timeSinceLaunch % MathHelper.floor(2 * (1 / multiplier)) == 0f) {
                this.removeFuel(4)
                if (!this.hasValidFuel()) {
                    this.stopRocketSound()
                }
            }
        } else if (!this.hasValidFuel() && this.getLaunched() && !this.world.isRemote) {
            if (abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 10 != 0.0) {
                this.motionY -= abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 20
            }
        }


        //GalaxySpace.debug(this.getEngine().getName());
    }

    override fun onTeleport(player: EntityPlayerMP?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)


        if (playerBase != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

            if (this.stacks == null || (this.stacks)!!.isEmpty()) {
                stats.setRocketStacks(NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY))
            } else {
                stats.setRocketStacks(this.stacks)
            }

            stats.setRocketType((this.rocketType)!!.getIndex())
            stats.setFuelLevel(this.fuelTank.getFluidAmount())


            if (engine_type != Engine_Type.FUEL_ENGINE) {
                val withengine: ItemStack = getPickedResult(null)
                if (!withengine.hasTagCompound()) withengine.setTagCompound(NBTTagCompound())
                withengine.getTagCompound()!!.putBoolean(engine_type.getName(), true)
                withengine.getTagCompound()!!.putInt(ItemSpaceSuit.mod_count, 0)

                for (i: Int in (stats.getRocketStacks())!!.indices) {
                    val stack: ItemStack = (stats.getRocketStacks())!!.get(i)
                    if (i == (stats.getRocketStacks())!!.size - 1) {
                        (stats.getRocketStacks())!!.set(i, withengine)
                        break
                    }
                }


                //stats.getRocketStacks().set(1, withengine);
            } else stats.setRocketItem(getPickedResult(null).getItem())
        }
    }


    protected abstract fun spawnParticles(launched: Boolean)

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return !this.isDead && par1EntityPlayer.getDistanceSq(this) <= 64.0
    }

    override fun writeEntityToNBT(par1NBTTagCompound: NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound)
        par1NBTTagCompound.putInt("engine_type", engine_type.getID())
    }

    override fun readEntityFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound)
        this.engine_type = (Engine_Type.byID(par1NBTTagCompound.getIntOr("engine_type", 0)))!!
    }

    override fun getRocketTier(): Int {
        return this.tier
    }

    override fun getPreLaunchWait(): Int {
        return 400
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun getItemsDropped(droppedItems: MutableList<ItemStack?>?): MutableList<ItemStack>? {
        super.getItemsDropped(droppedItems)
        val rocket: ItemStack = getPickedResult(null)
        rocket.setTagCompound(NBTTagCompound())
        rocket.getTagCompound()!!.putInt("RocketFuel", this.fuelTank.getFluidAmount())
        rocket.getTagCompound()!!.putBoolean(engine_type.getName(), true)
        if (engine_type != Engine_Type.FUEL_ENGINE) rocket.getTagCompound()!!.putInt(ItemSpaceSuit.mod_count, 0)
        (droppedItems)!!.add(rocket)
        return ((droppedItems as MutableList<ItemStack>?))!!
    }

    override fun getRenderOffsetY(): Float {
        return -0.2f
    }

    override fun isDockValid(dock: IFuelDock?): Boolean {
        return dock is IFuelDock
    }

    override fun addFuel(liquid: FluidStack?, doFill: Boolean): Int {
        return GSUtils.fillWithGCFuel(this.fuelTank, liquid, doFill, this)
    }

    fun setEngine(type: Engine_Type) {
        this.engine_type = type
    }

    override fun getEngine(): Engine_Type? {
        return this.engine_type
    }

    abstract override fun getFuelTankCapacity(): Int

    abstract override fun getYOffset(): Double

    abstract override fun getPickedResult(target: RayTraceResult?): ItemStack

    abstract override fun getMountedYOffset(): Double

    abstract override fun getRotateOffset(): Float

    abstract override fun getOnPadYOffset(): Double
}
