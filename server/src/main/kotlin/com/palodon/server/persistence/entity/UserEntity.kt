package com.palodon.server.persistence.entity

import com.palodon.server.enumerator.SnowflakeIdType
import com.palodon.server.persistence.generator.SnowflakeId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(schema = "palodon", name = "user")
class UserEntity {
    @Id
    @SnowflakeId(type = SnowflakeIdType.USER)
    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    var userId: Long? = null

    @Column(name = "username", nullable = false, updatable = false, unique = true)
    var username: String? = null

    @Column(name = "displayname", nullable = false)
    var displayname: String? = null

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
}