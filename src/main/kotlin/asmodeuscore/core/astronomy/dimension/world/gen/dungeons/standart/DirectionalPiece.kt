package asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart

import foundation.persistence.containsTag

import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.lang.Exception
import java.lang.reflect.Constructor
import java.util.Random

abstract class DirectionalPiece : Piece {
    private var direction: EnumFacing? = null

    constructor()

    constructor(configuration: DungeonConfiguration?, direction: EnumFacing?) : super(configuration) {
        this.direction = direction
    }

    fun getDirection(): EnumFacing? {
        return direction
    }

    fun setDirection(direction: EnumFacing?) {
        this.direction = direction
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        tagCompound.putInt("direction", this.direction!!.ordinal)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        if (nbt.containsTag("direction")) {
            this.direction = EnumFacing.from3DDataValue(nbt.getIntOr("direction", 0))
        } else {
            this.direction = EnumFacing.NORTH
        }
    }

    fun getCorridor(rand: Random, startPiece: DungeonStart, maxAttempts: Int, small: Boolean): Piece? {
        var randomDir: EnumFacing
        var blockX: Int
        var blockZ: Int
        var sizeX: Int
        var sizeZ: Int
        var valid: Boolean
        var attempts: Int = maxAttempts
        do {
            val randDir: Int = rand.nextInt(4)
            randomDir = EnumFacing.byHorizontalIndex(
                (if (randDir == getDirection()!!.getOpposite().getHorizontalIndex()) randDir + 1 else randDir) % 4
            )
            val extension: BoundingBox = getExtension(
                randomDir,
                (this.configuration)!!.getHallwayLengthMin() + rand.nextInt((this.configuration)!!.getHallwayLengthMax() - (this.configuration)!!.getHallwayLengthMin()),
                3
            )
            blockX = extension.minX()
            blockZ = extension.minZ()
            sizeX = extension.maxX() - extension.minX()
            sizeZ = extension.maxZ() - extension.minZ()
            valid = !startPiece.checkIntersection(extension)
            attempts--
        } while (!valid && attempts > 0)

        if (!valid) {
            return null
        }

        return getRoom<SizedPiece?>(
            ((this.configuration)!!.getCorridor())!!,
            startPiece,
            rand,
            blockX,
            blockZ,
            sizeX,
            if (small) 3 else (this.configuration)!!.getHallwayHeight(),
            sizeZ,
            randomDir
        ) //new Corridor(this.configuration, rand, blockX, blockZ, sizeX, small ? 3 : this.configuration.getHallwayHeight(), sizeZ, randomDir);
    }

    private fun <T : SizedPiece?> getRoom(
        clazz: Class<*>,
        startPiece: DungeonStart,
        rand: Random,
        blockX: Int,
        blockZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        randomDir: EnumFacing
    ): T? {
        try {
            /*Constructor<?> c0 = clazz.getConstructor(DungeonConfiguration.class, Random.class, Integer.TYPE, Integer.TYPE, EnumFacing.class);
            T dummy = (T) c0.newInstance(this.configuration, rand, 0, 0, this.getDirection().getOpposite());
            BoundingBox extension = getExtension(this.getDirection(), getDirection().getAxis() == EnumFacing.Axis.X ? dummy.getSizeX() : dummy.getSizeZ(), getDirection().getAxis() == EnumFacing.Axis.X ? dummy.getSizeZ() : dummy.getSizeX());
            if (startPiece.checkIntersection(extension))
            {
                return null;
            }
            int sizeX = extension.maxX() - extension.minX();
            int sizeZ = extension.maxZ() - extension.minZ();
            int sizeY = small ? 3 : this.configuration.getHallwayHeight();//dummy.sizeY;
            int blockX = extension.minX();
            int blockZ = extension.minZ();*/
            val c1: Constructor<*> = clazz.getConstructor(
                DungeonConfiguration::class.java,
                Random::class.java,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                EnumFacing::class.java
            )
            return c1.newInstance(this.configuration, rand, blockX, blockZ, sizeX, sizeY, sizeZ, randomDir) as T?
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
