package galaxyspace.core.events

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler.EnumModelPacketType
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler.ThermalArmorEvent
import net.minecraftforge.common.config.Config.Type
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.fml.common.eventhandler.Event.Result
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent

import asmodeuscore.api.dimension.IAdvancedSpace
import asmodeuscore.api.dimension.IProviderFreeze
import asmodeuscore.api.item.IItemSpaceFood
import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.event.PressureEvent
import asmodeuscore.core.event.RadiationEvent
import asmodeuscore.core.handler.LightningStormHandler
import galaxyspace.GalaxySpace
import galaxyspace.api.item.IJetpackArmor
import galaxyspace.core.GSBlocks
import galaxyspace.core.GSFluids
import galaxyspace.core.GSItems
import galaxyspace.core.configs.*
import galaxyspace.core.handler.capabilities.*
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.network.trackers.MultiSeatRocketDriverTracker
import galaxyspace.core.prefab.entities.EntityAstroWolf
import galaxyspace.core.prefab.items.rockets.ItemTier4Rocket
import galaxyspace.core.prefab.items.rockets.ItemTier5Rocket
import galaxyspace.core.prefab.items.rockets.ItemTier6Rocket
import galaxyspace.core.util.GSDamageSource
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigCore
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigDimensions
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigDimensions
import galaxyspace.systems.SolarSystem.moons.titan.dimension.WorldProviderTitan
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.WorldProviderKuiperBelt
import galaxyspace.systems.SolarSystem.planets.mars.dimension.WorldProviderMars_WE
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemThermalPaddingBase
import galaxyspace.systems.SolarSystem.planets.overworld.items.tools.ItemPlasmaSword
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityPlanetShield
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRadiationStabiliser
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.event.ZeroGravityEvent
import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent
import micdoodle8.mods.galacticraft.api.event.wgen.GCCoreEventPopulate
import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC
import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ItemTier1Rocket
import micdoodle8.mods.galacticraft.core.util.*
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemTier3Rocket
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars
import micdoodle8.mods.galacticraft.planets.mars.items.ItemTier2Rocket
import net.minecraft.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockLiquid
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.PotionEffect
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumActionResult
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.DimensionType
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingFallEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import kotlin.math.min

