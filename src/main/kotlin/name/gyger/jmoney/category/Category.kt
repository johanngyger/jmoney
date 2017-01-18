package name.gyger.jmoney.category

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
open class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    var id: Long = 0

    @ManyToOne
    @JsonIgnore
    var parent: Category? = null

    @OneToMany(mappedBy = "parent", cascade = arrayOf(CascadeType.REMOVE, CascadeType.MERGE))
    @OrderBy("name")
    val children: MutableList<Category> = mutableListOf()

    var name: String = ""

    enum class Type {
        NORMAL, SPLIT, TRANSFER, ACCOUNT, ROOT
    }

    @Enumerated
    var type: Category.Type = Category.Type.NORMAL

    @Transient
    var parentId: Long = 0

    @Transient
    @JsonIgnore
    var level: Int = 0

    fun getNameIndented(): String {
        // TODO correct indentation
        return "L$level: $name"
    }
}