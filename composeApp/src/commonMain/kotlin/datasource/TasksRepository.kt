package datasource

import datasource.model.TaskModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import utils.now
import utils.plusDays

class TasksRepository {
    fun getTasks(date: LocalDateTime) = _tasks.value.filter { task ->
        task.date.date == date.date
    }

    companion object {
        private val _today = LocalDateTime.now()
        private val _tasks = MutableStateFlow(
            listOf(
                TaskModel(
                    title = "Go to the gym"
                ),
                TaskModel(
                    title = "Read a book"
                ),
                TaskModel(
                    title = "Learn all about wine",
                    date = _today.plusDays(1)
                ),
                TaskModel(
                    title = "Join a toastmaster club",
                    date = _today.plusDays(1)
                ),
                TaskModel(
                    title = "Drive across the Golden Gate Bridge",
                    date = _today.plusDays(1)
                ),
                TaskModel(
                    title="Cook a new recipe for dinner",
                    date=_today.plusDays(2)
                ),
                TaskModel(
                    title="Visit a local museum",
                    date=_today.plusDays(2)
                ),
                TaskModel(
                    title="Start learning a new language",
                    date=_today.plusDays(2)
                ),
                TaskModel(
                    title="Attend a virtual workshop",
                    date=_today.plusDays(3)
                ),
                TaskModel(
                    title="Write in a gratitude journal",
                    date=_today.plusDays(3)
                ),
                TaskModel(
                    title="Explore a nearby hiking trail",
                    date=_today.plusDays(3)
                ),
                TaskModel(
                    title="Take a photography walk",
                    date=_today.plusDays(4)
                ),
                TaskModel(
                    title="Plan a weekend getaway",
                    date=_today.plusDays(4)
                ),
                TaskModel(
                    title="Join a local book club",
                    date=_today.plusDays(4)
                ),
                TaskModel(
                    title="Go for a morning run",
                    date=_today.plusDays(5)
                ),
                TaskModel(
                    title="Practice meditation",
                    date=_today.plusDays(5)
                ),
                TaskModel(
                    title="Read a chapter from a novel",
                    date=_today.plusDays(5)
                ),
            )
        )
    }
}