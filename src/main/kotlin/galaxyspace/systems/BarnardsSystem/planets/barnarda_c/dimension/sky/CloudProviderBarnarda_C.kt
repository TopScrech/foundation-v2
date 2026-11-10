package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension.sky

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension.sky.CloudProviderBarnarda_C
import asmodeuscore.core.astronomy.sky.CloudProviderBase
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

open class CloudProviderBarnarda_C : CloudProviderBase() {
    override fun getCloudMovementSpeed(world: World?): Float {
        return if ((world)!!.thunderingStrength > 0) 4f else 1f
    }

    public override fun getCloudTexture(): ResourceLocation {
        return ResourceLocation("textures/environment/clouds.png")
    }

    public override fun getCloudColor(renderPartialTicks: Float): Vec3d {
        return Vec3d(150.0, 150.0, 150.0)
    }
}
