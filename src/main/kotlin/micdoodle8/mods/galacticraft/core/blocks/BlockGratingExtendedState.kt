package micdoodle8.mods.galacticraft.core.blocks

import javax.annotation.Nonnull

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Iterables
import com.google.common.base.Predicates
import micdoodle8.mods.galacticraft.core.util.JavaUtil
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fluids.BlockFluidClassic
import java.util.Collections
import java.util.HashMap
import java.util.Optional

open class BlockGratingExtendedState constructor(
    blockIn: Block?,
    properties: Array<IProperty<*>?>,
    unlistedProperties: Array<IUnlistedProperty<*>?>
) : ExtendedBlockState(blockIn, properties, unlistedProperties) {
    @Nonnull
    override fun createState(
        @Nonnull block: Block,
        @Nonnull properties: ImmutableMap<IProperty<*>?, Comparable<*>?>,
        unlistedProperties: ImmutableMap<IUnlistedProperty<*>?, Optional<*>>?
    ): StateImplementation {
        if (unlistedProperties == null || unlistedProperties.isEmpty()) return super.createState(
            block,
            properties,
            unlistedProperties
        )
        return ExtendedStateImplementation(block, properties, unlistedProperties, null, null)
    }

    protected open class ExtendedStateImplementation constructor(
        private val blockBase: Block,
        properties: ImmutableMap<IProperty<*>?, Comparable<*>?>,
        private val unlistedProperties: ImmutableMap<IUnlistedProperty<*>?, Optional<*>>,
        table: ImmutableTable<IProperty<*>?, Comparable<*>?, IBlockState?>?,
        clean: IBlockState?
    ) : StateImplementation(blockBase, properties, table), IExtendedBlockState {
        private val cleanState: IBlockState

        init {
            this.cleanState = if (clean == null) this else clean
        }

        /**
         * Get a version of this BlockState with the given Property now set to the given value
         */
        @Nonnull
        override fun <T : Comparable<T>?, V : T?> withProperty(
            @Nonnull property: IProperty<T>,
            @Nonnull value: V?
        ): IBlockState {
            val clean: IBlockState = super.withProperty(property, (value)!!)
            if (clean === this.cleanState) {
                return this
            }

            if (Iterables.all<Optional<*>>(
                    unlistedProperties.values,
                    Predicates.equalTo<Optional<*>>(Optional.empty<Any?>())
                )
            ) { // no dynamic properties present, looking up in the normal table
                return clean
            }

            return ExtendedStateImplementation(
                getBlock(),
                clean.getProperties(),
                unlistedProperties,
                (clean as StateImplementation).getPropertyValueTable(),
                this.cleanState
            )
        }

        override fun <V> withProperty(property: IUnlistedProperty<V?>, value: V?): IExtendedBlockState {
            require(this.unlistedProperties.containsKey(property)) { "Cannot set unlisted property " + property + " as it does not exist in " + getBlock().getBlockState() }
            require(property.isValid(value)) {
                "Cannot set unlisted property " + property + " to " + value + " on block " + Block.REGISTRY.getNameForObject(
                    getBlock()
                ) + ", it is not an allowed value"
            }
            val newMap: MutableMap<IUnlistedProperty<*>?, Optional<*>> =
                HashMap<IUnlistedProperty<*>?, Optional<*>>(unlistedProperties)
            newMap.put(property, Optional.ofNullable<V?>(value))
            if (Iterables.all<Optional<*>>(
                    newMap.values,
                    Predicates.equalTo<Optional<*>>(Optional.empty<Any?>())
                )
            ) { // no dynamic properties, lookup normal state
                return (cleanState as net.minecraftforge.common.property.IExtendedBlockState?)!!
            }
            return ExtendedStateImplementation(
                getBlock(),
                getProperties(),
                ImmutableMap.copyOf<IUnlistedProperty<*>?, Optional<*>>(newMap),
                propertyValueTable,
                this.cleanState
            )
        }

        override fun getUnlistedNames(): MutableCollection<IUnlistedProperty<*>?> {
            return Collections.unmodifiableCollection<IUnlistedProperty<*>?>(unlistedProperties.keys)
        }

        override fun <V> getValue(property: IUnlistedProperty<V?>): V? {
            require(this.unlistedProperties.containsKey(property)) { "Cannot get unlisted property " + property + " as it does not exist in " + getBlock().getBlockState() }
            return property.getType().cast(this.unlistedProperties.get(property)!!.orElse(null))
        }

        override fun getUnlistedProperties(): ImmutableMap<IUnlistedProperty<*>?, Optional<*>>? {
            return unlistedProperties
        }

        override fun getClean(): IBlockState {
            return cleanState
        }

        override fun getBlock(): Block {
            if (JavaUtil.instance.isCalledBy(
                    BlockFluidClassic::class.java,
                    BlockFluidBase::class.java,
                    BlockFluidGC::class.java
                )
            ) {
                val bs: IBlockState? = (blockBase as BlockGrating).getLiquidBlock(this)
                if (bs != null) return bs.getBlock()
            }
            return this.blockBase
        }
    }
}
