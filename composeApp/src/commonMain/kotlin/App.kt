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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import datasource.model.TaskModel
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.viewmodel.viewModel
import ui.TasksListUiState
import ui.TasksListViewModel
import ui.theme.AppTheme
import utils.DAYS_PER_WEEK
import utils.getFirstDateOfWeek
import utils.getIndexOfTodayWeek
import utils.getNextDates
import utils.getPreviousDates
import utils.minus
import utils.now
import utils.plus

private const val QUANTITY_ITEMS_TO_LOAD = 8
private const val INITIAL_ITEMS_COUNT = 5
private const val INITIAL_ITEMS_TO_LOAD_PREVIOUS_AND_NEXT = INITIAL_ITEMS_COUNT - 1
private const val TIME_IN_MILLIS_TO_SCROLL_TO_NEW_INDEX = 100L

@Composable
fun App() {
    AppTheme {
        PreComposeApp {
            val viewModel = viewModel(modelClass = TasksListViewModel::class) {
                TasksListViewModel()
            }
            val uiState by viewModel.uiState.collectAsState(TasksListUiState())

            Scaffold {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(21.dp)
                ) {
                    Header(
                        selectedDate = uiState.selectedDate,
                        onSelectDate = viewModel::onSelectDate
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TasksList(
                        plannedTasks = uiState.plannedTasks
                    )
                }
            }
        }
    }
}

@Composable
fun Header(
    selectedDate: LocalDateTime,
    onSelectDate: (selectedDate: LocalDateTime) -> Unit
) {
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
        selectedDate = selectedDate,
        onSelectDate = onSelectDate,
        shouldScrollToCurrentWeek = shouldScrollToCurrentWeek.value,
        onScrollToCurrentWeek = {
            shouldScrollToCurrentWeek.value = false
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalCalendar(
    selectedDate: LocalDateTime,
    onSelectDate: (selectedDate: LocalDateTime) -> Unit,
    shouldScrollToCurrentWeek: Boolean,
    onScrollToCurrentWeek: () -> Unit
) {
    val today = remember { LocalDateTime.now() }
    val firstDateOfTodayWeek = remember { today.getFirstDateOfWeek() }
    val listOfDates = remember {
        mutableStateOf(
            getPreviousAndNextDatesSpacedBy(
                baseDate = firstDateOfTodayWeek,
                count = INITIAL_ITEMS_TO_LOAD_PREVIOUS_AND_NEXT
            )
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
            val isFirstPage = { page == 0 }
            if (isLastPage()) {
                val nextDates = listOfDates.value[page]
                    .getNextDates(count = QUANTITY_ITEMS_TO_LOAD, diff = DAYS_PER_WEEK, selfInclude = false)
                val newDates = listOfDates.value + nextDates
                listOfDates.value = newDates
            } else if (isFirstPage()) {
                val previousDates = listOfDates.value[page]
                    .getPreviousDates(count = QUANTITY_ITEMS_TO_LOAD, diff = DAYS_PER_WEEK, selfInclude = false)
                val newDates = previousDates + listOfDates.value
                listOfDates.value = newDates
                delay(TIME_IN_MILLIS_TO_SCROLL_TO_NEW_INDEX)
                pagerState.scrollToPage(QUANTITY_ITEMS_TO_LOAD)
            }
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
                    val (backgroundColor, textColor) = if (date.date == selectedDate.date) {
                        Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Pair(Color.Transparent, MaterialTheme.colorScheme.onSurface)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onSelectDate(date)
                        }
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
}

@Composable
fun TasksList(
    plannedTasks: List<TaskModel>
) {
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
        if (plannedTasks.isEmpty()) {
            item {
                Text(
                    text = "There is no planned tasks for this day!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 12.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(plannedTasks) {task ->
                Task(
                    task = task.title,
                    isPlanned = true
                )
            }
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
