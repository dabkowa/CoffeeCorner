package com.swissre.coffeecorner;

import com.swissre.coffeecorner.service.ClientBasketService;
import com.swissre.coffeecorner.service.IClientBasket;

/**
 * 
 * This is a command line based entry for the Charlene's Coffee Corner Work Assignment at Swiss Re.
 * 
 * @author Andrzej Dabkowski
 *
 */
public class Application {
	
	public static void main(String[] args) {
		String [] inputProductList = {
				"large coffee with extra milk",
				"small coffee with special roast",
				"bacon roll",
				"orange juice"
		};
		
		IClientBasket clientBasketService = new ClientBasketService();
		
		for(String singleProductLine : inputProductList)
			clientBasketService.addProduct(singleProductLine);
		
		clientBasketService.applyBonusProgram();
		String printedReceipt = clientBasketService.printReceipt();

        System.out.println(printedReceipt);
	}
}
