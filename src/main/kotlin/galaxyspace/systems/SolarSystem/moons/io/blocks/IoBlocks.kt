package galaxyspace.systems.SolarSystem.moons.io.blocks

import galaxyspace.GalaxySpace
import galaxyspace.api.block.IEnergyGeyser
import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class IoBlocks constructor() : Block(Material.ROCK), ISortableBlock, ITerraformableBlock, IEnergyGeyser {
    init {
        this.setTranslationKey("ioblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
        this.setTickRandomly(true)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumIoBlocks in EnumIoBlocks.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun getCollisionBoundingBox(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        if (state === state.withProperty(
                BASIC_TYPE,
                EnumIoBlocks.IO_ASH
            )
        ) return SOUL_SAND_AABB

        return super.getCollisionBoundingBox(state, worldIn, pos)
    }

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
        when (state.getValue(BASIC_TYPE)) {
            EnumIoBlocks.IO_LAVA_GEYSER, EnumIoBlocks.IO_SULFUR_GEYSER -> world.scheduleBlockUpdate(
                pos,
                this,
                this.tickRate(world) + world.rand.nextInt(10),
                0
            )

            else -> {}
        }
    }

    override fun onEntityWalk(world: World, pos: BlockPos, entity: Entity) {
        if (world.getBlockState(pos) === world.getBlockState(pos)
                .withProperty(BASIC_TYPE, EnumIoBlocks.IO_LAVA_GEYSER)
        ) {
            if (entity is EntityLivingBase) entity.setFire(5)
        }

        if (world.getBlockState(pos) === world.getBlockState(pos)
                .withProperty(BASIC_TYPE, EnumIoBlocks.IO_SULFUR_GEYSER)
        ) {
            if (entity is EntityPlayer) entity.addPotionEffect(PotionEffect(MobEffects.POISON, 40 * 20))
        }
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!world.isAreaLoaded(pos, 16)) return

        if (state === state.withProperty(BASIC_TYPE, EnumIoBlocks.IO_LAVA_GEYSER)) {
            if (world.getBlockState(pos.down()).getMaterial() === Material.LAVA && !world.isBlockNormalCube(
                    pos.up(),
                    false
                ) && world.rand.nextInt(2) == 0
            ) {
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1)),
                        0.01,
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(200 + rand.nextInt(40), 4, false, Vector3(0.1, 0.1, 0.1), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1)),
                        0.01,
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(200 + rand.nextInt(40), 4, false, Vector3(0.1, 0.1, 0.1), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1)),
                        0.01,
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(200 + rand.nextInt(40), 6, false, Vector3(0.1, 0.1, 0.1), 1.0) as Array<Any?>?)
                )
                if (world.rand.nextInt(2) == 0) {
                    (GalaxySpace.proxy)!!.spawnParticle(
                        "waterbubbles",
                        Vector3(
                            pos.getX() + rand.nextDouble(),
                            pos.getY() + 1.0 + rand.nextDouble(),
                            pos.getZ() + rand.nextDouble()
                        ),
                        Vector3(
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1)),
                            0.8,
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1))
                        ),
                        (arrayOf<Any>(100 + rand.nextInt(40), 48, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                    )
                    (GalaxySpace.proxy)!!.spawnParticle(
                        "waterbubbles",
                        Vector3(
                            pos.getX() + rand.nextDouble(),
                            pos.getY() + 1.0 + rand.nextDouble(),
                            pos.getZ() + rand.nextDouble()
                        ),
                        Vector3(
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1)),
                            0.8,
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1))
                        ),
                        (arrayOf<Any>(100 + rand.nextInt(40), 48, true, Vector3(1.0, 1.0, 1.0), 2.0) as Array<Any?>?)
                    )
                    (GalaxySpace.proxy)!!.spawnParticle(
                        "waterbubbles",
                        Vector3(
                            pos.getX() + rand.nextDouble(),
                            pos.getY() + 1.0 + rand.nextDouble(),
                            pos.getZ() + rand.nextDouble()
                        ),
                        Vector3(
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1)),
                            0.8,
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1))
                        ),
                        (arrayOf<Any>(100 + rand.nextInt(40), 48, true, Vector3(1.0, 1.0, 1.0), 2.0) as Array<Any?>?)
                    )
                    (GalaxySpace.proxy)!!.spawnParticle(
                        "waterbubbles",
                        Vector3(
                            pos.getX() + rand.nextDouble(),
                            pos.getY() + 1.0 + rand.nextDouble(),
                            pos.getZ() + rand.nextDouble()
                        ),
                        Vector3(
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1)),
                            1.2 + rand.nextDouble(),
                            0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1))
                        ),
                        (arrayOf<Any>(100 + rand.nextInt(40), 48, true, Vector3(1.0, 1.0, 1.0), 2.0) as Array<Any?>?)
                    )
                }
            }
            world.scheduleBlockUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(10) + 10, 0)
        }

        if (state === state.withProperty(BASIC_TYPE, EnumIoBlocks.IO_SULFUR_GEYSER)) {
            var flag: Boolean = false
            for (i: Int in 0..14) {
                if (world.getBlockState(pos.down(i)).getBlock() === (VenusModule.sulphuricAcid)!!.getBlock()) {
                    flag = true
                    break
                }
            }

            if (world.isAirBlock(pos.up()) && flag && world.rand.nextInt(10) == 0) {
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1)),
                        0.25,
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(20, 4, false, Vector3(0.6, 0.8, 0.2), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1)),
                        0.15,
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(10, 5, false, Vector3(0.6, 0.8, 0.2), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1)),
                        0.15,
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(10, 6, false, Vector3(0.6, 0.8, 0.2), 1.0) as Array<Any?>?)
                )
            }

            world.scheduleBlockUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(10) + 10, 0)
        }
    }

    override fun onEntityCollision(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        if (state === state.withProperty(BASIC_TYPE, EnumIoBlocks.IO_ASH)) {
            entityIn.motionX *= 0.4
            entityIn.motionZ *= 0.4
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        when (state.getValue(BASIC_TYPE)) {
            EnumIoBlocks.IO_SULFUR_ORE, EnumIoBlocks.IO_VOLCANIC_ORE -> return GSItems.BASIC
            else -> return Item.getItemFromBlock(state.getBlock())
        }
    }

    override fun getDrops(
        drops: NonNullList<ItemStack>,
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ) {
        val rand: Random = if (world is World) world.rand else RANDOM

        val count: Int = quantityDropped(state, fortune, rand)
        drops.clear()
        for (i: Int in 0..0) {
            val item: Item = this.getItemDropped(state, rand, fortune)
            if (item !== Items.AIR) {
                when (state.getValue(BASIC_TYPE)) {
                    EnumIoBlocks.IO_SULFUR_ORE -> drops.add(ItemStack(item, 1, 7))
                    EnumIoBlocks.IO_VOLCANIC_ORE -> drops.add(ItemStack(item, 1, 12))
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (state.getValue(BASIC_TYPE)) {
            EnumIoBlocks.IO_SULFUR_ORE -> return 7
            EnumIoBlocks.IO_VOLCANIC_ORE -> return 12
            else -> return getMetaFromState(state)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumIoBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        IO_GRUNT(0, "io_grunt"),
        IO_STONE(1, "io_stone"),
        IO_ASH(2, "io_ash"),
        IO_COPPER_ORE(3, "io_copper_ore"),
        IO_SULFUR_ORE(4, "io_sulfur_ore"),
        IO_VOLCANIC_ORE(5, "io_volcanic_ore"),
        IO_LAVA_GEYSER(6, "io_lava_geyser"),
        IO_SULFUR_GEYSER(7, "io_sulfur_geyser"),
        IO_DUNGEON_TOP(8, "io_top"),
        IO_DUNGEON_FLOOR(9, "io_floor"),
        IO_DUNGEON_BRICKS(10, "io_dungeon_bricks");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumIoBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumIoBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumIoBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumIoBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        if ((world)!!.getBlockState(pos).getBlock().getMetaFromState((world)!!.getBlockState(pos)) <= 2) return true
        return false
    }

    override fun isWorkGeyser(world: World?, state: IBlockState?, pos: BlockPos?): Boolean {
        if (state === (state)!!.withProperty(BASIC_TYPE, EnumIoBlocks.IO_SULFUR_GEYSER)) {
            var flag: Boolean = false
            for (i: Int in 0..14) {
                if ((world)!!.getBlockState((pos)!!.down(i)).getBlock() === (VenusModule.sulphuricAcid)!!.getBlock()) {
                    flag = true
                    break
                }
            }
            return flag
        }
        if (state === (state)!!.withProperty(
                BASIC_TYPE,
                EnumIoBlocks.IO_LAVA_GEYSER
            )
        ) return (world)!!.getBlockState((pos)!!.down()).getBlock() === FluidRegistry.LAVA.getBlock()
        return false
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumIoBlocks> =
            PropertyEnum.create<EnumIoBlocks>("type", EnumIoBlocks::class.java)

        @JvmField
        protected val SOUL_SAND_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9, 1.0)
    }
}
