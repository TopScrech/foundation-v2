package galaxyspace.systems.BarnardsSystem.core.events

import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.fml.common.eventhandler.Event.Result

import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigDimensions
import galaxyspace.systems.BarnardsSystem.core.events.BREventHandler
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.core.handler.capabilities.GSStatsCapability
import galaxyspace.core.handler.capabilities.StatsCapability
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.UseHoeEvent
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

open class BREventHandler {
    @SubscribeEvent
    open fun onHoeUse(event: UseHoeEvent) {
        val world: World = event.getWorld()
        val state: IBlockState = world.getBlockState(event.getPos())

        if (state === BRBlocks.BARNARDA_C_GRASS.getDefaultState() || state === BRBlocks.BARNARDA_C_BLOCKS.getDefaultState()
                .withProperty(
                    Barnarda_C_Blocks.Companion.BASIC_TYPE,
                    EnumBlockBarnardaC.DIRT
                )
        ) {
            world.setBlockState(event.getPos(), BRBlocks.BARNARDA_C_FARMLAND.getDefaultState(), 3)
            event.setResult(Event.Result.ALLOW)
        }
    }

    @SubscribeEvent
    open fun furnanceFuel(e: FurnaceFuelBurnTimeEvent) {
        val stack: ItemStack = e.getItemStack()
        if (stack.isItemEqual(ItemStack(BRBlocks.BARNARDA_C_BLOCKS, 1, 6))) {
            e.setBurnTime(180)
        }

        if (stack.isItemEqual(ItemStack(BRBlocks.BARNARDA_C_VIOLET_LOG, 1, 0))) {
            e.setBurnTime(-1)
        }
    }

    @SubscribeEvent
    open fun onEntityUpdate(event: LivingEvent.LivingUpdateEvent) {
        val living: EntityLivingBase = event.getEntityLiving()
        val world: World? = living.world

        if (living is EntityPlayerMP) {
            val player: EntityPlayerMP = living
            val gsstats: StatsCapability = (galaxyspace.core.handler.capabilities.StatsCapability.get(player))!!

            if (BRConfigCore.Companion.enableBarnardsSystems && BRConfigDimensions.Companion.enableBarnardaC && BRConfigCore.Companion.survivalModeOnBarnarda && !gsstats.isBarnardaSurvivalMode()) {
                WorldUtil.transferEntityToDimension(
                    player,
                    BRConfigDimensions.Companion.dimensionIDBarnardaC,
                    player.getServerWorld()
                )

                gsstats.setBarnardaSurvivalMode()

                player.sendMessage(TextComponentString(EnumColor.BRIGHT_GREEN.toString() + "[BETA] Welcome in survival mode on Barnarda C."))
            }
        }
    }
}
