package micdoodle8.mods.galacticraft.core.event

import net.minecraft.network.chat.Component

import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.api.recipe.SchematicEvent.FlipPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicEvent.Unlock
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraft.world.item.Item
import net.minecraft.item.ItemBucket
import net.minecraft.item.ItemFireball
import net.minecraft.item.ItemFlintAndSteel
import net.minecraft.world.item.ItemStack
import net.minecraftforge.event.world.ChunkEvent.Load
import net.minecraftforge.event.world.WorldEvent.Save
import net.minecraftforge.fml.common.eventhandler.Event.Result

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent
import micdoodle8.mods.galacticraft.api.item.IKeyItem
import micdoodle8.mods.galacticraft.api.item.IKeyable
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicEvent
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.TransformerHooks
import micdoodle8.mods.galacticraft.core.client.SkyProviderOverworld
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiPositionedContainer
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.*
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockGravel
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockSand
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.effect.MobEffects
import net.minecraft.item.*
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.phys.Vec3 as Vec3d

import net.minecraft.world.level.Level as World
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.biome.BiomeDesert
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingFallEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.living.ZombieEvent
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.event.entity.player.PlayerDropsEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.terraingen.PopulateChunkEvent
import net.minecraftforge.event.terraingen.TerrainGen
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class EventHandlerGC {
    @SubscribeEvent
    open fun playerJoinWorld(event: EntityJoinWorldEvent) {
        TickHandlerServer.markWorldNeedsUpdate(GCCoreUtil.getDimensionID(event.getWorld()))
    }

    @SubscribeEvent
    open fun onRocketLaunch(event: EntitySpaceshipBase.RocketLaunchEvent?) {
//        if (!event.getEntity().world.isRemote && event.getEntity().world.provider.dimensionId == 0)
//        {
//            if (event.rocket.riddenByEntity instanceof EntityPlayerMP)
//            {
//                TickHandlerServer.playersRequestingMapData.add((EntityPlayerMP) event.rocket.riddenByEntity);
//            }
//        }
    }

    @SubscribeEvent
    open fun onConfigChanged(event: ConfigChangedEvent) {
        if (event.getModID() == Constants.MOD_ID_CORE) {
            ConfigManagerCore.syncConfig(false)
        }
    }

    @SubscribeEvent
    open fun onWorldSave(event: WorldEvent.Save) {
        ChunkLoadingCallback.save(event.getWorld() as WorldServer?)
    }

    @SubscribeEvent
    open fun onChunkDataLoad(event: ChunkDataEvent.Load) {
        ChunkLoadingCallback.load(event.getWorld() as WorldServer?)
    }

    @SubscribeEvent
    open fun onWorldLoad(event: ChunkEvent.Load) {
        if (!event.getWorld().isRemote) {
            ChunkLoadingCallback.load(event.getWorld() as WorldServer?)
        }
    }

    @SubscribeEvent
    open fun onEntityDamaged(event: LivingHurtEvent) {
        if (event.getSource().damageType == DamageSource.ON_FIRE.damageType) {
            if (OxygenUtil.noAtmosphericCombustion(event.getEntityLiving().world.provider)) {
                if (OxygenUtil.isAABBInBreathableAirBlock(
                        event.getEntityLiving().world,
                        event.getEntityLiving().getEntityBoundingBox()
                    )
                ) {
                    return
                }

                if (event.getEntityLiving().world is WorldServer) {
                    (event.getEntityLiving().world as WorldServer).spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        event.getEntityLiving().posX,
                        event.getEntityLiving().posY + event.getEntityLiving()
                            .getEntityBoundingBox().maxY - event.getEntityLiving().getEntityBoundingBox().minY,
                        event.getEntityLiving().posZ,
                        50,
                        0.0,
                        0.05,
                        0.0,
                        0.001
                    )
                }

                event.getEntityLiving().extinguish()
            }
        }
    }

    @SubscribeEvent
    open fun onEntityFall(event: LivingFallEvent) {
        if (event.getEntityLiving() is EntityPlayer) {
            val player: EntityPlayer = event.getEntityLiving() as EntityPlayer
            if (player.getVehicle() is EntityAutoRocket || player.getVehicle() is EntityLanderBase) {
                event.setDistance(0.0f)
                event.setCanceled(true)
                return
            }
        }

        if (event.getEntityLiving().world.provider is IGalacticraftWorldProvider) {
            event.setDistance(event.getDistance() * (event.getEntityLiving().world.provider as IGalacticraftWorldProvider).getFallDamageModifier())
        }
    }

    @SubscribeEvent
    open fun blockBreakSpeed(event: PlayerEvent.BreakSpeed) {
        val p: EntityPlayer = event.getEntityPlayer()
        if (!p.onGround && p.level().provider is IZeroGDimension && !ConfigManagerCore.hardMode && event.getOriginalSpeed() < 5.0f) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0f)
        }
    }

    @SubscribeEvent
    open fun onPlayerLeftClickedBlock(event: PlayerInteractEvent.LeftClickBlock) {
        //Skip events triggered from Thaumcraft Golems and other non-players
        if (event.getEntityPlayer() == null || event.getEntityPlayer().inventory == null || event.getPos() == null || (event.getPos()
                .getX() == 0 && event.getPos().getY() == 0 && event.getPos().getZ() == 0)
        ) {
            return
        }

        val worldObj: World? = event.getEntityPlayer().world
        if (worldObj == null) {
            return
        }

        val heldStack: ItemStack = event.getEntityPlayer().inventory.getCurrentItem()
        val tileClicked: TileEntity? = worldObj.getBlockEntity(event.getPos())

        if (!heldStack.isEmpty()) {
            if (tileClicked != null && tileClicked is IKeyable) {
                event.setCanceled(!(tileClicked as IKeyable).canBreak() && !event.getEntityPlayer().capabilities.isCreativeMode)
            }
        } else if (tileClicked != null && tileClicked is IKeyable) {
            event.setCanceled(!(tileClicked as IKeyable).canBreak() && !event.getEntityPlayer().capabilities.isCreativeMode)
        }
    }

    @SubscribeEvent
    open fun onPlayerRightClickedBlock(event: PlayerInteractEvent.RightClickBlock) {
        //Skip events triggered from Thaumcraft Golems and other non-players
        if (event.getEntityPlayer() == null || event.getEntityPlayer().inventory == null || event.getPos() == null || (event.getPos()
                .getX() == 0 && event.getPos().getY() == 0 && event.getPos().getZ() == 0)
        ) {
            return
        }

        val worldObj: World? = event.getEntityPlayer().world
        if (worldObj == null) {
            return
        }

        val idClicked: Block = worldObj.getBlockState(event.getPos()).getBlock()

        if (idClicked === Blocks.BED && worldObj.provider is IGalacticraftWorldProvider && !worldObj.isClientSide && !(worldObj.provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()) {
            if (GalacticraftCore.isPlanetsLoaded) {
                val stats: GCPlayerStats = (GCPlayerStats.get(event.getEntityPlayer()))!!
                if (!stats.hasReceivedBedWarning()) {
                    event.getEntityPlayer()
                        .sendMessage(Component.literal(GCCoreUtil.translate("gui.bed_fail.message")))
                    stats.setReceivedBedWarning(true)
                }
            }

            if (worldObj.provider is WorldProviderSpaceStation) {
                //On space stations simply block the bed activation => no explosion
                event.setCanceled(true)
                return
            }


            //Optionally prevent beds from exploding - depends on canRespawnHere() in the WorldProvider interacting with this
            bedActivated = true
            if (worldObj.provider.canRespawnHere() && !bedActivated) {
                bedActivated = true

                //On planets allow the bed to be used to designate a player spawn point
                event.getEntityPlayer()
                    .setSpawnChunk(event.getPos(), false, GCCoreUtil.getDimensionID(event.getWorld()))
            } else {
                bedActivated = false
            }
        }

        val heldStack: ItemStack = event.getEntityPlayer().inventory.getCurrentItem()
        val tileClicked: TileEntity? = worldObj.getBlockEntity(event.getPos())

        if (!heldStack.isEmpty()) {
            if (tileClicked != null && tileClicked is IKeyable) {
                if (heldStack.getItem() is IKeyItem) {
                    if ((heldStack.getItem() as IKeyItem).getTier(heldStack) == -1 || (tileClicked as IKeyable).getTierOfKeyRequired() == -1 || (heldStack.getItem() as IKeyItem).getTier(
                            heldStack
                        ) == (tileClicked as IKeyable).getTierOfKeyRequired()
                    ) {
                        event.setCanceled(
                            (tileClicked as IKeyable).onValidKeyActivated(
                                event.getEntityPlayer(),
                                heldStack,
                                event.getFace()
                            )
                        )
                    } else if (!event.getEntityPlayer().isSneaking()) {
                        event.setCanceled(
                            (tileClicked as IKeyable).onActivatedWithoutKey(
                                event.getEntityPlayer(),
                                event.getFace()
                            )
                        )
                    }
                } else {
                    event.setCanceled(
                        (tileClicked as IKeyable).onActivatedWithoutKey(
                            event.getEntityPlayer(),
                            event.getFace()
                        )
                    )
                }
            }

            if (heldStack.getItem() is ItemFlintAndSteel || heldStack.getItem() is ItemFireball) {
                if (!worldObj.isClientSide) {
                    if (idClicked !== Blocks.TNT && OxygenUtil.noAtmosphericCombustion(event.getEntityPlayer().world.provider) && !OxygenUtil.isAABBInBreathableAirBlock(
                            event.getEntityLiving().world,
                            AxisAlignedBB(
                                event.getPos().getX().toDouble(),
                                event.getPos().getY().toDouble(),
                                event.getPos().getZ().toDouble(),
                                (event.getPos().getX() + 1).toDouble(),
                                (event.getPos().getY() + 2).toDouble(),
                                (event.getPos().getZ() + 1).toDouble()
                            )
                        )
                    ) {
                        event.setCanceled(true)
                    }
                }
            }
        } else if (tileClicked != null && tileClicked is IKeyable) {
            event.setCanceled((tileClicked as IKeyable).onActivatedWithoutKey(event.getEntityPlayer(), event.getFace()))
        }
    }

    @SubscribeEvent
    open fun entityLivingEvent(event: LivingEvent.LivingUpdateEvent) {
        val entityLiving: EntityLivingBase = event.getEntityLiving()
        if (entityLiving is EntityPlayerMP) {
            (GalacticraftCore.handler)!!.onPlayerUpdate(entityLiving)
            if (GalacticraftCore.isPlanetsLoaded) {
                (AsteroidsModule.playerHandler)!!.onPlayerUpdate(entityLiving)
            }
            return
        }

        if (entityLiving.tickCount % ConfigManagerCore.suffocationCooldown == 0) {
            if (entityLiving.level().provider is IGalacticraftWorldProvider) {
                if ((entityLiving !is EntityPlayer) && (entityLiving !is IEntityBreathable || !(entityLiving as IEntityBreathable).canBreath()) && !(entityLiving.level().provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()) {
                    if (!OxygenUtil.isAABBInBreathableAirBlock(entityLiving)) {
                        val suffocationEvent: GCCoreOxygenSuffocationEvent =
                            GCCoreOxygenSuffocationEvent.Pre(entityLiving)
                        MinecraftForge.EVENT_BUS.post(suffocationEvent)

                        if (suffocationEvent.isCanceled()) {
                            return
                        }

                        foundation.damage.FoundationDamageTypes.hurt(entityLiving,
                            DamageSourceGC.oxygenSuffocation,
                            max(ConfigManagerCore.suffocationDamage / 2, 1).toFloat()
                        )

                        val suffocationEventPost: GCCoreOxygenSuffocationEvent =
                            GCCoreOxygenSuffocationEvent.Post(entityLiving)
                        MinecraftForge.EVENT_BUS.post(suffocationEventPost)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    open fun entityUpdateCancelInFreefall(event: EntityEvent.CanUpdate) {
        if (event.getEntity() is EntityMeteor) {
            event.setCanUpdate(true)
            return
        }

        if (event.getEntity().world.provider is IZeroGDimension) {
            if ((event.getEntity().world.provider as IZeroGDimension).inFreefall(event.getEntity())) {
                event.setCanUpdate(true)
                //                event.entity.move(event.entity.motionX, event.entity.motionY, event.entity.motionZ);
            }
        }
    }

    private fun fillBucket(world: World, position: RayTraceResult): ItemStack? {
        val state: IBlockState = world.getBlockState(position.getBlockPos())
        val block: Block = state.getBlock()

        val bucket: Item? = bucketList.get(block)

        if (bucket != null && block.getMetaFromState(state) == 0) {
            world.setBlockAndUpdate(position.getBlockPos(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
            return ItemStack(bucket)
        }

        return null
    }

    @SubscribeEvent
    open fun onBucketFill(event: FillBucketEvent) {
        if (event.getEmptyBucket() == null || event.getEmptyBucket().getItem() !is ItemBucket) {
            return
        }
        val pos: RayTraceResult? = event.getTarget()
        if (pos == null) {
            return
        }

        val ret: ItemStack? = fillBucket(event.getWorld(), pos)

        if (ret == null)  //Intentional null check - see fillBucket()
        {
            return
        }

        event.setFilledBucket(ret)
        event.setResult(Event.Result.ALLOW)
    }

    @SubscribeEvent
    open fun populate(event: PopulateChunkEvent.Post) {
        val doGen: Boolean = TerrainGen.populate(
            event.getGenerator(),
            event.getWorld(),
            event.getRand(),
            event.getChunkX(),
            event.getChunkZ(),
            event.isHasVillageGenerated(),
            PopulateChunkEvent.Populate.EventType.CUSTOM
        )

        if (!doGen) {
            return
        }

        val worldX: Int = event.getChunkX() shl 4
        val worldZ: Int = event.getChunkZ() shl 4

        generateOil(event.getWorld(), event.getRand(), worldX + 15, worldZ + 15, false)
    }

    @SubscribeEvent
    open fun schematicUnlocked(event: SchematicEvent.Unlock) {
        val stats: GCPlayerStats = (GCPlayerStats.get((event.player)!!))!!

        if (!(stats.getUnlockedSchematics())!!.contains(event.page)) {
            (stats.getUnlockedSchematics())!!.add((event.page)!!)
            Collections.sort<ISchematicPage>((stats.getUnlockedSchematics())!!)

            if (event.player != null && ((event.player)!!.connection != null)!!) {
                val iArray: Array<Int> = (arrayOfNulls<Int>((stats.getUnlockedSchematics())!!.size) as Array<Int>)

                for (i: Int in iArray.indices) {
                    val page: ISchematicPage? = (stats.getUnlockedSchematics())!!.get(i)
                    iArray[i] = if (page == null) -2 else page.getPageID()
                }

                val objList: MutableList<Any?> = ArrayList<Any?>()
                objList.add(iArray)

                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_UPDATE_SCHEMATIC_LIST,
                        GCCoreUtil.getDimensionID(((event.player)!!.level())!!),
                        objList
                    ), event.player
                )
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun schematicFlipEvent(event: SchematicEvent.FlipPage) {
        var page: ISchematicPage? = null

        when (event.direction) {
            1 -> page = getNextSchematic(event.index)
            -1 -> page = getLastSchematic(event.index)
        }

        if (page != null) {
            val cs: GuiScreen? = event.currentGui
            var benchX: Int = Minecraft.getInstance().player.x.toInt()
            var benchY: Int = Minecraft.getInstance().player.y.toInt()
            var benchZ: Int = Minecraft.getInstance().player.z.toInt()
            if (cs is GuiPositionedContainer) {
                benchX = cs.getX()
                benchY = cs.getY()
                benchZ = cs.getZ()
            }
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_OPEN_SCHEMATIC_PAGE,
                    GCCoreUtil.getDimensionID(Minecraft.getInstance().level),
                    arrayOf<Any>(page.getPageID(), benchX, benchY, benchZ)
                )
            )
            Minecraft.getInstance().player.openGui(
                GalacticraftCore.instance,
                page.getGuiID(),
                Minecraft.getInstance().player.level(),
                benchX,
                benchY,
                benchZ
            )
        }
    }

    @SubscribeEvent
    open fun onPlayerDeath(event: PlayerDropsEvent) {
        if (event.getEntityLiving() is EntityPlayerMP) {
            val stats: GCPlayerStats = (GCPlayerStats.get(event.getEntityPlayer()))!!
            if (!event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory")) {
                event.getEntityPlayer().captureDrops = true
                for (i: Int in (stats.getExtendedInventory())!!.getSizeInventory() - 1 downTo 0) {
                    val stack: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(i)

                    if (!stack.isEmpty()) {
                        event.getEntityPlayer().dropItem(stack, true, false)
                        (stats.getExtendedInventory())!!.setInventorySlotContents(i, ItemStack.EMPTY)
                    }
                }
                event.getEntityLiving().captureDrops = false
            }
        }
    }

    // @OnlyIn(Dist.CLIENT)
    // @SubscribeEvent
    // public void onMinecraftLoaded(MinecraftLoadedEvent event)
    // {
    // ;
    // }
    //	@OnlyIn(Dist.CLIENT)
    //	@SubscribeEvent
    //	public void onSoundLoad(SoundLoadEvent event)
    //	{
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/scaryscape.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip1.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip2.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip3.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip4.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip5.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip6.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip7.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "ambience/singledrip8.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "entity/bossdeath.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "entity/bosslaugh.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "entity/bossliving.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "entity/slime_death.ogg");
    //		ClientProxyCore.newMusic.add(this.func_110654_c(event.manager.soundPoolMusic, Constants.TEXTURE_PREFIX + "music/mars_JC.ogg"));
    //		ClientProxyCore.newMusic.add(this.func_110654_c(event.manager.soundPoolMusic, Constants.TEXTURE_PREFIX + "music/mimas_JC.ogg"));
    //		ClientProxyCore.newMusic.add(this.func_110654_c(event.manager.soundPoolMusic, Constants.TEXTURE_PREFIX + "music/orbit_JC.ogg"));
    //		ClientProxyCore.newMusic.add(this.func_110654_c(event.manager.soundPoolMusic, Constants.TEXTURE_PREFIX + "music/scary_ambience.ogg"));
    //		ClientProxyCore.newMusic.add(this.func_110654_c(event.manager.soundPoolMusic, Constants.TEXTURE_PREFIX + "music/spacerace_JC.ogg"));
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "player/closeairlock.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "player/openairlock.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "player/parachute.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "player/unlockchest.ogg");
    //		event.manager.addSound(Constants.TEXTURE_PREFIX + "shuttle/shuttle.ogg");
    //	}
    //
    //	@OnlyIn(Dist.CLIENT)
    //	private SoundPoolEntry func_110654_c(SoundPool pool, String par1Str)
    //	{
    //		try
    //		{
    //			ResourceLocation resourcelocation = new ResourceLocation(par1Str);
    //			String s1 = String.format("%s:%s:%s/%s", new Object[] { "mcsounddomain", resourcelocation.getResourceDomain(), "sound", resourcelocation.getResourcePath() });
    //			SoundPoolProtocolHandler soundpoolprotocolhandler = new SoundPoolProtocolHandler(pool);
    //			return new SoundPoolEntry(par1Str, new URL((URL) null, s1, soundpoolprotocolhandler));
    //		}
    //		catch (MalformedURLException e)
    //		{
    //			e.printStackTrace();
    //		}
    //
    //		return null;
    //	} TODO Fix sounds
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onLeaveBedButtonClicked(event: SleepCancelledEvent?) {
        val player: EntityPlayer = Minecraft.getInstance().player

        val c: BlockPos? = player.bedLocation

        if (c != null) {
            val event0: EventWakePlayer = EventWakePlayer(player, c, true, true, false, true)
            MinecraftForge.EVENT_BUS.post(event0)
            player.wakeUpPlayer(true, true, false)

            if (player.level().isRemote && GalacticraftCore.isPlanetsLoaded) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_WAKE_PLAYER,
                        GCCoreUtil.getDimensionID(player.level()),
                        arrayOf<Any?>()
                    )
                )
            }
        }
    }

    @SubscribeEvent
    open fun onZombieSummonAid(event: ZombieEvent.SummonAidEvent) {
        if (event.getEntity() is EntityEvolvedZombie) {
            event.setCustomSummonedAid(EntityEvolvedZombie(event.getWorld()))

            if ((event.getEntity() as EntityLivingBase).getRandom()
                    .nextFloat() < (event.getEntity() as EntityEvolvedZombie).getEntityAttribute((event.getEntity() as EntityEvolvedZombie).getReinforcementsAttribute())
                    .getAttributeValue()
            ) {
                event.setResult(Event.Result.ALLOW)
            } else {
                event.setResult(Event.Result.DENY)
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun overrideSkyColor(event: EntityViewRenderEvent.FogColors) {
        //Disable any night vision effects on the sky, if the planet has no atmosphere
        if (event.getEntity() is EntityLivingBase && (event.getEntity() as EntityLivingBase).hasEffect(MobEffects.NIGHT_VISION)) {
            val worldclient: WorldClient = Minecraft.getInstance().level

            if (worldclient.provider is IGalacticraftWorldProvider && (worldclient.provider as IGalacticraftWorldProvider).hasNoAtmosphere() && event.getState()
                    .getMaterial() === Material.AIR && !(worldclient.provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()
            ) {
                val vec: Vec3d = worldclient.getFogColor(1.0f)
                event.setRed(vec.x.toFloat())
                event.setGreen(vec.y.toFloat())
                event.setBlue(vec.z.toFloat())
                return
            }

            if (worldclient.provider.getSkyRenderer() is SkyProviderOverworld && event.getEntity().posY > Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                val vec: Vec3d = TransformerHooks.getFogColorHook(event.getEntity().world)
                event.setRed(vec.x.toFloat())
                event.setGreen(vec.y.toFloat())
                event.setBlue(vec.z.toFloat())
                return
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onSoundPlayed(event: PlaySoundEvent) {
        val sound = event.sound ?: return
        if (sound.attenuation == net.minecraft.client.resources.sounds.SoundInstance.Attenuation.NONE) return
        val player = Minecraft.getInstance().player ?: return
        val environment = foundation.level.FoundationSpaceEnvironment.get(player.level())
        if (environment.body.isEmpty) return
        val gear = ClientProxyCore.playerItemData[PlayerUtil.getName(player)]
        if (gear != null && gear.getFrequencyModule() != -1) return
        val bounds = AxisAlignedBB(sound.x - 0.0015, sound.y - 0.0015, sound.z - 0.0015,
            sound.x + 0.0015, sound.y + 0.0015, sound.z + 0.0015)
        if (!OxygenUtil.isAABBInBreathableAirBlock(player) || !OxygenUtil.isAABBInBreathableAirBlock(player.level(), bounds)) {
            event.sound = foundation.client.FoundationSoundAttenuation.reduce(sound, environment.atmosphere.soundReduction())
        }
    }

    open class SleepCancelledEvent : Event()


    open class OrientCameraEvent : Event()


    companion object {
        @JvmField
        var bucketList: MutableMap<Block?, Item?> = HashMap<Block?, Item?>(4, 1f)

        @JvmField
        var bedActivated: Boolean = false

        @JvmStatic
        fun oilPresent(world: World, rand: Random, x: Int, z: Int, pos: BlockVec3): Boolean {
            var doGen2: Boolean = false

            for (dim: Int in (ConfigManagerCore.externalOilGen)!!) {
                if (dim == GCCoreUtil.getDimensionID(world)) {
                    doGen2 = true
                    break
                }
            }

            if (!doGen2) {
                return false
            }

            val biome: Biome = world.getBiome(BlockPos(x + 8, 0, z + 8))

            if (biome === Biomes.SKY || biome === Biomes.HELL) {
                return false
            }

            rand.setSeed(world.getSeed())
            val i1: Long = rand.nextInt() / 2L * 2L + 1L
            val j1: Long = rand.nextInt() / 2L * 2L + 1L
            rand.setSeed(x * i1 + z * j1 xor world.getSeed())

            var randMod: Double = min(0.2, 0.05 * ConfigManagerCore.oilGenFactor)

            if (biome.getBaseHeight() >= 0.45f) {
                randMod /= 2.0
            }
            if (biome.getBaseHeight() < -0.5f) {
                randMod *= 1.8
            }
            if (biome is BiomeDesert) {
                randMod *= 1.8
            }

            val flag1: Boolean = rand.nextDouble() <= randMod
            val flag2: Boolean = rand.nextDouble() <= randMod

            if (flag1 || flag2) {
                pos.y = 17 + rand.nextInt(10) + rand.nextInt(5)
                pos.x =
                    x + 8 - rand.nextInt(16) //do not change without thinking about chunk loading, see notes in generateOil()
                pos.z =
                    z + 8 - rand.nextInt(16) //do not change without thinking about chunk loading, see notes in generateOil()
                return true
            }

            return false
        }

        /**
         * xx, zz are the central position of 4 chunks: the chunk currently being populated + 1 in the x,z plane
         * We must not stray more than 1 chunk away from this position, that's 16 blocks
         */
        @JvmStatic
        fun generateOil(world: World, rand: Random, xx: Int, zz: Int, testFirst: Boolean) {
            val pos: BlockVec3 = BlockVec3()
            if (oilPresent(world, rand, xx, zz, pos)) {
                val x: Int = pos.x
                val cy: Int = pos.y
                val z: Int = pos.z
                val r: Int = 3 + rand.nextInt(5)

                //The method loads blocks in the range (x - r - 1) to (x + r + 1) - whatever the randoms, all these positions must be inside the +/-1 chunk range
                //This can be minimum xx - 7 - 7 - 1, that's OK!
                //This can be maximum xx + 8 + 7 + 1, that's also OK!
                if (testFirst && checkOilPresent(world, x, cy, z, r)) {
                    return
                }

                val r2: Int = r * r

                val crudeOil: IBlockState = (GCBlocks.crudeOil)!!.defaultBlockState()
                for (bx: Int in -r..r) {
                    for (by: Int in -r + 2..r - 2) {
                        val xySquared: Int = bx * bx + by * by * 3
                        for (bz: Int in -r..r) {
                            if (xySquared + bz * bz <= r2) {
                                if (checkBlock(world, BlockPos(bx + x - 1, by + cy, bz + z))) {
                                    continue
                                }
                                if (checkBlock(world, BlockPos(bx + x + 1, by + cy, bz + z))) {
                                    continue
                                }
                                if (checkBlock(world, BlockPos(bx + x, by + cy - 1, bz + z))) {
                                    continue
                                }
                                if (checkBlock(world, BlockPos(bx + x, by + cy, bz + z - 1))) {
                                    continue
                                }
                                if (checkBlock(world, BlockPos(bx + x, by + cy, bz + z + 1))) {
                                    continue
                                }
                                if (checkBlockAbove(world, BlockPos(bx + x, by + cy + 1, bz + z))) {
                                    continue
                                }

                                world.setBlock(BlockPos(bx + x, by + cy, bz + z), crudeOil, 2)
                            }
                        }
                    }
                }
            }
        }

        @JvmStatic
        private fun checkOilPresent(world: World, x: Int, cy: Int, z: Int, r: Int): Boolean {
            val r2: Int = r * r

            for (bx: Int in -r..r) {
                for (by: Int in -r + 2..r - 2) {
                    val xySquared: Int = bx * bx + by * by * 3
                    for (bz: Int in -r..r) {
                        if (xySquared + bz * bz <= r2) {
                            if (checkBlock(world, BlockPos(bx + x - 1, by + cy, bz + z))) {
                                continue
                            }
                            if (checkBlock(world, BlockPos(bx + x + 1, by + cy, bz + z))) {
                                continue
                            }
                            if (checkBlock(world, BlockPos(bx + x, by + cy - 1, bz + z))) {
                                continue
                            }
                            if (checkBlock(world, BlockPos(bx + x, by + cy, bz + z - 1))) {
                                continue
                            }
                            if (checkBlock(world, BlockPos(bx + x, by + cy, bz + z + 1))) {
                                continue
                            }
                            if (checkBlockAbove(world, BlockPos(bx + x, by + cy + 1, bz + z))) {
                                continue
                            }

                            if (world.getBlockState(BlockPos(bx + x, by + cy, bz + z))
                                    .getBlock() === GCBlocks.crudeOil
                            ) {
                                return true
                            }
                        }
                    }
                }
            }

            return false
        }

        @JvmStatic
        fun retrogenOil(world: World, chunk: Chunk) {
            val cx: Int = chunk.x
            val cz: Int = chunk.z

            generateOil(world, Random(), cx shl 4, cz shl 4, true)
        }

        @JvmStatic
        private fun checkBlock(w: World, pos: BlockPos): Boolean {
            val state: IBlockState = w.getBlockState(pos)
            val b: Block = state.getBlock()
            if (b.getMaterial(state) === Material.AIR) {
                return true
            }
            return b is BlockLiquid && b !== GCBlocks.crudeOil
        }

        @JvmStatic
        private fun checkBlockAbove(w: World, pos: BlockPos): Boolean {
            val b: Block = w.getBlockState(pos).getBlock()
            if (b is BlockSand) {
                return true
            }
            if (b is BlockGravel) {
                return true
            }
            return false
        }

        @JvmStatic
        @OnlyIn(Dist.CLIENT)
        private fun getNextSchematic(currentIndex: Int): ISchematicPage? {
            val player: EntityPlayerSP =
                (PlayerUtil.getPlayerBaseClientFromPlayer(Minecraft.getInstance().player, false))!!
            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(player))!!

            val size: Int = (stats.getUnlockedSchematics())!!.size
            val idList: HashMap<Int,Int> = HashMap<Int,Int>(size, 1f)
            for (i: Int in 0..<size) {
                idList.put(i, ((stats.getUnlockedSchematics())!!.get(i))!!.getPageID())
            }

            val keys: SortedSet<Int> = TreeSet<Int>(idList.keys)
            val iterator: MutableIterator<Int> = keys.iterator()

            for (count: Int in keys.indices) {
                val i: Int = iterator.next()!!
                val page: ISchematicPage? = SchematicRegistry.getMatchingRecipeForID(idList.get(i)!!)

                if (page!!.getPageID() == currentIndex) {
                    if (count + 1 < (stats.getUnlockedSchematics())!!.size) {
                        return (stats.getUnlockedSchematics())!!.get(count + 1)
                    } else {
                        return null
                    }
                }
            }

            return null
        }

        @JvmStatic
        @OnlyIn(Dist.CLIENT)
        private fun getLastSchematic(currentIndex: Int): ISchematicPage? {
            val player: EntityPlayerSP =
                (PlayerUtil.getPlayerBaseClientFromPlayer(Minecraft.getInstance().player, false))!!
            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(player))!!

            val size: Int = (stats.getUnlockedSchematics())!!.size
            val idList: HashMap<Int,Int> = HashMap<Int,Int>(size, 1f)
            for (i: Int in 0..<size) {
                idList.put(i, ((stats.getUnlockedSchematics())!!.get(i))!!.getPageID())
            }

            val keys: SortedSet<Int> = TreeSet<Int>(idList.keys)
            val iterator: MutableIterator<Int> = keys.iterator()

            for (count: Int in keys.indices) {
                val i: Int = iterator.next()!!
                val page: ISchematicPage? = SchematicRegistry.getMatchingRecipeForID(idList.get(i)!!)

                if (page!!.getPageID() == currentIndex) {
                    if (count - 1 >= 0) {
                        return (stats.getUnlockedSchematics())!!.get(count - 1)
                    } else {
                        return null
                    }
                }
            }

            return null
        }

    }
}
