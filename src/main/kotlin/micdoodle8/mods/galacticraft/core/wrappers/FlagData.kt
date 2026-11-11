package micdoodle8.mods.galacticraft.core.wrappers

import foundation.persistence.containsTag

import java.awt.image.BufferedImage
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.nbt.CompoundTag as NBTTagCompound

open class FlagData
constructor(private var width: Int, private var height: Int) {
    private val color: Array<Array<ByteArray>?>

    init {
        this.color = (((Array<Array<ByteArray?>?>(width) { ((Array<ByteArray?>(height) { ByteArray(3) } as Array<ByteArray>? as Array<ByteArray?>?)) } as Array<ByteArray>? as Array<Array<ByteArray>?>?))!!)

        for (i: Int in 0..<width) {
            for (j: Int in 0..<height) {
                (this)!!.color[i]!![j][0] = 127
                (this)!!.color[i]!![j][1] = 127
                (this)!!.color[i]!![j][2] = 127
            }
        }
    }

    open fun getHeight(): Int {
        return this.height
    }

    open fun setHeight(height: Int) {
        this.height = height
    }

    open fun getWidth(): Int {
        return this.width
    }

    open fun setWidth(width: Int) {
        this.width = width
    }

    /**
     * Returns the color, normalized, at the flag coordinates.
     *
     *
     * 0, 0 is the top left.
     *
     * @param posX x-position of the flag
     * @param posY y-position of the flag
     * @return The color vector, capped from 0-1
     */
    open fun getColorAt(posX: Int, posY: Int): Vector3 {
        if (posX >= this.width || posY >= this.height) {
            return Vector3(0.0, 0.0, 0.0)
        }

        return Vector3(
            (this.color[posX]!![posY][0] + 128) / 256.0,
            (this.color[posX]!![posY][1] + 128) / 256.0,
            (this.color[posX]!![posY][2] + 128) / 256.0
        )
    }

    /**
     * Set the color at the designated flag coordinates
     *
     * @param posX     The x-position of the flag
     * @param posY     The y-position of the flag
     * @param colorVec The color vector, values from 0-256
     */
    open fun setColorAt(posX: Int, posY: Int, colorVec: Vector3) {
        this.color[posX]!![posY][0] = (colorVec.intX() - 128).toByte()
        this.color[posX]!![posY][1] = (colorVec.intY() - 128).toByte()
        this.color[posX]!![posY][2] = (colorVec.intZ() - 128).toByte()
    }

    open fun saveFlagData(nbt: NBTTagCompound) {
        nbt.putInt("FWidth", this.width)
        nbt.putInt("FHeight", this.height)

        for (i: Int in 0..<this.height) {
            val colorRow: IntArray = IntArray(this.width)
            for (j: Int in 0..<this.width) {
                val arrayColor: ByteArray = this.color[j]!![i]
                colorRow[j] = ColorUtil.to32BitColorB(arrayColor[0], arrayColor[1], arrayColor[2])
            }
            nbt.putIntArray("FRow" + i, colorRow)
        }
    }

    open fun toBufferedImage(): BufferedImage {
        val image: BufferedImage = BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB)
        for (i: Int in 0..<this.width) {
            for (j: Int in 0..<this.height) {
                val col: Int =
                    ((this.color[i]!![j][0] + 128) shl 16) or ((this.color[i]!![j][1] + 128) shl 8) or (this.color[i]!![j][2] + 128)
                image.setRGB(i, j, col)
            }
        }
        return image
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val flagData: FlagData = o as FlagData

        if (height != flagData.height) {
            return false
        }
        if (width != flagData.width) {
            return false
        }
        return color.contentDeepEquals(flagData.color)
    }

    companion object {
        @JvmField
        var DEFAULT: FlagData = FlagData(48, 32)

        @JvmStatic
        fun readFlagData(nbt: NBTTagCompound): FlagData {
            if (nbt.containsTag("FlagWidth")) {
                //Legacy saves
                val width: Int = nbt.getIntOr("FlagWidth", 0)
                val height: Int = nbt.getIntOr("FlagHeight", 0)

                val flagData: FlagData = FlagData(width, height)

                for (i: Int in 0..<width) {
                    for (j: Int in 0..<height) {
                        flagData.color[i]!![j][0] = nbt.getByte("ColorR-X" + i + "-Y" + j)
                        flagData.color[i]!![j][1] = nbt.getByte("ColorG-X" + i + "-Y" + j)
                        flagData.color[i]!![j][2] = nbt.getByte("ColorB-X" + i + "-Y" + j)
                    }
                }

                return flagData
            }

            //New more compact flag save style
            val width: Int = nbt.getIntOr("FWidth", 0)
            val height: Int = nbt.getIntOr("FHeight", 0)

            val flagData: FlagData = FlagData(width, height)
            for (i: Int in 0..<height) {
                val colorRow: IntArray = nbt.getIntArray("FRow" + i)
                for (j: Int in 0..<width) {
                    val color: Int = colorRow[j]
                    flagData.color[j]!![i][0] = (color shr 16).toByte()
                    flagData.color[j]!![i][1] = ((color shr 8) and 255).toByte()
                    flagData.color[j]!![i][2] = (color and 255).toByte()
                }
            }
            return flagData
        }
    }
}
