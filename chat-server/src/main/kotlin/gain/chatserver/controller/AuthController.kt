package gain.chatserver.controller

import gain.chatserver.dto.request.LoginRequest
import gain.chatserver.dto.request.SignUpRequest
import gain.chatserver.dto.response.LoginResponse
import gain.chatserver.service.AuthService
import gain.chatserver.util.ResponseFormat
import gain.chatserver.util.ResponseFormatBuilder
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(val authService: AuthService) {
    val log = LoggerFactory.getLogger(javaClass)
    @PostMapping("/sign-up")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<ResponseFormat<Any>> {
        authService.signUp(request)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.noData())
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<ResponseFormat<LoginResponse>> {
        val result = authService.login(request)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.build(result))
    }
}