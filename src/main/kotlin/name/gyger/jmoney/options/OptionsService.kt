package name.gyger.jmoney.options

import name.gyger.jmoney.session.SessionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
@Transactional
open class OptionsService(private val sessionService: SessionService) {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun importFile(inputStream: InputStream) {
        sessionService.removeOldSession()
        LegacySessionMigrator(inputStream, em).importSession()
    }

}
