package example.cfasdemo

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

private val log = LoggerFactory.getLogger(CustomMetricsProvider::class.java)

interface CustomMetricsProvider {
    fun getMyCounter(): Int
}

@Service
class CustomMetricsProviderRedis(
    private val redisCacheService: RedisCacheService,
) : CustomMetricsProvider {
    override fun getMyCounter(): Int {
        // it's stored as a string in controller
        val cached = redisCacheService.getCachedObject("my_counter") as String?
        if (cached != null) {
            log.info("got my_counter $cached")
            return cached.toInt()
        }
        // implement logic for calculating myCounter
        val myCounter = 0
        redisCacheService.cacheObject("my_counter", myCounter.toString())
        return myCounter
    }
}