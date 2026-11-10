package micdoodle8.mods.galacticraft.core.client

import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.image.BufferedImage

open class DynamicTextureProper constructor(//We could transform these in the base class to protected
    private val width: Int, //but whatever.
    private val height: Int
) : DynamicTexture(width, height) {
    private var updateFlag = false

    constructor(img: BufferedImage) : this(img.getWidth(), img.getHeight()) {
        this.update(img)
    }

    open fun update(img: BufferedImage) {
        img.getRGB(0, 0, this.width, this.height, this.getTextureData(), 0, this.width)
        this.updateFlag = true
    }

    override fun getGlTextureId(): Int {
        if (this.updateFlag) {
            this.updateFlag = false
            this.updateDynamicTexture()
        }
        return super.getGlTextureId()
    }
}
