package micdoodle8.mods.galacticraft.core.entities.player

import com.mojang.authlib.GameProfile
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.client.EventHandlerClient
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class GCEntityOtherPlayerMP constructor(par1World: World, profile: GameProfile) :
    EntityOtherPlayerMP(par1World, profile) {
    private var checkedCape = false
    private var galacticraftCape: ResourceLocation? = null

    override fun getLocationCape(): ResourceLocation? {
        if (this.getRidingEntity() is EntitySpaceshipBase) {
            // Don't draw any cape if riding a rocket (the cape renders outside the rocket model!)
            return null
        }

        val vanillaCape: ResourceLocation? = super.getLocationCape()

        if (!this.checkedCape) {
            val networkplayerinfo: NetworkPlayerInfo? = this.getPlayerInfo()
            this.galacticraftCape = ClientProxyCore.capeMap.get(networkplayerinfo!!.getGameProfile().getName())
            this.checkedCape = true
        }

        if ((ConfigManagerCore.overrideCapes || vanillaCape == null) && galacticraftCape != null) {
            return galacticraftCape
        }

        return vanillaCape
    }

    @SideOnly(Side.CLIENT)
    override fun getBrightnessForRender(): Int {
        var height: Double = this.posY + this.getEyeHeight().toDouble()
        if (height > 255.0) height = 255.0
        val blockpos: BlockPos = BlockPos(this.posX, height, this.posZ)
        return if (this.world.isBlockLoaded(blockpos)) this.world.getCombinedLight(blockpos, 0) else 0
    }

    override fun isSneaking(): Boolean {
        if (EventHandlerClient.sneakRenderOverride && this.world.provider !is IZeroGDimension) {
            if (this.onGround && this.inventory.getCurrentItem() != null && this.inventory.getCurrentItem()
                    .getItem() is IHoldableItem && (this.getRidingEntity() !is ICameraZoomEntity)
            ) {
                val holdableItem: IHoldableItem = this.inventory.getCurrentItem().getItem() as IHoldableItem

                if (holdableItem.shouldCrouch(this)) {
                    return true
                }
            }
        }
        return super.isSneaking()
    }
}
