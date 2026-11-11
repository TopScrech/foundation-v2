package micdoodle8.mods.galacticraft.core.fluid

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockBreathableAir
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledBlockChange
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

open class ThreadFindSeal
constructor(world: World?, head: BlockPos, checkCount: Int, sealers: MutableList<TileEntityOxygenSealer>) {
    @JvmField
    var sealedFinal: AtomicBoolean = AtomicBoolean()

    @JvmField
    var looping: AtomicBoolean = AtomicBoolean()

    private var world: World? = null
    private var head: BlockVec3? = null
    private var sealed = false
    private var sealers: MutableList<TileEntityOxygenSealer>? = null
    private var checkCount: Int = 0
    private var sealersAround: HashMap<BlockVec3?, TileEntityOxygenSealer?>? = null
    private var currentLayer: MutableList<BlockVec3>? = null
    private var airToReplace: MutableList<BlockVec3>? = null
    private var fireToReplace: MutableList<BlockVec3>? = null
    private var breatheableToReplace: MutableList<BlockVec3>? = null
    private var airToReplaceBright: MutableList<BlockVec3>? = null
    private var breatheableToReplaceBright: MutableList<BlockVec3>? = null
    private var ambientThermalTracked: MutableList<BlockVec3>? = null
    private var ambientThermalTrackedBright: MutableList<BlockVec3>? = null
    private var otherSealers: MutableList<TileEntityOxygenSealer>? = null
    private var torchesToUpdate: MutableList<BlockVec3?>? = null
    private var foundAmbientThermal: Boolean = false

    @JvmField
    var leakTrace: MutableList<BlockVec3?>? = null

    constructor(sealer: TileEntityOxygenSealer) : this(
        sealer.getWorld(),
        sealer.getPos().up(),
        sealer.getFindSealChecks(),
        ArrayList<TileEntityOxygenSealer>(mutableListOf<TileEntityOxygenSealer>(sealer))
    )

    init {
run initialization@ {

        if (anylooping.getAndSet(true)) {
            return@initialization
        }
        this.world = world
        this.head = BlockVec3(head)
        this.checkCount = checkCount
        this.sealers = sealers
        this.foundAmbientThermal = false
        checkedClear()
        checkedSize = 0
        this.torchesToUpdate = LinkedList<BlockVec3?>()

        this.sealersAround = TileEntityOxygenSealer.getSealersAround(world, head, 1024 * 1024)

        //If called by a sealer test the head block and if partiallySealable mark its sides done as required
        if (!sealers.isEmpty()) {
            if (checkCount > 0) {
                val headState: IBlockState = this.world!!.getBlockState(head)
                if (!(headState.getBlock().isAir(headState, world, head))) {
                    this.canBlockPassAirCheck(headState.getBlock(), this.head, 1)
                    //reset the checkCount as canBlockPassAirCheck might have changed it
                    this.checkCount = checkCount
                }
            }

            this.looping.set(true)
            for (eachSealer: TileEntityOxygenSealer in sealers) {
                eachSealer.threadSeal = this
            }

            //            if (ConfigManagerCore.enableSealerMultithreading)
//            {
//                new ThreadedFindSeal();
//            }
//            else
//            {
            this.check()
            //            }
        } else  //If not called by a sealer, it's a breathable air edge check
        {
            //Run this in the main thread
            this.check()
        }
        anylooping.set(false)
    
}
}

    //Multi-threaded version of the code for sealer updates (not for edge checks).
    open inner class ThreadedFindSeal constructor() : Thread("GC Sealer Roomfinder Thread") {
        init {
            if (this.isAlive()) {
                this.interrupt()
            }

            //Run this as a separate thread
            this.start()
        }

        override fun run() {
            this@ThreadFindSeal.check()
            anylooping.set(false)
        }
    }

    open fun check() {
        val time1: Long = System.nanoTime()

        this.sealed = true
        val tile: TileEntity? = this.head!!.getTileEntityOnSide((world)!!, EnumFacing.DOWN)
        this.foundAmbientThermal = tile is TileEntityOxygenSealer && tile.thermalControlEnabled()
        this.checkedAdd(this.head)
        this.currentLayer = LinkedList<BlockVec3>()
        this.airToReplace = LinkedList<BlockVec3>()
        this.airToReplaceBright = LinkedList<BlockVec3>()
        this.ambientThermalTracked = LinkedList<BlockVec3>()
        this.ambientThermalTrackedBright = LinkedList<BlockVec3>()

        if (this.checkCount > 0) {
            this.currentLayer!!.add(this.head!!)
            if (this.head!!.x < -29990000 || this.head!!.z < -29990000 || this.head!!.x >= 29990000 || this.head!!.z >= 29990000) {
                val state: IBlockState = (this.head!!.getBlockState_noChunkLoad((this.world)!!))!!
                if (Blocks.AIR === state.getBlock()) {
                    this.airToReplace!!.add(this.head!!.clone())
                } else if (state.getBlock() === GCBlocks.brightAir) {
                    this.airToReplaceBright!!.add(this.head!!.clone())
                }
                this.doLayerNearMapEdge()
            } else {
                val headState: IBlockState? = this.head!!.getBlockStateSafe_noChunkLoad((this.world)!!)
                if (Blocks.AIR === headState!!.getBlock()) {
                    this.airToReplace!!.add(this.head!!.clone())
                } else if ((headState)!!.getBlock() === GCBlocks.brightAir) {
                    this.airToReplaceBright!!.add(this.head!!.clone())
                }
                this.doLayer()
            }
        } else {
            this.sealed = false
        }

        val time2: Long = System.nanoTime()

        //Can only be properly sealed if there is at least one sealer here (on edge check)
        if (this.sealers!!.isEmpty()) {
            this.sealed = false
        }

        if (this.sealed) {
            this.makeSealGood(this.foundAmbientThermal)
            this.leakTrace = null
        } else {
            var checkedSave: Int = checkedSize
            checkedClear()
            this.breatheableToReplace = LinkedList<BlockVec3>()
            this.breatheableToReplaceBright = LinkedList<BlockVec3>()
            this.fireToReplace = LinkedList<BlockVec3>()
            this.otherSealers = LinkedList<TileEntityOxygenSealer>()
            // unseal() will mark breatheableAir blocks for change as it
            // finds them, also searches for unchecked sealers
            this.currentLayer!!.clear()
            this.currentLayer!!.add(this.head!!)
            this.torchesToUpdate!!.clear()
            if (this.head!!.x < -29990000 || this.head!!.z < -29990000 || this.head!!.x >= 29990000 || this.head!!.z >= 29990000) {
                this.unsealNearMapEdge()
            } else {
                this.unseal()
            }

            if (!this.otherSealers!!.isEmpty()) {
                // OtherSealers will have members if the space to be made
                // unbreathable actually still has an unchecked sealer in it
                val sealersSave: MutableList<TileEntityOxygenSealer>? = this.sealers
                val torchesSave: MutableList<BlockVec3?>? = this.torchesToUpdate
                val sealersDone: MutableList<TileEntityOxygenSealer?> = ArrayList<TileEntityOxygenSealer?>()
                sealersDone.addAll(this.sealers!!)
                for (otherSealer: TileEntityOxygenSealer in this.otherSealers!!) {
                    // If it hasn't already been counted, need to check the
                    // other sealer immediately in case it can keep the space
                    // sealed
                    if (!sealersDone.contains(otherSealer) && otherSealer.getFindSealChecks() > 0) {
                        val newhead: BlockVec3 = BlockVec3(otherSealer).translate(0, 1, 0)
                        this.sealed = true
                        this.checkCount = otherSealer.getFindSealChecks()
                        this.sealers = LinkedList<TileEntityOxygenSealer>()
                        this.sealers!!.add(otherSealer)
                        if (otherSealer.thermalControlEnabled()) {
                            foundAmbientThermal = true
                        }
                        checkedClear()
                        this.checkedAdd(newhead)
                        this.currentLayer!!.clear()
                        this.airToReplace!!.clear()
                        this.airToReplaceBright!!.clear()
                        this.torchesToUpdate = LinkedList<BlockVec3?>()
                        this.currentLayer!!.add(newhead.clone())
                        if (newhead.x < -29990000 || newhead.z < -29990000 || newhead.x >= 29990000 || newhead.z >= 29990000) {
                            this.doLayerNearMapEdge()
                        } else {
                            this.doLayer()
                        }

                        // If found a sealer which can still seal the space, it
                        // should take over as head
                        if (this.sealed) {
                            if (ConfigManagerCore.enableDebug) {
                                GCLog.info("Oxygen Sealer replacing head at x" + this.head!!.x + " y" + (this.head!!.y - 1) + " z" + this.head!!.z)
                            }
                            if (!sealersSave!!.isEmpty()) {
                                val oldHead: TileEntityOxygenSealer = sealersSave.get(0)
                                if (!this.sealers!!.contains(oldHead)) {
                                    this.sealers!!.add(oldHead)
                                    if (oldHead.thermalControlEnabled()) {
                                        foundAmbientThermal = true
                                    }
                                }
                            }
                            this.head = newhead.clone()
                            otherSealer.threadSeal = this
                            otherSealer.stopSealThreadCooldown = 75 + TileEntityOxygenSealer.countEntities
                            checkedSave += checkedSize
                            break
                        } else {
                            sealersDone.addAll(this.sealers!!)
                        }

                        checkedSave += checkedSize
                    }
                }

                // Restore sealers to what it was, if this search did not
                // result in a seal
                if (!this.sealed) {
                    this.sealers = sealersSave
                    this.torchesToUpdate = torchesSave
                } else {
                    //If the second search sealed the area, there may also be air or torches to update
                    this.makeSealGood(foundAmbientThermal)
                }
            }
            checkedSize = checkedSave

            if (!this.sealed) {
                val block: Block = (this.head!!.getBlockState((this.world)!!))!!.getBlock()
                if (block === GCBlocks.breatheableAir) {
                    this.breatheableToReplace!!.add(this.head!!)
                }
                if (block === GCBlocks.brightBreatheableAir) {
                    this.breatheableToReplaceBright!!.add(this.head!!)
                }
                this.makeSealBad()
            } else {
                this.leakTrace = null
            }
        }

        // Set any sealers found which are not the head sealer, not to run their
        // own seal checks for a while
        // (The player can control which is the head sealer in a space by
        // enabling just that one and disabling all the others)
        val headSealer: TileEntityOxygenSealer? = this.sealersAround!!.get(this.head!!.clone().translate(0, -1, 0))

        //TODO: if multi-threaded, this final code block giving access to the sealer tiles needs to be threadsafe

        // If it is sealed, cooldown can be extended as frequent checks are not needed
        if (headSealer != null) {
            headSealer.stopSealThreadCooldown = 75 + TileEntityOxygenSealer.countEntities
        }

        for (sealer: TileEntityOxygenSealer in this.sealers!!) {
            // Sealers which are not the head sealer: put them on cooldown so
            // the inactive ones don't start their own threads and so unseal
            // this volume
            // and update threadSeal reference of all sealers found (even the
            // inactive ones)
            if (sealer !== headSealer && headSealer != null) {
                sealer.threadSeal = this
                sealer.stopSealThreadCooldown = headSealer.stopSealThreadCooldown + 51
            }
        }

        this.sealedFinal.set(this.sealed)
        this.looping.set(false)

        if (ConfigManagerCore.enableDebug) {
            val time3: Long = System.nanoTime()
            val total: Float = (time3 - time1) / 1000000.0f
            val looping: Float = (time2 - time1) / 1000000.0f
            val replacing: Float = (time3 - time2) / 1000000.0f
            GCLog.info("Oxygen Sealer Check Completed at x" + this.head!!.x + " y" + this.head!!.y + " z" + this.head!!.z)
            GCLog.info("   Sealed: " + this.sealed + "  ~  " + this.sealers!!.size + " sealers  ~  " + (checkedSize - 1) + " blocks")
            GCLog.info(
                "   Total Time taken: " + String.format("%.2f", total) + "ms  ~  " + String.format(
                    "%.2f",
                    looping
                ) + " + " + String.format("%.2f", replacing) + ""
            )
        }
    }

    private fun makeSealGood(ambientThermal: Boolean) {
        if (!this.airToReplace!!.isEmpty() || !this.airToReplaceBright!!.isEmpty() || !ambientThermalTracked!!.isEmpty() || !ambientThermalTracked!!.isEmpty()) {
            val changeList: MutableList<ScheduledBlockChange?> = LinkedList<ScheduledBlockChange?>()
            val breatheableAirID: Block = (GCBlocks.breatheableAir)!!
            val breatheableAirIDBright: Block = (GCBlocks.brightBreatheableAir)!!
            val metadata: Int = if (ambientThermal) 1 else 0
            //TODO: Can we somehow detect only changes in state of ambientThermal since last check?  tricky...
            for (checkedVec: BlockVec3 in this.airToReplace!!) {
                //No block update for performance reasons; deal with unlit torches separately
                changeList.add(
                    ScheduledBlockChange(
                        checkedVec.toBlockPos(),
                        breatheableAirID.defaultBlockState().setValue(BlockBreathableAir.THERMAL, metadata % 2 == 1),
                        0
                    )
                )
            }
            for (checkedVec: BlockVec3 in this.airToReplaceBright!!) {
                changeList.add(
                    ScheduledBlockChange(
                        checkedVec.toBlockPos(),
                        breatheableAirIDBright.defaultBlockState().setValue(BlockBreathableAir.THERMAL, metadata % 2 == 1),
                        0
                    )
                )
            }
            for (checkedVec: BlockVec3 in this.ambientThermalTracked!!) {
                if (checkedVec.getBlockMetadata((this.world)!!) != metadata) {
                    changeList.add(
                        ScheduledBlockChange(
                            checkedVec.toBlockPos(),
                            breatheableAirID.defaultBlockState().setValue(BlockBreathableAir.THERMAL, metadata % 2 == 1),
                            0
                        )
                    )
                }
            }
            for (checkedVec: BlockVec3 in this.ambientThermalTrackedBright!!) {
                if (checkedVec.getBlockMetadata((this.world)!!) != metadata) {
                    changeList.add(
                        ScheduledBlockChange(
                            checkedVec.toBlockPos(),
                            breatheableAirIDBright.defaultBlockState().setValue(BlockBreathableAir.THERMAL, metadata % 2 == 1),
                            0
                        )
                    )
                }
            }

            TickHandlerServer.scheduleNewBlockChange(GCCoreUtil.getDimensionID(this.world), changeList)
        }
        if (!this.torchesToUpdate!!.isEmpty()) {
            TickHandlerServer.scheduleNewTorchUpdate(GCCoreUtil.getDimensionID(this.world), this.torchesToUpdate)
        }
    }

    private fun makeSealBad() {
        if (!this.breatheableToReplace!!.isEmpty() || !this.breatheableToReplaceBright!!.isEmpty()) {
            val changeList: MutableList<ScheduledBlockChange?> = LinkedList<ScheduledBlockChange?>()
            for (checkedVec: BlockVec3 in this.breatheableToReplace!!) {
                changeList.add(ScheduledBlockChange(checkedVec.toBlockPos(), Blocks.AIR.defaultBlockState(), 0))
            }
            for (checkedVec: BlockVec3 in this.fireToReplace!!) {
                changeList.add(ScheduledBlockChange(checkedVec.toBlockPos(), Blocks.AIR.defaultBlockState(), 2))
            }
            for (checkedVec: BlockVec3 in this.breatheableToReplaceBright!!) {
                changeList.add(ScheduledBlockChange(checkedVec.toBlockPos(), (GCBlocks.brightAir)!!.defaultBlockState(), 0))
            }
            TickHandlerServer.scheduleNewBlockChange(GCCoreUtil.getDimensionID(this.world), changeList)
        }
        if (!this.torchesToUpdate!!.isEmpty()) {
            TickHandlerServer.scheduleNewTorchUpdate(GCCoreUtil.getDimensionID(this.world), this.torchesToUpdate)
        }
    }

    private fun unseal() {
        //Local variables are fractionally faster than statics
        val breatheableAirID: Block? = GCBlocks.breatheableAir
        val breatheableAirIDBright: Block? = GCBlocks.brightBreatheableAir
        val oxygenSealerID: Block? = GCBlocks.oxygenSealer
        val fireBlock: Block? = Blocks.FIRE
        val airBlock: Block? = Blocks.AIR
        val airBlockBright: Block? = GCBlocks.brightAir
        val toReplaceLocal: MutableList<BlockVec3>? = this.breatheableToReplace
        val toReplaceLocalBright: MutableList<BlockVec3>? = this.breatheableToReplaceBright
        var nextLayer: LinkedList<BlockVec3> = LinkedList<BlockVec3>()
        val world: World? = this.world
        var side: Int
        var bits: Int

        while (this.currentLayer!!.size > 0) {
            for (vec: BlockVec3 in this.currentLayer!!) {
                side = 0
                bits = vec.sideDoneBits
                do {
                    if ((bits and (1 shl side)) == 0) {
                        if (!checkedContains(vec, side)) {
                            val sideVec: BlockVec3 = vec.newVecSide(side)
                            val state: IBlockState? = sideVec.getBlockStateSafe_noChunkLoad((world)!!)
                            val block: Block? = if (state == null) null else state.getBlock()

                            if (block === breatheableAirID) {
                                toReplaceLocal!!.add(sideVec)
                                nextLayer.add(sideVec)
                                checkedAdd(sideVec)
                            } else if (block === breatheableAirIDBright) {
                                toReplaceLocalBright!!.add(sideVec)
                                nextLayer.add(sideVec)
                                checkedAdd(sideVec)
                            } else if (block === fireBlock) {
                                this.fireToReplace!!.add(sideVec)
                                nextLayer.add(sideVec)
                                checkedAdd(sideVec)
                            } else if (block === oxygenSealerID) {
                                val sealer: TileEntityOxygenSealer? = this.sealersAround!!.get(sideVec)

                                if (sealer != null && !this.sealers!!.contains(sealer)) {
                                    if (side == 0) {
                                        //Accessing the vent side of the sealer, so add it
                                        this.otherSealers!!.add(sealer)
                                        checkedAdd(sideVec)
                                    }
                                    //if side is not 0, do not add to checked so can be rechecked from other sides
                                } else {
                                    checkedAdd(sideVec)
                                }
                            } else {
                                if (block != null && block !== airBlock && block !== airBlockBright) {
                                    //This test applies any necessary checkedAdd();
                                    if (this.canBlockPassAirCheck(block, sideVec, side)) {
                                        //Look outbound through partially sealable blocks in case there is breatheableAir to clear beyond
                                        nextLayer.add(sideVec)
                                    }
                                } else {
                                    if (state != null) checkedAdd(sideVec)
                                }
                            }
                        }
                    }
                    side++
                } while (side < 6)
            }

            // Set up the next layer as current layer for the while loop
            this.currentLayer = nextLayer
            nextLayer = LinkedList<BlockVec3>()
        }
    }

    /**
     * Literally the only difference from unseal() should be this:
     * Block id = sideVec.getBlockID_noChunkLoad(world);
     *
     * In this code, there is a map edge check on the x, z coordinates (outside map edge at 30,000,000 blocks?)
     * This check is skipped in the "safe" version of the same code, for higher performance
     * because doing this check 50000 times when looking at blocks around a sealer at spawn is obviously dumb
     */
    private fun unsealNearMapEdge() {
        //Local variables are fractionally faster than statics
        val breatheableAirID: Block? = GCBlocks.breatheableAir
        val breatheableAirIDBright: Block? = GCBlocks.brightBreatheableAir
        val oxygenSealerID: Block? = GCBlocks.oxygenSealer
        val fireBlock: Block? = Blocks.FIRE
        val airBlock: Block? = Blocks.AIR
        val airBlockBright: Block? = GCBlocks.brightAir
        val toReplaceLocal: MutableList<BlockVec3>? = this.breatheableToReplace
        var nextLayer: LinkedList<BlockVec3> = LinkedList<BlockVec3>()
        val world: World? = this.world
        var side: Int
        var bits: Int

        while (this.currentLayer!!.size > 0) {
            for (vec: BlockVec3 in this.currentLayer!!) {
                side = 0
                bits = vec.sideDoneBits
                do {
                    if ((bits and (1 shl side)) == 0) {
                        if (!checkedContains(vec, side)) {
                            val sideVec: BlockVec3 = vec.newVecSide(side)
                            val state: IBlockState? = sideVec.getBlockState_noChunkLoad((world)!!)
                            val block: Block? = if (state == null) null else state.getBlock()

                            if (block === breatheableAirID) {
                                toReplaceLocal!!.add(sideVec)
                                nextLayer.add(sideVec)
                                checkedAdd(sideVec)
                            } else if (block === breatheableAirIDBright) {
                                this.breatheableToReplaceBright!!.add(sideVec)
                                nextLayer.add(sideVec)
                                checkedAdd(sideVec)
                            } else if (block === fireBlock) {
                                this.fireToReplace!!.add(sideVec)
                                nextLayer.add(sideVec)
                                checkedAdd(sideVec)
                            } else if (block === oxygenSealerID) {
                                val sealer: TileEntityOxygenSealer? = this.sealersAround!!.get(sideVec)

                                if (sealer != null && !this.sealers!!.contains(sealer)) {
                                    if (side == 0) {
                                        //Accessing the vent side of the sealer, so add it
                                        this.otherSealers!!.add(sealer)
                                        checkedAdd(sideVec)
                                    }
                                    //if side is not 0, do not add to checked so can be rechecked from other sides
                                } else {
                                    checkedAdd(sideVec)
                                }
                            } else {
                                if (block != null && block !== airBlock && block !== airBlockBright) {
                                    //This test applies any necessary checkedAdd();
                                    if (this.canBlockPassAirCheck(block, sideVec, side)) {
                                        //Look outbound through partially sealable blocks in case there is breatheableAir to clear beyond
                                        nextLayer.add(sideVec)
                                    }
                                } else {
                                    if (block != null) checkedAdd(sideVec)
                                }
                            }
                        }
                    }
                    side++
                } while (side < 6)
            }

            // Set up the next layer as current layer for the while loop
            this.currentLayer = nextLayer
            nextLayer = LinkedList<BlockVec3>()
        }
    }

    private fun doLayer() {
        //Local variables are fractionally faster than statics
        val breatheableAirID: Block? = GCBlocks.breatheableAir
        val airID: Block? = Blocks.AIR
        val breatheableAirIDBright: Block? = GCBlocks.brightBreatheableAir
        val airIDBright: Block? = GCBlocks.brightAir
        val oxygenSealerID: Block? = GCBlocks.oxygenSealer
        var nextLayer: LinkedList<BlockVec3> = LinkedList<BlockVec3>()
        val world: World? = this.world
        var side: Int
        var bits: Int

        while (this.sealed && this.currentLayer!!.size > 0) {
            for (vec: BlockVec3 in this.currentLayer!!) {
                //This is for side = 0 to 5 - but using do...while() is fractionally quicker
                side = 0
                bits = vec.sideDoneBits
                do {
                    //Skip the side which this was entered from
                    //This is also used to skip looking on the solid sides of partially sealable blocks
                    if ((bits and (1 shl side)) == 0) {
                        // The sides 0 to 5 correspond with the EnumFacings
                        // but saves a bit of time not to call EnumFacing

                        if (!checkedContains(vec, side)) {
                            val sideVec: BlockVec3 = vec.newVecSide(side)
                            if (this.checkCount > 0) {
                                this.checkCount--

                                val state: IBlockState? = sideVec.getBlockStateSafe_noChunkLoad((world)!!)
                                val block: Block = state!!.getBlock()
                                // The most likely case
                                if (block === breatheableAirID) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.ambientThermalTracked!!.add(sideVec)
                                } else if (block === airID) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.airToReplace!!.add(sideVec)
                                } else if (block === breatheableAirIDBright) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.ambientThermalTrackedBright!!.add(sideVec)
                                } else if (block === airIDBright) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.airToReplaceBright!!.add(sideVec)
                                } else if (block == null) {
                                    // Broken through to the void or the
                                    // stratosphere (above y==255) - set
                                    // unsealed and abort
                                    this.checkCount = 0
                                    this.sealed = false
                                    return
                                } else if (block === oxygenSealerID) {
                                    val sealer: TileEntityOxygenSealer? = this.sealersAround!!.get(sideVec)

                                    if (sealer != null && !this.sealers!!.contains(sealer)) {
                                        if (side == 0) {
                                            checkedAdd(sideVec)
                                            this.sealers!!.add(sealer)
                                            if (sealer.thermalControlEnabled()) {
                                                foundAmbientThermal = true
                                            }
                                            this.checkCount += sealer.getFindSealChecks()
                                        }
                                        //if side != 0, no checkedAdd() - allows this sealer to be checked again from other sides
                                    }
                                } else if (this.canBlockPassAirCheck(block, sideVec, side)) {
                                    nextLayer.add(sideVec)
                                }
                                //If the chunk was unloaded, BlockVec3.getBlockID returns Blocks.bedrock
                                //which is a solid block, so the loop will treat that as a sealed edge
                                //and not iterate any further in that direction
                            } else {
                                val state: IBlockState? = sideVec.getBlockStateSafe_noChunkLoad((this.world)!!)
                                val block: Block? = if (state == null) null else state.getBlock()
                                // id == null means the void or height y>255, both
                                // of which are unsealed obviously
                                if (block == null || block === airID || block === breatheableAirID || block === airIDBright || block === breatheableAirIDBright || this.canBlockPassAirCheck(
                                        block,
                                        sideVec,
                                        side
                                    )
                                ) {
                                    this.sealed = false
                                    if (this.sealers!!.size > 0) {
                                        vec.sideDoneBits = side shl 6
                                        traceLeak(vec)
                                    }
                                    return
                                }
                            }
                        }
                    }
                    side++
                } while (side < 6)
            }

            // Is there a further layer of air/permeable blocks to test?
            this.currentLayer = nextLayer
            nextLayer = LinkedList<BlockVec3>()
        }
    }

    /**
     * Again, literally the only difference from doLayer() should be these two lines:
     * Block id = sideVec.getBlockID_noChunkLoad(world);
     *
     * In this code, there is a map edge check on the x, z coordinates (outside map edge at 30,000,000 blocks?)
     * This check is skipped in the "safe" version of the same code, for higher performance
     * because doing this check 50000 times when looking at blocks around a sealer at spawn is obviously dumb
     */
    private fun doLayerNearMapEdge() {
        //Local variables are fractionally faster than statics
        val breatheableAirID: Block? = GCBlocks.breatheableAir
        val airID: Block? = Blocks.AIR
        val breatheableAirIDBright: Block? = GCBlocks.brightBreatheableAir
        val airIDBright: Block? = GCBlocks.brightAir
        val oxygenSealerID: Block? = GCBlocks.oxygenSealer
        var nextLayer: LinkedList<BlockVec3> = LinkedList<BlockVec3>()
        val world: World? = this.world
        var side: Int
        var bits: Int

        while (this.sealed && this.currentLayer!!.size > 0) {
            for (vec: BlockVec3 in this.currentLayer!!) {
                //This is for side = 0 to 5 - but using do...while() is fractionally quicker
                side = 0
                bits = vec.sideDoneBits
                do {
                    //Skip the side which this was entered from
                    //This is also used to skip looking on the solid sides of partially sealable blocks
                    if ((bits and (1 shl side)) == 0) {
                        // The sides 0 to 5 correspond with the EnumFacings
                        // but saves a bit of time not to call EnumFacing

                        if (!checkedContains(vec, side)) {
                            val sideVec: BlockVec3 = vec.newVecSide(side)
                            if (this.checkCount > 0) {
                                this.checkCount--

                                val state: IBlockState? = sideVec.getBlockState_noChunkLoad((world)!!)
                                val block: Block? = if (state == null) null else state.getBlock()
                                // The most likely case
                                if (block === breatheableAirID) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.ambientThermalTracked!!.add(sideVec)
                                } else if (block === airID) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.airToReplace!!.add(sideVec)
                                } else if (block === breatheableAirIDBright) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.ambientThermalTrackedBright!!.add(sideVec)
                                } else if (block === airIDBright) {
                                    checkedAdd(sideVec)
                                    nextLayer.add(sideVec)
                                    this.airToReplaceBright!!.add(sideVec)
                                } else if (block == null) {
                                    // Broken through to the void or the
                                    // stratosphere (above y==255) - set
                                    // unsealed and abort
                                    this.checkCount = 0
                                    this.sealed = false
                                    return
                                } else if (block === oxygenSealerID) {
                                    val sealer: TileEntityOxygenSealer? = this.sealersAround!!.get(sideVec)

                                    if (sealer != null && !this.sealers!!.contains(sealer)) {
                                        if (side == 0) {
                                            checkedAdd(sideVec)
                                            this.sealers!!.add(sealer)
                                            if (sealer.thermalControlEnabled()) {
                                                foundAmbientThermal = true
                                            }
                                            this.checkCount += sealer.getFindSealChecks()
                                        }
                                        //if side != 0, no checkedAdd() - allows this sealer to be checked again from other sides
                                    }
                                } else if (this.canBlockPassAirCheck(block, sideVec, side)) {
                                    nextLayer.add(sideVec)
                                }
                                //If the chunk was unloaded, BlockVec3.getBlockID returns Blocks.bedrock
                                //which is a solid block, so the loop will treat that as a sealed edge
                                //and not iterate any further in that direction
                            } else {
                                val state: IBlockState = (sideVec.getBlockState_noChunkLoad((this.world)!!))!!
                                val block: Block = state.getBlock()
                                // id == null means the void or height y>255, both
                                // of which are unsealed obviously
                                if (block == null || block === airID || block === breatheableAirID || block === airIDBright || block === breatheableAirIDBright || this.canBlockPassAirCheck(
                                        block,
                                        sideVec,
                                        side
                                    )
                                ) {
                                    this.sealed = false
                                    if (this.sealers!!.size > 0) {
                                        vec.sideDoneBits = side shl 6
                                        traceLeak(vec)
                                    }
                                    return
                                }
                            }
                        }
                    }
                    side++
                } while (side < 6)
            }

            // Is there a further layer of air/permeable blocks to test?
            this.currentLayer = nextLayer
            nextLayer = LinkedList<BlockVec3>()
        }
    }


    private fun checkedAdd(vec: BlockVec3?) {
        val dx: Int = this.head!!.x - vec!!.x
        val dz: Int = this.head!!.z - vec.z
        if (dx < -8191 || dx > 8192) return
        if (dz < -8191 || dz > 8192) return
        val bucket: intBucket? = buckets[((dx and 15) shl 4) + (dz and 15)]
        bucket!!.add(vec.y + ((dx and 0x3FF0) + ((dz and 0x3FF0) shl 10) + ((vec.sideDoneBits and 0x1C0) shl 18) shl 4))
    }

    /**
     * Currently unused - the sided implementation is used instead
     */
    private fun checkedContains(vec: BlockVec3): Boolean {
        val dx: Int = this.head!!.x - vec.x
        val dz: Int = this.head!!.z - vec.z
        if (dx < -8191 || dx > 8192) return true
        if (dz < -8191 || dz > 8192) return true
        val bucket: intBucket? = buckets[((dx and 15) shl 4) + (dz and 15)]
        return bucket!!.contains(vec.y + ((dx and 0x3FF0) + ((dz and 0x3FF0) shl 10) shl 4))
    }

    private fun checkedContains(vec: BlockVec3, side: Int): Boolean {
        var y: Int = vec.y
        var dx: Int = this.head!!.x - vec.x
        var dz: Int = this.head!!.z - vec.z
        when (side) {
            0 -> {
                y--
                if (y < 0) return false
            }

            1 -> {
                y++
                if (y > 255) return false
            }

            2 -> dz++
            3 -> dz--
            4 -> dx++
            5 -> dx--
        }
        if (dx < -8191 || dx > 8192) return true
        if (dz < -8191 || dz > 8192) return true
        val bucket: intBucket? = buckets[((dx and 15) shl 4) + (dz and 15)]
        return bucket!!.contains(y + ((dx and 0x3FF0) + ((dz and 0x3FF0) shl 10) shl 4))
    }

    private fun checkedContainsTrace(x: Int, y: Int, z: Int): BlockVec3? {
        val dx: Int = this.head!!.x - x
        val dz: Int = this.head!!.z - z
        if (dx < -8191 || dx > 8192) return null
        if (dz < -8191 || dz > 8192) return null
        val bucket: intBucket? = buckets[((dx and 15) shl 4) + (dz and 15)]
        val side: Int = bucket!!.getMSB4shifted(y + ((dx and 0x3FF0) + ((dz and 0x3FF0) shl 10) shl 4))
        if (side >= 0) {
            val vec: BlockVec3 = BlockVec3(x, y, z)
            vec.sideDoneBits = side
            return vec
        }
        return null
    }

    open fun checkedAll(): MutableList<BlockPos?> {
        val list: MutableList<BlockPos?> = LinkedList<BlockPos?>()
        for (i: Int in 0..255) {
            if (buckets[i].size() == 0) continue
            val ddx: Int = i shr 4
            val ddz: Int = i and 15
            val ints: IntArray = buckets[i].contents()
            for (j: Int in 0..<buckets[i].size()) {
                var k: Int = ints[j]
                val y: Int = k and 255
                k = k shr 4
                var dx: Int = (k and 0x3FF0) + ddx
                var dz: Int = ((k shr 10) and 0x3FF0) + ddz
                if (dx > 0x2000) dx -= 0x4000
                if (dz > 0x2000) dz -= 0x4000
                list.add(BlockPos(head!!.x + dx, y, head!!.z + dz))
            }
        }
        return list
    }

    private fun traceLeak(tracer: BlockVec3?) {
        var tracer: BlockVec3? = tracer
        val route: ArrayList<BlockVec3?> = ArrayList<BlockVec3?>()
        val start: BlockVec3? = this.head!!.clone().translate(0, 1, 0)
        var count: Int = 0
        var x: Int = tracer!!.x
        var y: Int = tracer.y
        var z: Int = tracer.z
        while (tracer != start && count < 90) {
            route.add(tracer)
            when (tracer!!.sideDoneBits shr 6) {
                1 -> y--
                0 -> y++
                3 -> z--
                2 -> z++
                5 -> x--
                4 -> x++
            }
            tracer = checkedContainsTrace(x, y, z)
            if (tracer == null) {
                return
            }
            count++
        }

        this.leakTrace = ArrayList<BlockVec3?>()
        this.leakTrace!!.add(start)
        for (j: Int in route.indices.reversed()) {
            this.leakTrace!!.add(route.get(j))
        }
    }

    private fun canBlockPassAirCheck(block: Block, vec: BlockVec3?, side: Int): Boolean {
        if (block is IPartialSealableBlock) {
            val testSide: EnumFacing? = EnumFacing.from3DDataValue(side)
            val blockPartial: IPartialSealableBlock = block as IPartialSealableBlock
            val vecPos: BlockPos = BlockPos(vec!!.x, vec.y, vec.z)
            if (blockPartial.isSealed(this.world, vecPos, testSide)) {
                // If a partial block checks as solid, allow it to be tested
                // again from other directions
                // This won't cause an endless loop, because the block won't
                // be included in nextLayer if it checks as solid
                this.checkCount--
                return false
            }

            //Find the solid sides so they don't get iterated into, when doing the next layer
            for (face: EnumFacing in EnumFacing.values()) {
                if (face == testSide) {
                    continue
                }
                if (blockPartial.isSealed(this.world, vecPos, face)) {
                    vec.setSideDone(face.getIndex() xor 1)
                }
            }
            checkedAdd(vec)
            return true
        }

        //Check leaves first, because their isOpaqueCube() test depends on graphics settings
        //(See net.minecraft.block.BlockLeaves.isOpaqueCube()!)
        if (block is BlockLeaves) {
            checkedAdd(vec)
            return true
        }

        val state: IBlockState = world!!.getBlockState(vec!!.toBlockPos())
        if (block.isOpaqueCube(state)) {
            checkedAdd(vec)
            //Gravel, wool and sponge are porous
            return block is BlockGravel || block.getMaterial(state) === Material.CLOTH || block is BlockSponge
        }

        if (block is BlockGlass || block is BlockStainedGlass) {
            checkedAdd(vec)
            return false
        }

        //Solid but non-opaque blocks, for example special glass
        if (OxygenPressureProtocol.nonPermeableBlocks.containsKey(block)) {
            val metaList: ArrayList<Int> = OxygenPressureProtocol.nonPermeableBlocks.get(block)!!
            if (metaList.contains(-1) || metaList.contains(vec.getBlockMetadata((this.world)!!))) {
                checkedAdd(vec)
                return false
            }
        }

        if (block is BlockUnlitTorch) {
            this.torchesToUpdate!!.add(vec)
            checkedAdd(vec)
            return true
        }

        //Half slab seals on the top side or the bottom side according to its metadata
        if (block is BlockSlab) {
            val isTopSlab: Boolean = (vec.getBlockMetadata((this.world)!!) and 8) == 8
            //Looking down onto a top slab or looking up onto a bottom slab
            if (side == 0 && isTopSlab || side == 1 && !isTopSlab) {
                //Sealed from that solid side but allow other sides still to be checked
                this.checkCount--
                return false
            }
            //Not sealed
            vec.setSideDone(if (isTopSlab) 1 else 0)
            checkedAdd(vec)
            return true
        }

        //Farmland etc only seals on the solid underside
        if (block is BlockFarmland || block is BlockEnchantmentTable || block is BlockLiquid) {
            if (side == 1) {
                //Sealed from the underside but allow other sides still to be checked
                this.checkCount--
                return false
            }
            //Not sealed
            vec.setSideDone(0)
            checkedAdd(vec)
            return true
        }

        if (block is BlockPistonBase) {
            val piston: BlockPistonBase = block
            if (state.getValue(BlockPistonBase.EXTENDED)) {
                val facing: Int = (state.getValue(BlockPistonBase.FACING) as EnumFacing).getIndex()
                if (side == facing) {
                    this.checkCount--
                    return false
                }
                vec.setSideDone(facing xor 1)
                checkedAdd(vec)
                return true
            }
            checkedAdd(vec)
            return false
        }

        //General case - this should cover any block which correctly implements isBlockSolidOnSide
        //including most modded blocks - Forge microblocks in particular is covered by this.
        // ### Any exceptions in mods should implement the IPartialSealableBlock interface ###
        if (block.isSideSolid(state, this.world, vec.toBlockPos(), EnumFacing.from3DDataValue(side xor 1))) {
            //Solid on all sides
            if (block.getMaterial(state).blocksMovement() && block.isFullCube(state)) {
                checkedAdd(vec)
                return false
            }
            //Sealed from this side but allow other sides still to be checked
            this.checkCount--
            return false
        }

        //Easy case: airblock, return without checking other sides
        if (block.getMaterial(state) === Material.AIR) {
            checkedAdd(vec)
            return true
        }

        //Not solid on that side.
        //Look to see if there is any other side which is solid in which case a check will not be needed next time
        for (i: Int in 0..5) {
            if (i == (side xor 1)) {
                continue
            }
            if (block.isSideSolid(state, this.world, BlockPos(vec.x, vec.y, vec.z), EnumFacing.from3DDataValue(i))) {
                vec.setSideDone(i)
            }
        }

        //Not solid from this side, so this is not sealed
        checkedAdd(vec)
        return true
    }

    open class intBucket {
        private var maxSize = 64 //default size
        private var size = 0
        private var table = IntArray(maxSize)

        open fun add(i: Int) {
            if (this.contains(i)) return

            if (size >= maxSize) {
                val newTable: IntArray = IntArray(maxSize + maxSize)
                System.arraycopy(table, 0, newTable, 0, maxSize)
                table = newTable
                maxSize += maxSize
            }
            table[size] = i
            size++
            checkedSize++
        }

        open fun contains(test: Int): Boolean {
            for (i: Int in size - 1 downTo 0) {
                if ((table[i] and 0xFFFFFFF) == test) return true
            }
            return false
        }

        open fun getMSB4shifted(test: Int): Int {
            for (i: Int in size - 1 downTo 0) {
                if ((table[i] and 0xFFFFFFF) == test) {
                    return (table[i] and -0x10000000) shr 22
                }
            }
            return -1
        }

        open fun clear() {
            size = 0
        }

        open fun size(): Int {
            return size
        }

        open fun contents(): IntArray {
            return table
        }
    }

    companion object {
        @JvmField
        var anylooping: AtomicBoolean = AtomicBoolean()
        private var buckets: Array<intBucket>
        private var checkedSize: Int = 0

        init {
            buckets = (arrayOfNulls<intBucket>(256) as Array<ThreadFindSeal.intBucket>)
            checkedInit()
        }

        @JvmStatic
        private fun checkedInit() {
            for (i: Int in 0..255) {
                buckets[i] = intBucket()
            }
        }

        @JvmStatic
        private fun checkedClear() {
            for (i: Int in 0..255) {
                buckets[i].clear()
            }
            checkedSize = 0
        }
    }
}
