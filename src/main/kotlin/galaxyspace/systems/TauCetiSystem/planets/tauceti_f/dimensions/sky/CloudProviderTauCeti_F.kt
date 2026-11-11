package galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.sky

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.sky.CloudProviderTauCeti_F
import asmodeuscore.core.astronomy.sky.CustomCloudRender
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d

open class CloudProviderTauCeti_F constructor() : CustomCloudRender(f) {
    public override fun getCloudMovementSpeed(world: WorldClient?): Float {
        return 1f
    }

    public override fun getCloudTexture(): ResourceLocation {
        return ResourceLocation.parse("textures/environment/clouds.png")
    }

    public override fun getCloudColor(renderPartialTicks: Float): Vec3d {
        return Vec3d(150.0, 150.0, 150.0)
    }

    companion object {
        @JvmField
        var f: FloatArray = floatArrayOf(1.0f)
    }
}
