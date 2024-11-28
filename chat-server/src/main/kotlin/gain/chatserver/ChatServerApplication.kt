package gain.chatserver

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "chat-server-api", version = "1.0"))
class ChatServerApplication

fun main(args: Array<String>) {
    runApplication<ChatServerApplication>(*args)
}
