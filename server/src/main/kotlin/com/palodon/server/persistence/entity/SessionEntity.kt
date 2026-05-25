package com.palodon.server.persistence.entity

import com.palodon.server.enumerator.SnowflakeIdType
import com.palodon.server.persistence.generator.SnowflakeId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kotlin.time.Clock
import kotlin.time.Instant

@Entity
@Table(schema = "palodon", name = "session")
class SessionEntity {
    @Id
    @SnowflakeId(type = SnowflakeIdType.SESSION)
    @Column(name = "session_id", nullable = false, updatable = false, unique = true)
    var sessionId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null

    @Column(name = "refresh_token_hash", nullable = false, unique = true)
    var refreshTokenHash: String? = null

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Clock.System.now()

    @Column(name = "last_used_at", nullable = false)
    var lastUsedAt: Instant = Clock.System.now()

    @Column(name = "user_agent")
    var userAgent: String? = null

    @Column(name = "ip_address")
    var ipAddress: String? = null

    @Column(name = "is_revoked", nullable = false)
    var isRevoked: Boolean = false
}