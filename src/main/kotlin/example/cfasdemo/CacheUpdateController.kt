package example.cfasdemo

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class CacheUpdateController(
    val redisCacheService: RedisCacheService,
) {
    @GetMapping("/api/cache/{name}")
    fun cacheGet(@PathVariable name: String): Any? {
        return redisCacheService.getCachedObject(name)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/api/cache/{name}/{value}")
    fun cachePut(@PathVariable name: String, @PathVariable value: String) {
        redisCacheService.cacheObject(name, value, 3600)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/cache/{name}")
    fun cacheDelete(@PathVariable name: String) {
        redisCacheService.deleteCachedObject(name)
    }
}
