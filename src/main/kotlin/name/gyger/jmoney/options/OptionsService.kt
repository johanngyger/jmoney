package name.gyger.jmoney.options

import name.gyger.jmoney.session.SessionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream

@Service
@Transactional
class OptionsService(private val sessionService: SessionService,
                     private val legacySessionMigrator: LegacySessionMigrator) {

    fun importFile(inputStream: InputStream) {
        sessionService.removeOldSession()
        legacySessionMigrator.importSession(inputStream)
    }

}
