package example.cfasdemo

import com.fasterxml.jackson.annotation.JsonProperty

data class Metric (

    /* The name of the custom metric, must have been defined by using it in the scaling policy for the application before submitting. */
    @JsonProperty("name")
    val name: String,

    /* The value of the custom metric. */
    @JsonProperty("value")
    val value: Int,

    /* An optional unit for the metric, for display purposes only. */
    @JsonProperty("unit")
    val unit: String? = null
)

data class Metrics (
    /* The index of the application instance the custom metrics are submitted for. Can be found in the `CF_INSTANCE_INDEX` environment variable. */
    @JsonProperty("instance_index")
    val instanceIndex: Int,

    /* The custom metrics to be submitted. */
    @JsonProperty("metrics")
    val metrics: List<Metric>
)
