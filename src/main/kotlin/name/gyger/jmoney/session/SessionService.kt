package name.gyger.jmoney.session

import name.gyger.jmoney.category.CategoryInitializer
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query

@Service
@Transactional
class SessionService(private val categoryInitializer: CategoryInitializer) {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getSession(): Session {
        val query = "SELECT s FROM Session s LEFT JOIN FETCH s.rootCategory " +
                "LEFT JOIN FETCH s.splitCategory LEFT JOIN FETCH s.transferCategory"
        return em.createQuery(query, Session::class.java).singleResult
    }

    fun initSession() {
        removeOldSession()
        val session = Session()
        categoryInitializer.initCategories(session)
        em.persist(session)
    }

    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent?) {
        if (!isSessionAvailable()) {
            initSession()
        }
    }

    fun removeOldSession() {
        if (isSessionAvailable()) {
            val oldSession = getSession()
            em.remove(oldSession)
        }
    }

    fun isSessionAvailable(): Boolean {
        val count = em.createQuery("SELECT COUNT(s) FROM Session s").singleResult as Long
        return count == 1L
    }

}
