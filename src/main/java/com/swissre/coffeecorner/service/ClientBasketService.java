package com.swissre.coffeecorner.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.swissre.coffeecorner.entity.Product;
import com.swissre.coffeecorner.entity.Product.ProductType;
import com.swissre.coffeecorner.exception.UnknownProductException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the service class representing the necessary logic for client's basket 
 * in Charlene's Coffee Corner Work Assignment.
 * 
 * It provides the implementation of all necessary business operations.
 * 
 * @author Andrzej Dabkowski
 *
 */
public class ClientBasketService implements IClientBasket {
	
	private static final String BEVERAGE_EXTRAS_DELIMITER = " with ";
	
	private static final int BEVERAGE_FREE_THRESHOLD = 5;
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static Map<String, Product> productCatalog = new HashMap<>();
	
	static {
		
		productCatalog.put( "large coffee", 
				new Product("large coffee", ProductType.BEVERAGE, new BigDecimal("3.50")));
		
		productCatalog.put( "medium coffee", 
				new Product("medium coffee", ProductType.BEVERAGE, new BigDecimal("3.00")));
		
		productCatalog.put( "small coffee",
				new Product("small coffee", ProductType.BEVERAGE, new BigDecimal("2.50")));
		
		productCatalog.put( "orange juice",
				new Product("orange juice", ProductType.BEVERAGE, new BigDecimal("3.95")));
		
		productCatalog.put( "extra milk",		
				new Product("extra milk", ProductType.EXTRAS, new BigDecimal("0.30")));
		
		productCatalog.put( "foamed milk",
				new Product("foamed milk", ProductType.EXTRAS, new BigDecimal("4.50")));
		
		productCatalog.put( "special roast",
				new Product("special roast", ProductType.EXTRAS, new BigDecimal("0.90")));
		
		productCatalog.put( "bacon roll",
				new Product("bacon roll", ProductType.SNACK, new BigDecimal("4.50")));
	}
	
	private List<Product> items = new ArrayList<>();
	
	// having applied bonus programs these lists get populated
	private List<Product> gratisProducts = new ArrayList<>();
	
	private List<Product> beverages = new ArrayList<>();
	private List<Product> snacks = new ArrayList<>();
	private List<Product> extras = new ArrayList<>();
	
	
	public ClientBasketService() {
		
	}
	
	public List<Product> addProduct(String productLine) {
		List<Product> products = parseProductLine(productLine);
		
		items.addAll(products);
		
		return products;
	}
	
	@Override
	public List<Product> applyBonusProgram() {
		
		Map<ProductType, List<Product>> productsByProductType =
				items.stream().collect(Collectors.groupingBy(Product::getProductType));
		
		beverages = productsByProductType.get(Product.ProductType.BEVERAGE);
		snacks = productsByProductType.get(Product.ProductType.SNACK);
		extras = productsByProductType.get(Product.ProductType.EXTRAS);
		
		int beveragesSize = beverages == null ? 0 : beverages.size();
		int snacksSize = snacks == null ? 0 : snacks.size();
		
		// Rule : if a customer orders a beverage and a snack, one of the extras is free
		int noOfFreeExtras = Math.min(beveragesSize, snacksSize);
		
		// Rule : every 5th beverage is for free
		int noOfFreeBeverages = beveragesSize / BEVERAGE_FREE_THRESHOLD;
		
		// Removing extras that are for free
		for(int i = 0 ; i < noOfFreeExtras && !extras.isEmpty() ; i++) {
			Product gratisExtras = extras.remove(0); // Assumption: gratis are the first x extras in this list (not the first x cheapest)
			gratisProducts.add(gratisExtras);
		}
		
		// Removing beverages that are for free
		for(int i = 0 ; i < noOfFreeBeverages ; i++) {
			Product gratisBeverage = beverages.remove(0); // Assumption: gratis are the first x beverages in this list (not the first x cheapest)
			gratisProducts.add(gratisBeverage);
		}
		
		return gratisProducts;
	}
	
