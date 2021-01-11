package converter

import java.lang.Exception
import java.util.*

object GlobalState {
    var inProgress: Boolean = true
}

val scanner = Scanner(System.`in`)

fun main() {

    while (GlobalState.inProgress) {
        println("Enter what you want to convert (or exit):")
        parseInputAndAct()
    }
}

fun parseInputAndAct() {

    val line = scanner.nextLine()
    val split = line.split(" ")

    if (split[0] == "exit") {
        exit()
        return
    }

    val findSrc = Regex("^(-?\\d.*?\\s)(.*)(?=\\s(convertTo|in|to)\\s)").find(line)
    val findDist = Regex("(?<=\\s(convertTo|in|to)\\s).*\$").find(line)

    try {

        val match = findSrc!!
        val srcValue = match
            .groups[1]!!
            .value.toDouble()

        val srcUnit = getUnitOrNull(match.groups[2]!!.value)
        val distUnit = getUnitOrNull(findDist!!.groups[0]!!.value)

        if (srcUnit == null || distUnit == null || !srcUnit.canBeConvertedTo(distUnit)) {
            val srcStr = (srcUnit?.plural ?: "???").toLowerCase()
            val distStr = (distUnit?.plural ?: "???").toLowerCase()
            println("Conversion from $srcStr to $distStr is impossible")
        } else {
            val measure = Measure(srcValue, srcUnit)
            println("$measure is ${measure.to(distUnit)}")
        }
    } catch (e: InvalidMeasureValue){
        println(e.message)
    } catch (e: Exception) {
        println("Parse error")
        return
    }
}

fun exit() {
    GlobalState.inProgress = false
}