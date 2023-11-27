package datasource.model

import kotlinx.datetime.LocalDateTime
import utils.now

data class TaskModel(
    val title: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val isDone: Boolean = false
)