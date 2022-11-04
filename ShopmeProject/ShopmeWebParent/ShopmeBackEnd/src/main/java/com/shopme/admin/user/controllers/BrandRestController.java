package com.shopme.admin.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.brand.BrandService;

@RestController
public class BrandRestController {

	@Autowired
	private BrandService service;
	
	@PostMapping("/brands/check_name")
	public String chechDuplicateName(@Param("id") Integer id, @Param("name") String name) {
		return service.isNameUnique(id, name) ? "OK" : "Duplicate Name";
	}
}
