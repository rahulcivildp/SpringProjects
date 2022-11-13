package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import com.shopme.admin.ShopmeBackEndApplication;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@ContextConfiguration(classes = ShopmeBackEndApplication.class)
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewProduct() {
		Brand brand = entityManager.find(Brand.class, 9);
		Category category = entityManager.find(Category.class, 4);
		
		Product product = new Product();
		product.setName("Ryzen 6 5600G");
		product.setAlias("ryzen_6_5600g");
		product.setShortDescription("Lenovo IdeaCentre Gaming 5 Desktop (AMD Ryzen 5 5600G/16GB/512GB SSD/Windows 11/NVIDIA GeForce RTX 3060 12GB GDDR6/WiFi 5/Bluetooth 5.0/Raven Black), 90RW00GCIN");
		product.setFullDescription("Processor: AMD Ryzen 5 5600G | Speed: 3.9 GHz (Base) - 4.4 GHz (Max) | 6 Cores | 12 Threads | 3MB L2/ 16MB L3 Cache\r\n"
				+ "OS: Pre-Loaded Windows 11 Home with Lifetime Validity\r\n"
				+ "Memory and Storage: 16GB DDR4-3200 RAM, expandable up to 32GB | Storage: 512GB SSD\r\n"
				+ "Graphics: NVIDIA GeForce RTX 3060 12GB GDDR6 Graphics\r\n"
				+ "Cooling : 380W Power Supply | Rear Fan + Additional front fan | Optimize gameplay with 3 performance modes (Balance, Quiet , Performance)\r\n"
				+ "Connectivity: WiFi 5 (11ac, 2x2) | Bluetooth 5.0\r\n"
				+ "Ports: 1x USB-A 3.2 Gen 1 | 1x USB-A 3.2 Gen 2 | 1x USB-C 3.2 Gen 1 (Data Transfer and 5V@3A charging) | 4x USB-A 2.0 | 1x 3.5mm Jack | 1x Ethernet (RJ-45) | 1x HDMI 1.4 | 1x VGA | 1x audio-out (3.5mm) | Without CD Drive");
		product.setBrands(brand);
		product.setCategories(category);
		product.setPrice(97990);
		product.setCost(75000);
		product.setEnabled(true);
		product.setInStock(true);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		product.setMainImage("ssss");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllProducts() {
		Iterable<Product> listProducts = repo.findAll();
		
		listProducts.forEach(System.out::println);
	}
	
	@Test
	public void testGetProducts() {
		Integer id = 3;
		Product product = repo.findById(id).get();
		System.out.println(product);
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 3;
		Product product = repo.findById(id).get();
		product.setPrice(77000);
		
		repo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		
		assertThat(updatedProduct.getPrice()).isNotNull();
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		repo.deleteById(id);
		
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main maii image.jpg");
		product.addExtraImages("main i image.jpg");
		product.addExtraImages("main ise.jpg");
		product.addExtraImages("msi image.jpg");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveProductWithDetails() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.addDetail("main i image.jpg", "128 GB");
		product.addDetail("main i image.jpg", "128 GB");
		product.addDetail("main i image.jpg", "128 GB");
		product.addDetail("main i image.jpg", "128 GB");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(4);
	}
}
