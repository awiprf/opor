package com.opor.lib.cfg.rds.aut

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * Shared Redis configuration for the Auth (aut) domain.
 *
 * Provides a pre-configured [RedisTemplate] bean with String
 * serializers. Each microservice under the aut domain depends
 * on this library and provides its own Redis connection values
 * via application.yml (spring.data.redis.*).
 *
 * The RedisConnectionFactory is auto-configured by Spring Boot
 * based on each service's application.yml properties.
 */
@Configuration
class RdAutConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = StringRedisSerializer()
        template.afterPropertiesSet()
        return template
    }
}
