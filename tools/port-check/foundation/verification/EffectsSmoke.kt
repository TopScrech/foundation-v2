package foundation.verification

import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.registers.potions.ACPotions
import asmodeuscore.core.utils.ACAttributePlayer
import asmodeuscore.core.utils.ACDamageSource
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import com.mojang.serialization.Lifecycle
import foundation.damage.FoundationDamageTypes
import foundation.effect.FoundationEffectCures
import galaxyspace.core.GSPotions
import galaxyspace.core.util.GSDamageSource
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.MappedRegistry
import net.minecraft.core.RegistrationInfo
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.VanillaPackResourcesBuilder
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.resources.MultiPackResourceManager
import net.minecraft.tags.DamageTypeTags
import net.minecraft.tags.TagLoader
import net.minecraft.world.damagesource.DamageScaling
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeMap
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.event.entity.EntityAttributeModificationEvent
import net.minecraftforge.registries.RegistryManager
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import javax.imageio.ImageIO
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.AnnotationNode

fun verifyEffects(resources: Path, lookup: HolderLookup.Provider) {
    val effects = BuiltInRegistries.MOB_EFFECT as MappedRegistry<MobEffect>
    val forgeEffects = RegistryManager.ACTIVE.getRegistry<MobEffect>(Registries.MOB_EFFECT)
    effects.unfreeze(); forgeEffects?.unfreeze()
    val entries = ACPotions.events + GSPotions.events
    for ((id, effect) in entries) {
        if (forgeEffects != null) forgeEffects.register(id, effect) else Registry.register(effects, id, effect)
    }
    forgeEffects?.freeze(); effects.freeze()
    val attributes = BuiltInRegistries.ATTRIBUTE as MappedRegistry<Attribute>
    val forgeAttributes = RegistryManager.ACTIVE.getRegistry<Attribute>(Registries.ATTRIBUTE)
    attributes.unfreeze(); forgeAttributes?.unfreeze()
    for ((id, attribute) in ACAttributePlayer.entries) {
        if (forgeAttributes != null) forgeAttributes.register(id, attribute) else Registry.register(attributes, id, attribute)
    }
    forgeAttributes?.freeze(); attributes.freeze()
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val ops = RegistryOps.create(JsonOps.INSTANCE, access)
    for ((id, effect) in entries) {
        check(BuiltInRegistries.MOB_EFFECT.getValue(id) === effect)
        check(effect.category == MobEffectCategory.HARMFUL)
        check(effect.descriptionId == "effect.${id.namespace}.${id.path}")
        val language = JsonParser.parseString(Files.readString(resources.resolve("assets/${id.namespace}/lang/en_us.json"))).asJsonObject
        check(language.has(effect.descriptionId))
        val icon = resources.resolve("assets/${id.namespace}/textures/mob_effect/${id.path}.png")
        val image = ImageIO.read(icon.toFile())
        val size = if (id.path in listOf("overheat", "hypothermia")) 16 else 18
        check(image.width == size && image.height == size)
        val legacy = resources.resolve("assets/${id.namespace}/textures/gui/potions/${if (id.path == "antiradiation") "radiation" else id.path}.png")
        check(Files.readAllBytes(icon).contentEquals(Files.readAllBytes(legacy)))
        for (amplifier in listOf(0, 1, 4, 31, 32, 255)) {
            val instance = MobEffectInstance(ACPotions.holder(effect), 125, amplifier, true, false, true,
                MobEffectInstance(ACPotions.holder(effect), 300, 0))
            val encoded = MobEffectInstance.CODEC.encodeStart(ops, instance).getOrThrow()
            val decoded = MobEffectInstance.CODEC.parse(ops, encoded).getOrThrow()
            check(MobEffectInstance.CODEC.encodeStart(ops, decoded).getOrThrow() == encoded)
            val packet = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
            try {
                MobEffectInstance.STREAM_CODEC.encode(packet, instance)
                val received = MobEffectInstance.STREAM_CODEC.decode(packet)
                check(MobEffectInstance.CODEC.encodeStart(ops, received).getOrThrow() == encoded && !packet.isReadable)
            } finally { packet.release() }
        }
        val period = if (effect === ACPotions.hypothermia) 15 else 20
        check((0..100).filter { effect.shouldApplyEffectTickThisTick(it, 0) } == (0..100 step period).toList())
        check((0..100).filter { effect.shouldApplyEffectTickThisTick(it, 1) } == (0..100 step (period / 2)).toList())
        check((0..100).all { effect.shouldApplyEffectTickThisTick(it, 4) })
        check((0..100).filter { effect.shouldApplyEffectTickThisTick(it, 32) } == (0..100 step period).toList())
    }
    check(ACPotions.radiation.color == -4502242)
    check(ACPotions.overheat.color and 0xffffff == 0x965000)
    check(ACPotions.hypothermia.color and 0xffffff == 0x1e90ff)
    check(GSPotions.antiradiation.color and 0xffffff == 0xff0a64)
    val previousRate = AsmodeusConfig.solarRadiationDamageRate
    try {
        AsmodeusConfig.solarRadiationDamageRate = 2.5f
        check(ACPotions.radiation.damageAmount(0) == 7.5f && ACPotions.radiation.damageAmount(255) == 7.5f)
    } finally { AsmodeusConfig.solarRadiationDamageRate = previousRate }
    check(ACPotions.overheat.damageAmount(0) == 0f && ACPotions.overheat.damageAmount(3) == 0.6f)
    check(ACPotions.hypothermia.damageAmount(0) == 0f && ACPotions.hypothermia.damageAmount(3) == 0.6f)

    val modification = hashMapOf<EntityType<out LivingEntity>, AttributeSupplier.Builder>()
    val event = EntityAttributeModificationEvent(modification)
    ACAttributePlayer.addPlayerAttributes(event); ACAttributePlayer.addPlayerAttributes(event)
    val supplier = requireNotNull(modification[EntityTypes.PLAYER]).build()
    check(supplier.hasAttribute(ACAttributePlayer.RADIATION_LVL) && !supplier.hasAttribute(ACAttributePlayer.TEST))
    val map = AttributeMap(supplier)
    val radiation = requireNotNull(map.getInstance(ACAttributePlayer.RADIATION_LVL))
    check(radiation.baseValue == 0.0 && radiation.attribute.value().isClientSyncable)
    radiation.baseValue = 57.0
    check(GSPotions.antiradiation.reduceRadiation(radiation) && radiation.value == 56.0)
    radiation.baseValue = 0.25
    check(GSPotions.antiradiation.reduceRadiation(radiation) && radiation.value == -0.75)
    check(!GSPotions.antiradiation.reduceRadiation(radiation))
    radiation.baseValue = 0.0
    check(!GSPotions.antiradiation.reduceRadiation(radiation))
    radiation.baseValue = 54.0
    val saved = AttributeInstance.Packed.LIST_CODEC.encodeStart(ops, map.pack()).getOrThrow()
    val restored = AttributeMap(supplier)
    restored.apply(AttributeInstance.Packed.LIST_CODEC.parse(ops, saved).getOrThrow())
    check(restored.getValue(ACAttributePlayer.RADIATION_LVL) == 54.0)
    check(map.attributesToSync.any { it.attribute == ACAttributePlayer.RADIATION_LVL })
    val active = entries.values.map { MobEffectInstance(ACPotions.holder(it), 100) } + MobEffectInstance(MobEffects.POISON, 100)
    check(FoundationEffectCures.curable(active, ItemStack(Items.MILK_BUCKET)).toSet() == setOf(GSPotions.holder(GSPotions.antiradiation), MobEffects.POISON))
    check(FoundationEffectCures.curable(active, ItemStack(Items.HONEY_BOTTLE)).isEmpty())
    // Match the actual target method and compiled injection descriptor without claiming live weaving
    val target = net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect::class.java
    val method = target.getDeclaredMethod("apply", net.minecraft.world.level.Level::class.java, ItemStack::class.java, LivingEntity::class.java)
    check(method.returnType == Boolean::class.javaPrimitiveType)
    val mixinNode = ClassNode()
    val mixinBytes = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("foundation/mixin/MilkEffectCuresMixin.class")).use { it.readAllBytes() }
    ClassReader(mixinBytes).accept(mixinNode, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
    val injection = mixinNode.methods.single { it.name == "foundationCureMilk" }
    check(injection.desc == "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V")
    val annotation = (injection.visibleAnnotations.orEmpty() + injection.invisibleAnnotations.orEmpty()).single { it.desc == "Lorg/spongepowered/asm/mixin/injection/Inject;" }
    val options = annotation.values.chunked(2).associate { it[0] as String to it[1] }
    check(options["method"] == listOf("apply") && options["cancellable"] == true && options["remap"] == false)
    val at = (options["at"] as List<*>).single() as AnnotationNode
    check(at.values.chunked(2).associate { it[0] as String to it[1] }["value"] == "HEAD")
    verifyDamageTypes(resources, lookup)
    println("Passed four native effect registrations, tick schedules, damage amounts, names, unchanged icon pixels, hidden-effect saves and packets, player radiation attribute registration and persistence, anti-radiation reduction, and milk cure selection")
}

