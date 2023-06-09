import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.spring.ArmeriaAutoConfiguration
import com.linecorp.armeria.spring.ArmeriaServerConfigurator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(ArmeriaAutoConfiguration::class)
open class ServerConfig {
    @Bean
    open fun server(): ArmeriaServerConfigurator {
        return ArmeriaServerConfigurator {sb -> sb
            .service("/healthcheck") { ctx, req ->
                HttpResponse.of(200)
            }
        }
    }
}