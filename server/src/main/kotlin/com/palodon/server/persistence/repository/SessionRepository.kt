package com.palodon.server.persistence.repository

import com.palodon.server.persistence.entity.SessionEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SessionRepository: PanacheRepository<SessionEntity> {
}