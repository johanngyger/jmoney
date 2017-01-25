package name.gyger.jmoney.category

import org.springframework.stereotype.Component

@Component
class CategoryFactory(private val categoryRepository: CategoryRepository) {

    fun createNormalCategories(root: Category) {
        createNormalCategory("Taxes", root)
        createNormalCategory("Memberships", root)
        createNormalCategory("Donations", root)
        createNormalCategory("Fees", root)
        createNormalCategory("Gifts", root)

        val income = createNormalCategory("Income", root)
        createNormalCategory("Wages", income)
        createNormalCategory("Sidelines", income)

        val children = createNormalCategory("Children", root)
        createNormalCategory("Doctor", children)
        createNormalCategory("Clothing", children)
        createNormalCategory("Child care", children)
        createNormalCategory("Toys", children)

        val housing = createNormalCategory("Living", root)
        createNormalCategory("Additional costs", housing)
        createNormalCategory("Rent/Mortgage", housing)
        createNormalCategory("TV", housing)

        val communication = createNormalCategory("Communication", root)
        createNormalCategory("Phone", communication)
        createNormalCategory("Mobile", communication)
        createNormalCategory("Internet", communication)

        val insurance = createNormalCategory("Insurance", root)
        createNormalCategory("Health insurance", insurance)
        createNormalCategory("Household insurance", insurance)

        val household = createNormalCategory("Household", root)
        createNormalCategory("Food", household)
        createNormalCategory("Restaurant", household)
        createNormalCategory("Clothing", household)

        val transport = createNormalCategory("Traffic", root)
        createNormalCategory("Car", transport)
        createNormalCategory("Public transport", transport)

        val entertainment = createNormalCategory("Entertainment", root)
        createNormalCategory("Books", entertainment)
        createNormalCategory("Newspapers", entertainment)
        createNormalCategory("Magazines", entertainment)
        createNormalCategory("Music", entertainment)
        createNormalCategory("Movies", entertainment)
        createNormalCategory("Games", entertainment)

        val leisure = createNormalCategory("Leisure", root)
        createNormalCategory("Outgoing", leisure)
        createNormalCategory("Cinema", leisure)
        createNormalCategory("Sports", leisure)
        createNormalCategory("Concerts", leisure)
        createNormalCategory("Trips", leisure)
        createNormalCategory("Vacation", leisure)

        val healthCare = createNormalCategory("Health", root)
        createNormalCategory("Doctor", healthCare)
        createNormalCategory("Drugstore", healthCare)
        createNormalCategory("Dentist", healthCare)
        createNormalCategory("Hygiene", healthCare)
    }

    fun createRootCategory(): Category {
        return createCategory(Category.Type.ROOT, "[ROOT]", null)
    }

    fun createTransferCategory(root: Category): Category {
        return createCategory(Category.Type.TRANSFER, "[TRANSFER]", root)
    }

    fun createSplitCategory(root: Category): Category {
        return createCategory(Category.Type.SPLIT, "[SPLIT]", root)
    }

    private fun createNormalCategory(name: String, parent: Category): Category {
        return createCategory(Category.Type.NORMAL, name, parent)
    }

    private fun createCategory(type: Category.Type, name: String, parent: Category?): Category {
        val c = Category()
        c.type = type
        c.name = name
        c.parent = parent
        categoryRepository.save(c)
        return c
    }

}
