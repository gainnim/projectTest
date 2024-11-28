package gain.chatserver.repository

import gain.chatserver.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findUserById(id: String): User?
    fun findUserByName(name: String): User?
    fun findUserByUuid(uuid: UUID): User?
}