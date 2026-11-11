package micdoodle8.mods.galacticraft.planets.mars

import foundation.level.offset

import micdoodle8.mods.galacticraft.core.event.EventHandlerGC.OrientCameraEvent
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars.EnumMachineType

import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.api.event.wgen.GCCoreEventPopulate
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.event.EventLandingPadRemoval
import micdoodle8.mods.galacticraft.core.event.EventWakePlayer
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityCryogenicChamber
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import micdoodle8.mods.galacticraft.planets.mars.world.gen.WorldGenEggs
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EntityDamageSource
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class EventHandlerMars {
    @SubscribeEvent
    open fun onLivingDeath(event: LivingDeathEvent) {
        if (event.getSource().damageType == "slimeling" && event.getSource() is EntityDamageSource) {
            val source: EntityDamageSource = event.getSource() as EntityDamageSource

            if (source.getTrueSource() is EntitySlimeling && !source.getTrueSource()!!.world.isRemote) {
                (source.getTrueSource() as EntitySlimeling).kills++
            }
        }
    }

    @SubscribeEvent
    open fun onLivingAttacked(event: LivingAttackEvent) {
        if (!event.getEntity()
                .isEntityInvulnerable(event.getSource()) && !event.getEntity().level().isRemote && event.getEntityLiving()
                .getHealth() <= 0.0f && !(event.getSource().isFireDamage() && event.getEntityLiving()
                .hasEffect(MobEffects.FIRE_RESISTANCE))
        ) {
            val entity: Entity? = event.getSource().getTrueSource()

            if (entity is EntitySlimeling) {
                val entitywolf: EntitySlimeling = entity

                if (entitywolf.isTamed()) {
//                    event.entityLiving.recentlyHit = 100;
//                    event.entityLiving.attackingPlayer = null; TODO
                }
            }
        }
    }

    @SubscribeEvent
    open fun onPlayerWakeUp(event: EventWakePlayer) {
        val player: EntityPlayer = event.getEntityPlayer()
        val c: BlockPos = player.bedLocation
        val state: IBlockState = player.level().getBlockState(c)
        val blockID: Block = state.getBlock()

        if (blockID === MarsBlocks.machine && state.getValue(BlockMachineMars.Companion.TYPE) == BlockMachineMars.EnumMachineType.CRYOGENIC_CHAMBER) {
            if (!event.immediately && event.updateWorld && event.setSpawn) {
                event.result = EntityPlayer.SleepResult.NOT_POSSIBLE_HERE
            } else if (!event.immediately && !event.updateWorld && event.setSpawn) {
                if (!player.level().isRemote) {
                    player.heal(5.0f)
                    (GCPlayerStats.get(player))!!.setCryogenicChamberCooldown(6000)

                    val ws: WorldServer = player.level() as WorldServer
                    ws.updateAllPlayersSleepingFlag()
                    if (ws.areAllPlayersAsleep() && ws.getGameRules().getBoolean("doDaylightCycle")) {
                        WorldUtil.setNextMorning(ws)
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onPlayerRotate(event: RenderPlayerGC.RotatePlayerEvent) {
        val blockPos: BlockPos? = event.getEntityPlayer().bedLocation
        if (blockPos != null) {
            var state: IBlockState = event.getEntityPlayer().world.getBlockState(blockPos)
            if (state.getBlock() === GCBlocks.fakeBlock && state.getValue(BlockMulti.MULTI_TYPE) == BlockMulti.EnumBlockMultiType.CRYO_CHAMBER) {
                val tile: TileEntity? = event.getEntityPlayer().world.getBlockEntity(blockPos)
                if (tile is TileEntityMulti) {
                    state = event.getEntityPlayer().world.getBlockState(tile.mainBlockPosition)
                }
            }

            if (state.getBlock() === MarsBlocks.machine && state.getValue(
                    BlockMachineMars.Companion.TYPE
                ) == BlockMachineMars.EnumMachineType.CRYOGENIC_CHAMBER
            ) {
                event.shouldRotate = true
                event.vanillaOverride = true
            }
        }
    }

    private var eggGenerator: WorldGenerator? = null

    @SubscribeEvent
    open fun onPlanetDecorated(event: GCCoreEventPopulate.Post) {
        if (this.eggGenerator == null) {
            this.eggGenerator = WorldGenEggs((MarsBlocks.rock)!!)
        }

        if ((event.world)!!.provider is WorldProviderMars) {
            val eggsPerChunk: Int = 2
            var blockpos: BlockPos

            for (eggCount: Int in 0..<eggsPerChunk) {
                blockpos =
                    (event.pos)!!.offset((event.rand)!!.nextInt(16) + 8, (event.rand)!!.nextInt(104) + 24, (event.rand)!!.nextInt(16) + 8)
                this.eggGenerator!!.generate(event.world, event.rand, blockpos)
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun orientCamera(event: EventHandlerGC.OrientCameraEvent?) {
        val entity: EntityPlayer? = Minecraft.getInstance().player

        if (entity != null) {
            val x: Int = MathHelper.floor(entity.x)
            val y: Int = MathHelper.floor(entity.y)
            val z: Int = MathHelper.floor(entity.z)
            var tile: TileEntity? = Minecraft.getInstance().level.getTileEntity(BlockPos(x, y - 1, z))

            if (tile is TileEntityMulti) {
                tile = tile.getMainBlockTile()
            }

            if (tile is TileEntityCryogenicChamber) {
                GL11.glRotatef(180f, 0.0f, 1.0f, 0.0f)

                when (tile.getBlockMetadata() and 3) {
                    0 -> GL11.glTranslatef(-0.4f, -0.5f, 4.1f)
                    1 -> GL11.glTranslatef(0f, -0.5f, 4.1f)
                    2 -> GL11.glTranslatef(0f, -0.5f, 4.1f)
                    3 -> GL11.glTranslatef(0.0f, -0.5f, 4.1f)
                }

                GL11.glRotatef(-180f, 0.0f, 1.0f, 0.0f)

                GL11.glRotatef(
                    (FMLClientHandler.instance().getClientPlayerEntity().sleepTimer - 50).toFloat(),
                    0.0f,
                    1.0f,
                    0.0f
                )
                GL11.glTranslatef(0.0f, 0.3f, 0.0f)
            }
        }
    }

    @SubscribeEvent
    open fun onLandingPadRemoved(event: EventLandingPadRemoval) {
        val tile: TileEntity? = (event.world)!!.getBlockEntity(event.pos)

        if (tile is IFuelDock) {
            val dock: IFuelDock = tile as IFuelDock

            for (connectedTile: ILandingPadAttachable? in (dock.getConnectedTiles())!!) {
                if (connectedTile is TileEntityLaunchController) {
                    val launchController: TileEntityLaunchController = connectedTile
                    if (launchController.getEnergyStoredGC() > 0.0f && launchController.launchPadRemovalDisabled && !launchController.getDisabled(
                            0
                        )
                    ) {
                        event.allow = false
                        return
                    }
                    break
                }
            }
        }
    }
}
