package name.gyger.jmoney.account

import name.gyger.jmoney.category.Category
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EntryTests {

    @Test
    fun testContainsEmpty() {
        val e = Entry()
        assertThat(e.contains(null)).isTrue()
        assertThat(e.contains("")).isTrue()
        assertThat(e.contains("Some text")).isFalse()
    }

    @Test
    fun testContainsDescription() {
        val e = Entry()
        e.description = "Description"
        assertThat(e.contains("Desc")).isTrue()
    }

    @Test
    fun testContainsCategoryName() {
        val e = Entry()
        val c = Category()
        c.name = "Category"
        e.category = c
        assertThat(e.contains("Cat")).isTrue()
    }

    @Test
    fun testContainsMemo() {
        val e = Entry()
        e.memo = "Memo"
        assertThat(e.contains("Memo")).isTrue()
    }

}
