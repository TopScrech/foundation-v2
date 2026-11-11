package micdoodle8.mods.galacticraft.core.network

import foundation.item.legacyItemValue

import io.netty.buffer.ByteBuf
import java.io.IOException
import java.util.UUID
import java.io.ByteArrayInputStream
import com.google.common.math.DoubleMath
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorage
import micdoodle8.mods.galacticraft.core.tile.FluidTankGC
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor as EnumDyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fml.common.network.ByteBufUtils
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.reflect.Field
import java.util.ArrayList
import kotlin.math.abs

object NetworkUtil {
    @JvmStatic
    @Throws(IOException::class)
    fun encodeData(buffer: ByteBuf, sendData: MutableCollection<Any?>) {
        for (dataValue: Any? in sendData) {
            if (dataValue is Int) {
                buffer.writeInt(dataValue)
            } else if (dataValue is Float) {
                buffer.writeFloat(dataValue)
            } else if (dataValue is Double) {
                buffer.writeDouble(dataValue)
            } else if (dataValue is Byte) {
                buffer.writeByte(dataValue.toInt())
            } else if (dataValue is Boolean) {
                buffer.writeBoolean(dataValue)
            } else if (dataValue is String) {
                ByteBufUtils.writeUTF8String(buffer, dataValue)
            } else if (dataValue is Short) {
                buffer.writeShort(dataValue.toInt())
            } else if (dataValue is Long) {
                buffer.writeLong(dataValue)
            } else if (dataValue is EnergyStorage) {
                val storage: EnergyStorage = dataValue
                buffer.writeFloat(storage.getCapacityGC())
                buffer.writeFloat(storage.getMaxReceive())
                buffer.writeFloat(storage.getMaxExtract())
                buffer.writeFloat(storage.getEnergyStoredGC())
            } else if (dataValue is NBTTagCompound) {
                writeNBTTagCompound(dataValue, buffer)
            } else if (dataValue is FluidTankGC) {
                val tankGC: FluidTankGC = dataValue
                val pos: BlockPos = tankGC.getTilePosition()
                buffer.writeInt(pos.getX())
                buffer.writeInt(pos.getY())
                buffer.writeInt(pos.getZ())
                writeFluidTank(dataValue as FluidTank, buffer)
            } else if (dataValue is FluidTank) {
                writeFluidTank(dataValue, buffer)
            } else if (dataValue is Entity) {
                buffer.writeInt(dataValue.getId())
            } else if (dataValue is Vector3) {
                buffer.writeDouble(dataValue.x)
                buffer.writeDouble(dataValue.y)
                buffer.writeDouble(dataValue.z)
            } else if (dataValue is BlockVec3) {
                buffer.writeInt(dataValue.x)
                buffer.writeInt(dataValue.y)
                buffer.writeInt(dataValue.z)
            } else if (dataValue is ByteArray) {
                val size: Int = dataValue.size
                buffer.writeInt(size)
                val pos: Int = buffer.writerIndex()
                buffer.capacity(pos + size)
                buffer.setBytes(pos, dataValue)
                buffer.writerIndex(pos + size)
            } else if (dataValue is UUID) {
                buffer.writeLong(dataValue.getLeastSignificantBits())
                buffer.writeLong(dataValue.getMostSignificantBits())
            } else if (dataValue is MutableCollection<*>) {
                encodeData(buffer, dataValue as MutableCollection<Any?>)
            } else if (dataValue is FlagData) {
                buffer.writeInt(dataValue.getWidth())
                buffer.writeInt(dataValue.getHeight())

                for (i: Int in 0..<dataValue.getWidth()) {
                    for (j: Int in 0..<dataValue.getHeight()) {
                        val vec: Vector3 = dataValue.getColorAt(i, j)
                        buffer.writeByte((vec.x * 256 - 128).toInt().toByte().toInt())
                        buffer.writeByte((vec.y * 256 - 128).toInt().toByte().toInt())
                        buffer.writeByte((vec.z * 256 - 128).toInt().toByte().toInt())
                    }
                }
            } else if ((dataValue is Array<*> && Int::class.javaObjectType.isAssignableFrom(dataValue!!.javaClass.componentType))) {
                val array: Array<Int> = dataValue as Array<Int>
                buffer.writeInt(array.size)

                for (i: Int in array.indices) {
                    buffer.writeInt(array[i]!!)
                }
            } else if (dataValue is Array<*> && dataValue.javaClass.componentType == String::class.java) {
                val array: Array<String?> = dataValue as Array<String?>
                buffer.writeInt(array.size)

                for (i: Int in array.indices) {
                    ByteBufUtils.writeUTF8String(buffer, array[i])
                }
            } else if ((dataValue is Array<*> && Footprint::class.java.isAssignableFrom(dataValue!!.javaClass.componentType))) {
                val array: Array<Footprint?> = dataValue as Array<Footprint?>
                buffer.writeInt(array.size)

                for (i: Int in array.indices) {
                    buffer.writeInt(array[i]!!.dimension)
                    buffer.writeFloat((array[i]!!.position)!!.x.toFloat())
                    buffer.writeFloat((array[i]!!.position)!!.y.toFloat() + 1)
                    buffer.writeFloat((array[i]!!.position)!!.z.toFloat())
                    buffer.writeFloat(array[i]!!.rotation)
                    buffer.writeShort(array[i]!!.age.toInt())
                    ByteBufUtils.writeUTF8String(buffer, array[i]!!.owner)
                }
            } else if (dataValue is EnumFacing) {
                buffer.writeInt(dataValue.getIndex())
            } else if (dataValue is BlockPos) {
                val pos: BlockPos = dataValue
                buffer.writeInt(pos.getX())
                buffer.writeInt(pos.getY())
                buffer.writeInt(pos.getZ())
            } else if (dataValue is EnumDyeColor) {
                buffer.writeInt(dataValue.getDyeDamage())
            } else {
                if (dataValue == null) {
                    GCLog.severe("Cannot construct PacketSimple with null data, this is a bug.")
                }
                GCLog.info("Could not find data type to encode!: " + dataValue)
            }
        }
    }

