import de.thws.fiw.kotlin.pipeline.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LogParserTests {

    @Test
    fun get_line_descriptions() {
        val log = listOf(
            "INFO: User login",
            "BARNING: Disk almost full",
            "ERROR: Invalid password"
        )
        val expected = "1| INFO: User login\n2| <<<Fehler>>>\n3| ERROR: Invalid password"

        val actual = LogParser().getLineDescriptions(log)

        assertEquals(expected, actual)
    }

    @Test
    fun get_valid_lines() {
        val log = listOf(
            "INFO: User login",
            "BARNING: Disk almost full",
            "ERROR: Invalid password"
        )
        val expected = "INFO: User login\nERROR: Invalid password"

        val actual = LogParser().getValidLines(log)

        assertEquals(expected, actual)
    }

}