package galaxyspace.core.hooks

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants
import foundation.block.getMetaFromState

import java.lang.reflect.Method
import java.util.HashSet
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type
import galaxyspace.core.prefab.entities.EntityMultiSeatRocket
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.entity.IEntityNoisy
import micdoodle8.mods.galacticraft.api.item.ISensorGlassesArmor
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.client.BubbleRenderer
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.client.FootprintRenderer
import micdoodle8.mods.galacticraft.core.client.SkyProviderMoon
import micdoodle8.mods.galacticraft.core.client.SkyProviderOverworld
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.client.jei.GalacticraftJEI
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.IBubbleProviderColored
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.network.GalacticraftPacketHandler
import micdoodle8.mods.galacticraft.core.network.PacketRotateRocket
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.MapUtil
import micdoodle8.mods.galacticraft.core.wrappers.BlockMetaList
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import net.minecraft.block.material.Material
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase

import galaxyspace.core.configs.GSConfigCore
import galaxyspace.GalaxySpace
import galaxyspace.core.events.SetBlockEvent
import galaxyspace.core.events.UpdateBlockEvent
import asmodeuscore.api.dimension.IAdvancedSpace
import galaxyspace.api.block.IEnergyGeyser
import galaxyspace.core.hooks.GSHooksManager
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.item.IItemThermal
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityGasLiquefier
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityGeothermalGenerator
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemStack
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.village.MerchantRecipeList
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.util.ArrayList
import java.util.LinkedList
import java.util.Locale
import java.util.Random
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object GSHooksManager {
    //@Hook(returnCondition = ReturnCondition.ON_TRUE, booleanReturnConstant = false)
    @JvmStatic
    fun setBlockState(world: World?, pos: BlockPos?, newState: IBlockState?, flags: Int): Boolean {
        return MinecraftForge.EVENT_BUS.post(SetBlockEvent(world, pos, newState, flags))
    }

    //@Hook(returnCondition = ReturnCondition.ALWAYS, isMandatory = true)
    @JvmStatic
    fun randomTick(block: Block, worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        val event: UpdateBlockEvent = UpdateBlockEvent(worldIn, pos, state, rand)
        MinecraftForge.EVENT_BUS.post(event)

        if (!event.isCanceled()) block.updateTick(worldIn, pos, state, rand)
    }

    //@Hook(returnCondition = ReturnCondition.ALWAYS)
    @JvmStatic
    fun getAirProducts(te: TileEntityMethaneSynthesizer): Int {
        val WP: WorldProvider? = te.getWorld().provider
        if (WP is IGalacticraftWorldProvider) {
            val atmos: ArrayList<EnumAtmosphericGas> =
                ((WP as IGalacticraftWorldProvider).getCelestialBody())!!.atmosphere.composition

            if (atmos.contains(EnumAtmosphericGas.CO2)) return 1
            /*
            if (atmos.size() > 0)
            {
                if (atmos.get(0) == EnumAtmosphericGas.CO2)
                {
                    return 1;
                }
            }
            if (atmos.size() > 1)
            {
                if (atmos.get(1) == EnumAtmosphericGas.CO2)
                {
                    return 1;
                }
            }
            if (atmos.size() > 2)
            {
                if (atmos.get(2) == EnumAtmosphericGas.CO2)
                {
                    return 1;
                }
            }
            */
            return 0
        }

        return 0
    }

    //@Hook(returnCondition = ReturnCondition.ALWAYS)
    @JvmStatic
    fun getAirProducts(te: TileEntityGasLiquefier): Int {
        val WP: WorldProvider? = te.getWorld().provider
        if (WP is IGalacticraftWorldProvider) {
            var result: Int = 0
            val atmos: ArrayList<EnumAtmosphericGas> = ((WP as IGalacticraftWorldProvider)
                .getCelestialBody())!!.atmosphere.composition
            if (atmos.size > 0) {
                result = te.getIdFromName(atmos.get(0)!!.name.lowercase(Locale.getDefault())) + 1
            }
            if (atmos.size > 1) {
                result += 16 * (te.getIdFromName(atmos.get(1)!!.name.lowercase(Locale.getDefault())) + 1)
            }
            if (atmos.size > 2) {
                result += 256 * (te.getIdFromName(atmos.get(2)!!.name.lowercase(Locale.getDefault())) + 1)
            }

            return result
        }

        return 35
    }

    @JvmStatic
    fun checkThermalStatus(handler: GCPlayerHandler, player: EntityPlayerMP, playerStats: GCPlayerStats) {
        if (player.level().provider is IGalacticraftWorldProvider && !player.capabilities.isCreativeMode && !CompatibilityManager.isAndroid(
                player
            )
        ) {
            val thermalPaddingHelm: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(6)
            val thermalPaddingChestplate: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(7)
            val thermalPaddingLeggings: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(8)
            val thermalPaddingBoots: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(9)
            var lowestThermalStrength: Float = 0.0f
            if (!thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty()) {
                if (thermalPaddingHelm.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingHelm.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingChestplate.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingChestplate.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingLeggings.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingLeggings.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingBoots.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingBoots.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                lowestThermalStrength /= 4.0f
                lowestThermalStrength = abs(lowestThermalStrength) //It shouldn't be negative, but just in case!
            }

            var provider: IGalacticraftWorldProvider = player.level().provider as IGalacticraftWorldProvider
            var thermalLevelMod: Float = provider.getThermalLevelModifier()

            if (player.level().provider is IAdvancedSpace) {
                provider = player.level().provider as IAdvancedSpace
                thermalLevelMod = provider.getThermalLevelModifier(player)
            }


            //System.out.println(thermalLevelMod);
            var absThermalLevelMod: Float = abs(thermalLevelMod)

            if (absThermalLevelMod > 0.0) {
                val thermalLevelCooldownBase: Int = abs(MathHelper.floor(200 / thermalLevelMod))
                var normaliseCooldown: Int = MathHelper.floor(150 / lowestThermalStrength)
                var thermalLevelTickCooldown: Int = thermalLevelCooldownBase
                if (thermalLevelTickCooldown < 1) {
                    thermalLevelTickCooldown = 1 //Prevent divide by zero errors
                }

                if (!thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty()) {
                    //If the thermal strength exceeds the dimension's thermal level mod, it can't improve the normalise
                    //This factor of 1.5F is chosen so that a combination of Tier 1 and Tier 2 thermal isn't enough to normalise on Venus (three Tier 2, one Tier 1 stays roughly constant)
                    val relativeFactor: Float = max(1.0f, absThermalLevelMod / lowestThermalStrength) / 1.5f
                    normaliseCooldown = MathHelper.floor(normaliseCooldown / absThermalLevelMod * relativeFactor)
                    if (normaliseCooldown < 1) {
                        normaliseCooldown = 1 //Prevent divide by zero errors
                    }
                    // Player is wearing all required thermal padding items
                    if ((player.tickCount - 1) % normaliseCooldown == 0) {
                        handler.normaliseThermalLevel(player, playerStats, 1)
                    }
                    thermalLevelMod /= max(1.0f, lowestThermalStrength / 2.0f)
                    absThermalLevelMod = abs(thermalLevelMod)
                }

                if (OxygenUtil.isAABBInBreathableAirBlock(player, true)) {
                    playerStats.setThermalLevelNormalising(true)
                    handler.normaliseThermalLevel(player, playerStats, 1)
                    // If player is in ambient thermal area, slowly reset to normal
                    return
                }

                // For each piece of thermal equipment being used, slow down the the harmful thermal change slightly
                if (!thermalPaddingHelm.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingChestplate.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingLeggings.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingBoots.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }

                // Instead of increasing/decreasing the thermal level by a large amount every ~200 ticks, increase/decrease
                //      by a small amount each time (still the same average increase/decrease)
                var thermalLevelTickCooldownSingle: Int =
                    MathHelper.floor(thermalLevelTickCooldown / absThermalLevelMod)
                if (thermalLevelTickCooldownSingle < 1) {
                    thermalLevelTickCooldownSingle = 1 //Prevent divide by zero errors
                }

                if ((player.tickCount - 1) % thermalLevelTickCooldownSingle == 0) {
                    val last: Int = playerStats.getThermalLevel()
                    playerStats.setThermalLevel(min(max(last + (if (thermalLevelMod < 0) -1 else 1), -22), 22))

                    if (playerStats.getThermalLevel() != last) {
                        //handler.sendThermalLevelPacket(player, playerStats);
                        (GalacticraftCore.packetPipeline)!!.sendTo(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.C_UPDATE_THERMAL_LEVEL,
                                GCCoreUtil.getDimensionID(player.level()),
                                arrayOf<Any>(playerStats.getThermalLevel(), playerStats.isThermalLevelNormalising())
                            ), player
                        )
                    }
                }

                // If the normalisation is outpacing the freeze/overheat
                playerStats.setThermalLevelNormalising(thermalLevelTickCooldownSingle > normaliseCooldown && !thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty())

                if (!playerStats.isThermalLevelNormalising()) {
                    if ((player.tickCount - 1) % thermalLevelTickCooldown == 0) {
                        if (abs(playerStats.getThermalLevel()) >= 22) {
                            foundation.damage.FoundationDamageTypes.hurt(player, DamageSourceGC.thermal, 1.5f)
                        }
                    }

                    if (playerStats.getThermalLevel() < -15) {
                        player.addEffect(PotionEffect(MobEffects.SLOWNESS, 5, 2, true, true))
                    }

                    if (playerStats.getThermalLevel() > 15) {
                        player.addEffect(PotionEffect(MobEffects.NAUSEA, 5, 2, true, true))
                    }
                }
            } else  //Normalise thermal level if on Space Station or non-modifier planet
            {
                playerStats.setThermalLevelNormalising(true)
                handler.normaliseThermalLevel(player, playerStats, 2)
            }
        } else  //Normalise thermal level if on Overworld or any non-GC dimension
        {
            playerStats.setThermalLevelNormalising(true)
            handler.normaliseThermalLevel(player, playerStats, 3)
        }
    }

    //@Hook(returnCondition = ReturnCondition.ALWAYS)
    @JvmStatic
    fun getSolarEnergyMultiplier(wp: WorldProviderVenus?): Double {
        return 0.0
    }

    //@Hook(returnCondition = ReturnCondition.ALWAYS)
    @JvmStatic
    fun getGuaranteedLoot(creeper: EntityCreeperBoss?, rand: Random): ItemStack {
        val stackList: MutableList<ItemStack?> = LinkedList<ItemStack?>()
        stackList.addAll(GalacticraftRegistry.getDungeonLoot(2))

        return stackList.get(rand.nextInt(stackList.size))!!.copy()
    }

    //@Hook(injectOnLine=59, returnCondition = ReturnCondition.ALWAYS)
    @JvmStatic
    fun update(te: TileEntityGeothermalGenerator) {
        if (te.ticks % 20 == 0) {
            val below: BlockPos = te.getPos().down()
            val stateBelow: IBlockState = te.getWorld().getBlockState(below)

            val lastValidSpout: Boolean = ReflectionHelper.getPrivateValue<Boolean, TileEntityGeothermalGenerator?>(
                micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityGeothermalGenerator::class.java,
                te,
                "validSpout"
            )!!
            var validSpoutHook: Boolean = false
            ReflectionHelper.setPrivateValue<TileEntityGeothermalGenerator?,Boolean>(
                TileEntityGeothermalGenerator::class.java,
                te,
                validSpoutHook,
                "validSpout"
            )
            if (stateBelow.getBlock() is IEnergyGeyser) {
                var pos1: BlockPos = below.below()
                while (te.getPos().getY() - pos1.getY() < 20) {
                    val state: IBlockState = te.getWorld().getBlockState(pos1)
                    val geyser: IEnergyGeyser = stateBelow.getBlock() as IEnergyGeyser
                    val work: Boolean = geyser.isWorkGeyser(te.getWorld(), stateBelow, below)

                    if (work) {
                        validSpoutHook = true
                        ReflectionHelper.setPrivateValue<TileEntityGeothermalGenerator?,Boolean>(
                            TileEntityGeothermalGenerator::class.java,
                            te,
                            validSpoutHook,
                            "validSpout"
                        )
                        break
                    } else if (!state.getBlock().isAir(te.getWorld().getBlockState(pos1), te.getWorld(), pos1)) {
                        // Not valid
                        break
                    }

                    pos1 = pos1.below()
                }
            } else if (stateBelow.getBlock() === VenusBlocks.spout) {
                var pos1: BlockPos = below.below()
                while (te.getPos().getY() - pos1.getY() < 20) {
                    val state: IBlockState = te.getWorld().getBlockState(pos1)
                    if (state.getBlock() === (VenusModule.sulphuricAcid)!!.getBlock()) {
                        validSpoutHook = true
                        ReflectionHelper.setPrivateValue<TileEntityGeothermalGenerator?,Boolean>(
                            TileEntityGeothermalGenerator::class.java,
                            te,
                            validSpoutHook,
                            "validSpout"
                        )
                        break
                    } else if (!state.getBlock().isAir(te.getWorld().getBlockState(pos1), te.getWorld(), pos1)) {
                        // Not valid
                        break
                    }
                    pos1 = pos1.below()
                }
            }

            if (te.getWorld().isRemote && validSpoutHook != lastValidSpout) {
                // Update active texture
                val state: IBlockState = te.getWorld().getBlockState(te.getPos())
                te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3)
            }
        }

        if (!te.getWorld().isRemote) {
            te.recharge(te.getItem(0))

            if (te.disableCooldown > 0) {
                te.disableCooldown--
            }

            te.generateWatts = min(
                max(
                    GSHooksManager.getGenerate(
                        te, ReflectionHelper.getPrivateValue<Boolean, TileEntityGeothermalGenerator?>(
                            micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityGeothermalGenerator::class.java,
                            te,
                            "validSpout"
                        )!!
                    ), 0
                ), TileEntityGeothermalGenerator.MAX_GENERATE_GJ_PER_TICK
            )
        } else {
            if (te.generateWatts > 0 && te.ticks % ((TileEntityGeothermalGenerator.MAX_GENERATE_GJ_PER_TICK.toFloat() / (te.generateWatts + 1)).toInt() * 5 + 1) == 0) {
                val posX: Double = te.getPos().getX() + 0.5
                val posY: Double = te.getPos().getY() + 1.0
                val posZ: Double = te.getPos().getZ() + 0.5
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX - 0.25, posY, posZ - 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX - 0.25, posY, posZ),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX - 0.25, posY, posZ + 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX, posY, posZ - 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle("acidExhaust", Vector3(posX, posY, posZ), Vector3(0.0, 0.025, 0.0))
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX, posY, posZ + 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX + 0.25, posY, posZ - 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX + 0.25, posY, posZ),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX + 0.25, posY, posZ + 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
            }
        }

        te.produce()
    }

    @JvmStatic
    private fun getGenerate(te: TileEntityGeothermalGenerator, valid: Boolean): Int {
        if (te.getDisabled(0)) {
            return 0
        }

        if (!valid) {
            return 0
        }

        val diff: Int =
            TileEntityGeothermalGenerator.MAX_GENERATE_GJ_PER_TICK - TileEntityGeothermalGenerator.MIN_GENERATE_GJ_PER_TICK
        return floor((sin((te.ticks / 50.0f).toDouble()) * 0.5f + 0.5f) * diff + TileEntityGeothermalGenerator.MIN_GENERATE_GJ_PER_TICK).toInt()
    }

    // @Hook(returnCondition = ReturnCondition.ALWAYS)
    @JvmStatic
    fun getGravity(wp: WorldProviderAsteroids?): Float {
        if (GSConfigCore.enableZeroGravityOnAsteroids) return 0.08f

        return 0.072f
    }

    /*
	private static List<BlockPos> connectedPads = new ArrayList<BlockPos>();
	private static Ticket chunkLoadTicket;
	
	@Hook(injectOnLine=88)
    public static void update(TileEntityLaunchController te)
    {		
        if (!te.getWorld().isRemote)
        {
      		te.controlEnabled = te.launchSchedulingEnabled && te.hasEnoughEnergyToRun && !te.getDisabled(0);
        	
      		boolean frequencyCheckNeeded = ReflectionHelper.getPrivateValue(TileEntityLaunchController.class, te, "frequencyCheckNeeded");
        	if (frequencyCheckNeeded)
            {
                te.checkDestFrequencyValid();
                frequencyCheckNeeded = false;
            }
        	ReflectionHelper.setPrivateValue(TileEntityLaunchController.class, te, frequencyCheckNeeded, "frequencyCheckNeeded");
           
        	if (te.requiresClientUpdate)
            {
                // PacketDispatcher.sendPacketToAllPlayers(this.getPacket());
                // TODO
                te.requiresClientUpdate = false;
            }

            if (te.ticks % 40 == 0)
            {
                te.setFrequency(te.frequency);
                te.setDestinationFrequency(te.destFrequency);
                
                //System.out.println(te.attachedDock);
            }

            //Ticket chunkLoadTicket = ReflectionHelper.getPrivateValue(TileEntityLaunchController.class, te, "chunkLoadTicket");
            //List<BlockPos> connectedPads = ReflectionHelper.getPrivateValue(TileEntityLaunchController.class, te, "connectedPads");
            if (te.ticks % 20 == 0)
            {
                if (chunkLoadTicket != null)
                {                	
                    for (int i = 0; i < connectedPads.size(); i++)
                    {
                        BlockPos coords = connectedPads.get(i);
                        Block block = te.getWorld().getBlockState(coords).getBlock();
                        TileEntity tile = te.getWorld().getTileEntity(coords);
                        
                        if (!(tile instanceof IFuelDock))
                        {
                        	connectedPads.remove(i);
                        	//ReflectionHelper.setPrivateValue(TileEntityLaunchController.class, te, connectedPads, "connectedPads");
                            ForgeChunkManager.unforceChunk(chunkLoadTicket, new ChunkPos(coords.getX() >> 4, coords.getZ() >> 4));
                        }
                    }
                }
            }
        }
        else
        {
            if (te.frequency == -1 && te.destFrequency == -1)
            {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimpleMars(EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI, GCCoreUtil.getDimensionID(te.getWorld()), new Object[] { 5, te.getPos(), 0 }));
            }
        }
        return;
    }
	
	@Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void onTicketLoaded(TileEntityLaunchController te, Ticket ticket, boolean placed)
    {
        if (!te.getWorld().isRemote && ConfigManagerMars.launchControllerChunkLoad)
        {
            if (ticket == null)
            {
                return;
            }
           
            if (chunkLoadTicket == null)
            {
            	chunkLoadTicket = ticket;
            }
   
            NBTTagCompound nbt = chunkLoadTicket.getModData();
            nbt.putInt("ChunkLoaderTileX", te.getPos().getX());
            nbt.putInt("ChunkLoaderTileY", te.getPos().getY());
            nbt.putInt("ChunkLoaderTileZ", te.getPos().getZ());

            //List<BlockPos> connectedPads = ReflectionHelper.getPrivateValue(TileEntityLaunchController.class, te, "connectedPads");
            
            
            for (int x = -3; x <= 3; x++)
            {
                for (int z = -3; z <= 3; z++)
                {
                    TileEntity tile = te.getWorld().getTileEntity(te.getPos().add(x, 0, z));
                    Block blockID = te.getWorld().getBlockState(te.getPos().add(x, 0, z)).getBlock();

                    if (tile instanceof IFuelDock)
                    {
                    	if (te.getPos().getX() + x >> 4 != te.getPos().getX() >> 4 || te.getPos().getZ() + z >> 4 != te.getPos().getZ() >> 4)
                        {
                        	
                            connectedPads.add(new BlockPos(te.getPos().getX() + x, te.getPos().getY(), te.getPos().getZ() + z));
                        	//ReflectionHelper.setPrivateValue(TileEntityLaunchController.class, te, connectedPads, "connectedPads");
                            
                            if (placed)
                            {
                                ChunkLoadingCallback.forceChunk(chunkLoadTicket, te.getWorld(), te.getPos().getX() + x, te.getPos().getY(), te.getPos().getZ() + z, te.getOwnerName());
                            }
                            else
                            {
                                ChunkLoadingCallback.addToList(te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), te.getOwnerName());
                            }
                        }
                    }
                }
            }

            ChunkLoadingCallback.forceChunk(chunkLoadTicket, te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), te.getOwnerName());
        }
    }
	
	@Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean canAttachToLandingPad(TileEntityLaunchController te, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getBlockEntity(pos);
        //GalaxySpace.instance.debug(tile);
        return tile instanceof IFuelDock;
    }
	

	@Hook(returnCondition = ReturnCondition.ALWAYS)
	public static void updateRocketOnDockSettings(TileEntityLaunchController te)
    {
        if (te.attachedDock instanceof IFuelDock)
        {
       	
        	IFuelDock pad = ((IFuelDock) te.attachedDock);
            IDockable rocket = pad.getDockedEntity();
            if (rocket instanceof EntityAutoRocket)
            {
                ((EntityAutoRocket) rocket).updateControllerSettings(pad);
            }
        }
    }
	
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	public static boolean setTarget(EntityAutoRocket rocket, boolean doSet, int destFreq)
    {
		Class<?> controllerClass = ReflectionHelper.getPrivateValue(EntityAutoRocket.class, rocket, "controllerClass");
    	
		
        WorldServer[] servers = GCCoreUtil.getWorldServerList(rocket.world);
        if (!GalacticraftCore.isPlanetsLoaded || controllerClass == null)
        {
            return false;
        }

        for (int i = 0; i < servers.length; i++)
        {
            WorldServer world = servers[i];

            try
            {
                for (TileEntity tile : new ArrayList<TileEntity>(world.loadedTileEntityList))
                {
                    if (!controllerClass.isInstance(tile))
                        continue;

                    tile = world.getBlockEntity(tile.getPos());
                    if (!controllerClass.isInstance(tile))
                			continue;

                		int controllerFrequency = controllerClass.getField("frequency").getInt(tile);

                		if (destFreq == controllerFrequency)
                		{
                			boolean targetSet = false;
                			
                			blockLoop:
                				for (int x = -3; x <= 4; x++)
                				{
                					for (int z = -3; z <= 4; z++)
                					{
                                        BlockPos pos = new BlockPos(tile.getPos().add(x, 0, z));
                						Block block = world.getBlockState(pos).getBlock();
                						TileEntity tileen = world.getBlockEntity(pos);
                	                        
                						//if (block instanceof BlockLandingPadFull)
                						if (tileen instanceof IFuelDock)
                						{
                							System.out.println(block);
                							if (doSet)
                							{
                								rocket.targetVec = pos;
                							}
                							targetSet = true;
                							break blockLoop;
                						}
                					}
                				}

                			if (doSet)
                			{
                				rocket.targetDimension = tile.getWorld().provider.getDimension();
                			}

                			if (!targetSet)
                			{
                				if (doSet)
                				{
                					rocket.targetVec = null;
                				}

                				return false;
                			}
                			else
                			{
                				return true;
                			}
                		}
                	}
                }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }

        return false;
    }
	
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	public static boolean setFrequency(EntityAutoRocket rocket)
    {
		Class<?> controllerClass = ReflectionHelper.getPrivateValue(EntityAutoRocket.class, rocket, "controllerClass");
		BlockVec3 activeLaunchController  = ReflectionHelper.getPrivateValue(EntityAutoRocket.class, rocket, "activeLaunchController");
        if (!GalacticraftCore.isPlanetsLoaded || controllerClass == null)
        {
            return false;
        }

        if (activeLaunchController != null)
                                {
            TileEntity launchController = activeLaunchController.getTileEntity(rocket.world);
            if (controllerClass.isInstance(launchController))
                                {
                                    try
                                    {
                                Boolean b = (Boolean) controllerClass.getMethod("validFrequency").invoke(launchController);

                                if (b != null && b)
                                {
                                    int controllerFrequency = controllerClass.getField("destFrequency").getInt(launchController);
                                    boolean foundPad = setTarget(rocket, false, controllerFrequency);
                                   
                                    if (foundPad)
                                    {
                                        rocket.destinationFrequency = controllerFrequency;
                                        GCLog.debug("Rocket under launch control: going to target frequency " + controllerFrequency);
                                        return true;
                                    }
                                }
                            }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

        rocket.destinationFrequency = -1;
        return false;
    }
	*/
    private val DEFAULT_TRADE_LIST_MAP: Array<EntityAlienVillager.ITradeList?> =
        ((arrayOf<EntityAlienVillager.ITradeList>(
            EntityAlienVillager.ItemAndEmeraldToItem(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1),
                EntityAlienVillager.PriceInfo(40, 55),
                BasicItems.SCHEMATIC_BOX.getItemStack()
            ),


            EntityAlienVillager.ListItemForEmeralds(
                ItemStack(GCItems.oxMask, 1, 0),
                EntityAlienVillager.PriceInfo(1, 2)
            ),
            EntityAlienVillager.ListItemForEmeralds(
                ItemOxygenTank.stack(requireNotNull(GCItems.oxTankLight), 1, 235),
                EntityAlienVillager.PriceInfo(3, 4)
            ),
            EntityAlienVillager.ListItemForEmeralds(
                ItemStack(GCItems.oxygenGear, 1, 0),
                EntityAlienVillager.PriceInfo(3, 4)
            ),
            EntityAlienVillager.ListItemForEmeralds(
                ItemStack(GCItems.fuelCanister, 1, 317),
                EntityAlienVillager.PriceInfo(3, 4)
            ),
            EntityAlienVillager.ListItemForEmeralds(
                FoundationItemVariants.stack(requireNotNull(GCItems.parachute), 1, 0),
                EntityAlienVillager.PriceInfo(1, 2)
            ),
            EntityAlienVillager.ListItemForEmeralds(
                ItemStack(GCItems.battery, 1, 58),
                EntityAlienVillager.PriceInfo(2, 4)
            ),
            EntityAlienVillager.ItemAndEmeraldToItem(
                ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY),
                EntityAlienVillager.PriceInfo(1, 1),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 1)
            ),  //carrots = also yields a tin!
            EntityAlienVillager.ListItemForEmeralds(
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, ItemBasic.WAFER_BASIC),
                EntityAlienVillager.PriceInfo(3, 4)
            ),
            EntityAlienVillager.ItemAndEmeraldToItem(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0),
                EntityAlienVillager.PriceInfo(3, 5),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1)
            ),  //Exchange buggy and rocket schematics
            EntityAlienVillager.ItemAndEmeraldToItem(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1),
                EntityAlienVillager.PriceInfo(3, 5),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0)
            ),  //Exchange buggy and rocket schematics
            EntityAlienVillager.ItemAndEmeraldToItem(
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 2, 3),
                EntityAlienVillager.PriceInfo(1, 1),
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 6)
            ),  //Compressed Tin - needed to craft a Fuel Loader
            EntityAlienVillager.ItemAndEmeraldToItem(
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 2, 4),
                EntityAlienVillager.PriceInfo(1, 1),
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 7)
            ),  //Compressed Copper - needed to craft a Fuel Loader
            EntityAlienVillager.EmeraldForItems(
                ItemStack(Blocks.SAPLING, 1, 3),
                EntityAlienVillager.PriceInfo(11, 39)
            ) //The one thing Alien Villagers don't have and can't get is jungle trees...
        ) as Array<EntityAlienVillager.ITradeList?>?))!!

    //@Hook(returnCondition = ReturnCondition.ALWAYS)
    @JvmStatic
    fun populateBuyingList(e: EntityAlienVillager) {
        var buyingList: MerchantRecipeList? =
            ReflectionHelper.getPrivateValue<MerchantRecipeList?, EntityAlienVillager?>(
                EntityAlienVillager::class.java,
                e,
                "buyingList"
            )
        if (buyingList == null) {
            buyingList = MerchantRecipeList()
        }

        for (tradeList: EntityAlienVillager.ITradeList? in DEFAULT_TRADE_LIST_MAP) {
            tradeList!!.modifyMerchantRecipeList(buyingList, e.getEntityWorld().rand)
        }

        ReflectionHelper.setPrivateValue<EntityAlienVillager?, MerchantRecipeList?>(
            EntityAlienVillager::class.java,
            e,
            buyingList,
            "buyingList"
        )
    }
    /**
     * RUS: Хук, заменяющий метод [TickHandlerClient.onClientTick],
     * запрещает "пассажирам" управлять ракетой, если она многоместная
     * EN: Hook replacing the method [TickHandlerClient.onClientTick],
     * prohibits "passengers" from controlling a rocket if it is multi-seat
     * @author gug2
     */
    //	@Hook(returnCondition = ReturnCondition.ALWAYS)
    //	public static void onClientTick(TickHandlerClient object, ClientTickEvent event) {
    //		final Minecraft minecraft = FMLClientHandler.instance().getClient();
    //        final WorldClient world = minecraft.world;
    //        final EntityPlayerSP player = minecraft.player;
    //
    //        if (object.teleportingGui != null)
    //        {
    //            if (minecraft.currentScreen != object.teleportingGui)
    //            {
    //                minecraft.currentScreen = object.teleportingGui;
    //            }
    //        }
    //
    //        if (object.menuReset)
    //        {
    //            TickHandlerClient.resetClient();
    //            object.menuReset = false;
    //        }
    //
    //        if (event.phase == Phase.START && player != null)
    //        {
    //            if (ClientProxyCore.playerHead == null && player.getGameProfile() != null)
    //            {
    //                Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(player.getGameProfile());
    //
    //                if (map.containsKey(Type.SKIN))
    //                {
    //                    ClientProxyCore.playerHead = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
    //                }
    //                else
    //                {
    //                    ClientProxyCore.playerHead = DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(player.getGameProfile()));
    //                }
    //            }
    //
    //            //TickHandlerClient.tickCount++;
    //            // Added code
    //            long reflectedTickCount = ReflectionHelper.getPrivateValue(TickHandlerClient.class, object, "tickCount");
    //            ReflectionHelper.setPrivateValue(TickHandlerClient.class, object, reflectedTickCount + 1, "tickCount");
    //            //
    //
    //            if (!GalacticraftCore.proxy.isPaused())
    //            {
    //            	// Added code
    //            	Set<FluidNetwork> reflectedFluidNetworks = ReflectionHelper.getPrivateValue(TickHandlerClient.class, object, "fluidNetworks");
    //            	//
    //
    //                Iterator<FluidNetwork> it = reflectedFluidNetworks.iterator();
    //                while (it.hasNext())
    //                {
    //                    FluidNetwork network = it.next();
    //
    //                    if (network.getTransmitters().size() == 0)
    //                    {
    //                        it.remove();
    //                    }
    //                    else
    //                    {
    //                        network.clientTick();
    //                    }
    //                }
    //            }
    //
    //            //if (TickHandlerClient.tickCount % 20 == 0)
    //            // Added code
    //            if (reflectedTickCount % 20 == 0)
    //            //
    //            {
    //                BubbleRenderer.clearBubbles();
    //
    //                for (TileEntity tile : player.world.tickableTileEntities)
    //                {
    //                    if (tile instanceof IBubbleProviderColored)
    //                    {
    //                        BubbleRenderer.addBubble((IBubbleProviderColored)tile);
    //                    }
    //                }
    //
    //                if (object.updateJEIhiding)
    //                {
    //                	object.updateJEIhiding = false;
    //                    // Update JEI to hide the ingot compressor recipe for GC steel in hard mode
    //                    // Update JEI to hide adventure mode recipes when not in adventure mode
    //                    GalacticraftJEI.updateHidden(CompressorRecipes.steelIngotsPresent && ConfigManagerCore.hardMode && !ConfigManagerCore.challengeRecipes, !ConfigManagerCore.challengeRecipes);
    //                }
    //
    //                for (List<Footprint> fpList : FootprintRenderer.footprints.values())
    //                {
    //                    Iterator<Footprint> fpIt = fpList.iterator();
    //                    while (fpIt.hasNext())
    //                    {
    //                        Footprint fp = fpIt.next();
    //                        fp.age += 20;
    //                        fp.lightmapVal = player.world.getCombinedLight(new BlockPos(fp.position.x, fp.position.y, fp.position.z), 0);
    //
    //                        if (fp.age >= Footprint.MAX_AGE)
    //                        {
    //                            fpIt.remove();
    //                        }
    //                    }
    //                }
    //
    //                if (!player.inventory.armorItemInSlot(3).isEmpty() && player.inventory.armorItemInSlot(3).getItem() instanceof ISensorGlassesArmor)
    //                {
    //                    ClientProxyCore.valueableBlocks.clear();
    //
    //                    for (int i = -4; i < 5; i++)
    //                    {
    //                        int x = MathHelper.floor(player.posX + i);
    //                        for (int j = -4; j < 5; j++)
    //                        {
    //                            int y = MathHelper.floor(player.posY + j);
    //                            for (int k = -4; k < 5; k++)
    //                            {
    //                                int z = MathHelper.floor(player.posZ + k);
    //                                BlockPos pos = new BlockPos(x, y, z);
    //
    //                                IBlockState state = player.world.getBlockState(pos);
    //                                final Block block = state.getBlock();
    //
    //                                if (block.getMaterial(state) != Material.AIR)
    //                                {
    //                                    int metadata = block.getMetaFromState(state);
    //                                    boolean isDetectable = false;
    //
    //                                    for (BlockMetaList blockMetaList : ClientProxyCore.detectableBlocks)
    //                                    {
    //                                        if (blockMetaList.getBlock() == block && blockMetaList.getMetaList().contains(metadata))
    //                                        {
    //                                            isDetectable = true;
    //                                            break;
    //                                        }
    //                                    }
    //
    //                                    if (isDetectable || (block instanceof IDetectableResource && ((IDetectableResource) block).isValueable(state)))
    //                                    {
    //                                        ClientProxyCore.valueableBlocks.add(new BlockVec3(x, y, z));
    //                                    }
    //                                }
    //                            }
    //                        }
    //                    }
    //
    //                    TileEntityOxygenSealer nearestSealer = TileEntityOxygenSealer.getNearestSealer(world, MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ));
    //                    if (nearestSealer != null && !nearestSealer.sealed)
    //                    {
    //                        ClientProxyCore.leakTrace = nearestSealer.getLeakTraceClient();
    //                    }
    //                    else
    //                    {
    //                        ClientProxyCore.leakTrace = null;
    //                    }
    //                }
    //                else
    //                {
    //                    ClientProxyCore.leakTrace = null;
    //                }
    //
    //                if (world != null)
    //                {
    //                    if (MapUtil.resetClientFlag.getAndSet(false))
    //                    {
    //                        MapUtil.resetClientBody();
    //                    }
    //                }
    //            }
    //
    //            //if (ClientProxyCore.leakTrace != null) this.spawnLeakParticles();
    //            // Added code
    //            if (ClientProxyCore.leakTrace != null) {
    //            	try {
    //            		Method reflectedSpawnLeakParticles = object.getClass().getDeclaredMethod("spawnLeakParticles");
    //            		reflectedSpawnLeakParticles.setAccessible(true);
    //            		reflectedSpawnLeakParticles.invoke(object);
    //            	} catch (Exception e) {
    //            		e.printStackTrace();
    //            	}
    //            }
    //            //
    //
    //            if (world != null && TickHandlerClient.spaceRaceGuiScheduled && minecraft.currentScreen == null && ConfigManagerCore.enableSpaceRaceManagerPopup)
    //            {
    //                player.openGui(GalacticraftCore.instance, GuiIdsCore.SPACE_RACE_START, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
    //                TickHandlerClient.spaceRaceGuiScheduled = false;
    //            }
    //
    //            // Added code
    //            boolean checkedVersion = false;
    //            //try {
    //            //	checkedVersion = ReflectionHelper.getPrivateValue(TickHandlerClient.class, object, "checkedVersion");
    //            //} catch (Exception e) {
    //            //	e.printStackTrace();
    //            //}
    //            //
    //            if (world != null && checkedVersion)
    //            {
    //                //ThreadVersionCheck.startCheck();
    //                //checkedVersion = false;
    //
    //            	// Added code
    //            	try {
    //            		Class<?> threadVersionClass = Class.forName("micdoodle8.mods.galacticraft.core.util.ThreadVersionCheck");
    //            		Object threadVersionInstance = threadVersionClass.newInstance();
    //            		threadVersionClass.getDeclaredMethod("startCheck").invoke(threadVersionInstance);
    //
    //            		ReflectionHelper.setPrivateValue(TickHandlerClient.class, object, false, "checkedVersion");
    //            	} catch (Exception e) {
    //            		e.printStackTrace();
    //            	}
    //            	//
    //            }
    //
    //            boolean inSpaceShip = false;
    //            if (player.getRidingEntity() instanceof EntitySpaceshipBase)
    //            {
    //                inSpaceShip = true;
    //                EntitySpaceshipBase rocket = (EntitySpaceshipBase) player.getRidingEntity();
    //
    //                if (rocket.prevRotationPitch != rocket.rotationPitch || rocket.prevRotationYaw != rocket.rotationYaw)
    //                {
    //                	// Added code
    //                	if(rocket instanceof EntityMultiSeatRocket) {
    //                		boolean isDriver = player.equals( ((EntityMultiSeatRocket) rocket).getDriver() );
    //
    //                		if(isDriver) {
    //                			GalacticraftCore.packetPipeline.sendToServer(new PacketRotateRocket(player.getRidingEntity()));
    //                		}
    //                	} else {
    //                	//
    //                		GalacticraftCore.packetPipeline.sendToServer(new PacketRotateRocket(player.getRidingEntity()));
    //                    // Added code
    //                	}
    //                    //
    //                }
    //            }
    //
    //            if (world != null)
    //            {
    //                if (world.provider instanceof WorldProviderSurface)
    //                {
    //                    if (world.provider.getSkyRenderer() == null && inSpaceShip &&
    //                            player.getRidingEntity().posY > Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT)
    //                    {
    //                        world.provider.setSkyRenderer(new SkyProviderOverworld());
    //                    }
    //                    else if (world.provider.getSkyRenderer() instanceof SkyProviderOverworld && player.posY <= Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT)
    //                    {
    //                        world.provider.setSkyRenderer(null);
    //                    }
    //                }
    //                else if (world.provider instanceof WorldProviderSpaceStation)
    //                {
    //                    if (world.provider.getSkyRenderer() == null)
    //                    {
    //                        ((WorldProviderSpaceStation) world.provider).createSkyProvider();
    //                    }
    //                }
    //                else if (world.provider instanceof WorldProviderMoon)
    //                {
    //                    if (world.provider.getSkyRenderer() == null)
    //                    {
    //                        world.provider.setSkyRenderer(new SkyProviderMoon());
    //                    }
    //
    //                    if (world.provider.getCloudRenderer() == null)
    //                    {
    //                        world.provider.setCloudRenderer(new CloudRenderer());
    //                    }
    //                }
    //            }
    //
    //            if (inSpaceShip)
    //            {
    //                final EntitySpaceshipBase ship = (EntitySpaceshipBase) player.getRidingEntity();
    //                boolean hasChanged = false;
    //
    //                if (minecraft.gameSettings.keyBindLeft.isKeyDown())
    //                {
    //                	// Added code
    //                	if(ship instanceof EntityMultiSeatRocket) {
    //                		boolean isDriver = player.equals( ((EntityMultiSeatRocket) ship).getDriver() );
    //
    //                		if(isDriver) {
    //                			ship.turnYaw(-1.0F);
    //                			hasChanged = true;
    //                		}
    //                	} else {
    //                	//
    //                		ship.turnYaw(-1.0F);
    //                		hasChanged = true;
    //                    // Added code
    //                	}
    //                    //
    //                }
    //
    //                if (minecraft.gameSettings.keyBindRight.isKeyDown())
    //                {
    //                	// Added code
    //                	if(ship instanceof EntityMultiSeatRocket) {
    //                		boolean isDriver = player.equals( ((EntityMultiSeatRocket) ship).getDriver() );
    //
    //                		if(isDriver) {
    //                			ship.turnYaw(1.0F);
    //                			hasChanged = true;
    //                		}
    //                	} else {
    //                	//
    //                		ship.turnYaw(1.0F);
    //                		hasChanged = true;
    //                    // Added code
    //                	}
    //                    //
    //                }
    //
    //                if (minecraft.gameSettings.keyBindForward.isKeyDown())
    //                {
    //                	// Added code
    //                	if(ship instanceof EntityMultiSeatRocket) {
    //                		boolean isDriver = player.equals( ((EntityMultiSeatRocket) ship).getDriver() );
    //
    //                		if(isDriver) {
    //                			if(ship.getLaunched()) {
    //                				ship.turnPitch(-0.7F);
    //                				hasChanged = true;
    //                			}
    //                		}
    //                	} else {
    //                	//
    //                    	if (ship.getLaunched())
    //                    	{
    //                        	ship.turnPitch(-0.7F);
    //                        	hasChanged = true;
    //                    	}
    //                    // Added code
    //            		}
    //                	//
    //                }
    //
    //                if (minecraft.gameSettings.keyBindBack.isKeyDown())
    //                {
    //                	// Added code
    //                	if(ship instanceof EntityMultiSeatRocket) {
    //                		boolean isDriver = player.equals( ((EntityMultiSeatRocket) ship).getDriver() );
    //
    //                		if(isDriver) {
    //                			if(ship.getLaunched()) {
    //                				ship.turnPitch(0.7F);
    //                				hasChanged = true;
    //                			}
    //                		}
    //                	} else {
    //                	//
    //                    	if (ship.getLaunched())
    //                    	{
    //                        	ship.turnPitch(0.7F);
    //                        	hasChanged = true;
    //                    	}
    //                    // Added code
    //            		}
    //                	//
    //                }
    //
    //                if (hasChanged)
    //                {
    //                    GalacticraftCore.packetPipeline.sendToServer(new PacketRotateRocket(ship));
    //                }
    //            }
    //
    //            if (world != null)
    //            {
    //                List entityList = world.loadedEntityList;
    //                for (Object e : entityList)
    //                {
    //                    if (e instanceof IEntityNoisy)
    //                    {
    //                        IEntityNoisy vehicle = (IEntityNoisy) e;
    //                        if (vehicle.getSoundUpdater() == null)
    //                        {
    //                            ISound noise = vehicle.setSoundUpdater(FMLClientHandler.instance().getClient().player);
    //                            if (noise != null)
    //                            {
    //                                FMLClientHandler.instance().getClient().getSoundManager().play(noise);
    //                            }
    //                        }
    //                    }
    //                }
    //            }
    //
    //            if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection)
    //            {
    //                player.motionY = 0;
    //            }
    //
    //            if (world != null && world.provider instanceof IGalacticraftWorldProvider && OxygenUtil.noAtmosphericCombustion(world.provider) && ((IGalacticraftWorldProvider) world.provider).shouldDisablePrecipitation())
    //            {
    //                world.setRainStrength(0.0F);
    //            }
    //
    //            boolean isPressed = KeyHandlerClient.spaceKey.isKeyDown();
    //
    //            if (!isPressed)
    //            {
    //                ClientProxyCore.lastSpacebarDown = false;
    //            }
    //
    //            if (player.getRidingEntity() != null && isPressed && !ClientProxyCore.lastSpacebarDown)
    //            {
    //                // Added code
    //            	if(player.getRidingEntity() instanceof EntityMultiSeatRocket) {
    //            		boolean isDriver = player.equals( ((EntityMultiSeatRocket)player.getRidingEntity()).getDriver() );
    //
    //            		if(isDriver) {
    //            			GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_IGNITE_ROCKET, GCCoreUtil.getDimensionID(player.world), new Object[] {}));
    //            			ClientProxyCore.lastSpacebarDown = true;
    //            		}
    //            	} else {
    //            	//
    //            		GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_IGNITE_ROCKET, GCCoreUtil.getDimensionID(player.world), new Object[] {}));
    //            		ClientProxyCore.lastSpacebarDown = true;
    //                // Added code
    //            	}
    //                //
    //            }
    //
    //            if (!(object.screenConnectionsUpdateList.isEmpty()))
    //            {
    //                HashSet<TileEntityScreen> updateListCopy = (HashSet<TileEntityScreen>) object.screenConnectionsUpdateList.clone();
    //                object.screenConnectionsUpdateList.clear();
    //                for (TileEntityScreen te : updateListCopy)
    //                {
    //                    if (te.getWorld().getBlockState(te.getPos()).getBlock() == GCBlocks.screen)
    //                    {
    //                        if (te.refreshOnUpdate)
    //                        {
    //                            te.refreshConnections(true);
    //                        }
    //                        te.getWorld().markBlockRangeForRenderUpdate(te.getPos(), te.getPos());
    //                    }
    //                }
    //            }
    //        }
    //        else if (event.phase == Phase.END)
    //        {
    //        	// Added code
    //        	List<GalacticraftPacketHandler> reflectedPacketHandlers = ReflectionHelper.getPrivateValue(TickHandlerClient.class, object, "packetHandlers");
    //        	//
    //
    //            if (world != null)
    //            {
    //                for (GalacticraftPacketHandler handler : reflectedPacketHandlers)
    //                {
    //                    handler.tick(world);
    //                }
    //            }
    //        }
    //
    //        return;
    //	}
}
