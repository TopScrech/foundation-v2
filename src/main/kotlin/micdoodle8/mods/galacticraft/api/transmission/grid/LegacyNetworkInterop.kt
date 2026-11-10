package micdoodle8.mods.galacticraft.api.transmission.grid

// Preserved the unchecked calls permitted by the original raw Java network API
@Suppress("UNCHECKED_CAST")
internal fun IGridNetwork<*, *, *>.splitUntyped(connection: Any?) {
    (this as IGridNetwork<Any?, Any?, Any?>).split(connection)
}

@Suppress("UNCHECKED_CAST")
internal fun IGridNetwork<*, *, *>.mergeUntyped(other: IGridNetwork<*, *, *>?): IGridNetwork<*, *, *>? {
    return (this as IGridNetwork<Any?, Any?, Any?>).merge(other) as IGridNetwork<*, *, *>?
}
