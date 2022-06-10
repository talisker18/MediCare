package com.henz.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.henz.data_access.ProductRepository;
import com.henz.entity.Product;

import lombok.Getter;

@Getter
public class ProductPage {
	
	@Autowired
	private ProductRepository productRepository;
	
	private final String prefixForButtonId = "buttonAddProduct";
	private List<String> buttonIdsToAddProductToShoppingCart = new ArrayList<String>();
	
	public void initListWithButtonIds() {
		List<Product> productList = this.productRepository.getAllProducts();
		String [] productIdArray = new String[productList.size()];
		
		for(int i = 0; i < productList.size(); i++) {
			productIdArray[i] = productList.get(i).getId().toString();
		}
		
		buttonIdsToAddProductToShoppingCart = Arrays.stream(productIdArray)
				.map(productId -> this.prefixForButtonId.concat(productId))
				.collect(Collectors.toList());
	}
	
	public String pickRandomButtonId() {
		Random rand = new Random();
		
		return buttonIdsToAddProductToShoppingCart.get(
				rand.nextInt(buttonIdsToAddProductToShoppingCart.size())
				);
	}

}
