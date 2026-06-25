package de.thws.fiw.kotlin.pipeline

class LogFilter {
    fun filterLogByLevel(log: List<String>, level: LogLevel): List<String> {
        return log.filter { it.startsWith(level.text) }
            .map { it.substring(level.text.length).trimStart() }
    }

    fun getLogLevelCount(log: List<String>): Map<LogLevel, Int> {
        return LogLevel.entries.associateWith { level ->
            log.count { it.startsWith(level.text) }
        }
    }
}