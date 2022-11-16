package com.shopme.admin.user.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.product.ProductSaveHelper;
import com.shopme.admin.product.ProductService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.common.exception.UserNotFoundException;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired 
	private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null, 0);
		//List<Product> listAllProducts = service.listAll();
		//model.addAttribute("listAllProducts", listAllProducts);
		//return "products/products";
		
		
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, 
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword, 
			@Param("categoryId") Integer categoryId
			) {
		System.out.println("Selected Category ID: " + categoryId);
		System.out.println("Sort Field: " + sortField);
		System.out.println("Sort Order: " + sortDir);
		Page<Product> productPage = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = productPage.getContent();
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		long startCount = (pageNum - 1) * CategoryService.ITEMS_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ITEMS_PER_PAGE - 1;
		if (endCount > productPage.getTotalElements()) {
			endCount = productPage.getTotalElements();
		}
		
		String reverseSort = sortDir.equals("asc") ? "desc" : "asc";
		
		if(categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", productPage.getTotalElements());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort", reverseSort);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listCategories", listCategories);
		
		return "products/products";
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateUnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean enabled, Model model, RedirectAttributes redirectAttributes) {
		productService.updateUserEnabledStatus(id, enabled);
		String status = enabled? "ENABLED" : "DISABLED";
		redirectAttributes.addFlashAttribute("message", "Product ID: " + id + " is successfully " + status + ".");
		return "redirect:/products";
	}
	
	@GetMapping("/products/new")
	public String newUser(Model model) {
		List<Category> listCategory = categoryService.listCategoriesUsedInForm();
		List<Brand> listBrand = productService.listBrand();
		
		Product product = new Product();
		product.setEnabled(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listCategory", listCategory);
		model.addAttribute("listBrand", listBrand);
		model.addAttribute("pageTitle", "Create new product");
		model.addAttribute("numberOfExistingExtraImages", 0);
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product products, RedirectAttributes redirectAttributes, 
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipartFiles, 
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs, 
			@RequestParam(name = "detailNames", required = false) String[] detailNames, 
			@RequestParam(name = "detailValues", required = false) String[] detailValues, 
			@RequestParam(name = "imageIds", required = false) String[] imageIds, 
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException, ProductNotFoundException {
		
		if (loggedUser.hasRole("Salesperson")) {
			Product product = productService.getProductDetails(products.getId());
			productService.saveProductPrice(products);
			redirectAttributes.addFlashAttribute("message", "The product price has been saved sucessfully.");
			return ProductSaveHelper.getRedirectURLtoAffectedUser(product);
		}
		
		ProductSaveHelper.setMainImageName(products, mainImageMultipartFile);
		ProductSaveHelper.setExistingExtraImagesName(products, imageIds, imageNames);
		ProductSaveHelper.setNewExtraImageName(products, extraImageMultipartFiles);
		ProductSaveHelper.setProductDetails(products, detailIDs, detailNames, detailValues);
		
		Product savedProduct = productService.saveProduct(products);
		
		ProductSaveHelper.saveUploadedImages(mainImageMultipartFile, extraImageMultipartFiles, savedProduct);
		
		ProductSaveHelper.DeleteExtraImagesFromDirectory(products);
		
		redirectAttributes.addFlashAttribute("message", "The product has been saved sucessfully.");
		
		return ProductSaveHelper.getRedirectURLtoAffectedUser(products);
	}

	
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws ProductNotFoundException {
		try {
			Product product = productService.getProductDetails(id);
			List<Category> listCategory = categoryService.listCategoriesUsedInForm();
			List<Brand> listBrand = productService.listBrand();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "Edit Product: ID - " + id);
			model.addAttribute("listCategory", listCategory);
			model.addAttribute("listBrand", listBrand);
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			
			
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("alert", e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws ProductNotFoundException {
		try {
			Product product = productService.getProductDetails(id);
			model.addAttribute("product", product);
			
			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("alert", e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			productService.deleteProduct(id);
			String deletemExtraUploadDir = "../product-photos/" + id + "/extras/";
			String deleteMainUploadDir = "../product-photos/" + id ;
			FileUploadUtil.cleanDir(deletemExtraUploadDir);
			FileUploadUtil.cleanDir(deleteMainUploadDir);
			redirectAttributes.addFlashAttribute("warning", "User ID: " + id + " is deleted successfully.");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("warning", e.getMessage());
		}
		return "redirect:/products";
	}
	
	
}
