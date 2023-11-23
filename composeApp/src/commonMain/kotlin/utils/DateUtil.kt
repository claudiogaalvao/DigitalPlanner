package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

const val DAYS_PER_WEEK = 7L

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}
fun LocalDateTime.plus(value: Long, unit: DateTimeUnit.TimeBased): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    return this.toInstant(timeZone)
        .plus(value, unit)
        .toLocalDateTime(timeZone)
}

fun LocalDateTime.minus(value: Long, unit: DateTimeUnit.TimeBased): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    return this.toInstant(timeZone)
        .minus(value, unit)
        .toLocalDateTime(timeZone)
}

fun LocalDateTime.getFirstDateOfWeek(
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
): LocalDateTime {
    var firstDateOfWeek = this
    while (firstDateOfWeek.dayOfWeek != firstDayOfWeek) {
        firstDateOfWeek = firstDateOfWeek.minus(24, DateTimeUnit.HOUR)
    }
    return firstDateOfWeek
}

fun List<LocalDateTime>.getIndexOfTodayWeek(): Int {
    val firstDateOfTodayWeek = LocalDateTime.now().getFirstDateOfWeek()
    return indexOfFirst { current ->
        firstDateOfTodayWeek.date == current.date
    }
}

fun LocalDateTime.getNextDates(
    count: Int = 6,
    diff: Long = 1,
    selfInclude: Boolean = true
): List<LocalDateTime> {
    val allDates = mutableListOf<LocalDateTime>()
    if (selfInclude) allDates.add(this)
    var currentDate = this
    for (nextCount in 0..< count) {
        val newDate = currentDate.plus(24 * diff, DateTimeUnit.HOUR)
        allDates.add(newDate)
        currentDate = newDate
    }
    return allDates
}

fun LocalDateTime.getPreviousDates(
    count: Int = 7,
    diff: Long = 1,
    selfInclude: Boolean = true
): List<LocalDateTime> {
    val allDates = mutableListOf<LocalDateTime>()
    if (selfInclude) allDates.add(this)
    var currentDate = this
    for (nextCount in 0..< count) {
        val newDate = currentDate.minus(24 * diff, DateTimeUnit.HOUR)
        allDates.add(newDate)
        currentDate = newDate
    }
    return allDates.reversed()
}