    @JvmStatic
    fun decodeData(types: Array<Class<*>>, buffer: ByteBuf): ArrayList<Any?> {
        val objList: ArrayList<Any?> = ArrayList<Any?>()

        for (clazz: Class<*> in types) {
            if (clazz == Int::class.java) {
                objList.add(buffer.readInt())
            } else if (clazz == Float::class.java) {
                objList.add(buffer.readFloat())
            } else if (clazz == Double::class.java) {
                objList.add(buffer.readDouble())
            } else if (clazz == Byte::class.java) {
                objList.add(buffer.readByte())
            } else if (clazz == Boolean::class.java) {
                objList.add(buffer.readBoolean())
            } else if (clazz == String::class.java) {
                objList.add(ByteBufUtils.readUTF8String(buffer))
            } else if (clazz == Short::class.java) {
                objList.add(buffer.readShort())
            } else if (clazz == Long::class.java) {
                objList.add(buffer.readLong())
            } else if (clazz == ByteArray::class.java) {
                val size: Int = buffer.readInt()
                val bytes: ByteArray = ByteArray(size)
                buffer.readBytes(bytes, 0, size)
                objList.add(bytes)
            } else if (clazz == EnergyStorage::class.java) {
                val storage: EnergyStorage = EnergyStorage(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
                storage.setEnergyStored(buffer.readFloat())
                objList.add(storage)
            } else if (clazz == NBTTagCompound::class.java) {
                try {
                    objList.add(readNBTTagCompound(buffer))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (clazz == BlockVec3::class.java) {
                objList.add(BlockVec3(buffer.readInt(), buffer.readInt(), buffer.readInt()))
            } else if (clazz == UUID::class.java) {
                objList.add(UUID(buffer.readLong(), buffer.readLong()))
            } else if (clazz == Vector3::class.java) {
                objList.add(Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()))
            } else if (clazz == FlagData::class.java) {
                val width: Int = buffer.readInt()
                val height: Int = buffer.readInt()
                val flagData: FlagData = FlagData(width, height)

                for (i: Int in 0..<width) {
                    for (j: Int in 0..<height) {
                        flagData.setColorAt(
                            i,
                            j,
                            Vector3(
                                (buffer.readByte() + 128).toDouble(),
                                (buffer.readByte() + 128).toDouble(),
                                (buffer.readByte() + 128).toDouble()
                            )
                        )
                    }
                }

                objList.add(flagData)
            } else if (clazz == Array<Int>::class.java) {
                val size: Int = buffer.readInt()

                for (i: Int in 0..<size) {
                    objList.add(buffer.readInt())
                }
            } else if (clazz == Array<String>::class.java) {
                val size: Int = buffer.readInt()

                for (i: Int in 0..<size) {
                    objList.add(ByteBufUtils.readUTF8String(buffer))
                }
            } else if (clazz == Array<Footprint>::class.java) {
                val size: Int = buffer.readInt()

                for (i: Int in 0..<size) {
                    objList.add(
                        Footprint(
                            buffer.readInt(),
                            Vector3(
                                buffer.readFloat().toDouble(),
                                buffer.readFloat().toDouble(),
                                buffer.readFloat().toDouble()
                            ),
                            buffer.readFloat(),
                            buffer.readShort(),
                            ByteBufUtils.readUTF8String(buffer),
                            -1
                        )
                    )
                }
            } else if (clazz == EnumFacing::class.java) {
                objList.add(EnumFacing.from3DDataValue(buffer.readInt()))
            } else if (clazz == BlockPos::class.java) {
                objList.add(BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()))
            } else if (clazz == EnumDyeColor::class.java) {
                objList.add(EnumDyeColor.byDyeDamage(buffer.readInt()))
            }
        }

        return objList
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getFieldValueFromStream(field: Field, buffer: ByteBuf, world: World): Any? {
        val dataValue: Class<*> = field.getType()

        if (dataValue == Int::class.javaPrimitiveType) {
            return buffer.readInt()
        } else if (dataValue == Float::class.javaPrimitiveType) {
            return buffer.readFloat()
        } else if (dataValue == Double::class.javaPrimitiveType) {
            return buffer.readDouble()
        } else if (dataValue == Byte::class.javaPrimitiveType) {
            return buffer.readByte()
        } else if (dataValue == Boolean::class.javaPrimitiveType) {
            return buffer.readBoolean()
        } else if (dataValue == String::class.java) {
            return ByteBufUtils.readUTF8String(buffer)
        } else if (dataValue == Short::class.javaPrimitiveType) {
            return buffer.readShort()
        } else if (dataValue == Long::class.java) {
            return buffer.readLong()
        } else if (dataValue == NBTTagCompound::class.java) {
            return readNBTTagCompound(buffer)
        } else if (dataValue == FluidTankGC::class.java) {
            return readFluidTankGC(buffer, world)
        } else if (dataValue == FluidTank::class.java) {
            return readFluidTank(buffer)
        } else if (dataValue == Vector3::class.java) {
            return Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
        } else if (dataValue == BlockVec3::class.java) {
            return BlockVec3(buffer.readInt(), buffer.readInt(), buffer.readInt())
        } else if (dataValue == UUID::class.java) {
            return UUID(buffer.readLong(), buffer.readLong())
        } else if (dataValue == ByteArray::class.java) {
            val bytes: ByteArray = ByteArray(buffer.readInt())
            for (i: Int in bytes.indices) {
                bytes[i] = buffer.readByte()
            }
            return bytes
        } else if (dataValue == EnergyStorage::class.java) {
            val capacity: Float = buffer.readFloat()
            val maxReceive: Float = buffer.readFloat()
            val maxExtract: Float = buffer.readFloat()
            val storage: EnergyStorage = EnergyStorage(capacity, maxReceive, maxExtract)
            storage.setEnergyStored(buffer.readFloat())
            return storage
        } else if (dataValue == EnumFacing::class.java) {
            return EnumFacing.from3DDataValue(buffer.readInt())
        } else if (dataValue == BlockPos::class.java) {
            return BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt())
        } else if (dataValue == EnumDyeColor::class.java) {
            return EnumDyeColor.byDyeDamage(buffer.readInt())
        } else {
            var c: Class<*>? = dataValue

            while (c != null) {
                if (c == Entity::class.java) {
                    return world.getEntity(buffer.readInt())
                }

                c = c.getSuperclass()
            }
        }

        throw NullPointerException("Field type not found: " + field.getType().getSimpleName())
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readItemStack(buffer: ByteBuf): ItemStack {
        var itemstack: ItemStack = ItemStack.EMPTY
        val itemID: Short = buffer.readShort()

        if (itemID >= 0) {
            val stackSize: Byte = buffer.readByte()
            val meta: Short = buffer.readShort()
            itemstack = ItemStack(Item.getItemById(itemID.toInt()), stackSize.toInt(), meta.toInt())
            if (buffer.readBoolean()) {
                itemstack.setTagCompound(readNBTTagCompound(buffer))
            }
        }

        return itemstack
    }

    @JvmStatic
    @Throws(IOException::class)
    fun writeItemStack(itemStack: ItemStack, buffer: ByteBuf) {
        if (itemStack.isEmpty()) {
            buffer.writeShort(-1)
        } else {
            buffer.writeShort(Item.getIdFromItem(itemStack.getItem()))
            buffer.writeByte(itemStack.getCount())
            buffer.writeShort(itemStack.legacyItemValue)
            var nbttagcompound: NBTTagCompound? = null

            if (itemStack.getItem().isDamageable() || itemStack.getItem().getShareTag()) {
                nbttagcompound = itemStack.getTagCompound()
            }

            buffer.writeBoolean(nbttagcompound != null)
            if (nbttagcompound != null) {
                writeNBTTagCompound(nbttagcompound, buffer)
            }
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readNBTTagCompound(buffer: ByteBuf): NBTTagCompound? {
        try {
            val length: Int = buffer.readInt()
            val compressed: ByteArray = ByteArray(length)
            buffer.readBytes(compressed)
            val bais: ByteArrayInputStream = ByteArrayInputStream(compressed)
            return CompressedStreamTools.readCompressed(bais)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun writeNBTTagCompound(nbt: NBTTagCompound, buffer: ByteBuf) {
        try {
            val baos: ByteArrayOutputStream = ByteArrayOutputStream()
            CompressedStreamTools.writeCompressed(nbt, baos)
            val compressed: ByteArray = baos.toByteArray()
            buffer.writeInt(compressed.size)
            buffer.writeBytes(compressed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun writeFluidTank(fluidTank: FluidTank?, buffer: ByteBuf) {
        if (fluidTank == null) {
            buffer.writeInt(0)
            ByteBufUtils.writeUTF8String(buffer, "")
            buffer.writeInt(0)
        } else {
            buffer.writeInt(fluidTank.getCapacity())
            ByteBufUtils.writeUTF8String(
                buffer,
                if (fluidTank.getFluid() == null) "" else fluidTank.getFluid()!!.getFluid().getName()
            )
            buffer.writeInt(fluidTank.getFluidAmount())
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readFluidTankGC(buffer: ByteBuf, world: World): FluidTankGC {
        val pos: BlockPos = BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt())
        val tile: TileEntity? = world.getBlockEntity(pos)
        val capacity: Int = buffer.readInt()
        val fluidName: String = ByteBufUtils.readUTF8String(buffer)
        val fluidTank: FluidTankGC = FluidTankGC(capacity, tile)
        val amount: Int = buffer.readInt()

        if (fluidName == "") {
            fluidTank.setFluid(null)
        } else {
            val fluid: Fluid = FluidRegistry.getFluid(fluidName)
            fluidTank.setFluid(FluidStack(fluid, amount))
        }

        return fluidTank
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readFluidTank(buffer: ByteBuf): FluidTank {
        val capacity: Int = buffer.readInt()
        val fluidName: String = ByteBufUtils.readUTF8String(buffer)
        val fluidTank: FluidTank = FluidTank(capacity)
        val amount: Int = buffer.readInt()

        if (fluidName == "") {
            fluidTank.setFluid(null)
        } else {
            val fluid: Fluid = FluidRegistry.getFluid(fluidName)
            fluidTank.setFluid(FluidStack(fluid, amount))
        }

        return fluidTank
    }

    @JvmStatic
    fun fuzzyEquals(a: Any?, b: Any?): Boolean {
        if ((a == null) != (b == null)) {
            return false
        } else if (a == null) {
            return true
        } else if (a is Float && b is Float) {
            val af: Float = a
            val bf: Float = b
            return af == bf || abs(af - bf) < 0.01f
        } else if (a is Double && b is Double) {
            return DoubleMath.fuzzyEquals(a, b, 0.01)
        } else if (a is Entity && b is Entity) {
            val a2: Entity = a
            val b2: Entity = b
            return fuzzyEquals(a2.getId(), b2.getId())
        } else if (a is Vector3 && b is Vector3) {
            val a2: Vector3 = a
            val b2: Vector3 = b
            return fuzzyEquals(a2.x, b2.x) &&
                    fuzzyEquals(a2.y, b2.y) &&
                    fuzzyEquals(a2.z, b2.z)
        } else if (a is EnergyStorage && b is EnergyStorage) {
            val a2: EnergyStorage = a
            val b2: EnergyStorage = b
            return fuzzyEquals(a2.getEnergyStoredGC(), b2.getEnergyStoredGC()) &&
                    fuzzyEquals(a2.getCapacityGC(), b2.getCapacityGC()) &&
                    fuzzyEquals(a2.getMaxReceive(), b2.getMaxReceive()) &&
                    fuzzyEquals(a2.getMaxExtract(), b2.getMaxExtract())
        } else if (a is FluidTank && b is FluidTank) {
            val a2: FluidTank = a
            val b2: FluidTank = b
            val fluidA: FluidStack? = a2.getFluid()
            val fluidB: FluidStack? = b2.getFluid()
            return fuzzyEquals(a2.getCapacity(), b2.getCapacity()) &&
                    fuzzyEquals(
                        if (fluidA != null) fluidA.getFluid().getName() else "",
                        if (fluidB != null) fluidB.getFluid().getName() else ""
                    ) &&
                    fuzzyEquals(a2.getFluidAmount(), b2.getFluidAmount())
        } else {
            return a == b
        }
    }

    @JvmStatic
    fun cloneNetworkedObject(a: Any?): Any? {
        // We only need to clone mutable objects
        if (a is EnergyStorage) {
            val prevStorage: EnergyStorage = a
            val storage: EnergyStorage =
                EnergyStorage(prevStorage.getCapacityGC(), prevStorage.getMaxReceive(), prevStorage.getMaxExtract())
            storage.setEnergyStored(prevStorage.getEnergyStoredGC())
            return storage
        } else if (a is FluidTankGC) {
            val prevTank: FluidTankGC = a
            val tank: FluidTankGC = FluidTankGC(prevTank.getFluid(), prevTank.getCapacity(), prevTank.getTile())
            return tank
        } else if (a is FluidTank) {
            val prevTank: FluidTank = a
            var prevFluid: FluidStack? = prevTank.getFluid()
            prevFluid = if (prevFluid == null) null else prevFluid.copy()
            val tank: FluidTank = FluidTank(prevFluid, prevTank.getCapacity())
            return tank
        } else {
            return a
        }
    }
}
