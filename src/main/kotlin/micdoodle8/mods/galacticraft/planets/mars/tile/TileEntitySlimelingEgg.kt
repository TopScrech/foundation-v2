package micdoodle8.mods.galacticraft.planets.mars.tile

import java.util.UUID
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.management.PreYggdrasilConverter
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class TileEntitySlimelingEgg : TileEntity(), ITickable {
    @JvmField
    var timeToHatch: Int = -1

    @JvmField
    var lastTouchedPlayerUUID: String? = ""

    @JvmField
    var lastTouchedPlayerName: String = ""

    override fun update() {
        if (!this.world.isRemote) {
            if (this.timeToHatch > 0) {
                this.timeToHatch--
            } else if (this.timeToHatch == 0 && lastTouchedPlayerUUID != null && lastTouchedPlayerUUID!!.length > 0) {
                val state: IBlockState = this.world.getBlockState(this.getPos())
                val metadata: Int = state.getBlock().getMetaFromState(state) % 3

                var colorRed: Float = 0.0f
                var colorGreen: Float = 0.0f
                var colorBlue: Float = 0.0f

                when (metadata) {
                    0 -> colorRed = 1.0f
                    1 -> colorBlue = 1.0f
                    2 -> {
                        colorRed = 1.0f
                        colorGreen = 1.0f
                    }
                }

                val slimeling: EntitySlimeling = EntitySlimeling(this.world, colorRed, colorGreen, colorBlue)

                slimeling.setPosition(
                    this.getPos().getX() + 0.5,
                    this.getPos().getY() + 1.0,
                    this.getPos().getZ() + 0.5
                )
                slimeling.setOwnerId(UUID.fromString(this.lastTouchedPlayerUUID))
                slimeling.setOwnerUsername(this.lastTouchedPlayerName)

                if (!this.world.isRemote) {
                    this.world.spawnEntity(slimeling)
                }

                slimeling.setTamed(true)
                slimeling.getNavigator().clearPath()
                slimeling.setAttackTarget(null as EntityLivingBase?)
                slimeling.setHealth(20.0f)

                this.world.setBlockToAir(this.getPos())
            }
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.timeToHatch = nbt.getInteger("TimeToHatch")

        val uuid: String
        if (nbt.hasKey("OwnerUUID", 8)) {
            uuid = nbt.getString("OwnerUUID")
        } else {
            uuid =
                PreYggdrasilConverter.convertMobOwnerIfNeeded(this.world.getMinecraftServer(), nbt.getString("Owner"))
        }

        if (uuid.length > 0) {
            lastTouchedPlayerUUID = uuid
        }

        this.lastTouchedPlayerName = nbt.getString("OwnerUsername")
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("TimeToHatch", this.timeToHatch)
        nbt.setString("OwnerUUID", this.lastTouchedPlayerUUID)
        nbt.setString("OwnerUsername", this.lastTouchedPlayerName)
        return nbt
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
