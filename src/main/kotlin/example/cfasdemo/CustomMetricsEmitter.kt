package example.cfasdemo

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URI

private val log = LoggerFactory.getLogger(CustomMetricsEmitter::class.java)

@Component
class CustomMetricsEmitter(
    private val cfEnv: CfEnv,
    private val customMetricsProvider: CustomMetricsProvider,
    private val webClientProvider: WebClientProvider
) {
    @Scheduled(fixedDelayString = "PT30S", initialDelayString = "PT30S")
    fun emitMetrics() {
        val myCounter: Int = customMetricsProvider.getMyCounter()

        log.info("emit my_counter: $myCounter")

        val metrics = Metrics(
            instanceIndex = cfEnv.cfInstanceIndex,
            metrics = listOf(
                Metric(name = "my_counter", value = myCounter)
            )
        )
        webClientProvider.getWebClient()
            .post()
            .uri(URI.create(cfEnv.customMetricsEndpoint))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(metrics)
            .retrieve()
            .toEntity(String::class.java).subscribe { responseEntity ->
                log.info("response ${responseEntity.statusCode} ${responseEntity.body}")
            }
    }
}
