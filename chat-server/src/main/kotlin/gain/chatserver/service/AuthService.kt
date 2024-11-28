package gain.chatserver.service

import gain.chatserver.dto.request.LoginRequest
import gain.chatserver.dto.request.SignUpRequest
import gain.chatserver.dto.response.LoginResponse
import gain.chatserver.entity.User
import gain.chatserver.repository.UserRepository
import gain.chatserver.util.error.CustomError
import gain.chatserver.util.error.ErrorState
import gain.chatserver.util.jwt.JwtProvider
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(val userRepository: UserRepository, val passwordEncoder: BCryptPasswordEncoder, val jwtProvider: JwtProvider) {
    fun signUp(request: SignUpRequest) {
        userRepository.findUserByName(request.name)?.let { throw CustomError(ErrorState.NAME_IS_ALREADY_USED) }
        userRepository.findUserById(request.id)?.let { throw CustomError(ErrorState.ID_IS_ALREADY_USED) }
        val user = User(
                name = request.name,
                id = request.id,
                password = passwordEncoder.encode(request.password)
        )
        userRepository.save(user)
    }
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findUserById(request.id) ?: throw CustomError(ErrorState.NOT_FOUND_ID)
        if (!passwordEncoder.matches(request.password, user.password)) throw CustomError(ErrorState.WRONG_PASSWORD)
        val accessToken = jwtProvider.createToken(user.uuid)
        return LoginResponse(accessToken = accessToken)
    }
}