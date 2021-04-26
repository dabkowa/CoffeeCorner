package com.swissre.coffeecorner.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.swissre.coffeecorner.entity.Product;
import com.swissre.coffeecorner.exception.UnknownProductException;

public class ClientBasketServiceTest {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	
	private IClientBasket clientBasketService;
	
	@Before
	public void before() {
		clientBasketService = new ClientBasketService();
	}
	
	@Test
	public void addProduct_notInCatalog_failure() {
		
		exceptionRule.expect(UnknownProductException.class);
	    exceptionRule.expectMessage(
	    	String.format("Product catalog does not contain product [Hiperlarge coffee]."));

	    clientBasketService.addProduct("Hiperlarge coffee");
	}
	
	@Test
	public void addProduct_parsing_failure() {
		
		exceptionRule.expect(UnknownProductException.class);
	    exceptionRule.expectMessage(
	    		String.format("Some input parsing issue. It was supposed to containg beverage and extras [large coffee with ]."));

	    clientBasketService.addProduct("large coffee with ");
	}
	
	@Test
	public void addProduct_parsing_failure2() {
		
		exceptionRule.expect(UnknownProductException.class);
	    exceptionRule.expectMessage(
	    		String.format("Some input parsing issue. It was supposed to containg beverage and extras [ with extra milk]."));

	    clientBasketService.addProduct(" with extra milk");
	}
	
	@Test
	public void addProduct_beverage_and_extras_success() {
		List<Product> products = clientBasketService.addProduct("large coffee with extra milk");
		
		assertNotNull(products);
		assertTrue(products.size() == 2);
		
	}
	
	@Test
	public void addProduct_beverage_success() {
		List<Product> products = clientBasketService.addProduct("large coffee");
		
		assertNotNull(products);
		assertTrue(products.size() == 1);
	}
	
	@Test
	public void addProduct_snack_success() {
		List<Product> products = clientBasketService.addProduct("bacon roll");
		
		assertNotNull(products);
		assertTrue(products.size() == 1);
	}
	
	@Test
	public void addProduct_extras_success() {
		List<Product> products = clientBasketService.addProduct("extra milk");
		
		assertNotNull(products);
		assertTrue(products.size() == 1);
	}
	
	@Test
	public void printReceipt_beverage_extras_gratis() {
		String [] inputProductList = {
				"large coffee with extra milk",
				"small coffee with special roast",
				"bacon roll",
				"orange juice",
				"medium coffee",
				"orange juice"
		};  // 5x beverage, 2x extras, 1x snack  --- gratis are: large coffee , extra milk
		
		
		for(String singleProductLine : inputProductList)
			clientBasketService.addProduct(singleProductLine);
		
		List<Product> gratisProducts = clientBasketService.applyBonusProgram();
		
		assertNotNull(gratisProducts);
		assertTrue(gratisProducts.size() == 2);
		
		Product gratisProduct = gratisProducts.get(0);
		assertEquals("extra milk", gratisProduct.getName());
		assertEquals(Product.ProductType.EXTRAS, gratisProduct.getProductType());
		
		gratisProduct = gratisProducts.get(1);
		assertEquals("large coffee", gratisProduct.getName());
		assertEquals(Product.ProductType.BEVERAGE, gratisProduct.getProductType());
		
		String printedReceipt = clientBasketService.printReceipt();
		String expectedReceipt =  
		
		"Your receipt :" + LINE_SEPARATOR + 
		"Product description   Quantity   Price    Total" + LINE_SEPARATOR +
		"Gratis products:" + LINE_SEPARATOR +
		" extra milk               1      0.00     0.00" + LINE_SEPARATOR +
		" large coffee             1      0.00     0.00" + LINE_SEPARATOR +
		"Beverages:" + LINE_SEPARATOR +
		" orange juice             2      3.95     7.90" + LINE_SEPARATOR +
		" small coffee             1      2.50     2.50" + LINE_SEPARATOR +
		" medium coffee            1      3.00     3.00" + LINE_SEPARATOR +
		"Extras:" + LINE_SEPARATOR +
		" special roast            1      0.90     0.90" + LINE_SEPARATOR +
		"Snacks:" + LINE_SEPARATOR +
		" bacon roll               1      4.50     4.50" + LINE_SEPARATOR +
		"                                Total:   18.80" + LINE_SEPARATOR;
		assertEquals(expectedReceipt, printedReceipt);
	}
	