	public String printReceipt() {
		BigDecimal sumTotalValue = new BigDecimal("0.00");
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Your receipt :").append(LINE_SEPARATOR);
		buffer.append("Product description   Quantity   Price    Total").append(LINE_SEPARATOR);
		
		if(gratisProducts!=null && !gratisProducts.isEmpty()) {
			buffer.append("Gratis products:").append(LINE_SEPARATOR);
			
			for(Product gratisProduct : gratisProducts ) {
				buffer.append(String.format(" %-21s  %3d      %.2f     %.2f", gratisProduct.getName(), 1, 0.00, 0.00)).append(LINE_SEPARATOR);
			}
		}
		
		if(beverages!=null && !beverages.isEmpty()) {
			buffer.append("Beverages:").append(LINE_SEPARATOR);
			
			Map<String, Long> beveragesByCount = beverages.stream().collect(
	                Collectors.groupingBy(Product::getName, Collectors.counting()));
			
			for(Map.Entry<String, Long> entry : beveragesByCount.entrySet()) {
				String name = entry.getKey();
				long quantity =  entry.getValue();
				BigDecimal price = productCatalog.get(name).getPrice();
				BigDecimal totalValue = price.multiply(new BigDecimal(quantity));
				sumTotalValue = sumTotalValue.add(totalValue);
				
				buffer.append(String.format(" %-21s  %3d      %.2f     %.2f", name, quantity, 
						price.doubleValue(), totalValue.doubleValue())).append(LINE_SEPARATOR);
			}
		}
		
		if(extras!=null && !extras.isEmpty()) {
			buffer.append("Extras:").append(LINE_SEPARATOR);
			
			Map<String, Long> extrasByCount = extras.stream().collect(
	                Collectors.groupingBy(Product::getName, Collectors.counting()));
			
			for(Map.Entry<String, Long> entry : extrasByCount.entrySet()) {
				String name = entry.getKey();
				long quantity =  entry.getValue();
				BigDecimal price = productCatalog.get(name).getPrice();
				BigDecimal totalValue = price.multiply(new BigDecimal(quantity));
				sumTotalValue = sumTotalValue.add(totalValue);
				
				buffer.append(String.format(" %-21s  %3d      %.2f     %.2f", name, quantity, 
						price.doubleValue(), totalValue.doubleValue())).append(LINE_SEPARATOR);
			}
		}
		
		if(snacks!=null && !snacks.isEmpty()) {
			buffer.append("Snacks:").append(LINE_SEPARATOR);
			
			Map<String, Long> stacksByCount = snacks.stream().collect(
	                Collectors.groupingBy(Product::getName, Collectors.counting()));
			
			for(Map.Entry<String, Long> entry : stacksByCount.entrySet()) {
				String name = entry.getKey();
				long quantity = entry.getValue();
				BigDecimal price = productCatalog.get(name).getPrice();
				BigDecimal totalValue = price.multiply(new BigDecimal(quantity));
				sumTotalValue = sumTotalValue.add(totalValue);
				
				buffer.append(String.format(" %-21s  %3d      %.2f     %.2f", name, quantity, 
						price.doubleValue(), totalValue.doubleValue())).append(LINE_SEPARATOR);
			}
		}
		
		buffer.append(String.format("                                Total:   %.2f",sumTotalValue))
				.append(LINE_SEPARATOR);
		
		// System.out.print(buffer.toString());
		
		return buffer.toString();	
	}
	
	private List<Product> parseProductLine(String productLine) {
		List<Product> products = new ArrayList<>();
		
		if(productLine.contains(BEVERAGE_EXTRAS_DELIMITER)) {
			// productLine contains a beverage and an extras
			String[] singleProductLines = productLine.split(BEVERAGE_EXTRAS_DELIMITER);
			
			if(singleProductLines.length !=2 || singleProductLines[0].isEmpty() 
					|| singleProductLines[1].isEmpty()  ) {
				throw new UnknownProductException(
						String.format("Some input parsing issue. It was supposed to containg beverage and extras [%s].", productLine));
			}
			
			Product beverage = parseSingleProductLine(singleProductLines[0]);
			Product extras = parseSingleProductLine(singleProductLines[1]);
			
			products.add(beverage);
			products.add(extras);
			
		} else {
			// productLine contains one product
			Product beverage = parseSingleProductLine(productLine);
			products.add(beverage);
		}
		
		return products;
	}
	
	private Product parseSingleProductLine(String singleProductLine) {
		Product product = new Product();
		
		if(productCatalog.containsKey(singleProductLine)) {
			product = productCatalog.get(singleProductLine);
		} else {
			throw new UnknownProductException(
					String.format("Product catalog does not contain product [%s].", singleProductLine));

		}
		
		return product;
	}

	public List<Product> getGratisProducts() {
		return gratisProducts;
	}
}
