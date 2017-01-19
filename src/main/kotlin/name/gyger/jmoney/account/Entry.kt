package name.gyger.jmoney.account

import com.fasterxml.jackson.annotation.JsonIgnore
import name.gyger.jmoney.category.Category
import java.util.*
import javax.persistence.*

@Entity
class Entry {
    var creation = Calendar.getInstance().time.time
    var date: Date? = null
    var valuta: Date? = null
    var description: String = ""
    var memo: String = ""
    var amount: Long = 0

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    var id: Long = 0

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    var account: Account? = null

    @JsonIgnore
    @ManyToOne
    var category: Category? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    var splitEntry: Entry? = null

    @OneToMany(mappedBy = "splitEntry", fetch = FetchType.EAGER, cascade = arrayOf(CascadeType.REMOVE))
    var subEntries: MutableList<Entry> = mutableListOf()

    /**
     * Double entry booking
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.REMOVE))
    var other: Entry? = null

    enum class Status {
        RECONCILING, CLEARED
    }

    @Enumerated
    var status: Status? = null

    @Transient
    var balance: Long = 0

    @Transient
    var accountId: Long = 0

    @Transient
    var categoryId: Long = 0

    fun getCategoryName(): String {
        return category?.name ?: ""
    }

    operator fun contains(filter: String?): Boolean {
        val myFilter = filter ?: ""
        return myFilter in description
                || myFilter in getCategoryName()
                || myFilter in memo
    }
}