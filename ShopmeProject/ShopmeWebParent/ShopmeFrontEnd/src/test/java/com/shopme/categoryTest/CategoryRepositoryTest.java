package com.shopme.categoryTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import com.shopme.ShopmeFrontEndApplication;
import com.shopme.category.CategoryRepository;
import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@ContextConfiguration(classes = ShopmeFrontEndApplication.class)
public class CategoryRepositoryTest {
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Test
	public void testListEnabledCategories() {
		List<Category> categories = categoryRepo.findAllCategories();
		categories.forEach(category -> {
			System.out.println(category.getName() + " (" + category.isEnabled() + ")");
		});
	}
	
	@Test
	public void testFindCategoryByAlias() {
		String alias = "computer_components";
		Category category = categoryRepo.findByAliasEnabled(alias);
		
		assertThat(category).isNotNull();
	}
}
