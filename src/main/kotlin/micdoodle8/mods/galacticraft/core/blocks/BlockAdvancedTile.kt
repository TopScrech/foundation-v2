package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.tile.ILockable
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

abstract class BlockAdvancedTile(par3Material: Material?) : BlockAdvanced((par3Material)!!), ITileEntityProvider {
    init {
        this.hasTileEntity = true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return null
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        this.dropEntireInventory(worldIn, pos, state)
        super.breakBlock(worldIn, pos, state)
    }

    fun dropEntireInventory(worldIn: World, pos: BlockPos, state: IBlockState?) {
        val tileEntity: TileEntity? = worldIn.getTileEntity(pos)
        if (tileEntity is ILockable) {
            (tileEntity as ILockable).clearLockedInventory()
        }

        if (tileEntity != null) {
            if (tileEntity is IInventory) {
                val inventory: IInventory = tileEntity as IInventory

                val syncRandom: Random = GCCoreUtil.getRandom(pos)

                for (var6: Int in 0..<inventory.getSizeInventory()) {
                    val var7: ItemStack = inventory.getStackInSlot(var6)

                    if (var7 != null && !var7.isEmpty()) {
                        val var8: Float = syncRandom.nextFloat() * 0.8f + 0.1f
                        val var9: Float = syncRandom.nextFloat() * 0.8f + 0.1f
                        val var10: Float = syncRandom.nextFloat() * 0.8f + 0.1f

                        while (!var7.isEmpty()) {
                            val var12: EntityItem = EntityItem(
                                worldIn,
                                (pos.getX() + var8).toDouble(),
                                (pos.getY() + var9).toDouble(),
                                (pos.getZ() + var10).toDouble(),
                                var7.splitStack(syncRandom.nextInt(21) + 10)
                            )
                            val var13: Float = 0.05f
                            var12.motionX = (syncRandom.nextGaussian().toFloat() * var13).toDouble()
                            var12.motionY = (syncRandom.nextGaussian().toFloat() * var13 + 0.2f).toDouble()
                            var12.motionZ = (syncRandom.nextGaussian().toFloat() * var13).toDouble()
                            worldIn.spawnEntity(var12)
                        }
                    }
                }
            }
        }
    }
}
