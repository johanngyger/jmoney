package name.gyger.jmoney.session

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SessionRepository : CrudRepository<Session, Long> {

    @Query("SELECT s FROM Session s LEFT JOIN FETCH s.rootCategory " +
            "LEFT JOIN FETCH s.splitCategory LEFT JOIN FETCH s.transferCategory")
    fun getSession(): Session

    @Query("SELECT count(s) = 1 FROM Session s")
    fun isSessionAvailable(): Boolean

}