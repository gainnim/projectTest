//package gain.chatserver.handler
//
//import com.corundumstudio.socketio.AckRequest
//import com.corundumstudio.socketio.SocketIOClient
//import com.corundumstudio.socketio.SocketIOServer
//import com.corundumstudio.socketio.annotation.OnConnect
//import com.corundumstudio.socketio.annotation.OnDisconnect
//import com.corundumstudio.socketio.annotation.OnEvent
//import gain.chatserver.util.jwt.JwtProvider
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Component
//import java.util.*
//
//@Component
//class SignalingHandler(val socketIOServer: SocketIOServer, val jwtProvider: JwtProvider) {
//    init {
//        socketIOServer.addListeners(this);
//        socketIOServer.start();
//    }
//    val log = LoggerFactory.getLogger(javaClass)
//    val sessions = mutableMapOf<UUID, UUID?>() // sessionID, roomID
//    val rooms = mutableMapOf<UUID, UUID>() // 받는쪽 UUID, 보네는 쪽 UUID
//
//    @OnConnect
//    fun onConnect(client: SocketIOClient) {
//        log.info("connected " + client.sessionId)
//        sessions.put(client.sessionId, null)
//    }
//    @OnDisconnect
//    fun onDisconnect(client: SocketIOClient) {
//        val clientId = client.sessionId
//        val room = sessions.get(client.sessionId)
//        if (room != null) {
//            sessions.remove(client.sessionId)
//            client.namespace.getRoomOperations(room.toString()).sendEvent("userDisconnected", clientId.toString())
//        }
//        log.info("disconnectd " + client.sessionId)
//        log.info(clientId.toString(), "  " , room.toString())
//    }
//    @OnEvent("test")
//    fun onTest(client: SocketIOClient, test: String) {
//        client.sendEvent("ok")
//        log.info(test)
//    }
//    // todo token validation
//    @OnEvent("joinRoom")  // room = 내가 통화 걸때 = 상대 uuid, 내가 통화 받을때 = 내 uuid
//    fun onJoinRoom(client: SocketIOClient, token: String, room: UUID) {   // principal = 전화거는 사람 = 내 uuid todo 수정하기, room 통화 받는 사람
//        val principal = jwtProvider.getUuidByToken(token)
//        if (socketIOServer.getRoomOperations(room.toString()).clients.size == 0) {  // 전화걸때
//            client.joinRoom(room.toString())
//            client.sendEvent("created", room)
//            sessions.put(client.sessionId, room)
//            rooms.put(room, principal)
//        }else if (socketIOServer.getRoomOperations(room.toString()).clients.size == 1) {  //  전화 받을때
//            client.joinRoom(room.toString())
//            client.sendEvent("joined", room)
//            sessions.put(client.sessionId, room)
//            client.sendEvent("setCaller", room) // 상대방 uuid 넣어야함
//        }else {
//            client.sendEvent("full", room)
//        }
//        log.info("onJoined")
//        log.info(client.sessionId.toString(), "  ", room.toString())
//    }
//
//    @OnEvent("ready")
//    fun onReady(client: SocketIOClient, room: UUID, ackRequest: AckRequest) {
//        client.namespace.getRoomOperations(room.toString()).sendEvent("ready", room)
//        log.info("onReady")
//        log.info(client.sessionId.toString(), "  ", room.toString())
//    }
//
//    @OnEvent("candidate")
//    fun onCandidate(client: SocketIOClient, payload: Map<String, Any>) {
//        val room = payload.get("room") as String
//        client.namespace.getRoomOperations(room).sendEvent("candidate", payload);
//        log.info("onCandidate")
//        log.info(client.sessionId.toString(), "  ", room.toString())
//    }
//
//    @OnEvent("offer")
//    fun onOffer(client: SocketIOClient, payload: Map<String, Any?>) {
//        val room = payload["room"] as String
//        val sdp = payload["sdp"]
//        client.namespace.getRoomOperations(room).sendEvent("offer", sdp)
//        log.info("onOffer")
//        log.info(client.sessionId.toString(), "  ", room.toString())
//    }
//
//    @OnEvent("answer")
//    fun onAnswer(client: SocketIOClient, payload: Map<String, Any?>) {
//        val room = payload["room"] as String
//        val sdp = payload["sdp"]
//        client.namespace.getRoomOperations(room).sendEvent("answer", sdp)
//        log.info("onAnswer")
//        log.info(client.sessionId.toString(), "  ", room.toString())
//    }
//
//    @OnEvent("leaveRoom")
//    fun onLeaveRoom(client: SocketIOClient, room: String) {
//        client.leaveRoom(room)
//        log.info("onLeaveRoom")
//        log.info(client.sessionId.toString(), "  ", room.toString())
//    }
//}