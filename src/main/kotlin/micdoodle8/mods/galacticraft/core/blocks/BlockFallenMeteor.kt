package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityFallenMeteor
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.Random
import kotlin.math.min

open class BlockFallenMeteor constructor(assetName: String?) : Block(Material.ROCK), ITileEntityProvider,
    IShiftDescription, ISortableBlock {
    init {
        this.setHardness(40.0f)
        this.setSoundType(SoundType.STONE)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return BOUNDS
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    override fun quantityDroppedWithBonus(par1: Int, par2Random: Random): Int {
        return 1 + (par2Random.nextFloat() + 0.75f).toInt()
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return (GCItems.meteoricIronRaw)!!
    }

    public override fun onEntityCollision(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState?,
        entityIn: Entity?
    ) {
        val tile: TileEntity? = worldIn.getTileEntity(pos)

        if (tile is TileEntityFallenMeteor) {
            val meteor: TileEntityFallenMeteor = tile

            if (meteor.getHeatLevel() <= 0) {
                return
            }

            if (entityIn is EntityLivingBase) {
                val livingEntity: EntityLivingBase = entityIn

                worldIn.playSound(
                    null,
                    (pos.getX() + 0.5f).toDouble(),
                    (pos.getY() + 0.5f).toDouble(),
                    (pos.getZ() + 0.5f).toDouble(),
                    SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                    SoundCategory.NEUTRAL,
                    0.5f,
                    2.6f + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8f
                )

                for (var5: Int in 0..7) {
                    worldIn.spawnParticle(
                        EnumParticleTypes.SMOKE_LARGE,
                        pos.getX() + Math.random(),
                        pos.getY() + 0.2 + Math.random(),
                        pos.getZ() + Math.random(),
                        0.0,
                        0.0,
                        0.0
                    )
                }

                if (!livingEntity.isBurning()) {
                    livingEntity.setFire(2)
                }

                var var9: Double = pos.getX() + 0.5f - livingEntity.posX
                var var7: Double

                var7 = livingEntity.posZ - pos.getZ()
                while (var9 * var9 + var7 * var7 < 1.0E-4) {
                    var9 = (Math.random() - Math.random()) * 0.01
                    var7 = (Math.random() - Math.random()) * 0.01
                }

                livingEntity.knockBack(livingEntity, 1f, var9, var7)
            }
        }
    }

    override fun onBlockPlacedBy(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        itemStack: ItemStack
    ) {
        world.setBlockState(pos, this.getDefaultState(), 3)
        val tile: TileEntity? = world.getTileEntity(pos)

        if (tile is TileEntityFallenMeteor) {
            tile.setHeatLevel(0)
        }
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            this.tryToFall(worldIn, pos, state)
        }
    }

    private fun tryToFall(world: World, pos: BlockPos, state: IBlockState) {
        if (this.canFallBelow(world, pos.down()) && pos.getY() >= 0) {
            val prevHeatLevel: Int = (world.getTileEntity(pos) as TileEntityFallenMeteor).getHeatLevel()
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3)
            var blockpos1: BlockPos

            blockpos1 = pos.down()
            while (this.canFallBelow(world, blockpos1) && blockpos1.getY() > 0) {
                blockpos1 = blockpos1.down()
            }

            if (blockpos1.getY() >= 0) {
                world.setBlockState(blockpos1.up(), state, 3)
                (world.getTileEntity(blockpos1.up()) as TileEntityFallenMeteor).setHeatLevel(prevHeatLevel)
            }
        }
    }

    private fun canFallBelow(world: World, pos: BlockPos): Boolean {
        val block: Block = world.getBlockState(pos).getBlock()

        if (block.getMaterial(world.getBlockState(pos)) === Material.AIR) {
            return true
        } else if (block === Blocks.FIRE) {
            return true
        } else {
            return if (block.getMaterial(world.getBlockState(pos)) === Material.WATER) true else block.getMaterial(
                world.getBlockState(
                    pos
                )
            ) === Material.LAVA
        }
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityFallenMeteor()
    }

    override fun canSilkHarvest(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Boolean {
        return true
    }

    override fun getPlayerRelativeBlockHardness(
        state: IBlockState,
        player: EntityPlayer,
        world: World,
        pos: BlockPos
    ): Float {
        val hardness: Float = this.getBlockHardness(state, world, pos)
        if (hardness < 0.0f) {
            return 0.0f
        }

        val power: Int = canHarvestBlock(this, player, state)
        if (power > 0) {
            return power * player.getDigSpeed(state, pos) / hardness / 30f
        } else {
            return player.getDigSpeed(state, pos) / hardness / 30f
        }
    }

    open fun canHarvestBlock(block: Block, player: EntityPlayer, state: IBlockState): Int {
        val stack: ItemStack = player.inventory.getCurrentItem()
        val tool: String? = block.getHarvestTool(state)
        if (stack.isEmpty() || tool == null) {
            return if (player.canHarvestBlock(state)) 1 else 0
        }

        val toolLevel: Int =
            stack.getItem().getHarvestLevel(stack, tool, player, state) - block.getHarvestLevel(state) + 1
        if (toolLevel < 1) {
            return if (player.canHarvestBlock(state)) 1 else 0
        }

        return toolLevel
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        if (state.getBlock() !== this) return 0

        val rand: Random = if (world is World) world.rand else Random()
        return MathHelper.getInt(rand, 3, 7)
    }

    companion object {
        private val BOUNDS = AxisAlignedBB(0.15, 0.05, 0.15, 0.85, 0.75, 0.85)

        @JvmStatic
        fun colorMultiplier(worldIn: IBlockAccess?, pos: BlockPos?): Int {
            if (worldIn != null && pos != null) {
                val tile: TileEntity? = worldIn.getTileEntity(pos)

                if (tile is TileEntityFallenMeteor) {
                    val meteor: TileEntityFallenMeteor = tile

                    val col: Vector3 = Vector3(198.0, 108.0, 58.0)
                    col.translate((200 - meteor.getScaledHeatLevel() * 200).toDouble())
                    col.x = min(255.0, col.x)
                    col.y = min(255.0, col.y)
                    col.z = min(255.0, col.z)

                    return ColorUtil.to32BitColor(
                        255,
                        col.x.toInt().toByte().toInt(),
                        col.y.toInt().toByte().toInt(),
                        col.z.toInt().toByte().toInt()
                    )
                }
            }

            return 16777215
        }
    }
}
