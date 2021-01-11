package converter

open class ConverterException(override val message: String? = null): RuntimeException(message)

class InvalidValueForMeasureUnit(message: String? = null): ConverterException(message)

class InvalidMeasureValue(message: String? = null): ConverterException(message)