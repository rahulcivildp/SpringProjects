package com.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
		return viewCategoryByPage(alias, 1, model);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,@PathVariable("pageNum") int pageNum , Model model) {
		Category category = categoryService.getCategory(alias);
		if(category == null) {
			return "error/404";
		}
		
		List<Category> categoryParentsList = categoryService.getCategoryParents(category);
		
		Page<Product> pageProduct = productService.listByCategory(pageNum, category.getId());
		List<Product> listProducts = pageProduct.getContent();
		
		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProduct.getTotalElements());
		model.addAttribute("page_title", category.getName());
		model.addAttribute("categoryParentsList", categoryParentsList);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("category", category);
		
		return "products/products_by_category";
	}
}
