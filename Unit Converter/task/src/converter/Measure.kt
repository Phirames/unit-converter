package converter

import java.lang.RuntimeException

class Measure(private val value: Double, private val measureUnit: IMeasureUnit) {

    init {
        try {
            measureUnit.assertValueValid(value)
        } catch (e: InvalidValueForMeasureUnit) {
            throw InvalidMeasureValue("${measureUnit.measureClassName()} shouldn't be negative")
        }
    }

    fun to(unit: IMeasureUnit): Measure {

        return when {
            !measureUnit.canBeConvertedTo(unit) -> throw RuntimeException("Can not be converted to $unit")
            else -> {
                val basic = measureUnit.basicDirectFormula(value);
                val dist = unit.basicIndirectFormula(basic)

                Measure(dist, unit)
            }
        }
    }

    override fun toString(): String {

        val measureUnitAlias = when (value) {
            -1.00, 1.00 -> measureUnit.single
            else -> measureUnit.plural
        }

        return "$value $measureUnitAlias"
    }

}