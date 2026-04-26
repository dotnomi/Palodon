package com.palodon.server.persistence.generator

import com.palodon.server.enumerator.SnowflakeIdType
import org.hibernate.annotations.IdGeneratorType

@IdGeneratorType(SnowflakeIdentifierGenerator::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class SnowflakeId(val type: SnowflakeIdType = SnowflakeIdType.USER)
