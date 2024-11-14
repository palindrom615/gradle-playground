import brave.sampler.Sampler
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.brave.BraveService
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
                    .spanReporter { span -> println(span) }
                    .samplerFactory(SamplerFactory { serviceName -> Sampler.ALWAYS_SAMPLE })
            val tracing =
                builder.build().get("talk-place-search-server", RequestContextCurrentTraceContext.ofDefault())

            val server = Server.builder()
                .http(8081)
                .decorator(BraveService.newDecorator(tracing))
                .service("/healthcheck") { ctx, req ->
                    HttpResponse.of(200)
                }
                .build()
            val future = server.start()
            future.join()
        }

    }
}