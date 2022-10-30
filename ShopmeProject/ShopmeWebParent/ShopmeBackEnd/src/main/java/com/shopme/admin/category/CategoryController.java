package com.shopme.admin.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Category;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null);
		
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, @Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
		System.out.println("Sort Field: " + sortField);
		System.out.println("Sort Order: " + sortDir);
		Page<Category> pageCategory = service.listByPage(pageNum, sortField, sortDir, keyword);
		
		List<Category> listCategories = pageCategory.getContent();
		
		long startCount = (pageNum - 1) * CategoryService.ITEMS_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ITEMS_PER_PAGE - 1;
		if (endCount > pageCategory.getTotalElements()) {
			endCount = pageCategory.getTotalElements();
		}
		
		String reverseSort = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageCategory.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageCategory.getTotalElements());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort", reverseSort);
		model.addAttribute("keyword", keyword);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateUnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean enabled, Model model, RedirectAttributes redirectAttributes) {
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled? "ENABLED" : "DISABLED";
		redirectAttributes.addFlashAttribute("message", "Category: " + id + " is successfully " + status + ".");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/new")
	public String newUser(Model model) {
		
		Category category = new Category();
		category.setEnabled(true);
		
		model.addAttribute("category", category);
		model.addAttribute("pageTitle", "Create new category");
		
		return "categories/categories_form";
	}
}