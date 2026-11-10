package micdoodle8.mods.galacticraft.core.util


/**
 * Simple color enum for adding colors to in-game GUI strings of text.
 *
 * @author AidanBrady
 */
enum class EnumColor
    (
    /**
     * The color code that will be displayed
     */
    private val code: String,
    /**
     * A friendly name of the color.
     */
    private val unlocalizedName: String, private val rgbCode: IntArray
) {
    BLACK("\u00a70", "black", intArrayOf(0, 0, 0)),
    DARK_BLUE("\u00a71", "darkBlue", intArrayOf(0, 0, 170)),
    DARK_GREEN("\u00a72", "darkGreen", intArrayOf(0, 170, 0)),
    DARK_AQUA("\u00a73", "darkAqua", intArrayOf(0, 170, 170)),
    DARK_RED("\u00a74", "darkRed", intArrayOf(170, 0, 0)),
    PURPLE("\u00a75", "purple", intArrayOf(170, 0, 170)),
    ORANGE("\u00a76", "orange", intArrayOf(255, 170, 0)),
    GREY("\u00a77", "grey", intArrayOf(170, 170, 170)),
    DARK_GREY("\u00a78", "darkGrey", intArrayOf(85, 85, 85)),
    INDIGO("\u00a79", "indigo", intArrayOf(85, 85, 255)),
    BRIGHT_GREEN("\u00a7a", "brightGreen", intArrayOf(85, 255, 85)),
    AQUA("\u00a7b", "aqua", intArrayOf(85, 255, 255)),
    RED("\u00a7c", "red", intArrayOf(255, 85, 85)),
    PINK("\u00a7d", "pink", intArrayOf(255, 85, 255)),
    YELLOW("\u00a7e", "yellow", intArrayOf(255, 255, 85)),
    WHITE("\u00a7f", "white", intArrayOf(255, 255, 255));

    fun getCode(): String {
        return code
    }

    fun getLocalizedName(): String? {
        return GCCoreUtil.translate("color." + this.unlocalizedName)
    }

    fun getName(): String {
        return this.code + this.getLocalizedName()
    }

    fun getColor(index: Int): Float {
        return this.rgbCode[index] / 255f
    }

    override fun toString(): String {
        return this.code
    }
}
