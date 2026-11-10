package galaxyspace.core.util.researches

import com.google.common.collect.ImmutableList
import galaxyspace.api.IResearch
import galaxyspace.core.util.researches.ResearchUtil
import galaxyspace.core.prefab.researches.TestResearch
import java.lang.RuntimeException
import java.util.ArrayList

object ResearchUtil {
    private val list: MutableList<IResearch> = ArrayList<IResearch>()

    @JvmStatic
    fun initResearches() {
        addReserach(TestResearch(1, 15, 15))
        addReserach(TestResearch(5, 35, 15))
        addReserach(TestResearch(6, 48, 38))
        addReserach(TestResearch(10, 55, 38))
        addReserach(TestResearch(11, 66, 50))
    }

    @JvmStatic
    private fun addReserach(research: IResearch) {
        if (!list.isEmpty()) for (res: IResearch in list) if (research.getID() == res.getID()) throw RuntimeException("Research ID " + research.getID() + " already have. Change ID.")

        list.add(research)
    }

    @JvmStatic
    fun getReserachList(): MutableList<IResearch?>? {
        return ImmutableList.copyOf<IResearch?>(list)
    }

    @JvmStatic
    fun getResearch(id: Int): IResearch? {
        if (!list.isEmpty()) for (res: IResearch in list) if (res.getID() == id) return res
        return null
    }
}
