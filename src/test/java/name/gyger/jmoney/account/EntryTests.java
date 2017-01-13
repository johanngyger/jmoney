package name.gyger.jmoney.account;

import name.gyger.jmoney.category.Category;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EntryTests {

    @Test
    public void testContainsEmpty() {
        Entry e = new Entry();
        assertThat(e.contains(null)).isTrue();
        assertThat(e.contains("")).isTrue();
        assertThat(e.contains("Some text")).isFalse();
    }

    @Test
    public void testContainsDescription() {
        Entry e = new Entry();
        e.setDescription("Description");
        assertThat(e.contains("Desc")).isTrue();
    }

    @Test
    public void testContainsCategoryName() {
        Entry e = new Entry();
        Category c = new Category();
        c.setName("Category");
        e.setCategory(c);
        assertThat(e.contains("Cat")).isTrue();
    }

    @Test
    public void testContainsMemo() {
        Entry e = new Entry();
        e.setMemo("Memo");
        assertThat(e.contains("Memo")).isTrue();
    }

}
