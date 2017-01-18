package name.gyger.jmoney.account

import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.Category.Type
import name.gyger.jmoney.session.Session

import javax.persistence.*

@Entity
class Account : Category() {
    var bank: String? = null
    var accountNumber: String? = null
    var currencyCode: String? = null
    var startBalance: Long = 0
    var minBalance: Long? = null
    var abbreviation: String? = null
    @Column(length = 1000) var comment: String? = null

    @OneToMany(mappedBy = "account", cascade = arrayOf(CascadeType.REMOVE))
    var entries: List<Entry> = mutableListOf()

    @ManyToOne
    var session: Session? = null

    init {
        type = Category.Type.ACCOUNT
    }

}