package gain.chatserver.handler

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.annotation.OnConnect
import com.corundumstudio.socketio.annotation.OnDisconnect
import com.corundumstudio.socketio.annotation.OnEvent
import gain.chatserver.util.jwt.JwtProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class SignalingHandler2(val socketIOServer: SocketIOServer, val jwtProvider: JwtProvider) {
    init {
        socketIOServer.addListeners(this);
        socketIOServer.start();
    }

    val log: Logger = LoggerFactory.getLogger(javaClass)
    val sessions = mutableMapOf<UUID, SocketIOClient>() // userUUID, sessionId
    val userUUIDs = mutableMapOf<UUID, UUID>() // sessionID, userUUID
    val receivedCalls = mutableMapOf<UUID, MutableSet<UUID>>() // 받는쪽 uuid, 보네는 쪽 uuid
    val sendedCalls = mutableMapOf<UUID, MutableSet<UUID>>() // 나, 받는쪽
    val rooms = mutableMapOf<UUID, MutableSet<SocketIOClient>>() // random uuid

    @OnConnect
    fun onConnect(client: SocketIOClient) {
        log.info("connected " + client.sessionId)
    }

    @OnEvent("checkIn")
    fun checkIn(client: SocketIOClient, token: String) {
        val uuid = jwtProvider.getUuidByToken(token)
        receivedCalls[uuid]?.forEach {
            client.sendEvent("arrivedCall", it)
        }
        sessions[uuid] = client
        userUUIDs[client.sessionId] = uuid
        log.info("checkIn")
    }

    @OnEvent("sendCall")
    fun sendCall(client: SocketIOClient, userUUID: UUID) {
        var receivedCall = receivedCalls[userUUID]
        if (receivedCall == null) {
            receivedCall = mutableSetOf(userUUIDs[client.sessionId]!!)
        } else {
            receivedCall.add(userUUIDs[client.sessionId]!!)
        }

        val sendedCall = sendedCalls[userUUIDs[client.sessionId]]
        if (sendedCall == null) {
            sendedCalls[userUUIDs[client.sessionId]!!] = mutableSetOf(userUUID)
        } else {
            sendedCall.add(userUUID)
        }

        sessions[userUUID]?.sendEvent("arrivedCall", userUUIDs[client.sessionId]!!)
        log.info("sendCall")
    }

    @OnEvent("callAccept")
    fun callAccept(client: SocketIOClient, userUUID: UUID) {
        val userSession = sessions[userUUID]!!
        val roomUUID = UUID.randomUUID()
        rooms[roomUUID] = mutableSetOf(client, userSession)
        client.sendEvent("accept", roomUUID)
        userSession.sendEvent("accepted", roomUUID)
    }

    @OnEvent("candidate")
    fun candidate(client: SocketIOClient, payload: Map<String, Any>) {
        val roomUUID = UUID.fromString(payload["room"].toString())
        rooms[roomUUID]
                ?.filter { it != client }
                ?.map { it.sendEvent("candidate", payload) }
        log.info("onCandidate")
        log.info(client.sessionId.toString(), "  ", roomUUID.toString())
    }

    @OnEvent("offer")
    fun onOffer(client: SocketIOClient, payload: Map<String, Any?>) {
        val roomUUID = UUID.fromString(payload["room"].toString())
        val sdp = payload["sdp"]
        rooms[roomUUID]
                ?.filter { it != client }
                ?.map { it.sendEvent("offer", sdp) }
        log.info("onOffer")
        log.info(client.sessionId.toString(), "  ", roomUUID.toString())
    }

    @OnEvent("answer")
    fun onAnswer(client: SocketIOClient, payload: Map<String, Any?>) {
        val roomUUID = UUID.fromString(payload["room"].toString())
        val sdp = payload["sdp"]
        rooms[roomUUID]
                ?.filter { it != client }
                ?.forEach { it.sendEvent("answer", sdp) }
        log.info("onAnswer")
        log.info(client.sessionId.toString(), "  ", roomUUID.toString())
    }

    @OnEvent("leaveCall")
    fun onLeaveRoom(client: SocketIOClient, room: UUID) {
        rooms[room]?.forEach { it.sendEvent("callOver") }
        rooms.remove(room)
        log.info("onLeaveCall")
        log.info(client.sessionId.toString(), "  ", room.toString())
    }

    @OnDisconnect
    fun onDisconnect(client: SocketIOClient) {
        val userUUID = userUUIDs[client.sessionId]
        sessions.remove(userUUID)
        userUUIDs.remove(client.sessionId)
        sendedCalls[userUUID]?.forEach {
            receivedCalls[it]?.minus(userUUID)
        }
        sendedCalls.remove(userUUID)
        log.info("disconnected " + client.sessionId)
    }
}