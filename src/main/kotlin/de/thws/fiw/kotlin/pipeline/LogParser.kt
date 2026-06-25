package de.thws.fiw.kotlin.pipeline

class LogParser {
    private object Format {
        fun lineDescription(lineNumber: Int, message: String): String =
            "$lineNumber| $message"
    }

    private sealed class Result<out T> {
        abstract override fun toString(): String

        data class Success<T>(val value: T) : Result<T>(){
            override fun toString(): String = value.toString()
        }

        object Failure : Result<Nothing>(){
            override fun toString(): String = "<<<Fehler>>>"
        }

        fun getValueOrNull(): T? = when (this) {
            is Success -> value
            is Failure -> null
        }

        fun toLineDescription(index: Int) : String =
            Format.lineDescription(index + 1, toString())
    }

    private fun findLogLevel(line: String): LogLevel? =
        LogLevel.entries.find { line.startsWith(it.text) }

    private fun parseLog(line: String): Result<LogEntry> {
        val level = findLogLevel(line)
        val result =
            if (level != null) Result.Success(LogEntry(level, line))
            else Result.Failure
        return result
    }

    fun getLineDescriptions(log: List<String>): String {
        return log
            .map { parseLog(it) }
            .mapIndexed { index, result -> result.toLineDescription(index) }
            .joinToString("\n")
    }

    fun getValidLines(log: List<String>): String {
        return log
            .map { parseLog(it) }
            .mapNotNull { it.getValueOrNull() }
            .joinToString("\n")
    }
}