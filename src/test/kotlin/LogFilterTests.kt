import de.thws.fiw.kotlin.pipeline.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LogFilterTests {

    @Test
    fun filter_log_by_level() {
        val log = listOf("INFO: User login", "ERROR: Invalid password", "WARNING: Disk almost full")
        val expected = listOf("Invalid password")

        val actual = LogFilter().filterLogByLevel(log, LogLevel.ERROR)

        assertEquals(expected, actual)
    }

    @Test
    fun get_log_level_count() {
        val log = listOf(
            "INFO: User login",
            "ERROR: Invalid password",
            "WARNING: Disk almost full",
            "ERROR: Invalid username",
            "WARNING: Battery almost empty"
        )
        val expected = mapOf(LogLevel.INFO to 1, LogLevel.ERROR to 2, LogLevel.WARNING to 2)

        val actual = LogFilter().getLogLevelCount(log)

        assertEquals(expected, actual)

    }
}