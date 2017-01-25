package name.gyger.jmoney.category

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CategoryRepository : CrudRepository<Category, Long> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children")
    override fun findAll(): List<Category>

}