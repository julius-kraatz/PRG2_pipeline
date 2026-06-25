package de.thws.fiw.kotlin.pipeline

data class LogEntry(val level: LogLevel, val message: String){
    override fun toString(): String = message
}
