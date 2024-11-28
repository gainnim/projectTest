package gain.chatserver.util.error

class CustomError(val reason: ErrorState): RuntimeException(reason.message)