	@Test
	public void printReceipt_beverage_gratis() {
		String [] inputProductList = {
				"large coffee with extra milk",
				"small coffee with special roast",
				"orange juice",
				"medium coffee",
				"orange juice"
		};  // 5x beverage, 2x extras  --- gratis is: large coffee
		
		
		for(String singleProductLine : inputProductList)
			clientBasketService.addProduct(singleProductLine);
		
		List<Product> gratisProducts = clientBasketService.applyBonusProgram();
		
		assertNotNull(gratisProducts);
		assertTrue(gratisProducts.size() == 1);
		
		Product gratisProduct = gratisProducts.get(0);
		assertEquals("large coffee", gratisProduct.getName());
		assertEquals(Product.ProductType.BEVERAGE, gratisProduct.getProductType());
		
		String printedReceipt = clientBasketService.printReceipt();
		String expectedReceipt =  
		
		"Your receipt :" + LINE_SEPARATOR + 
		"Product description   Quantity   Price    Total" + LINE_SEPARATOR +
		"Gratis products:" + LINE_SEPARATOR +
		" large coffee             1      0.00     0.00" + LINE_SEPARATOR +
		"Beverages:" + LINE_SEPARATOR +
		" orange juice             2      3.95     7.90" + LINE_SEPARATOR +
		" small coffee             1      2.50     2.50" + LINE_SEPARATOR +
		" medium coffee            1      3.00     3.00" + LINE_SEPARATOR +
		"Extras:" + LINE_SEPARATOR +
		" special roast            1      0.90     0.90" + LINE_SEPARATOR +
		" extra milk               1      0.30     0.30" + LINE_SEPARATOR +
		"                                Total:   14.60" + LINE_SEPARATOR;
		assertEquals(expectedReceipt, printedReceipt);
	}
	
	@Test
	public void printReceipt_extras_gratis() {
		String [] inputProductList = {
				"large coffee with extra milk",
				"small coffee with special roast",
				"bacon roll",
				"orange juice",
				"medium coffee"
		};  // 4x beverage, 2x extras, 1x snack  --- gratis is: extra milk
		
		
		for(String singleProductLine : inputProductList)
			clientBasketService.addProduct(singleProductLine);
		
		List<Product> gratisProducts = clientBasketService.applyBonusProgram();
		
		assertNotNull(gratisProducts);
		assertTrue(gratisProducts.size() == 1);
		
		Product gratisProduct = gratisProducts.get(0);
		assertEquals("extra milk", gratisProduct.getName());
		assertEquals(Product.ProductType.EXTRAS, gratisProduct.getProductType());
		
		String printedReceipt = clientBasketService.printReceipt();
		String expectedReceipt =  
		
		"Your receipt :" + LINE_SEPARATOR + 
		"Product description   Quantity   Price    Total" + LINE_SEPARATOR +
		"Gratis products:" + LINE_SEPARATOR +
		" extra milk               1      0.00     0.00" + LINE_SEPARATOR +
		"Beverages:" + LINE_SEPARATOR +
		" orange juice             1      3.95     3.95" + LINE_SEPARATOR +
		" large coffee             1      3.50     3.50" + LINE_SEPARATOR +
		" small coffee             1      2.50     2.50" + LINE_SEPARATOR +
		" medium coffee            1      3.00     3.00" + LINE_SEPARATOR +
		"Extras:" + LINE_SEPARATOR +
		" special roast            1      0.90     0.90" + LINE_SEPARATOR +
		"Snacks:" + LINE_SEPARATOR +
		" bacon roll               1      4.50     4.50" + LINE_SEPARATOR +
		"                                Total:   18.35" + LINE_SEPARATOR;
		assertEquals(expectedReceipt, printedReceipt);
	}
	
	@Test
	public void printReceipt_zero_gratis() {
		String [] inputProductList = {
				"large coffee with extra milk",
				"small coffee with special roast",
				"orange juice",
				"medium coffee"
		};  // 4x beverage, 2x extras  --- gratis is: nothing
		
		
		for(String singleProductLine : inputProductList)
			clientBasketService.addProduct(singleProductLine);
		
		List<Product> gratisProducts = clientBasketService.applyBonusProgram();
		
		assertNotNull(gratisProducts);
		assertTrue(gratisProducts.size() == 0);
		
		String printedReceipt = clientBasketService.printReceipt();
		
		String expectedReceipt =  
		"Your receipt :" + LINE_SEPARATOR + 
		"Product description   Quantity   Price    Total" + LINE_SEPARATOR +
		"Beverages:" + LINE_SEPARATOR +
		" orange juice             1      3.95     3.95" + LINE_SEPARATOR +
		" large coffee             1      3.50     3.50" + LINE_SEPARATOR +
		" small coffee             1      2.50     2.50" + LINE_SEPARATOR +
		" medium coffee            1      3.00     3.00" + LINE_SEPARATOR +
		"Extras:" + LINE_SEPARATOR +
		" special roast            1      0.90     0.90" + LINE_SEPARATOR +
		" extra milk               1      0.30     0.30" + LINE_SEPARATOR +
		"                                Total:   14.15" + LINE_SEPARATOR;
		assertEquals(expectedReceipt, printedReceipt);
	}
	
	@Test
	public void printReceipt_empty_input() {
		
		List<Product> gratisProducts = clientBasketService.applyBonusProgram();
		
		assertNotNull(gratisProducts);
		assertTrue(gratisProducts.size() == 0);
		
		String printedReceipt = clientBasketService.printReceipt();
		
		String expectedReceipt =  
		"Your receipt :" + LINE_SEPARATOR + 
		"Product description   Quantity   Price    Total" + LINE_SEPARATOR +
		"                                Total:   0.00" + LINE_SEPARATOR;
		assertEquals(expectedReceipt, printedReceipt);
	}
}
