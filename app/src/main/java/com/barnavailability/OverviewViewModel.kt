package com.barnavailability

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*

private const val MAXROOMS = 10
private const val MAXDAYS = 7

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<String>()
    private val _wbDate = MutableLiveData<String>()
    private val _roomNames = MutableLiveData<Array<String>>()
    private val _events = MutableLiveData<Array<Array<SpannableString>>>()

    // The external immutable LiveData used in the fragment
    val wbDate: LiveData<String> = _wbDate
    val roomNames: LiveData<Array<String>> = _roomNames
    val events: LiveData<Array<Array<SpannableString>>> = _events

    private val privEvents = Array(MAXROOMS) { _ -> Array(MAXDAYS) { _ ->  SpannableString("") } }
    private val privRoomNames = Array(MAXROOMS) {""}
    private var error = false
    private val dateFormat = SimpleDateFormat("d MMM yyyy")
    private val date = Calendar.getInstance()

    init {
        setToMonday()
        fetchEventsForWeek()
    }

    private fun setToMonday() {
        while (date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
//            Log.d("setting", "going back one day")
            date.add(Calendar.DATE, -1)                             // go back one day
        }
    }

    private fun clearEvents() {
        for (i in 0 until MAXROOMS) {
            for (j in 0 until MAXDAYS) {
                privEvents[i][j] = SpannableString("")
            }
        }
        error = false
        _wbDate.value = "Please wait"
        _status.value = ""
    }

    fun fetchEventsForDate(day: Int, month: Int, year: Int) {
        date.set(year, month, day)
        setToMonday()
        fetchEventsForWeek()
    }

    fun fetchEventsForPreviousWeek() {
        date.add(Calendar.DATE, -7)
        fetchEventsForWeek()
    }

    fun fetchEventsForNextWeek() {
        date.add(Calendar.DATE, 7)
        fetchEventsForWeek()
    }

    private fun add_colour(str: String): SpannableString {
        val spannable = SpannableString(str)
//        Log.d("spanning", str)
        var count = 0
        for (line in str.split("\n")) {
//            Log.d("spanning", count.toString())
            val splitStr = line.split(":")
            if (splitStr.size > 1) {
                val start1 = count
                val end1 = splitStr[0].length + 3 + count
                var start2 = splitStr[0].length + 1 + splitStr[1].length - 2 + count
                if (str[start2] == ' ') {
                    start2++
                }
                val end2 = line.length + count
//                Log.d("spanning", line + " " + start1 + " " + end1 + " " + start2 + " " + end2)
                spannable.setSpan(
                    ForegroundColorSpan(Color.BLUE),
                    start1,
                    end1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.RED),
                    start2,
                    end2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            count += line.length + 1
        }
        return spannable
    }

    private fun parsePageToEvents(page: String) {
        error = false
        clearEvents()
        try {
            val html = Jsoup.parse(page)
//            Log.d("parsing", html.toString())
            val table: Element = html.getElementById("calendar")
//            Log.d("parsing", "table found")
            val rows: Elements = table.select("tr")
//            Log.d("parsing", "number of row = ".plus(rows.size))
            val headers: Elements = rows[0].select("th")
//            Log.d("parsing", "number of rooms = ".plus(headers.size - 1))
            for (i in 1 until headers.size) {
                privRoomNames[i] = headers[i].text()
//                Log.d("parsing", "added room ${privRoomNames[i]}")
            }
            for (i in 1..MAXDAYS) {
//                Log.d("parsing", "day ".plus(i))
                val rooms: Elements = rows[i].select("td")
                for (j in 0 until headers.size) {
                    var str = ""
                    val ul: Elements = rooms[j].select("ul")
                    if (ul.size > 0) {
                        val li: Elements = ul[0].select("li")
                        //                                Log.d("parsing", "number of events ".plus(li.size))
                        for (k in 0 until li.size) {
                            if (str.isNotEmpty()) {
                                str = str.plus("\n").plus(li[k].text())
                            } else {
                                str = li[k].text()

                            }
                        }
                    } else {
                        str = rooms[j].wholeText()
                    }
                    privEvents[j][i - 1] = add_colour(str)
                }
            }
        } catch (e: Exception) {
            Log.d("parsing", "caught error ".plus(e.toString()))
            error = true
        }
    }

    private fun fetchEventsForWeek() {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DAY_OF_MONTH)
        val path = "https://www.rbooker.co.uk/calendar/tithebarn/230?type=week&day=".plus(day).plus("&month=").plus(month).plus("&year=").plus(year)
        _wbDate.value = "Fetching ..."
        viewModelScope.launch {
            try {
                val page = BarnApi.retrofitService.getPage(path)
                parsePageToEvents(page)
                _wbDate.value = dateFormat.format(date.time)
                _status.value = "Error: ".plus(error)
            } catch (e: Exception) {
                Log.d("fetch", e.message.toString())
                _wbDate.value = "Failure: ${e.message}"
                error = true
            }
            _roomNames.value = privRoomNames
            _events.value = privEvents
        }
    }
}
