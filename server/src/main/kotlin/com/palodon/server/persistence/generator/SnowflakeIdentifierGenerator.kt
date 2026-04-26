package com.palodon.server.persistence.generator

import com.palodon.server.service.SnowflakeIdentifierService
import jakarta.enterprise.inject.spi.CDI
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext
import java.lang.reflect.Member

class SnowflakeIdentifierGenerator(
    config: SnowflakeId,
    member: Member,
    creationContext: CustomIdGeneratorCreationContext
) : IdentifierGenerator {
    private val idType = config.type

    override fun generate(session: SharedSessionContractImplementor?, `object`: Any?): Any {
        val generator = CDI.current().select(SnowflakeIdentifierService::class.java).get()
        return generator.generateId(idType)
    }
}
