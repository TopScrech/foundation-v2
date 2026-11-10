package galaxyspace.api.entity

/**
 * RUS: Интерфейс для сущностей, имеющих несколько посадочных мест
 * EN: Interface for multi-seats entities
 * @author gug2
 */
interface IMultiSeatEntity {
    fun getMaxSeats(): Int
}