private fun verifyDamageTypes(resources: Path, lookup: HolderLookup.Provider) {
    val keys = listOf(ACDamageSource.solar, ACDamageSource.pressure, ACDamageSource.heat, ACDamageSource.cold,
        DamageSourceGC.spaceshipCrash, DamageSourceGC.oxygenSuffocation, DamageSourceGC.thermal, DamageSourceGC.acid, GSDamageSource.acid)
    val registry = MappedRegistry(Registries.DAMAGE_TYPE, Lifecycle.stable())
    lookup.lookupOrThrow(Registries.DAMAGE_TYPE).listElements().forEach { holder ->
        registry.register(holder.unwrapKey().orElseThrow(), holder.value(), RegistrationInfo.BUILT_IN)
    }
    for (key in keys) {
        val id = key.identifier()
        val json = JsonParser.parseString(Files.readString(resources.resolve("data/${id.namespace}/damage_type/${id.path}.json")))
        val type = DamageType.DIRECT_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
        check(type.msgId() == id.path && type.scaling() == DamageScaling.NEVER)
        check(type.exhaustion() == if (key == DamageSourceGC.acid) 0.1f else 0f)
        check(DamageType.DIRECT_CODEC.parse(JsonOps.INSTANCE, DamageType.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, type).getOrThrow()).getOrThrow() == type)
        registry.register(key, type, RegistrationInfo.BUILT_IN)
    }
    registry.freeze()
    val location = PackLocationInfo("foundation-effects", Component.literal("Foundation effects"), PackSource.BUILT_IN, Optional.empty())
    val vanilla = VanillaPackResourcesBuilder().exposeNamespace("minecraft").pushJarResources().build(location)
    MultiPackResourceManager(PackType.SERVER_DATA, listOf(vanilla.fullResources(), PathPackResources(location, resources))).use { manager ->
        val tags = TagLoader.loadTagsForRegistry(manager, registry.key(), TagLoader.ElementLookup<Holder<DamageType>> { id, _ -> registry.get(id).map { it } })
        registry.prepareTagReload(TagLoader.LoadResult(registry.key(), tags)).apply()
    }
    val access = RegistryAccess.ImmutableRegistryAccess(listOf(registry))
    val ops = RegistryOps.create(JsonOps.INSTANCE, access)
    for (key in keys) {
        val source = FoundationDamageTypes.source(access, key)
        check(source.`is`(key) && source.entity == null && source.directEntity == null)
        val bypass = key !in listOf(DamageSourceGC.acid, GSDamageSource.acid)
        check(source.`is`(DamageTypeTags.BYPASSES_ARMOR) == bypass && source.`is`(DamageTypeTags.BYPASSES_SHIELD) == bypass)
        check(source.`is`(DamageTypeTags.BYPASSES_EFFECTS) == (key in listOf(DamageSourceGC.oxygenSuffocation, DamageSourceGC.thermal, GSDamageSource.acid)))
        check(source.`is`(DamageTypeTags.IS_EXPLOSION) == (key == DamageSourceGC.spaceshipCrash))
        val holder = source.typeHolder()
        check(DamageType.CODEC.parse(ops, DamageType.CODEC.encodeStart(ops, holder).getOrThrow()).getOrThrow() == holder)
        val packet = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            DamageType.STREAM_CODEC.encode(packet, holder)
            check(DamageType.STREAM_CODEC.decode(packet) == holder && !packet.isReadable)
        } finally { packet.release() }
    }
    println("Passed nine native damage type definitions, legacy exhaustion and mitigation tags, world-owned sources, save codecs, and packet codecs")
}
