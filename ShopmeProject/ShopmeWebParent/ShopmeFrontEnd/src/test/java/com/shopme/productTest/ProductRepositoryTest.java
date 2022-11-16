package com.shopme.productTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import com.shopme.ShopmeFrontEndApplication;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@ContextConfiguration(classes = ShopmeFrontEndApplication.class)
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Test
	public void findByAlias() {
		String alias = "canon-eos-m50";
		Product product = productRepo.findByAliasEnabled(alias);
		
		assertThat(product).isNotNull();
	}
}
