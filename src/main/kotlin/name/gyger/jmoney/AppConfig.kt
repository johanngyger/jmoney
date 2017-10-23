package name.gyger.jmoney

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun datatypeHibernateModule(): Module {
        val hibernate5Module = Hibernate5Module()
        hibernate5Module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION)
        return hibernate5Module
    }

}