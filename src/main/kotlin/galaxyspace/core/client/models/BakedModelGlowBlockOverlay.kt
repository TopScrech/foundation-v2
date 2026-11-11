package galaxyspace.core.client.models

import java.util.ArrayList
import java.util.HashMap

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.fml.client.FMLClientHandler

open class BakedModelGlowBlockOverlay constructor(private val type: QuadType?) : BakedModelDelegate(null) {
    private var bright = 0
    private var alpha = 0.0
    private val sprite: MutableMap<EnumFacing?, String?> = HashMap<EnumFacing?, String?>()
    private val cache: MutableList<BakedQuad?>? = null

    enum class QuadType {
        BLOCK,
        CROSS
    }

    /*
    public BakedModelGlowBlockOverlay(IBakedModel base, TextureAtlasSprite texture, int bright, double color) {
        super(base);
        this.bright = bright;
        this.alpha = color;
        sprite = texture;
    }
*/
    open fun setModel(base: IBakedModel?): BakedModelGlowBlockOverlay {
        this.base = (base)!!
        return this
    }

    open fun addTexture(side: EnumFacing?, texture: String?): BakedModelGlowBlockOverlay {
        this.sprite.put(side, texture)
        return this
    }

    open fun setBrightness(bright: Int): BakedModelGlowBlockOverlay {
        this.bright = bright
        return this
    }

    open fun setAlpha(alpha: Double): BakedModelGlowBlockOverlay {
        this.alpha = alpha
        return this
    }

    open fun getSprite(side: EnumFacing?): TextureAtlasSprite {
        return Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(this.sprite.get(side))
    }

    open fun getSprites(): MutableMap<EnumFacing?, String?> {
        return this.sprite
    }

