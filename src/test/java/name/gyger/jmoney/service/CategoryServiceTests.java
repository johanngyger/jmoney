package name.gyger.jmoney.service;

import name.gyger.jmoney.dto.AccountDetailsDto;
import name.gyger.jmoney.dto.AccountDto;
import name.gyger.jmoney.dto.CategoryDto;
import name.gyger.jmoney.model.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CategoryServiceTests {

    @Autowired
    SessionService sessionService;

    @Autowired
    CategoryService categoryService;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        sessionService.initSession();
        em.refresh(sessionService.getSession());
    }

    @Test
    public void testCategoryServices() {
/*
        em.refresh(categoryService.prefetchCategories());
        List<CategoryDto> categories = categoryService.getCategories();
        assertThat(categories).isNotEmpty();
*/
    }

}
