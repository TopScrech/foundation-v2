package micdoodle8.mods.galacticraft.core.util

import kotlin.math.min

open class JavaUtil : SecurityManager() {
    open fun getCaller(): Class<*>? {
        return getClassContext()[2]
    }

    open fun isCalledBy(clazz: Class<*>?): Boolean {
        val context: Array<Class<*>?> = getClassContext()

        val imax: Int = min(context.size, 6)
        for (i: Int in 2..<imax) {
            if (clazz == context[i]) {
                return true
            }
        }
        return false
    }

    open fun isCalledBy(clazz1: Class<*>?, clazz2: Class<*>?): Boolean {
        val context: Array<Class<*>?> = getClassContext()

        val imax: Int = min(context.size, 7)
        for (i: Int in 2..<imax) {
            val test: Class<*>? = context[i]
            if (test == clazz1 || test == clazz2) {
                return true
            }
        }
        return false
    }

    open fun isCalledBy(clazz1: Class<*>?, clazz2: Class<*>?, clazz3: Class<*>?): Boolean {
        val context: Array<Class<*>?> = getClassContext()

        val imax: Int = min(context.size, 6)
        for (i: Int in 2..<imax) {
            val test: Class<*>? = context[i]
            if (test == clazz1 || test == clazz2 || test == clazz3) {
                return true
            }
        }
        return false
    }

    open fun isCalledBy(name: String?): Boolean {
        val context: Array<Class<*>?> = getClassContext()

        val imax: Int = min(context.size, 6)
        for (i: Int in 2..<imax) {
            if (context[i]!!.getName() == name) {
                return true
            }
        }
        return false
    }

    open fun isCalledBySpecific(clazz: Class<*>): Boolean {
        val context: Array<Class<*>?> = getClassContext()
        if (context.size < 6) return false
        return (clazz == context[3] && !clazz.isAssignableFrom(context[4]))
    }

    open fun isCalledBySecond(clazz1: Class<*>?, clazz2: Class<*>?, clazz3: Class<*>?): Int {
        val context: Array<Class<*>?> = getClassContext()
        if (context.size < 4) return 0
        val test: Class<*>? = context[3]
        if (test == clazz1) return 1
        if (test == clazz2) return 2
        if (test == clazz3) return 3
        return 0
    }

    open fun isCalledByThird(clazz: Class<*>?): Boolean {
        val context: Array<Class<*>?> = getClassContext()
        if (context.size < 5) return false
        return (context[4] == clazz)
    }

    companion object {
        @JvmField
        var instance: JavaUtil = JavaUtil()
    }
}
