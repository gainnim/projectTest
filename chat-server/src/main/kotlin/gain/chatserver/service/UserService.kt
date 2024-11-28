package gain.chatserver.service

import gain.chatserver.dto.response.UserResponse
import gain.chatserver.repository.UserRepository
import gain.chatserver.util.error.CustomError
import gain.chatserver.util.error.ErrorState
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(val userRepository: UserRepository) {
    fun getFriends(userUUID: UUID): List<UserResponse> {
        val user = userRepository.findUserByUuid(userUUID) ?: throw CustomError(ErrorState.NOT_FOUND_USER)
        return user.friends.map { UserResponse(
                it.uuid,
                it.name
        ) }
    }
    fun addFriend(userUUID: UUID, name: String) {
        val user = userRepository.findUserByUuid(userUUID) ?: throw CustomError(ErrorState.NOT_FOUND_USER)
        val friend = userRepository.findUserByName(name) ?: throw CustomError(ErrorState.NOT_FOUND_USER)
        userRepository.save(user.copy(
                friends = user.friends.plus(friend)
        ))
    }
    fun getMyInfo(userUUID: UUID): UserResponse {
        val user = userRepository.findUserByUuid(userUUID) ?: throw CustomError(ErrorState.NOT_FOUND_USER)
        return user.let {
            UserResponse(
                    it.uuid,
                    it.name
            )
        }
    }
}