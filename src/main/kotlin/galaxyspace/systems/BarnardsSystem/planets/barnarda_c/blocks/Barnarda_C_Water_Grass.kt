package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import galaxyspace.systems.BarnardsSystem.core.BRItems
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Water_Grass
import net.minecraft.block.BlockBush
import net.minecraft.block.BlockLiquid
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.item.EntityBoat
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class Barnarda_C_Water_Grass constructor() : BlockBush(Material.VINE), IPlantable {
    init {
        this.setTranslationKey("barnarda_c_water_grass")
        this.setSoundType(SoundType.PLANT)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return WATER_GRASS_AABB
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return NULL_AABB
    }


    override fun onEntityCollision(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        if (!worldIn.isClientSide) {
            if (entityIn is EntityBoat) {
                worldIn.destroyBlock(BlockPos(pos), true)
            }

            if (entityIn is EntityLivingBase) {
                val living: EntityLivingBase = entityIn

                living.addEffect(PotionEffect(MobEffects.SLOWNESS, 10, 3))
            }
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(BRItems.BASIC, 1, 1)
    }

    override fun canSustainBush(state: IBlockState): Boolean {
        return state.getBlock() === Blocks.WATER || state.getMaterial() === Material.ICE
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            val iblockstate: IBlockState = worldIn.getBlockState(pos.below())
            val material: Material = iblockstate.getMaterial()
            return material === Material.WATER && (iblockstate.getValue(BlockLiquid.LEVEL)) == 0 || material === Material.ICE
        } else {
            return false
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        return null
    }

    override fun damageDropped(state: IBlockState): Int {
        return 0
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    override fun getPlantType(world: IBlockAccess, pos: BlockPos): EnumPlantType {
        return EnumPlantType.Water
    }

    companion object {
        @JvmField
        protected val WATER_GRASS_AABB: AxisAlignedBB = AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.09375, 0.9375)
    }
}
