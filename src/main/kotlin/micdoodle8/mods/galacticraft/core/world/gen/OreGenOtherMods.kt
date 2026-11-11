package micdoodle8.mods.galacticraft.core.world.gen

import foundation.level.offset

import micdoodle8.mods.galacticraft.api.event.wgen.GCCoreEventPopulate
import micdoodle8.mods.galacticraft.api.vector.BlockTuple
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars
import net.minecraft.world.level.block.Block
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import java.lang.Exception
import java.util.ArrayList
import java.util.Locale
import java.util.Random

open class OreGenOtherMods {
    private var world: World? = null
    private var randomGenerator: Random? = null

    private var pos: BlockPos? = null

    private var oreGen: WorldGenerator? = null

    @SubscribeEvent
    open fun onPlanetDecorated(event: GCCoreEventPopulate.Post) {
        this.world = event.world
        this.randomGenerator = event.rand
        this.pos = event.pos

        var dimDetected: Int = 0

        val prov: WorldProvider? = world!!.provider
        if (prov !is IGalacticraftWorldProvider || (prov is WorldProviderSpaceStation)) {
            return
        }

        var stoneBlock: Block? = null
        var stoneMeta: Int = 0

        if (prov is WorldProviderMoon) {
            stoneBlock = GCBlocks.blockMoon
            stoneMeta = 4
            dimDetected = 1
        } else if (GalacticraftCore.isPlanetsLoaded && prov is WorldProviderMars) {
            stoneBlock = MarsBlocks.marsBlock
            stoneMeta = 9
            dimDetected = 2
        }

        if (stoneBlock == null) {
            return
        }

        for (ore: OreGenData in data) {
            if (ore.dimRestrict == 0 || ore.dimRestrict == dimDetected) {
                this.oreGen =
                    WorldGenMinableMeta(ore.oreBlock, ore.sizeCluster, ore.oreMeta, true, stoneBlock, stoneMeta)
                this.genStandardOre1(ore.numClusters, this.oreGen!!, ore.minHeight, ore.maxHeight)
            }
        }
    }

    open fun genStandardOre1(amountPerChunk: Int, worldGenerator: WorldGenerator, minY: Int, maxY: Int) {
        for (var5: Int in 0..<amountPerChunk) {
            val blockpos: BlockPos = this.pos!!.offset(
                this.randomGenerator!!.nextInt(16),
                this.randomGenerator!!.nextInt(maxY - minY) + minY,
                this.randomGenerator!!.nextInt(16)
            )
            worldGenerator.generate(this.world, this.randomGenerator, blockpos)
        }
    }

    open class OreGenData {
        @JvmField
        var oreBlock: Block = (GCBlocks.blockMoon)!!

        @JvmField
        var oreMeta: Int = 0

        @JvmField
        var sizeCluster: Int = 4

        @JvmField
        var numClusters: Int = 8

        @JvmField
        var minHeight: Int = 0

        @JvmField
        var maxHeight: Int = 128

        @JvmField
        var dimRestrict: Int = 0

        constructor(block: Block, meta: Int, num: Int, cluster: Int, min: Int, max: Int, dim: Int) {
            this.oreBlock = block
            this.oreMeta = meta
            this.sizeCluster = cluster
            this.numClusters = num
            this.minHeight = min
            this.maxHeight = max
            this.dimRestrict = dim
        }

        constructor(block: Block, meta: Int, num: Int, cluster: Int) {
            this.oreBlock = block
            this.oreMeta = meta
            this.sizeCluster = cluster
            this.numClusters = num
            this.minHeight = 0
            this.maxHeight = 128
        }

        constructor(block: Block, meta: Int, num: Int) {
            this.oreBlock = block
            this.oreMeta = meta
            this.sizeCluster = 4
            this.numClusters = num
            this.minHeight = 0
            this.maxHeight = 128
        }
    }

    companion object {
        @JvmField
        var data: ArrayList<OreGenData> = ArrayList<OreGenData>()

        init {
            for (str in (ConfigManagerCore.oregenIDs)!!) {
                try {
                    val slash: Int = (str)!!.indexOf('/')
                    val s: String
                    var rarity: Int = 0 //0 = common  1 = uncommon  2 = rare
                    var depth: Int = 0 //0 = even   1 = deep   2 = shallow
                    var size: Int = 1 //0 = single 1 = standard 2 = large
                    var extraRandom: Boolean = false
                    var dim: Int = 0

                    if (slash >= 0) {
                        s = (str)!!.substring(0, slash).trim { it <= ' ' }
                        val params: String = (str)!!.substring(slash).uppercase(Locale.getDefault())
                        if (params.contains("UNCOMMON")) {
                            rarity = 1
                        } else if (params.contains("RARE")) {
                            rarity = 2
                        }

                        if (params.contains("DEEP")) {
                            depth = 1
                        } else if (params.contains("SHALLOW")) {
                            depth = 2
                        }

                        if (params.contains("SINGLE")) {
                            size = 0
                        } else if (params.contains("LARGE")) {
                            size = 2
                        }

                        if (params.contains("XTRARANDOM")) {
                            extraRandom = true
                        }

                        if (params.contains("ONLYMOON")) {
                            dim = 1
                        } else if (params.contains("ONLYMARS")) {
                            dim = 2
                        }
                    } else {
                        s = (str)!!
                    }

                    val bt: BlockTuple? = ConfigManagerCore.stringToBlock(s, "Other mod ore generate IDs", true)
                    if (bt == null) {
                        continue
                    }

                    var meta: Int = bt.meta
                    if (meta == -1) {
                        meta = 0
                    }

                    addOre(bt.block, meta, rarity, depth, size, extraRandom, dim)
                } catch (e: Exception) {
                    GCLog.severe("[config] External Sealable IDs: error parsing '" + str + "'. Must be in the form Blockname or BlockName:metadata followed by / parameters ")
                }
            }
        }

        @JvmStatic
        fun addOre(block: Block, meta: Int, rarity: Int, depth: Int, clumpSize: Int, extraRandom: Boolean, dim: Int) {
            var clusters: Int = 12
            var size: Int = 4
            var min: Int = 0
            var max: Int = 64

            when (depth) {
                0 -> {
                    //Evenly distributed
                    size = 6
                    clusters = 20
                    max = 80
                    if (rarity == 1) {
                        clusters = 9
                        size = 4
                    } else if (rarity == 2) {
                        clusters = 6
                        size = 3
                        max = 96
                    }
                }

                1 -> {
                    //Deep
                    size = 5
                    clusters = 12
                    max = 32
                    if (rarity == 1) {
                        clusters = 6
                        size = 4
                        max = 20
                    } else if (rarity == 2) {
                        clusters = 2
                        size = 3
                        max = 16
                    }
                }

                2 -> {
                    //Shallow
                    size = 6
                    clusters = 15
                    min = 32
                    max = 80
                    if (rarity == 1) {
                        clusters = 8
                        size = 4
                        min = 32
                        max = 72
                    } else if (rarity == 2) {
                        clusters = 3
                        size = 3
                        min = 40
                        max = 64
                    }
                }
            }

            if (clumpSize == 0) {
                size = 1
                clusters = (3 * clusters) / 2
            } else if (clumpSize == 2) {
                size *= 4
                clusters /= 2
            }

            if (extraRandom) {
                if (depth == 1) {
                    min = -max * 3
                } else {
                    max *= 4
                }
            }

            val ore: OreGenData = OreGenData(block, meta, clusters, size, min, max, dim)
            data.add(ore)
        }
    }
}
