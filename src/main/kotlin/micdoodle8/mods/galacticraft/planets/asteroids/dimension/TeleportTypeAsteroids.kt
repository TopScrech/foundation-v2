package micdoodle8.mods.galacticraft.planets.asteroids.dimension

import foundation.item.FoundationItemVariants
import foundation.block.getStateFromMeta

import java.util.Random

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.world.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.passive.EntityCow
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.init.PotionTypes
import net.minecraft.item.ItemMonsterPlacer
import net.minecraft.world.item.ItemStack
import net.minecraft.potion.PotionUtils
import net.minecraft.core.NonNullList
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.gen.ChunkProviderServer
import java.util.*
import kotlin.math.abs

open class TeleportTypeAsteroids : ITeleportType {
    override fun useParachute(): Boolean {
        return false
    }

    override fun getPlayerSpawnLocation(world: WorldServer?, player: EntityPlayerMP?): Vector3 {
        if (player != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            var x: Int = MathHelper.floor(stats.getCoordsTeleportedFromX())
            var z: Int = MathHelper.floor(stats.getCoordsTeleportedFromZ())
            val limit: Int = ConfigManagerCore.otherPlanetWorldBorders - 2
            if (limit > 20) {
                if (x > limit) {
                    z *= limit / x
                    x = limit
                } else if (x < -limit) {
                    z *= -limit / x
                    x = -limit
                }
                if (z > limit) {
                    x *= limit / z
                    z = limit
                } else if (z < -limit) {
                    x *= -limit / z
                    z = -limit
                }
            }

            var attemptCount: Int = 0

            //Small pre-generate with a chunk loading radius of 3, to make sure some asteroids get generated
            //(if the world is already generated here, this will be very quick)
            this.preGenChunks((world)!!, x shr 4, z shr 4)

            do {
                var bv3: BlockVec3? = null
                if ((world)!!.provider is WorldProviderAsteroids) {
                    bv3 = ((world)!!.provider as WorldProviderAsteroids).getClosestAsteroidXZ(x, 0, z, true)
                }

                if (bv3 != null) {
                    //Check whether the returned asteroid is too far from the desired entry location in which case, give up
                    if (bv3.distanceSquared(BlockVec3(x, 128, z)) > 25600) {
                        break
                    }

                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Testing asteroid at x" + (bv3.x) + " y" + (bv3.y) + " z" + bv3.z)
                    }
                    this.loadChunksAround(bv3.x, bv3.z, 2, (world)!!.getChunkProvider())
                    this.loadChunksAround(bv3.x, bv3.z, -3, (world)!!.getChunkProvider())

                    if (goodAsteroidEntry((world)!!, bv3.x, bv3.y, bv3.z)) {
                        return Vector3(bv3.x.toDouble(), 310.0, bv3.z.toDouble())
                    }
                    if (goodAsteroidEntry((world)!!, bv3.x + 2, bv3.y, bv3.z + 2)) {
                        return Vector3((bv3.x + 2).toDouble(), 310.0, (bv3.z + 2).toDouble())
                    }
                    if (goodAsteroidEntry((world)!!, bv3.x + 2, bv3.y, bv3.z - 2)) {
                        return Vector3((bv3.x + 2).toDouble(), 310.0, (bv3.z - 2).toDouble())
                    }
                    if (goodAsteroidEntry((world)!!, bv3.x - 2, bv3.y, bv3.z - 2)) {
                        return Vector3((bv3.x - 2).toDouble(), 310.0, (bv3.z - 2).toDouble())
                    }
                    if (goodAsteroidEntry((world)!!, bv3.x - 2, bv3.y, bv3.z + 2)) {
                        return Vector3((bv3.x - 2).toDouble(), 310.0, (bv3.z + 2).toDouble())
                    }

                    //Failed to find an asteroid even though there should be one there
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Removing drilled out asteroid at x" + (bv3.x) + " z" + (bv3.z))
                    }
                    ((world)!!.provider as WorldProviderAsteroids).removeAsteroid(bv3.x, bv3.y, bv3.z)
                }

                attemptCount++
            } while (attemptCount < 5)

