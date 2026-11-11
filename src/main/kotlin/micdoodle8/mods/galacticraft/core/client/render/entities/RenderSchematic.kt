package micdoodle8.mods.galacticraft.core.client.render.entities

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import foundation.schematic.FoundationSchematicMesh
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth

class SchematicRenderState : EntityRenderState() {
    var direction: Direction = Direction.SOUTH
    var texture: Identifier? = null
    var lightCoordsPerBlock: IntArray = IntArray(4)
}

open class RenderSchematic(context: EntityRendererProvider.Context) : EntityRenderer<EntityHangingSchematic, SchematicRenderState>(context) {
    override fun createRenderState(): SchematicRenderState = SchematicRenderState()
    override fun extractRenderState(entity: EntityHangingSchematic, state: SchematicRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        state.direction = entity.direction
        state.texture = SchematicRegistry.getSchematicTexture(entity.schematic)
        for (j in 0..1) for (i in 0..1) {
            val offsetX = i - 0.5
            val y = Mth.floor(entity.y + j - 0.5)
            var x = entity.blockX
            var z = entity.blockZ
            when (state.direction) {
                Direction.NORTH -> x = Mth.floor(entity.x + offsetX)
                Direction.SOUTH -> x = Mth.floor(entity.x - offsetX)
                Direction.WEST -> z = Mth.floor(entity.z - offsetX)
                Direction.EAST -> z = Mth.floor(entity.z + offsetX)
                else -> Unit
            }
            state.lightCoordsPerBlock[i + j * 2] = LightCoordsUtil.getLightCoords(entity.level(), BlockPos(x, y, z))
        }
    }
    override fun submit(state: SchematicRenderState, poses: PoseStack, collector: SubmitNodeCollector, camera: CameraRenderState) {
        val texture = state.texture ?: return
        val lights = state.lightCoordsPerBlock.copyOf()
        poses.pushPose()
        poses.rotateDegrees(Axis.YP, 180f - state.direction.get2DDataValue() * 90f)
        collector.submitCustomGeometry(poses, RenderTypes.entityCutout(texture)) { pose, buffer ->
            for (vertex in FoundationSchematicMesh.vertices) {
                buffer.addVertex(pose, vertex.x, vertex.y, vertex.z).setColor(-1).setUv(vertex.u, vertex.v)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(lights[vertex.lightIndex])
                    .setNormal(pose, vertex.nx, vertex.ny, vertex.nz)
            }
        }
        poses.popPose()
        super.submit(state, poses, collector, camera)
    }
}
