package foundation.verification

import micdoodle8.mods.galacticraft.core.blocks.BlockBreathableAir
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightAir
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightBreathableAir
import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState
import net.minecraft.core.BlockPos
import net.minecraft.core.MappedRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import net.minecraft.resources.Identifier
import net.minecraft.world.level.EmptyBlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.material.PushReaction

fun verifyAirBlocks() {
    val registry = BuiltInRegistries.BLOCK
    (registry as MappedRegistry<Block>).unfreeze()
    val forgeRegistry = ForgeRegistries.BLOCKS as ForgeRegistry<Block>
    forgeRegistry.unfreeze()
    val bright = BlockBrightAir("bright_air")
    val breathable = BlockBreathableAir("breatheable_air")
    val brightBreathable = BlockBrightBreathableAir("bright_breathable_air")
    forgeRegistry.register(Identifier.parse("galacticraftcore:bright_air"), bright)
    forgeRegistry.register(Identifier.parse("galacticraftcore:breatheable_air"), breathable)
    forgeRegistry.register(Identifier.parse("galacticraftcore:bright_breathable_air"), brightBreathable)
    forgeRegistry.freeze()
    registry.freeze()
    for (block in listOf(bright, breathable, brightBreathable)) {
        val state = block.defaultBlockState()
        check(state.isAir && state.canBeReplaced())
        check(state.renderShape == RenderShape.INVISIBLE)
        check(state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty)
        check(state.lightDampening == 0)
        check(state.pistonPushReaction == PushReaction.POPPED)
        check(state.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO) == 0f)
        // 1.12 setResistance(1000) multiplied by three, and its getter divided by five
        check(block.explosionResistance == 600f)
    }
    check(bright.defaultBlockState().lightEmission == 15)
    check(breathable.defaultBlockState().lightEmission == 0)
    check(brightBreathable.defaultBlockState().lightEmission == 15)
    check(!breathable.defaultBlockState().getValue(BlockBreathableAir.THERMAL))
    check(breathable.defaultBlockState().setValue(BlockBreathableAir.THERMAL, true).getValue(BlockBreathableAir.THERMAL))
    check(brightBreathable.defaultBlockState().setValue(BlockBrightBreathableAir.THERMAL, true).getValue(BlockBreathableAir.THERMAL))
    val typed: Block = breathable
    val thermal = typed.getStateFromMeta(1)
    check(thermal.getValue(BlockBreathableAir.THERMAL) && typed.getMetaFromState(thermal) == 1)
    check(!typed.getStateFromMeta(0).getValue(BlockBreathableAir.THERMAL))
    check(bright.descriptionId == "block.galacticraftcore.bright_air")
}
