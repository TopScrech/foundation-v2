package galaxyspace.core.mixins

import galaxyspace.api.block.IEnergyGeyser
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import galaxyspace.core.mixins.MixinTileEntityGeothermalGenerator
import org.spongepowered.asm.mixin.Unique
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityGeothermalGenerator
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import org.spongepowered.asm.mixin.Mixin
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Mixin(value = [TileEntityGeothermalGenerator::class], remap = false)
open class MixinTileEntityGeothermalGenerator {
    @Shadow
    private var validSpout = false

    /**
     * @author ViTold
     * @reason
     */
    @Inject(
        method = ["update", "func_73660_a"],
        at = [At(
            value = "INVOKE",
            target = "Lmicdoodle8/mods/galacticraft/core/energy/tile/TileBaseUniversalElectricalSource;update()V",
            shift = At.Shift.AFTER
        ), At(
            value = "INVOKE",
            target = "Lmicdoodle8/mods/galacticraft/core/energy/tile/TileBaseUniversalElectricalSource;func_73660_a()V",
            shift = At.Shift.AFTER
        )],
        cancellable = true
    )
    open fun update(ci: CallbackInfo) {
        val te: TileEntityGeothermalGenerator = this as Any as TileEntityGeothermalGenerator
        if (te.ticks % 20 == 0) {
            val below: BlockPos = te.getPos().down()
            val stateBelow: IBlockState = te.getWorld().getBlockState(below)

            val lastValidSpout: Boolean =
                this.validSpout //ReflectionHelper.getPrivateValue(TileEntityGeothermalGenerator.class, te, "validSpout");
            var validSpoutHook: Boolean = false
            //ReflectionHelper.setPrivateValue(TileEntityGeothermalGenerator.class, te, validSpoutHook, "validSpout");
            validSpout = validSpoutHook
            if (stateBelow.getBlock() is IEnergyGeyser) {
                var pos1: BlockPos = below.down()
                while (te.getPos().getY() - pos1.getY() < 20) {
                    val state: IBlockState = te.getWorld().getBlockState(pos1)
                    val geyser: IEnergyGeyser = stateBelow.getBlock() as IEnergyGeyser
                    val work: Boolean = geyser.isWorkGeyser(te.getWorld(), stateBelow, below)

                    if (work) {
                        validSpoutHook = true
                        //ReflectionHelper.setPrivateValue(TileEntityGeothermalGenerator.class, te, validSpoutHook, "validSpout");
                        validSpout = validSpoutHook
                        break
                    } else if (!state.getBlock().isAir(te.getWorld().getBlockState(pos1), te.getWorld(), pos1)) {
                        // Not valid
                        break
                    }
                    pos1 = pos1.down()
                }
            } else if (stateBelow.getBlock() === VenusBlocks.spout) {
                var pos1: BlockPos = below.down()
                while (te.getPos().getY() - pos1.getY() < 20) {
                    val state: IBlockState = te.getWorld().getBlockState(pos1)
                    if (state.getBlock() === (VenusModule.sulphuricAcid)!!.getBlock()) {
                        this.validSpout = true
                        break
                    }

                    if (!state.getBlock().isAir(te.getWorld().getBlockState(pos1), te.getWorld(), pos1)) {
                        break
                    }
                    pos1 = pos1.down()
                }
            }

            if (te.getWorld().isRemote && this.validSpout != lastValidSpout) {
                // Update active texture
                val state: IBlockState = te.getWorld().getBlockState(te.getPos())
                te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3)
            }
        }

        if (!te.getWorld().isRemote) {
            te.recharge(te.getStackInSlot(0) as ItemStack?)
            if (te.disableCooldown > 0) {
                --te.disableCooldown
            }

            te.generateWatts = min(max(`_1_12_2$getGenerate`(te, this.validSpout), 0), 200)
        } else if (te.generateWatts > 0 && te.ticks % ((200.0f / (te.generateWatts + 1).toFloat()).toInt() * 5 + 1) == 0) {
            val posX: Double = te.getPos().getX().toDouble() + 0.5
            val posY: Double = te.getPos().getY().toDouble() + 1.0
            val posZ: Double = te.getPos().getZ().toDouble() + 0.5
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX - 0.25, posY, posZ - 0.25),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX - 0.25, posY, posZ),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX - 0.25, posY, posZ + 0.25),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX, posY, posZ - 0.25),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX, posY, posZ),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX, posY, posZ + 0.25),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX + 0.25, posY, posZ - 0.25),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX + 0.25, posY, posZ),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
            GalacticraftPlanets.spawnParticle(
                "acidExhaust",
                Vector3(posX + 0.25, posY, posZ + 0.25),
                Vector3(0.0, 0.025, 0.0),
                *arrayOfNulls<Any>(0)
            )
        }

        te.produce()
        ci.cancel()
    }

    @Unique
    private fun `_1_12_2$getGenerate`(te: TileEntityGeothermalGenerator, valid: Boolean): Int {
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
}
