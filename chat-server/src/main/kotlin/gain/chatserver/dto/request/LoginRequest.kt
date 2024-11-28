package gain.chatserver.dto.request

import jakarta.validation.constraints.NotNull

data class LoginRequest(
    @NotNull
    val id: String,
    @NotNull
    val password: String
)