            GCLog.info("Failed to find good large asteroid landing spot! Falling back to making a small one.")
            this.makeSmallLandingSpot((world)!!, x, z)
            return Vector3(x.toDouble(), 310.0, z.toDouble())
        }

        GCLog.severe("Null player when teleporting to Asteroids!")
        return Vector3(0.0, 310.0, 0.0)
    }

    private fun goodAsteroidEntry(world: World, x: Int, yorig: Int, z: Int): Boolean {
        for (k: Int in 208 downTo 49) {
            if (!world.isEmptyBlock(BlockPos(x, k, z))) {
                if (abs(k - yorig) > 20) {
                    continue
                }
                //Clear the downward path of small asteroids and any other asteroid rock
                for (y: Int in k + 2..255) {
                    if (world.getBlockState(BlockPos(x, y, z)).getBlock() === AsteroidBlocks.blockBasic) {
                        world.setBlockAndUpdate(BlockPos(x, y, z), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    }
                    if (world.getBlockState(BlockPos(x - 1, y, z)).getBlock() === AsteroidBlocks.blockBasic) {
                        world.setBlockAndUpdate(BlockPos(x - 1, y, z), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    }
                    if (world.getBlockState(BlockPos(x, y, z - 1)).getBlock() === AsteroidBlocks.blockBasic) {
                        world.setBlockAndUpdate(BlockPos(x, y, z - 1), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    }
                    if (world.getBlockState(BlockPos(x - 1, y, z - 1)).getBlock() === AsteroidBlocks.blockBasic) {
                        world.setBlockAndUpdate(BlockPos(x - 1, y, z - 1), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    }
                }
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("Found asteroid at x" + (x) + " z" + (z))
                }
                return true
            }
        }
        return false
    }

    private fun makeSmallLandingSpot(world: World, x: Int, z: Int) {
        this.loadChunksAround(x, z, -1, world.getChunkProvider() as ChunkProviderServer)

        for (k: Int in 255 downTo 49) {
            if (!world.isEmptyBlock(BlockPos(x, k, z))) {
                this.makePlatform(world, x, k - 1, z)
                return
            }
            if (!world.isEmptyBlock(BlockPos(x - 1, k, z))) {
                this.makePlatform(world, x - 1, k - 1, z)
                return
            }
            if (!world.isEmptyBlock(BlockPos(x - 1, k, z - 1))) {
                this.makePlatform(world, x - 1, k - 1, z - 1)
                return
            }
            if (!world.isEmptyBlock(BlockPos(x, k, z - 1))) {
                this.makePlatform(world, x, k - 1, z - 1)
                return
            }
        }

        this.makePlatform(world, x, 48 + world.random.nextInt(128), z)
        return
    }

    private fun loadChunksAround(x: Int, z: Int, i: Int, cp: ChunkProviderServer) {
        cp.loadChunk(x shr 4, z shr 4)
        if ((x + i) shr 4 != x shr 4) {
            cp.loadChunk((x + i) shr 4, z shr 4)
            if ((z + i) shr 4 != z shr 4) {
                cp.loadChunk(x shr 4, (z + i) shr 4)
                cp.loadChunk((x + i) shr 4, (z + i) shr 4)
            }
        } else if ((z + i) shr 4 != z shr 4) {
            cp.loadChunk(x shr 4, (z + i) shr 4)
        }
    }

    private fun makePlatform(world: World, x: Int, y: Int, z: Int) {
        for (xx: Int in -3..2) {
            for (zz: Int in -3..2) {
                if (xx == -3 && (zz == -3 || zz == 2)) {
                    continue
                }
                if (xx == 2 && (zz == -3 || zz == 2)) {
                    continue
                }
                doBlock(world, x + xx, y, z + zz)
            }
        }
        for (xx: Int in -2..1) {
            for (zz: Int in -2..1) {
                doBlock(world, x + xx, y - 1, z + zz)
            }
        }
        doBlock(world, x - 1, y - 2, z - 1)
        doBlock(world, x - 1, y - 2, z)
        doBlock(world, x, y - 2, z)
        doBlock(world, x, y - 2, z - 1)
    }

    private fun doBlock(world: World, x: Int, y: Int, z: Int) {
        val meta: Int = (world.random.nextFloat() * 1.5f).toInt()
        if (world.isEmptyBlock(BlockPos(x, y, z))) {
            world.setBlock(BlockPos(x, y, z), (AsteroidBlocks.blockBasic)!!.getStateFromMeta(meta), 2)
        }
    }

    override fun getEntitySpawnLocation(world: WorldServer?, entity: Entity?): Vector3 {
        return Vector3((entity)!!.x, if (ConfigManagerCore.disableLander) 250.0 else 900.0, (entity)!!.z)
    }

    override fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3? {
        return null
    }

    private fun preGenChunks(w: World, cx: Int, cz: Int) {
        this.preGenChunk(w, cx, cz)
        for (r: Int in 1..2) {
            val xmin: Int = cx - r
            val xmax: Int = cx + r
            val zmin: Int = cz - r
            val zmax: Int = cz + r
            for (i: Int in -r..<r) {
                this.preGenChunk(w, xmin, cz + i)
                this.preGenChunk(w, xmax, cz - i)
                this.preGenChunk(w, cx - i, zmin)
                this.preGenChunk(w, cx + i, zmax)
            }
        }
    }

    private fun preGenChunk(w: World, chunkX: Int, chunkZ: Int) {
        w.getChunk(chunkX, chunkZ)
    }

    override fun onSpaceDimensionChanged(newWorld: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean) {
        if (!ridingAutoRocket && player != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!

            if (stats.getTeleportCooldown() <= 0) {
                if (player.capabilities.isFlying) {
                    player.capabilities.isFlying = false
                }

                if (!(newWorld)!!.isClientSide) {
                    val entryPod: EntityEntryPod = EntityEntryPod(player)

                    CompatibilityManager.forceLoadChunks(newWorld as WorldServer)
                    entryPod.forceSpawn = true
                    newWorld.addFreshEntity(entryPod)
                    CompatibilityManager.forceLoadChunksEnd(newWorld)
                }

                stats.setTeleportCooldown(10)
            }
        }
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
        val stats: GCPlayerStats = (GCPlayerStats.get((player)!!))!!
        SchematicRegistry.unlockNewPage(player, foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1)) //Knows how to build T2 rocket
        SchematicRegistry.unlockNewPage(player, foundation.item.FoundationItemVariants.stack(requireNotNull(MarsItems.schematic), 1, 0)) //Knows how to build T3 rocket
        SchematicRegistry.unlockNewPage(player, foundation.item.FoundationItemVariants.stack(requireNotNull(MarsItems.schematic), 1, 2)) //Knows how to build Astro Miner
        val rocketStacks: NonNullList<ItemStack> = NonNullList.create<ItemStack>()
        stats.setFuelLevel(1000)
        rocketStacks.add(ItemStack(GCItems.oxMask))
        rocketStacks.add(ItemStack(GCItems.oxygenGear))
        rocketStacks.add(ItemStack(GCItems.oxTankMedium))
        rocketStacks.add(ItemStack(GCItems.oxTankHeavy))
        rocketStacks.add(ItemStack(GCItems.oxTankHeavy))
        rocketStacks.add(ItemStack(AsteroidsItems.canisterLOX))
        rocketStacks.add(ItemStack(AsteroidsItems.canisterLOX))
        rocketStacks.add(ItemStack(AsteroidsItems.canisterLOX))
        rocketStacks.add(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 32, 7))
        rocketStacks.add(ItemStack(Blocks.GLASS_PANE, 16))
        rocketStacks.add(ItemStack(Blocks.PLANKS, 32, 2))
        rocketStacks.add(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 16, 2)) //Desh ingot
        rocketStacks.add(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 8, 13)) //Basic Wafer
        rocketStacks.add(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 2, 1)) //Solar Panels
        rocketStacks.add(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 16, 0)) //Canned food
        rocketStacks.add(ItemStack(Items.EGG, 12))

        val spawnEgg: ItemStack = ItemStack(Items.SPAWN_EGG, 2)
        val name: ResourceLocation? = EntityList.getKey(EntityCow::class.java)
        ItemMonsterPlacer.applyEntityIdToItemStack(spawnEgg, name)
        rocketStacks.add(spawnEgg)
        rocketStacks.add(
            PotionUtils.addPotionToItemStack(
                ItemStack(Items.POTIONITEM, 4),
                PotionTypes.LONG_NIGHT_VISION
            )
        ) //Night Vision Potion
        rocketStacks.add(ItemStack(MarsBlocks.machine, 1, 4)) //Cryogenic Chamber
        rocketStacks.add(ItemStack(MarsItems.rocketMars, 1, IRocketType.EnumRocketType.INVENTORY36.ordinal))
        stats.setRocketStacks(rocketStacks)
    }
}
