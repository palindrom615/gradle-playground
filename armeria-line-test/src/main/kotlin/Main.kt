import brave.sampler.Sampler
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.server.Server
import com.linecorp.imon.core.Convention
import com.linecorp.imon.core.Convention.Phase
import com.linecorp.imon.tracing.SamplerFactory
import com.linecorp.imon.tracing.http.TracingHttpFactory

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val builder =
                TracingHttpFactory.builder()
                    .instanceId(Convention.InstanceId("talk-place-search-server"))
                    .instancePhase(Phase.NONE)
                    .siteId(Convention.SiteId("talk"))
            builder.spanReporter { span -> println(span) }
            builder.samplerFactory(SamplerFactory { serviceName -> Sampler.ALWAYS_SAMPLE })

            val server = Server.builder()
                .http(8080)
                .service("/healthcheck") { ctx, req ->
                    HttpResponse.of(200)
                }
                .decorator(BraveService.newDecorator(builder.build()))
                .build()
            server.start()
        }

    }
}