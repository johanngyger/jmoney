package name.gyger.jmoney.session

import name.gyger.jmoney.account.Account
import name.gyger.jmoney.category.Category

import javax.persistence.*

@Entity
class Session(
        @OneToOne(cascade = arrayOf(CascadeType.REMOVE))
        var rootCategory: Category,

        @OneToOne
        var transferCategory: Category,

        @OneToOne
        var splitCategory: Category,

        @Id
        @GeneratedValue
        var id: Long = 0,

        @OneToMany(mappedBy = "session", cascade = arrayOf(CascadeType.REMOVE))
        val accounts: List<Account> = mutableListOf()
)
