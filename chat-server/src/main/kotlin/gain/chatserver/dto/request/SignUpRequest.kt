package gain.chatserver.dto.request

data class SignUpRequest(
    val name: String,
    val id: String,
    val password: String
)
