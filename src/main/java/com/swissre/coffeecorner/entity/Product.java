package com.swissre.coffeecorner.entity;

import java.math.BigDecimal;

public class Product {
	
	public enum ProductType {
		BEVERAGE, EXTRAS, SNACK
	}
	
	String name;
	BigDecimal price;
	ProductType productType;
	
	public Product() {}
	
	public Product(String name, ProductType productType, BigDecimal price)  {
		this.name = name;
		this.productType= productType; 
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", productType=" + productType + ", price=" + price + "]";
	}
	
	

}
