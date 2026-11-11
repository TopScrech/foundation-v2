package foundation.verification

import foundation.persistence.containsTag
import foundation.persistence.listOfType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag

fun verifyLegacyNbtTypes() {
    val tag = CompoundTag()
    tag.putInt("integer", 42)
    tag.putFloat("float", 3.5f)
    tag.putString("text", "42")
    check(tag.containsTag("integer", 3) && tag.containsTag("integer", 99))
    check(tag.containsTag("float", 5) && tag.containsTag("float", 99))
    check(!tag.containsTag("integer", 4) && !tag.containsTag("text", 99))
    check(tag.containsTag("text") && !tag.containsTag("absent"))
    val records = ListTag()
    records.add(CompoundTag().also { it.putByte("Slot", 5); it.putInt("x", -17) })
    tag.put("records", records)
    check(tag.listOfType("records", 10) === records)
    check(tag.listOfType("records", 8).isEmpty())
    check(tag.listOfType("absent", 10).isEmpty())
    check(tag.listOfType("text", 10).isEmpty())
    val restored = tag.listOfType("records", 10).getCompoundOrEmpty(0)
    check(restored.getByteOr("Slot", 0).toInt() == 5 && restored.getIntOr("x", 0) == -17)
    // 26.3 allows mixed lists, which cannot satisfy a legacy homogeneous list request
    records.add(StringTag.valueOf("unexpected"))
    check(tag.listOfType("records", 10).isEmpty())
    check(tag.getListOrEmpty("records").size == 2)
}
