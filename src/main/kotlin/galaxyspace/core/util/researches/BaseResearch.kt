package galaxyspace.core.util.researches

import galaxyspace.api.IResearch
import net.minecraft.world.item.ItemStack
import java.util.ArrayList

open class BaseResearch constructor(
    private val id: Int,
    private val name: String?,
    private val parents: Array<IResearch?>?,
    posX: Int,
    posY: Int
) : IResearch {
    private val items_for_need: MutableList<ItemStack?> = ArrayList<ItemStack?>()
    private val unlock_items: MutableList<ItemStack?> = ArrayList<ItemStack?>()
    private var need_exp = 0
    private var posX = 0
    private var posY = 0

    constructor(id: Int, name: String?, posX: Int, posY: Int) : this(id, name, null, posX, posY)

    init {
        this.posX = posX
        this.posY = posY
    }

    open fun setExperince(exp: Int) {
        this.need_exp = exp
    }

    open fun addNeedItem(stack: ItemStack?) {
        this.items_for_need.add(stack)
    }

    open fun addUnlockItem(stack: ItemStack?) {
        this.unlock_items.add(stack)
    }

    override fun getName(): String? {
        return this.name
    }

    override fun getNeedItems(): MutableList<ItemStack?> {
        return this.items_for_need
    }

    override fun getParents(): Array<IResearch?>? {
        return this.parents
    }

    override fun getNeedExperience(): Float {
        return this.need_exp.toFloat()
    }

    override fun getUnlockItems(): MutableList<ItemStack?> {
        return this.unlock_items
    }

    override fun getID(): Int {
        return this.id
    }

    override fun getPosX(): Int {
        return this.posX
    }

    override fun getPosY(): Int {
        return this.posY
    }

    override fun getDescription(): String {
        return "Test Desc"
    }

    override fun getTextureID(): Int {
        return 0
    }
}
