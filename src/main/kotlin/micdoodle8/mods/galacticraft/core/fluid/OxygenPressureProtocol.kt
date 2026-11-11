package micdoodle8.mods.galacticraft.core.fluid

import foundation.block.getMetaFromState

import java.util.ArrayList
import java.util.HashMap

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.vector.BlockTuple
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

object OxygenPressureProtocol {
    @JvmField
    val nonPermeableBlocks: MutableMap<Block?, ArrayList<Int>> = HashMap<Block?, ArrayList<Int>>()

    init {
        for (s in (ConfigManagerCore.sealableIDs)!!) {
            try {
                val bt: BlockTuple? = ConfigManagerCore.stringToBlock((s)!!, "External Sealable IDs", true)
                if (bt == null) {
                    continue
                }

                val meta: Int = bt.meta

                if (nonPermeableBlocks.containsKey(bt.block)) {
                    val list: ArrayList<Int> = nonPermeableBlocks.get(bt.block)!!
                    if (!list.contains(meta)) {
                        list.add(meta)
                    } else {
                        GCLog.info("[config] External Sealable IDs: skipping duplicate entry '" + s + "'.")
                    }
                } else {
                    val list: ArrayList<Int> = ArrayList<Int>()
                    list.add(meta)
                    nonPermeableBlocks.put(bt.block, list)
                }
            } catch (e: Exception) {
                GCLog.severe("[config] External Sealable IDs: error parsing '" + s + "'. Must be in the form Blockname or BlockName:metadata")
            }
        }
    }

    @JvmStatic
    fun updateSealerStatus(head: TileEntityOxygenSealer) {
        try {
            head.threadSeal = ThreadFindSeal(head)
        } catch (e: IllegalThreadStateException) {
        }
    }

    @JvmStatic
    fun onEdgeBlockUpdated(world: World, vec: BlockPos?) {
        if (ConfigManagerCore.enableSealerEdgeChecks) {
            TickHandlerServer.scheduleNewEdgeCheck(GCCoreUtil.getDimensionID(world), vec)
        }
    }

    @JvmStatic
    fun canBlockPassAir(world: World, block: Block?, pos: BlockPos, side: EnumFacing): Boolean {
        if (block == null) {
            return true
        }

        if (block is IPartialSealableBlock) {
            return !(block as IPartialSealableBlock).isSealed(world, pos, side)
        }

        //Check leaves first, because their isOpaqueCube() test depends on graphics settings
        //(See net.minecraft.block.BlockLeaves.isOpaqueCube()!)
        if (block is BlockLeaves) {
            return true
        }

        val state: IBlockState = world.getBlockState(pos)
        if (block.isOpaqueCube(state)) {
            return block is BlockGravel || block.getMaterial(state) === Material.CLOTH || block is BlockSponge
        }

        if (block is BlockGlass || block is BlockStainedGlass) {
            return false
        }

        //Solid but non-opaque blocks, for example special glass
        if (nonPermeableBlocks.containsKey(block)) {
            val metaList: ArrayList<Int> = nonPermeableBlocks.get(block)!!
            if (metaList.contains(-1) || metaList.contains(state.getBlock().getMetaFromState(state))) {
                return false
            }
        }

        //Half slab seals on the top side or the bottom side according to its metadata
        if (block is BlockSlab) {
            val meta: Int = state.getBlock().getMetaFromState(state)
            return !(side == EnumFacing.DOWN && (meta and 8) == 8 || side == EnumFacing.UP && (meta and 8) == 0)
        }

        //Farmland etc only seals on the solid underside
        if (block is BlockFarmland || block is BlockEnchantmentTable || block is BlockLiquid) {
            return side != EnumFacing.UP
        }

        if (block is BlockPistonBase) {
            if ((state.getValue(BlockPistonBase.EXTENDED) as Boolean)) {
                val facing: EnumFacing = state.getValue(BlockPistonBase.FACING)
                return side != facing
            }
            return false
        }

        //General case - this should cover any block which correctly implements isBlockSolidOnSide
        //including most modded blocks - Forge microblocks in particular is covered by this.
        // ### Any exceptions in mods should implement the IPartialSealableBlock interface ###
        return !block.isSideSolid(state, world, pos, side.getOpposite())
    }
}