    private fun putVertex(
        builder: UnpackedBakedQuad.Builder,
        state: IBlockState,
        normal: Vec3d,
        x: Double,
        y: Double,
        z: Double,
        sprite: TextureAtlasSprite,
        u: Float,
        v: Float,
        hasBrightness: Boolean,
        brightness: Int,
        color: Vec3d
    ) {
        var u: Float = u
        var v: Float = v
        for (e: Int in 0..<format.getElementCount()) {
            when (format.getElement(e).getUsage()) {
                VertexFormatElement.EnumUsage.POSITION -> builder.put(e, x.toFloat(), y.toFloat(), z.toFloat())
                VertexFormatElement.EnumUsage.COLOR -> builder.put(
                    e,
                    color.x.toFloat(),
                    color.y.toFloat(),
                    color.z.toFloat(),
                    1.0f
                )

                VertexFormatElement.EnumUsage.UV -> if (format.getElement(e).getIndex() == 1) {
                    if (hasBrightness) {
                        val blockLight: Float = (((brightness shr 4) and 15).toFloat() * 32) / 65535
                        val skyLight: Float = (((brightness shr 20) and 15).toFloat() * 32) / 65535
                        builder.put(e, blockLight, skyLight)
                    } else {
                        builder.put(e)
                    }
                } else {
                    u = sprite.getInterpolatedU(u.toDouble())
                    v = sprite.getInterpolatedV(v.toDouble())
                    builder.put(e, u, v)
                }

                VertexFormatElement.EnumUsage.NORMAL -> if (hasBrightness) {
                    builder.put(e, 0.0f, 1.0f, 0.0f)
                } else {
                    builder.put(e, normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
                }

                else -> builder.put(e)
            }
        }
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad?> {
        if (state == null) {
            return (base)!!.getQuads(state, side, rand)
        }

        val quads: MutableList<BakedQuad?> = ArrayList<BakedQuad?>(12)

        quads.addAll((this.base)!!.getQuads(state, side, rand))
        if (this.type == QuadType.CROSS) {
            quads.add(
                createQuad(
                    state,
                    Vec3d(1.0, 0.0, 1.0),
                    Vec3d(1.0, 1.0, 1.0),
                    Vec3d(0.0, 1.0, 0.0),
                    Vec3d(0.0, 0.0, 0.0),
                    getSprite(EnumFacing.NORTH),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
            quads.add(
                createQuad(
                    state,
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(0.0, 1.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0),
                    Vec3d(1.0, 0.0, 1.0005),
                    getSprite(EnumFacing.NORTH),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )

            quads.add(
                createQuad(
                    state,
                    Vec3d(0.0, 0.0, 1.0),
                    Vec3d(0.0, 1.0, 1.0),
                    Vec3d(1.0, 1.0, 0.0),
                    Vec3d(1.0, 0.0, 0.0005),
                    getSprite(EnumFacing.NORTH),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
            quads.add(
                createQuad(
                    state,
                    Vec3d(1.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 0.0),
                    Vec3d(0.0, 1.0, 1.0),
                    Vec3d(0.0005, 0.0, 1.0),
                    getSprite(EnumFacing.NORTH),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
        } else {
            if (this.sprite.containsKey(EnumFacing.DOWN)) quads.add(
                createQuad(
                    state,
                    Vec3d(1.0, 0.0, 0.0),
                    Vec3d(1.0, 0.0, 1.0),
                    Vec3d(0.0, 0.0, 1.0),
                    Vec3d(0.0, 0.0, 0.0),
                    getSprite(EnumFacing.DOWN),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
            if (this.sprite.containsKey(EnumFacing.UP)) quads.add(
                createQuad(
                    state,
                    Vec3d(0.0, 1.0, 0.0),
                    Vec3d(0.0, 1.0, 1.0),
                    Vec3d(1.0, 1.0, 1.0),
                    Vec3d(1.0, 1.0, 0.0),
                    getSprite(EnumFacing.UP),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
            if (this.sprite.containsKey(EnumFacing.NORTH)) quads.add(
                createQuad(
                    state,
                    Vec3d(1.0, 0.0, 1.0),
                    Vec3d(1.0, 1.0, 1.0),
                    Vec3d(0.0, 1.0, 1.0),
                    Vec3d(0.0, 0.0, 1.0),
                    getSprite(EnumFacing.NORTH),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
            if (this.sprite.containsKey(EnumFacing.SOUTH)) quads.add(
                createQuad(
                    state,
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(0.0, 1.0, 0.0),
                    Vec3d(1.0, 1.0, 0.0),
                    Vec3d(1.0, 0.0, 0.0),
                    getSprite(EnumFacing.SOUTH),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
            if (this.sprite.containsKey(EnumFacing.EAST)) quads.add(
                createQuad(
                    state,
                    Vec3d(0.0, 0.0, 1.0),
                    Vec3d(0.0, 1.0, 1.0),
                    Vec3d(0.0, 1.0, 0.0),
                    Vec3d(0.0, 0.0, 0.0),
                    getSprite(EnumFacing.EAST),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
            if (this.sprite.containsKey(EnumFacing.WEST)) quads.add(
                createQuad(
                    state,
                    Vec3d(1.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0),
                    Vec3d(1.0, 0.0, 1.0),
                    getSprite(EnumFacing.WEST),
                    true,
                    this.bright,
                    Vec3d(1.0, 1.0, 1.0),
                    0
                )
            )
        }

        return quads
    }


    private fun createQuad(
        state: IBlockState,
        v1: Vec3d,
        v2: Vec3d,
        v3: Vec3d,
        v4: Vec3d,
        sprite: TextureAtlasSprite,
        hasBrightness: Boolean,
        brightness: Int,
        color: Vec3d,
        tint: Int
    ): BakedQuad {
        val normal: Vec3d = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize()

        val builder: UnpackedBakedQuad.Builder = UnpackedBakedQuad.Builder(format)

        builder.setTexture(sprite)
        builder.setQuadTint(tint)

        putVertex(builder, state, normal, v1.x, v1.y, v1.z, sprite, 16f, 16f, hasBrightness, brightness, color)
        putVertex(builder, state, normal, v2.x, v2.y, v2.z, sprite, 16f, 0f, hasBrightness, brightness, color)
        putVertex(builder, state, normal, v3.x, v3.y, v3.z, sprite, 0f, 0f, hasBrightness, brightness, color)
        putVertex(builder, state, normal, v4.x, v4.y, v4.z, sprite, 0f, 16f, hasBrightness, brightness, color)

        //builder.setApplyDiffuseLighting(false);
        return builder.build()
    }


    companion object {
        private val format: VertexFormat =
            VertexFormat(DefaultVertexFormats.BLOCK).addElement(DefaultVertexFormats.TEX_2S)

        @JvmStatic
        fun isLightMapDisabled(): Boolean {
            return FMLClientHandler.instance().hasOptifine() || !ForgeModContainer.forgeLightPipelineEnabled
        }

        @JvmStatic
        fun getFormatWithLightMap(formatdef: VertexFormat): VertexFormat? {
            if (isLightMapDisabled()) {
                return formatdef
            }

            if (formatdef === DefaultVertexFormats.BLOCK) {
                return DefaultVertexFormats.BLOCK
            } else if (formatdef === DefaultVertexFormats.ITEM) {
                return format
            } else if (!formatdef.hasUvOffset(1)) {
                val result: VertexFormat = VertexFormat(formatdef)

                result.addElement(DefaultVertexFormats.TEX_2S)

                return result
            }

            return formatdef
        }
    }
}
