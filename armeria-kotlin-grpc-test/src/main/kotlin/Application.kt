import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.grpc.AsyncServerInterceptor
import com.linecorp.armeria.server.grpc.GrpcService
import example.armeria.grpc.kotlin.Hello
import example.armeria.grpc.kotlin.HelloServiceGrpcKt
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.protobuf.services.ProtoReflectionService
import io.netty.util.AttributeKey
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

fun main() {
    val interceptor = ServiceRequestContextInterceptor()
    val service = HelloServiceHandler()
    val server = Server.builder()
        .http(8081)
        .service(
            GrpcService.builder()
                .addServices(service)
                .addService(ProtoReflectionService.newInstance())
                .intercept(interceptor)
                .build()
        )
        .build()
    server.start().join()
}

val attributeKey = AttributeKey.valueOf<String>("key")

class HelloServiceHandler : HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
    private val logger = LoggerFactory.getLogger(javaClass)
    override suspend fun hello(request: Hello.HelloRequest): Hello.HelloReply {
        logger.info("request: $request")
        val context = try {
            ServiceRequestContext.current()
        } catch (e: Exception) {
            logger.error("error: $e")
            throw e
        }
        val ctxVal = context.attr(attributeKey)
        logger.info("ctxVal: $ctxVal")
        logger.info("context: $context")

        return Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.name}")
            .build()
    }
}

val delayed = CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS)

class AsyncServiceRequestContextInterceptor : AsyncServerInterceptor {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun <I : Any?, O : Any?> asyncInterceptCall(
        call: ServerCall<I, O>,
        headers: Metadata,
        next: ServerCallHandler<I, O>
    ): CompletableFuture<ServerCall.Listener<I>> {
        val context = ServiceRequestContext.current()
        context.setAttr(attributeKey, "value")
        logger.info("context: $context")
        return CompletableFuture.supplyAsync({
            next.startCall(call, headers)
        }, delayed)
    }
}


class ServiceRequestContextInterceptor : ServerInterceptor {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun <I : Any?, O : Any?> interceptCall(
        call: ServerCall<I, O>,
        headers: Metadata,
        next: ServerCallHandler<I, O>
    ): ServerCall.Listener<I> {
        val context = ServiceRequestContext.current()
        context.setAttr(attributeKey, "value")
        logger.info("context: $context")
        TimeUnit.SECONDS.sleep(3)
        return next.startCall(call, headers)
    }
}
