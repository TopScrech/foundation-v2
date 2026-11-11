package micdoodle8.mods.galacticraft.core.entities

import foundation.schematic.FoundationSchematicEntities
import foundation.schematic.FoundationSchematicState
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.decoration.HangingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

open class EntityHangingSchematic(type: EntityType<out EntityHangingSchematic>, level: Level) : HangingEntity(type, level) {
    constructor(level: Level, position: BlockPos, facing: Direction, index: Int) : this(FoundationSchematicEntities.TYPE, level) {
        this.pos = position
        schematic = index
        setDirection(facing)
    }

    var schematic: Int
        get() = entityData.get(SCHEMATIC)
        set(value) { require(value >= 0); entityData.set(SCHEMATIC, value) }
    private var supportCheckTicks = 0

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(SCHEMATIC, 0)
    }

    override fun tick() {
        super.tick()
        val level = level()
        if (level is ServerLevel && supportCheckTicks++ >= 10) {
            supportCheckTicks = 0
            if (!isRemoved && !survives()) { discard(); dropItem(level, null) }
        }
    }

    override fun calculateBoundingBox(position: BlockPos, facing: Direction): AABB = FoundationSchematicState.bounds(position, facing)
    override fun addAdditionalSaveData(output: ValueOutput) = FoundationSchematicState(pos, direction, schematic).write(output)
    override fun readAdditionalSaveData(input: ValueInput) {
        val state = FoundationSchematicState.read(input, blockPosition())
        pos = state.position
        schematic = state.schematic
        setDirection(state.direction)
    }
    override fun dropItem(level: ServerLevel, causedBy: Entity?) {
        if (level.gameRules.get(GameRules.ENTITY_DROPS) && !(causedBy is Player && causedBy.hasInfiniteMaterials())) {
            spawnAtLocation(level, SchematicRegistry.getSchematicItem(schematic))
        }
    }
    override fun playPlacementSound() { playSound(SoundEvents.PAINTING_PLACE, 1f, 1f) }
    override fun getPickResult(): ItemStack = SchematicRegistry.getSchematicItem(schematic)
    override fun snapTo(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) { setPos(x, y, z) }
    override fun trackingPosition(): Vec3 = Vec3.atLowerCornerOf(pos)
    override fun getAddEntityPacket(serverEntity: ServerEntity): Packet<ClientGamePacketListener> =
        ClientboundAddEntityPacket(this, direction.get3DDataValue(), pos)
    override fun recreateFromPacket(packet: ClientboundAddEntityPacket) {
        super.recreateFromPacket(packet)
        setDirection(Direction.from3DDataValue(packet.data))
    }
    fun getWidthPixels(): Int = 32
    fun getHeightPixels(): Int = 32

    companion object {
        private val SCHEMATIC: EntityDataAccessor<Int> = SynchedEntityData.defineId(EntityHangingSchematic::class.java, EntityDataSerializers.INT)
    }
}
