package example.cfasdemo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.NoOpTaskScheduler
import org.springframework.test.context.bean.override.mockito.MockitoBean


@SpringBootTest(properties = [
	"vcap.application.application_id=app-id",
	"vcap.services.autoscaler.credentials.custom_metrics.mtls_url=http://metrics.url",
	"CF_INSTANCE_INDEX=0",
	"CF_INSTANCE_CERT=/etc/certs/instance.crt",
	"CF_INSTANCE_KEY=/etc/certs/instance.key",
])
class CfAsDemoApplicationTests {

	@MockitoBean
	private lateinit var redisCacheService: RedisCacheService

	@MockitoBean
	private lateinit var customMetricsEmitter: CustomMetricsEmitter

	@Test
	fun contextLoads() {
	}

}
