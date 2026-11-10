package micdoodle8.mods.galacticraft.api.entity


/**
 * Implement into entities to allow transmission of data via telemetry
 */
interface ITelemetry {
    fun transmitData(data: IntArray?)

    fun receiveData(data: IntArray?, str: Array<String?>?)

    fun adjustDisplay(data: IntArray?)
}
