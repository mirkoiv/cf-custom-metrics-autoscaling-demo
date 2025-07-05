package example.cfasdemo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class CfAsDemoApplication {
	@Bean
	fun redisTemplate(
		connectionFactory: RedisConnectionFactory?,
		objectMapper: ObjectMapper
	): RedisTemplate<String, Any> {
		val template = RedisTemplate<String, Any>()
		template.connectionFactory = connectionFactory
		return template
	}
}

fun main(args: Array<String>) {
	runApplication<CfAsDemoApplication>(*args)
}
