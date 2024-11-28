package gain.chatserver.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        val uuid: UUID = UUID.randomUUID(),
        val id: String,
        val password: String,
        val name: String,
        @ManyToMany
        val friends: List<User> = listOf()
)