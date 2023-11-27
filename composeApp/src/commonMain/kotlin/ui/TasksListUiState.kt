package ui

import kotlinx.datetime.LocalDateTime
import datasource.model.TaskModel
import utils.now

data class TasksListUiState(
    val selectedDate: LocalDateTime = LocalDateTime.now(),
    val plannedTasks: List<TaskModel> = emptyList()
)