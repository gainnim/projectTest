package gain.chatserver.config

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class WebSocketConfig {
    @Value("\${socket.host}")
    private val host: String? = null

    @Value("\${socket.port}")
    private val port = 0
    @Bean
    fun socketIOServer(): SocketIOServer {
        val config = com.corundumstudio.socketio.Configuration()
        config.hostname = host
        config.port = port
        return SocketIOServer(config)
    }
}

