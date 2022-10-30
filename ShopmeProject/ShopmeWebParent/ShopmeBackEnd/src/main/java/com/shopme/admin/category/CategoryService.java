package com.shopme.admin.category;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	
	public static final int ITEMS_PER_PAGE = 12;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	public List<Category> listAll() {
		return (List<Category>) categoryRepo.findAll(Sort.by("name").ascending());
	}
	
	public Page<Category> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ITEMS_PER_PAGE, sort);
		
		if(keyword != null) {
			return categoryRepo.findCategory(keyword, pageable);
		}
		return categoryRepo.findAll(pageable);
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		categoryRepo.updateEnableStatus(id, enabled);
	}
}
