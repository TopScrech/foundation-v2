package galaxyspace.core.mixins

import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockAdvancedLandingPadFull
import micdoodle8.mods.galacticraft.api.entity.IDockable
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import net.minecraft.world.level.block.Block
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.common.ForgeChunkManager
import org.spongepowered.asm.mixin.Mixin
import java.util.ArrayList

@Mixin(value = [TileEntityLaunchController::class], remap = false)
open class MixinTileEntityLaunchConroller {
    @Shadow
    private var chunkLoadTicket: ForgeChunkManager.Ticket? = null

    private val connectedPads: MutableList<BlockPos?> = (ArrayList<Any?>() as MutableList<BlockPos?>)

    @Shadow
    private var frequencyCheckNeeded = false


    @Inject(at = [At("RETURN")], method = ["canAttachToLandingPad"], cancellable = true)
    open fun `galaxyspace$canAttachToLandingPad`(
        world: IBlockAccess,
        pos: BlockPos,
        cir: CallbackInfoReturnable<Boolean>
    ) {
        val tile: TileEntity? = world.getBlockEntity(pos)
        cir.setReturnValue(tile is IFuelDock)
    }

    @Inject(
        method = ["update"],
        at = [At(
            value = "INVOKE",
            target = "Lmicdoodle8/mods/galacticraft/core/energy/tile/TileBaseElectricBlockWithInventory;update()V",
            shift = At.Shift.AFTER
        )],
        cancellable = true
    )
    open fun update(ci: CallbackInfo) {
        val te: TileEntityLaunchController = this as Any as TileEntityLaunchController

        if (!(te.getWorld())!!.isClientSide) {
            te.controlEnabled = te.launchSchedulingEnabled && te.hasEnoughEnergyToRun && !te.getDisabled(0)
            if (this.frequencyCheckNeeded) {
                te.checkDestFrequencyValid()
                this.frequencyCheckNeeded = false
            }

            if (te.requiresClientUpdate) {
                te.requiresClientUpdate = false
            }

            if (te.ticks % 40 == 0) {
                te.setFrequency(te.frequency)
                te.setDestinationFrequency(te.destFrequency)
            }

            if (te.ticks % 20 == 0 && this.chunkLoadTicket != null) {
                for (i: Int in this.connectedPads.indices) {
                    val coords: BlockPos = this.connectedPads.get(i) as BlockPos
                    val block: Block = (te.getWorld())!!.getBlockState(coords).getBlock()
                    val tile: TileEntity? = (te.getWorld())!!.getBlockEntity(coords)

                    if (block !== GCBlocks.landingPadFull || block !== GSBlocks.ADVANCED_LANDING_PAD) {
                        this.connectedPads.removeAt(i)
                        ForgeChunkManager.unforceChunk(
                            this.chunkLoadTicket,
                            ChunkPos(coords.getX() shr 4, coords.getZ() shr 4)
                        )
                    }
                }
            }
        } else if (te.frequency == -1 && te.destFrequency == -1) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleMars(
                    PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI,
                    GCCoreUtil.getDimensionID(te.getWorld()),
                    arrayOf<Any?>(5, te.getPos(), 0)
                )
            )
        }
        ci.cancel()
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    open fun onTicketLoaded(ticket: ForgeChunkManager.Ticket?, placed: Boolean) {
        val lc: TileEntityLaunchController = this as Any as TileEntityLaunchController
        if (!(lc.getWorld())!!.isClientSide && ConfigManagerMars.launchControllerChunkLoad) {
            if (ticket == null) {
                return
            }

            if (this.chunkLoadTicket == null) {
                this.chunkLoadTicket = ticket
            }

            val nbt: NBTTagCompound = this.chunkLoadTicket!!.getModData()
            nbt.putInt("ChunkLoaderTileX", lc.getPos().getX())
            nbt.putInt("ChunkLoaderTileY", lc.getPos().getY())
            nbt.putInt("ChunkLoaderTileZ", lc.getPos().getZ())

            for (x: Int in -3..3) {
                for (z: Int in -3..3) {
                    val tile: TileEntity? = (lc.getWorld())!!.getBlockEntity(lc.getPos().add(x, 0, z))
                    val blockID: Block = (lc.getWorld())!!.getBlockState(lc.getPos().add(x, 0, z)).getBlock()
                    //System.out.println((blockID instanceof BlockLandingPadFull || blockID instanceof BlockAdvancedLandingPadFull) + " | " + (lc.getPos().getX() + x >> 4 != lc.getPos().getX() >> 4 || lc.getPos().getZ() + z >> 4 != lc.getPos().getZ() >> 4));
                    //System.out.println((lc.getPos().getX() + x >> 4) + " | " + (lc.getPos().getX() >> 4) + " || " + (lc.getPos().getZ() + z >> 4) + " | "  + (lc.getPos().getZ() >> 4));
                    //System.out.println(new BlockPos(lc.getPos().getX() + x, lc.getPos().getY(), lc.getPos().getZ() + z));
                    if ((blockID is BlockLandingPadFull || blockID is BlockAdvancedLandingPadFull)) { //&& (lc.getPos().getX() + x >> 4 != lc.getPos().getX() >> 4 || lc.getPos().getZ() + z >> 4 != lc.getPos().getZ() >> 4)) {
                        this.connectedPads.add(
                            BlockPos(
                                lc.getPos().getX() + x,
                                lc.getPos().getY(),
                                lc.getPos().getZ() + z
                            )
                        )
                        if (placed) {
                            ChunkLoadingCallback.forceChunk(
                                this.chunkLoadTicket,
                                (lc.getWorld())!!,
                                lc.getPos().getX() + x,
                                lc.getPos().getY(),
                                lc.getPos().getZ() + z,
                                lc.getOwnerName()
                            )
                        } else {
                            ChunkLoadingCallback.addToList(
                                (lc.getWorld())!!,
                                lc.getPos().getX(),
                                lc.getPos().getY(),
                                lc.getPos().getZ(),
                                lc.getOwnerName()
                            )
                        }
                    }
                }
            }
            println(this.connectedPads.toString())
            ChunkLoadingCallback.forceChunk(
                this.chunkLoadTicket,
                (lc.getWorld())!!,
                lc.getPos().getX(),
                lc.getPos().getY(),
                lc.getPos().getZ(),
                lc.getOwnerName()
            )
        }
    }

    @Inject(at = [At("HEAD")], method = ["updateRocketOnDockSettings"], cancellable = true)
    open fun updateRocketOnDockSettings(ci: CallbackInfo) {
        val te: TileEntityLaunchController = this as Any as TileEntityLaunchController

        if (te.attachedDock is IFuelDock) {
            val pad: IFuelDock = te.attachedDock as IFuelDock
            val rocket: IDockable? = pad.getDockedEntity()
            if (rocket is EntityAutoRocket) {
                rocket.updateControllerSettings(pad)
            }
            ci.cancel()
        }
    }
}
