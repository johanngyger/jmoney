package name.gyger.jmoney.session

import name.gyger.jmoney.category.CategoryFactory
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SessionService(private val sessionRepository: SessionRepository,
                     private val categoryFactory: CategoryFactory) {

    fun initSession() {
        removeOldSession()
        val rootCategory = categoryFactory.createRootCategory()
        val transferCategory = categoryFactory.createTransferCategory(rootCategory)
        val splitCategory = categoryFactory.createSplitCategory(rootCategory)
        val session = Session(rootCategory, transferCategory, splitCategory)
        categoryFactory.createNormalCategories(rootCategory)
        sessionRepository.save(session)
    }

    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent?) {
        if (!isSessionAvailable()) {
            initSession()
        }
    }

    fun removeOldSession() {
        if (isSessionAvailable()) {
            sessionRepository.delete(sessionRepository.getSession())
        }
    }

    fun isSessionAvailable(): Boolean {
        return sessionRepository.isSessionAvailable()
    }

}
