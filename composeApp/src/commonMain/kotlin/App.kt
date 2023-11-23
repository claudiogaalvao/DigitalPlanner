import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ui.theme.AppTheme
import utils.getFirstDateOfWeek
import utils.getIndexOfTodayWeek
import utils.getNextDates
import utils.minus
import utils.now
import utils.plus

@Composable
fun App() {
    AppTheme {
        Scaffold {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(21.dp)
            ) {
                Header()

                Spacer(modifier = Modifier.height(20.dp))

                TasksList()
            }
        }
    }
}

@Composable
fun Header() {
    val shouldScrollToCurrentWeek = remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.clickable {
                shouldScrollToCurrentWeek.value = true
            }
        ) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Text(
                text = todaysDate(),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        Row(
            modifier = Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(100))
                .padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "",
                tint = Color.Gray
            )
            Text(
                text = "0",
                fontSize = 20.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    HorizontalCalendar(
        shouldScrollToCurrentWeek = shouldScrollToCurrentWeek.value,
        onScrollToCurrentWeek = {
            shouldScrollToCurrentWeek.value = false
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalCalendar(
    shouldScrollToCurrentWeek: Boolean,
    onScrollToCurrentWeek: () -> Unit
) {
    val today = remember { LocalDateTime.now() }
    val firstDateOfTodayWeek = remember { today.getFirstDateOfWeek() }
    val listOfDates = remember {
        mutableStateOf(
            getPreviousAndNextDatesSpacedBy(baseDate = firstDateOfTodayWeek, count = 4)
        )
    }
    val pagerState = rememberPagerState(
        initialPage = listOfDates.value.getIndexOfTodayWeek(),
        pageCount = {
            listOfDates.value.size
        }
    )

    if (shouldScrollToCurrentWeek) {
        LaunchedEffect(pagerState) {
            pagerState.animateScrollToPage(listOfDates.value.getIndexOfTodayWeek())
            onScrollToCurrentWeek()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val isLastPage = { page == listOfDates.value.size - 1 }
            if (isLastPage()) {
                val nextDates = listOfDates.value[page]
                    .getNextDates(count = 2, diff = 7, selfInclude = false)
                val newDates = listOfDates.value + nextDates
                listOfDates.value = newDates
            }
            // TODO Get previous dates
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
    ) {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill
        ) { pageIndex ->
            val allDatesOfWeek = listOfDates.value[pageIndex].getNextDates()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (date in allDatesOfWeek) {
                    val (backgroundColor, textColor) = if (date == today) {
                        Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Pair(Color.Transparent, MaterialTheme.colorScheme.onSurface)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = date.dayOfWeek.name.take(3),
                            fontWeight = FontWeight.Light,
                            color = Color.Gray
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(top = 4.dp)
                                .background(backgroundColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                fontWeight = FontWeight.Light,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }

//    LazyRow(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(
//                MaterialTheme.colorScheme.surface,
//                RoundedCornerShape(8.dp)
//            )
//    ) {
//        items(dates) { date ->
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 12.dp),
//            ) {
//                Text("SEM")
//                Text(
//                    text = "${date.dayOfMonth}"
//                )
//            }
//        }
//    }

//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
//        horizontalArrangement = Arrangement.SpaceAround
//    ) {
//        daysOfWeek.forEachIndexed { index, day ->
//            Column(
//                modifier = Modifier.padding(vertical = 12.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = day.uppercase(),
//                    fontWeight = FontWeight.Light,
//                    color = Color.Gray
//                )
//                Text(
//                    modifier = Modifier.padding(top = 4.dp),
//                    text = index.toString(),
//                    fontWeight = FontWeight.Light,
//                    color = Color.White
//                )
//            }
//        }
//    }
}

@Composable
fun TasksList() {
    val plannedTasks = listOf("Go to the gym", "Study programming", "Read a book")
    val queuedTasks = listOf("Schedule dentist", "Request refund")
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Planned tasks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
        items(plannedTasks) {task ->
            Task(
                task = task,
                isPlanned = true
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(20.dp)
            )
            Text(
                text = "Queued tasks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
        items(queuedTasks) { task ->
            Task(
                task = task,
                isPlanned = false
            )
        }
    }
}

@Composable
fun Task(
    task: String,
    isPlanned: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPlanned) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = "",
                tint = Color.Gray
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "",
                tint = Color.Gray
            )
        }
    }
}

fun todaysDate(): String {
    fun LocalDateTime.format() = "${month.toString().lowercase().replaceFirstChar { it.uppercase() }} $dayOfMonth"

    val now = Clock.System.now()
    val zone = TimeZone.currentSystemDefault()
    return now.toLocalDateTime(zone).format()
}

fun getPreviousAndNextDatesSpacedBy(
    baseDate: LocalDateTime,
    count: Int,
    diff: Long = 7
): List<LocalDateTime> {
    val countForEach = count/2
    val allDates = mutableListOf<LocalDateTime>()
    val previousDates = mutableListOf<LocalDateTime>()
    val nextDates = mutableListOf<LocalDateTime>()
    var currentDate = baseDate
    for (previousCount in 0..<countForEach) {
        val newDate = currentDate.minus(24 * diff, DateTimeUnit.HOUR)
        previousDates.add(newDate)
        currentDate = newDate
    }
    currentDate = baseDate
    for (nextCount in 0..<countForEach) {
        val newDate = currentDate.plus(24 * diff, DateTimeUnit.HOUR)
        nextDates.add(newDate)
        currentDate = newDate
    }
    allDates.addAll(previousDates.reversed())
    allDates.add(baseDate)
    allDates.addAll(nextDates)
    return allDates
}
