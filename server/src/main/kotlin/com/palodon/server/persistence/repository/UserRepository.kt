package com.palodon.server.persistence.repository

import com.palodon.server.persistence.entity.UserEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository: PanacheRepository<UserEntity> {
}