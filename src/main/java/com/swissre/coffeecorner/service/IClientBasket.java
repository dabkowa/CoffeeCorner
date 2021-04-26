package com.swissre.coffeecorner.service;

import java.util.List;

import com.swissre.coffeecorner.entity.Product;

/**
 * Interface specifying business operations for the Charlene's Coffee Corner
 * 
 * @author Andrzej Dabkowski
 *
 */
public interface IClientBasket {
	
	/**
	 * Used to add product(s) formatted as a string 
	 * 
	 * @param productLine
	 * @return a list of added products
	 */
	public List<Product> addProduct(String productLine);
	
	/**
	 * Applies a bonus program (a set of rules specifying discounts for clients)
	 * 
	 * @return a list of products that are free of charge for the client as a result of the bonus program
	 */
	public List<Product> applyBonusProgram();
	
	/**
	 * Generates a receipt taking into account bonus program products
	 * 
	 * @return a generated receipt
	 */
	public String printReceipt();
}
