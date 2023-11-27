package ui

import datasource.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import moe.tlaster.precompose.viewmodel.ViewModel
import utils.now

class TasksListViewModel(
    private val tasksRepository: TasksRepository = TasksRepository()
): ViewModel() {

    private val _uiState = MutableStateFlow(
        TasksListUiState(
            plannedTasks = tasksRepository.getTasks(LocalDateTime.now())
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onSelectDate(selectedDate: LocalDateTime) {
        val newTasks = tasksRepository.getTasks(selectedDate)
        _uiState.update {
            it.copy(
                selectedDate = selectedDate,
                plannedTasks = newTasks.toList()
            )
        }
    }

}