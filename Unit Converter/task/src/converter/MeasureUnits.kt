package converter

import kotlin.math.sign

interface IMeasureUnit {
    val basicDirectFormula: (Double) -> Double
    val basicIndirectFormula: (Double) -> Double
    val single: String
    val plural: String
    val name: String
    val basic: IMeasureUnit
    val aliases: MutableList<String>

    fun list(): Array<out IMeasureUnit>

    fun canBeConvertedTo(unit: IMeasureUnit): Boolean {
        return list().any { it.name.equals(unit.name, ignoreCase = true) }
    }

    fun assertValueValid(value: Double): Double

    fun measureClassName(): String
}

enum class Distance(
    override val basicDirectFormula: (Double) -> Double,
    override val basicIndirectFormula: (Double) -> Double,
    override val single: String,
    override val plural: String,
    vararg aliases: String,

    ) : IMeasureUnit {

    METERS({it.assertIsPositive()}, {it.assertIsPositive()}, "meter", "meters", "m"),
    KILOMETERS({it.assertIsPositive() * 1000.00}, {it.assertIsPositive() / 1000.00}, "kilometer", "kilometers", "km"),
    CENTIMETERS({it.assertIsPositive() * 0.01 }, {it.assertIsPositive() / 0.01}, "centimeter", "centimeters", "cm"),
    MILLIMETERS({it.assertIsPositive() * 0.001}, {it.assertIsPositive() /0.001}, "millimeter", "millimeters", "mm"),
    MILES({it.assertIsPositive() * 1609.35}, {it.assertIsPositive() /1609.35}, "mile", "miles", "mi"),
    YARDS({it.assertIsPositive() * 0.9144}, {it.assertIsPositive() /0.9144}, "yard", "yards", "yd"),
    FEET({it.assertIsPositive() * 0.3048}, {it.assertIsPositive() /0.3048}, "foot", "feet", "ft"),
    INCHES({it.assertIsPositive() * 0.0254}, {it.assertIsPositive() /0.0254}, "inch", "inches", "in");

    override val aliases: MutableList<String> by lazy {
        val toMutableList = aliases.toMutableList()
        toMutableList.addAll(arrayOf(single, plural))
        toMutableList
    }
    override val basic: IMeasureUnit by lazy { METERS }

    override fun list() = values()

    override fun assertValueValid(value: Double) = value.assertIsPositive()

    override fun measureClassName() = "Length"
}

enum class Mass(
    override val basicDirectFormula: (Double) -> Double,
    override val basicIndirectFormula: (Double) -> Double,
    override val single: String,
    override val plural: String,
    vararg aliases: String,
) : IMeasureUnit {
    GRAMS({it.assertIsPositive() * 1.00}, {it.assertIsPositive() / 1.00}, "gram", "grams", "g"),
    KILOGRAMS({it.assertIsPositive() * 1000.00}, {it.assertIsPositive() / 1000.00}, "kilogram", "kilograms", "kg"),
    MILLIGRAMS({it.assertIsPositive() * 0.001}, {it.assertIsPositive() / 0.001}, "milligram", "milligrams", "mg"),
    POUNDS({it.assertIsPositive() * 453.592}, {it.assertIsPositive() / 453.592}, "pound", "pounds", "lb"),
    OUNCES({it.assertIsPositive() * 28.3495}, {it.assertIsPositive() / 28.3495}, "ounce", "ounces", "oz");

    override val aliases: MutableList<String> by lazy {
        val toMutableList = aliases.toMutableList()
        toMutableList.addAll(arrayOf(single, plural))
        toMutableList
    }
    override val basic: IMeasureUnit by lazy { GRAMS }

    override fun list() = values()

    override fun assertValueValid(value: Double) = value.assertIsPositive()

    override fun measureClassName() = "Weight"
}

enum class Temperature(
    override val basicDirectFormula: (Double) -> Double,
    override val basicIndirectFormula: (Double) -> Double,
    override val single: String,
    override val plural: String,
    vararg aliases: String,
) : IMeasureUnit {

    CELSIUS({it}, {it},
        "degree Celsius", "degrees Celsius", "degree celsius", "degrees celsius", "celsius", "dc", "c"),
    FAHRENHEIT({f -> (f  - 32.00) * 5/9}, {c -> (c * 9/5) + 32.00},
        "degree Fahrenheit", "degrees Fahrenheit", "degree fahrenheit", "degrees fahrenheit", "fahrenheit", "fahrenheit", "f", "df"),
    KELVINS({k -> k - 273.15}, {c -> c + 273.15},"kelvin", "kelvins", "k");

    override val aliases: MutableList<String> by lazy {
        val toMutableList = aliases.toMutableList()
        toMutableList.addAll(arrayOf(single, plural))
        toMutableList
    }
    override val basic: IMeasureUnit by lazy { CELSIUS }

    override fun list() = values()

    override fun assertValueValid(value: Double) = value

    override fun measureClassName() = "Temperature"
}

fun getUnitOrNull(string: String): IMeasureUnit? {
    val length = Distance.values().firstOrNull { it.aliases.contains(string.toLowerCase()) }
    if (length != null) {
        return length
    }

    val mass = Mass.values().firstOrNull { it.aliases.contains(string.toLowerCase()) }
    if (mass != null) {
        return mass
    }

    val temperature = Temperature.values().firstOrNull { it.aliases.contains(string.toLowerCase()) }
    if (temperature != null) {
        return temperature
    }

    return null
}

fun Double.assertIsPositive(): Double {
    return when {
        sign(this) > 0 -> this
        else -> throw InvalidValueForMeasureUnit("Value shouldn't be negative")
    }
}