open class GSEventHandler {
    @SubscribeEvent
    open fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.getModID() == GalaxySpace.MODID) {
            ConfigManager.sync(GalaxySpace.MODID, Config.Type.INSTANCE)
            (GSConfigCore.Companion.config)!!.save()
            (GSConfigDimensions.Companion.config)!!.save()
            (GSConfigSchematics.Companion.config)!!.save()
            (GSConfigEnergy.Companion.config)!!.save()
            (ACConfigCore.config)!!.save()
            (ACConfigDimensions.config)!!.save()
            (BRConfigCore.config)!!.save()
            (BRConfigDimensions.config)!!.save()

            GSConfigCore.Companion.syncConfig(true)
            GSConfigDimensions.Companion.syncConfig(true)
            GSConfigSchematics.Companion.syncConfig(true)
            GSConfigEnergy.Companion.syncConfig(true)
            ACConfigCore.syncConfig(true)
            ACConfigDimensions.syncConfig(true)
            BRConfigCore.syncConfig(true)
            BRConfigDimensions.syncConfig(true)

            GalaxySpace.debug = GSConfigCore.Companion.enableDebug
            (GalaxySpace.instance)!!.debug("reload")
        }
    }

    @SubscribeEvent
    open fun onAttachCapability(event: AttachCapabilitiesEvent<Entity?>) {
        if (event.getObject() is EntityPlayerMP) {
            event.addCapability(
                GSCapabilityStatsHandler.GS_PLAYER_PROPERTIES,
                GSCapabilityProviderStats(event.getObject() as EntityPlayerMP?)
            )
        } else if (event.getObject() is EntityPlayer && (event.getObject() as EntityPlayer).world.isRemote) {
            this.onAttachCapabilityClient(event)
        }
    }

    @SideOnly(Side.CLIENT)
    private fun onAttachCapabilityClient(event: AttachCapabilitiesEvent<Entity?>) {
        if (event.getObject() is EntityPlayerSP) {
            event.addCapability(
                GSCapabilityStatsHandler.GS_PLAYER_PROPERTIES_CLIENT,
                GSCapabilityProviderStatsClient(event.getObject() as EntityPlayerSP?)
            )
        }
    }

    @SubscribeEvent
    open fun onPlayerCloned(event: PlayerEvent.Clone) {
        val oldStats: StatsCapability? = galaxyspace.core.handler.capabilities.StatsCapability.get(event.getOriginal())
        val newStats: StatsCapability = (galaxyspace.core.handler.capabilities.StatsCapability.get(event.getEntityPlayer()))!!
        newStats.copyFrom(
            oldStats,
            !event.isWasDeath() || event.getOriginal().world.getGameRules().getBoolean("keepInventory")
        )
    }

    @SubscribeEvent
    open fun onPlayerLogin(event: net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent) {
        if (event.player is EntityPlayerMP) {
            //GalaxySpace.packetPipeline.sendTo(new GSPacketSimple(GSEnumSimplePacket.C_UPDATE_WORLD, GCCoreUtil.getDimensionID(event.player.world)), (EntityPlayerMP)event.player);

            val stats: StatsCapability = (galaxyspace.core.handler.capabilities.StatsCapability.get(event.player))!!

            val ids: Array<Int> = (arrayOfNulls<Int>(256) as Array<Int>)
            for (i: Int in (stats.getKnowledgeResearches())!!.indices) {
                ids[i] = ((stats)!!.getKnowledgeResearches())!![i]
            }
            (GalaxySpace.packetPipeline)!!.sendTo(
                GSPacketSimple(
                    GSEnumSimplePacket.C_UPDATE_RESEARCHES,
                    GCCoreUtil.getDimensionID(event.player.world),
                    *arrayOf<Any>(ids)
                ), event.player as EntityPlayerMP
            )
        }
    }

    @SubscribeEvent
    open fun onFall(e: LivingFallEvent) {
        if (e.getEntityLiving() is EntityPlayer) {
            val chest: ItemStack = (e.getEntityLiving() as EntityPlayer).getItemStackFromSlot(EntityEquipmentSlot.CHEST)
            e.setCanceled(
                chest != null && chest.getItem() is IJetpackArmor && (chest.getItem() as IJetpackArmor).canFly(
                    chest,
                    e.getEntityLiving() as EntityPlayer?
                ) && (chest.getItem() as IJetpackArmor).isActivated(chest)
            )
        }
    }

    @SubscribeEvent
    open fun onAttack(e: AttackEntityEvent?) {
    }

    @SubscribeEvent
    open fun onDamage(e: LivingDamageEvent) {
        if (e.getSource().getTrueSource() is EntityPlayerMP) {
            val player: EntityPlayerMP? = e.getSource().getTrueSource() as EntityPlayerMP?

            val stack: ItemStack = player!!.getHeldItemMainhand()

            if (stack.getItem() is ItemPlasmaSword && stack.getTagCompound()!!
                    .getFloat(ItemPlasmaSword.heat) >= 10.0f
            ) {
                player.sendMessage(TextComponentString(EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.message.overheat")))

                e.setCanceled(true)
            }
        }

        if (e.getEntityLiving() is EntityPlayerMP) {
            val player: EntityPlayerMP = e.getEntityLiving() as EntityPlayerMP
            val world: World = player.getEntityWorld()
            if (!world.isRemote) {
                var protect: Float = 0.0f

                for (s: ItemStack in player.inventory.armorInventory) {
                    if (s.getItem() is ItemSpaceSuit && s.hasTagCompound()) {
                        if ((s.getItem() as ItemSpaceSuit).getElectricityStored(s) > 5 && s.getTagCompound()!!
                                .hasKey("protection")
                        ) protect += 0.1f
                    }
                }

                e.setAmount(e.getAmount() - e.getAmount() * protect)
            }
        }
    }

    @SubscribeEvent
    open fun onSetBlock(e: SetBlockEvent) {
        if (e.world != null && !e.world.isRemote && e.world.provider is IGalacticraftWorldProvider && e.pos != null && e.block != null) {
            val thermal: Float = (e.world.provider as IGalacticraftWorldProvider).getThermalLevelModifier()
            val bb: AxisAlignedBB = AxisAlignedBB(e.pos.up())

            for (block: BlockToChange in block_to_change) {
                if (block.only_gs_dim && e.world.provider !is IProviderFreeze) continue

                if (e.world.provider is IProviderFreeze && !(e.world.provider as IProviderFreeze).isFreeze()) continue

                if (OxygenUtil.isAABBInBreathableAirBlock(e.world, bb, true)) continue

                if (block.need_check_temp) {
                    if ((e.block === block.state || e.block.getMaterial() === block.state.getMaterial())) {
                        if (thermal <= cool_temp * block.getTempModificator()) {
                            e.world.setBlockState(e.pos, block.cold_replaced)
                            e.setCanceled(true)
                        } else if (thermal >= warn_temp * block.getTempModificator()) {
                            e.world.setBlockState(e.pos, block.hot_replaced)
                            block.spawnParticleHotTemp(e.world, e.pos)
                            e.setCanceled(true)
                        }
                    }
                } else {
                    e.world.setBlockState(e.pos, block.cold_replaced)
                    block.spawnParticleHotTemp(e.world, e.pos)
                    e.setCanceled(true)
                }
            }
        }
    }

    @SubscribeEvent
    open fun onTickBlock(e: LivingEvent.LivingUpdateEvent) {
        if (e.getEntityLiving() is EntityPlayerMP) {
            val player: EntityPlayerMP = e.getEntityLiving() as EntityPlayerMP
            val world: World = player.getEntityWorld()
            if (!world.isRemote && world.provider is IGalacticraftWorldProvider) {
                if ((world.provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()) return
                if (((world.provider as IGalacticraftWorldProvider).getCelestialBody())!!.atmosphere.isGasPresent(
                        EnumAtmosphericGas.OXYGEN
                    )
                ) return

                val worldServer: WorldServer = player.getServerWorld()

                if (!GSConfigCore.Companion.enableOxygenForPlantsAndFoods) return

                val it: MutableIterator<Chunk> =
                    worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator())
                while (it.hasNext()) {
                    val chunk: Chunk = it.next()
                    val chunkPos: ChunkPos = chunk.getPos()
                    val x: Int = chunk.x * 16
                    val z: Int = chunk.z * 16

                    chunk.enqueueRelightChecks()
                    chunk.onTick(false)

                    var pos: BlockPos = chunk.getPrecipitationHeight(
                        BlockPos(
                            x + world.rand.nextInt(16),
                            0,
                            z + world.rand.nextInt(16)
                        )
                    )

                    if (!world.isAreaLoaded(pos, 1)) return

                    val thermal: Float = (world.provider as IGalacticraftWorldProvider).getThermalLevelModifier()
                    val thermal_check: Boolean = thermal > warn_temp || thermal < cool_temp

                    var y: Int = 0
                    while (y < pos.getY()) {
                        if (world.isAirBlock(pos)) {
                            if (world.rand.nextInt(8) == 0) pos = pos.down(1)
                            y++
                            continue
                        }

                        val state: IBlockState = world.getBlockState(pos)
                        val block: Block = state.getBlock()
                        val bb: AxisAlignedBB = AxisAlignedBB(pos)

                        if (block is BlockLeaves) {
                            if (!OxygenUtil.isAABBInBreathableAirBlock(world, bb, thermal_check)) {
                                world.setBlockState(pos, GSBlocks.DRY_LEAVES.getDefaultState(), 3)
                            }
                        }

                        if (block is BlockBush && block !== Blocks.DEADBUSH) {
                            if (!OxygenUtil.isAABBInBreathableAirBlock(world, bb, thermal_check)) {
                                if (world.getBlockState(pos.down()).isBlockNormalCube()) world.setBlockState(
                                    pos,
                                    Blocks.DEADBUSH.getDefaultState(),
                                    3
                                )
                                else world.setBlockToAir(pos)
                            }
                        }

                        pos = pos.down(1)
                        y++
                    }
                }
            }
        }
    }

    /*
    //TODO: in default methods
    @SubscribeEvent
    public void onUpdateBlocks(UpdateBlockEvent e) {

        if (!e.world.isRemote) {
            if (!e.world.isAreaLoaded(e.pos, 1)) return;

            if (e.world.provider instanceof IGalacticraftWorldProvider) {
                if (((IGalacticraftWorldProvider) e.world.provider).hasBreathableAtmosphere()) return;
                if (((IGalacticraftWorldProvider) e.world.provider).getCelestialBody().atmosphere.isGasPresent(EnumAtmosphericGas.OXYGEN))
                    return;

                float thermal = ((IGalacticraftWorldProvider) e.world.provider).getThermalLevelModifier();
                boolean thermal_check = thermal > warn_temp || thermal < cool_temp;
                IBlockState state = e.block;
                Block block = state.getBlock();

                AxisAlignedBB bb = new AxisAlignedBB(e.pos);
                if (!OxygenUtil.isAABBInBreathableAirBlock(e.world, bb, thermal_check) && GSConfigCore.enableOxygenForPlantsAndFoods) {

                    if (block instanceof BlockLeaves) {
                        e.world.setBlockState(e.pos, GSBlocks.DRY_LEAVES.getDefaultState(), 3);
                        e.setCanceled(true);
                    }

                    if (block instanceof BlockBush && block != Blocks.DEADBUSH) {
                        if (e.world.getBlockState(e.pos.down()).isBlockNormalCube())
                            e.world.setBlockState(e.pos, Blocks.DEADBUSH.getDefaultState(), 3);
                        else e.world.setBlockToAir(e.pos);

                        e.setCanceled(true);
                    }
                }
            }
        }
    }

*/
    @SubscribeEvent
    open fun onFillBucket(e: FillBucketEvent) {
        val world: World = e.getWorld()
        if (e.getTarget() == null) return
        if (e.getTarget()!!.typeOfHit != RayTraceResult.Type.BLOCK) return

        val pos: BlockPos = e.getTarget()!!.getBlockPos()

        if (e.getEmptyBucket()
                .getItem() === Items.BUCKET && world.getBlockState(pos) === Blocks.ICE.getDefaultState()
        ) {
            val ice_bucket: ItemStack = ItemStack(GSItems.BASIC, 1, BasicItems.ICE_BUCKET.getMeta())
            if (!ice_bucket.hasTagCompound()) ice_bucket.setTagCompound(NBTTagCompound())
            ice_bucket.getTagCompound()!!
                .setString("current_block", world.getBlockState(pos).getBlock().getRegistryName().toString())
            world.setBlockToAir(pos)
            e.setFilledBucket(ice_bucket)
            e.setResult(Event.Result.ALLOW)
        }


        if (e.getEmptyBucket().getItem() === Items.WATER_BUCKET) {
            if (world.provider is IGalacticraftWorldProvider) {
                val thermal: Float = (world.provider as IGalacticraftWorldProvider).getThermalLevelModifier()
                val water_bucket: ItemStack = ItemStack(Items.WATER_BUCKET)

                if (!GSUtils.getThermalControl(
                        world,
                        e.getTarget()!!.getBlockPos()
                    ) && (thermal > 2.0f || thermal < -2.0f)
                ) {
                    e.setFilledBucket(water_bucket)
                    e.setResult(Event.Result.ALLOW)
                }
            }
        }
    }

    @SubscribeEvent
    open fun onInteract(event: PlayerInteractEvent.RightClickBlock) {
        //Skip events triggered from Thaumcraft Golems and other non-players
        if (event.getEntityPlayer() == null || event.getEntityPlayer().inventory == null || (event.getPos()
                .getX() == 0 && event.getPos().getY() == 0 && event.getPos().getZ() == 0)
        ) {
            return
        }

        val world: World? = event.getEntityPlayer().world
        if (world == null) {
            return
        }

        val state: IBlockState = world.getBlockState(event.getPos())
        val block: Block = state.getBlock()
        val stack: ItemStack = event.getItemStack()
        val player: EntityPlayer = event.getEntityPlayer()

        if (!world.isRemote && !block.hasTileEntity(state)) {
            if (world.provider is IGalacticraftWorldProvider) {
                val bb: AxisAlignedBB = AxisAlignedBB(event.getPos().up())
                val thermal: Float = (world.provider as IGalacticraftWorldProvider).getThermalLevelModifier()

                for (ore: ItemsToChange in items_to_change) {
                    if (stack.getItem() == ore.itemstack.getItem() && !(world.provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()) {
                        if (FluidUtil.isFilledContainer(stack) && world.getTileEntity(event.getPos()) != null) if (world.getTileEntity(
                                event.getPos()
                            ) is IFluidHandlerWrapper || world.getTileEntity(event.getPos()) is IFluidHandler
                        ) return

                        if (ore.need_check_oxygen) {
                            if (!OxygenUtil.isAABBInBreathableAirBlock(
                                    world,
                                    bb,
                                    ore.need_check_temp && (thermal > 2.0f || thermal < -2.0f)
                                )
                            ) {
                                player.sendMessage(
                                    TextComponentString(
                                        EnumColor.DARK_RED.toString() + GCCoreUtil.translate(
                                            "gui.message.needoxygen"
                                        )
                                    )
                                )
                                event.setCanceled(true)
                            }
                        }

                        if (ore.need_check_temp) {
                            if (!GSUtils.getThermalControl(
                                    world,
                                    event.getPos().up()
                                ) && (thermal > 2.0f || thermal < -2.0f)
                            ) {
                                player.sendMessage(
                                    TextComponentString(
                                        EnumColor.DARK_RED.toString() + GCCoreUtil.translate(
                                            "gui.message.needthermal"
                                        )
                                    )
                                )
                                event.setCanceled(true)
                            }
                        }

                        if (ore.replaced !== Blocks.AIR.getDefaultState()) if (world.getBlockState(event.getPos())
                                .getBlock().isReplaceable(world, event.getPos())
                        ) world.setBlockState(event.getPos(), ore.replaced)
                        else world.setBlockState(event.getPos().up(), ore.replaced)
                        break
                    }
                }
            }
        }
    }

    @SubscribeEvent
    open fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        val player: EntityPlayer? = event.getEntityPlayer()

        if (!event.getWorld().isRemote) {
            if (GalaxySpace.debug) (GalaxySpace.instance)!!.debug((EntityRegistry.getEntry(event.getTarget().javaClass))!!)

            val stack: ItemStack = event.getItemStack()

            //if(stack.isItemEqual(ItemBasicGS.BasicItems.ANIMAL_CELL.getItemStack()))
            //{
            //stack.setTagCompound(new NBTTagCompound());
            //}
            if (event.getTarget() is EntityWolf && event.getTarget() !is EntityAstroWolf) {
                val wolf: EntityWolf = event.getTarget() as EntityWolf
                if (wolf.isTamed() && event.getItemStack().getItem() === GCItems.oxMask) {
                    val astrowolf: EntityAstroWolf = EntityAstroWolf(wolf.getEntityWorld())
                    astrowolf.setOwnerId(wolf.getOwnerId())
                    astrowolf.setTamed(wolf.isTamed())
                    astrowolf.setAngry(wolf.isAngry())
                    astrowolf.setHealth(wolf.getHealth())
                    astrowolf.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0)
                    astrowolf.setSitting(wolf.isSitting())
                    astrowolf.setPositionAndRotation(
                        wolf.posX,
                        wolf.posY,
                        wolf.posZ,
                        wolf.rotationYaw,
                        wolf.rotationPitch
                    )
                    event.getTarget().setDead()
                    event.getWorld().spawnEntity(astrowolf)
                }
            }
        }
    }

    @SubscribeEvent
    open fun onInteract(event: PlayerInteractEvent) {
        val world: World? = event.getWorld()
        if (world == null) {
            return
        }

        val player: EntityPlayer = event.getEntityPlayer()
        val i: ItemStack = event.getItemStack()

        if (!world.isRemote && GalaxySpace.debug) (GalaxySpace.instance)!!.debug(
            Item.REGISTRY.getNameForObject(i.getItem())
                .toString() + " | " + i.getTranslationKey() + " | " + Biome.getBiome(
                Biome.getIdForBiome(
                    world.getBiomeForCoordsBody(
                        event.getPos()
                    )
                )
            )
        )


        if (!world.isRemote && GSConfigCore.Companion.enableOxygenForPlantsAndFoods && !player.capabilities.isCreativeMode) {
            if (world.provider is IGalacticraftWorldProvider && !(world.provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()) {
                if (!OxygenUtil.isAABBInBreathableAirBlock(player, false) && checkFood(i)) {
                    player.sendMessage(TextComponentString(EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.message.needoxygen.food")))
                    event.setCancellationResult(EnumActionResult.FAIL)
                    event.setCanceled(true)
                }
            }
        }
    }

    private fun checkFood(s: ItemStack): Boolean {
        return !s.isEmpty() && s.getItem() is ItemFood && (s.getItem() !is micdoodle8.mods.galacticraft.core.items.ItemFood) && (s.getItem() !is IItemSpaceFood)
    }

    /*
    @SubscribeEvent
    public void onPortalCreated(BlockEvent.PortalSpawnEvent e)
    {
        World world = e.getWorld();
        if(world != null) {
            if(world.provider instanceof IGalacticraftWorldProvider) {
                e.setCanceled(!((IGalacticraftWorldProvider)world.provider).netherPortalsOperational());
            }
        }
    }
    */
    @SubscribeEvent
    open fun onSuffocationEvent(e: GCCoreOxygenSuffocationEvent.Pre) {
        val living: EntityLivingBase = e.getEntityLiving()

        val list: MutableSet<EntityEntry?> = HashSet<EntityEntry?>()
        for (string in (GSConfigCore.Companion.unsuffocation_entity)!!) {
            val meta: Array<String?> = (string)!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val entity: EntityEntry? = ForgeRegistries.ENTITIES.getValue(ResourceLocation(meta[0], meta[1]))
            if (entity != null) list.add(entity)
        }

        e.setCanceled(list.contains(EntityRegistry.getEntry(living.javaClass)))
    }

    @SubscribeEvent
    open fun onEntityUpdate(event: LivingEvent.LivingUpdateEvent) {
        val living: EntityLivingBase = event.getEntityLiving()
        val world: World = living.world

        if (living is EntityPlayer) {
            val player: EntityPlayer = living
            val chest: ItemStack = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST)

            if (chest != null && chest.getItem() is IJetpackArmor && !living.onGround) {
                if ((chest.getItem() as IJetpackArmor).canFly(
                        chest,
                        living
                    ) && (chest.getItem() as IJetpackArmor).isActivated(chest) && living.posY < 256
                ) {
                    living.fallDistance = 0.0f
                    living.distanceWalkedModified = 0.6f

                    (GalaxySpace.proxy)!!.resetPlayerInAirTime(living)

                    (chest.getItem() as IJetpackArmor).decrementFuel(chest)
                }
            }

            if (!player.capabilities.isCreativeMode) {
                val held: ItemStack = player.getHeldItemMainhand()

                if (held.getItem() is ItemTier1Rocket
                    || held.getItem() is ItemTier2Rocket
                    || held.getItem() is ItemTier3Rocket
                    || held.getItem() is ItemTier4Rocket
                    || held.getItem() is ItemTier5Rocket
                    || held.getItem() is ItemTier6Rocket
                ) {
                    player.addPotionEffect(PotionEffect(MobEffects.SLOWNESS, 10, 4))
                }
            }
        }


        if (living is EntityPlayerMP) {
            val player: EntityPlayerMP = living
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            val gsstats: StatsCapability? = galaxyspace.core.handler.capabilities.StatsCapability.get(player)

            LightningStormHandler.spawnLightning(player)

            // Added code
            if (!world.isRemote) {
                MultiSeatRocketDriverTracker.get().update()
            }

            //

            //this.updateSchematics(player, stats);
            //this.throwMeteors(player);

            //if(gs_stats.getKnowledgeResearch()[0] > 0)
            //{
            //GalaxySpace.debug(gs_stats.getKnowledgeResearch()[0] + "");
            //}
            //GalaxySpace.instance.debug(GSFluids.NaturalGas.getBlock().getDefaultState().getMaterial() + "");
            if (AsmodeusConfig.enableWeatherDamage && world.getTotalWorldTime() % 15 == 0L && !getProtectArmor(player) && world.provider is WorldProviderTitan && world.isRaining()) {
                var flag: Boolean = true
                for (y: Int in 256 downTo player.getPosition().getY() + 1) {
                    if (!world.isAirBlock(BlockPos(player.getPosition().getX(), y, player.getPosition().getZ()))) {
                        flag = false
                        break
                    }
                }
                if (flag) player.attackEntityFrom(GSDamageSource.acid, 0.5f * AsmodeusConfig.toxicRainDamageRate)
            }

            if (player.world.provider is WorldProviderKuiperBelt && player.posY <= -20) {
                player.connection.setPlayerLocation(
                    player.posX,
                    188.0,
                    player.posZ,
                    player.rotationYaw,
                    player.rotationPitch
                )
            }

            if (!player.capabilities.isCreativeMode && player.getHealth() <= 5.0f) {
                for (stack: ItemStack in player.inventory.mainInventory) {
                    if (stack.getItem() === GSItems.BASIC && stack.getItemDamage() == 18) {
                        if (stack.hasTagCompound() && stack.getTagCompound()!!.getBoolean("turnonoff")) {
                            val pos: IntArray = stack.getTagCompound()!!.getIntArray("position")
                            if (pos != null && pos.size >= 3 && player.world.provider.getDimension() == pos[3]) {
                                player.sendMessage(
                                    TextComponentString(
                                        EnumColor.AQUA.toString() + GCCoreUtil.translateWithFormat(
                                            "gui.message.emergency_teleport"
                                        ) + " x:" + pos[0] + " y:" + pos[1] + " z:" + pos[2]
                                    )
                                )

                                player.connection.setPlayerLocation(
                                    pos[0].toDouble(),
                                    pos[1].toDouble(),
                                    pos[2].toDouble(),
                                    player.rotationYaw,
                                    player.rotationPitch
                                )
                                stack.shrink(1)
                            }
                        }
                        break
                    }
                }
            }


            //
            if ((stats.getShieldControllerInSlot())!!.isItemEqual(ItemStack(GSItems.BASIC, 1, 16))) {
                val shield: ItemStack = (stats.getShieldControllerInSlot())!!
                if (shield.hasTagCompound()) {
                    var shieldtime: Int = shield.getTagCompound()!!.getInteger("shieldtime")

                    if (!player.capabilities.isCreativeMode && player.world.provider is IGalacticraftWorldProvider && ((player.world.provider as IGalacticraftWorldProvider).getCelestialBody())!!.atmosphere.isCorrosive()) {
                        if (shieldtime >= 1) {
                            if (player.ticksExisted % 20 == 0) {
                                shieldtime -= 1
                                shield.getTagCompound()!!.setInteger("shieldtime", shieldtime)
                            }
                        } else {
                            shield.shrink(1)
                            player.sendMessage(
                                TextComponentString(
                                    EnumColor.DARK_RED.toString() + GCCoreUtil.translateWithFormat(
                                        "gui.message.now_noshield"
                                    )
                                )
                            )
                        }
                    }
                } else {
                    shield.setTagCompound(NBTTagCompound())
                    shield.getTagCompound()!!.setInteger("shieldtime", ItemBasicGS.SHIELD_TIME)
                }
                val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                    (stats.getShieldControllerInSlot())!!,
                    EnumExtendedInventorySlot.SHIELD_CONTROLLER
                )

                if (gearID >= 0) {
                    GCPlayerHandler.sendGearUpdatePacket(
                        player,
                        GCPlayerHandler.EnumModelPacketType.ADD,
                        EnumExtendedInventorySlot.SHIELD_CONTROLLER,
                        gearID
                    )
                }
            }


            //stats.setLastShieldControllerInSlot(stats.getShieldControllerInSlot());
            val stack: ItemStack = player.inventory.armorInventory.get(3)
            val inv: IInventoryGC? = AccessInventoryGC.getGCInventoryForPlayer(player)
            if (!stack.isEmpty()) {
                if (stack.getItem() is ItemSpaceSuit && player.isInWater() && stack.hasTagCompound() && stack.getTagCompound()!!
                        .hasKey("water_breathing")
                ) {
                    val count: Int = 300
                    val air: Int = player.getAir()
                    for (i: Int in 2..3) {
                        if (!inv!!.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i)
                                .getItemDamage() != inv.getStackInSlot(i).getMaxDamage()
                        ) {
                            if (air < count) {
                                player.setAir(count)

                                if (inv.getStackInSlot(i)
                                        .getItem() !== GCItems.oxygenCanisterInfinite
                                ) inv.getStackInSlot(i).setItemDamage(inv.getStackInSlot(i).getItemDamage() + 1)
                            }
                            break
                        }
                    }
                }
            }

            if (OxygenUtil.isAABBInBreathableAirBlock(player) || player.world.provider.getDimensionType() == DimensionType.OVERWORLD) {
                for (i: Int in 2..3) if (!inv!!.getStackInSlot(i).isEmpty() && (inv.getStackInSlot(i)
                        .getItem() === GSItems.OXYGENTANK_TIER_EPP || inv.getStackInSlot(i)
                        .hasTagCompound() && inv.getStackInSlot(i).getTagCompound()!!.hasKey("epp"))
                ) {
                    val tank: ItemStack = inv.getStackInSlot(i)
                    if (tank.getItemDamage() != 0) {
                        if (player.ticksExisted % 10 == 0) {
                            tank.setItemDamage(tank.getItemDamage() - GSConfigCore.Companion.rechargeOxygenTankTierEPP)
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    open fun onThermalArmorEvent(event: GCPlayerHandler.ThermalArmorEvent) {
        if (event.armorStack == ItemStack.EMPTY) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.REMOVE)
            return
        }
        if ((event.armorStack)!!.getItem() is ItemThermalPaddingBase && (event.armorStack)!!.getItemDamage() == event.armorIndex) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.ADD)
            return
        }

        event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.NOTHING)
    }

    @SubscribeEvent
    open fun onPlanetDecorated(event: GCCoreEventPopulate.Post) {
        if (GSConfigWorld.Companion.enableMarsNewOres && ((event.world)!!.provider is WorldProviderMars || (event.world)!!.provider is WorldProviderMars_WE)) {
            genOre(
                (event.world)!!,
                (event.pos)!!,
                WorldGenMinableMeta(GSBlocks.MARS_ORES, 4, 0, true, (MarsBlocks.marsBlock)!!, 9),
                6,
                4,
                18
            ) //diamond
            genOre(
                (event.world)!!,
                (event.pos)!!,
                WorldGenMinableMeta(GSBlocks.MARS_ORES, 6, 1, true, (MarsBlocks.marsBlock)!!, 9),
                10,
                6,
                30
            ) //gold
            genOre(
                (event.world)!!,
                (event.pos)!!,
                WorldGenMinableMeta(GSBlocks.MARS_ORES, 16, 2, true, (MarsBlocks.marsBlock)!!, 9),
                15,
                6,
                70
            ) //coal
            genOre(
                (event.world)!!,
                (event.pos)!!,
                WorldGenMinableMeta(GSBlocks.MARS_ORES, 10, 3, true, (MarsBlocks.marsBlock)!!, 9),
                10,
                6,
                20
            ) //redstone
            genOre(
                (event.world)!!,
                (event.pos)!!,
                WorldGenMinableMeta(GSBlocks.MARS_ORES, 8, 4, true, (MarsBlocks.marsBlock)!!, 9),
                4,
                6,
                20
            ) //silicon
            genOre(
                (event.world)!!,
                (event.pos)!!,
                WorldGenMinableMeta(GSBlocks.MARS_ORES, 6, 5, true, (MarsBlocks.marsBlock)!!, 9),
                16,
                6,
                45
            ) //aluminum
        }

        if ((event.world)!!.provider is IGalacticraftWorldProvider) {
            for (lines in (GSConfigWorld.Companion.OREGEN_SETTINGS)!!) {
                val line: Array<String?> = (lines)!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                val dimId: Int = line[0]!!.toInt()
                val blockModId: String? = line[1]
                val blockName: String? = line[2]
                val blockMeta: Int = line[3]!!.toInt()

                val replaceBlockModId: String? = line[4]
                val replaceBlockName: String? = line[5]
                val replaceBlockMeta: Int = line[6]!!.toInt()

                val veinSize: Int = line[7]!!.toInt()
                val amountPerChunk: Int = line[8]!!.toInt()
                val minY: Int = line[9]!!.toInt()
                val maxY: Int = line[10]!!.toInt()

                val block: Block? = ForgeRegistries.BLOCKS.getValue(ResourceLocation(blockModId + ":" + blockName))
                val replaceBlock: Block? =
                    ForgeRegistries.BLOCKS.getValue(ResourceLocation(replaceBlockModId + ":" + replaceBlockName))

                if ((event.world)!!.provider.getDimension() == dimId) {
                    genOre(
                        (event.world)!!,
                        (event.pos)!!,
                        WorldGenMinableMeta((block)!!, veinSize, blockMeta, true, (replaceBlock)!!, replaceBlockMeta),
                        amountPerChunk,
                        minY,
                        maxY
                    )
                }
            }
        }
    }

    open fun genOre(world: World, pos: BlockPos, wg: WorldGenerator, amountPerChunk: Int, minY: Int, maxY: Int) {
        val pos1: BlockPos =
            pos.add(world.rand.nextInt(16), world.rand.nextInt(maxY - minY) + minY, world.rand.nextInt(16))

        for (i: Int in 0..<amountPerChunk) wg.generate(world, world.rand, pos1)
    }

    @SubscribeEvent
    open fun onZeroGravity(event: ZeroGravityEvent.InFreefall) {
        val entity: EntityLivingBase = event.getEntityLiving()
        if (inGravityZone(entity.getEntityWorld(), entity, false)) event.setCanceled(true)
    }

    @SubscribeEvent
    open fun onRadiation(event: RadiationEvent) {
        val living: EntityLivingBase? = event.getEntity() as EntityLivingBase?

        if (living is EntityPlayerMP) {
            val player: EntityPlayerMP = living


            var checkAirLock: Boolean = false
            for (y: Int in 0..254) {
                if (player.world.getBlockState(player.getPosition().up(y)).getBlock() === GCBlocks.airLockSeal) {
                    checkAirLock = true
                    break
                }
            }
            if (!player.capabilities.isCreativeMode) event.setCanceled(
                checkAirLock
                        || !GSConfigCore.Companion.enableRadiationSystem || getProtectArmor(player)
                        || player.getRidingEntity() is EntityLanderBase
                        || player.getRidingEntity() is EntityTieredRocket
                        || inRadiationBubble(player.getEntityWorld(), player.posX, player.posY, player.posZ)
                        || !(GSConfigCore.Companion.enableRadiationEffectForAndroid && CompatibilityManager.isAndroid(
                    player
                ))
            )
        }
    }

    @SubscribeEvent
    open fun onPressure(event: PressureEvent) {
        val living: EntityLivingBase? = event.getEntity() as EntityLivingBase?

        if (living is EntityPlayerMP) {
            val player: EntityPlayerMP = living
            val world: World = player.getEntityWorld()
            val level: Float = event.getPressureLevel().toFloat()

            if (!player.capabilities.isCreativeMode) event.setCanceled(
                !GSConfigCore.Companion.enablePressureSystem || getProtectArmor(player)
                        || inGravityZone(world, player, true)
                        || player.getRidingEntity() is EntityLanderBase
                        || player.getRidingEntity() is EntityTieredRocket
                        || !(GSConfigCore.Companion.enablePressureEffectForAndroid && CompatibilityManager.isAndroid(
                    player
                ))
            )
        }
    }

    protected open fun updateSchematics(player: EntityPlayerMP?, playerStats: GCPlayerStats?) {
        /* SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(GSConfigSchematics.idSchematicCone));
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(GSConfigSchematics.idSchematicBody));
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(GSConfigSchematics.idSchematicEngine));
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(GSConfigSchematics.idSchematicBooster));
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(GSConfigSchematics.idSchematicFins));
        */
        /*Collections.sort(playerStats.unlockedSchematics);

        if (player.playerNetServerHandler != null && (playerStats.unlockedSchematics.size() != playerStats.lastUnlockedSchematics.size() || (player.ticksExisted - 1) % 100 == 0))
        {
            Integer[] iArray = new Integer[playerStats.unlockedSchematics.size()];

            for (int i = 0; i < iArray.length; i++)
            {
                ISchematicPage page = playerStats.unlockedSchematics.get(i);
                iArray[i] = page == null ? -2 : page.getPageID();
            }

            List<Object> objList = new ArrayList<Object>();
            objList.add(iArray);

            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_UPDATE_SCHEMATIC_LIST, objList), player);
        }*/
    }

    protected open fun throwMeteors(player: EntityPlayerMP) {
        val world: World = player.world
        if (world.provider is IAdvancedSpace && !world.isRemote) {
            if ((world.provider as IGalacticraftWorldProvider).getMeteorFrequency() > 0 && ConfigManagerCore.meteorSpawnMod > 0.0) {
                // final int f = (int) (((IGalacticraftWorldProvider) world.provider).getMeteorFrequency() * 1000D * (1.0 / ConfigManagerCore.meteorSpawnMod));
                val f: Int = 1
                if (world.rand.nextInt(f) == 0)  /*&& world.getWorldTime() > 6000L && world.getWorldTime() < 9000L)*/ {
                    val closestPlayer: EntityPlayer? = world.getClosestPlayerToEntity(player, 100.0)

                    if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                        val x: Int
                        val y: Int
                        val z: Int
                        val motX: Double
                        val motZ: Double
                        x = world.rand.nextInt(1) - 4
                        y = world.rand.nextInt(20) + 200
                        z = world.rand.nextInt(1) - 4
                        motX = world.rand.nextDouble() * 1
                        motZ = world.rand.nextDouble() * 1

                        val meteor: EntityMeteor = EntityMeteor(
                            world,
                            player.posX + x,
                            player.posY + y,
                            player.posZ + z,
                            motX - 0.5,
                            0.0,
                            motZ - 0.5,
                            1
                        )

                        if (!world.isRemote) {
                            world.spawnEntity(meteor)
                        }
                    }
                }
            }
        }
    }

    private open class BlockToChange constructor(
        @field:kotlin.jvm.JvmField  val state: IBlockState,
        @field:kotlin.jvm.JvmField  val hot_replaced: IBlockState,
        @field:kotlin.jvm.JvmField  val cold_replaced: IBlockState,
        private val temp: Float,
        @field:kotlin.jvm.JvmField  val need_check_temp: Boolean
    ) {
        private var need_check_oxygen = false
        @field:kotlin.jvm.JvmField  var only_gs_dim = false
        private var particle_name = ""

        open fun setParticle(s: String): BlockToChange {
            this.particle_name = s
            return this
        }

        open fun setOnlyGSDim(): BlockToChange {
            this.only_gs_dim = true
            return this
        }

        open fun getTempModificator(): Float {
            return this.temp + 1.0f
        }

        open fun setOxygenCheck(check: Boolean): BlockToChange {
            this.need_check_oxygen = check
            return this
        }

        open fun spawnParticleHotTemp(world: World, pos: BlockPos) {
            if (!particle_name.isEmpty()) for (i: Int in 0..4) (GalaxySpace.proxy)!!.spawnParticle(
                particle_name,
                Vector3(
                    pos.getX() + world.rand.nextDouble(),
                    pos.getY() + 1.0 + world.rand.nextDouble(),
                    pos.getZ() + world.rand.nextDouble()
                ),
                Vector3(0.0, 0.001, 0.0),
                (arrayOf<Any>(10, 5, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
            )
        }
    }

    private open class ItemsToChange constructor(stack: ItemStack, placed: IBlockState) {
        @field:kotlin.jvm.JvmField  var itemstack: ItemStack = ItemStack.EMPTY
        @field:kotlin.jvm.JvmField  val replaced: IBlockState
        @field:kotlin.jvm.JvmField  var need_check_temp = false
        @field:kotlin.jvm.JvmField  var need_check_oxygen = false
        private var only_gs_dim = false

        init {
            this.itemstack = stack
            this.replaced = placed
        }

        open fun setOnlyGSDim(): ItemsToChange {
            this.only_gs_dim = true
            return this
        }

        open fun setOxygenCheck(check: Boolean): ItemsToChange {
            this.need_check_oxygen = check
            return this
        }

        open fun setTempCheck(check: Boolean): ItemsToChange {
            this.need_check_temp = check
            return this
        }
    }

    companion object {
        private val block_to_change: MutableList<BlockToChange> = ArrayList<BlockToChange>()
        private val items_to_change: MutableList<ItemsToChange> = ArrayList<ItemsToChange>()

        private const val hot_temp = 4f // > 120C
        private const val warn_temp = 2f // > 60C
        private val cool_temp = -1.2f // < -36
        private val cold_temp = -2f // < -60

        init {
            items_to_change.add(
                ItemsToChange(ItemStack(Blocks.FURNACE), Blocks.AIR.getDefaultState()).setOxygenCheck(
                    true
                )
            )
            //block_to_change.add(new BlockToChange(Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, 0), Blocks.AIR.getDefaultState(), Blocks.ICE.getDefaultState(), 0.0F, true).setParticle("waterbubbles").setOxygenCheck(false));
            block_to_change.add(
                BlockToChange(
                    (GSFluids.BLOCK_LEMETHANE)!!.getDefaultState().withProperty(BlockLiquid.LEVEL, 0),
                    Blocks.FIRE.getDefaultState(),
                    Blocks.AIR.getDefaultState(),
                    3.0f,
                    true
                ).setParticle("waterbubbles").setOxygenCheck(false)
            )
            block_to_change.add(
                BlockToChange(
                    (GSFluids.BLOCK_HELIUM_HYDROGEN)!!.getDefaultState().withProperty(BlockLiquid.LEVEL, 0),
                    Blocks.FIRE.getDefaultState(),
                    Blocks.AIR.getDefaultState(),
                    3.0f,
                    true
                ).setParticle("waterbubbles").setOxygenCheck(false)
            )
        }

        @JvmStatic
        fun consumeOxygenFromTank(player: EntityPlayerMP, consume_count: Int) {
            val inv: IInventoryGC? = AccessInventoryGC.getGCInventoryForPlayer(player)
            for (i: Int in 2..3) if (!inv!!.getStackInSlot(i).isEmpty()) {
                val tank: ItemStack = inv.getStackInSlot(i)
                if (tank.getItemDamage() != tank.getMaxDamage()) {
                    if (player.ticksExisted % 10 == 0) {
                        tank.setItemDamage(tank.getItemDamage() + consume_count)
                    }
                    break
                }
            }
        }

        @JvmStatic
        fun isValidOxygenTanks(player: EntityPlayerMP?): Boolean {
            var valid: Boolean = false
            val inv: IInventoryGC? = AccessInventoryGC.getGCInventoryForPlayer(player)
            for (i: Int in 2..3) if (!inv!!.getStackInSlot(i).isEmpty()) {
                val tank: ItemStack = inv.getStackInSlot(i)
                if (tank.getItemDamage() != tank.getMaxDamage()) {
                    valid = true
                    break
                }
            }

            return valid
        }

        @JvmStatic
        fun getProtectArmor(player: EntityPlayerMP): Boolean {
            val check: BooleanArray = BooleanArray(4)

            //boolean flag = false;
            for (string in (GSConfigCore.Companion.radiation_armor)!!) {
                val meta: Array<String?> = (string)!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val item: Item? = ForgeRegistries.ITEMS.getValue(ResourceLocation(string))

                if (item == null) continue

                if (meta.size > 2) {
                    for (i: Int in player.inventory.armorInventory.indices) {
                        val itemstack: ItemStack = ItemStack(item, 1, meta[2]!!.toInt())
                        check[i] = !player.inventory.armorInventory.get(i)
                            .isEmpty() && player.inventory.armorInventory.get(i) == itemstack
                    }
                } else for (i: Int in 0..3) {
                    check[i] =
                        !player.inventory.armorInventory.get(i).isEmpty() && player.inventory.armorInventory.get(i)
                            .getItem() === item
                }


                if (check[0] && check[1] && check[2] && check[3]) break
            }

            return check[0] && check[1] && check[2] && check[3]
        }


        @JvmStatic
        fun inGravityZone(world: World, player: EntityLivingBase, checkStabilisationModule: Boolean): Boolean {
            for (blockVec: BlockVec3Dim? in TileEntityGravitationModule.loadedTiles) {
                if (blockVec != null && blockVec.dim == world.provider.getDimension()) {
                    val tile: TileEntity? = world.getTileEntity(blockVec.toBlockPos())

                    if (tile is TileEntityGravitationModule) {
                        val gravity: TileEntityGravitationModule = tile

                        if (!gravity.disabled && gravity.hasEnoughEnergyToRun && gravity.inGravityZone(world, player)) {
                            if (checkStabilisationModule) {
                                for (i: Int in 0..3) if (gravity.getStackInSlot(i + 1)
                                        .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 1))
                                ) return true

                                return false
                            }
                            return true
                        }
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun inRadiationBubble(world: World, avgX: Double, avgY: Double, avgZ: Double): Boolean {
            for (blockVec: BlockVec3Dim? in TileEntityRadiationStabiliser.loadedTiles) {
                if (blockVec != null && blockVec.dim == world.provider.getDimension()) {
                    val tile: TileEntity? = world.getTileEntity(blockVec.toBlockPos())
                    if (tile is TileEntityRadiationStabiliser) {
                        if (tile.inBubble(avgX, avgY, avgZ)) return true
                    }
                }
            }

            return false
        }

        @JvmStatic
        fun inShieldBubble(world: World, avgX: Double, avgY: Double, avgZ: Double): Boolean {
            for (blockVec: BlockVec3Dim? in TileEntityPlanetShield.loadedTiles) {
                if (blockVec != null && blockVec.dim == world.provider.getDimension()) {
                    val tile: TileEntity? = world.getTileEntity(blockVec.toBlockPos())
                    if (tile is TileEntityPlanetShield) {
                        if (tile.inBubble(avgX, avgY, avgZ)) return true
                    }
                }
            }

            return false
        }

        @JvmStatic
        fun consumeItemStack(inventory: IInventory, stack: ItemStack): Boolean {
            //GalaxySpace.debug(getAmount(inventory, stack) + "");
            if (getAmount(inventory, stack) >= stack.getCount()) {
                for (i: Int in 0..<inventory.getSizeInventory()) {
                    if (isItemStackEqual(inventory.getStackInSlot(i), stack)) {
                        val amount: Int = min(stack.getCount(), inventory.getStackInSlot(i).getCount())
                        if (amount > 0) {
                            inventory.getStackInSlot(i).shrink(amount)
                            if (inventory.getStackInSlot(i).getCount() <= 0) {
                                inventory.setInventorySlotContents(i, ItemStack.EMPTY)
                            }
                            stack.shrink(amount)
                        }
                        if (stack.getCount() <= 0) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun getAmount(inventory: IInventory, stack: ItemStack): Int {
            var amount: Int = 0
            for (i: Int in 0..<inventory.getSizeInventory()) {
                if (isItemStackEqual(inventory.getStackInSlot(i), stack)) {
                    amount += inventory.getStackInSlot(i).getCount()
                }
            }
            return amount
        }

        @JvmStatic
        fun isItemStackEqual(stack1: ItemStack, stack2: ItemStack): Boolean {
            return (!stack1.isEmpty() && stack1.getItem() === stack2.getItem() && (stack1.getItemDamage() == stack2.getItemDamage() || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE))
        }

        @JvmStatic
        fun enableFlight(player: EntityPlayer, state: Boolean) {
            val chest: ItemStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)
            if (chest != null && chest.getItem() is IJetpackArmor) {
                (chest.getItem() as IJetpackArmor).switchState(chest, state)
                /*if (((IJetpack) chest.getItem()).isActivated(chest)) {
                player.worldObj.playSoundAtEntity(player, "jetpack:launch", 1, 1);
                JetCore.network.sendToAllAround(new SoundMessage(Item.getIdFromItem(chest.getItem()), player.getCommandSenderName()), new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64));
            }*/
            }
        }
    }
}
