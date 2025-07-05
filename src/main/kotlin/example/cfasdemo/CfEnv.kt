package example.cfasdemo

import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Component

@Component
class CfEnv(private val env: Environment) {
    init {
        requireNotNull(env["vcap.application.application_id"])
        requireNotNull(env["vcap.services.autoscaler.credentials.custom_metrics.mtls_url"])
        requireNotNull(env["CF_INSTANCE_INDEX"])
        requireNotNull(env["CF_INSTANCE_KEY"])
        requireNotNull(env["CF_INSTANCE_CERT"])
    }
    val applicationId: String
        get() = env["vcap.application.application_id"]!!

    val customMetricsMtlsUrl: String
        get() = env["vcap.services.autoscaler.credentials.custom_metrics.mtls_url"]!!

    val customMetricsEndpoint: String
        get() = "$customMetricsMtlsUrl/v1/apps/$applicationId/metrics"

    val cfInstanceIndex: Int
        get() = env["CF_INSTANCE_INDEX"]!!.toInt()

    val cfInstanceKey: String
        get() = env["CF_INSTANCE_KEY"]!!

    val cfInstanceCert: String
        get() = env["CF_INSTANCE_CERT"]!!
}