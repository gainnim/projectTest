package gain.chatserver.dto.response

import java.util.UUID

data class UserResponse(
        val uuid: UUID,
        val name: String
)
