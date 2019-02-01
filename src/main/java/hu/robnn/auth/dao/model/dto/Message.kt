package hu.robnn.auth.dao.model.dto

enum class Severity{
    INFO,
    WARNING,
    ERROR,
    DEBUG,
}

class Message(var severity: Severity, var message: String)

