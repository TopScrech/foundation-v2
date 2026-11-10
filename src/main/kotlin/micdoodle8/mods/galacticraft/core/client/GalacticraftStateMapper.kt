package micdoodle8.mods.galacticraft.core.client

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.util.ResourceLocation

open class GalacticraftStateMapper : StateMapperBase() {
    override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
        var loc: ResourceLocation = Block.REGISTRY.getNameForObject(state.getBlock())
        loc = ResourceLocation(loc.getNamespace().replace("|", ""), loc.getPath())
        return ModelResourceLocation(loc, getPropertyString(state))
    }

    companion object {
        @JvmField
        val INSTANCE: GalacticraftStateMapper = GalacticraftStateMapper()

        @JvmStatic
        fun getPropertyString(state: IBlockState): String {
            return INSTANCE.getPropertyString(state.getProperties())
        }
    }
}
