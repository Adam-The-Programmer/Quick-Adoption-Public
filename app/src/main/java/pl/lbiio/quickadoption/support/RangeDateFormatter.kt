package pl.lbiio.quickadoption.support

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
class RangeDateFormatter : DatePickerFormatter {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun formatDate(
        dateMillis: Long?,
        locale: CalendarLocale,
        forContentDescription: Boolean
    ): String? {
        if (dateMillis != null) {
            val date = LocalDate.ofEpochDay(dateMillis / 86400000) // Convert millis to LocalDate

            // Format the date as "yy MM dd"
            return String.format(
                "%02d.%02d.%02d",
                date.dayOfMonth,
                date.monthValue,
                date.year
            )
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String? {
        if (monthMillis != null) {
            val yearMonth = YearMonth.of(
                LocalDate.ofEpochDay(monthMillis / 86400000).year,
                LocalDate.ofEpochDay(monthMillis / 86400000).month
            )

            // Format the month and year, e.g., "Sep 23"
            return String.format(
                "%s.%02d",
                yearMonth.month.toString().substring(0, 3), // Get the first three characters of the month name
                yearMonth.year
            )
        }
        return null
    }
}