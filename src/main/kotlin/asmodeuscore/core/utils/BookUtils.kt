package asmodeuscore.core.utils

import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.utils.BookUtils
import net.minecraft.resources.Identifier as ResourceLocation
import java.util.Comparator
import java.util.HashMap
import java.util.Locale
import java.util.TreeSet

object BookUtils {
    @JvmField
    var GUIDE_BOOK_CATEGORIES: MutableMap<String?, ResourceLocation?> = HashMap<String?, ResourceLocation?>()

    @JvmField
    var GUIDE_BOOK_PAGES: MutableSet<IBookPage> = TreeSet<IBookPage>(object : Comparator<IBookPage> {
        override fun compare(arg0: IBookPage, arg1: IBookPage): Int {
            return (arg0.titlePage())!!.compareTo((arg1.titlePage())!!)
        }
    })

    @JvmStatic
    fun addGuideBookCategory(name: String?, icon: ResourceLocation?) {
        GUIDE_BOOK_CATEGORIES.put(name, icon)
    }

    @JvmStatic
    fun addGuideBookPage(page: IBookPage) {
        GUIDE_BOOK_PAGES.add(page)
    }

    enum class Book_Cateroies {
        GENERAL,
        BODIES,
        BLOCKS,
        ITEMS,
        MECHANICS;

        fun getName(): String {
            return this.name.lowercase(Locale.getDefault())
        }
    }
}
