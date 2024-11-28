package gain.chatserver.controller

import gain.chatserver.dto.response.UserResponse
import gain.chatserver.service.UserService
import gain.chatserver.util.ResponseFormat
import gain.chatserver.util.ResponseFormatBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.UUID

@RestController
@RequestMapping("/user")
class UserController(val userService: UserService) {
    @GetMapping("/friend")
    fun getFriends(principal: Principal): ResponseEntity<ResponseFormat<List<UserResponse>>> {
        val userUUID = UUID.fromString(principal.name)
        val result = userService.getFriends(userUUID)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.build(result))
    }
    @PostMapping("/friend")
    fun addFriend(principal: Principal, name: String): ResponseEntity<ResponseFormat<Any>> {
        val userUUID = UUID.fromString(principal.name)
        userService.addFriend(userUUID, name)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.noData())
    }
    @GetMapping()
    fun getMyInfo(principal: Principal): ResponseEntity<ResponseFormat<UserResponse>> {
        val userUUID = UUID.fromString(principal.name)
        val result = userService.getMyInfo(userUUID)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.build(result))
    }
}