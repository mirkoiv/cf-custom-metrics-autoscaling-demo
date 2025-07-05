package example.cfasdemo

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

private val log = LoggerFactory.getLogger(RedisCacheService::class.java)

@Service
class RedisCacheService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun cacheObject(key: String, value: Any, timeoutSeconds: Long? = null) {
        val ops = redisTemplate.opsForValue()
        if (timeoutSeconds != null) {
            ops.set(key, value, timeoutSeconds, TimeUnit.SECONDS)
        } else {
            ops.set(key, value)
        }
        log.info("Cached object with key: $key")
    }

    fun getCachedObject(key: String): Any? {
        val ops = redisTemplate.opsForValue()
        val cachedObject = ops.get(key)
        if (cachedObject != null) {
            log.info("Retrieved object with key: $key")
        } else {
            log.info("Object not found in cache for key: $key")
        }
        return cachedObject
    }

    fun deleteCachedObject(key: String) {
        val deleted = redisTemplate.delete(key)
        if (deleted) {
            log.info("Deleted object with key: $key")
        } else {
            log.info("Object with key: $key not found for deletion")
        }
    }
}
