package com.shopme.admin.product;

@SuppressWarnings("serial")
public class ProductNotFoundException extends Exception {

	public ProductNotFoundException (String mesage) {
		super(mesage);
	}
}
